package com.sarkari.exam.ui.screens.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

data class SubjectStat(val name: String, val score: Int, val color: Color)

class AnalyticsViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var accuracy by mutableStateOf(78)
    var consistency by mutableStateOf(92)
    var completion by mutableStateOf(45)
    
    var subjects = listOf(
        SubjectStat("Indian Polity", 85, PrimaryBlue),
        SubjectStat("History & Culture", 62, AccentSaffron),
        SubjectStat("Geography", 74, AccentGreen),
        SubjectStat("Economy", 48, AccentRed),
        SubjectStat("Current Affairs", 91, Color(0xFF8B5CF6))
    )

    var aiReport by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun fetchAiAnalysis(apiKey: String) {
        isLoading = true
        androidx.lifecycle.viewModelScope.launch {
            val prompt = "Analyze my exam performance. Accuracy: $accuracy%, Consistency: $consistency%. Strongest: Current Affairs. Weakest: Economy. Provide tips."
            val response = repository.getAiResponse(listOf(ChatMessage("user", prompt)), apiKey)
            isLoading = false
            aiReport = response
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBody)
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = PrimaryBlue, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
                    }
                    Column {
                        Text("Exam Statistics", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Detailed breakdown of your progress", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Gauges Row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GaugeCard(viewModel.accuracy, "Accuracy", PrimaryBlue, Modifier.weight(1f))
                    GaugeCard(viewModel.consistency, "Consistency", AccentSaffron, Modifier.weight(1f))
                    GaugeCard(viewModel.completion, "Completion", AccentGreen, Modifier.weight(1f))
                }
            }

            // Subject Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                            Text("Subject Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        viewModel.subjects.forEach { subject ->
                            SubjectProgressBar(subject)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // AI Report Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                            }
                            Text("AI Deep Dive Analysis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Get personalized insights and improvement tips based on your mock test patterns.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.fetchAiAnalysis("gsk_iLUpuE3ZfMSuA3U8pC1aWGdyb3FYpUvYQYf3x64T8C1Cq8N5C1C") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            enabled = !viewModel.isLoading
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Analyze My Performance", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (viewModel.aiReport != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = viewModel.aiReport!!,
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
}

@Composable
fun GaugeCard(value: Int, label: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                val sweepAngle = (value / 100f) * 360f
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFFF1F5F9),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text("$value%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SubjectProgressBar(subject: SubjectStat) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(subject.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("${subject.score}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = subject.color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = subject.score / 100f,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = subject.color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}
