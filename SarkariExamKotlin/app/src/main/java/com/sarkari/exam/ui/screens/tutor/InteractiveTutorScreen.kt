package com.sarkari.exam.ui.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveTutorScreen(navController: NavController) {
    var messages by remember { mutableStateOf(listOf(ChatMessage("Hello! I am Riya, your AI Tutor. How can I help you today?", false))) }
    var currentInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Riya AI Tutor", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = com.sarkari.exam.ui.theme.PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(com.sarkari.exam.ui.theme.BackgroundLight)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask anything...") },
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            messages = messages + ChatMessage(currentInput, true)
                            val q = currentInput
                            currentInput = ""
                            coroutineScope.launch {
                                // Simulate AI typing/fetching Retrofit response
                                kotlinx.coroutines.delay(1000)
                                messages = messages + ChatMessage("I will help you with: $q. (This is a native mock AI response)", false)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = com.sarkari.exam.ui.theme.SecondaryOrange)
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val align = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (msg.isUser) com.sarkari.exam.ui.theme.PrimaryBlue else Color.White
    val textColor = if (msg.isUser) Color.White else com.sarkari.exam.ui.theme.TextPrimary

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color,
            shadowElevation = 1.dp
        ) {
            Text(
                text = msg.text,
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
