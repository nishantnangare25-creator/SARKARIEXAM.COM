package com.sarkari.exam.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hero Section mirroring web design
        Surface(
            modifier = Modifier.size(80.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Face, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isRegister) "Create your account" else "Welcome back",
            style = MaterialTheme.typography.displayLarge,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Join 1,00,000+ students preparing for excellence",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        // Fields
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline, 
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            trailingIcon = {
                val icon = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline, 
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                loading = true
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (isRegister) "Sign Up Free" else "Secure Login", 
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { isRegister = !isRegister }) {
            Text(
                text = if (isRegister) "Already using Sarkari AI? Log In" else "Don't have an account? Sign Up Free",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

