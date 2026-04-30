package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SubscriptionState {
    object Idle : SubscriptionState()
    object Loading : SubscriptionState()
    object Success : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
    object Active : SubscriptionState() // If user is already premium
}

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _subState = MutableStateFlow<SubscriptionState>(SubscriptionState.Idle)
    val subState: StateFlow<SubscriptionState> = _subState.asStateFlow()

    init {
        checkSubscriptionStatus()
    }

    fun checkSubscriptionStatus() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.getBoolean("isPremium") == true) {
                            _subState.value = SubscriptionState.Active
                        }
                    }
            } catch (e: Exception) {
                // Ignore error, keep state as Idle
            }
        }
    }

    fun startFreeTrial() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _subState.value = SubscriptionState.Error("Please login first to subscribe.")
            return
        }

        viewModelScope.launch {
            _subState.value = SubscriptionState.Loading
            
            // Simulate Payment Gateway processing delay
            delay(2000)
            
            try {
                val data = hashMapOf(
                    "isPremium" to true, 
                    "premiumStartDate" to System.currentTimeMillis()
                )
                
                // Update Firestore
                db.collection("users").document(userId)
                    .update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        _subState.value = SubscriptionState.Success
                    }
                    .addOnFailureListener {
                        // If document doesn't exist, set it
                        db.collection("users").document(userId).set(data)
                            .addOnSuccessListener { _subState.value = SubscriptionState.Success }
                            .addOnFailureListener { e -> _subState.value = SubscriptionState.Error(e.message ?: "Transaction failed") }
                    }
            } catch (e: Exception) {
                _subState.value = SubscriptionState.Error(e.message ?: "Failed to process subscription.")
            }
        }
    }

    fun resetState() {
        if (_subState.value !is SubscriptionState.Active) {
            _subState.value = SubscriptionState.Idle
        }
    }
}
