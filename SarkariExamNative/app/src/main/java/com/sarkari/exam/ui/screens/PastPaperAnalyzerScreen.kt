package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastPaperAnalyzerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PastPaperAnalyzerViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Paper Analyzer", fontWeight = FontWeight.Bold) },
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
                    Text("Paste Questions to Analyze", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = viewModel.textInput.value,
                        onValueChange = { viewModel.textInput.value = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("Paste raw text of questions here to find weightage and difficulty.") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.analyzePaper() },
                        enabled = !viewModel.isAnalyzing.value && viewModel.textInput.value.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (viewModel.isAnalyzing.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Analyze with AI")
                        }
                    }
                    viewModel.error.value?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            viewModel.analysisReport.value?.let { report ->
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
