package com.sarkari.exam.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.WarningOrange

data class DrawerItem(val title: String, val icon: ImageVector, val badge: String? = null)

@Composable
fun AppDrawerContent(
    selectedRoute: String = "Home",
    onItemClick: (String) -> Unit = {}
) {
    val menuItems = listOf(
        DrawerItem("Home", Icons.Default.Home),
        DrawerItem("Mock Tests", Icons.Default.FactCheck, "NEW"),
        DrawerItem("Results & Analysis", Icons.Default.BarChart),
        DrawerItem("Study Planner", Icons.Default.CalendarToday),
        DrawerItem("AI Tools", Icons.Default.AutoFixHigh),
        DrawerItem("PYQ & PDFs", Icons.Default.Description),
        DrawerItem("Daily Quiz", Icons.Default.Timer),
        DrawerItem("Target Exam", Icons.Default.Adjust),
        DrawerItem("Notes Generator", Icons.Default.AutoStories),
        DrawerItem("Weak Topics", Icons.Default.QueryStats)
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // 1. Profile Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                }
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(WarningOrange, CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Nishant", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Aspirant", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. AI Status Card
        AiStatusCard()

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Main Menu Items
        Text(text = "MAIN MENU", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
        
        menuItems.forEachIndexed { index, item ->
            DrawerMenuItem(
                item = item,
                isSelected = selectedRoute == item.title,
                onClick = { onItemClick(item.title) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Quick Actions
        QuickActions()

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Settings & Support
        Divider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsMenuItem("Settings", Icons.Outlined.Settings)
        SettingsMenuItem("Help & Support", Icons.Outlined.HeadsetMic)
        SettingsMenuItem("Privacy Policy", Icons.Outlined.Shield)
        SettingsMenuItem("Logout", Icons.Default.Logout, isLogout = true)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun AiStatusCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Intermediate", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryBlue)
                Text(text = "72%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.72f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = PrimaryBlue,
                trackColor = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Performance Score", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DrawerMenuItem(item: DrawerItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryBlue else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (isSelected) PrimaryBlue else Color(0xFF334155)
            )
            if (item.badge != null) {
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun QuickActions() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Mock Test", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Text("Continue Learning", fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun SettingsMenuItem(title: String, icon: ImageVector, isLogout: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (isLogout) Color.Red else Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 14.sp, color = if (isLogout) Color.Red else Color(0xFF64748B))
    }
}
