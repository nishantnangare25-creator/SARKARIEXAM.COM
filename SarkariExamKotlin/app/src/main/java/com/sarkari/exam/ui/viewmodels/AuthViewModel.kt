package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(email, password).fold(
                onSuccess = { token -> _authState.value = AuthState.Success(token) },
                onFailure = { error -> _authState.value = AuthState.Error(error.message ?: "Login failed") }
            )
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.signup(name, email, password).fold(
                onSuccess = { token -> _authState.value = AuthState.Success(token) },
                onFailure = { error -> _authState.value = AuthState.Error(error.message ?: "Signup failed") }
            )
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
