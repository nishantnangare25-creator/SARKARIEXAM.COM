package com.sarkari.exam.ui.screens.test

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMockTestScreen(
    onBack: () -> Unit = {},
    onComplete: (score: Int) -> Unit = {}
) {
    var timeLeft by remember { mutableStateOf(600L) } // 10 minutes in seconds
    var currentQuestionIndex by remember { mutableStateOf(4) } // Mock starting at Q5
    val totalQuestions = 20
    var selectedOption by remember { mutableStateOf<Int?>(1) } // Mock B selected

    // Timer Logic
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            onComplete(15) // Auto submit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "10 Min Mock Test", 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 20.sp,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                actions = {
                    TimerPill(timeLeft = timeLeft)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SurfaceGray)
        ) {
            // Test Info Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "QUICK PRACTICE",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = PrimaryBlue
                    )
                }
                Text(
                    text = "$totalQuestions Questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            // Progress Bar with custom styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = PrimaryBlue,
                    trackColor = BorderColor.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                color = BackgroundWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Which of the following articles of the Constitution of India deals with the Right to Equality before Law?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 32.sp
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Options List
                    val options = listOf("Article 14", "Article 15", "Article 16", "Article 17")
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        options.forEachIndexed { index, option ->
                            OptionItem(
                                label = ('A' + index).toString(),
                                text = option,
                                isSelected = selectedOption == index,
                                onClick = { selectedOption = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // AI Helpers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(52.dp),
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Quick Hint", color = WarningOrange, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                        Surface(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(52.dp),
                            color = BackgroundWhite,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explain Answer", color = PrimaryBlue, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Bottom Navigation & Actions
            BottomSection(
                currentIdx = currentQuestionIndex,
                total = totalQuestions,
                onNext = { if (currentQuestionIndex < totalQuestions - 1) currentQuestionIndex++ }
            )
        }
    }
}

@Composable
fun TimerPill(timeLeft: Long) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeStr = String.format("%02d:%02d", minutes, seconds)
    
    val backgroundColor = when {
        timeLeft > 300 -> Color(0xFFECFDF5) // Greenish
        timeLeft > 60 -> Color(0xFFFFF7ED)  // Yellowish
        else -> Color(0xFFFEF2F2)           // Reddish
    }
    
    val contentColor = when {
        timeLeft > 300 -> Color(0xFF10B981)
        timeLeft > 60 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Surface(
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeStr,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
        }
    }
}

@Composable
fun OptionItem(label: String, text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) PrimaryBlue else BorderColor.copy(alpha = 0.5f)
    val bgColor = if (isSelected) PrimaryBlue.copy(alpha = 0.04f) else BackgroundWhite
    val labelBgColor = if (isSelected) PrimaryBlue else SurfaceGray
    val labelTextColor = if (isSelected) BackgroundWhite else TextMuted
    val textColor = if (isSelected) PrimaryBlue else TextPrimary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(labelBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, fontWeight = FontWeight.ExtraBold, color = labelTextColor, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BottomSection(currentIdx: Int, total: Int, onNext: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundWhite,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Question Dots
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(total) { index ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    index == currentIdx -> PrimaryBlue
                                    index < currentIdx -> SuccessGreen
                                    else -> SurfaceGray
                                }
                            )
                            .border(
                                width = if (index == currentIdx) 0.dp else 1.dp,
                                color = if (index == currentIdx) Color.Transparent else BorderColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = if (index <= currentIdx) BackgroundWhite else TextMuted,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(56.dp),
                    color = BackgroundWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Skip", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.5f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Next Question", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
