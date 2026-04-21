package sarkari.exam_ai.ui.screens.pyq

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.theme.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import sarkari.exam_ai.ui.viewmodels.ContentViewModel
import sarkari.exam_ai.ui.viewmodels.UiState


data class PyqPaper(
    val id: Int,
    val title: String,
    val year: Int,
    val examId: String,
    val size: String,
    val type: String
)


@Composable
fun PyqLibraryScreen(
    navController: NavController, 
    paddingValues: PaddingValues,
    viewModel: ContentViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val pdfsState by viewModel.pdfsState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Search Bar
        SarkariTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search papers...",
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier.padding(bottom = 20.dp)
        )

        when (val state = pdfsState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is UiState.Success -> {
                val filteredPapers = state.data.filter { it.title.contains(searchQuery, ignoreCase = true) }
                
                if (filteredPapers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No papers found", color = TextMuted)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredPapers) { paper ->
                            SarkariCard(
                                onClick = { navController.navigate(sarkari.exam_ai.ui.navigation.Screen.MockTest.route) }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = PrimaryBlue.copy(alpha = 0.1f),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            SarkariBadge(text = paper.category.uppercase(), variant = BadgeVariant.Primary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = paper.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Paper • ${paper.size}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                    
                                    IconButton(onClick = { navController.navigate(sarkari.exam_ai.ui.navigation.Screen.MockTest.route) }) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = "Start", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading library", color = AccentRed)
                }
            }
        }
    }
}
