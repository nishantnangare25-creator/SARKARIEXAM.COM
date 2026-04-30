package com.sarkari.exam.ui.screens.notes

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.GenerationState
import com.sarkari.exam.ui.viewmodels.InputTab
import com.sarkari.exam.ui.viewmodels.NotesGeneratorViewModel
import com.sarkari.exam.ui.viewmodels.RecentNoteItem

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGeneratorScreen(
    onOpenDrawer: () -> Unit,
    viewModel: NotesGeneratorViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val inputUrl by viewModel.inputUrl.collectAsState()
    
    val examFocused by viewModel.examFocused.collectAsState()
    val convertFlashcards by viewModel.convertFlashcards.collectAsState()
    val highlightPoints by viewModel.highlightPoints.collectAsState()
    val linkPyq by viewModel.linkPyq.collectAsState()
    
    val generationState by viewModel.generationState.collectAsState()
    val recentNotes by viewModel.recentNotes.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("AI Notes Generator 📄", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* History */ }) {
                        Icon(Icons.Outlined.History, contentDescription = "History", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* Save */ }) {
                        Icon(Icons.Outlined.Save, contentDescription = "Save", tint = PrimaryBlue)
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
            
            // Output Section (Shows if successfully generated)
            if (generationState is GenerationState.Success) {
                item {
                    GeneratedOutputCard(
                        note = (generationState as GenerationState.Success).note,
                        onClose = { viewModel.clearState() }
                    )
                }
            }

            // Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    NotesDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    NotesDropdown(
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
                InputTabRow(currentTab = currentTab, onTabSelect = { viewModel.setTab(it) })
            }

            // Input Area based on Tab
            item {
                AnimatedContent(targetState = currentTab, label = "input_area") { tab ->
                    when (tab) {
                        InputTab.TEXT -> TextInputArea(inputText, onTextChange = { viewModel.onInputTextChanged(it) })
                        InputTab.FILE -> FileUploadArea()
                        InputTab.URL -> UrlInputArea(inputUrl, onUrlChange = { viewModel.onInputUrlChanged(it) })
                    }
                }
            }

            // Error Message
            if (generationState is GenerationState.Error) {
                item {
                    Text(
                        text = (generationState as GenerationState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Smart Options
            item {
                Text("Smart Options", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmartToggle("Exam-focused notes", examFocused) { viewModel.toggleSmartOption("examFocused") }
                    SmartToggle("Convert to flashcards", convertFlashcards) { viewModel.toggleSmartOption("flashcards") }
                    SmartToggle("Highlight important points", highlightPoints) { viewModel.toggleSmartOption("highlight") }
                    SmartToggle("Link with PYQs", linkPyq) { viewModel.toggleSmartOption("linkPyq") }
                }
            }

            // Generate Button
            item {
                val isLoading = generationState is GenerationState.Loading
                Button(
                    onClick = { viewModel.generateNotes() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, Color(0xFF4371D7))
                            )
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            "Generate Notes 🤖", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 18.sp, 
                            color = Color.White
                        )
                    }
                }
            }

            // Recent Notes
            item {
                Text("Recent Notes", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            }
            
            items(recentNotes) { note ->
                RecentNoteCard(note)
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun InputTabRow(currentTab: InputTab, onTabSelect: (InputTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5E7EB)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            InputTab.values().forEach { tab ->
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
                        text = tab.name.capitalize(),
                        color = if (isSelected) PrimaryBlue else TextMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TextInputArea(text: String, onTextChange: (String) -> Unit) {
    Column {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Paste topic, paragraph, or question here...", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = PrimaryBlue,
                unfocusedIndicatorColor = Color(0xFFE5E7EB)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${text.split("\\s+".toRegex()).count { it.isNotEmpty() }}/1000 words",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun FileUploadArea() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { /* Handle Upload */ },
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
            Text("Tap to upload PDF or Image", fontWeight = FontWeight.Bold, color = TextDark)
            Text("Max file size: 10MB", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun UrlInputArea(url: String, onUrlChange: (String) -> Unit) {
    OutlinedTextField(
        value = url,
        onValueChange = onUrlChange,
        placeholder = { Text("Paste article or webpage URL", color = TextMuted) },
        leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null, tint = TextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = Color(0xFFE5E7EB)
        ),
        singleLine = true
    )
}

@Composable
fun SmartToggle(label: String, isChecked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 15.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
        )
    }
}

@Composable
fun GeneratedOutputCard(note: com.sarkari.exam.ui.viewmodels.GeneratedNote, onClose: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(note.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryBlue, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(note.summary, color = TextDark, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            note.bullets.forEach { bullet ->
                Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.Top) {
                    Text("• ", fontWeight = FontWeight.Bold, color = AccentOrange)
                    Text(bullet, color = TextDark, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                TextButton(onClick = { /* Copy */ }) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
                TextButton(onClick = { /* Save */ }) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
                TextButton(onClick = { /* Download */ }) {
                    Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
            }
        }
    }
}

@Composable
fun RecentNoteCard(note: RecentNoteItem) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { /* Open Note */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(note.subject, color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(color = AccentOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                        Text(note.exam, color = AccentOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Text(note.timeAgo, color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesDropdown(
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
