package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApiResponseResult

@Composable
fun FormattedResponseViewer(
    result: ApiResponseResult,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Body, 1: Headers, 2: Cookies
    var searchQuery by remember { mutableStateOf("") }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val displayText = remember(selectedTab, result, searchQuery) {
        val raw = if (selectedTab == 0) result.formattedBody else result.body
        if (searchQuery.isBlank()) raw
        else {
            raw.lines().filter { it.contains(searchQuery, ignoreCase = true) }.joinToString("\n")
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Status & Metadata Card (200 OK, Latency, Size)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF18191E),
            border = BorderStroke(1.dp, Color(0xFF282A31)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Badge (e.g. 200 OK)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (result.statusCode in 200..299) Color(0xFF10B981) else Color(0xFFEF4444)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${result.statusCode}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFF04200E)
                            )
                            Text(
                                text = result.statusMessage.ifBlank { "OK" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF04200E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = result.method.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = result.url.ifBlank { "/api/v1/users/profile" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🕒 ${result.timeMs}ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE5E7EB)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "💾 ${formatSize(result.sizeBytes)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE5E7EB)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search/Filter Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter response...", fontSize = 13.sp, color = Color(0xFF6B7280)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF9CA3AF))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF18191E),
                unfocusedContainerColor = Color(0xFF18191E),
                focusedBorderColor = Color(0xFF383A44),
                unfocusedBorderColor = Color(0xFF282A31),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_response_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Collapse All & Copy Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { /* Collapse feature */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE5E7EB)),
                border = BorderStroke(1.dp, Color(0xFF383A44)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.UnfoldLess, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Collapse All", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = { onCopy(displayText) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE5E7EB)),
                border = BorderStroke(1.dp, Color(0xFF383A44)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_response_button")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Body / Headers / Cookies Subtabs
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF18191E),
            border = BorderStroke(1.dp, Color(0xFF282A31)),
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
                        text = { Text("Body", fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Headers", fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF2D3039)) {
                                    Text("${result.headers.size}", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Cookies", fontSize = 13.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Box(modifier = Modifier.padding(14.dp)) {
                    when (selectedTab) {
                        0 -> {
                            // Monospace Dark Terminal Viewer with Syntax Highlighting
                            val annotatedJson = remember(displayText) { highlightJsonSyntax(displayText) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 420.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF111216))
                                    .padding(12.dp)
                            ) {
                                SelectionContainer {
                                    Row(
                                        modifier = Modifier
                                            .verticalScroll(verticalScrollState)
                                            .horizontalScroll(horizontalScrollState)
                                    ) {
                                        Text(
                                            text = annotatedJson,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Headers Tab
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 350.dp)
                                    .verticalScroll(verticalScrollState)
                            ) {
                                result.headers.forEach { (name, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFA2C2FB),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = value,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFE5E7EB),
                                            modifier = Modifier.weight(2f)
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Cookies Tab
                            Text(
                                text = "No cookies received with this response.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    return if (bytes < 1024) "$bytes B"
    else String.format("%.1f KB", bytes / 1024.0)
}

private fun highlightJsonSyntax(json: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = json.lines()
        lines.forEachIndexed { index, line ->
            var i = 0
            while (i < line.length) {
                val ch = line[i]
                when {
                    ch == '"' -> {
                        val endQuote = line.indexOf('"', i + 1)
                        if (endQuote != -1) {
                            val strContent = line.substring(i, endQuote + 1)
                            val isKey = line.indexOf(':', endQuote) in (endQuote + 1)..(endQuote + 3)
                            if (isKey) {
                                withStyle(SpanStyle(color = Color(0xFFE5E7EB), fontWeight = FontWeight.SemiBold)) {
                                    append(strContent)
                                }
                            } else {
                                withStyle(SpanStyle(color = Color(0xFF10B981))) { // Green string
                                    append(strContent)
                                }
                            }
                            i = endQuote + 1
                        } else {
                            withStyle(SpanStyle(color = Color(0xFF10B981))) { append(ch) }
                            i++
                        }
                    }
                    ch.isDigit() || (ch == '-' && i + 1 < line.length && line[i + 1].isDigit()) -> {
                        var j = i
                        while (j < line.length && (line[j].isDigit() || line[j] == '.' || line[j] == '-')) {
                            j++
                        }
                        val numStr = line.substring(i, j)
                        withStyle(SpanStyle(color = Color(0xFF60A5FA))) { // Blue number
                            append(numStr)
                        }
                        i = j
                    }
                    line.startsWith("true", i) -> {
                        withStyle(SpanStyle(color = Color(0xFFF59E0B))) { append("true") }
                        i += 4
                    }
                    line.startsWith("false", i) -> {
                        withStyle(SpanStyle(color = Color(0xFFF59E0B))) { append("false") }
                        i += 5
                    }
                    line.startsWith("null", i) -> {
                        withStyle(SpanStyle(color = Color(0xFFEF4444))) { append("null") }
                        i += 4
                    }
                    else -> {
                        withStyle(SpanStyle(color = Color(0xFF9CA3AF))) { append(ch) }
                        i++
                    }
                }
            }
            if (index < lines.size - 1) append("\n")
        }
    }
}

