package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.navigation.Screen
import sarkari.exam_ai.ui.theme.*
import sarkari.exam_ai.ui.viewmodels.SettingsViewModel
import sarkari.exam_ai.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userSettings by settingsViewModel.userSettings.collectAsState()
    
    var customKey by remember { mutableStateOf("") }
    var isKeySaved by remember { mutableStateOf(false) }

    val exams = listOf("UPSC Civil Services", "MPSC", "SSC CGL/CHSL", "Banking (IBPS/SBI)", "Railway (RRB)", "NDA", "State PSC")
    // All major Indian languages — AppCompatDelegate maps these to their BCP-47 codes
    val languages = listOf(
        "English", "Hindi", "Marathi", "Tamil", "Telugu",
        "Kannada", "Bengali", "Gujarati", "Punjabi"
    )

    var expandedExam by remember { mutableStateOf(false) }
    var expandedLang by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Section
        SarkariCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("ST", fontSize = 28.sp, fontWeight = FontWeight.Black, color = PrimaryBlue)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Student User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("student@example.com", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SarkariButton(
                    text = "Logout",
                    onClick = { /* Handle Logout */ },
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.fillMaxWidth(),
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = AccentRed) }
                )
            }
        }

        // App Preferences Section
        SarkariCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = PrimaryBlue)
                    Text("App Preferences", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                
                // Exam Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedExam,
                    onExpandedChange = { expandedExam = !expandedExam }
                ) {
                    OutlinedTextField(
                        value = userSettings.examCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Exam Target") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExam) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedExam,
                        onDismissRequest = { expandedExam = false }
                    ) {
                        exams.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    settingsViewModel.updateExamCategory(selectionOption)
                                    expandedExam = false
                                }
                            )
                        }
                    }
                }

                // Language Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedLang,
                    onExpandedChange = { expandedLang = !expandedLang }
                ) {
                    OutlinedTextField(
                        value = userSettings.language,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("App Language") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLang) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLang,
                        onDismissRequest = { expandedLang = false }
                    ) {
                        languages.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    settingsViewModel.updateLanguage(selectionOption)
                                    expandedLang = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // AI Developer Settings (BYOK)
        SarkariCard(
            borderColor = Color(0xFF8B5CF6).copy(alpha = 0.3f),
            borderWidth = 1.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFF8B5CF6))
                    Text("Custom API Key (BYOK)", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text(
                    "Use your own Google Gemini API key to bypass rate limits and get faster AI responses.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                
                SarkariTextField(
                    value = customKey,
                    onValueChange = { customKey = it },
                    label = "Paste your Gemini API Key",
                    placeholder = "AIzaSy...",
                    isError = false
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { /* Open Link */ }) {
                        Text("Get Free Key", fontSize = 12.sp, color = PrimaryBlue)
                    }
                    SarkariButton(
                        text = if (isKeySaved) "Saved!" else "Save Key",
                        onClick = { isKeySaved = true },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        // Info & Legal
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About Us",
                iconColor = Color(0xFF38BDF8),
                onClick = { navController.navigate(Screen.AboutUs.route) }
            )
            
            SettingsItem(
                icon = Icons.Default.Shield,
                title = "Privacy Policy",
                iconColor = Color(0xFF8B5CF6),
                onClick = { navController.navigate(Screen.PrivacyPolicy.route) }
            )
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, iconColor: Color, onClick: () -> Unit) {
    SarkariCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Surface(
                color = iconColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
