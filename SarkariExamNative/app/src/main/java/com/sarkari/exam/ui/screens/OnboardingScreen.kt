package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.domain.Constants

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val step by viewModel.step

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Text("Personalize Your Journey", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            
            // Stepper Dots
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalArrangement = Arrangement.Center) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (step >= i) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                            .border(1.dp, if (step >= i) Color(0xFF1D4ED8) else Color(0xFFCBD5E1), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step > i) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text(i.toString(), color = if (step >= i) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (i < 4) Spacer(modifier = Modifier.width(12.dp))
                }
            }

            // Content Area - Responsive Scroll
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (step) {
                    1 -> Step1ExamSelect(viewModel)
                    2 -> Step2LanguageSelect(viewModel)
                    3 -> Step3HoursLevelSelect(viewModel)
                    4 -> Step4SubjectSelect(viewModel)
                }
            }

            // Navigation Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                if (step > 1) {
                    OutlinedButton(onClick = viewModel::previousStep, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                Button(
                    onClick = { if (step < 4) viewModel.nextStep() else onFinish() }, 
                    modifier = Modifier.weight(1f),
                    enabled = !(step == 1 && viewModel.exam.value.isEmpty())
                ) {
                    Text(if (step < 4) "Next" else "Finish Setup")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(if (step < 4) Icons.Default.ArrowForward else Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun Step1ExamSelect(viewModel: OnboardingViewModel) {
    val selected = viewModel.exam.value
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Which exam are you targeting?", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp)) }
        items(Constants.EXAMS) { exam ->
            val isSelected = selected == exam.id
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth().clickable { viewModel.setExam(exam.id) }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(exam.icon, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                    Text(exam.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF334155))
                }
            }
        }
    }
}

@Composable
fun Step2LanguageSelect(viewModel: OnboardingViewModel) {
    val selected = viewModel.language.value
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Select your preferred language", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp)) }
        items(Constants.LANGUAGES) { lang ->
            val isSelected = selected == lang.code
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth().clickable { viewModel.setLanguage(lang.code) }
            ) {
                Text("${lang.nativeName} (${lang.name})", modifier = Modifier.padding(16.dp), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF334155))
            }
        }
    }
}

@Composable
fun Step3HoursLevelSelect(viewModel: OnboardingViewModel) {
    Column {
        Text("Daily Study Hours", fontWeight = FontWeight.Bold)
        Text("${viewModel.hours.value} hrs/day", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(vertical = 12.dp))
        Slider(
            value = viewModel.hours.value.toFloat(),
            onValueChange = { viewModel.setHours(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF2563EB))
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Current Prep Level", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Constants.PREP_LEVELS.forEach { lvl ->
                val isSelected = viewModel.level.value == lvl
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                    modifier = Modifier.clickable { viewModel.setLevel(lvl) }.weight(1f)
                ) {
                    Text(lvl.replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(symmetric = 8.dp, vertical = 12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color(0xFF1D4ED8) else Color.Gray)
                }
            }
        }
    }
}

@Composable
fun Step4SubjectSelect(viewModel: OnboardingViewModel) {
    val subjects = viewModel.getSubjectsForExam()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { 
            Text("Assess your subjects", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
            Text("Help us personalize your study plan by marking subjects.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
        }
        items(subjects) { sub ->
            val isWeak = viewModel.weakSubjects.value.contains(sub)
            val isStrong = viewModel.strongSubjects.value.contains(sub)
            
            Surface(shape = RoundedCornerShape(8.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(sub, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isWeak,
                            onClick = { viewModel.toggleSubject(sub, true) },
                            label = { Text("Weak", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE4E6), selectedLabelColor = Color(0xFFE11D48))
                        )
                        FilterChip(
                            selected = isStrong,
                            onClick = { viewModel.toggleSubject(sub, false) },
                            label = { Text("Strong", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD1FAE5), selectedLabelColor = Color(0xFF059669))
                        )
                    }
                }
            }
        }
    }
}
