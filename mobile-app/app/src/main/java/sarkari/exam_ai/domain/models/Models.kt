package sarkari.exam_ai.domain.models

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

data class StudyPlan(
    val examName: String,
    val generatedPlanContent: String,
    val timestamp: Long
)

data class TestResult(
    val id: String,
    val exam: String,
    val subject: String,
    val score: Int,
    val total: Int,
    val date: String
)

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val createdAt: Long
)
