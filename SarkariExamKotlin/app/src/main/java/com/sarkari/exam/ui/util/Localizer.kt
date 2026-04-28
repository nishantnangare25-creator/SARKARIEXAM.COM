package com.sarkari.exam.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.viewmodels.UserViewModel

object Localizer {
    private val translations = mapOf(
        "en" to mapOf(
            "hello" to "Hello, Aspirant",
            "prepare_smarter" to "Prepare smarter with AI",
            "target_exam" to "TARGET EXAM",
            "mock_tests" to "Mock Tests",
            "ai_tools" to "AI Tools",
            "results" to "Results",
            "profile" to "Profile",
            "settings" to "Settings",
            "logout" to "Logout"
        ),
        "hi" to mapOf(
            "hello" to "नमस्ते, अभ्यर्थी",
            "prepare_smarter" to "AI के साथ स्मार्ट तैयारी करें",
            "target_exam" to "लक्ष्य परीक्षा",
            "mock_tests" to "मॉक टेस्ट",
            "ai_tools" to "AI टूल्स",
            "results" to "परिणाम",
            "profile" to "प्रोफ़ाइल",
            "settings" to "सेटिंग्स",
            "logout" to "लॉगआउट"
        ),
        "mr" to mapOf(
            "hello" to "नमस्कार, विद्यार्थी",
            "prepare_smarter" to "AI सह स्मार्ट तयारी करा",
            "target_exam" to "लक्ष्य परीक्षा",
            "mock_tests" to "मॉक टेस्ट",
            "ai_tools" to "AI टूल्स"
        ),
        "bn" to mapOf(
            "hello" to "হ্যালো, পরীক্ষার্থী",
            "prepare_smarter" to "AI এর সাথে স্মার্ট প্রস্তুতি নিন",
            "target_exam" to "লক্ষ্য পরীক্ষা"
        ),
        "ta" to mapOf(
            "hello" to "வணக்கம், தேர்வரே",
            "prepare_smarter" to "AI உடன் சிறந்த முறையில் தயாராகுங்கள்",
            "target_exam" to "இலக்கு தேர்வு"
        ),
        "te" to mapOf(
            "hello" to "నమస్కారం, అభ్యర్థి",
            "prepare_smarter" to "AI తో తెలివిగా సిద్ధమవ్వండి",
            "target_exam" to "లక్ష్య పరీక్ష"
        )
    )

    fun getString(key: String, lang: String): String {
        return translations[lang]?.get(key) ?: translations["en"]?.get(key) ?: key
    }
}

@Composable
fun stringResource(key: String, userViewModel: UserViewModel = viewModel()): String {
    val userProfile by userViewModel.userProfile.collectAsState()
    return Localizer.getString(key, userProfile.language)
}
