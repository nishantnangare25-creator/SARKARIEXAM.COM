package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.theme.*

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import sarkari.exam_ai.ui.viewmodels.ContentViewModel
import sarkari.exam_ai.ui.viewmodels.UiState

@Composable
fun PdfsScreen(navController: NavController, paddingValues: PaddingValues, viewModel: ContentViewModel = viewModel()) {
    val pdfsState = viewModel.pdfsState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        when (pdfsState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error fetching PDFs: ${pdfsState.message}", color = AccentRed)
                }
            }
            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val pdfs = pdfsState.data
                    
                    // If empty, show a few dummy ones so UI isn't completely blank for mockup
                    val displayPdfs = if (pdfs.isEmpty()) {
                        listOf("Polity Notes", "Current Affairs Oct", "Maths Formulas", "General Science PDF", "Interview Guide", "History Timelines").map {
                            sarkari.exam_ai.data.models.PdfItem(id = 0, title = it, size = "~2MB", category = "All", downloadUrl = "")
                        }
                    } else {
                        pdfs
                    }

                    items(displayPdfs.size) { index ->
                        val pdfItem = displayPdfs[index]
                        SarkariCard(onClick = { /* Open PDF viewer */ }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AccentRed.copy(alpha = 0.1f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = AccentRed)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(pdfItem.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Text(pdfItem.size, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
