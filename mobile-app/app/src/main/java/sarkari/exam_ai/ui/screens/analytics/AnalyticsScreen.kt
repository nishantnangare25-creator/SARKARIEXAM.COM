package sarkari.exam_ai.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.theme.*

@Composable
fun AnalyticsScreen(navController: NavController, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- 1. Circular Meters (Image 4) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 16.dp)) {
                AnalyticsMeterCard("Accuracy", 0f, AccentRed, "Needs Focus")
                AnalyticsMeterCard("Consistency", 0.1f, AccentSaffron, "Needs Focus")
                AnalyticsMeterCard("Completion", 0.02f, AccentGreen, "Needs Focus")
            }
        }

        // --- 2. Subject-wise Performance ---
        item {
            SarkariCard(padding = 20.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subject-wise Performance", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(20.dp))
                SubjectProgressBar("Indian Polity", 0.85f, PrimaryBlue)
                SubjectProgressBar("History & Culture", 0.62f, AccentOrange)
                SubjectProgressBar("Geography", 0.74f, AccentGreen)
                SubjectProgressBar("Economy", 0.48f, AccentRed)
                SubjectProgressBar("Current Affairs", 0.91f, PrimaryBlue)
            }
        }

        // --- 3. Milestones achieved ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Milestones", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        SarkariBadge("8 ACHIEVED", BadgeVariant.Success)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    MilestoneItem(
                        icon = Icons.Default.TrendingUp,
                        title = "Foundation Builder",
                        subtitle = "Completed early preparation modules.",
                        tint = AccentGreen
                    )
                    MilestoneItem(
                        icon = Icons.Default.Schedule,
                        title = "Time Master",
                        subtitle = "Improved test completion speed by 15%.",
                        tint = PrimaryBlue
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tip Box
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("AI Pre-Tip", color = AccentSaffron, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                "Focus on Indian Polity this week. Based on your trend, it's your biggest growth opportunity.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 4. AI Deep Dive Card ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFFEFF6FF), Color(0xFFFEFCE8)))).padding(20.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Performance Deep Dive", style = MaterialTheme.typography.titleLarge, color = Color(0xFF1E3A8A))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Your preparation is currently focused on factual recall. To improve, try more analytical mock tests.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E3A8A).copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        SarkariButton(
                            text = "Get Full Report",
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            variant = ButtonVariant.Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsMeterCard(label: String, progress: Float, color: Color, status: String) {
    SarkariCard(padding = 20.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(
                        color = color.copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ErrorOutline, null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(status, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SubjectProgressBar(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun MilestoneItem(icon: ImageVector, title: String, subtitle: String, tint: Color) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}
