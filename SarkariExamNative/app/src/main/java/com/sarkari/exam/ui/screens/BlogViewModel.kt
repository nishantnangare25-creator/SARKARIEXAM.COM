package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class BlogPost(
    val id: String,
    val title: String,
    val excerpt: String,
    val date: String,
    val readTime: String,
    val featuredImage: String,
    val tags: List<String>
)

class BlogViewModel : ViewModel() {
    val posts = mutableStateListOf<BlogPost>()
    val searchQuery = mutableStateOf("")
    val activeTag = mutableStateOf("All")

    init {
        posts.addAll(listOf(
            BlogPost("1", "UPSC Prelims Strategy 2026", "A deep dive into how to crack the CSAT and GS paper on your first attempt.", "2024-04-10", "5 min read", "https://via.placeholder.com/300x200/2563eb/ffffff?text=UPSC+Prelims", listOf("exam-strategies", "UPSC")),
            BlogPost("2", "Banking Exams Complete Guide", "Difference between IBPS PO and SBI Clerks and their syllabus breakdown.", "2024-04-05", "8 min read", "https://via.placeholder.com/300x200/f97316/ffffff?text=Banking", listOf("Banking", "exam-strategies")),
            BlogPost("3", "February Current Affairs Snapshot", "Key events that transpired last month affecting national policy.", "2024-04-01", "3 min read", "https://via.placeholder.com/300x200/10b981/ffffff?text=News", listOf("current-affairs", "News"))
        ))
    }

    val allTags = listOf("All", "exam-strategies", "UPSC", "Banking", "current-affairs", "News")

    fun getFilteredPosts(): List<BlogPost> {
        return posts.filter {
            val matchesSearch = it.title.contains(searchQuery.value, ignoreCase = true) || it.excerpt.contains(searchQuery.value, ignoreCase = true)
            val matchesTag = activeTag.value == "All" || it.tags.contains(activeTag.value)
            matchesSearch && matchesTag
        }
    }
}
