package sarkari.exam_ai.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DiscussionPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "General",
    val authorName: String = "Anonymous",
    val authorId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class CommunityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableStateFlow<List<DiscussionPost>>(emptyList())
    val posts: StateFlow<List<DiscussionPost>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPosts()
    }

    fun fetchPosts(category: String = "All") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val query = if (category == "All") {
                    db.collection("discussions").orderBy("timestamp", Query.Direction.DESCENDING)
                } else {
                    db.collection("discussions")
                        .whereEqualTo("category", category)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                }

                val snapshot = query.get().await()
                val fetchedPosts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DiscussionPost::class.java)?.copy(id = doc.id)
                }
                _posts.value = fetchedPosts
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPost(title: String, content: String, category: String, authorName: String, authorId: String) {
        viewModelScope.launch {
            try {
                val newPost = DiscussionPost(
                    title = title,
                    content = content,
                    category = category,
                    authorName = authorName,
                    authorId = authorId,
                    timestamp = System.currentTimeMillis()
                )
                db.collection("discussions").add(newPost).await()
                fetchPosts() // Refresh
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
