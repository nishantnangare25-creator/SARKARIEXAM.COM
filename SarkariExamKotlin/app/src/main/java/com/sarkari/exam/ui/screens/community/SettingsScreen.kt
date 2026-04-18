package com.sarkari.exam.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    
    var customKey by remember { mutableStateOf("") }
    var keySaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            
            // Profile Card
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Profile Management", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)).padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Brush.linearGradient(listOf(PrimaryBlue, AccentGreen)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Welcome Guest", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Login to save progress", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = { navController.navigate(Screen.Login.route) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Login / Sign Up", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BYOK Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentSaffron.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Custom API Key (BYOK)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        "If our servers are busy, you can use your own free Google Gemini API Key to bypass rate limits.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = customKey,
                        onValueChange = { customKey = it; keySaved = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("API Key (AIzaSy...)") },
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { keySaved = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
                    ) {
                        Text(if (keySaved) "Saved Locally" else "Save Key", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Legal Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(8.dp).size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("About Us", fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    Row(modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = AccentSaffron.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = AccentSaffron, modifier = Modifier.padding(8.dp).size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Privacy Policy", fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

