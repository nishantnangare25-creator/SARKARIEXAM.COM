package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.navigation.Screen
import sarkari.exam_ai.ui.theme.*
import sarkari.exam_ai.ui.viewmodels.*
import sarkari.exam_ai.R
import androidx.compose.ui.res.stringResource

@Composable
fun DashboardScreen(
    navController: NavController, 
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel = viewModel(),
    contentViewModel: ContentViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser = authViewModel.currentUser
    val currentAffairsState by contentViewModel.currentAffairsState.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    val calendar = java.util.Calendar.getInstance()
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val greetingText = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- 1. Greeting Area ---
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Surface(
                    color = PrimaryBg,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_overview),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryBlue
                    )
                }
                val greeting = when (hour) {
                    in 0..11 -> stringResource(R.string.greeting_morning)
                    in 12..16 -> stringResource(R.string.greeting_afternoon)
                    else -> stringResource(R.string.greeting_evening)
                }
                Text(
                    text = "$greeting, ${currentUser?.displayName ?: "Aspirant"}! 👋",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // --- 2. Streak & Readiness Stats ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill("🔥 12 Days", "Daily Streak") // Replace with real streak from userProfile when available
                StatPill("📈 ${userProfile.level.replaceFirstChar { it.uppercase() }}", "Level")
            }
        }

        // --- 3. Action Cards ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FeatureActionCard(
                    title = stringResource(R.string.feat_mock_test),
                    subtitle = stringResource(R.string.feat_mock_subtitle),
                    icon = Icons.Default.Psychology,
                    tint = PrimaryBlue,
                    onClick = { navController.navigate(Screen.MockTest.route) }
                )
                FeatureActionCard(
                    title = stringResource(R.string.feat_pyq_library),
                    subtitle = stringResource(R.string.feat_pyq_subtitle),
                    icon = Icons.Default.MenuBook,
                    tint = AccentSaffron,
                    onClick = { navController.navigate(Screen.PyqLibrary.route) }
                )
                FeatureActionCard(
                    title = stringResource(R.string.feat_pyq_practice),
                    subtitle = stringResource(R.string.feat_pyq_practice_subtitle),
                    icon = Icons.Default.TrackChanges,
                    tint = AccentRed,
                    onClick = { navController.navigate(Screen.MockTest.route) }
                )
                FeatureActionCard(
                    title = stringResource(R.string.feat_ai_tutor),
                    subtitle = stringResource(R.string.feat_ai_tutor_subtitle),
                    icon = Icons.Default.AutoAwesome,
                    tint = AccentGreen,
                    onClick = { navController.navigate(Screen.Tutor.route) }
                )
            }
        }

        // --- 4. Performance Analytics ---
        item {
            SarkariCard(padding = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.performance_analytics), style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        stringResource(R.string.view_details),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        modifier = Modifier.clickable { navController.navigate(Screen.Analytics.route) }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                PerformanceLine("History", 0f)
                PerformanceLine("Geography", 0f)
                PerformanceLine("Polity", 0f)
                PerformanceLine("Current Affairs", 0f)
            }
        }

        // --- 5. Smart Suggestions ---
        item {
            SarkariCard(padding = 20.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.smart_suggestions), style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionPill("Take a full mock test")
                    SuggestionPill("New update in Current Affairs")
                }
            }
        }

        // --- 6. AI Engine Status ---
        item {
            SarkariCard(
                padding = 20.dp,
                borderColor = AccentGreen.copy(alpha = 0.3f),
                backgroundColor = Color(0xFFF0FDF4) // Light success bg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudQueue, null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI High-Capacity Engine", color = AccentGreen, fontWeight = FontWeight.Bold)
                    }
                    SarkariBadge("SCALABLE MODE", BadgeVariant.Success)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Available Slots:", color = TextSecondary, fontSize = 12.sp)
                    Text("11 Slots Active", color = AccentGreen, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.8f,
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = AccentGreen,
                    trackColor = AccentGreen.copy(alpha = 0.1f)
                )
            }
        }

        // --- 7. Daily Challenge ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.background(GradientPrimary).padding(20.dp)) {
                    Column {
                        Text("DAILY CHALLENGE", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Constitution of India Special Quiz", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(Screen.MockTest.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentSaffron),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Challenge Now", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 8. Current Affairs Section ---
        item {
            SarkariCard(padding = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Newspaper, null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.current_affairs), style = MaterialTheme.typography.titleMedium)
                    }
                    SarkariBadge("LATEST UPDATES", BadgeVariant.Saffron)
                }
                Spacer(modifier = Modifier.height(16.dp))
                when (val state = currentAffairsState) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                    is UiState.Success -> {
                        state.data.take(3).forEach { item ->
                            CurrentAffairsItem(item.date, item.title)
                        }
                        TextButton(
                            onClick = { navController.navigate(Screen.CurrentAffairs.route) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.view_all_affairs), color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    is UiState.Error -> {
                        Text("Failed to load updates", color = AccentRed, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatPill(value: String, label: String) {
    SarkariCard(
        modifier = Modifier.wrapContentWidth(),
        padding = 8.dp,
        elevation = 1.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(value, fontWeight = FontWeight.ExtraBold, color = TextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
fun FeatureActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    SarkariCard(onClick = onClick, padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = tint.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text("Go Now →", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun PerformanceLine(label: String, progress: Float) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = PrimaryBlue,
            trackColor = BorderColor
        )
    }
}

@Composable
fun SuggestionPill(text: String) {
    Surface(
        color = BgPrimary,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun CurrentAffairsItem(date: String, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = BgAccentSaffron,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                val parts = date.split(" ")
                Text(parts[0], fontWeight = FontWeight.Bold, color = AccentOrange, fontSize = 12.sp)
                Text(parts[1], color = AccentOrange, fontSize = 10.sp)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
    }
}
