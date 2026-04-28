package com.sarkari.exam.ui.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sarkari.exam.data.models.ChatMessage
import com.sarkari.exam.data.repository.AiRepository
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.launch

class TutorViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var messages = mutableStateListOf<ChatMessage>(
        ChatMessage("assistant", "Hello! I am Riya, your AI Tutor. How can I help you today?")
    )
    var isLoading by mutableStateOf(false)
    var currentInput by mutableStateOf("")

    fun sendMessage(apiKey: String = com.sarkari.exam.data.AppConstants.GROQ_API_KEY) {
        if (currentInput.isBlank()) return
        
        val userMsg = currentInput.trim()
        messages.add(ChatMessage("user", userMsg))
        currentInput = ""
        isLoading = true
        
        // Use coroutine to fetch AI response
        viewModelScope.launch {
            val response = repository.getAiResponse(messages, apiKey, "groq")
                ?: repository.getAiResponse(messages, com.sarkari.exam.data.AppConstants.OPENROUTER_API_KEY_1, "en", "DL", "groq")
            
            isLoading = false
            if (response != null) {
                messages.add(ChatMessage("assistant", response))
            } else {
                messages.add(ChatMessage("assistant", "I am currently experiencing some heavy load. Please try again in a moment!"))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveTutorScreen(navController: NavController, tutorViewModel: TutorViewModel = viewModel()) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll when messages change
    LaunchedEffect(tutorViewModel.messages.size, tutorViewModel.isLoading) {
        if (tutorViewModel.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(tutorViewModel.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Text("Riya AI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                            Text("Online", fontSize = 10.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = BackgroundBody, shape = RoundedCornerShape(4.dp)) {
                                Text("EN", modifier = Modifier.padding(horizontal = 4.dp), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* PDF Download */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                    IconButton(onClick = { tutorViewModel.messages.clear(); tutorViewModel.messages.add(ChatMessage("assistant", "Chat cleared! How can I help?")) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = AccentRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBody)
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(tutorViewModel.messages) { msg ->
                    NativeChatBubble(msg)
                }
                
                if (tutorViewModel.isLoading) {
                    item {
                        ThinkingBubble()
                    }
                }
            }

            // Input Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = tutorViewModel.currentInput,
                        onValueChange = { tutorViewModel.currentInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask Riya anything...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        enabled = !tutorViewModel.isLoading,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    
                    FloatingActionButton(
                        onClick = { tutorViewModel.sendMessage() },
                        modifier = Modifier.size(48.dp),
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        if (tutorViewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NativeChatBubble(msg: ChatMessage) {
    val isAssistant = msg.role == "assistant"
    val alignment = if (isAssistant) Alignment.Start else Alignment.End
    val bgColor = if (isAssistant) Color.White else PrimaryBlue
    val textColor = if (isAssistant) TextPrimary else Color.White
    val shape = if (isAssistant) {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isAssistant) {
                Box(modifier = Modifier.size(24.dp).background(PrimaryBlue, CircleShape), contentAlignment = Alignment.Center) {
                    Text("R", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(if (isAssistant) "Riya" else "You", fontSize = 11.sp, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = 1.dp,
            border = if (isAssistant) androidx.compose.foundation.BorderStroke(1.dp, BorderColor) else null
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 14.sp,
                color = textColor,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ThinkingBubble() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(24.dp).background(PrimaryBlue.copy(alpha = 0.5f), CircleShape))
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) {
                    Box(modifier = Modifier.size(6.dp).background(PrimaryBlue, CircleShape))
                }
            }
        }
    }
}

