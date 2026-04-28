package com.sarkari.exam.ui.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGeneratorScreen(
    onBack: () -> Unit = {},
    onViewHistory: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var inputText by remember { mutableStateOf("") }
    var examFocusedMode by remember { mutableStateOf(true) }
    var flashcardGen by remember { mutableStateOf(false) }
    var pyqLinking by remember { mutableStateOf(true) }
    var selectedExam by remember { mutableStateOf("UPSC") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Notes Generator", 
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onViewHistory) { Icon(Icons.Outlined.History, contentDescription = null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceGray)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Input Tabs
            InputTabRow(selectedTab) { selectedTab = it }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Input Area
            InputArea(
                text = inputText,
                onTextChange = { if (it.length <= 5000) inputText = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Smart Formatting Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Smart Formatting", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = "PRO", color = WarningOrange, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormattingToggle("Exam-Focused Mode", "Filter out irrelevant content", Icons.Default.FilterCenterFocus, examFocusedMode) { examFocusedMode = it }
            FormattingToggle("Flashcard Generator", "Auto-convert notes into Q&A", Icons.Default.Layers, flashcardGen) { flashcardGen = it }
            FormattingToggle("PYQ Linking", "Highlight concepts asked in past exams", Icons.Default.Link, pyqLinking) { pyqLinking = it }

            Spacer(modifier = Modifier.height(32.dp))

            // Target Exam Section
            Text(text = "Target Exam", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))
            TargetExamChips(selectedExam) { selectedExam = it }

            Spacer(modifier = Modifier.height(32.dp))

            // Generate Button
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Generate Notes ⚡", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recently Generated Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Recently Generated", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = "View All", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RecentNoteItem("Indian Constitution:", listOf("UPSC", "Polity"), "2 hours ago")
            RecentNoteItem("Important Lakes of India", listOf("SSC CGL", "Geography"), "Yesterday")
            RecentNoteItem("Compound Interest", listOf("Banking", "Quant"), "3 days ago")
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InputTabRow(selectedIdx: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BackgroundWhite)
            .padding(6.dp)
            .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
    ) {
        val tabs = listOf("Text" to Icons.Default.TextFields, "File" to Icons.Default.Description, "URL" to Icons.Default.Link)
        tabs.forEachIndexed { index, pair ->
            val isSelected = selectedIdx == index
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color.White else Color.Transparent,
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(pair.second, contentDescription = null, tint = if (isSelected) PrimaryBlue else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = pair.first, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryBlue else Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InputArea(text: String, onTextChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        color = BackgroundWhite
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Paste your syllabus topic, article, or raw notes here...", color = TextMuted, fontSize = 15.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                    color = SurfaceGray
                ) {
                    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Image", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = PrimaryBlue)
                    }
                }
                Text(text = "${text.length} / 5000", color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FormattingToggle(title: String, subtitle: String, icon: ImageVector, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        color = BackgroundWhite
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PrimaryBlue.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary)
                Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BackgroundWhite, 
                    checkedTrackColor = SuccessGreen,
                    uncheckedThumbColor = BackgroundWhite,
                    uncheckedTrackColor = BorderColor
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetExamChips(selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val exams = listOf("UPSC", "SSC CGL", "Banking")
        exams.forEach { exam ->
            FilterChip(
                selected = selected == exam,
                onClick = { onSelect(exam) },
                label = { Text(exam) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
        AssistChip(
            onClick = { },
            label = { Text("+ Add Custom") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun RecentNoteItem(title: String, tags: List<String>, time: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        color = BackgroundWhite
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(SurfaceGray, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = TextMuted)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
                Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryBlue.copy(alpha = 0.06f)
                        ) {
                            Text(
                                text = tag, 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                                fontSize = 11.sp, 
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(text = time, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(top = 10.dp))
            }
            IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted) }
        }
    }
}
