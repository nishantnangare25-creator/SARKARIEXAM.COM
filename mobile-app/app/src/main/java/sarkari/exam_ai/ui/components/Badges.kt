package sarkari.exam_ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sarkari.exam_ai.ui.theme.*

enum class BadgeVariant {
    Primary, Green, Saffron, Red, Gray, Success
}

@Composable
fun SarkariBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.Primary,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (variant) {
        BadgeVariant.Primary -> PrimaryBlue.copy(alpha = 0.1f)
        BadgeVariant.Green -> AccentGreen.copy(alpha = 0.1f)
        BadgeVariant.Saffron -> AccentSaffron.copy(alpha = 0.1f)
        BadgeVariant.Red -> AccentRed.copy(alpha = 0.1f)
        BadgeVariant.Gray -> TextMuted.copy(alpha = 0.1f)
        BadgeVariant.Success -> AccentGreen.copy(alpha = 0.1f)
    }

    val textColor = when (variant) {
        BadgeVariant.Primary -> PrimaryBlue
        BadgeVariant.Green -> AccentGreen
        BadgeVariant.Saffron -> AccentOrange
        BadgeVariant.Red -> AccentRed
        BadgeVariant.Gray -> TextSecondary
        BadgeVariant.Success -> AccentGreen
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(100.dp),
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SarkariChip(
    text: String,
    isActive: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) PrimaryBlue.copy(alpha = 0.1f) else BackgroundBody
    val textColor = if (isActive) PrimaryBlue else TextSecondary
    val borderColor = if (isActive) PrimaryBlue.copy(alpha = 0.5f) else BorderColor

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
