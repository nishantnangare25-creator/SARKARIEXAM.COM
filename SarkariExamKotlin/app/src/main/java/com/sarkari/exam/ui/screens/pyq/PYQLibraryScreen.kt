package com.sarkari.exam.ui.screens.pyq

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.AccentSaffron
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.theme.TextSecondary
import kotlin.random.Random

data class PYQPdf(
    val id: Int,
    val title: String,
    val examId: String,
    val year: Int,
    val size: String,
    val type: String
)

data class ExamDef(val id: String, val titles: List<String>, val startYear: Int, val name: String)

val EXAM_CONFIGS = listOf(
    ExamDef("upsc", listOf("UPSC Civil Services Prelims GS Paper 1", "UPSC Prelims CSAT", "UPSC Mains GS Paper 1", "UPSC Mains GS Paper 2", "UPSC Mains GS Paper 3", "UPSC Mains GS Paper 4"), 2004, "UPSC Civil Services"),
    ExamDef("mpsc", listOf("MPSC Rajyaseva Prelims Paper 1", "MPSC Rajyaseva Prelims CSAT", "MPSC Mains GS 1"), 2010, "MPSC"),
    ExamDef("ssc", listOf("SSC CGL Tier 1 Quantitative Aptitude", "SSC CGL Tier 1 General Awareness", "SSC CGL Tier 2 Maths"), 2014, "SSC CGL/CHSL"),
    ExamDef("banking", listOf("IBPS PO Prelims Reasoning", "IBPS PO Prelims Quantitative Aptitude", "SBI Clerk Mains General Awareness"), 2015, "Banking"),
    ExamDef("railway", listOf("RRB NTPC Stage 1 CBT", "RRB Group D General Science"), 2015, "Railway"),
    ExamDef("nda", listOf("NDA Mathematics Paper 1", "NDA General Ability Test"), 2014, "NDA"),
    ExamDef("state_psc", listOf("BPSC Prelims (Bihar PSC)", "UPPSC Prelims GS", "RPSC RAS Prelims"), 2014, "State PSC")
)

fun generateMockPdfs(): List<PYQPdf> {
    val list = mutableListOf<PYQPdf>()
    var idCounter = 1
    val currentYear = 2024
    
    EXAM_CONFIGS.forEach { exam ->
        for (year in currentYear downTo exam.startYear) {
            exam.titles.forEach { title ->
                val sizeVal = String.format("%.1f", Random.nextDouble(1.2, 4.2))
                list.add(
                    PYQPdf(
                        id = idCounter++,
                        title = title,
                        examId = exam.id,
                        year = year,
                        size = "$sizeVal MB",
                        type = if (year % 4 == 0) "Question Paper + Solution" else "Question Paper"
                    )
                )
            }
        }
    }
    return list
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PYQLibraryScreen(navController: NavController) {
    val mockPdfs = remember { generateMockPdfs() }
    
    var searchQuery by remember { mutableStateOf("") }
    var filterYear by remember { mutableStateOf("") }
    var filterExam by remember { mutableStateOf("") }
    
    var examExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    
    val filteredPdfs = remember(searchQuery, filterYear, filterExam) {
        mockPdfs.filter { pdf ->
            val matchExam = filterExam.isEmpty() || pdf.examId == filterExam
            val matchYear = filterYear.isEmpty() || pdf.year.toString() == filterYear
            val matchSearch = pdf.title.contains(searchQuery, ignoreCase = true)
            matchExam && matchYear && matchSearch
        }
    }
    
    val years = (2004..2024).reversed().map { it.toString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PYQ Library", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = AccentSaffron.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "PREVIOUS PAPERS",
                    color = AccentSaffron,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("PYQ Library", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Access 10,000+ official previous year question papers.", color = TextSecondary)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Upload Drag Drop Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .clickable { /* Simulate File Picker */ }
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tap to upload Custom PDF", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Parse any offline PDF to practice.", color = TextSecondary, fontSize = 13.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Filters
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search papers...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Year Filter
                        ExposedDropdownMenuBox(
                            expanded = yearExpanded,
                            onExpandedChange = { yearExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (filterYear.isEmpty()) "All Years" else filterYear,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = yearExpanded,
                                onDismissRequest = { yearExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Years") },
                                    onClick = { filterYear = ""; yearExpanded = false }
                                )
                                years.forEach { y ->
                                    DropdownMenuItem(
                                        text = { Text(y) },
                                        onClick = { filterYear = y; yearExpanded = false }
                                    )
                                }
                            }
                        }
                        
                        // Exam Filter
                        ExposedDropdownMenuBox(
                            expanded = examExpanded,
                            onExpandedChange = { examExpanded = it },
                            modifier = Modifier.weight(1.5f)
                        ) {
                            val displayExam = if (filterExam.isEmpty()) "All Exams" else EXAM_CONFIGS.find { it.id == filterExam }?.name ?: ""
                            OutlinedTextField(
                                value = displayExam,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = examExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = examExpanded,
                                onDismissRequest = { examExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Exams") },
                                    onClick = { filterExam = ""; examExpanded = false }
                                )
                                EXAM_CONFIGS.forEach { e ->
                                    DropdownMenuItem(
                                        text = { Text(e.name) },
                                        onClick = { filterExam = e.id; examExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Results Grid
            if (filteredPdfs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Article, contentDescription = null, tint = TextMuted, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No materials found", fontWeight = FontWeight.Bold)
                        Text("Try adjusting your filters.", color = TextSecondary)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPdfs) { pdf ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            EXAM_CONFIGS.find { it.id == pdf.examId }?.name ?: "General",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = PrimaryBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(pdf.year.toString(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(pdf.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                                    Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${pdf.type} • ${pdf.size}", color = TextSecondary, fontSize = 13.sp)
                                }
                                
                                Button(
                                    onClick = { navController.navigate(Screen.PYQTest.route) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Practice")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

