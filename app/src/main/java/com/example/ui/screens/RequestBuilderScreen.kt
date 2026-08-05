package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApiKeyLocation
import com.example.data.model.ApiRequestState
import com.example.data.model.ApiResponseResult
import com.example.data.model.AuthType
import com.example.data.model.BodyType
import com.example.data.model.HttpMethod
import com.example.ui.ApiViewModel
import com.example.ui.components.CodeSnippetDialog
import com.example.ui.components.FormattedResponseViewer
import com.example.ui.components.KeyValueEditor
import com.example.ui.components.KeyValueItem
import com.example.ui.components.MethodBadge
import com.example.ui.components.PresetApisBottomSheet
import com.example.ui.components.StatusBadge

@Composable
fun RequestBuilderScreen(
    viewModel: ApiViewModel,
    requestState: ApiRequestState,
    responseResult: ApiResponseResult?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showMethodDropdown by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Params, 1: Headers, 2: Auth, 3: Body
    var showSaveDialog by remember { mutableStateOf(false) }
    var showCodeDialog by remember { mutableStateOf(false) }
    var showPresetSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Request Header: Title + Unsaved Badge + Quick Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = requestState.name.ifBlank { "New Request" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF262830),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383A44))
                ) {
                    Text(
                        text = "Unsaved",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showPresetSheet = true },
                    modifier = Modifier.testTag("open_preset_samples_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Presets", tint = Color(0xFFA2C2FB))
                }
                IconButton(
                    onClick = { showCodeDialog = true },
                    modifier = Modifier.testTag("open_code_generator_button")
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = "Code", tint = Color.White)
                }
                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.testTag("open_save_dialog_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Request Card: Method Selector + URL Field + SEND Button
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF18191E),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282A31)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Method Picker Dropdown Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF111216))
                        .clickable { showMethodDropdown = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = requestState.method.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = requestState.method.color
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF)
                        )
                    }

                    DropdownMenu(
                        expanded = showMethodDropdown,
                        onDismissRequest = { showMethodDropdown = false },
                        modifier = Modifier.background(Color(0xFF1E2026))
                    ) {
                        HttpMethod.values().forEach { method ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = method.name,
                                        fontWeight = FontWeight.Bold,
                                        color = method.color
                                    )
                                },
                                onClick = {
                                    viewModel.updateMethod(method)
                                    showMethodDropdown = false
                                },
                                modifier = Modifier.testTag("dropdown_method_${method.name}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // URL Input Field
                OutlinedTextField(
                    value = requestState.url,
                    onValueChange = { viewModel.updateUrl(it) },
                    placeholder = { Text("https://api.example.com/v1/users", fontSize = 13.sp, color = Color(0xFF6B7280), fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    ),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111216),
                        unfocusedContainerColor = Color(0xFF111216),
                        focusedBorderColor = Color(0xFF383A44),
                        unfocusedBorderColor = Color(0xFF262830)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // SEND Button
                Button(
                    onClick = { viewModel.sendRequest() },
                    enabled = !isLoading && requestState.url.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA2C2FB),
                        contentColor = Color(0xFF0B1220),
                        disabledContainerColor = Color(0xFF3A4254),
                        disabledContentColor = Color(0xFF8B95A5)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_request_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF0B1220),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SENDING...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SEND", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF0B1220)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Request Configuration Tabs Container
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF18191E),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282A31)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = Color(0xFF18191E),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Params", fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                                if (requestState.queryParams.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF2D3039)) {
                                        Text("${requestState.queryParams.size}", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_params")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Headers", fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                if (requestState.headers.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF2D3039)) {
                                        Text("${requestState.headers.size}", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_headers")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Body", fontSize = 13.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFF10B981))
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_body")
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text("Auth", fontSize = 13.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.testTag("tab_auth")
                    )
                }

                Box(modifier = Modifier.padding(14.dp)) {
                    when (selectedTab) {
                        0 -> {
                            // Query Params
                            KeyValueEditor(
                                items = requestState.queryParams.map { KeyValueItem(it.key, it.value, it.enabled) },
                                keyPlaceholder = "Paramètre",
                                valuePlaceholder = "Valeur",
                                onItemChange = { idx, k, v, e -> viewModel.updateQueryParam(idx, k, v, e) },
                                onItemDelete = { idx -> viewModel.removeQueryParam(idx) },
                                onAddItem = { viewModel.addQueryParam() }
                            )
                        }
                        1 -> {
                            // Headers
                            KeyValueEditor(
                                items = requestState.headers.map { KeyValueItem(it.key, it.value, it.enabled) },
                                keyPlaceholder = "En-tête (e.g. Content-Type)",
                                valuePlaceholder = "Valeur (e.g. application/json)",
                                onItemChange = { idx, k, v, e -> viewModel.updateHeader(idx, k, v, e) },
                                onItemDelete = { idx -> viewModel.removeHeader(idx) },
                                onAddItem = { viewModel.addHeader() }
                            )
                        }
                        2 -> {
                            // Body Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Format du Corps", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    if (requestState.bodyType == BodyType.JSON) {
                                        OutlinedButton(
                                            onClick = { viewModel.beautifyJsonBody() },
                                            modifier = Modifier.testTag("beautify_json_button")
                                        ) {
                                            Icon(imageVector = Icons.Default.FormatAlignLeft, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFA2C2FB))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Formater JSON", fontSize = 11.sp, color = Color(0xFFA2C2FB))
                                        }
                                    }
                                }

                                ScrollableTabRow(
                                    selectedTabIndex = requestState.bodyType.ordinal,
                                    edgePadding = 0.dp,
                                    containerColor = Color(0xFF111216)
                                ) {
                                    BodyType.values().forEach { bType ->
                                        Tab(
                                            selected = requestState.bodyType == bType,
                                            onClick = { viewModel.updateBodyType(bType) },
                                            text = { Text(bType.label, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                when (requestState.bodyType) {
                                    BodyType.JSON, BodyType.RAW_TEXT, BodyType.XML -> {
                                        OutlinedTextField(
                                            value = requestState.bodyContent,
                                            onValueChange = { viewModel.updateBodyContent(it) },
                                            placeholder = {
                                                Text(
                                                    if (requestState.bodyType == BodyType.JSON) "{\n  \"key\": \"value\"\n}"
                                                    else "Saisir le contenu...",
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color(0xFF6B7280)
                                                )
                                            },
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                color = Color.White,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp
                                            ),
                                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF111216),
                                                unfocusedContainerColor = Color(0xFF111216),
                                                focusedBorderColor = Color(0xFF383A44),
                                                unfocusedBorderColor = Color(0xFF262830)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .testTag("body_content_field")
                                        )
                                    }
                                    BodyType.FORM_DATA -> {
                                        KeyValueEditor(
                                            items = requestState.formDataParams.map { KeyValueItem(it.key, it.value, it.enabled) },
                                            keyPlaceholder = "Champ Formulaire",
                                            valuePlaceholder = "Valeur",
                                            onItemChange = { idx, k, v, e -> viewModel.updateFormDataParam(idx, k, v, e) },
                                            onItemDelete = { idx -> viewModel.removeFormDataParam(idx) },
                                            onAddItem = { viewModel.addFormDataParam() }
                                        )
                                    }
                                    BodyType.NONE -> {
                                        Text("Aucun corps n'est envoyé avec cette requête.", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Auth Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Type d'authentification", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                AuthType.values().forEach { auth ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.updateAuthType(auth) }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = requestState.authType == auth,
                                            onClick = { viewModel.updateAuthType(auth) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(auth.label, fontSize = 14.sp, color = Color.White)
                                    }
                                }

                                when (requestState.authType) {
                                    AuthType.BEARER -> {
                                        OutlinedTextField(
                                            value = requestState.authToken,
                                            onValueChange = { viewModel.updateAuthToken(it) },
                                            label = { Text("Token Bearer") },
                                            placeholder = { Text("eyJhbGciOi...") },
                                            modifier = Modifier.fillMaxWidth().testTag("auth_token_field")
                                        )
                                    }
                                    AuthType.BASIC -> {
                                        OutlinedTextField(
                                            value = requestState.authUser,
                                            onValueChange = { viewModel.updateBasicAuth(it, requestState.authPass) },
                                            label = { Text("Utilisateur") },
                                            modifier = Modifier.fillMaxWidth().testTag("auth_user_field")
                                        )
                                        OutlinedTextField(
                                            value = requestState.authPass,
                                            onValueChange = { viewModel.updateBasicAuth(requestState.authUser, it) },
                                            label = { Text("Mot de passe") },
                                            modifier = Modifier.fillMaxWidth().testTag("auth_pass_field")
                                        )
                                    }
                                    AuthType.API_KEY -> {
                                        OutlinedTextField(
                                            value = requestState.apiKeyName,
                                            onValueChange = { viewModel.updateApiKey(it, requestState.apiKeyValue, requestState.apiKeyLocation) },
                                            label = { Text("Nom de clé (e.g. X-API-Key)") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = requestState.apiKeyValue,
                                            onValueChange = { viewModel.updateApiKey(requestState.apiKeyName, it, requestState.apiKeyLocation) },
                                            label = { Text("Valeur") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    AuthType.NONE -> {}
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // RESPONSE INSPECTOR SECTION
        Text(
            text = "Réponse de l'API",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (responseResult == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Appuyez sur 'ENVOYER LA REQUÊTE' pour afficher la réponse",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        } else {
            // Status pill bar
            StatusBadge(result = responseResult)

            Spacer(modifier = Modifier.height(10.dp))

            if (responseResult.errorMessage != null) {
                // Error Alert Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = responseResult.errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                // Formatted Response Body Viewer
                FormattedResponseViewer(
                    result = responseResult,
                    onCopy = { text ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("API Response", text)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Réponse copiée dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                    },
                    onShare = { text ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, text)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Partager la réponse"))
                    }
                )
            }
        }
    }

    // Modal Dialogs
    if (showSaveDialog) {
        SaveRequestDialog(
            initialName = requestState.name,
            collections = viewModel.collections.value,
            onDismiss = { showSaveDialog = false },
            onSave = { name, colId ->
                viewModel.saveCurrentRequest(name, colId)
                showSaveDialog = false
                Toast.makeText(context, "Requête sauvegardée !", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showCodeDialog) {
        CodeSnippetDialog(
            requestState = requestState,
            onDismiss = { showCodeDialog = false },
            onCopySnippet = { code ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Code Snippet", code)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Code copié dans le presse-papiers !", Toast.LENGTH_SHORT).show()
                showCodeDialog = false
            }
        )
    }

    if (showPresetSheet) {
        PresetApisBottomSheet(
            samples = viewModel.repository.getPresetApis(),
            onSelectSample = { sample ->
                viewModel.loadPresetSample(sample)
                Toast.makeText(context, "Exemple chargé: ${sample.name}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showPresetSheet = false }
        )
    }
}

@Composable
fun SaveRequestDialog(
    initialName: String,
    collections: List<com.example.data.db.CollectionEntity>,
    onDismiss: () -> Unit,
    onSave: (name: String, collectionId: Long?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedCollectionId by remember { mutableStateOf<Long?>(collections.firstOrNull()?.id) }

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
                    text = "Sauvegarder la requête",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la requête") },
                    modifier = Modifier.fillMaxWidth().testTag("save_request_name_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Dossier / Collection", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))

                collections.forEach { col ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCollectionId = col.id }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedCollectionId == col.id,
                            onClick = { selectedCollectionId = col.id }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(col.name, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (name.isNotBlank()) onSave(name, selectedCollectionId) },
                        modifier = Modifier.testTag("confirm_save_request_button")
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
