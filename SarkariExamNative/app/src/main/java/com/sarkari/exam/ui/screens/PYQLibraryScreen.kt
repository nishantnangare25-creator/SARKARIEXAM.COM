package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PYQLibraryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMockTest: () -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    viewModel: PYQLibraryViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PYQ Library", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onNavigateToAnalyzer) { 
                        Icon(Icons.Default.Search, contentDescription = "Analyzer", tint = Color(0xFF2563EB))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                
                // Filters
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.searchQuery.value,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = { Text("Search by paper name...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Basic dropdown mocks using Buttons for simplicity 
                            OutlinedButton(onClick = { /* Handle Year dropdown */ }, modifier = Modifier.weight(1f)) {
                                Text(if (viewModel.filterYear.value.isEmpty()) "All Years" else viewModel.filterYear.value)
                            }
                            OutlinedButton(onClick = { /* Handle Exam dropdown */ }, modifier = Modifier.weight(1f)) {
                                Text(if (viewModel.filterExam.value.isEmpty()) "All Exams" else viewModel.filterExam.value.uppercase())
                            }
                        }
                    }
                }

                // AI Analyzer Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { onNavigateToAnalyzer() }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Past Paper AI Analyzer", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            Text("Analyze syllabus weightage and patterns.", fontSize = 12.sp, color = Color(0xFF3B82F6))
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF3B82F6))
                    }
                }

                // List of Papers
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.getFilteredPdfs()) { pdf ->
                        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(4.dp)) {
                                        Text(pdf.examId.uppercase(), fontSize = 10.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${pdf.year}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(pdf.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), lineHeight = 20.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${pdf.type} • ${pdf.size}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onNavigateToMockTest,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Mock Test")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
