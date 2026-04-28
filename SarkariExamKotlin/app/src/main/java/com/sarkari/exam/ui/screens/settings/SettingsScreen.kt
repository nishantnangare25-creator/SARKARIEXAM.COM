package com.sarkari.exam.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var examAlerts by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Search, contentDescription = null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceGray)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // User Profile Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BackgroundWhite,
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SurfaceGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Aarav Sharma", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = AccentOrange.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "PREMIUM PLAN", color = AccentOrange, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).background(SurfaceGray, CircleShape)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Sections
            SettingsGroup(title = "ACCOUNT") {
                SettingsItem("Edit Profile", Icons.Outlined.Person)
                SettingsItem("Change Password", Icons.Outlined.Lock)
                SettingsItem("Manage Subscription", Icons.Outlined.CreditCard, rightText = "Active")
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsGroup(title = "NOTIFICATIONS") {
                SettingsToggle("Push Notifications", Icons.Outlined.Notifications, pushNotifications) { pushNotifications = it }
                SettingsToggle("Exam Alerts", Icons.Outlined.WarningAmber, examAlerts) { examAlerts = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsGroup(title = "PREFERENCES") {
                SettingsToggle("Dark Mode", Icons.Outlined.DarkMode, darkMode) { darkMode = it }
                SettingsItem("Language", Icons.Outlined.Language, rightText = "English")
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsGroup(title = "SUPPORT & LEGAL") {
                SettingsItem("Help Center", Icons.Outlined.HelpOutline)
                SettingsItem("Contact Us", Icons.Outlined.Email)
                SettingsItem("Privacy Policy", Icons.Outlined.Shield)
                SettingsItem("Terms & Conditions", Icons.Outlined.Gavel)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logout Session", color = ErrorRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SarkariExamAI v1.0.4 (Build 42)",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 11.sp,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
            color = TextMuted
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = BackgroundWhite,
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, icon: ImageVector, rightText: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
        if (rightText != null) {
            Text(text = rightText, color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BorderColor, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun SettingsToggle(title: String, icon: ImageVector, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundWhite, 
                checkedTrackColor = SuccessGreen,
                uncheckedThumbColor = BackgroundWhite,
                uncheckedTrackColor = BorderColor
            )
        )
    }
}
