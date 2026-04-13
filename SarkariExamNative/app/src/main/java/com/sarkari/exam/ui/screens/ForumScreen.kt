package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForumViewModel = viewModel()
) {
    if (viewModel.selectedThread.value != null) {
        ThreadDetailView(viewModel)
        return
    }

    val categories = listOf(Pair("all", "All"), Pair("current-affairs", "Current Affairs"), Pair("exam-strategies", "Strategies"), Pair("doubt-solving", "Doubts"), Pair("general", "General"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Forum", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showNew.value = true }, containerColor = Color(0xFF2563EB), contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "New Post")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Category Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()).padding(bottom = 16.dp)
                ) {
                    categories.forEach { (id, name) ->
                        val isSelected = viewModel.activeCategory.value == id
                        Surface(
                            modifier = Modifier.clickable { viewModel.activeCategory.value = id },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFF2563EB) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else Color(0xFFE2E8F0))
                        ) {
                            Text(name, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // New Post Dialog Overlay Check
                if (viewModel.showNew.value) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Create New Post", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                            OutlinedTextField(value = viewModel.newTitle.value, onValueChange = { viewModel.newTitle.value = it }, placeholder = { Text("Post Title") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = true)
                            OutlinedTextField(value = viewModel.newContent.value, onValueChange = { viewModel.newContent.value = it }, placeholder = { Text("What's on your mind?") }, modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 12.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { viewModel.showNew.value = false }) { Text("Cancel", color = Color.Gray) }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { viewModel.createPost() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) { Text("Post") }
                            }
                        }
                    }
                }

                val threadsList = viewModel.getFilteredThreads()
                if (threadsList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No posts found.", color = Color.Gray) }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(threadsList) { thread ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectedThread.value = thread }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                            Text(thread.category.uppercase(), fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Text(thread.createdAt, fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(thread.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("By ${thread.author} • ${thread.replies.size} replies", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadDetailView(viewModel: ForumViewModel) {
    val thread = viewModel.selectedThread.value ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discussion", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectedThread.value = null }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = viewModel.replyText.value,
                        onValueChange = { viewModel.replyText.value = it },
                        placeholder = { Text("Write a reply...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.addReply() },
                        modifier = Modifier.background(Color(0xFF2563EB), RoundedCornerShape(24.dp))
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Original Post
            item {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(thread.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 8.dp), lineHeight = 28.sp)
                        Text("Posted by ${thread.author} • ${thread.createdAt}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                        Text(thread.content, fontSize = 15.sp, color = Color(0xFF334155), lineHeight = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Replies (${thread.replies.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            }

            // Replies
            items(thread.replies) { reply ->
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(reply.author, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 4.dp))
                        Text(reply.content, fontSize = 14.sp, color = Color(0xFF475569))
                    }
                }
            }
        }
    }
}
