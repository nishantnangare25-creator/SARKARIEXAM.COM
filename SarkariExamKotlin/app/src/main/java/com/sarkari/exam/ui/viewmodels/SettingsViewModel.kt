package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettingsProfile(
    val name: String = "Aarav Sharma",
    val subtitle: String = "Premium User",
    val initials: String = "AS"
)

class SettingsViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow(UserSettingsProfile())
    val userProfile: StateFlow<UserSettingsProfile> = _userProfile.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    private val _examAlertsEnabled = MutableStateFlow(true)
    val examAlertsEnabled: StateFlow<Boolean> = _examAlertsEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun togglePushNotifications() {
        _pushNotificationsEnabled.value = !_pushNotificationsEnabled.value
    }

    fun toggleExamAlerts() {
        _examAlertsEnabled.value = !_examAlertsEnabled.value
    }

    fun toggleDarkMode() {
        _darkModeEnabled.value = !_darkModeEnabled.value
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }
}
