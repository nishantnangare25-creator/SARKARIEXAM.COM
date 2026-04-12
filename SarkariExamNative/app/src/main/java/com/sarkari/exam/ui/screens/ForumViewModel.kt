package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class ForumReply(
    val author: String,
    val content: String
)

data class ForumThread(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    var replies: MutableList<ForumReply>,
    val content: String,
    val createdAt: String
)

class ForumViewModel : ViewModel() {
    val activeCategory = mutableStateOf("all")
    val threads = mutableStateListOf<ForumThread>()
    val selectedThread = mutableStateOf<ForumThread?>(null)

    val newTitle = mutableStateOf("")
    val newContent = mutableStateOf("")
    val newCategory = mutableStateOf("general")
    val showNew = mutableStateOf(false)
    val replyText = mutableStateOf("")

    init {
        threads.addAll(listOf(
            ForumThread("1", "How to prepare for UPSC Prelims 2026?", "exam-strategies", "Rahul S.", mutableListOf(ForumReply("Priya M.", "Focus on NCERTs first, then move to standard reference books."), ForumReply("Amit K.", "Mock tests are key.")), "I'm starting my UPSC preparation. What's the best strategy for Prelims?", "2 hours ago"),
            ForumThread("2", "Budget 2026 Key Highlights for Exams", "current-affairs", "Sneha P.", mutableListOf(ForumReply("Vikram D.", "Focus on the fiscal deficit target.")), "Here are the key points from Budget 2026...", "5 hours ago"),
            ForumThread("3", "Difference between Article 14 and Article 19?", "doubt-solving", "Anjali R.", mutableListOf(), "Can someone explain the fundamental difference between Article 14 and 19?", "1 day ago")
        ))
    }

    fun getFilteredThreads(): List<ForumThread> {
        return if (activeCategory.value == "all") threads else threads.filter { it.category == activeCategory.value }
    }

    fun createPost(authorName: String = "Anonymous") {
        if (newTitle.value.isBlank()) return
        val newThread = ForumThread(
            id = System.currentTimeMillis().toString(),
            title = newTitle.value,
            content = newContent.value,
            category = newCategory.value,
            author = authorName,
            replies = mutableListOf(),
            createdAt = "Just now"
        )
        threads.add(0, newThread)
        showNew.value = false
        newTitle.value = ""
        newContent.value = ""
    }

    fun addReply(authorName: String = "Anonymous") {
        if (replyText.value.isBlank() || selectedThread.value == null) return
        val threadId = selectedThread.value!!.id
        val index = threads.indexOfFirst { it.id == threadId }
        
        if (index != -1) {
            val thread = threads[index]
            val newReplies = thread.replies.toMutableList()
            newReplies.add(ForumReply(authorName, replyText.value))
            
            val updatedThread = thread.copy(replies = newReplies)
            threads[index] = updatedThread
            selectedThread.value = updatedThread
            replyText.value = ""
        }
    }
}
