package com.sarkari.exam.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.viewmodels.AuthState
import com.sarkari.exam.ui.viewmodels.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val PrimaryBlue = Color(0xFF2F5BB7)
val AccentOrange = Color(0xFFFF6A00)
val BackgroundWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF111827)
val TextMuted = Color(0xFF6B7280)
val InputBackground = Color(0xFFF9FAFB)
val BorderColor = Color(0xFFE5E7EB)

enum class AuthMode {
    LOGIN, SIGNUP
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
            viewModel.resetState()
        }
    }
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Branding Section
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.8f, animationSpec = tween(800))
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer { translationY = floatAnim }
                    .clip(RoundedCornerShape(24.dp))
                    .background(BackgroundWhite)
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier
                            .size(42.dp)
                            .offset(x = (-6).dp, y = (-6).dp)
                    )
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier
                            .size(30.dp)
                            .offset(x = 10.dp, y = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(800, delayMillis = 200))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SarkariExamAI",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextDark
                )

                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = InputBackground
                ) {
                    Text(
                        text = "Smart Preparation with AI",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = if (authMode == AuthMode.LOGIN) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = PrimaryBlue,
            divider = { HorizontalDivider(color = BorderColor) },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (authMode == AuthMode.LOGIN) 0 else 1]),
                    color = PrimaryBlue,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = authMode == AuthMode.LOGIN,
                onClick = { authMode = AuthMode.LOGIN },
                text = { Text("Login", fontWeight = FontWeight.Bold) },
                selectedContentColor = PrimaryBlue,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = authMode == AuthMode.SIGNUP,
                onClick = { authMode = AuthMode.SIGNUP },
                text = { Text("Sign Up", fontWeight = FontWeight.Bold) },
                selectedContentColor = PrimaryBlue,
                unselectedContentColor = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input Fields
        AnimatedContent(
            targetState = authMode,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "auth_form"
        ) { mode ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (mode == AuthMode.SIGNUP) {
                    AuthTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Enter Full Name",
                        leadingIcon = Icons.Default.Person
                    )
                }

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter Email or Mobile",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = if (mode == AuthMode.LOGIN) "Password" else "Create Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )

                if (mode == AuthMode.SIGNUP) {
                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm Password",
                        leadingIcon = Icons.Default.LockReset,
                        isPassword = true,
                        keyboardType = KeyboardType.Password
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options Row (Remember Me / Forgot Password for Login, Terms for Signup)
        AnimatedContent(
            targetState = authMode,
            label = "auth_options"
        ) { mode ->
            if (mode == AuthMode.LOGIN) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(x = (-12).dp)) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                        )
                        Text(
                            text = "Remember Me", 
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = TextDark
                        )
                    }
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        ),
                        modifier = Modifier.clickable { /* Handle forgot password */ }
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue),
                        modifier = Modifier.offset(x = (-12).dp)
                    )
                    
                    val annotatedText = buildAnnotatedString {
                        append("I agree to ")
                        withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) {
                            append("Terms")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(color = PrimaryBlue, fontWeight = FontWeight.Bold)) {
                            append("Privacy Policy")
                        }
                    }
                    
                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.clickable { /* Handle Terms Click */ }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Primary Button
        GradientButton(
            text = if (authMode == AuthMode.LOGIN) "Sign In" else "Create Account",
            isLoading = isLoading,
            onClick = {
                if (authMode == AuthMode.LOGIN) {
                    viewModel.login(email, password)
                } else {
                    if (password == confirmPassword && agreeToTerms) {
                        viewModel.signup(fullName, email, password)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OR Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            Text(
                text = "OR",
                color = TextMuted,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Social Auth
        SocialAuthButton(
            text = if (authMode == AuthMode.LOGIN) "Continue with Google" else "Sign up with Google",
            onClick = { /* Handle Google Auth */ }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = TextMuted) },
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = TextMuted)
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InputBackground),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = BorderColor,
            cursorColor = PrimaryBlue,
            containerColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "button_scale")

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryBlue, AccentOrange)
                )
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun SocialAuthButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle, // Placeholder for Google Icon
                contentDescription = "Google",
                tint = Color(0xFFDB4437),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )
        }
    }
}
