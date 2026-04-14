package com.sarkari.exam.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.BackgroundLight
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.SurfaceWhite
import com.sarkari.exam.ui.theme.TextPrimary
import com.sarkari.exam.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(navController: NavController) {
    var step by remember { mutableStateOf(1) }
    var selectedExam by remember { mutableStateOf<String?>(null) }
    var selectedLanguage by remember { mutableStateOf("en") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier.padding(top = 40.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { index ->
                val isActive = step > index
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) PrimaryBlue else Color.LightGray
                ) {}
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                1 -> Step1ExamSelection(selectedExam) { selectedExam = it }
                2 -> Step2LanguageSelection(selectedLanguage) { selectedLanguage = it }
                3 -> Step3PrepDetails()
                4 -> Step4Finish {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 1) {
                TextButton(onClick = { step-- }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Text("Back", modifier = Modifier.padding(start = 4.dp))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = { if (step < 4) step++ },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = step < 4 && (step != 1 || selectedExam != null)
            ) {
                Text(if (step == 4) "Get Started" else "Next")
                if (step < 4) Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun Step1ExamSelection(selected: String?, onSelect: (String) -> Unit) {
    Column {
        Text("Choose your focus", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Which government exam are you preparing for?", fontSize = 16.sp, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val exams = listOf("UPSC", "SSC CGL", "Banking", "Railways", "State PSC")
        exams.forEach { exam ->
            val isSelected = selected == exam
            Surface(
                onClick = { onSelect(exam) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else SurfaceWhite,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue) else null
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(exam, modifier = Modifier.weight(1f), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue)
                }
            }
        }
    }
}

@Composable
fun Step2LanguageSelection(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text("Language Preference", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Select the language for your study materials.", fontSize = 16.sp, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val langs = listOf("English" to "en", "Hindi" to "hi", "Marathi" to "mr")
        langs.forEach { (name, code) ->
            val isSelected = selected == code
            Surface(
                onClick = { onSelect(code) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else SurfaceWhite
            ) {
                Text(name, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun Step3PrepDetails() {
    Column {
        Text("Let's Get Specific", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("How many hours can you dedicate daily?", fontSize = 16.sp, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Slider(value = 4f, onValueChange = {}, valueRange = 1f..12f, steps = 11)
        Text("4 hours per day", fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun Step4Finish(onFinish: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(80.dp), tint = PrimaryBlue)
        Spacer(modifier = Modifier.height(24.dp))
        Text("You're all set!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Your personalized dashboard is ready.", color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
        
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text("Go to Dashboard")
        }
    }
}


