package sarkari.exam_ai.ui.screens.mocktest

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import sarkari.exam_ai.data.models.Question
import sarkari.exam_ai.data.repository.AiRepository
import sarkari.exam_ai.data.repository.UserPreferencesRepository
import sarkari.exam_ai.ui.components.ButtonVariant
import sarkari.exam_ai.ui.components.ExamSelectorCard
import sarkari.exam_ai.ui.components.SarkariButton
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.components.SarkariTextField
import sarkari.exam_ai.ui.components.SarkariBadge
import sarkari.exam_ai.ui.components.BadgeVariant
import sarkari.exam_ai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class QuizState { SETUP, ACTIVE, RESULT }

class MockTestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiRepository()
    private val firebaseManager = sarkari.exam_ai.data.firebase.FirebaseManager()
    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    var state by mutableStateOf(QuizState.SETUP)
    var questions = mutableStateListOf<Question>()
    var currentIndex by mutableStateOf(0)
    var answers = mutableStateMapOf<Int, String>()
    var isLoading by mutableStateOf(false)
    var timerSeconds by mutableStateOf(600)
    var errorMessage by mutableStateOf<String?>(null)

    // Quiz Setup Params — pre-filled from user's saved preference
    var selectedExam by mutableStateOf("")
    var selectedSubject by mutableStateOf("")

    init {
        // Auto-load saved exam preference so the dropdown pre-selects the right exam
        viewModelScope.launch {
            val prefs = prefsRepo.userSettingsFlow.first()
            if (selectedExam.isBlank() && prefs.examCategory.isNotBlank()) {
                selectedExam = prefs.examCategory
            }
        }
    }

    fun startQuiz(language: String = "en") {
        if (selectedExam.isBlank()) {
            errorMessage = "Please select a target exam"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val prefs = prefsRepo.userSettingsFlow.first()
            val langCode = when (prefs.language) {
                "Hindi" -> "hi"; "Marathi" -> "mr"; "Tamil" -> "ta"
                "Telugu" -> "te"; "Kannada" -> "kn"; else -> "en"
            }
            try {
                val parsed = repository.generateMockQuestions(
                    exam = selectedExam,
                    subject = selectedSubject,
                    count = 5,
                    language = langCode
                )
                if (parsed.isNotEmpty()) {
                    questions.clear()
                    questions.addAll(parsed)
                    state = QuizState.ACTIVE
                    startTimer()
                } else {
                    errorMessage = "Could not generate questions. Please try again."
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to connect to AI server"
            } finally {
                isLoading = false
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (timerSeconds > 0 && state == QuizState.ACTIVE) {
                delay(1000)
                timerSeconds--
            }
            if (timerSeconds == 0) finishQuiz()
        }
    }

    fun finishQuiz() {
        state = QuizState.RESULT
        val score = getScore()
        val total = questions.size
        val uid = firebaseManager.auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                firebaseManager.saveTestResult(uid, selectedExam, selectedSubject, score, total)
            }
        }
    }

    fun getScore(): Int {
        var score = 0
        questions.forEachIndexed { index, q ->
            if (answers[q.id ?: (index + 1)] == q.correctAnswer) score++
        }
        return score
    }
}

@Composable
fun MockTestScreen(navController: NavController, paddingValues: PaddingValues, viewModel: MockTestViewModel = viewModel()) {
    Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(BgPrimary)) {
        when (viewModel.state) {
            QuizState.SETUP -> QuizSetupView(viewModel)
            QuizState.ACTIVE -> QuizActiveView(viewModel)
            QuizState.RESULT -> QuizResultView(viewModel)
        }
    }
}

@Composable
fun QuizSetupView(viewModel: MockTestViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Hero header ──────────────────────────────────────────────────
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Psychology, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Daily Smart Mock Test", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text("10-minute adaptive quiz to boost your preparation", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // ── Exam selector (website style dropdown) ────────────────────────
        item {
            SarkariCard(padding = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // ExamSelectorCard — exact website dropdown
                    ExamSelectorCard(
                        selectedExam = viewModel.selectedExam,
                        onExamSelected = { viewModel.selectedExam = it }
                    )

                    SarkariTextField(
                        value = viewModel.selectedSubject,
                        onValueChange = { viewModel.selectedSubject = it },
                        label = "Subject (optional)",
                        placeholder = "e.g. History, Polity, Economics"
                    )

                    if (viewModel.errorMessage != null) {
                        Text(viewModel.errorMessage!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                    }

                    SarkariButton(
                        text = "Start Mock Test",
                        onClick = { viewModel.startQuiz() },
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Primary,
                        isLoading = viewModel.isLoading,
                        icon = { Icon(Icons.Default.PlayArrow, null) }
                    )
                }
            }
        }

        // ── Quick pick chips ──────────────────────────────────────────────
        item {
            Text("Quick Select", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sarkari.exam_ai.ui.components.EXAM_OPTIONS.forEach { option ->
                    FilterChip(
                        selected = viewModel.selectedExam == option.name,
                        onClick = { viewModel.selectedExam = option.name },
                        label = { Text("${option.emoji} ${option.name.split(" ").first()}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuizActiveView(viewModel: MockTestViewModel) {
    val q = viewModel.questions.getOrNull(viewModel.currentIndex) ?: return
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Headers (Timer, Progress)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Question ${viewModel.currentIndex + 1}/${viewModel.questions.size}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                Text(
                    text = String.format("%02d:%02d", viewModel.timerSeconds / 60, viewModel.timerSeconds % 60),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (viewModel.timerSeconds < 60) AccentRed else AccentOrange
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LinearProgressIndicator(
            progress = (viewModel.currentIndex + 1).toFloat() / viewModel.questions.size,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = PrimaryBlue,
            trackColor = BorderColor
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SarkariCard {
            Text(text = q.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            q.options.forEach { option ->
                val isSelected = viewModel.answers[q.id] == option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.answers[q.id as Int] = option },
                    color = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else BorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, style = MaterialTheme.typography.bodyLarge, color = if (isSelected) PrimaryBlue else TextPrimary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SarkariButton(
                text = "Previous",
                onClick = { if (viewModel.currentIndex > 0) viewModel.currentIndex-- },
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Secondary,
                enabled = viewModel.currentIndex > 0
            )
            SarkariButton(
                text = if (viewModel.currentIndex == viewModel.questions.size - 1) "Finish" else "Next",
                onClick = { 
                    if (viewModel.currentIndex < viewModel.questions.size - 1) {
                        viewModel.currentIndex++
                    } else {
                        viewModel.finishQuiz()
                    }
                },
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Primary
            )
        }
    }
}

@Composable
fun QuizResultView(viewModel: MockTestViewModel) {
    val score = viewModel.getScore()
    val total = if (viewModel.questions.isNotEmpty()) viewModel.questions.size else 1
    val percent = (score.toFloat() / total * 100).toInt()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Score Summary ---
        item {
            SarkariCard(padding = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            drawArc(
                                color = BorderColor,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = PrimaryBlue,
                                startAngle = -90f,
                                sweepAngle = 360f * (percent / 100f),
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Your Score $score/$total",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Accuracy: $percent% • Time: 10m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SarkariButton(
                        text = "Take Another Test",
                        onClick = { 
                            viewModel.state = QuizState.SETUP
                            viewModel.timerSeconds = 600
                            viewModel.currentIndex = 0
                            viewModel.answers.clear() 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        variant = ButtonVariant.Primary,
                        icon = { Icon(Icons.Default.Refresh, null) }
                    )
                }
            }
        }

        // --- 2. Review Header ---
        item {
            Text(
                text = "Question Review",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // --- 3. Review Items ---
        itemsIndexed(viewModel.questions) { index, q ->
            val userAnswer = viewModel.answers[q.id]
            val isCorrect = userAnswer == q.correctAnswer
            
            SarkariCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isCorrect) AccentGreen.copy(alpha = 0.2f) else AccentRed.copy(alpha = 0.2f),
                padding = 20.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SarkariBadge(
                                text = if (isCorrect) "CORRECT" else "INCORRECT",
                                variant = if (isCorrect) BadgeVariant.Success else BadgeVariant.Red
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Question ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) AccentGreen else AccentRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Options in Review
                    q.options.forEach { option ->
                        val isCorrectOption = option == q.correctAnswer
                        val isUserSelection = option == userAnswer
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (isCorrectOption) AccentGreen.copy(alpha = 0.05f) else Color.Transparent,
                            border = if (isCorrectOption) BorderStroke(1.dp, AccentGreen) else BorderStroke(1.dp, BorderColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCorrectOption) AccentGreen else if (isUserSelection) AccentRed else TextPrimary
                                )
                                if (isCorrectOption) {
                                    Icon(Icons.Default.CheckCircle, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                } else if (isUserSelection && !isCorrect) {
                                    Icon(Icons.Default.Cancel, null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    
                    // AI Explanation (Image 3 Style)
                    if (q.explanation != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "AI EXPLANATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = q.explanation!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
