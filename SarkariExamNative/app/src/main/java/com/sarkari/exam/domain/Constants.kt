package com.sarkari.exam.domain

object Constants {
    val EXAMS = listOf(
        Exam("ssc_cgl", "SSC CGL", "🏛️"),
        Exam("upsc_cse", "UPSC CSE", "📚"),
        Exam("rrb_ntpc", "RRB NTPC", "🚂"),
        Exam("ibps_po", "IBPS PO", "💼")
    )

    val LANGUAGES = listOf(
        Language("en", "English", "English"),
        Language("hi", "हिन्दी", "Hindi"),
        Language("mr", "मराठी", "Marathi"),
        Language("gu", "ગુજરાતી", "Gujarati")
    )

    val PREP_LEVELS = listOf("beginner", "intermediate", "advanced")

    val SUBJECTS = mapOf(
        "ssc_cgl" to listOf("Quantitative Aptitude", "General Intelligence", "General English", "General Awareness"),
        "upsc_cse" to listOf("History", "Geography", "Polity", "Economy", "Environment", "Science & Tech"),
        "rrb_ntpc" to listOf("Mathematics", "General Intelligence", "General Awareness"),
        "ibps_po" to listOf("English Language", "Quantitative Aptitude", "Reasoning Ability", "Computer Aptitude", "Banking Awareness")
    )
}

data class Exam(val id: String, val name: String, val icon: String)
data class Language(val code: String, val nativeName: String, val name: String)
