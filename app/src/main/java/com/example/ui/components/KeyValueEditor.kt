package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class KeyValueItem(
    val key: String,
    val value: String,
    val enabled: Boolean
)

@Composable
fun KeyValueEditor(
    items: List<KeyValueItem>,
    keyPlaceholder: String = "Clé",
    valuePlaceholder: String = "Valeur",
    onItemChange: (index: Int, key: String, value: String, enabled: Boolean) -> Unit,
    onItemDelete: (index: Int) -> Unit,
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (items.isEmpty()) {
            Text(
                text = "Aucun paramètre configuré.",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.enabled,
                        onCheckedChange = { enabled ->
                            onItemChange(index, item.key, item.value, enabled)
                        },
                        modifier = Modifier.testTag("checkbox_$index")
                    )

                    OutlinedTextField(
                        value = item.key,
                        onValueChange = { key ->
                            onItemChange(index, key, item.value, item.enabled)
                        },
                        placeholder = { Text(keyPlaceholder, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("key_field_$index"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = item.value,
                        onValueChange = { value ->
                            onItemChange(index, item.key, value, item.enabled)
                        },
                        placeholder = { Text(valuePlaceholder, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("value_field_$index"),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { onItemDelete(index) },
                        modifier = Modifier.testTag("delete_button_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onAddItem,
            modifier = Modifier.testTag("add_key_value_button")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ajouter une ligne")
        }
    }
}
