package com.sarkari.exam.ui.screens.pyq

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

enum class PYQTestState { SETUP, ACTIVE, RESULT }

class PYQTestViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var state by mutableStateOf(PYQTestState.SETUP)
    var questions = mutableStateListOf<Question>()
    var currentIndex by mutableStateOf(0)
    var answers = mutableStateMapOf<Int, String>()
    var isLoading by mutableStateOf(false)
    var timerSeconds by mutableStateOf(600) // 10 minutes strictly
    var errorMsg by mutableStateOf<String?>(null)
    
    var selectedExam by mutableStateOf("")
    var selectedSubject by mutableStateOf("")

    fun startQuiz(apiKey: String = "YOUR_API_KEY") {
        isLoading = true
        errorMsg = null
        viewModelScope.launch {
            val topic = if (selectedSubject.isNotEmpty()) "$selectedExam - $selectedSubject" else selectedExam
            val prompt = """
                You are an expert competitive exam creator. Generate exactly 5 REAL previous year practice MCQ questions for $topic.
                These must reflect actual historical patterns.
                CRITICAL RULES:
                1. DO NOT USE JSON. Respond STRICTLY in plain text format.
                2. Format EACH question exactly like this:
                Q: [Question text]
                A) [Option 1]
                B) [Option 2]
                C) [Option 3]
                D) [Option 4]
                Answer: [A, B, C, or D]
                Explanation: [Reasoning]
            """.trimIndent()
            
            val response = repository.getAiResponse(listOf(com.sarkari.exam.data.models.ChatMessage("user", prompt)), apiKey)
            
            if (response != null) {
                val parsed = repository.parseQuestions(response)
                if (parsed.isNotEmpty()) {
                    questions.clear()
                    questions.addAll(parsed)
                    state = PYQTestState.ACTIVE
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
            Question(1, "The Constitution of India was adopted by the Constituent Assembly on:", listOf("26 January 1950", "26 November 1949", "15 August 1947", "30 January 1948"), "26 November 1949", "The Constituent Assembly adopted the Constitution on 26 Nov 1949, though it came into effect on 26 Jan 1950."),
            Question(2, "Which among the following is the oldest mountain range in India?", listOf("Himalayas", "Aravallis", "Vindhyas", "Satpura"), "Aravallis", "The Aravalli Range is the oldest block of mountains in India, running across Rajasthan to Haryana."),
            Question(3, "The term 'Microeconomics' and 'Macroeconomics' were coined by:", listOf("Adam Smith", "John Maynard Keynes", "Ragnar Frisch", "Alfred Marshall"), "Ragnar Frisch", "Ragnar Frisch coined these terms in 1933 to distinguish between individual and aggregate economic behavior."),
            Question(4, "Which fundamental right cannot be suspended even during an emergency under Article 352?", listOf("Right to Equality", "Right to Freedom of Speech", "Right to Life and Personal Liberty", "Right to Constitutional Remedies"), "Right to Life and Personal Liberty", "Articles 20 and 21 (Protection in respect of conviction for offences and Right to life and personal liberty) cannot be suspended."),
            Question(5, "The Battle of Plassey was fought in the year:", listOf("1757", "1764", "1857", "1858"), "1757", "The Battle of Plassey was a decisive victory of the British East India Company over the Nawab of Bengal in 1757.")
        )
        questions.clear()
        questions.addAll(staticQs)
        state = PYQTestState.ACTIVE
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (timerSeconds > 0 && state == PYQTestState.ACTIVE) {
                delay(1000)
                timerSeconds--
            }
            if (timerSeconds == 0) state = PYQTestState.RESULT
        }
    }

    fun getScore(): Int = questions.count { q -> answers[q.id] == q.correctAnswer }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PYQTestScreen(navController: NavController, viewModel: PYQTestViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PYQ Test", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (viewModel.state) {
                PYQTestState.SETUP -> PYQSetupView(viewModel)
                PYQTestState.ACTIVE -> PYQActiveView(viewModel)
                PYQTestState.RESULT -> PYQResultView(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PYQSetupView(viewModel: PYQTestViewModel) {
    val exams = listOf("UPSC Civil Services", "MPSC", "SSC CGL/CHSL", "Banking", "Railway", "NDA", "State PSC")
    val subjectsMap = mapOf(
        "UPSC Civil Services" to listOf("History", "Geography", "Polity", "Economy", "Ethics", "Essay"),
        "MPSC" to listOf("History", "Geography", "Polity", "Economy", "Science", "Marathi Language"),
        "SSC CGL/CHSL" to listOf("Quantitative Aptitude", "English", "General Intelligence", "General Awareness")
    )
    
    var examExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header identical to web
            Surface(
                color = AccentSaffron.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "PREMIUM PRACTICE",
                    color = AccentSaffron,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("PYQs Mock Test", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
            Text("Master your exam with a focused 10-minute challenge using real past year questions.", color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Configure Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Configure Your Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Text("Select Your Exam", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = examExpanded,
                        onExpandedChange = { examExpanded = !examExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.selectedExam,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Choose an exam...") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = examExpanded,
                            onDismissRequest = { examExpanded = false }
                        ) {
                            exams.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.selectedExam = selectionOption
                                        viewModel.selectedSubject = ""
                                        examExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Target Subject (Optional)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                    val availableSubjects = subjectsMap[viewModel.selectedExam] ?: listOf("All Subjects")
                    ExposedDropdownMenuBox(
                        expanded = subjectExpanded,
                        onExpandedChange = { subjectExpanded = !subjectExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (viewModel.selectedSubject.isEmpty() && viewModel.selectedExam.isNotEmpty()) "All Subjects" else viewModel.selectedSubject,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("All Subjects") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = viewModel.selectedExam.isNotEmpty()
                        )
                        ExposedDropdownMenu(
                            expanded = subjectExpanded,
                            onDismissRequest = { subjectExpanded = false }
                        ) {
                            availableSubjects.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.selectedSubject = if (selectionOption == "All Subjects") "" else selectionOption
                                        subjectExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.startQuiz() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !viewModel.isLoading && viewModel.selectedExam.isNotEmpty()
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Generating...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start 10-Min Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
            
            // How it works Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("How it works", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.padding(bottom = 16.dp))
                    
                    FeatureItem(Icons.Default.Timer, "10-Minute Sprint", "Short, high-intensity focus sessions to build mental stamina.", PrimaryBlue)
                    FeatureItem(Icons.Default.LibraryBooks, "Real PYQ Patterns", "Questions modeled after historic exam data and trending topics.", AccentSaffron)
                    FeatureItem(Icons.Default.AutoAwesome, "AI Explanations", "Get instant, high-quality reasoning for every correct and incorrect answer.", AccentGreen)
                }
            }
        }
    }
}

@Composable
fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp).size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(desc, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun PYQActiveView(viewModel: PYQTestViewModel) {
    val q = viewModel.questions.getOrNull(viewModel.currentIndex) ?: return
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Headers (Timer, Progress)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Surface(color = if (viewModel.timerSeconds < 60) AccentRed else AccentOrange, shape = RoundedCornerShape(16.dp)) {
               Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                   Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                   Spacer(modifier = Modifier.width(6.dp))
                   Text(
                       text = String.format("%02d:%02d", viewModel.timerSeconds / 60, viewModel.timerSeconds % 60),
                       fontWeight = FontWeight.Bold,
                       color = Color.White
                   )
               }
            }
            OutlinedButton(
                onClick = { viewModel.state = PYQTestState.RESULT },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Finish Early", fontSize = 13.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LinearProgressIndicator(
            progress = (viewModel.currentIndex + 1).toFloat() / viewModel.questions.size,
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = PrimaryBlue,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("QUESTION ${viewModel.currentIndex + 1} OF ${viewModel.questions.size}", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = q.question, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(32.dp))
                
                q.options.forEachIndexed { i, option ->
                    val isSelected = viewModel.answers[q.id ?: -1] == option
                    Button(
                        onClick = { viewModel.answers[q.id ?: -1] = option },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.background,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = ('A' + i).toString(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = option, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = { if (viewModel.currentIndex > 0) viewModel.currentIndex-- },
                enabled = viewModel.currentIndex > 0
            ) {
                Text("Previous")
            }
            if (viewModel.currentIndex < viewModel.questions.size - 1) {
                Button(
                    onClick = { viewModel.currentIndex++ },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next Question")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = { viewModel.state = PYQTestState.RESULT },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("See Results")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun PYQResultView(viewModel: PYQTestViewModel) {
    val score = viewModel.getScore()
    val total = viewModel.questions.size
    val percent = (score.toFloat() / total * 100).toInt()
    
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("Test Completed", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Mock Test Results", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
            
            // Score Summary Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                        CircularProgressIndicator(
                            progress = percent / 100f,
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 6.dp,
                            color = PrimaryBlue,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text("$percent%", fontWeight = FontWeight.Black, fontSize = 20.sp, color = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Column {
                        Text("You Scored $score/$total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You attempted $total questions. Your accuracy is $percent% which is ${if (percent >= 70) "Excellent!" else if (percent >= 40) "Good progress!" else "Keep practicing!"}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Retry trigger
            Button(
                onClick = { viewModel.state = PYQTestState.SETUP; viewModel.timerSeconds = 600; viewModel.currentIndex = 0; viewModel.answers.clear() },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Another PYQ Session", fontWeight = FontWeight.Bold)
            }
            
            Text("Question Review", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        itemsIndexed(viewModel.questions) { index, q ->
            val userAnswer = viewModel.answers[q.id ?: -1]
            val isCorrect = userAnswer == q.correctAnswer
            
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        // Left colored stripe equivalent (handled in card border realistically but standard UI prefers an icon or badge here)
                        Surface(color = if (isCorrect) AccentGreen else AccentRed, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                            Text(if (isCorrect) "Correct" else "Incorrect", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Question ${index + 1}", fontSize = 12.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(q.question, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 22.sp)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            q.options.forEach { opt ->
                                val isSelectedOpt = userAnswer == opt
                                val isActualAnswer = q.correctAnswer == opt
                                val bgColor = if (isActualAnswer) AccentGreen.copy(alpha = 0.1f) else if (isSelectedOpt) AccentRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.background
                                val textColor = if (isActualAnswer) AccentGreen else if (isSelectedOpt) AccentRed else TextSecondary
                                
                                Surface(color = bgColor, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(opt, fontWeight = if (isActualAnswer || isSelectedOpt) FontWeight.Bold else FontWeight.Normal, color = textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                        if (isActualAnswer) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                        else if (isSelectedOpt) Icon(Icons.Default.Cancel, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            
                            if (q.explanation != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    color = PrimaryBlue.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("AI EXPLANATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = q.explanation!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

