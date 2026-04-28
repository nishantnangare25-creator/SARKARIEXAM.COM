package com.sarkari.exam.ui.screens.analytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboard(
    onBack: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF5F6FA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Learning Insights", 
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Share, contentDescription = null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Title Section
            Column {
                Text(
                    text = "Performance Overview",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Detailed analysis of your progress.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Grid (2x2)
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Tests Done",
                        value = "42",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                        title = "Avg Score",
                        value = "76%",
                        icon = Icons.Default.Adjust,
                        modifier = Modifier.weight(1f),
                        color = SuccessGreen
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Hours",
                        value = "45.2",
                        icon = Icons.Default.Timer,
                        modifier = Modifier.weight(1f),
                        color = AccentPurple
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                        title = "Global Rank",
                        value = "#2540",
                        icon = Icons.Default.MilitaryTech,
                        modifier = Modifier.weight(1f),
                        color = AccentOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subject Performance Card
            AnalyticsCard(title = "Subject Performance", iconRight = Icons.Default.Schedule) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    PerformanceItem("Quantitative Aptitude", 0.78f, PrimaryBlue)
                    PerformanceItem("Logical Reasoning", 0.85f, SuccessGreen)
                    PerformanceItem("English Comprehension", 0.62f, WarningOrange)
                    PerformanceItem("General Awareness", 0.55f, ErrorRed)

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Detailed Report", color = PrimaryBlue, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Tests Card
            AnalyticsCard(title = "Recent Tests", textRight = "View All") {
                Column {
                    RecentTestItem("SSC CGL Mock 14", "145/200", "82%", "Today")
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                    RecentTestItem("Quantitative Sectional", "42/50", "90%", "Yesterday")
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                    RecentTestItem("English Vocab Mini", "15/20", "75%", "2 days ago")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Insights Card
            AiInsightCard(
                text = "Your accuracy in General Awareness has dropped by 12% this week. Focus on current affairs from the last 3 months.",
                onAction = { }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = BackgroundWhite,
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value, 
                fontWeight = FontWeight.Black, 
                fontSize = 22.sp, 
                color = TextPrimary
            )
            Text(
                text = title, 
                fontSize = 12.sp, 
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    iconRight: ImageVector? = null,
    textRight: String? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = BackgroundWhite,
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 18.sp, 
                    color = TextPrimary
                )
                if (iconRight != null) Icon(iconRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                if (textRight != null) Text(text = textRight, color = PrimaryBlue, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@Composable
fun PerformanceItem(subject: String, progress: Float, color: Color) {
    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) { animatedProgress = progress }
    val progressAnimate by animateFloatAsState(targetValue = animatedProgress, animationSpec = tween(1200, easing = FastOutSlowInEasing))

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = subject, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(text = "${(progress * 100).toInt()}%", fontWeight = FontWeight.Black, fontSize = 14.sp, color = color)
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = progressAnimate,
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = BorderColor.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun RecentTestItem(title: String, score: String, accuracy: String, date: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(SurfaceGray, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = TextMuted)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary)
            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = date, fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.size(4.dp).background(BorderColor, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Acc: $accuracy", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = score, fontWeight = FontWeight.Black, color = PrimaryBlue, fontSize = 17.sp)
    }
}

@Composable
fun AiInsightCard(text: String, onAction: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = PrimaryBlue.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BackgroundWhite, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "AI Smart Insights", fontWeight = FontWeight.ExtraBold, color = PrimaryBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp), 
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("IMPROVE NOW \u2192", fontWeight = FontWeight.ExtraBold, color = BackgroundWhite)
            }
        }
    }
}
