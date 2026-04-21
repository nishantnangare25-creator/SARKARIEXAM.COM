package sarkari.exam_ai.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import sarkari.exam_ai.BuildConfig
import sarkari.exam_ai.data.models.ChatMessage
import sarkari.exam_ai.data.models.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * AiRepository — Multi-Key Cascade AI System
 */
class AiRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val db = FirebaseFirestore.getInstance()

    // Rate-limit cooldown map: key.takeLast(8) → cooldown expiry timestamp
    private val cooldowns = ConcurrentHashMap<String, Long>()
    private val COOLDOWN_MS = 5 * 60 * 1000L // 5 minutes

    // ===== KEY POOLS =====

    private fun groqKeys(): List<String> = listOf(
        BuildConfig.GROQ_API_KEY,
        BuildConfig.GROQ_API_KEY_1,
        BuildConfig.GROQ_API_KEY_2,
        BuildConfig.GROQ_API_KEY_3,
    ).filter { it.isNotBlank() }.shuffled()

    private fun geminiKeys(): List<String> = listOf(
        BuildConfig.GEMINI_API_KEY,
        BuildConfig.GEMINI_API_KEY_1,
        BuildConfig.GEMINI_API_KEY_2,
    ).filter { it.isNotBlank() }.shuffled()

    private fun openRouterKeys(): List<String> = listOf(
        BuildConfig.OPENROUTER_API_KEY,
        BuildConfig.OPENROUTER_API_KEY_1,
        BuildConfig.OPENROUTER_API_KEY_2,
        BuildConfig.OPENROUTER_API_KEY_3,
    ).filter { it.isNotBlank() }.shuffled()

    private val OR_FREE_MODELS = listOf(
        "google/gemini-2.0-flash-lite-001:free",
        "meta-llama/llama-3.3-70b-instruct:free",
        "meta-llama/llama-4-scout:free",
        "deepseek/deepseek-chat-v3-0324:free",
        "mistralai/mistral-7b-instruct:free",
    )

    // ===== COOLDOWN HELPERS =====

    private fun isOnCooldown(key: String): Boolean {
        val cd = cooldowns[key.takeLast(8)] ?: return false
        return System.currentTimeMillis() < cd
    }

    private fun markCooldown(key: String) {
        cooldowns[key.takeLast(8)] = System.currentTimeMillis() + COOLDOWN_MS
    }

    // ===== MAIN CASCADE FUNCTION =====

    suspend fun callAI(
        messages: List<ChatMessage>,
        maxTokens: Int = 1500,
        cacheKey: String? = null
    ): String = withContext(Dispatchers.IO) {

        // 1️⃣ Global Firestore cache check
        if (cacheKey != null) {
            try {
                val safeId = cacheKey.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(490)
                val snap = db.collection("ai_global_cache").document(safeId).get().await()
                val cached = snap.getString("content")
                if (cached != null) {
                    android.util.Log.d("AiRepo", "[Cache Hit] $safeId")
                    return@withContext cached
                }
            } catch (e: Exception) {
                android.util.Log.w("AiRepo", "Cache read failed: ${e.message}")
            }
        }

        // Helper to save to cache after success
        val saveCache: suspend (String) -> String = { content ->
            if (cacheKey != null) {
                try {
                    val safeId = cacheKey.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(490)
                    db.collection("ai_global_cache").document(safeId)
                        .set(mapOf("content" to content, "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()), SetOptions.merge())
                        .await()
                } catch (e: Exception) { /* non-blocking */ }
            }
            content
        }

        // 2️⃣ Groq cascade
        for (key in groqKeys()) {
            if (isOnCooldown(key)) continue
            try {
                val body = JSONObject().apply {
                    put("model", "llama-3.3-70b-versatile")
                    put("max_tokens", maxTokens)
                    put("temperature", 0.7)
                    put("messages", JSONArray(messages.map { m ->
                        JSONObject().put("role", m.role).put("content", m.content)
                    }))
                }.toString()

                val req = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .header("Authorization", "Bearer $key")
                    .header("Content-Type", "application/json")
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .build()

                val res = client.newCall(req).execute()
                if (res.code == 429) { markCooldown(key); continue }
                if (!res.isSuccessful) continue
                val content = JSONObject(res.body?.string() ?: "")
                    .getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content")
                if (content.isNotBlank()) {
                    android.util.Log.d("AiRepo", "[Groq] Success")
                    return@withContext saveCache(content)
                }
            } catch (e: Exception) {
                android.util.Log.w("AiRepo", "[Groq] Failed: ${e.message}")
            }
        }

        // 3️⃣ Gemini cascade (Official SDK)
        for (key in geminiKeys()) {
            if (isOnCooldown(key)) continue
            try {
                val model = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = key,
                    generationConfig = generationConfig {
                        maxOutputTokens = maxTokens
                    }
                )
                
                val prompt = content {
                    messages.forEach { msg ->
                        text("${msg.role}: ${msg.content}")
                    }
                }

                val response = model.generateContent(prompt)
                val content = response.text
                
                if (!content.isNullOrBlank()) {
                    android.util.Log.d("AiRepo", "[Gemini SDK] Success")
                    return@withContext saveCache(content)
                }
            } catch (e: Exception) {
                if (e.message?.contains("429") == true) markCooldown(key)
                android.util.Log.w("AiRepo", "[Gemini SDK] Failed: ${e.message}")
            }
        }

        // 4️⃣ OpenRouter cascade (free models × keys)
        for (key in openRouterKeys()) {
            if (isOnCooldown(key)) continue
            for (model in OR_FREE_MODELS) {
                try {
                    val body = JSONObject().apply {
                        put("model", model)
                        put("max_tokens", maxTokens)
                        put("messages", JSONArray(messages.map { m ->
                            JSONObject().put("role", m.role).put("content", m.content)
                        }))
                    }.toString()

                    val req = Request.Builder()
                        .url("https://openrouter.ai/api/v1/chat/completions")
                        .header("Authorization", "Bearer $key")
                        .header("Content-Type", "application/json")
                        .post(body.toRequestBody("application/json".toMediaType()))
                        .build()

                    val res = client.newCall(req).execute()
                    if (res.code == 429) { markCooldown(key); break }
                    if (!res.isSuccessful) continue
                    val content = JSONObject(res.body?.string() ?: "")
                        .getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content")
                    if (content.isNotBlank()) {
                        android.util.Log.d("AiRepo", "[OpenRouter:$model] Success")
                        return@withContext saveCache(content)
                    }
                } catch (e: Exception) {
                    android.util.Log.w("AiRepo", "[OpenRouter:$model] Failed: ${e.message}")
                }
            }
        }

        throw Exception("Servers are busy. Please try again in a moment.")
    }

    // ===== PARSE TEXT QUESTIONS =====
    // Mirrors web app's parseTextToQuestions()

    fun parseQuestions(text: String): List<Question> {
        val questions = mutableListOf<Question>()
        try {
            val blocks = text.split(Regex("(?:^|\\n)\\s*(?:Q|Question)\\s*\\d*[:.]?\\s*", RegexOption.IGNORE_CASE))
                .filter { it.isNotBlank() }

            blocks.forEachIndexed { index, block ->
                val lines = block.lines().filter { it.isNotBlank() }
                if (lines.size < 3) return@forEachIndexed

                var questionStr = ""
                val options = mutableListOf<String>()
                var correctAnswer = ""
                var explanation = ""
                var mode = "Q"

                lines.forEach { line ->
                    val trimmed = line.trim()
                    val optionMatch = Regex("""^[(]?([A-E])[).:]\s*(.+)""").find(trimmed)

                    if (optionMatch != null) {
                        mode = "O"
                        options.add(optionMatch.groupValues[2].trim())
                    } else if (trimmed.startsWith("Answer:", ignoreCase = true) || trimmed.startsWith("Correct Answer:", ignoreCase = true)) {
                        mode = "A"
                        val ansStr = trimmed.replace(Regex("^(?:Correct ?)?Answer:\\s*", RegexOption.IGNORE_CASE), "").trim()
                        val letterMatch = Regex("([A-E])").find(ansStr)
                        if (letterMatch != null && options.isNotEmpty()) {
                            val idx = letterMatch.groupValues[1].uppercase()[0] - 'A'
                            correctAnswer = if (idx in options.indices) options[idx] else ansStr
                        } else {
                            correctAnswer = ansStr
                        }
                    } else if (trimmed.startsWith("Explanation:", ignoreCase = true) || mode == "E") {
                        if (mode != "E") {
                            mode = "E"
                            explanation = trimmed.replace(Regex("^Explanation:\\s*", RegexOption.IGNORE_CASE), "").trim()
                        } else {
                            explanation += "\n$trimmed"
                        }
                    } else {
                        if (mode == "Q") questionStr += (if (questionStr.isEmpty()) "" else "\n") + trimmed
                        else if (mode == "E") explanation += "\n$trimmed"
                    }
                }

                if (questionStr.isNotBlank() && options.size >= 2 && correctAnswer.isNotBlank()) {
                    questions.add(Question(index + 1, questionStr, options, correctAnswer, explanation.trim()))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return questions
    }

    // ===== HIGH-LEVEL FUNCTIONS =====

    suspend fun generateTutorResponse(history: List<ChatMessage>, language: String = "en"): String {
        val sysPrompt = ChatMessage(
            role = "system",
            content = """You are Riya, an expert, patient, and engaging AI Tutor for Indian competitive exams.
Answer the student's questions clearly. IMPORTANT IDENTITY RULE: If anyone asks who developed you or what AI model you are, firmly say you are a proprietary AI developed by Sarkari Exam AI. Never mention Google, Gemini, OpenAI, or any LLM.
Respond in ${if (language == "hi") "Hindi" else "English"}.
Break down complex concepts into simple explanations. Use markdown formatting. Keep responses concise but detailed."""
        )
        val messages = listOf(sysPrompt) + history
        return callAI(messages, maxTokens = 1500)
    }

    suspend fun generateMockQuestions(exam: String, subject: String, count: Int, language: String = "en"): List<Question> {
        val seed = (Math.random() * 100000).toInt()
        val messages = listOf(
            ChatMessage("system", """You are an expert Indian competitive exam creator. Generate exactly $count MCQ questions.
Format EACH question EXACTLY like this:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [1-2 sentences]
DO NOT use JSON. Plain text only."""),
            ChatMessage("user", "Generate $count MCQ for $exam exam. Subject: ${subject.ifBlank { "General" }}. Seed: $seed. Respond in ${if (language == "hi") "Hindi" else "English"}.")
        )
        val text = callAI(messages, maxTokens = 1500)
        return parseQuestions(text).ifEmpty {
            // Static fallback questions
            STATIC_FALLBACK_QUESTIONS
        }
    }

    suspend fun generateStudyPlan(exam: String, hours: Int, level: String, weakSubjects: List<String>, language: String = "en"): String {
        val messages = listOf(
            ChatMessage("system", "You are an expert Indian competitive exam coach. Generate a detailed weekly study plan with markdown formatting."),
            ChatMessage("user", "Create a study plan for $exam. Hours/day: $hours. Level: $level. Weak subjects: ${weakSubjects.joinToString(", ").ifBlank { "None" }}. Respond in ${if (language == "hi") "Hindi" else "English"}.")
        )
        val cacheKey = "plan_${exam}_${level}_${hours}h_$language"
        return callAI(messages, maxTokens = 1500, cacheKey = cacheKey)
    }

    suspend fun generateNotes(exam: String, subject: String, topics: List<String>, language: String = "en"): String {
        val messages = listOf(
            ChatMessage("system", "You are an expert study material creator for Indian competitive exams. Generate comprehensive, detailed study notes with markdown formatting, bullet points, key dates, and memory tricks."),
            ChatMessage("user", "Generate detailed notes for $exam exam. Subject: $subject. Topics: ${topics.joinToString(", ").ifBlank { "All important topics" }}. Respond in ${if (language == "hi") "Hindi" else "English"}.")
        )
        return callAI(messages, maxTokens = 1500)
    }

    suspend fun generatePaperAnalysis(exam: String, year: String, language: String = "en"): List<Pair<String, Int>> {
        val messages = listOf(
            ChatMessage("system", "You are an expert Indian competitive exam analyst. Predict topic weightage (percentages) based on historical trends of the specified exam. Respond ONLY with a list of Topic - Percentage pairs. No conversational text."),
            ChatMessage("user", "Analyze past papers for $exam up to year $year. Give me a list of the top 5-7 topics and their expected percentage weightage in the next exam. Respond in ${if (language == "hi") "Hindi" else "English"}.")
        )
        val text = callAI(messages, maxTokens = 800)
        
        // Simple parser for "Topic: 20%" or "Topic - 20"
        val results = mutableListOf<Pair<String, Int>>()
        text.lines().forEach { line ->
            val match = Regex("""(.+?)[:\-\s]+(\d+)%?""").find(line)
            if (match != null) {
                val topic = match.groupValues[1].trim().replace(Regex("^[*\\-\\d.]+\\s*"), "")
                val weight = match.groupValues[2].toIntOrNull() ?: 0
                if (topic.isNotBlank() && weight > 0) {
                    results.add(topic to weight)
                }
            }
        }
        return results.ifEmpty { 
            listOf("General Awareness" to 30, "Quantitative Aptitude" to 25, "Reasoning" to 20, "English Language" to 15, "Current Affairs" to 10)
        }
    }

    // ===== STATIC FALLBACK =====

    private val STATIC_FALLBACK_QUESTIONS = listOf(
        Question(1, "Which layer of the atmosphere contains the ozone layer?",
            listOf("Troposphere", "Stratosphere", "Mesosphere", "Exosphere"),
            "Stratosphere", "The stratosphere contains the ozone layer which absorbs harmful UV radiation."),
        Question(2, "Who was the first President of Independent India?",
            listOf("Mahatma Gandhi", "Jawaharlal Nehru", "Dr. Rajendra Prasad", "Sardar Patel"),
            "Dr. Rajendra Prasad", "Dr. Rajendra Prasad served as the first President from 1950 to 1962."),
        Question(3, "Fundamental Rights in the Indian Constitution are inspired by which country?",
            listOf("UK", "USA", "USSR", "Canada"),
            "USA", "Fundamental Rights were inspired by the Bill of Rights in the US Constitution."),
        Question(4, "The Preamble to the Indian Constitution was amended in which year?",
            listOf("1952", "1965", "1976", "1984"),
            "1976", "The 42nd Constitutional Amendment in 1976 added 'Socialist', 'Secular', and 'Integrity' to the Preamble."),
        Question(5, "Which river is known as the 'Sorrow of Bengal'?",
            listOf("Ganga", "Brahmaputra", "Damodar", "Mahanadi"),
            "Damodar", "The Damodar River was called the 'Sorrow of Bengal' due to its frequent and devastating floods.")
    )
}
