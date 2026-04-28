package com.sarkari.exam.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas

data class AiTool(val title: String, val subtitle: String, val icon: ImageVector, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsDashboard(
    onNavigateToRiya: () -> Unit = {},
    onNavigateToNotes: () -> Unit = {}
) {
    val tools = listOf(
        AiTool("Notes Generator", "AI-powered notes", Icons.Default.Description, Color(0xFF3B82F6)),
        AiTool("Study Planner", "Custom schedule", Icons.Default.DateRange, Color(0xFF8B5CF6)),
        AiTool("Paper Analyzer", "PYQ Trends", Icons.Default.BarChart, Color(0xFF10B981)),
        AiTool("PYQ Generator", "Practice papers", Icons.Default.AutoAwesome, Color(0xFFF59E0B)),
        AiTool("Weak Topics", "Analysis & fix", Icons.Default.QueryStats, Color(0xFFEF4444)),
        AiTool("AI Mock Test", "Smart evaluation", Icons.Default.FactCheck, Color(0xFF06B6D4)),
        AiTool("Flashcards", "Quick revision", Icons.Default.Style, Color(0xFFEC4899)),
        AiTool("Revision Booster", "Daily targets", Icons.Default.RocketLaunch, Color(0xFFF43F5E))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Dashboard", 
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ) 
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Search, contentDescription = null, tint = TextPrimary) }
                    IconButton(onClick = { }) { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TextPrimary) }
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Featured Card
            FeaturedAiCard(onTryNow = onNavigateToNotes)

            Spacer(modifier = Modifier.height(24.dp))

            // AI Tools Grid
            Text(
                text = "All AI Utilities",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Note: LazyVerticalGrid doesn't work well inside a verticalScroll Column directly.
            // Using a custom layout for the grid here.
            AiToolsGrid(
                tools = tools,
                onNavigateToNotes = onNavigateToNotes,
                onNavigateToRiya = onNavigateToRiya
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Performance Card
            PerformanceCard(score = 0.72f)

            Spacer(modifier = Modifier.height(24.dp))

            // Smart Suggestion
            SmartSuggestionBanner()

            Spacer(modifier = Modifier.height(24.dp))

            // Weak Topics Mini Panel
            WeakTopicsPanel()
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FeaturedAiCard(onTryNow: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryBlue, Color(0xFF1D4ED8))
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Generate Notes from\nPYQ / PDF instantly",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BackgroundWhite,
                    lineHeight = 32.sp
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onTryNow,
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Now", color = PrimaryBlue, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            }
        }
        Icon(
            imageVector = Icons.Default.AutoFixHigh,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp),
            tint = BackgroundWhite.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun AiToolsGrid(
    tools: List<AiTool>,
    onNavigateToNotes: () -> Unit,
    onNavigateToRiya: () -> Unit
) {
    Column {
        for (i in tools.indices step 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ToolItem(
                    tool = tools[i], 
                    modifier = Modifier.weight(1f),
                    onClick = { if (tools[i].title == "Notes Generator") onNavigateToNotes() }
                )
                if (i + 1 < tools.size) {
                    ToolItem(
                        tool = tools[i + 1], 
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ToolItem(tool: AiTool, modifier: Modifier, onClick: () -> Unit = {}) {
    Surface(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = BackgroundWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(tool.color.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, contentDescription = null, tint = tool.color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = tool.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary)
            Text(text = tool.subtitle, fontSize = 12.sp, color = TextMuted)
        }
    }
}

@Composable
fun PerformanceCard(score: Float) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = BackgroundWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Your Performance", 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextPrimary
                )
                Text(
                    text = "${(score * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = SuccessGreen
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = score,
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = SuccessGreen,
                trackColor = BorderColor.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun SmartSuggestionBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFEF9C3).copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFFDE047), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = Color(0xFF854D0E), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Based on your weak topics \u2192",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF854D0E),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Focus on Modern History",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Start Practice", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun WeakTopicsPanel() {
    Column {
        Text(text = "Improve Weak Topics", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        WeakTopicItem("Quantitative Aptitude", 0.40f, Color(0xFFEF4444))
        Spacer(modifier = Modifier.height(8.dp))
        WeakTopicItem("Indian Polity", 0.55f, Color(0xFFF59E0B))
    }
}

@Composable
fun WeakTopicItem(subject: String, progress: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = subject, style = MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = Color(0xFFF1F5F9)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
