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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sarkari.exam.data.models.ChatMessage
import com.sarkari.exam.data.repository.AiRepository
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.launch

class StudyPlannerViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var selectedExam by mutableStateOf("UPSC")
    var selectedHours by mutableStateOf("4 hrs")
    var selectedLevel by mutableStateOf("Beginner")
    var isLoading by mutableStateOf(false)
    var plan by mutableStateOf<String?>(null)

    fun generatePlan(apiKey: String) {
        isLoading = true
        androidx.lifecycle.viewModelScope.launch {
            val prompt = "Create a detailed study plan for $selectedExam exam preparation. Study hours: $selectedHours. Level: $selectedLevel. Provide a weekly breakdown."
            val response = repository.getAiResponse(listOf(ChatMessage("user", prompt)), apiKey)
            isLoading = false
            plan = response
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(navController: NavController, viewModel: StudyPlannerViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Study Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (viewModel.plan != null) {
                        IconButton(onClick = { /* PDF Download */ }) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                        }
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(10.dp))
                }
                Column {
                    Text("Personalized Roadmap", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text("AI-generated schedule just for you", fontSize = 12.sp, color = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Target Exam", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.selectedExam,
                        onValueChange = { viewModel.selectedExam = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedBorderColor = BorderColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Hours per Day", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.selectedHours,
                        onValueChange = { viewModel.selectedHours = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedBorderColor = BorderColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Current Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.selectedLevel,
                        onValueChange = { viewModel.selectedLevel = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedBorderColor = BorderColor)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.generatePlan("gsk_iLUpuE3ZfMSuA3U8pC1aWGdyb3FYpUvYQYf3x64T8C1Cq8N5C1C") }, // Demo key
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Master Plan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (viewModel.plan != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Sparkles, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
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
}
