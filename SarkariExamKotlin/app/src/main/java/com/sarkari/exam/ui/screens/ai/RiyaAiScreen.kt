package com.sarkari.exam.ui.screens.ai

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.viewmodels.AiState
import com.sarkari.exam.ui.viewmodels.AiViewModel
import com.sarkari.exam.data.api.AiMessage

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiyaAiScreen(
    onBack: () -> Unit = {},
    viewModel: AiViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("Hello! I'm Riya, your AI Tutor. How can I help you with your preparation today? 👋", false)
        )
    }
    
    val aiState by viewModel.aiState.collectAsState()
    
    LaunchedEffect(aiState) {
        if (aiState is AiState.Success) {
            chatMessages.add(ChatMessage((aiState as AiState.Success).content, false))
            viewModel.resetState()
        } else if (aiState is AiState.Error) {
            chatMessages.add(ChatMessage("Sorry, I encountered an error: ${(aiState as AiState.Error).message}", false))
            viewModel.resetState()
        }
    }

    // Convert local messages to API format
    fun generateAIResponse() {
        if (messageText.isNotBlank()) {
            chatMessages.add(ChatMessage(messageText, true))
            
            // Build message history for API
            val apiMessages = chatMessages.map { 
                AiMessage(role = if (it.isUser) "user" else "assistant", content = it.text) 
            }
            
            viewModel.generateContent(apiMessages)
            messageText = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(PrimaryBlue, AccentPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = BackgroundWhite, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Riya AI", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextPrimary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Online Assistant", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary) }
                },
                actions = {
                    IconButton(onClick = { chatMessages.clear() }) { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = TextMuted) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceGray)
        ) {
            // Chat Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(chatMessages) { message ->
                    ChatBubble(message)
                }
            }

            // Quick Suggestions
            if (chatMessages.size == 1) {
                QuickSuggestions { suggestion ->
                    chatMessages.add(ChatMessage(suggestion, true))
                    
                    val apiMessages = chatMessages.map { 
                        AiMessage(role = if (it.isUser) "user" else "assistant", content = it.text) 
                    }
                    viewModel.generateContent(apiMessages)
                }
            }

            // Show typing indicator
            if (aiState is AiState.Loading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                }
            }

            // Input Section
            ChatInputArea(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    generateAIResponse()
                }
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isUser) PrimaryBlue else BackgroundWhite,
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = if (message.isUser) 24.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 24.dp
            ),
            border = if (!message.isUser) BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)) else null,
            shadowElevation = if (message.isUser) 4.dp else 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                color = if (message.isUser) BackgroundWhite else TextPrimary,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, fontWeight = if (message.isUser) FontWeight.Medium else FontWeight.Normal)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSuggestions(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf("Summarize Modern History", "Solve a Math Doubt", "SSC CGL Strategy", "Practice PYQs")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        suggestions.forEach { suggestion ->
            SuggestionChip(
                onClick = { onSuggestionClick(suggestion) },
                label = { Text(suggestion, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                colors = AssistChipDefaults.assistChipColors(containerColor = BackgroundWhite, labelColor = PrimaryBlue),
                border = AssistChipDefaults.assistChipBorder(borderColor = PrimaryBlue.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputArea(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundWhite,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = TextMuted)
            }
            
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask Riya anything...", color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = SurfaceGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            FloatingActionButton(
                onClick = onSend,
                containerColor = PrimaryBlue,
                contentColor = BackgroundWhite,
                shape = CircleShape,
                modifier = Modifier.size(52.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
    }
}
