package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    GaugeCard("Accuracy", viewModel.accuracy.value, Color(0xFF2563EB), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    GaugeCard("Consistency", viewModel.consistency.value, Color(0xFFF97316), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    GaugeCard("Completion", viewModel.completion.value, Color(0xFF10B981), Modifier.weight(1f))
                }
            }

            item {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Subject Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
                        viewModel.subjects.forEach { subject ->
                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(subject.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("${subject.score}%", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = subject.score / 100f,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Color(subject.color),
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("AI Deep Dive Analysis", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Get predictive insights tailored to your weaknesses.", color = Color.Gray, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
                        
                        Button(
                            onClick = { viewModel.fetchDeepDiveAnalysis() },
                            enabled = !viewModel.isAnalyzing.value,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            if (viewModel.isAnalyzing.value) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Generate Full Report")
                            }
                        }

                        viewModel.error.value?.let {
                            Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }

            viewModel.analysisReport.value?.let { report ->
                item {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                        SelectionContainer {
                            Text(
                                text = report,
                                modifier = Modifier.padding(20.dp),
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GaugeCard(label: String, progress: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                CircularProgressIndicator(progress = 1f, color = Color(0xFFF1F5F9), strokeWidth = 8.dp, modifier = Modifier.matchParentSize())
                CircularProgressIndicator(progress = progress / 100f, color = color, strokeWidth = 8.dp, modifier = Modifier.matchParentSize())
                Text("$progress%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        }
    }
}
