package com.sarkari.exam.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sarkari.exam.ui.screens.auth.LoginScreen
import com.sarkari.exam.ui.screens.auth.SignupScreen
import com.sarkari.exam.ui.screens.notes.NotesGeneratorScreen
import com.sarkari.exam.ui.screens.tutor.InteractiveTutorScreen


// ==========================================
// 1. DESIGN SYSTEM (COLORS, TYPOGRAPHY)
// ==========================================

object BananiColors {
    val Primary = Color(0xFF2E5BC0)
    val Background = Color(0xFFF5F7FA)
    val Surface = Color(0xFFFFFFFF)
    val Success = Color(0xFF22C55E)
    val Warning = Color(0xFFF97316)
    val TextPrimary = Color(0xFF1A202C)
    val TextSecondary = Color(0xFF718096)
    val Border = Color(0xFFE2E8F0)
}

@Composable
fun BananiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BananiColors.Primary,
            background = BananiColors.Background,
            surface = BananiColors.Surface,
            onSurface = BananiColors.TextPrimary,
            onBackground = BananiColors.TextPrimary
        ),
        content = content
    )
}

// ==========================================
// 2. REUSABLE COMPONENTS
// ==========================================

@Composable
fun BananiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = BananiColors.Primary,
    contentColor: Color = Color.White,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun BananiCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, BananiColors.Border, RoundedCornerShape(24.dp)),
        color = BananiColors.Surface,
        content = { Column(modifier = Modifier.padding(20.dp)) { content() } }
    )
}

@Composable
fun BananiChip(
    text: String,
    backgroundColor: Color = BananiColors.Primary.copy(alpha = 0.1f),
    textColor: Color = BananiColors.Primary
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun BananiProgressBar(progress: Float, color: Color = BananiColors.Primary) {
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        color = color,
        trackColor = BananiColors.Border
    )
}

// ==========================================
// 3. SCREENS
// ==========================================

// --- HOME SCREEN ---
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Scaffold(
        containerColor = BananiColors.Background,
        topBar = {
            HeaderSection()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TargetExamCard()
            Spacer(modifier = Modifier.height(20.dp))
            
            Text("Practice & Tests", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            PracticeGrid(onNavigate)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("AI Tools", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            AiToolsRow(onNavigate)
            
            Spacer(modifier = Modifier.height(24.dp))
            ActivityFeed()
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFF1E40AF), Color(0xFF2E5BC0))))
            .padding(top = 48.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Hello, Nishant 👋", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Ready for your UPSC prep?", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun TargetExamCard() {
    BananiCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(BananiColors.Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Adjust, contentDescription = null, tint = BananiColors.Primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("TARGET EXAM", fontSize = 10.sp, color = BananiColors.TextSecondary, fontWeight = FontWeight.Bold)
                Text("UPSC Civil Services", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            TextButton(onClick = {}) {
                Text("CHANGE", color = BananiColors.Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PracticeGrid(onNavigate: (String) -> Unit) {
    val items = listOf(
        "Mock Tests" to Icons.Default.FactCheck,
        "PYQ Practice" to Icons.Default.History,
        "Daily Quiz" to Icons.Default.Timer,
        "Topic Tests" to Icons.Default.LibraryBooks
    )
    
    Column {
        for (i in items.indices step 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                GridItem(items[i].first, items[i].second, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                GridItem(items[i+1].first, items[i+1].second, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun GridItem(title: String, icon: ImageVector, modifier: Modifier) {
    BananiCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(40.dp).background(BananiColors.Primary.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = BananiColors.Primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AiToolsRow(onNavigate: (String) -> Unit) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        listOf("Riya AI", "Notes Gen", "Planner", "Analyzer", "PYQ Gen").forEach { tool ->
            Surface(
                modifier = Modifier.padding(end = 12.dp).width(140.dp).clickable { 
                    if (tool == "Notes Gen") onNavigate("notes_gen") 
                    else if (tool == "Riya AI") onNavigate("tutor")
                    else onNavigate("ai_tools") 
                },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BananiColors.Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BananiColors.Primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tool, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ActivityFeed() {
    Text("Recently Generated", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(12.dp))
    repeat(3) {
        BananiCard(modifier = Modifier.padding(bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = BananiColors.TextSecondary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Indian Polity Notes #4", fontWeight = FontWeight.Bold)
                    Text("Generated 2h ago", color = BananiColors.TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- PREMIUM SCREEN ---
@Composable
fun PremiumScreen(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = null) }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("Upgrade to Premium", fontWeight = FontWeight.Black, fontSize = 28.sp, textAlign = TextAlign.Center)
        Text("Unlock all AI-powered features", color = BananiColors.TextSecondary, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(40.dp))
        
        BananiCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                BananiChip("MOST POPULAR")
                Spacer(modifier = Modifier.height(16.dp))
                Text("₹99/month", fontWeight = FontWeight.Black, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BananiChip("50% OFF", backgroundColor = BananiColors.Success.copy(alpha = 0.1f), textColor = BananiColors.Success)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BenefitItem("Unlimited AI Mock Tests")
            BenefitItem("Advanced PYQ Analysis")
            BenefitItem("Personalized Study Plan")
            BenefitItem("No Advertisement")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        BananiButton(text = "Start 7-Day Free Trial", onClick = {})
        Spacer(modifier = Modifier.height(16.dp))
        Text("Cancel anytime. No questions asked.", color = BananiColors.TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun BenefitItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BananiColors.Success, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

// --- AI DASHBOARD ---
@Composable
fun AiDashboardScreen(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BananiColors.Background).padding(16.dp)) {
        Text("AI Tools Dashboard", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf("Riya AI (Tutor)", "Study Planner", "Notes Gen", "PYQ Analyzer", "Revision Booster", "Weak Topics", "Flashcards")) { tool ->
                Surface(
                    modifier = Modifier.clickable {
                        if (tool == "Notes Gen") onNavigate("notes_gen")
                        else if (tool == "Riya AI (Tutor)") onNavigate("tutor")
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BananiColors.Border)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = BananiColors.Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tool, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}


// --- MOCK TEST SCREEN ---
@Composable
fun MockTestScreen() {
    Column(modifier = Modifier.fillMaxSize().background(BananiColors.Background).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Question 4/100", fontWeight = FontWeight.Bold)
            Text("42:15", fontWeight = FontWeight.Bold, color = BananiColors.Warning)
        }
        Spacer(modifier = Modifier.height(20.dp))
        BananiProgressBar(progress = 0.04f)
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            "Which article of the Indian Constitution deals with the Right to Equality?",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 26.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        listOf("Article 14-18", "Article 19-22", "Article 23-24", "Article 25-28").forEach { option ->
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BananiColors.Border),
                color = Color.White
            ) {
                Text(option, modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Medium)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {}, 
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("HINT") }
            Spacer(modifier = Modifier.width(12.dp))
            BananiButton(text = "NEXT", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}

// --- ANALYTICS SCREEN ---
@Composable
fun AnalyticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BananiColors.Background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Your Performance", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        BananiCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Overall Accuracy", color = BananiColors.TextSecondary, fontSize = 14.sp)
                Text("86%", fontWeight = FontWeight.Black, fontSize = 48.sp, color = BananiColors.Success)
                Spacer(modifier = Modifier.height(16.dp))
                BananiProgressBar(progress = 0.86f, color = BananiColors.Success)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Subject-wise Progress", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        SubjectProgressItem("Quantitative Aptitude", 0.72f, BananiColors.Primary)
        SubjectProgressItem("General Intelligence", 0.91f, BananiColors.Success)
        SubjectProgressItem("English Language", 0.65f, BananiColors.Warning)
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Recent Scores", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        repeat(3) {
            BananiCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Mock Test #12", fontWeight = FontWeight.Bold)
                        Text("24 April 2026", color = BananiColors.TextSecondary, fontSize = 12.sp)
                    }
                    Text("174/200", fontWeight = FontWeight.ExtraBold, color = BananiColors.Primary)
                }
            }
        }
    }
}

@Composable
fun SubjectProgressItem(name: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        BananiProgressBar(progress = progress, color = color)
    }
}

// --- PROFILE SCREEN ---
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BananiColors.Background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(100.dp).background(BananiColors.Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = BananiColors.Primary)
            }
            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp).background(BananiColors.Warning, CircleShape).border(2.dp, Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nishant Nangare", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text("nishant@example.com", color = BananiColors.TextSecondary, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatItem("124", "TESTS", Modifier.weight(1f))
            StatItem("86%", "ACCURACY", Modifier.weight(1f))
            StatItem("12", "STREAK", Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileSection("Personal Info") {
            ProfileInfoRow("Full Name", "Nishant Nangare")
            ProfileInfoRow("Phone", "+91 9876543210")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileSection("Exam Preferences") {
            ProfileInfoRow("Target Exam", "UPSC Civil Services")
            ProfileInfoRow("Language", "English")
        }
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier) {
    BananiCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = BananiColors.Primary)
            Text(label, fontSize = 10.sp, color = BananiColors.TextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        BananiCard { content() }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = BananiColors.TextSecondary, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// --- SETTINGS SCREEN ---
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BananiColors.Background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Settings", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsGroup("General") {
            SettingsItem("Night Mode", Icons.Default.NightsStay, hasSwitch = true)
            SettingsItem("Notifications", Icons.Default.Notifications, hasSwitch = true)
            SettingsItem("App Language", Icons.Default.Language, value = "English")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsGroup("Account") {
            SettingsItem("Change Password", Icons.Default.Lock)
            SettingsItem("Privacy Policy", Icons.Default.Shield)
            SettingsItem("Logout", Icons.Default.Logout, color = Color.Red)
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BananiColors.Primary, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        BananiCard { content() }
    }
}

@Composable
fun SettingsItem(
    title: String, 
    icon: ImageVector, 
    hasSwitch: Boolean = false, 
    value: String? = null,
    color: Color = BananiColors.TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (color == Color.Red) color else BananiColors.TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = color)
        if (hasSwitch) {
            Switch(checked = true, onCheckedChange = {})
        } else if (value != null) {
            Text(value, color = BananiColors.TextSecondary, fontSize = 14.sp)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BananiColors.Border)
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BananiColors.Border)
        }
    }
}

// --- MAIN NAVIGATION ASSEMBLY ---
@Composable
fun BananiAppMain() {
    val navController = rememberNavController()
    BananiTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onNavigateToSignup = { navController.navigate("signup") },
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("signup") {
                SignupScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onSignupSuccess = {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") { HomeScreen { route -> navController.navigate(route) } }
            composable("premium") { PremiumScreen { navController.popBackStack() } }
            composable("ai_tools") { AiDashboardScreen { route -> navController.navigate(route) } }

            composable("mock_test") { MockTestScreen() }
            composable("analytics") { AnalyticsScreen() }
            composable("profile") { ProfileScreen() }
            composable("settings") { SettingsScreen() }
            composable("notes_gen") { NotesGeneratorScreen(onBack = { navController.popBackStack() }) }
            composable("tutor") { InteractiveTutorScreen(navController = navController) }


        }
    }
}

