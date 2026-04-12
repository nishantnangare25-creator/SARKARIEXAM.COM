package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGeneratorScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotesGeneratorViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Notes Generator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Define Your Scope", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = viewModel.exam.value,
                            onValueChange = { viewModel.exam.value = it },
                            label = { Text("Exam") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = viewModel.subject.value,
                            onValueChange = { viewModel.subject.value = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.topics.value,
                        onValueChange = { viewModel.topics.value = it },
                        label = { Text("Specific Topics (e.g. Fundamental Rights)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.generateNotes() },
                        enabled = !viewModel.isGenerating.value && viewModel.topics.value.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (viewModel.isGenerating.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Detailed Notes")
                        }
                    }
                    viewModel.error.value?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            viewModel.notesResult.value?.let { report ->
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFBEB), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)), modifier = Modifier.fillMaxWidth()) {
                    SelectionContainer {
                        Text(
                            text = report,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}
