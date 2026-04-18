package com.sarkari.exam.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.theme.*

data class ForumThread(
    val id: String, val title: String, val category: String, val author: String, val content: String, val createdAt: String, val repliesCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(navController: NavController) {
    
    val demoThreads = listOf(
        ForumThread("1", "How to prepare for UPSC Prelims 2026?", "Exam Strategies", "Rahul S.", "I'm starting my UPSC preparation. What's the best strategy for Prelims?", "2 hours ago", 2),
        ForumThread("2", "Budget 2026 Key Highlights for Exams", "Current Affairs", "Sneha P.", "Here are the key points from Budget 2026 that are important for competitive exams...", "5 hours ago", 1),
        ForumThread("3", "Difference between Article 14 and Article 19?", "Doubt Solving", "Anjali R.", "Can someone explain the fundamental difference between Article 14 (Right to Equality) and Article 19 (Right to Freedom)?", "1 day ago", 0)
    )
    
    var activeCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Current Affairs", "Exam Strategies", "Doubt Solving")

    // Thread detail view simulator
    var selectedThread by remember { mutableStateOf<ForumThread?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedThread == null) "Discussion Forum" else "Thread", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { if (selectedThread != null) selectedThread = null else navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (selectedThread == null) {
                FloatingActionButton(onClick = { /* Open New Post dialog */ }, containerColor = PrimaryBlue) {
                    Icon(Icons.Default.Add, contentDescription = "New Thread", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            
            if (selectedThread == null) {
                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(activeCategory),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = PrimaryBlue,
                    edgePadding = 20.dp
                ) {
                    categories.forEachIndexed { index, cat ->
                        Tab(
                            selected = activeCategory == cat,
                            onClick = { activeCategory = cat },
                            text = { Text(cat, fontWeight = if (activeCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
                
                val filtered = if (activeCategory == "All") demoThreads else demoThreads.filter { it.category == activeCategory }
                
                LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filtered) { thread ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedThread = thread },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                        Text(thread.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                    }
                                    Text(thread.createdAt, fontSize = 11.sp, color = TextSecondary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(thread.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("By ${thread.author}  •  ${thread.repliesCount} replies", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            } else {
                // Detail View
                Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                    Text(selectedThread!!.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                                Text(selectedThread!!.createdAt, fontSize = 11.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(selectedThread!!.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Posted by ${selectedThread!!.author}", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(selectedThread!!.content, fontSize = 14.sp, lineHeight = 22.sp, color = TextPrimary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Replies (${selectedThread!!.repliesCount})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Input Bar Mock
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Write a reply...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        IconButton(onClick = { }, modifier = Modifier.background(PrimaryBlue, RoundedCornerShape(50)).size(48.dp)) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

