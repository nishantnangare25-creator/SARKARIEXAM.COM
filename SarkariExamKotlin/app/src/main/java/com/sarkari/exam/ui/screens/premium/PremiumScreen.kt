package com.sarkari.exam.ui.screens.premium

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*

@Composable
fun PremiumScreen(
    onClose: () -> Unit = {},
    onStartTrial: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF1F5F9), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }

            TimerBadge(time = "02:15:30")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title Section
        Text(
            text = "Upgrade to Premium",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Unlock all AI-powered features",
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Main Offer Card
        Box(contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = PrimaryBlue.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(SurfaceGray, BackgroundWhite)
                        )
                    )
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SPECIAL OFFER",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = AccentOrange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₹99",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 64.sp
                        )
                    )
                    Text(
                        text = " /month",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SuccessGreen.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "50% OFF (was ₹199)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = SuccessGreen
                    )
                }
            }

            // Most Popular Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E3A8A),
                border = BorderStroke(1.5.dp, BackgroundWhite.copy(alpha = 0.4f)),
                modifier = Modifier.offset(y = 0.dp)
            ) {
                Text(
                    text = "MOST POPULAR",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BackgroundWhite
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Free Trial Row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
            color = BackgroundWhite
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "7 Days Free Trial 🎁",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Then ₹99/month. Cancel Anytime.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SecondaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Features List
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureItem(text = "Unlimited Mock Tests")
            FeatureItem(text = "AI Notes Generator")
            FeatureItem(text = "PYQ Analysis")
            FeatureItem(text = "No Ads")
        }

        Spacer(modifier = Modifier.height(48.dp))

        // CTA Section
        Button(
            onClick = onStartTrial,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Start 7-Day Free Trial",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = BackgroundWhite
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = BackgroundWhite)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Info
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(0.8f)
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SECURE PAYMENT 🔒",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = SuccessGreen
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(0.6f)
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(24.dp), tint = TextMuted)
                Text("UPI", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextMuted)
                Text("CARDS", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextMuted)
                Text("WALLET", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(SecondaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text, 
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), 
            color = TextPrimary
        )
    }
}

@Composable
fun TimerBadge(time: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Offer ends in $time",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFEF4444)
            )
        }
    }
}
