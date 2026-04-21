package sarkari.exam_ai.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import sarkari.exam_ai.ui.components.ButtonVariant
import sarkari.exam_ai.ui.components.SarkariButton
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.navigation.Screen
import sarkari.exam_ai.ui.theme.BackgroundBody
import sarkari.exam_ai.ui.theme.PrimaryBlue
import sarkari.exam_ai.ui.theme.SurfaceWhite
import sarkari.exam_ai.ui.theme.TextPrimary
import sarkari.exam_ai.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(navController: NavController) {
    var step by remember { mutableStateOf(1) }
    var selectedExam by remember { mutableStateOf<String?>(null) }
    var selectedLanguage by remember { mutableStateOf("en") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBody)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Indicator
        Row(
            modifier = Modifier.padding(top = 40.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(4) { index ->
                val isActive = step > index
                Surface(
                    modifier = Modifier.height(6.dp).weight(1f),
                    shape = RoundedCornerShape(100.dp),
                    color = if (isActive) PrimaryBlue else Color.LightGray.copy(alpha = 0.3f)
                ) {}
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                1 -> Step1ExamSelection(selectedExam) { selectedExam = it }
                2 -> Step2LanguageSelection(selectedLanguage) { selectedLanguage = it }
                3 -> Step3PrepDetails()
                4 -> Step4Finish()
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                SarkariButton(
                    text = "Back",
                    onClick = { step-- },
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Secondary
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            SarkariButton(
                text = if (step == 4) "Get Started" else "Continue",
                onClick = { 
                    if (step < 4) step++ 
                    else {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Primary,
                enabled = step == 4 || (step < 4 && (step != 1 || selectedExam != null))
            )
        }
    }
}

@Composable
fun Step1ExamSelection(selected: String?, onSelect: (String) -> Unit) {
    Column {
        Text("Choose your focus", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text("Which government exam are you preparing for?", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val exams = listOf("UPSC CSE", "SSC CGL", "Banking (IBPS/SBI)", "Railways (RRB)", "State PSC")
        exams.forEach { exam ->
            val isSelected = selected == exam
            SarkariCard(
                onClick = { onSelect(exam) },
                modifier = Modifier.padding(vertical = 6.dp),
                backgroundColor = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.White,
                padding = 16.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exam, 
                        modifier = Modifier.weight(1f), 
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryBlue else TextPrimary
                    )
                    if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue)
                }
            }
        }
    }
}

@Composable
fun Step2LanguageSelection(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text("Language Selection", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text("Select the language for your AI study assistant.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val langs = listOf("English" to "en", "Hindi" to "hi", "Marathi" to "mr")
        langs.forEach { (name, code) ->
            val isSelected = selected == code
            SarkariCard(
                onClick = { onSelect(code) },
                modifier = Modifier.padding(vertical = 6.dp),
                backgroundColor = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name, 
                        modifier = Modifier.weight(1f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryBlue else TextPrimary
                    )
                    if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue)
                }
            }
        }
    }
}

@Composable
fun Step3PrepDetails() {
    var hours by remember { mutableStateOf(4f) }
    Column {
        Text("Dedication Level", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text("How many hours can you dedicate daily?", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        SarkariCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${hours.toInt()} hours per day", 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black, 
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = hours, 
                    onValueChange = { hours = it }, 
                    valueRange = 1f..12f, 
                    steps = 11,
                    colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                )
            }
        }
    }
}

@Composable
fun Step4Finish() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.Center, 
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            color = PrimaryBlue.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(60.dp), tint = PrimaryBlue)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("You're all set!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text(
            text = "Your personalized AI dashboard is ready to boost your preparation.", 
            color = TextSecondary, 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}


