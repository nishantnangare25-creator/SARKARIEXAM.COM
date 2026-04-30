package com.sarkari.exam.ui.screens.paperanalyzer

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.AnalysisState
import com.sarkari.exam.ui.viewmodels.AnalyzerTab
import com.sarkari.exam.ui.viewmodels.PaperAnalysisResult
import com.sarkari.exam.ui.viewmodels.PaperAnalyzerViewModel
import com.sarkari.exam.ui.viewmodels.TopicTrend

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperAnalyzerScreen(
    onOpenDrawer: () -> Unit,
    viewModel: PaperAnalyzerViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Past Paper Analyzer 📊", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* History */ }) {
                        Icon(Icons.Outlined.History, contentDescription = "History", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filter", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnalyzerDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyzerDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs
            item {
                AnalyzerTabRow(currentTab = currentTab, onTabSelect = { viewModel.setTab(it) })
            }

            // Input Area
            item {
                AnimatedContent(targetState = currentTab, label = "input_area") { tab ->
                    when (tab) {
                        AnalyzerTab.UPLOAD_PDF -> FileUploadArea()
                        AnalyzerTab.SELECT_PYQ -> SelectPyqArea(viewModel.yearsList, selectedYear) { viewModel.onYearSelected(it) }
                        AnalyzerTab.ENTER_QUESTIONS -> EnterQuestionsArea()
                    }
                }
            }

            // Analyze Button
            item {
                val isLoading = analysisState is AnalysisState.Loading
                Button(
                    onClick = { viewModel.analyzePaper() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Analyze Paper 🚀", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }

            // Results Section
            if (analysisState is AnalysisState.Success) {
                val result = (analysisState as AnalysisState.Success).result

                item {
                    Divider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(vertical = 8.dp))
                    Text("Analysis Report", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
                }

                item { SummaryCard(result) }
                item { AIInsightsCard(result.aiInsight) }
                item { TrendAnalysisSection(result.trends) }
                item { DifficultyBreakdownSection(result.easyPercent, result.mediumPercent, result.hardPercent) }
                item { ImportantQuestionsSection(result.importantQuestions) }
                item { ExportActions() }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun AnalyzerTabRow(currentTab: AnalyzerTab, onTabSelect: (AnalyzerTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5E7EB)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            val tabs = listOf(
                Pair(AnalyzerTab.UPLOAD_PDF, "Upload PDF"),
                Pair(AnalyzerTab.SELECT_PYQ, "Select PYQ"),
                Pair(AnalyzerTab.ENTER_QUESTIONS, "Enter Qs")
            )
            tabs.forEach { (tab, title) ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelect(tab) }
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) PrimaryBlue else TextMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FileUploadArea() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(180.dp).clickable { },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tap to upload Past Paper PDF", fontWeight = FontWeight.Bold, color = TextDark)
            Text("Supported formats: PDF, Images", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun SelectPyqArea(yearsList: List<String>, selectedYear: String, onYearSelected: (String) -> Unit) {
    Column {
        Text("Select Year", fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(yearsList) { year ->
                val isSelected = selectedYear == year
                Surface(
                    modifier = Modifier.clickable { onYearSelected(year) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryBlue else Color.White,
                    border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
                ) {
                    Text(
                        text = year,
                        color = if (isSelected) Color.White else TextDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EnterQuestionsArea() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Type or paste questions here...") },
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = Color(0xFFE5E7EB)
        )
    )
}

@Composable
fun SummaryCard(result: PaperAnalysisResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(result.totalQuestions.toString(), fontWeight = FontWeight.Black, fontSize = 24.sp, color = PrimaryBlue)
                Text("Questions", color = TextMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(result.overallDifficulty, fontWeight = FontWeight.Black, fontSize = 20.sp, color = AccentOrange)
                Text("Difficulty", color = TextMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(result.topTopic, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF00B859))
                Text("Top Topic", color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AIInsightsCard(insight: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = PrimaryBlue.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("AI Insight", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(insight, color = TextDark, fontSize = 14.sp, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Practice Similar", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TrendAnalysisSection(trends: List<TopicTrend>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Topic-wise Trends", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            trends.forEach { trend ->
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(trend.topic, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${trend.percentage}%", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = trend.percentage / 100f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = PrimaryBlue,
                        trackColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyBreakdownSection(easy: Int, medium: Int, hard: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Difficulty Breakdown", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().height(24.dp).clip(CircleShape)) {
                Box(modifier = Modifier.weight(easy.toFloat()).fillMaxHeight().background(Color(0xFF00B859)))
                Box(modifier = Modifier.weight(medium.toFloat()).fillMaxHeight().background(AccentOrange))
                Box(modifier = Modifier.weight(hard.toFloat()).fillMaxHeight().background(Color(0xFFD32F2F)))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DifficultyLegend("Easy", "$easy%", Color(0xFF00B859))
                DifficultyLegend("Medium", "$medium%", AccentOrange)
                DifficultyLegend("Hard", "$hard%", Color(0xFFD32F2F))
            }
        }
    }
}

@Composable
fun DifficultyLegend(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, color = TextMuted, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
        }
    }
}

@Composable
fun ImportantQuestionsSection(questions: List<String>) {
    Column {
        Text("Most Repeated Questions", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
        Spacer(modifier = Modifier.height(12.dp))
        questions.forEach { question ->
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(question, color = TextDark, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ExportActions() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedButton(
            onClick = { /* Save */ },
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
        ) {
            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save")
        }
        Button(
            onClick = { /* Download PDF */ },
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Outlined.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("PDF Report")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzerDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryBlue
            ),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
