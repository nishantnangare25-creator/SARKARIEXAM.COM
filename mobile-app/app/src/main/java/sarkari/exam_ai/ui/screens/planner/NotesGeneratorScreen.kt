package sarkari.exam_ai.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import sarkari.exam_ai.data.repository.AiRepository
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.theme.*

class NotesViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var selectedExam by mutableStateOf("UPSC")
    var selectedSubject by mutableStateOf("")
    var topics by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var notes by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun generateNotes(language: String = "en") {
        if (selectedSubject.isBlank()) {
            errorMessage = "Please enter a subject"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = repository.generateNotes(
                    exam = selectedExam,
                    subject = selectedSubject,
                    topics = if (topics.isNotBlank()) listOf(topics) else emptyList(),
                    language = language
                )
                notes = response
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to generate notes"
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun NotesGeneratorScreen(navController: NavController, paddingValues: PaddingValues, viewModel: NotesViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Intro
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = AccentGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.School, contentDescription = null, tint = AccentGreen, modifier = Modifier.padding(10.dp))
            }
            Column {
                Text("Instant Study Material", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text("AI-powered notes for any topic", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // Input Form
        SarkariCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SarkariTextField(
                    value = viewModel.selectedExam,
                    onValueChange = { viewModel.selectedExam = it },
                    label = "Target Exam",
                    placeholder = "e.g. UPSC, SSC"
                )
                
                SarkariTextField(
                    value = viewModel.selectedSubject,
                    onValueChange = { viewModel.selectedSubject = it },
                    label = "Subject",
                    placeholder = "e.g. Indian Polity"
                )

                SarkariTextField(
                    value = viewModel.topics,
                    onValueChange = { viewModel.topics = it },
                    label = "Topics (Optional)",
                    placeholder = "e.g. Fundamental Rights"
                )

                if (viewModel.errorMessage != null) {
                    Text(viewModel.errorMessage!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                SarkariButton(
                    text = "Generate AI Notes",
                    onClick = { viewModel.generateNotes() },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                    isLoading = viewModel.isLoading,
                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (viewModel.notes != null) {
            SarkariCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Generated Material", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = viewModel.notes!!,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
