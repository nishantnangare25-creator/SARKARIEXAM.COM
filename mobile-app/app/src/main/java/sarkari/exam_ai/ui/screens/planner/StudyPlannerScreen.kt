package sarkari.exam_ai.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import sarkari.exam_ai.data.repository.AiRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import sarkari.exam_ai.data.models.ChatMessage
import sarkari.exam_ai.data.repository.UserPreferencesRepository
import sarkari.exam_ai.ui.components.ButtonVariant
import sarkari.exam_ai.ui.components.ExamSelectorCard
import sarkari.exam_ai.ui.components.SarkariButton
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.components.SarkariTextField
import sarkari.exam_ai.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StudyPlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiRepository()
    private val firebaseManager = sarkari.exam_ai.data.firebase.FirebaseManager()
    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    var selectedExam by mutableStateOf("UPSC Civil Services")
    var selectedHours by mutableStateOf("4")
    var selectedLevel by mutableStateOf("Beginner")
    var isLoading by mutableStateOf(false)
    var plan by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            val prefs = prefsRepo.userSettingsFlow.first()
            if (prefs.examCategory.isNotBlank()) selectedExam = prefs.examCategory
        }
    }

    fun generatePlan() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val prefs = prefsRepo.userSettingsFlow.first()
            val langCode = when (prefs.language) {
                "Hindi" -> "hi"; "Marathi" -> "mr"; "Tamil" -> "ta"
                "Telugu" -> "te"; "Kannada" -> "kn"; else -> "en"
            }
            try {
                val hoursInt = selectedHours.filter { it.isDigit() }.toIntOrNull() ?: 4
                val response = repository.generateStudyPlan(
                    exam = selectedExam,
                    hours = hoursInt,
                    level = selectedLevel,
                    weakSubjects = emptyList(),
                    language = langCode
                )
                plan = response
                val uid = firebaseManager.auth.currentUser?.uid
                if (uid != null && response.isNotBlank()) {
                    firebaseManager.saveStudyPlan(uid, response)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to generate study plan"
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun StudyPlannerScreen(navController: NavController, paddingValues: PaddingValues, viewModel: StudyPlannerViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Book, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(10.dp))
            }
            Column {
                Text("Personalized Roadmap", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("AI-generated schedule just for you", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Calendar Row
        Text("This Week", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val days = listOf("Mon\n12", "Tue\n13", "Wed\n14", "Thu\n15", "Fri\n16", "Sat\n17", "Sun\n18")
            items(days) { dayStr ->
                val isActive = dayStr == "Wed\n14"
                Surface(
                    color = if (isActive) PrimaryBlue else Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if(isActive) PrimaryBlue else BorderColor),
                    modifier = Modifier.size(width = 60.dp, height = 70.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = dayStr,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if(isActive) Color.White else TextPrimary,
                            fontWeight = if(isActive) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form Content
        SarkariCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Website-style exam selector
                ExamSelectorCard(
                    selectedExam = viewModel.selectedExam,
                    onExamSelected = { viewModel.selectedExam = it }
                )

                SarkariTextField(
                    value = viewModel.selectedHours,
                    onValueChange = { viewModel.selectedHours = it },
                    label = "Hours per Day",
                    placeholder = "e.g. 4, 6"
                )

                SarkariTextField(
                    value = viewModel.selectedLevel,
                    onValueChange = { viewModel.selectedLevel = it },
                    label = "Current Level",
                    placeholder = "e.g. Beginner, Intermediate"
                )

                if (viewModel.errorMessage != null) {
                    Text(viewModel.errorMessage!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                SarkariButton(
                    text = "Generate Master Plan",
                    onClick = { viewModel.generatePlan() },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                    isLoading = viewModel.isLoading,
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (viewModel.plan != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Text("Your AI Study Roadmap", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
            ) {
                Text(
                    text = viewModel.plan!!,
                    modifier = Modifier.padding(20.dp),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = TextPrimary
                )
            }
        }
    }
}
