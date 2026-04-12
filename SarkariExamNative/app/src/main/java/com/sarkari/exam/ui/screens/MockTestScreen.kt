package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MockTestScreen(
    onNavigateBack: () -> Unit,
    viewModel: MockTestViewModel = viewModel()
) {
    val isStarted by viewModel.isStarted
    val isFinished by viewModel.isFinished
    val isLoading by viewModel.isLoading
    val questions by viewModel.questions
    val currentIndex by viewModel.currentQuestionIndex
    val error by viewModel.error
    val selectedAnswer by viewModel.selectedAnswer
    val showExplanation by viewModel.showExplanation
    val score by viewModel.score

    if (!isStarted) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "AI Mock Test Engine", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Text(text = "Generates live questions using OpenRouter Gemini 2.5", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp), textAlign = TextAlign.Center)
            
            error?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
            }

            Button(
                onClick = { viewModel.startQuiz("UPSC", "Indian Polity") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Start UPSC Polity Test")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Go Back")
            }
        }
    } else if (isFinished) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Test Completed!", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            
            Surface(shape = RoundedCornerShape(120.dp), color = Color(0xFFEFF6FF), modifier = Modifier.size(120.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "$score / ${questions.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.resetQuiz() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Take Another Test")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text("Return to Dashboard")
            }
        }
    } else {
        if (questions.isEmpty()) return
        val q = questions[currentIndex]
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(24.dp)
        ) {
            LinearProgressIndicator(
                progress = (currentIndex + 1).toFloat() / questions.size.toFloat(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                color = Color(0xFF2563EB)
            )
            
            Text(text = "Question ${currentIndex + 1} of ${questions.size}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = q.question, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 24.dp), lineHeight = 28.sp)
            
            q.options.forEach { opt ->
                val isSelected = selectedAnswer == opt
                val isCorrect = opt == q.correctAnswer
                val btnColor = if (showExplanation) {
                    if (isCorrect) Color(0xFFD1FAE5) else if (isSelected) Color(0xFFFEE2E2) else Color.White
                } else {
                    if (isSelected) Color(0xFFDBEAFE) else Color.White
                }
                
                Surface(
                    onClick = { viewModel.submitAnswer(opt) },
                    shape = RoundedCornerShape(12.dp),
                    color = btnColor,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(opt, modifier = Modifier.padding(16.dp), color = Color(0xFF334155), fontSize = 16.sp)
                }
            }
            
            if (showExplanation) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F5F9), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Explanation:", fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.padding(bottom = 8.dp))
                        Text(q.explanation, color = Color(0xFF334155), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { viewModel.nextQuestion() },
                enabled = showExplanation,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(if (currentIndex == questions.size - 1) "Finish Test" else "Next Question")
            }
        }
    }
}
