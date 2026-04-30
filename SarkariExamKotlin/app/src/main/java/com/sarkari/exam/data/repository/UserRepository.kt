package com.sarkari.exam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sarkari.exam.domain.models.UserProfile
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {
            val document = usersCollection.document(uid).get().await()
            if (document.exists()) {
                val profile = UserProfile(
                    uid = document.getString("uid") ?: "",
                    displayName = document.getString("displayName") ?: "",
                    email = document.getString("email") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L
                    // If you add exam, target, etc to Firebase UserProfile, fetch them here.
                )
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            usersCollection.document(userProfile.uid).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
