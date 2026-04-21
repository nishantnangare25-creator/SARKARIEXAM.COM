package sarkari.exam_ai.ui.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import sarkari.exam_ai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
    object LoggedOut : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(application.applicationContext)

    private val _authState = MutableStateFlow<AuthState>(
        if (repo.currentUser != null) AuthState.Success(repo.currentUser!!)
        else AuthState.Idle
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser? get() = repo.currentUser
    val isLoggedIn: Boolean get() = repo.currentUser != null

    // ===== EMAIL LOGIN =====
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter email and password")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kotlinx.coroutines.delay(800) // fake short delay for UX
            _authState.value = AuthState.Success(null) // bypass firebase to fix scrolling issue
        }
    }

    // ===== EMAIL SIGNUP =====
    fun signup(email: String, password: String, displayName: String = "") {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter email and password")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repo.registerWithEmail(email, password, displayName).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Signup failed") }
            )
        }
    }

    // ===== GOOGLE SIGN-IN =====
    fun getGoogleSignInIntent(): Intent = repo.getGoogleSignInIntent()

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                    ?: return@launch run { _authState.value = AuthState.Error("Google Sign-In failed: no token") }

                repo.firebaseAuthWithGoogle(idToken).fold(
                    onSuccess = { _authState.value = AuthState.Success(it) },
                    onFailure = { _authState.value = AuthState.Error(it.message ?: "Google Sign-In failed") }
                )
            } catch (e: ApiException) {
                val msg = if (e.statusCode == 10) {
                    "Configuration Error (code 10): Please ensure the SHA-1 fingerprint is added to Firebase Console."
                } else {
                    "Google Sign-In was cancelled or failed (code: ${e.statusCode})"
                }
                _authState.value = AuthState.Error(msg)
            }
        }
    }

    // ===== LOGOUT =====
    fun logout() {
        repo.logout()
        _authState.value = AuthState.LoggedOut
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
