package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveTutorScreen(
    onNavigateBack: () -> Unit,
    viewModel: InteractiveTutorViewModel = viewModel()
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll when messages change
    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF2563EB)), contentAlignment = Alignment.Center) {
                            Text("R", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Riya AI Tutor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.currentInput.value,
                        onValueChange = { viewModel.currentInput.value = it },
                        placeholder = { Text("Ask Riya a question...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF2563EB)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = !viewModel.isLoading.value && viewModel.currentInput.value.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (viewModel.isLoading.value) Color.Gray else Color(0xFF2563EB))
                    ) {
                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues)) {
            if (viewModel.messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("👋 Hello! I am Riya.", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("Your personal AI tutor. Ask me to explain concepts, solve math problems, or review essays.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
                    
                    val suggestions = listOf("Explain Quantum Mechanics", "Help me build a study plan", "What are the Fundamental Rights?")
                    suggestions.forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { viewModel.currentInput.value = suggestion }
                        ) {
                            Text(suggestion, modifier = Modifier.padding(16.dp), color = Color(0xFF2563EB), fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.messages) { msg ->
                        val isUser = msg.role == "user"
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                                if (!isUser) {
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF2563EB)), contentAlignment = Alignment.Center) {
                                        Text("R", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Riya AI", fontSize = 12.sp, color = Color.Gray)
                                } else {
                                    Text("You", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF64748B)), contentAlignment = Alignment.Center) {
                                        Text("U", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isUser) 16.dp else 0.dp, bottomEnd = if (isUser) 0.dp else 16.dp),
                                color = if (isUser) Color(0xFF2563EB) else Color.White,
                                shadowElevation = if (isUser) 0.dp else 2.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    modifier = Modifier.padding(12.dp),
                                    color = if (isUser) Color.White else Color(0xFF1E293B),
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
