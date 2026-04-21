package sarkari.exam_ai.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import sarkari.exam_ai.domain.models.UserProfile
import kotlinx.coroutines.tasks.await

/**
 * FirebaseManager — Full Firestore integration
 * Mirrors web app's firebase.js:
 *   saveUserProfile, getUserProfile,
 *   saveTestResult, getTestHistory,
 *   saveStudyPlan, getStudyPlan,
 *   saveToGlobalCache, getFromGlobalCache
 */
class FirebaseManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ===== USER PROFILE =====

    suspend fun saveUserProfile(userProfile: UserProfile) {
        try {
            firestore.collection("users").document(userProfile.uid)
                .set(userProfile, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                UserProfile(
                    uid = document.getString("uid") ?: uid,
                    displayName = document.getString("displayName") ?: "",
                    email = document.getString("email") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ===== TEST HISTORY =====
    // Mirrors web app: saveTestResult(), getTestHistory()

    suspend fun saveTestResult(uid: String, exam: String, subject: String, score: Int, total: Int) {
        try {
            val result = mapOf(
                "uid" to uid,
                "exam" to exam,
                "subject" to subject,
                "score" to score,
                "total" to total,
                "percentage" to ((score.toDouble() / total) * 100).toInt(),
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            firestore.collection("testResults").add(result).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getTestHistory(uid: String): List<Map<String, Any>> {
        return try {
            val snap = firestore.collection("testResults")
                .whereEqualTo("uid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get().await()
            snap.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ===== STUDY PLAN =====

    suspend fun saveStudyPlan(uid: String, planContent: String) {
        try {
            firestore.collection("studyPlans").document(uid)
                .set(mapOf(
                    "plan" to planContent,
                    "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                ), SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getStudyPlan(uid: String): String? {
        return try {
            val snap = firestore.collection("studyPlans").document(uid).get().await()
            snap.getString("plan")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ===== AI GLOBAL CACHE =====
    // Same as web app: saves AI responses to Firestore so all users benefit

    suspend fun saveToGlobalCache(cacheKey: String, content: String) {
        try {
            val safeDocId = cacheKey.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(490)
            firestore.collection("ai_global_cache").document(safeDocId)
                .set(mapOf(
                    "content" to content,
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                ), SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getFromGlobalCache(cacheKey: String): String? {
        return try {
            val safeDocId = cacheKey.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(490)
            val snap = firestore.collection("ai_global_cache").document(safeDocId).get().await()
            snap.getString("content")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
