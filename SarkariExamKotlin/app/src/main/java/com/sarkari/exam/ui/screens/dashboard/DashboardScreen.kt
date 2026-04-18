package com.sarkari.exam.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.components.*
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, onOpenDrawer: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Sidebar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                AiGreetingHeader(userName = "Scholar")
            }
            
            // Quick Actions Grid (Native implementation using weight for row-based grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            "Mock Tests", 
                            "Native AI Simulation", 
                            Icons.Default.PlayArrow, 
                            MaterialTheme.colorScheme.primary, 
                            Modifier.weight(1f)
                        ) { navController.navigate(Screen.MockTest.route) }
                        QuickActionCard(
                            "PYQ Library", 
                            "Official Archives", 
                            Icons.Default.Menu, 
                            MaterialTheme.colorScheme.secondary, 
                            Modifier.weight(1f)
                        ) { navController.navigate(Screen.PYQLibrary.route) }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            "PYQ Test", 
                            "Previous Paper Prep", 
                            Icons.Default.CheckCircle, 
                            MaterialTheme.colorScheme.error, 
                            Modifier.weight(1f)
                        ) { navController.navigate(Screen.PYQTest.route) }
                        QuickActionCard(
                            "AI Tutor", 
                            "Interactive Coaching", 
                            Icons.Default.Face, 
                            MaterialTheme.colorScheme.tertiary, 
                            Modifier.weight(1f)
                        ) { navController.navigate(Screen.Tutor.route) }
                    }
                }
            }
            
            // Performance Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Analytics.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📈 Performance Analytics",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PerformanceBar("History", 0.75f, MaterialTheme.colorScheme.primary)
                        PerformanceBar("Geography", 0.45f, MaterialTheme.colorScheme.secondary)
                        PerformanceBar("Polity", 0.90f, MaterialTheme.colorScheme.tertiary)
                        PerformanceBar("Current Affairs", 0.30f, MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            // Suggestions Section
            item {
                Column {
                    Text(
                        text = "⚡ Personalized Suggestions",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(text = "Focus: Polity Review")
                        SuggestionChip(text = "New Update Available", isActive = false)
                    }
                }
            }
            
            // AI Status
            item {
                AiEngineCard(slots = 482)
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
fun SuggestionChip(text: String, isActive: Boolean = true) {
    Surface(
        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

