package com.sarkari.exam.data.models

data class Language(
    val code: String,
    val name: String,
    val nativeName: String
) {
    companion object {
        val supportedLanguages = listOf(
            Language("en", "English", "English"),
            Language("hi", "Hindi", "हिंदी"),
            Language("mr", "Marathi", "मराठी"),
            Language("ta", "Tamil", "தமிழ்"),
            Language("te", "Telugu", "తెలుగు"),
            Language("kn", "Kannada", "ಕನ್ನಡ"),
            Language("ml", "Malayalam", "മലയാളം"),
            Language("bn", "Bengali", "বাংলা"),
            Language("gu", "Gujarati", "ગુજરાતી"),
            Language("pa", "Punjabi", "ਪੰਜਾਬੀ"),
            Language("ur", "Urdu", "اردو"),
            Language("or", "Odia", "ଓଡ଼ିଆ"),
            Language("as", "Assamese", "অসমীয়া")
        )
        
        fun getLanguageByCode(code: String): Language {
            return supportedLanguages.find { it.code == code } ?: supportedLanguages.first()
        }
    }
}
