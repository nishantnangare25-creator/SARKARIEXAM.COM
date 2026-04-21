package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun CurrentAffairsScreen(
    navController: NavController, 
    paddingValues: PaddingValues,
    viewModel: ContentViewModel = viewModel()
) {
    val state by viewModel.currentAffairsState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Hint Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AccentSaffron.copy(alpha = 0.1f)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentSaffron.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentSaffron)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Daily Static Fact", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("The Indian Constitution is the longest written constitution of any sovereign country.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // News Feed
        when (val s = state) {
            is UiState.Loading -> {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            }
            is UiState.Success -> {
                items(s.data) { item ->
                    SarkariCard {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SarkariBadge(text = item.category ?: "General", variant = BadgeVariant.Primary)
                                Text(text = item.date, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = item.content ?: "", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 20.sp)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { /* Share */ }, contentPadding = PaddingValues(0.dp)) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                item {
                    Text("Error loading data", color = AccentRed, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
