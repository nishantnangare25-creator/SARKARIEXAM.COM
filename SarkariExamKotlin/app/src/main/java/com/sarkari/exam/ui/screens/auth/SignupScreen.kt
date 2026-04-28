package com.sarkari.exam.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.components.AuthTextField
import com.sarkari.exam.ui.components.GradientAuthButton
import com.sarkari.exam.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.viewmodels.AuthState
import com.sarkari.exam.ui.viewmodels.AuthViewModel

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var emailOrMobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignupSuccess()
            viewModel.resetState()
        }
    }

    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

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

        // Title Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create Account 🚀",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Join us and start your smart preparation",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Input Fields
        AuthTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = emailOrMobile,
            onValueChange = { emailOrMobile = it },
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

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            leadingIcon = Icons.Default.VerifiedUser,
            isPassword = true,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Create Account Button
        GradientAuthButton(
            text = "Create Account",
            onClick = { 
                viewModel.signup(fullName, emailOrMobile, password)
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Footer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Login",
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBlue,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
