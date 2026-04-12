package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class NewsItem(
    val title: String,
    val desc: String,
    val category: String,
    val date: String
)

class CurrentAffairsViewModel : ViewModel() {
    val news = mutableStateListOf<NewsItem>()
    val selectedCategory = mutableStateOf("All")
    val isLoading = mutableStateOf(true)

    val categories = listOf("All", "National", "Economy", "Science & Tech", "International", "Sports", "Policy")

    init {
        fetchNews()
    }

    fun fetchNews(force: Boolean = false) {
        if (force) {
            isLoading.value = true
            news.clear()
        }
        
        viewModelScope.launch {
            // Simulating network delay for fetching news
            delay(1000)
            
            val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            
            news.addAll(listOf(
                NewsItem("Union Budget 2026 Key Highlights", "The Finance Minister presented the budget focusing on infrastructure, health, and agricultural subsidies.", "Economy", today),
                NewsItem("ISRO successfully launches new Earth Observation Satellite", "The satellite will help in disaster management and agricultural monitoring.", "Science & Tech", today),
                NewsItem("New Education Policy amendments announced", "The government introduces new structural changes to vocational training programs.", "Policy", today),
                NewsItem("National Games 2026 kicks off in Gujarat", "Over 7000 athletes from across the country will participate in 36 sporting events.", "Sports", today)
            ))
            
            isLoading.value = false
        }
    }

    fun getFilteredNews(): List<NewsItem> {
        if (selectedCategory.value == "All") return news
        return news.filter { it.category.contains(selectedCategory.value, ignoreCase = true) }
    }
}
