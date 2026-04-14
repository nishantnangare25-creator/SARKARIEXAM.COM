package com.sarkari.exam.ui.screens.mocktest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.domain.models.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(navController: NavController) {
    val dummyQuestion = Question(
        id = 1,
        question = "Which layer of the atmosphere contains the ozone layer?",
        options = listOf("Troposphere", "Stratosphere", "Mesosphere", "Exosphere"),
        correctAnswer = "Stratosphere",
        explanation = "The stratosphere contains the ozone layer, which absorbs most of the sun's harmful ultraviolet radiation."
    )

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Mock Test engine", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = com.sarkari.exam.ui.theme.PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(com.sarkari.exam.ui.theme.BackgroundLight)
                .padding(16.dp)
        ) {
            Text(text = "Question 1 of 10", color = com.sarkari.exam.ui.theme.TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dummyQuestion.question, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = com.sarkari.exam.ui.theme.TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            dummyQuestion.options.forEach { option ->
                val isSelected = selectedOption == option
                val backgroundColor = when {
                    !isSubmitted && isSelected -> com.sarkari.exam.ui.theme.PrimaryBlue.copy(alpha = 0.2f)
                    isSubmitted && option == dummyQuestion.correctAnswer -> com.sarkari.exam.ui.theme.GreenSuccess.copy(alpha = 0.2f)
                    isSubmitted && isSelected && option != dummyQuestion.correctAnswer -> com.sarkari.exam.ui.theme.RedError.copy(alpha = 0.2f)
                    else -> Color.White
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    onClick = { if (!isSubmitted) selectedOption = option }
                ) {
                    Text(text = option, modifier = Modifier.padding(16.dp), color = com.sarkari.exam.ui.theme.TextPrimary)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { isSubmitted = true },
                enabled = selectedOption != null && !isSubmitted,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = com.sarkari.exam.ui.theme.PrimaryBlue)
            ) {
                Text("Submit Answer")
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = com.sarkari.exam.ui.theme.GreenSuccess.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Explanation:", fontWeight = FontWeight.Bold, color = com.sarkari.exam.ui.theme.GreenSuccess)
                        Text(dummyQuestion.explanation, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
