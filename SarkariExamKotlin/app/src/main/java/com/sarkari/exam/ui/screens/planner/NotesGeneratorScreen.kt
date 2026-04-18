package com.sarkari.exam.ui.screens.planner

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
import com.sarkari.exam.data.models.ChatMessage
import com.sarkari.exam.data.repository.AiRepository
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.launch

class NotesGeneratorViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var selectedExam by mutableStateOf("")
    var selectedSubject by mutableStateOf("")
    var customTopics by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var generatedNotes by mutableStateOf<String?>(null)

    fun generateNotes(apiKey: String = "YOUR_API_KEY") {
        isLoading = true
        viewModelScope.launch {
            val topicHint = if(customTopics.isNotEmpty()) "specifically covering: $customTopics" else ""
            val prompt = "Generate comprehensive, structured, and highly readable exam study notes for $selectedExam - $selectedSubject $topicHint. Use highly readable text (bulleted lists, subheadings)."
            val response = repository.getAiResponse(listOf(ChatMessage("user", prompt)), apiKey)
            isLoading = false
            generatedNotes = response ?: "Failed to generate notes. Please try again."
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGeneratorScreen(navController: NavController, viewModel: NotesGeneratorViewModel = viewModel()) {
    
    val exams = listOf("UPSC Civil Services", "MPSC", "SSC CGL/CHSL", "Banking", "Railway", "NDA", "State PSC")
    val subjectsMap = mapOf(
        "UPSC Civil Services" to listOf("History", "Geography", "Polity", "Economy", "Science"),
        "SSC CGL/CHSL" to listOf("Quantitative Aptitude", "English", "General Intelligence", "General Awareness")
    )
    
    var examExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes Generator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(10.dp).size(28.dp))
                }
                Column {
                    Text("Instant Cheat Sheets", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Deep dive AI-generated study notes", fontSize = 13.sp, color = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Exam Dropdown
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Exam", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                            ExposedDropdownMenuBox(expanded = examExpanded, onExpandedChange = { examExpanded = !examExpanded }) {
                                OutlinedTextField(
                                    value = viewModel.selectedExam,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Select") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                ExposedDropdownMenu(expanded = examExpanded, onDismissRequest = { examExpanded = false }) {
                                    exams.forEach { e ->
                                        DropdownMenuItem(text = { Text(e) }, onClick = { viewModel.selectedExam = e; viewModel.selectedSubject = ""; examExpanded = false })
                                    }
                                }
                            }
                        }
                        
                        // Subject Dropdown
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Subject", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                            val availableSubjects = subjectsMap[viewModel.selectedExam] ?: listOf("General Knowledge", "Reasoning", "Maths", "English")
                            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = !subjectExpanded }) {
                                OutlinedTextField(
                                    value = viewModel.selectedSubject,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Select") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = viewModel.selectedExam.isNotEmpty()
                                )
                                ExposedDropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                                    availableSubjects.forEach { s ->
                                        DropdownMenuItem(text = { Text(s) }, onClick = { viewModel.selectedSubject = s; subjectExpanded = false })
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Specific Topics (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = viewModel.customTopics,
                        onValueChange = { viewModel.customTopics = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Fundamental Rights, Optics") },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.generateNotes() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !viewModel.isLoading && viewModel.selectedExam.isNotEmpty() && viewModel.selectedSubject.isNotEmpty()
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Writing Notes...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Notes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            if (viewModel.generatedNotes != null && !viewModel.isLoading) {
                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Your Notes", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Row {
                        IconButton(onClick = {  }) { Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = AccentRed) }
                        IconButton(onClick = {  }) { Icon(Icons.Default.Download, contentDescription = "MD/Text", tint = PrimaryBlue) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = viewModel.generatedNotes!!,
                        modifier = Modifier.padding(24.dp),
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

