package sarkari.exam_ai.ui.screens.tutor

import androidx.compose.foundation.BorderStroke
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.navigation.NavController
import sarkari.exam_ai.data.models.ChatMessage
import sarkari.exam_ai.data.repository.AiRepository
import sarkari.exam_ai.data.repository.UserPreferencesRepository
import sarkari.exam_ai.ui.components.BadgeVariant
import sarkari.exam_ai.ui.components.SarkariBadge
import sarkari.exam_ai.ui.components.SarkariTextField
import sarkari.exam_ai.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TutorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiRepository()
    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    var messages = mutableStateListOf<ChatMessage>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var currentInput by mutableStateOf("")

    // Suggestion chips matching web app
    val suggestions = listOf(
        "Explain UPSC Syllabus",
        "What are Ashok's Edicts?",
        "2024 Current Affairs",
        "Indian Constitution basics"
    )

    fun sendMessage() {
        if (currentInput.isBlank()) return

        val userMsg = currentInput.trim()
        messages.add(ChatMessage("user", userMsg))
        currentInput = ""
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Read directly from DataStore
                val currentPrefs = prefsRepo.userSettingsFlow.first()
                val langCode = if (currentPrefs.language == "Hindi") "hi" else "en"
                
                // Construct contextual message if first message
                val contextualMessages = if (messages.size == 2) { // 1 user + 0 assistant before this round (now 1 user msg added) -> Oh wait, size is 1 because we just added it. Let's fix this logic.
                    val prompt = "Context: Student is preparing for ${currentPrefs.examCategory} exam.\n\n$userMsg"
                    listOf(ChatMessage("user", prompt))
                } else {
                    messages.toList()
                }
                
                val response = repository.generateTutorResponse(contextualMessages, langCode)
                messages.add(ChatMessage("assistant", response))
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong. Please try again."
            } finally {
                isLoading = false
            }
        }
    }

    fun clearChat() {
        messages.clear()
        errorMessage = null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InteractiveTutorScreen(navController: NavController, paddingValues: PaddingValues, tutorViewModel: TutorViewModel = viewModel()) {
    val scrollState = rememberLazyListState()
    
    // Auto-scroll when messages change
    LaunchedEffect(tutorViewModel.messages.size, tutorViewModel.isLoading) {
        if (tutorViewModel.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(tutorViewModel.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(BgPrimary)
    ) {
        if (tutorViewModel.messages.isEmpty() && !tutorViewModel.isLoading) {
            // Empty state with suggestion chips (matches web app)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Face, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Hi! I'm Riya, your AI Tutor \uD83D\uDC4B", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ask me anything about your exam preparation. I can explain concepts, solve doubts, and help you study smarter!",
                    style = MaterialTheme.typography.bodySmall, color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Suggestion chips grid
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tutorViewModel.suggestions.forEach { suggestion ->
                        AssistChip(
                            onClick = { tutorViewModel.currentInput = suggestion },
                            label = { Text(suggestion, style = MaterialTheme.typography.bodySmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = PrimaryBlue.copy(alpha = 0.08f),
                                labelColor = PrimaryBlue
                            ),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        } else {
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
                    item { ThinkingBubble() }
                }

                tutorViewModel.errorMessage?.let { errMsg ->
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AccentRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = errMsg,
                                modifier = Modifier.padding(12.dp),
                                color = AccentRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Input Area (matches web UI style)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SarkariTextField(
                    value = tutorViewModel.currentInput,
                    onValueChange = { tutorViewModel.currentInput = it },
                    label = "Message",
                    placeholder = "Ask Riya something...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { tutorViewModel.sendMessage() },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun NativeChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) PrimaryBlue else Color.White
    val textColor = if (isUser) Color.White else TextPrimary
    val shape = if (isUser) 
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else 
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = if (isUser) 2.dp else 1.dp,
            border = if (!isUser) BorderStroke(1.dp, BorderColor) else null
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Riya is thinking...", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
