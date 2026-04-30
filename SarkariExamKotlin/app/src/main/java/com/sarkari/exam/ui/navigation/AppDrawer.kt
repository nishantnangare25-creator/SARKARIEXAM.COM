package com.sarkari.exam.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val children: List<MenuItem>? = null
)

val menuItems = listOf(
    MenuItem("Home", Icons.Outlined.Home, "home"),
    MenuItem("Current Affairs", Icons.Outlined.Newspaper, "current_affairs"),
    MenuItem("Tests", Icons.Outlined.Quiz, "tests_group", listOf(
        MenuItem("Mock Test", Icons.Outlined.Assignment, "mock_test")
    )),
    MenuItem("Study Tools", Icons.Outlined.Build, "tools_group", listOf(
        MenuItem("Study Planner", Icons.Outlined.DateRange, "planner"),
        MenuItem("Notes Generator", Icons.Outlined.NoteAdd, "notes_gen"),
        MenuItem("Past Paper Analyzer", Icons.Outlined.History, "paper_analyzer"),
        MenuItem("PYQ PDFs", Icons.Outlined.PictureAsPdf, "pyq_pdfs")
    )),
    MenuItem("AI", Icons.Outlined.AutoAwesome, "ai_group", listOf(
        MenuItem("Riya (AI Assistant)", Icons.Outlined.SmartToy, "riya_ai")
    )),
    MenuItem("Analytics", Icons.Outlined.Insights, "analytics"),
    MenuItem("Community", Icons.Outlined.People, "community_group", listOf(
        MenuItem("Community", Icons.Outlined.Forum, "community"),
        MenuItem("Study Partners", Icons.Outlined.GroupAdd, "study_partners")
    )),
    MenuItem("Account", Icons.Outlined.Person, "account_group", listOf(
        MenuItem("Settings", Icons.Outlined.Settings, "settings")
    ))
)

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    closeDrawer: () -> Unit,
    userName: String = "Aarav Sharma",
    userSubtitle: String = "Premium User"
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // User Profile Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9FAFB))
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(userName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
            Text(userSubtitle, color = PrimaryBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Divider(color = Color(0xFFE5E7EB))

        // Menu Items
        Column(modifier = Modifier.padding(12.dp)) {
            menuItems.forEach { item ->
                DrawerItemNode(item, currentRoute, onNavigate, closeDrawer)
            }
        }
    }
}

@Composable
fun DrawerItemNode(
    item: MenuItem,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    closeDrawer: () -> Unit
) {
    val isExpandable = item.children != null
    var expanded by remember { mutableStateOf(false) }
    val isSelected = currentRoute == item.route || item.children?.any { it.route == currentRoute } == true

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    if (isExpandable) {
                        expanded = !expanded
                    } else {
                        onNavigate(item.route)
                        closeDrawer()
                    }
                }
                .background(if (isSelected && !isExpandable) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryBlue else TextMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryBlue else TextDark,
                modifier = Modifier.weight(1f)
            )
            if (isExpandable) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }

        if (isExpandable) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(start = 40.dp)) {
                    item.children!!.forEach { child ->
                        val childSelected = currentRoute == child.route
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onNavigate(child.route)
                                    closeDrawer()
                                }
                                .background(if (childSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = child.title,
                                fontWeight = if (childSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (childSelected) PrimaryBlue else TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
