package sarkari.exam_ai.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// Primary
val PrimaryBlue = Color(0xFF1A56DB)
val PrimaryLight = Color(0xFF3B82F6)
val PrimaryDark = Color(0xFF1E3A8A)
val PrimaryBg = Color(0xFFEFF6FF)
val PrimaryGlow = Color(0x261A56DB) // ~15% opacity

// Accent Saffron
val AccentOrange = Color(0xFFEA6C10)
val AccentSaffron = Color(0xFFF59E0B)
val AccentSaffronLight = Color(0xFFFCD34D)
val BgAccentSaffron = Color(0x1AF59E0B)

// Accent Green
val AccentGreen = Color(0xFF059669)
val AccentGreenLight = Color(0xFF10B981)
val BgAccentGreen = Color(0x1A059669)

// Accent Red
val AccentRed = Color(0xFFDC2626)
val AccentRedLight = Color(0xFFEF4444)
val BgAccentRed = Color(0x1ADC2626)

// Supporting
val AccentPink = Color(0xFFDB2777)

// Backgrounds
val BgPrimary = Color(0xFFF8FAFD)
val BgSecondary = Color(0xFFFFFFFF)
val BgTertiary = Color(0xFFF1F5F9)

// Text
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val TextMuted = Color(0xFF9CA3AF)

// Borders
val BorderColor = Color(0xFFE5E7EB)
val BorderLight = Color(0xFFF3F4F6)
val BorderMedium = Color(0xFFD1D5DB)
val BorderBlue = Color(0x401A56DB) // ~25% opacity

// -- GRADIENTS --
val GradientPrimary = Brush.linearGradient(
    colors = listOf(Color(0xFF1A56DB), Color(0xFF3B82F6))
)
val GradientSaffron = Brush.linearGradient(
    colors = listOf(Color(0xFFEA6C10), Color(0xFFF59E0B))
)
val GradientGreen = Brush.linearGradient(
    colors = listOf(Color(0xFF059669), Color(0xFF10B981))
)
val GradientRed = Brush.linearGradient(
    colors = listOf(Color(0xFFDC2626), Color(0xFFEF4444))
)

// Legacy backward-compatibility overrides (already in file)
val SarkariRed = Color(0xFFE31E24)
val HeaderDark = Color(0xFF1E3A8A)
val SurfaceWhite = Color(0xFFFFFFFF)
val DividerGray = Color(0xFFE5E7EB)
val TextGray = Color(0xFF6B7280)
val LinkBlue = Color(0xFF1A56DB)
val BackgroundBody = BgPrimary
val SurfaceCard = BgSecondary
