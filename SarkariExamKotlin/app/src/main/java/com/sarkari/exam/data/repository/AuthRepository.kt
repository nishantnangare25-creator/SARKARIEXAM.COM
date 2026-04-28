package com.sarkari.exam.data.repository

import android.content.Context
import com.sarkari.exam.data.local.AppPreferences
import com.sarkari.exam.data.firebase.FirebaseManager
import com.sarkari.exam.domain.models.UserProfile
import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context) {
    private val appPreferences = AppPreferences(context)
    private val firebaseManager = FirebaseManager()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseManager.auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                appPreferences.setLoggedIn(true)
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Login failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(name: String, email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseManager.auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                // Save user profile to Firestore
                val userProfile = UserProfile(
                    uid = user.uid,
                    displayName = name,
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
                firebaseManager.saveUserProfile(userProfile)
                
                appPreferences.setLoggedIn(true)
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Signup failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        firebaseManager.auth.signOut()
        appPreferences.setLoggedIn(false)
    }
}
