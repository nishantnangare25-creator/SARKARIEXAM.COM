package com.sarkari.exam.ui.screens.mocktest

import androidx.compose.animation.*
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
import com.sarkari.exam.ui.viewmodels.MockTestViewModel
import com.sarkari.exam.ui.viewmodels.TestItem
import com.sarkari.exam.ui.viewmodels.TestMode
import com.sarkari.exam.ui.viewmodels.UserPerformance

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestDashboardScreen(
    onOpenDrawer: () -> Unit,
    viewModel: MockTestViewModel = viewModel()
) {
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val testMode by viewModel.testMode.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val tests by viewModel.tests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val performance by viewModel.performance.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Mock Test & PYQ", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filter", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* History */ }) {
                        Icon(Icons.Outlined.History, contentDescription = "History", tint = PrimaryBlue)
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
            // Mode Toggle
            item {
                ModeToggleSwitch(
                    currentMode = testMode,
                    onModeSelect = { viewModel.onModeToggle(it) }
                )
            }

            // Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    CustomDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // PYQ Years Filter
            if (testMode == TestMode.PYQ) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.pyqYears) { year ->
                            val isSelected = selectedYear == year
                            Surface(
                                modifier = Modifier.clickable { viewModel.onYearSelected(year) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PrimaryBlue else Color.White,
                                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
                            ) {
                                Text(
                                    text = year,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Performance & Gamification
            item {
                PerformanceSummary(performance)
            }

            // Quick Test Section
            item {
                QuickTestCard()
            }

            // Test List
            item {
                Text(
                    text = if (testMode == TestMode.MOCK) "Available Mock Tests" else "Previous Year Papers",
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 18.sp, 
                    color = TextDark
                )
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else {
                items(tests) { test ->
                    TestCard(test)
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ModeToggleSwitch(currentMode: TestMode, onModeSelect: (TestMode) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            val mockSelected = currentMode == TestMode.MOCK
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .clickable { onModeSelect(TestMode.MOCK) }
                    .background(if (mockSelected) PrimaryBlue else Color.Transparent)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Mock Test", color = if (mockSelected) Color.White else TextMuted, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .clickable { onModeSelect(TestMode.PYQ) }
                    .background(if (!mockSelected) PrimaryBlue else Color.Transparent)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("PYQ Mode", color = if (!mockSelected) Color.White else TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
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
            )
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

@Composable
fun PerformanceSummary(performance: UserPerformance) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFDAA520))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(performance.rank, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                        Text("${performance.xpPoints} XP", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(color = AccentOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                    Text("🔥 ${performance.streak} Streak", color = AccentOrange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatItem(value = "${performance.accuracy}%", label = "Accuracy", color = Color(0xFF00B859))
                StatItem(value = performance.testsAttempted.toString(), label = "Attempted", color = PrimaryBlue)
                StatItem(value = performance.lastScore, label = "Last Score", color = AccentOrange)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        Text(label, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun QuickTestCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF0E6), // Soft orange bg
        border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("⚡ 10 Minute Quick Test", color = AccentOrange, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Bite-sized practice for quick revision", color = TextDark, fontSize = 12.sp)
            }
            Button(
                onClick = { /* Start */ },
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TestCard(test: TestItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(test.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                    if (test.year != null) {
                        Text("Year: ${test.year}", color = TextMuted, fontSize = 12.sp)
                    }
                }
                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = if (test.type == TestMode.MOCK) "MOCK" else "PYQ",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Assignment, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${test.totalQuestions} Qs", color = TextMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Timer, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${test.durationMinutes} mins", color = TextMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.BarChart, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(test.difficulty, color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Start Test */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Test", fontWeight = FontWeight.Bold)
            }
        }
    }
}
