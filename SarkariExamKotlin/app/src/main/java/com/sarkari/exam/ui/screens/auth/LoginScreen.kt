package com.sarkari.exam.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.components.AuthTextField
import com.sarkari.exam.ui.components.GradientAuthButton
import com.sarkari.exam.ui.components.SocialAuthButton
import com.sarkari.exam.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.viewmodels.AuthState
import com.sarkari.exam.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    // Animations
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Error Message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Logo Section
        Box(
            modifier = Modifier
                .size(110.dp)
                .graphicsLayer { translationY = floatAnim }
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryBlue, AccentPurple)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = BackgroundWhite,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SarkariExamAI",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            ),
            color = TextPrimary
        )

        Surface(
            modifier = Modifier.padding(top = 10.dp),
            shape = RoundedCornerShape(12.dp),
            color = PrimaryBlue.copy(alpha = 0.06f)
        ) {
            Text(
                text = "Next-Gen AI Preparation",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Welcome Text
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign in to continue your preparation",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input Fields
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email or Mobile Number",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Options Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                )
                Text(text = "Remember Me", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Forgot Password?",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                ),
                modifier = Modifier.clickable { /* Handle forgot password */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        GradientAuthButton(
            text = "Login",
            onClick = { 
                viewModel.login(email, password)
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "OR", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Google Login
        SocialAuthButton(
            text = "Continue with Google",
            onClick = { /* Handle Google Login */ }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Footer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don’t have an account? ", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Sign Up",
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBlue,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
