package com.sarkari.exam.ui.screens.studyplanner

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.Priority
import com.sarkari.exam.ui.viewmodels.StudyPlannerViewModel
import com.sarkari.exam.ui.viewmodels.StudyTask

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(
    onOpenDrawer: () -> Unit,
    viewModel: StudyPlannerViewModel = viewModel()
) {
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Study Planner 📅", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Calendar view */ }) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { showAddTaskDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
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
                    PlannerDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    PlannerDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weekly Schedule
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.weekDays) { day ->
                        val isSelected = selectedDay == day
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.onDaySelected(day) }
                                .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) Color.White else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // AI Study Plan Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryBlue,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Generated Plan", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Based on your weak topics and performance", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            color = AccentOrange,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { /* Generate */ }
                        ) {
                            Text(
                                text = "Generate 🤖", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Progress Tracker
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProgressCard("Study Hours", "${String.format("%.1f", viewModel.getTotalStudyHours())}h", Icons.Default.Schedule, Modifier.weight(1f))
                    ProgressCard("Tasks Done", "${viewModel.getCompletedTasksCount()}/${tasks.size}", Icons.Default.TaskAlt, Modifier.weight(1f))
                    ProgressCard("Consistency", "86%", Icons.Default.TrendingUp, Modifier.weight(1f))
                }
            }

            // Daily Plan Section
            item {
                Text("Today's Plan", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            }

            if (tasks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No tasks scheduled for today. Take a break or add a new task!", color = TextMuted, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(tasks) { task ->
                    TaskCard(task, onToggle = { viewModel.toggleTaskCompletion(task.id) })
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) } // Space for FAB
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAdd = { title, duration, priority ->
                viewModel.addTask(title, duration, priority)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun ProgressCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
            Text(label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TaskCard(task: StudyTask, onToggle: () -> Unit) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFD32F2F) // Red
        Priority.MEDIUM -> AccentOrange
        Priority.LOW -> Color(0xFF00B859) // Green
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (task.isCompleted) TextMuted else TextDark,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.subject, color = TextMuted, fontSize = 12.sp)
                    Text(" • ${task.durationMin} min", color = TextMuted, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(color = priorityColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = task.priority.name,
                    color = priorityColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerDropdown(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, Int, Priority) -> Unit) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Priority", fontWeight = FontWeight.Medium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Priority.values().forEach { p ->
                        val isSelected = priority == p
                        Surface(
                            modifier = Modifier.clickable { priority = p },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) PrimaryBlue else Color.White,
                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
                        ) {
                            Text(
                                text = p.name,
                                color = if (isSelected) Color.White else TextDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, duration.toIntOrNull() ?: 30, priority) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
