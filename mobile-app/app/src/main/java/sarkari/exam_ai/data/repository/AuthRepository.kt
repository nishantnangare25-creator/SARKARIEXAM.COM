package sarkari.exam_ai.data.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import sarkari.exam_ai.BuildConfig
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository — Real Firebase Auth
 * Mirrors the web app's firebase.js auth functions:
 *   loginWithEmail, registerWithEmail, loginWithGoogle, logout, getUserProfile, saveUserProfile
 */
class AuthRepository(private val context: Context) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    // ===== EMAIL AUTH =====

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Login failed — user not found"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(friendlyAuthError(e.message)))
        }
    }

    suspend fun registerWithEmail(email: String, password: String, displayName: String = ""): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Signup failed"))
            
            // Save profile to Firestore (same as web app's registerWithEmail)
            val name = displayName.ifBlank { email.substringBefore("@") }
            saveUserProfile(user.uid, mapOf(
                "email" to user.email,
                "displayName" to name,
                "createdAt" to System.currentTimeMillis()
            ))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(friendlyAuthError(e.message)))
        }
    }

    // ===== GOOGLE SIGN-IN =====

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent = getGoogleSignInClient().signInIntent

    suspend fun firebaseAuthWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("Google sign-in failed"))
            
            // Create profile if doesn't exist (same as web app's loginWithGoogle)
            val existing = getUserProfile(user.uid)
            if (existing == null) {
                saveUserProfile(user.uid, mapOf(
                    "displayName" to (user.displayName ?: "Student"),
                    "email" to user.email,
                    "photoURL" to user.photoUrl?.toString(),
                    "createdAt" to System.currentTimeMillis()
                ))
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(friendlyAuthError(e.message)))
        }
    }

    // ===== LOGOUT =====

    fun logout() {
        auth.signOut()
        getGoogleSignInClient().signOut()
    }

    // ===== FIRESTORE USER PROFILE =====

    suspend fun saveUserProfile(uid: String, data: Map<String, Any?>) {
        try {
            db.collection("users").document(uid)
                .set(data.filterValues { it != null }, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserProfile(uid: String): Map<String, Any>? {
        return try {
            val snap = db.collection("users").document(uid).get().await()
            if (snap.exists()) snap.data else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ===== FRIENDLY ERROR MESSAGES =====

    private fun friendlyAuthError(msg: String?): String {
        return when {
            msg == null -> "Something went wrong. Please try again."
            "no user record" in msg || "user-not-found" in msg -> "No account found with this email. Please sign up first."
            "wrong-password" in msg || "invalid-credential" in msg -> "Incorrect password. Please try again."
            "email-already-in-use" in msg -> "This email is already registered. Please login instead."
            "weak-password" in msg -> "Password is too weak. Use at least 6 characters."
            "invalid-email" in msg -> "Please enter a valid email address."
            "network" in msg -> "Network error. Please check your internet connection."
            else -> msg
        }
    }
}
