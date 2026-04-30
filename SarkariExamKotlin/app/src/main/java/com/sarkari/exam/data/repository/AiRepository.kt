package com.sarkari.exam.data.repository

import com.sarkari.exam.data.AppConstants
import com.sarkari.exam.data.network.GroqChatRequest
import com.sarkari.exam.data.network.GroqMessage
import com.sarkari.exam.data.network.RetrofitClient

class AiRepository {
    private val groqApi = RetrofitClient.groqApiService
    private val authHeader = "Bearer ${AppConstants.GROQ_API_KEY}"

    suspend fun sendChatMessage(
        conversationHistory: List<GroqMessage>,
        userMessage: String,
        examContext: String = "SSC CGL",
        subjectContext: String = "General"
    ): Result<String> {
        return try {
            val systemPrompt = GroqMessage(
                role = "system",
                content = "You are Riya, a friendly and expert AI study assistant for Indian government exam aspirants. " +
                        "The user is preparing for $examContext, focusing on $subjectContext. " +
                        "Always give concise, clear, helpful answers. Use simple Hindi-English (Hinglish) if the user prefers. " +
                        "For questions, provide step-by-step explanations. Keep responses focused and to the point."
            )

            val messages = mutableListOf(systemPrompt)
            messages.addAll(conversationHistory.takeLast(8)) // Keep last 8 messages for context
            messages.add(GroqMessage(role = "user", content = userMessage))

            val request = GroqChatRequest(
                model = "llama3-8b-8192",
                messages = messages
            )

            val response = groqApi.createChatCompletion(authHeader, request)
            val reply = response.choices.firstOrNull()?.message?.content
                ?: "Sorry, I could not generate a response."
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateNotes(
        topic: String,
        examContext: String = "SSC CGL",
        subjectContext: String = "General",
        includeFlashcards: Boolean = false,
        includePyqLink: Boolean = false
    ): Result<String> {
        return try {
            val prompt = buildString {
                append("Generate comprehensive, well-structured study notes for the following topic:\n\n")
                append("Topic: $topic\n")
                append("Exam: $examContext | Subject: $subjectContext\n\n")
                append("Format the notes as:\n")
                append("📌 Key Concepts (bullet points)\n")
                append("📖 Detailed Explanation\n")
                append("🔢 Important Facts & Figures\n")
                if (includeFlashcards) append("🗂️ Flashcard Q&A pairs at the end\n")
                if (includePyqLink) append("📝 Typical PYQ-style questions related to this topic\n")
                append("\nMake it easy to memorize and exam-focused.")
            }

            val messages = listOf(
                GroqMessage(role = "system", content = "You are an expert teacher creating high-quality study material for Indian competitive exams."),
                GroqMessage(role = "user", content = prompt)
            )

            val request = GroqChatRequest(model = "llama3-8b-8192", messages = messages)
            val response = groqApi.createChatCompletion(authHeader, request)
            val notes = response.choices.firstOrNull()?.message?.content
                ?: "Could not generate notes. Please try again."
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzePaper(
        questions: String,
        examContext: String = "SSC CGL",
        subjectContext: String = "General"
    ): Result<String> {
        return try {
            val prompt = """
                Analyze the following exam questions from $examContext ($subjectContext) paper:
                
                $questions
                
                Please provide:
                1. 📊 Topic-wise analysis (which topics appear most)
                2. 🎯 Difficulty breakdown (Easy/Medium/Hard percentages)
                3. 💡 Key patterns noticed
                4. 📈 Recommended focus areas for preparation
                5. 🤖 AI Insight for the aspirant
                
                Be concise and actionable.
            """.trimIndent()

            val messages = listOf(
                GroqMessage(role = "system", content = "You are an expert exam analyst for Indian competitive exams."),
                GroqMessage(role = "user", content = prompt)
            )

            val request = GroqChatRequest(model = "llama3-8b-8192", messages = messages)
            val response = groqApi.createChatCompletion(authHeader, request)
            val analysis = response.choices.firstOrNull()?.message?.content
                ?: "Could not analyze. Please try again."
            Result.success(analysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
