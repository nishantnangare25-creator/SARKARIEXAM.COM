package com.sarkari.exam.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SarkariSettings", Context.MODE_PRIVATE)
    
    var customKey by remember { mutableStateOf(sharedPref.getString("custom_api_key", "") ?: "") }
    var keySaved by remember { mutableStateOf(false) }

    fun saveKey() {
        if (customKey.isNotBlank()) {
            sharedPref.edit().putString("custom_api_key", customKey.trim()).apply()
        } else {
            sharedPref.edit().remove("custom_api_key").apply()
        }
        keySaved = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
            )
        }
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingVals)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Profile Management Card
            item {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Profile Management", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Aspirant", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                                Text("user@example.com", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4DEF4444))
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }

            // BYOK Developer Section
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp), 
                    color = Color.White, 
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4DA855F7)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Custom API Key (BYOK)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }
                        Text("If our servers are busy, you can use your own free Google Gemini API Key to bypass rate limits.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))
                        
                        OutlinedTextField(
                            value = customKey,
                            onValueChange = { customKey = it; keySaved = false },
                            placeholder = { Text("Paste AIzaSy...") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Get Free Key", color = Color(0xFF2563EB), fontSize = 14.sp, modifier = Modifier.clickable { /* open browser */ })
                            Button(onClick = { saveKey() }) {
                                Text(if (keySaved) "Saved!" else "Save Key")
                            }
                        }
                    }
                }
            }

            // Information Section
            item {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Information", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }

                        SettingsLinkItem("About Us", Icons.Default.Info, Color(0xFF0EA5E9), Color(0x1A0EA5E9)) {}
                        Spacer(modifier = Modifier.height(12.dp))
                        SettingsLinkItem("Privacy Policy", Icons.Default.Settings, Color(0xFFA855F7), Color(0x1AA855F7)) {}
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsLinkItem(title: String, icon: ImageVector, iconColor: Color, bgColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(bgColor), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(">", color = Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
