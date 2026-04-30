package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val date: String,
    val category: String,
    val isBookmarked: Boolean = false
)

class CurrentAffairsViewModel : ViewModel() {

    private val _allArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _filteredArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val filteredArticles: StateFlow<List<NewsArticle>> = _filteredArticles.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val filters = listOf("All", "National", "International", "Economy", "Science", "Sports")

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            NewsArticle("1", "India's GDP Growth Surpasses Estimates", "The latest quarter shows a robust growth of 8.4%, beating most analyst estimates.", "25 Feb 2026", "Economy"),
            NewsArticle("2", "ISRO Announces New Lunar Mission", "Chandrayaan-4 is scheduled for launch next year with advanced rover capabilities.", "24 Feb 2026", "Science"),
            NewsArticle("3", "Global Climate Summit Concludes", "Nations agree on new carbon emission targets for 2035 in Paris.", "23 Feb 2026", "International"),
            NewsArticle("4", "New Education Policy Implemented in 5 States", "The structural changes focus on skill-based learning over rote memorization.", "22 Feb 2026", "National"),
            NewsArticle("5", "India Wins T20 World Cup 2026", "A thrilling finale ends with India lifting the trophy against Australia.", "21 Feb 2026", "Sports")
        )
        _allArticles.value = dummyData
        applyFilters()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun onFilterSelect(filter: String) {
        _selectedFilter.value = filter
        applyFilters()
    }

    fun toggleBookmark(articleId: String) {
        val updatedArticles = _allArticles.value.map { article ->
            if (article.id == articleId) {
                article.copy(isBookmarked = !article.isBookmarked)
            } else {
                article
            }
        }
        _allArticles.value = updatedArticles
        applyFilters() // Refresh the filtered list to show bookmark state changes
    }

    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val filter = _selectedFilter.value

        val filtered = _allArticles.value.filter { article ->
            val matchesSearch = article.title.lowercase().contains(query) || article.summary.lowercase().contains(query)
            val matchesFilter = if (filter == "All") true else article.category == filter
            
            matchesSearch && matchesFilter
        }
        _filteredArticles.value = filtered
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Simulate network delay
            delay(1500)
            // Reload or fetch new data
            loadDummyData()
            _isRefreshing.value = false
        }
    }
}
