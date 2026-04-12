package com.sarkari.exam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MockTestScreen(
    onNavigateBack: () -> Unit,
    viewModel: MockTestViewModel = viewModel()
) {
    val isStarted by viewModel.isStarted
    val isLoading by viewModel.isLoading
    val questions by viewModel.questions
    val currentIndex by viewModel.currentQuestionIndex
    val error by viewModel.error

    if (!isStarted) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "AI Mock Test", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp))
            
            error?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
            }

            Button(
                onClick = { viewModel.startQuiz("SSC CGL", "General") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Generating via OpenRouter AI..." else "Start SSC CGL Mock Test")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Go Back")
            }
        }
    } else {
        val q = questions[currentIndex]
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(text = "Question ${currentIndex + 1} / ${questions.size}", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
            Text(text = q.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 24.dp))
            
            q.options.forEach { opt ->
                OutlinedButton(
                    onClick = { /* Check Answer */ },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(opt, modifier = Modifier.padding(8.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finish Test")
            }
        }
    }
}
