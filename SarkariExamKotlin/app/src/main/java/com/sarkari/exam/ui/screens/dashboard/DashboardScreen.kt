package com.sarkari.exam.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = com.sarkari.exam.ui.theme.PrimaryBlue)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(com.sarkari.exam.ui.theme.BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WelcomeSection()
            }
            item {
                ActionGrid(navController)
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = com.sarkari.exam.ui.theme.SurfaceWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Welcome back, Student!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = com.sarkari.exam.ui.theme.TextPrimary)
            Text(text = "Ready to conquer your exams today?", fontSize = 14.sp, color = com.sarkari.exam.ui.theme.TextSecondary)
        }
    }
}

@Composable
fun ActionGrid(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ActionCard("Mock Tests", Modifier.weight(1f)) {
            navController.navigate(Screen.MockTest.route)
        }
        ActionCard("AI Tutor", Modifier.weight(1f)) {
            navController.navigate(Screen.Tutor.route)
        }
    }
}

@Composable
fun ActionCard(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = com.sarkari.exam.ui.theme.PrimaryBlue.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, fontWeight = FontWeight.Bold, color = com.sarkari.exam.ui.theme.PrimaryBlue)
        }
    }
}
