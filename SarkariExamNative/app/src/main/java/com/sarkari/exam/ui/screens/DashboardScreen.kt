package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    onNavigateToMockTest: () -> Unit, 
    onNavigateToStudyPlanner: () -> Unit = {}, 
    onNavigateToSettings: () -> Unit = {},
    onNavigateToTutor: () -> Unit = {},
    onNavigateToNews: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToPyq: () -> Unit = {},
    onNavigateToForum: () -> Unit = {},
    onNavigateToPeers: () -> Unit = {},
    onNavigateToNotes: () -> Unit = {},
    onNavigateToBlog: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 800.dp) // Constrain max width for tablets so it looks centralized and neat
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderSection(onNavigateToSettings) }
            item { QuickActionsGrid(onNavigateToMockTest, onNavigateToStudyPlanner, onNavigateToPyq, onNavigateToTutor, onNavigateToNews, onNavigateToNotes, isTablet) }
            item {
                if (isTablet) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f).clickable { onNavigateToAnalytics() }) { AnalyticsSection() }
                        Box(modifier = Modifier.weight(1f)) { AiEngineStatusCard() }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.clickable { onNavigateToAnalytics() }) { AnalyticsSection() }
                        AiEngineStatusCard()
                    }
                }
            }
            item { CommunityHubSection(onNavigateToForum, onNavigateToPeers, onNavigateToBlog) }
            item { DailyQuizHighlight(onNavigateToMockTest) }
        }
    }
}

@Composable
fun HeaderSection(onNavigateToSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFE0E7FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Overview", color = Color(0xFF4338CA), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF64748B), modifier = Modifier.size(20.dp).clickable { onNavigateToSettings() })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Good Morning, Aspirant! \uD83D\uDC4B", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text("Ready to conquer your goals today?", fontSize = 14.sp, color = Color.Gray)
        }
        
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Streak", fontSize = 10.sp, color = Color.Gray)
                    Text("\uD83D\uDD25 12 Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Readiness", fontSize = 10.sp, color = Color.Gray)
                    Text("\uD83D\uDCC8 84%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(onNavigateToMockTest: () -> Unit, onNavigateToStudyPlanner: () -> Unit, onNavigateToPyq: () -> Unit, onNavigateToTutor: () -> Unit, onNavigateToNews: () -> Unit, onNavigateToNotes: () -> Unit, isTablet: Boolean) {
    val actions = listOf(
        Pair("Mock Test", Icons.Default.Edit),
        Pair("Study Planner", Icons.Default.DateRange),
        Pair("PYQ Test", Icons.Default.CheckCircle),
        Pair("AI Notes", Icons.Default.Create),
        Pair("News", Icons.Default.List),
        Pair("Tutor", Icons.Default.Star)
    )

    if (isTablet) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions.forEach { (title, icon) ->
                QuickActionCard(title, icon, Modifier.weight(1f)) { 
                    if (title == "Mock Test") onNavigateToMockTest()
                    if (title == "Study Planner") onNavigateToStudyPlanner()
                    if (title == "PYQ Test") onNavigateToPyq()
                    if (title == "AI Notes") onNavigateToNotes()
                    if (title == "News") onNavigateToNews()
                    if (title == "Tutor") onNavigateToTutor()
                }
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions.take(3).forEach { (title, icon) ->
                QuickActionCard(title, icon, Modifier.weight(1f)) { 
                    if (title == "Mock Test") onNavigateToMockTest()
                    if (title == "Study Planner") onNavigateToStudyPlanner()
                    if (title == "PYQ Test") onNavigateToPyq()
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions.drop(3).forEach { (title, icon) ->
                QuickActionCard(title, icon, Modifier.weight(1f)) {
                    if (title == "AI Notes") onNavigateToNotes()
                    if (title == "News") onNavigateToNews()
                    if (title == "Tutor") onNavigateToTutor()
                } 
            }
        }
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Go Now ->", color = Color(0xFF2563EB), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnalyticsSection() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Performance Analytics", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
            ProgressBar("History", 75f, Color(0xFF3B82F6))
            ProgressBar("Geography", 45f, Color(0xFFF59E0B))
            ProgressBar("Polity", 90f, Color(0xFF10B981))
            ProgressBar("Current Affairs", 30f, Color(0xFFEF4444))
        }
    }
}

@Composable
fun ProgressBar(title: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("${progress.toInt()}%", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun AiEngineStatusCard() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFECFDF5),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("AI High-Capacity Engine", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("SCALABLE MODE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color(0xFF10B981), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Available Slots: 250 Active", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Engine is ready for 2,00,000+ daily requests.", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CommunityHubSection(onNavigateToForum: () -> Unit, onNavigateToPeers: () -> Unit, onNavigateToBlog: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp), color = Color(0xFFEFF6FF), modifier = Modifier.weight(1f).clickable { onNavigateToForum() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF2563EB))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Forum", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                Text("Join discussion", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFBEB), modifier = Modifier.weight(1f).clickable { onNavigateToBlog() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.List, contentDescription = null, tint = Color(0xFFD97706))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Read Blog", fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                Text("Sarkari Hub Articles", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp), color = Color(0xFFFDF4FF), modifier = Modifier.weight(1f).clickable { onNavigateToPeers() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFD946EF))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Peers", fontWeight = FontWeight.Bold, color = Color(0xFF86198F))
                Text("Find partners", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DailyQuizHighlight(onNavigateToMockTest: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF2563EB))))
                .padding(16.dp)
        ) {
            Column {
                Text("DAILY DRILL", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Text("Take Today's Challenge", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                Button(
                    onClick = onNavigateToMockTest,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Now", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
