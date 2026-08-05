package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApiRequestState
import com.example.data.network.CodeGenerator

@Composable
fun CodeSnippetDialog(
    requestState: ApiRequestState,
    onDismiss: () -> Unit,
    onCopySnippet: (String) -> Unit
) {
    var selectedLanguage by remember { mutableIntStateOf(0) } // 0: cURL, 1: Kotlin, 2: JS, 3: Python

    val snippetText = remember(selectedLanguage, requestState) {
        when (selectedLanguage) {
            0 -> CodeGenerator.generateCurl(requestState)
            1 -> CodeGenerator.generateKotlinOkHttp(requestState)
            2 -> CodeGenerator.generateJavaScriptFetch(requestState)
            3 -> CodeGenerator.generatePythonRequests(requestState)
            else -> ""
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Générer du code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedLanguage,
                    edgePadding = 0.dp
                ) {
                    Tab(
                        selected = selectedLanguage == 0,
                        onClick = { selectedLanguage = 0 },
                        text = { Text("cURL", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedLanguage == 1,
                        onClick = { selectedLanguage = 1 },
                        text = { Text("Kotlin (OkHttp)", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedLanguage == 2,
                        onClick = { selectedLanguage = 2 },
                        text = { Text("JavaScript (fetch)", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedLanguage == 3,
                        onClick = { selectedLanguage = 3 },
                        text = { Text("Python (requests)", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Code Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = snippetText,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Copy Action Button
                Button(
                    onClick = { onCopySnippet(snippetText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_code_snippet_button")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Copier le code")
                }
            }
        }
    }
}
