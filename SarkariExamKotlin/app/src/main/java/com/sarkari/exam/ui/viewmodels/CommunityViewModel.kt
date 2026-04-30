package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.sarkari.exam.data.repository.CommunityRepository

enum class CommunityTab { FEED, DISCUSSIONS, STUDY_PARTNERS }

data class FeedPost(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val authorInitials: String,
    val timeAgo: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val isLiked: Boolean = false
)

data class DiscussionTopic(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val repliesCount: Int,
    val lastActive: String
)

data class StudyPartner(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val initials: String,
    val targetExam: String,
    val isOnline: Boolean
)

class CommunityViewModel : ViewModel() {

    private val communityRepository = CommunityRepository()
    // Dynamic Options
    val examsList = listOf("SSC CGL", "UPSC Civil Services", "Banking", "Railway")
    private val subjectMap = mapOf(
        "SSC CGL" to listOf("All Subjects", "Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("All Subjects", "History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking" to listOf("All Subjects", "Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway" to listOf("All Subjects", "Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All Subjects")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Tabs
    private val _currentTab = MutableStateFlow(CommunityTab.FEED)
    val currentTab: StateFlow<CommunityTab> = _currentTab.asStateFlow()

    // Data States
    private val _feedPosts = MutableStateFlow<List<FeedPost>>(emptyList())
    val feedPosts: StateFlow<List<FeedPost>> = _feedPosts.asStateFlow()

    private val _discussions = MutableStateFlow<List<DiscussionTopic>>(emptyList())
    val discussions: StateFlow<List<DiscussionTopic>> = _discussions.asStateFlow()

    private val _studyPartners = MutableStateFlow<List<StudyPartner>>(emptyList())
    val studyPartners: StateFlow<List<StudyPartner>> = _studyPartners.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun setTab(tab: CommunityTab) {
        _currentTab.value = tab
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: listOf("All Subjects")
        _availableSubjects.value = subjects
        _selectedSubject.value = "All Subjects"
        loadData()
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        loadData()
    }

    fun toggleLike(postId: String) {
        // Optimistic UI update first
        val currentPost = _feedPosts.value.find { it.id == postId } ?: return
        val newIsLiked = !currentPost.isLiked
        _feedPosts.value = _feedPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(isLiked = newIsLiked, likes = if (newIsLiked) post.likes + 1 else post.likes - 1)
            } else { post }
        }
        // Sync with Firestore
        viewModelScope.launch {
            communityRepository.toggleLike(postId, currentPost.isLiked).onFailure {
                // Revert on failure
                _feedPosts.value = _feedPosts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(isLiked = currentPost.isLiked, likes = currentPost.likes)
                    } else { post }
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentExam = _selectedExam.value

            // Fetch real posts from Firestore
            communityRepository.fetchPosts(currentExam)
                .onSuccess { posts ->
                    // If Firestore has data, use it; else show local fallback
                    if (posts.isNotEmpty()) {
                        _feedPosts.value = posts
                    } else {
                        // Fallback seeded data if collection is empty
                        _feedPosts.value = listOf(
                            FeedPost(
                                authorName = "Ramesh Kumar", authorInitials = "RK",
                                timeAgo = "2h ago",
                                content = "Does anyone have short notes for modern history relevant for $currentExam?",
                                likes = 45, comments = 12, shares = 3
                            ),
                            FeedPost(
                                authorName = "Sneha Sharma", authorInitials = "SS",
                                timeAgo = "5h ago",
                                content = "Just scored 145/200 in the latest mock test! What's your target score for Tier 1?",
                                likes = 120, comments = 34, shares = 5, isLiked = true
                            )
                        )
                    }
                }
                .onFailure {
                    // Keep showing empty or previous data on error
                }

            // Discussions (mock — can be replaced with Firestore later)
            _discussions.value = listOf(
                DiscussionTopic(title = "Best books for $currentExam Quant?", repliesCount = 89, lastActive = "10m ago"),
                DiscussionTopic(title = "How to manage time during the exam?", repliesCount = 45, lastActive = "1h ago"),
                DiscussionTopic(title = "Expected cutoff for this year?", repliesCount = 156, lastActive = "Just now")
            )

            // Study Partners (mock — can be replaced with Firestore later)
            _studyPartners.value = listOf(
                StudyPartner(name = "Amit Singh", initials = "AS", targetExam = currentExam, isOnline = true),
                StudyPartner(name = "Priya Das", initials = "PD", targetExam = currentExam, isOnline = false),
                StudyPartner(name = "Rahul Verma", initials = "RV", targetExam = currentExam, isOnline = true)
            )

            _isLoading.value = false
        }
    }
}
