package com.sarkari.exam.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sarkari.exam.data.models.ChatMessage
import com.sarkari.exam.data.repository.AiRepository
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

data class NewsItem(
    val date: String,
    val title: String,
    val category: String,
    val desc: String
)

class CurrentAffairsViewModel : ViewModel() {
    private val repository = AiRepository()
    var newsList by mutableStateOf<List<NewsItem>>(emptyList())
    var isLoading by mutableStateOf(true)
    var selectedCategory by mutableStateOf("All")
    
    val categories = listOf("All", "National", "Economy", "Science & Tech", "International", "Sports", "Policy")

    init {
        fetchNews()
    }

    fun fetchNews(apiKey: String = "YOUR_API_KEY") {
        isLoading = true
        viewModelScope.launch {
            val prompt = """Provide 5 important current affairs for Indian competitive exams for today. Respond EXACTLY with a JSON array in this format: [{"date": "18 Apr", "title": "Headline", "category": "National", "desc": "Summary"}]. Do not use markdown blocks."""
            val response = repository.getAiResponse(listOf(ChatMessage("user", prompt)), apiKey)
            isLoading = false
            
            if (response != null) {
                try {
                    val cleanedResponse = response.replace("```json", "").replace("```", "").trim()
                    val jsonArray = JSONArray(cleanedResponse)
                    val parsedNews = mutableListOf<NewsItem>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        parsedNews.add(NewsItem(
                            date = obj.optString("date", "Today"),
                            title = obj.optString("title", "Update"),
                            category = obj.optString("category", "General"),
                            desc = obj.optString("desc", "")
                        ))
                    }
                    newsList = parsedNews
                } catch (e: JSONException) {
                    // Fallback
                    newsList = listOf(NewsItem("Today", "AI Generation Failed", "Error", "Please manually pull to refresh to try again."))
                }
            } else {
                newsList = listOf(NewsItem("Today", "Connection Error", "Network", "Could not fetch latest updates."))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentAffairsScreen(navController: NavController, viewModel: CurrentAffairsViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current Affairs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchNews() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentSaffron)
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
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = AccentSaffron.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentSaffron.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Daily Fact", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("India's forex reserves reached an all-time high this week.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { cat ->
                    val isSelected = viewModel.selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { viewModel.selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PrimaryBlue else TextPrimary
                        )
                    }
                }
            }

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentSaffron)
                }
            } else {
                val filtered = if (viewModel.selectedCategory == "All") viewModel.newsList else viewModel.newsList.filter { it.category.contains(viewModel.selectedCategory, ignoreCase = true) }
                
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filtered) { news ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Surface(
                                        color = AccentSaffron.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            news.category.uppercase(),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentSaffron
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(news.date, fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(news.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 22.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(news.desc, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.Start) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Share News", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

