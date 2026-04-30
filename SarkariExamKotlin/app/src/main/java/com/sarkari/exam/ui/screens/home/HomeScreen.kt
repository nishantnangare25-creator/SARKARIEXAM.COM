package com.sarkari.exam.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    userName: String = "Aarav Sharma",
    targetExam: String = "SSC CGL 2024"
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hello, $userName 👋", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PrimaryBlue)
                        Text("Prepare smarter with AI", fontSize = 14.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = PrimaryBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = PrimaryBlue)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search exams, tests, notes...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color.White
                ),
                singleLine = true
            )

            // Target Exam Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = PrimaryBlue,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Target Exam", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(targetExam, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { /* Change Exam */ }
                    ) {
                        Text("Change", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }

            // Streak Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("5 Day Streak", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 0.7f,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = AccentOrange,
                            trackColor = Color(0xFFFFF0E6)
                        )
                    }
                }
            }

            // AI Recommendation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFF7ED), // Very light orange
                border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentOrange)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Suggestion", color = AccentOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Take a quick Quantitative Aptitude test to improve.", color = TextDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onNavigate("mock_test") },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Start Test", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Practice Grid
            Text("Practice & Prepare", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            
            val gridItems = listOf(
                Pair("Mock Tests", Icons.Default.FactCheck),
                Pair("PYQ Practice", Icons.Default.History),
                Pair("Topic-wise Tests", Icons.Default.Category),
                Pair("Daily Quiz", Icons.Default.Timer),
                Pair("Full-Length", Icons.Default.Assignment),
                Pair("AI Test Gen", Icons.Default.AutoFixHigh)
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in gridItems.indices step 2) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        GridCard(gridItems[i].first, gridItems[i].second, Modifier.weight(1f)) { onNavigate("mock_test") }
                        if (i + 1 < gridItems.size) {
                            GridCard(gridItems[i+1].first, gridItems[i+1].second, Modifier.weight(1f)) { onNavigate("mock_test") }
                        }
                    }
                }
            }

            // AI Tools
            Text("AI Tools", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GridCard("Smart Notes", Icons.Default.NoteAdd, Modifier.weight(1f)) { onNavigate("notes_gen") }
                GridCard("Paper Analyzer", Icons.Default.Insights, Modifier.weight(1f)) { onNavigate("paper_analyzer") }
            }

            // Weak Topics
            Text("Focus Areas (Weak Topics)", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    WeakTopicRow("Number System", 0.4f)
                    WeakTopicRow("Indian History", 0.55f)
                    Button(
                        onClick = { onNavigate("mock_test") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.1f), contentColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Improve Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GridCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() }.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
        }
    }
}

@Composable
fun WeakTopicRow(topic: String, progress: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(topic, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
            Text("${(progress * 100).toInt()}% Accuracy", color = TextMuted, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = PrimaryBlue,
            trackColor = PrimaryBlue.copy(alpha = 0.1f)
        )
    }
}
