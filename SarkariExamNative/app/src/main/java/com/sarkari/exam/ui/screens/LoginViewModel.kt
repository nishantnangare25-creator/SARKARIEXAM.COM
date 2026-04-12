package com.sarkari.exam.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isRegister = mutableStateOf(false)
    val isRegister: State<Boolean> = _isRegister

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun toggleMode() {
        _isRegister.value = !_isRegister.value
        _error.value = null
    }

    fun submit() {
        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _error.value = "Please fill in all fields"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                if (_isRegister.value) {
                    authRepository.registerWithEmail(_email.value, _password.value)
                } else {
                    authRepository.loginWithEmail(_email.value, _password.value)
                }
                _isSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Authentication failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loginWithGoogle() {
        // TODO: Implement Google Sign-In SDK Native flow
        _error.value = "Google Login requires Native configurations."
    }
}
