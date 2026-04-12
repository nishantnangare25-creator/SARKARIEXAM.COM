package com.sarkari.exam.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser

    suspend fun loginWithEmail(email: String, parseword: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, parseword).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun registerWithEmail(email: String, parseword: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, parseword).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun logout() {
        auth.signOut()
    }
}
