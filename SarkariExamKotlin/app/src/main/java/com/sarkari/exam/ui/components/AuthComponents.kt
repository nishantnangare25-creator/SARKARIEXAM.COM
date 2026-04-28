package com.sarkari.exam.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = PrimaryBlue) },
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F8F9)),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = PrimaryBlue
        ),
        singleLine = true
    )
}

@Composable
fun GradientAuthButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryBlue, Color(0xFF3B82F6))
                )
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
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
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Google Icon Placeholder (Can use a real image if available)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Red.copy(alpha = 0.1f)), // Mock Google icon color
                contentAlignment = Alignment.Center
            ) {
                Text("G", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
