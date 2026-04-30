package com.sarkari.exam.ui.screens.pyqpdfs

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.PyqPdf
import com.sarkari.exam.ui.viewmodels.PyqPdfViewModel

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqPdfScreen(
    onOpenDrawer: () -> Unit,
    viewModel: PyqPdfViewModel = viewModel()
) {
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredPdfs by viewModel.filteredPdfs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val downloadedPdfs = filteredPdfs.filter { it.isDownloaded }
    val availablePdfs = filteredPdfs.filter { !it.isDownloaded }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("PYQ PDFs 📚", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search Action */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = PrimaryBlue)
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
            
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search PYQ PDFs...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent,
                        containerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PyqDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    PyqDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Year Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.yearsList) { year ->
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

            // Feature Card
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
                            Text("Convert PDF to Notes 🤖", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Let AI extract key insights from papers.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            color = AccentOrange,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { /* Go to Notes Gen */ }
                        ) {
                            Text(
                                text = "Generate", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else {
                // Saved / Downloaded Section
                if (downloadedPdfs.isNotEmpty()) {
                    item {
                        Text("Downloaded PDFs", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                    }
                    items(downloadedPdfs, key = { it.id }) { pdf ->
                        PdfCard(pdf, onDownload = {})
                    }
                    item {
                        Divider(color = Color(0xFFE5E7EB), modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                // Available PDFs Section
                item {
                    Text("Available Papers", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                }

                if (availablePdfs.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No papers found for selected filters.", color = TextMuted)
                        }
                    }
                } else {
                    items(availablePdfs, key = { it.id }) { pdf ->
                        PdfCard(pdf, onDownload = { viewModel.downloadPdf(pdf.id) })
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun PdfCard(pdf: PyqPdf, onDownload: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { /* View PDF */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(48.dp).background(AccentOrange.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = AccentOrange)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(pdf.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(pdf.subject, color = TextMuted, fontSize = 12.sp)
                        Text(" • ", color = TextMuted, fontSize = 12.sp)
                        Text(pdf.year, color = TextMuted, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(pdf.size, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* View PDF */ }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Visibility, contentDescription = "View", tint = PrimaryBlue)
                    }
                    
                    if (pdf.isDownloaded) {
                        Surface(color = Color(0xFF00B859).copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Downloaded", tint = Color(0xFF00B859), modifier = Modifier.padding(4.dp).size(20.dp))
                        }
                    } else if (pdf.isDownloading) {
                        CircularProgressIndicator(
                            progress = pdf.downloadProgress,
                            color = AccentOrange,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.FileDownload, contentDescription = "Download", tint = PrimaryBlue)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PyqDropdown(
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
