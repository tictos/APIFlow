package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.EnvironmentEntity
import com.example.data.model.EnvironmentVariable
import com.example.ui.ApiViewModel
import com.example.ui.components.KeyValueEditor
import com.example.ui.components.KeyValueItem

@Composable
fun EnvironmentsScreen(
    viewModel: ApiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val environments by viewModel.environments.collectAsState()
    val activeEnv by viewModel.activeEnvironment.collectAsState()

    var showEnvDialog by remember { mutableStateOf(false) }
    var editingEnv by remember { mutableStateOf<EnvironmentEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Variables d'environnement",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Utilisez {{variable}} dans vos URL, en-têtes ou corps pour remplacer dynamiquement les valeurs.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (environments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Aucun environnement configuré", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(environments) { env ->
                        val vars = viewModel.repository.parseVariablesJson(env.variablesJson)
                        val isActive = env.id == activeEnv?.id

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setActiveEnvironment(env.id)
                                    Toast.makeText(context, "Environnement '${env.name}' activé", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("env_card_${env.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isActive,
                                        onClick = {
                                            viewModel.setActiveEnvironment(env.id)
                                            Toast.makeText(context, "Environnement '${env.name}' activé", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = env.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${vars.size} variables",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = {
                                        editingEnv = env
                                        showEnvDialog = true
                                    }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier")
                                    }

                                    IconButton(onClick = { viewModel.deleteEnvironment(env.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                if (vars.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    vars.take(3).forEach { v ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "{{${v.key}}}",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = v.value,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (vars.size > 3) {
                                        Text(
                                            text = "+ ${vars.size - 3} autres...",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingEnv = null
                showEnvDialog = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_environment_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Nouvel environnement")
        }
    }

    if (showEnvDialog) {
        EnvironmentEditDialog(
            environment = editingEnv,
            viewModel = viewModel,
            onDismiss = { showEnvDialog = false },
            onSave = { id, name, vars ->
                viewModel.saveEnvironment(id = id, name = name, variables = vars, isActive = editingEnv?.isActive ?: false)
                showEnvDialog = false
                Toast.makeText(context, "Environnement sauvegardé !", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun EnvironmentEditDialog(
    environment: EnvironmentEntity?,
    viewModel: ApiViewModel,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, variables: List<EnvironmentVariable>) -> Unit
) {
    var name by remember { mutableStateOf(environment?.name ?: "") }
    var variables by remember {
        mutableStateOf(
            if (environment != null) viewModel.repository.parseVariablesJson(environment.variablesJson)
            else listOf(EnvironmentVariable("baseUrl", "https://api.example.com", true))
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (environment == null) "Nouvel environnement" else "Modifier l'environnement",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'environnement (e.g. Staging)") },
                    modifier = Modifier.fillMaxWidth().testTag("env_name_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Variables", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                KeyValueEditor(
                    items = variables.map { KeyValueItem(it.key, it.value, it.enabled) },
                    keyPlaceholder = "Variable (e.g. token)",
                    valuePlaceholder = "Valeur",
                    onItemChange = { idx, k, v, e ->
                        val list = variables.toMutableList()
                        if (idx in list.indices) {
                            list[idx] = EnvironmentVariable(k, v, e)
                            variables = list
                        }
                    },
                    onItemDelete = { idx ->
                        val list = variables.toMutableList()
                        if (idx in list.indices) {
                            list.removeAt(idx)
                            variables = list
                        }
                    },
                    onAddItem = {
                        val list = variables.toMutableList()
                        list.add(EnvironmentVariable("", "", true))
                        variables = list
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (name.isNotBlank()) onSave(environment?.id ?: 0, name, variables) },
                        modifier = Modifier.testTag("confirm_save_env_button")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
