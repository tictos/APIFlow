package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApiViewModel
import com.example.ui.AppNavigationTab
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.EnvironmentsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.RequestBuilderScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ApiFlowApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiFlowApp(viewModel: ApiViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val requestState by viewModel.requestState.collectAsState()
    val responseResult by viewModel.responseResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activeEnv by viewModel.activeEnvironment.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF22242C))
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "PostBoy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    },
                    actions = {
                        // Environment Selector Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF22242C),
                            modifier = Modifier
                                .clickable { viewModel.selectTab(AppNavigationTab.ENVIRONMENTS) }
                                .testTag("active_env_header_pill")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeEnv?.name ?: "Env",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE5E7EB)
                                )
                            }
                        }

                        // History Action Button
                        IconButton(
                            onClick = { viewModel.selectTab(AppNavigationTab.HISTORY) },
                            modifier = Modifier.testTag("header_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Historique",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0E0F12)
                    )
                )
                // Bottom border line under TopAppBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF1F2128))
                )
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF121316),
                tonalElevation = 0.dp
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1F2128))
                    )
                    NavigationBar(
                        containerColor = Color(0xFF121316),
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == AppNavigationTab.REQUEST_BUILDER,
                            onClick = { viewModel.selectTab(AppNavigationTab.REQUEST_BUILDER) },
                            icon = { Icon(imageVector = Icons.Default.Send, contentDescription = "Requests") },
                            label = { Text("Requests", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF04200E),
                                selectedTextColor = Color(0xFF10B981),
                                indicatorColor = Color(0xFF00C853),
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF)
                            ),
                            modifier = Modifier.testTag("nav_item_request")
                        )
                        NavigationBarItem(
                            selected = currentTab == AppNavigationTab.COLLECTIONS,
                            onClick = { viewModel.selectTab(AppNavigationTab.COLLECTIONS) },
                            icon = { Icon(imageVector = Icons.Default.Folder, contentDescription = "Collections") },
                            label = { Text("Collections", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF04200E),
                                selectedTextColor = Color(0xFF10B981),
                                indicatorColor = Color(0xFF00C853),
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF)
                            ),
                            modifier = Modifier.testTag("nav_item_collections")
                        )
                        NavigationBarItem(
                            selected = currentTab == AppNavigationTab.HISTORY,
                            onClick = { viewModel.selectTab(AppNavigationTab.HISTORY) },
                            icon = { Icon(imageVector = Icons.Default.History, contentDescription = "History") },
                            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF04200E),
                                selectedTextColor = Color(0xFF10B981),
                                indicatorColor = Color(0xFF00C853),
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF)
                            ),
                            modifier = Modifier.testTag("nav_item_history")
                        )
                        NavigationBarItem(
                            selected = currentTab == AppNavigationTab.ENVIRONMENTS,
                            onClick = { viewModel.selectTab(AppNavigationTab.ENVIRONMENTS) },
                            icon = { Icon(imageVector = Icons.Default.Tune, contentDescription = "Settings") },
                            label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF04200E),
                                selectedTextColor = Color(0xFF10B981),
                                indicatorColor = Color(0xFF00C853),
                                unselectedIconColor = Color(0xFF9CA3AF),
                                unselectedTextColor = Color(0xFF9CA3AF)
                            ),
                            modifier = Modifier.testTag("nav_item_environments")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavigationTab.REQUEST_BUILDER -> {
                    RequestBuilderScreen(
                        viewModel = viewModel,
                        requestState = requestState,
                        responseResult = responseResult,
                        isLoading = isLoading
                    )
                }
                AppNavigationTab.COLLECTIONS -> {
                    CollectionsScreen(viewModel = viewModel)
                }
                AppNavigationTab.ENVIRONMENTS -> {
                    EnvironmentsScreen(viewModel = viewModel)
                }
                AppNavigationTab.HISTORY -> {
                    HistoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}

