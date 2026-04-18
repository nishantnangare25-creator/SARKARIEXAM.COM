package com.sarkari.exam.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sarkari.exam.domain.models.UserProfile
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun saveUserProfile(userProfile: UserProfile) {
        try {
            firestore.collection("users").document(userProfile.uid)
                .set(userProfile).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                UserProfile(
                    uid = document.getString("uid") ?: "",
                    displayName = document.getString("displayName") ?: "",
                    email = document.getString("email") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

