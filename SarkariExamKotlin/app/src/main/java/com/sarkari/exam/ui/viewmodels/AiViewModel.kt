package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.AiMessage
import com.sarkari.exam.data.api.AiOptions
import com.sarkari.exam.data.api.CloudflareAiRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    data class Success(val content: String) : AiState()
    data class Error(val message: String) : AiState()
}

class AiViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _aiState = MutableStateFlow<AiState>(AiState.Idle)
    val aiState: StateFlow<AiState> = _aiState.asStateFlow()

    fun generateContent(messages: List<AiMessage>, temperature: Double = 0.7) {
        viewModelScope.launch {
            _aiState.value = AiState.Loading
            try {
                val request = CloudflareAiRequest(
                    action = "ai",
                    messages = messages,
                    options = AiOptions(temperature = temperature)
                )
                
                val response = RetrofitClient.sarkariApiService.getAiCompletion(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.content != null) {
                        _aiState.value = AiState.Success(body.content)
                    } else if (body?.error != null) {
                        _aiState.value = AiState.Error(body.error)
                    } else {
                        _aiState.value = AiState.Error("Empty response from server")
                    }
                } else {
                    _aiState.value = AiState.Error("HTTP Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _aiState.value = AiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun resetState() {
        _aiState.value = AiState.Idle
    }
}
