package sarkari.exam_ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sarkari.exam_ai.ui.theme.*

enum class ButtonVariant {
    Primary, CTA, Success, Danger, Secondary, Ghost, Outline
}

@Composable
fun SarkariButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false
) {
    val containerColor = when (variant) {
        ButtonVariant.Primary -> PrimaryBlue
        ButtonVariant.CTA -> AccentSaffron
        ButtonVariant.Success -> AccentGreen
        ButtonVariant.Danger -> AccentRed
        ButtonVariant.Secondary -> Color.White
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Outline -> Color.Transparent
    }

    val contentColor = when (variant) {
        ButtonVariant.Primary, ButtonVariant.CTA, ButtonVariant.Success, ButtonVariant.Danger -> Color.White
        ButtonVariant.Secondary -> TextPrimary
        ButtonVariant.Ghost, ButtonVariant.Outline -> PrimaryBlue
    }

    val border = if (variant == ButtonVariant.Outline) {
        androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue)
    } else if (variant == ButtonVariant.Secondary) {
        androidx.compose.foundation.BorderStroke(1.5.dp, BorderColor)
    } else null

    val isGradient = variant == ButtonVariant.Primary || variant == ButtonVariant.CTA || variant == ButtonVariant.Success || variant == ButtonVariant.Danger
    val gradientBrush = when (variant) {
        ButtonVariant.Primary -> GradientPrimary
        ButtonVariant.CTA -> GradientSaffron
        ButtonVariant.Success -> GradientGreen
        ButtonVariant.Danger -> GradientRed
        else -> null
    }

    val buttonModifier = if (isGradient && gradientBrush != null) {
        Modifier.background(brush = gradientBrush, shape = RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isGradient) Color.Transparent else containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = border,
        contentPadding = PaddingValues(0.dp) // Reset padding so gradient takes full space, we apply padding to inner box
    ) {
        Box(
            modifier = buttonModifier.fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
