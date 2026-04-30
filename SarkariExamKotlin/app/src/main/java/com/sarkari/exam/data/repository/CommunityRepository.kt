package com.sarkari.exam.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sarkari.exam.ui.viewmodels.FeedPost
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CommunityRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("community_posts")

    suspend fun fetchPosts(exam: String): Result<List<FeedPost>> {
        return try {
            val snapshot = postsCollection
                .whereEqualTo("exam", exam)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                try {
                    FeedPost(
                        id = doc.id,
                        authorName = doc.getString("authorName") ?: "Unknown",
                        authorInitials = doc.getString("authorInitials") ?: "?",
                        timeAgo = "Recently",
                        content = doc.getString("content") ?: "",
                        likes = (doc.getLong("likes") ?: 0L).toInt(),
                        comments = (doc.getLong("comments") ?: 0L).toInt(),
                        shares = (doc.getLong("shares") ?: 0L).toInt()
                    )
                } catch (e: Exception) { null }
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(postId: String, isLiked: Boolean): Result<Unit> {
        return try {
            val increment = if (isLiked) -1L else 1L
            postsCollection.document(postId)
                .update("likes", FieldValue.increment(increment))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(
        authorName: String,
        authorInitials: String,
        content: String,
        exam: String
    ): Result<Unit> {
        return try {
            val post = hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "authorName" to authorName,
                "authorInitials" to authorInitials,
                "content" to content,
                "exam" to exam,
                "likes" to 0L,
                "comments" to 0L,
                "shares" to 0L,
                "timestamp" to FieldValue.serverTimestamp()
            )
            postsCollection.add(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
