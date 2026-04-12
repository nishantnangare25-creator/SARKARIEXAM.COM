package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.domain.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(
    onNavigateBack: () -> Unit,
    viewModel: StudyPlannerViewModel = viewModel()
) {
    val exam by viewModel.exam
    val hours by viewModel.hours
    val level by viewModel.level
    val plan by viewModel.plan
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Study Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Generate your AI Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Customize your constraints down below.", color = Color.Gray, fontSize = 14.sp)
            }

            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        
                        // Exam Selection Simple Alternative
                        Column {
                            Text("Select Exam", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Constants.EXAMS.take(2).forEach { e ->
                                    val isSelected = exam == e.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color.Transparent),
                                        modifier = Modifier.weight(1f).clickable { viewModel.setExam(e.id) }
                                    ) {
                                        Text(e.name, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(8.dp), color = if (isSelected) Color(0xFF1D4ED8) else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        // Hours Selection
                        Column {
                            Text("Hours per Day: $hours", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Slider(
                                value = hours.toFloat(),
                                onValueChange = { viewModel.setHours(it.toInt()) },
                                valueRange = 1f..12f,
                                steps = 10,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF2563EB))
                            )
                        }

                        // Prep Level
                        Column {
                            Text("Prep Level", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Constants.PREP_LEVELS.forEach { lvl ->
                                    val isSelected = level == lvl
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                        modifier = Modifier.weight(1f).clickable { viewModel.setLevel(lvl) }
                                    ) {
                                        Text(lvl.replaceFirstChar { it.uppercase() }, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(8.dp), fontSize = 12.sp, color = if (isSelected) Color(0xFF1D4ED8) else Color.Gray)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.generatePlan() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing...")
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Study Plan")
                            }
                        }

                        error?.let {
                            Text(text = it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }

            plan?.let { markdownPlan ->
                item {
                    Text("Your Generated Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SelectionContainer {
                            Text(
                                text = markdownPlan,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}
