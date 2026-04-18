package com.sarkari.exam.ui.screens.mocktest

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.sarkari.exam.data.models.Question
import com.sarkari.exam.data.repository.AiRepository
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class QuizState { SETUP, ACTIVE, RESULT }

class MockTestViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var state by mutableStateOf(QuizState.SETUP)
    var questions = mutableStateListOf<Question>()
    var currentIndex by mutableStateOf(0)
    var answers = mutableStateMapOf<Int, String>()
    var isLoading by mutableStateOf(false)
    var timerSeconds by mutableStateOf(600)
    var errorMsg by mutableStateOf<String?>(null)
    
    // Quiz Setup Params
    var selectedExam by mutableStateOf("")
    var selectedSubject by mutableStateOf("")

    fun startQuiz(apiKey: String) {
        isLoading = true
        errorMsg = null
        viewModelScope.launch {
            val prompt = """
                You are an expert competitive exam creator. Generate exactly 5 practice MCQ questions for $selectedExam exam, subject $selectedSubject.
                CRITICAL RULES:
                1. DO NOT USE JSON. Respond STRICTLY in plain text/markdown format.
                2. Format EACH question exactly like this:
                Q: [Question text]
                A) [Option 1]
                B) [Option 2]
                C) [Option 3]
                D) [Option 4]
                Answer: [A, B, C, or D]
                Explanation: [1-2 sentences of explanation]
                Return ONLY the structured text, no extra conversational filler.
            """.trimIndent()
            
            val response = repository.getAiResponse(listOf(com.sarkari.exam.data.models.ChatMessage("user", prompt)), apiKey)
            
            if (response != null) {
                val parsed = repository.parseQuestions(response)
                if (parsed.isNotEmpty()) {
                    questions.clear()
                    questions.addAll(parsed)
                    state = QuizState.ACTIVE
                    startTimer()
                } else {
                    loadFallbackQuestions()
                }
            } else {
                loadFallbackQuestions()
            }
            isLoading = false
        }
    }

    private fun loadFallbackQuestions() {
        val staticQs = listOf(
            Question(1, "Which layer of the atmosphere contains the ozone layer?", listOf("Troposphere", "Stratosphere", "Mesosphere", "Exosphere"), "Stratosphere", "The stratosphere contains the ozone layer, which absorbs most of the sun's harmful ultraviolet radiation."),
            Question(2, "Who was the first President of Independent India?", listOf("Mahatma Gandhi", "Jawaharlal Nehru", "Dr. Rajendra Prasad", "Sardar Patel"), "Dr. Rajendra Prasad", "Dr. Rajendra Prasad served as the first President of India from 1950 to 1962."),
            Question(3, "The Fundamental Rights in the Indian Constitution are inspired by which country?", listOf("UK", "USA", "USSR", "Canada"), "USA", "Fundamental Rights in India were inspired by the Bill of Rights in the US Constitution."),
            Question(4, "What is the capital of Australia?", listOf("Sydney", "Melbourne", "Canberra", "Perth"), "Canberra", "Canberra is the capital city of Australia, located inland from the south-east coast."),
            Question(5, "Which planet in our solar system is known as the Red Planet?", listOf("Venus", "Jupiter", "Mars", "Saturn"), "Mars", "Mars is often called the Red Planet because of its reddish appearance, due to iron oxide on its surface.")
        )
        questions.clear()
        questions.addAll(staticQs)
        state = QuizState.ACTIVE
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (timerSeconds > 0 && state == QuizState.ACTIVE) {
                delay(1000)
                timerSeconds--
            }
            if (timerSeconds == 0) state = QuizState.RESULT
        }
    }

    fun getScore(): Int {
        var score = 0
        questions.forEach { q ->
            if (answers[q.id] == q.correctAnswer) score++
        }
        return score
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(navController: NavController, viewModel: MockTestViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Test", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (viewModel.state == QuizState.ACTIVE) {
                            // Show confirmation dialog logic here
                        }
                        navController.popBackStack() 
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(BackgroundBody)) {
            when (viewModel.state) {
                QuizState.SETUP -> QuizSetupView(viewModel)
                QuizState.ACTIVE -> QuizActiveView(viewModel)
                QuizState.RESULT -> QuizResultView(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupView(viewModel: MockTestViewModel) {
    val exams = listOf("UPSC CSE", "SSC CGL", "Bank PO", "State PSC", "NDA", "CDS", "Railways")
    val subjectsMap = mapOf(
        "UPSC CSE" to listOf("History", "Geography", "Polity", "Economy", "Current Affairs", "Environment"),
        "SSC CGL" to listOf("Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "Bank PO" to listOf("Quantitative Aptitude", "Reasoning", "English", "Banking Awareness"),
        "State PSC" to listOf("History", "Geography", "Polity", "State Specific"),
        "NDA" to listOf("Mathematics", "General Ability Test", "English"),
        "CDS" to listOf("English", "General Knowledge", "Elementary Mathematics"),
        "Railways" to listOf("Mathematics", "General Intelligence", "General Science", "General Awareness")
    )
    
    var examExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            color = PrimaryBlue.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("AI Mock Test", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text("Generate a personalized test in seconds", fontSize = 14.sp, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ExposedDropdownMenuBox(
            expanded = examExpanded,
            onExpandedChange = { examExpanded = !examExpanded }
        ) {
            OutlinedTextField(
                value = viewModel.selectedExam,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Exam") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline, 
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            ExposedDropdownMenu(
                expanded = examExpanded,
                onDismissRequest = { examExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                exams.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.selectedExam = selectionOption
                            viewModel.selectedSubject = "" // reset subject
                            examExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val availableSubjects = subjectsMap[viewModel.selectedExam] ?: listOf("General Knowledge", "Aptitude", "English", "Reasoning")
        
        ExposedDropdownMenuBox(
            expanded = subjectExpanded,
            onExpandedChange = { if (viewModel.selectedExam.isNotEmpty()) subjectExpanded = !subjectExpanded }
        ) {
            OutlinedTextField(
                value = viewModel.selectedSubject,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Subject") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline, 
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                enabled = viewModel.selectedExam.isNotEmpty()
            )
            ExposedDropdownMenu(
                expanded = subjectExpanded,
                onDismissRequest = { subjectExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                availableSubjects.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.selectedSubject = selectionOption
                            subjectExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.startQuiz("YOUR_API_KEY") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = !viewModel.isLoading && viewModel.selectedExam.isNotEmpty() && viewModel.selectedSubject.isNotEmpty()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Generating Questions...")
            } else {
                Text("Start Mock Test", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            Text("Question ${viewModel.currentIndex + 1}/${viewModel.questions.size}", fontSize = 12.sp, color = TextMuted)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                Text(
                    text = String.format("%02d:%02d", viewModel.timerSeconds / 60, viewModel.timerSeconds % 60),
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
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = q.question, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(24.dp))
                
                q.options.forEach { option ->
                    val isSelected = viewModel.answers[q.id ?: -1] == option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.answers[q.id ?: -1] = option },
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else BackgroundBody,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option, fontSize = 14.sp, color = if (isSelected) PrimaryBlue else TextPrimary)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { if (viewModel.currentIndex > 0) viewModel.currentIndex-- },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Previous")
            }
            Button(
                onClick = { 
                    if (viewModel.currentIndex < viewModel.questions.size - 1) {
                        viewModel.currentIndex++
                    } else {
                        viewModel.state = QuizState.RESULT
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(if (viewModel.currentIndex == viewModel.questions.size - 1) "Finish" else "Next")
            }
        }
    }
}

@Composable
fun QuizResultView(viewModel: MockTestViewModel) {
    val score = viewModel.getScore()
    val percent = (score.toFloat() / viewModel.questions.size * 100).toInt()
    
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Quiz Result", fontSize = 14.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        CircularProgressIndicator(
                            progress = percent / 100f,
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 10.dp,
                            color = if (percent >= 70) AccentGreen else if (percent >= 40) AccentSaffron else AccentRed,
                            trackColor = BorderColor
                        )
                        Text("$percent%", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("You scored $score out of ${viewModel.questions.size}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = if (percent >= 70) "Excellent Job!" else "Keep Practicing!",
                        color = if (percent >= 70) AccentGreen else TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.state = QuizState.SETUP; viewModel.timerSeconds = 600; viewModel.currentIndex = 0; viewModel.answers.clear() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Retake Test")
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Review Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        itemsIndexed(viewModel.questions) { index, q ->
            val userAnswer = viewModel.answers[q.id ?: -1]
            val isCorrect = userAnswer == q.correctAnswer
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCorrect) AccentGreen.copy(alpha = 0.3f) else AccentRed.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Question ${index + 1}", fontSize = 11.sp, color = TextMuted)
                        Icon(
                            if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) AccentGreen else AccentRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = q.question, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Your Answer: ${userAnswer ?: "Skipped"}", fontSize = 13.sp, color = if (isCorrect) AccentGreen else AccentRed)
                    if (!isCorrect) {
                        Text("Correct Answer: ${q.correctAnswer}", fontSize = 13.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                    }
                    
                    if (q.explanation != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(color = BackgroundBody, shape = RoundedCornerShape(8.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(12.dp))
                                    Text("EXPLANATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = q.explanation!!, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

