package com.sarkari.exam.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

class PastPaperAnalyzerViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var selectedExam by mutableStateOf("")
    var pdfTextContent by mutableStateOf("")
    var fileName by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var analysisResult by mutableStateOf<String?>(null)

    fun startAnalysis(apiKey: String = "YOUR_API_KEY") {
        isLoading = true
        viewModelScope.launch {
            val prompt = "Analyze this past paper text for $selectedExam. Extract the weightage of different topics, identify the pattern of questions, predict important areas for the next exam, and give a difficulty rating. Text: $pdfTextContent"
            val response = repository.getAiResponse(listOf(ChatMessage("user", prompt)), apiKey)
            isLoading = false
            analysisResult = response ?: "Analysis failed. Ensure text is valid."
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastPaperAnalyzerScreen(navController: NavController, viewModel: PastPaperAnalyzerViewModel = viewModel()) {
    
    val exams = listOf("UPSC Civil Services", "MPSC", "SSC CGL/CHSL", "Banking", "Railway", "NDA", "State PSC")
    var examExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Paper Analyzer", fontWeight = FontWeight.Bold) },
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
                Surface(color = AccentSaffron.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = AccentSaffron, modifier = Modifier.padding(10.dp).size(28.dp))
                }
                Column {
                    Text("Exam Trend Analysis", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Discover what truly matters.", fontSize = 13.sp, color = TextSecondary)
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
                    
                    Text("Select Exam", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    ExposedDropdownMenuBox(expanded = examExpanded, onExpandedChange = { examExpanded = !examExpanded }) {
                        OutlinedTextField(
                            value = viewModel.selectedExam,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Choose an exam") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = examExpanded, onDismissRequest = { examExpanded = false }) {
                            exams.forEach { e ->
                                DropdownMenuItem(text = { Text(e) }, onClick = { viewModel.selectedExam = e; examExpanded = false })
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Dropzone simulator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                            .clickable { viewModel.fileName = "mock_paper_2023.txt"; viewModel.pdfTextContent = "Q1. Who was the first PM? Q2. What is GDP? Q3. Define Fundamental Rights." }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap to simulate file upload", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (viewModel.fileName.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("📄 ${viewModel.fileName}", fontSize = 12.sp, color = AccentGreen)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Or paste paper text manually", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = viewModel.pdfTextContent,
                        onValueChange = { viewModel.pdfTextContent = it; viewModel.fileName = "" },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Paste text here...") },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.startAnalysis() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !viewModel.isLoading && viewModel.pdfTextContent.isNotEmpty()
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Analyzing Structure...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze Patterns", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            if (viewModel.analysisResult != null && !viewModel.isLoading) {
                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Analysis Breakdown", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    IconButton(onClick = {  }) { Icon(Icons.Default.Download, contentDescription = "Download Report", tint = PrimaryBlue) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = viewModel.analysisResult!!,
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

