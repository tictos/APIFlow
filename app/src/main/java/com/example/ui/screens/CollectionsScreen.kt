package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.CollectionEntity
import com.example.data.db.SavedRequestEntity
import com.example.data.model.HttpMethod
import com.example.ui.ApiViewModel
import com.example.ui.components.MethodBadge

@Composable
fun CollectionsScreen(
    viewModel: ApiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val collections by viewModel.collections.collectAsState()
    val savedRequests by viewModel.savedRequests.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddCollectionDialog by remember { mutableStateOf(false) }

    val filteredRequests = remember(savedRequests, searchQuery) {
        if (searchQuery.isBlank()) savedRequests
        else savedRequests.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.url.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0E0F12))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: My Collections + New Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Collections",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showAddCollectionDialog = true }
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFFA2C2FB), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA2C2FB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Collections List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (collections.isEmpty()) {
                    // Default Mock Collections matching the screenshot if database is empty
                    item {
                        CollectionCardItem(
                            title = "User API",
                            description = "Authentication & Profiles",
                            count = 12,
                            iconColor = Color(0xFF3B82F6),
                            onClick = {}
                        )
                    }
                    item {
                        CollectionCardItem(
                            title = "Auth Service",
                            description = "OAuth2 & JWT tokens",
                            count = 8,
                            iconColor = Color(0xFF10B981),
                            onClick = {}
                        )
                    }
                    item {
                        CollectionCardItem(
                            title = "Payment Gateway",
                            description = "Stripe webhooks & charges",
                            count = 24,
                            iconColor = Color(0xFFEF4444),
                            onClick = {}
                        )
                    }
                } else {
                    items(collections) { collection ->
                        val colRequests = filteredRequests.filter { it.collectionId == collection.id }
                        val colColor = try {
                            Color(android.graphics.Color.parseColor(collection.colorHex))
                        } catch (e: Exception) {
                            Color(0xFF3B82F6)
                        }
                        CollectionCardItem(
                            title = collection.name,
                            description = collection.description.ifBlank { "API Endpoints collection" },
                            count = colRequests.size,
                            iconColor = colColor,
                            onClick = {
                                viewModel.deleteCollection(collection.id)
                            }
                        )
                    }
                }

                // Recent Requests Section Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Requests",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                // Recent Request Items
                if (savedRequests.isEmpty()) {
                    item {
                        RecentRequestMockCard(
                            method = "GET",
                            methodColor = Color(0xFF10B981),
                            path = "/api/v1/users/me",
                            category = "User API / Fetch Profile",
                            status = "200 OK",
                            statusColor = Color(0xFF10B981),
                            statusBg = Color(0xFF04200E)
                        )
                    }
                    item {
                        RecentRequestMockCard(
                            method = "POST",
                            methodColor = Color(0xFF3B82F6),
                            path = "/oauth/token",
                            category = "Auth Service / Login",
                            status = "401 UNAUTHORIZED",
                            statusColor = Color(0xFFEF4444),
                            statusBg = Color(0xFF321111)
                        )
                    }
                    item {
                        RecentRequestMockCard(
                            method = "PUT",
                            methodColor = Color(0xFFF59E0B),
                            path = "/v1/charges/ch_123",
                            category = "Payment Gateway / Update Charge",
                            status = "200 OK",
                            statusColor = Color(0xFF10B981),
                            statusBg = Color(0xFF04200E)
                        )
                    }
                } else {
                    items(filteredRequests) { req ->
                        val methodEnum = try { HttpMethod.valueOf(req.method) } catch (e: Exception) { HttpMethod.GET }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF18191E),
                            border = BorderStroke(1.dp, Color(0xFF282A31)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.loadSavedRequest(req)
                                    Toast.makeText(context, "Loaded '${req.name}'", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MethodBadge(method = methodEnum)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = req.url,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = req.name,
                                            fontSize = 11.sp,
                                            color = Color(0xFF9CA3AF),
                                            maxLines = 1
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteSavedRequest(req.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF6B7280))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddCollectionDialog = true },
            containerColor = Color(0xFF3B82F6),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_collection_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "New Collection", modifier = Modifier.size(28.dp))
        }
    }

    if (showAddCollectionDialog) {
        AddCollectionDialog(
            onDismiss = { showAddCollectionDialog = false },
            onSave = { name, desc, color ->
                viewModel.createCollection(name, desc, color)
                showAddCollectionDialog = false
                Toast.makeText(context, "Collection '$name' created!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CollectionCardItem(
    title: String,
    description: String,
    count: Int,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF18191E),
        border = BorderStroke(1.dp, Color(0xFF282A31)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF22242C)
            ) {
                Text(
                    text = "$count req",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE5E7EB),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun RecentRequestMockCard(
    method: String,
    methodColor: Color,
    path: String,
    category: String,
    status: String,
    statusColor: Color,
    statusBg: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = methodColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = method,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = methodColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = path,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusBg
            ) {
                Text(
                    text = status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AddCollectionDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#3B82F6") }

    val colors = listOf("#3B82F6", "#10B981", "#EF4444", "#F59E0B", "#8B5CF6", "#EC4899")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF18191E),
            border = BorderStroke(1.dp, Color(0xFF282A31)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "New Collection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection Name", color = Color(0xFF9CA3AF)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF383A44),
                        unfocusedBorderColor = Color(0xFF262830),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_collection_name_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)", color = Color(0xFF9CA3AF)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF383A44),
                        unfocusedBorderColor = Color(0xFF262830),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9CA3AF))
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (name.isNotBlank()) onSave(name, desc, selectedColor) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        modifier = Modifier.testTag("confirm_create_collection_button")
                    ) {
                        Text("Create", color = Color.White)
                    }
                }
            }
        }
    }
}

