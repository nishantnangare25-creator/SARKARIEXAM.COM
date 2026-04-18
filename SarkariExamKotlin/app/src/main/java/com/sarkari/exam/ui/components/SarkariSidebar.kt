package com.sarkari.exam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.navigation.Screen
import com.sarkari.exam.ui.theme.*

data class NavSection(val label: String, val items: List<NavItem>)
data class NavItem(val route: String, val icon: ImageVector, val label: String, val color: Color, val bgColor: Color)

@Composable
fun SarkariSidebar(
    navController: NavController,
    currentRoute: String?,
    closeDrawer: () -> Unit
) {
    val knownRoutes = listOf(
        Screen.Dashboard.route, Screen.MockTest.route, Screen.Tutor.route, 
        Screen.Analytics.route, Screen.StudyPlanner.route, Screen.PYQLibrary.route, Screen.PYQTest.route,
        Screen.NotesGenerator.route, Screen.PastPaperAnalyzer.route, Screen.CurrentAffairs.route, Screen.Blog.route,
        Screen.Forum.route, Screen.PeerMatching.route, Screen.Settings.route
    )

    val sections = listOf(
        NavSection("Main", listOf(
            NavItem(Screen.Dashboard.route, Icons.Default.Dashboard, "Dashboard", PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f)),
            NavItem(Screen.CurrentAffairs.route, Icons.Default.Newspaper, "Current Affairs", AccentSaffron, AccentSaffron.copy(alpha = 0.1f)),
            NavItem(Screen.Blog.route, Icons.Default.MenuBook, "Blog", AccentGreen, AccentGreen.copy(alpha = 0.1f))
        )),
        NavSection("Tests", listOf(
            NavItem(Screen.MockTest.route, Icons.Default.LibraryBooks, "Mock Test", PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f)),
            NavItem(Screen.PYQTest.route, Icons.Default.CheckCircle, "PYQs Mock Test", AccentSaffron, AccentSaffron.copy(alpha = 0.1f))
        )),
        NavSection("Study Tools", listOf(
            NavItem(Screen.StudyPlanner.route, Icons.Default.DateRange, "Study Planner", AccentGreen, AccentGreen.copy(alpha = 0.1f)),
            NavItem(Screen.NotesGenerator.route, Icons.Default.Create, "Notes", AccentGreen, AccentGreen.copy(alpha = 0.1f)),
            NavItem(Screen.PastPaperAnalyzer.route, Icons.Default.Description, "Past Papers", AccentSaffron, AccentSaffron.copy(alpha = 0.1f)),
            NavItem(Screen.PYQLibrary.route, Icons.Default.Download, "PYQ PDFs", AccentSaffron, AccentSaffron.copy(alpha = 0.1f))
        )),
        NavSection("AI", listOf(
            NavItem(Screen.Tutor.route, Icons.Default.Face, "AI Tutor", PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f)),
            NavItem(Screen.Analytics.route, Icons.Default.Assessment, "Analytics", AccentGreen, AccentGreen.copy(alpha = 0.1f))
        )),
        NavSection("Community", listOf(
            NavItem(Screen.Forum.route, Icons.Default.Forum, "Forum", AccentSaffron, AccentSaffron.copy(alpha = 0.1f)),
            NavItem(Screen.PeerMatching.route, Icons.Default.People, "Peer Matching", AccentGreen, AccentGreen.copy(alpha = 0.1f))
        )),
        NavSection("Account", listOf(
            NavItem(Screen.Settings.route, Icons.Default.Settings, "Settings", Color.Gray, Color.LightGray.copy(alpha = 0.2f))
        ))
    )

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Brand strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.School, contentDescription = "Logo", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("AIGovPrep", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
            }
            
            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // Nav Sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                sections.forEach { section ->
                    Text(
                        text = section.label.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
                    )
                    
                    section.items.forEach { item ->
                        val isActive = currentRoute == item.route && item.route != "coming_soon"
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.5f) else Color.Transparent)
                                .clickable { 
                                    if (item.route in knownRoutes) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Dashboard.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } else {
                                        // Optional: Navigate to a generic coming soon screen if created
                                        navController.navigate("coming_soon") { launchSingleTop = true }
                                    }
                                    closeDrawer()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(item.bgColor, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.label,
                                fontSize = 14.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isActive) MaterialTheme.colorScheme.primary else TextSecondary
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            
            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(BackgroundBody, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(8.dp).background(AccentGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI-Powered Prep · Active", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

