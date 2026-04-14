package com.sarkari.exam.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.components.*
import com.sarkari.exam.ui.theme.*

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBody)
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
                            PrimaryBlue, 
                            Modifier.weight(1f)
                        ) { navController.navigate(Screen.MockTest.route) }
                        QuickActionCard(
                            "PYQ Library", 
                            "Official Archives", 
                            Icons.Default.Menu, 
                            AccentSaffron, 
                            Modifier.weight(1f)
                        ) { /* To be implemented */ }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            "PYQ Test", 
                            "Previous Paper Prep", 
                            Icons.Default.CheckCircle, 
                            AccentRed, 
                            Modifier.weight(1f)
                        ) { /* To be implemented */ }
                        QuickActionCard(
                            "AI Tutor", 
                            "Interactive Coaching", 
                            Icons.Default.Face, 
                            AccentGreen, 
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
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "📈 Performance Analytics",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PerformanceBar("History", 0.75f, PrimaryBlue)
                        PerformanceBar("Geography", 0.45f, AccentSaffron)
                        PerformanceBar("Polity", 0.90f, AccentGreen)
                        PerformanceBar("Current Affairs", 0.30f, AccentRed)
                    }
                }
            }
            
            // Suggestions Section
            item {
                Column {
                    Text(
                        text = "⚡ Personalized Suggestions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
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
    icon: org.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text(text = desc, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun SuggestionChip(text: String, isActive: Boolean = true) {
    Surface(
        color = if (isActive) PrimaryBlue else BackgroundBody,
        shape = RoundedCornerShape(20.dp),
        border = if (!isActive) androidx.compose.foundation.BorderStroke(1.dp, BorderColor) else null,
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) Color.White else TextSecondary
        )
    }
}
