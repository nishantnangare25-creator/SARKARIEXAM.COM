package com.sarkari.exam.ui.screens.analytics

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.AnalyticsViewModel
import com.sarkari.exam.ui.viewmodels.SubjectPerformance

val BackgroundLight = Color.White
val ColorGreen = Color(0xFF00B859)
val ColorRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    
    val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsState()
    val selectedGraphToggle by viewModel.selectedGraphToggle.collectAsState()
    
    val stats by viewModel.stats.collectAsState()
    val subjectPerformances by viewModel.subjectPerformances.collectAsState()
    val testHistory by viewModel.testHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Analytics 📊", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
                        Text("Track your performance", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filter", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* Calendar */ }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar", tint = PrimaryBlue)
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
                    AnalyticsDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Time Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.timeFilters) { filter ->
                        val isSelected = selectedTimeFilter == filter
                        Surface(
                            modifier = Modifier.clickable { viewModel.onTimeFilterSelected(filter) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PrimaryBlue else Color.White,
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else TextDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else {
                // Performance Summary (2x2 Grid)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard("Accuracy", stats.accuracy, Icons.Outlined.AdsClick, Modifier.weight(1f))
                            SummaryCard("Tests Attempted", stats.testsAttempted, Icons.Outlined.Assignment, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard("Study Time", stats.studyTime, Icons.Outlined.Timer, Modifier.weight(1f))
                            SummaryCard("Rank", stats.rank, Icons.Outlined.EmojiEvents, Modifier.weight(1f))
                        }
                    }
                }

                // AI Insights Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF0E6), // Soft orange bg
                        border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("AI Insight", fontWeight = FontWeight.ExtraBold, color = AccentOrange, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("You are weak in General Awareness. Focus on current affairs and revision.", color = TextDark, fontSize = 14.sp, lineHeight = 20.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { /* Action */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Improve Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Performance Graph
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Performance Trend", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                                GraphToggle(
                                    toggles = viewModel.graphToggles,
                                    selected = selectedGraphToggle,
                                    onSelect = { viewModel.onGraphToggleSelected(it) }
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            // Placeholder for Line Chart
                            Box(
                                modifier = Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.SsidChart, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("[ Line Chart Area ]", color = TextMuted)
                                }
                            }
                        }
                    }
                }

                // Subject-wise Performance
                item {
                    Text("Subject Performance", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            subjectPerformances.forEach { subject ->
                                SubjectProgressBar(subject)
                            }
                        }
                    }
                }

                // Test History
                item {
                    Text("Recent Test History", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                }

                items(testHistory) { history ->
                    HistoryCard(history)
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).background(PrimaryBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 24.sp, color = TextDark)
        }
    }
}

@Composable
fun SubjectProgressBar(subjectPerf: SubjectPerformance) {
    val barColor = when (subjectPerf.statusColorHex) {
        "BLUE" -> PrimaryBlue
        "GREEN" -> ColorGreen
        "ORANGE" -> AccentOrange
        "RED" -> ColorRed
        else -> PrimaryBlue
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(subjectPerf.subject, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
            Text("${subjectPerf.percentage}%", color = barColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = subjectPerf.percentage / 100f,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun HistoryCard(item: com.sarkari.exam.ui.viewmodels.TestHistoryItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Assignment, contentDescription = null, tint = TextMuted)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.date, color = TextMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.score, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = PrimaryBlue)
                Text("${item.accuracy} Acc", color = ColorGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GraphToggle(toggles: List<String>, selected: String, onSelect: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFF3F4F6)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            toggles.forEach { toggle ->
                val isSelected = selected == toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onSelect(toggle) }
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = toggle,
                        color = if (isSelected) PrimaryBlue else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDropdown(
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
