package com.sarkari.exam.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.SettingsViewModel
import com.sarkari.exam.ui.viewmodels.UserSettingsProfile

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val pushNotifications by viewModel.pushNotificationsEnabled.collectAsState()
    val examAlerts by viewModel.examAlertsEnabled.collectAsState()
    val darkMode by viewModel.darkModeEnabled.collectAsState()
    val language by viewModel.selectedLanguage.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Settings ⚙️", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // User Card
            item {
                UserProfileCard(userProfile)
            }

            // Account Section
            item {
                SettingsSection(title = "Account") {
                    SettingsItem(icon = Icons.Outlined.Person, title = "Edit Profile", subtitle = "Update your personal details")
                    SettingsItem(icon = Icons.Outlined.Lock, title = "Change Password", subtitle = "Secure your account")
                    SettingsItem(icon = Icons.Outlined.StarBorder, title = "Manage Subscription", subtitle = "Premium plan details")
                }
            }

            // Notifications Section
            item {
                SettingsSection(title = "Notifications") {
                    SettingsToggleItem(
                        icon = Icons.Outlined.NotificationsActive, 
                        title = "Push Notifications", 
                        subtitle = "Receive daily updates", 
                        isChecked = pushNotifications,
                        onToggle = { viewModel.togglePushNotifications() }
                    )
                    SettingsToggleItem(
                        icon = Icons.Outlined.Campaign, 
                        title = "Exam Alerts", 
                        subtitle = "Get notified about exam dates", 
                        isChecked = examAlerts,
                        onToggle = { viewModel.toggleExamAlerts() }
                    )
                }
            }

            // Preferences Section
            item {
                SettingsSection(title = "Preferences") {
                    SettingsToggleItem(
                        icon = Icons.Outlined.DarkMode, 
                        title = "Dark Mode", 
                        subtitle = "Switch to dark theme", 
                        isChecked = darkMode,
                        onToggle = { viewModel.toggleDarkMode() }
                    )
                    SettingsItem(
                        icon = Icons.Outlined.Language, 
                        title = "Language", 
                        subtitle = language,
                        onClick = { viewModel.setLanguage(if (language == "English") "Hindi" else "English") }
                    )
                }
            }

            // Privacy Section
            item {
                SettingsSection(title = "Privacy") {
                    SettingsItem(icon = Icons.Outlined.PrivacyTip, title = "Privacy Policy", subtitle = "Read our policies")
                    SettingsItem(icon = Icons.Outlined.Gavel, title = "Terms & Conditions", subtitle = "App usage terms")
                }
            }

            // Support Section
            item {
                SettingsSection(title = "Support") {
                    SettingsItem(icon = Icons.Outlined.HelpOutline, title = "Help Center", subtitle = "FAQs and guides")
                    SettingsItem(icon = Icons.Outlined.SupportAgent, title = "Contact Us", subtitle = "Reach out for support")
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = { /* Logout Action */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF0F0)) // Light Red
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun UserProfileCard(profile: UserSettingsProfile) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(AccentOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(profile.initials, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.name, fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = profile.subtitle, 
                        color = PrimaryBlue, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            IconButton(onClick = { /* Edit Profile */ }, modifier = Modifier.background(BackgroundLight, CircleShape)) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
        )
    }
}
