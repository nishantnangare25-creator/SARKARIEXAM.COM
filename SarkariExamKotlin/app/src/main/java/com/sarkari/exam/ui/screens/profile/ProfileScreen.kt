package com.sarkari.exam.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = TextMuted)
            }
            
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(PrimaryBlue, AccentPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = BackgroundWhite)
                    }
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(36.dp)
                            .background(AccentOrange, CircleShape)
                            .border(3.dp, BackgroundWhite, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = BackgroundWhite, modifier = Modifier.size(18.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Aarav Sharma", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = TextPrimary)
                Text(text = "aarav@email.com", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("124", "TESTS", Icons.Default.Description, Modifier.weight(1f))
            StatCard("86%", "ACCURACY", Icons.Default.Adjust, Modifier.weight(1f))
            StatCard("12", "STREAK", Icons.Default.LocalFireDepartment, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Upgrade Card
        UpgradeCard()

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Sections
        ProfileSection(title = "Personal Info", icon = Icons.Default.PersonOutline) {
            InfoRow("Name", "Aarav Sharma")
            InfoRow("Phone", "+91 9876543210")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileSection(title = "Exam Preferences", icon = Icons.Default.MenuBook) {
            InfoRow("Target Exam", "SSC CGL", isTag = true, tagColor = PrimaryBlue)
            InfoRow("Subjects", "Math, English", isTag = true, tagColor = WarningOrange)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileSection(title = "Recent Activity", icon = Icons.Default.History, rightText = "View All") {
            ActivityItem("SSC Mock Test #5", "Score: 145/200", Icons.Default.CheckCircle, PrimaryBlue)
            ActivityItem("Notes Generated", "Topic: Indian Polity", Icons.Default.Description, WarningOrange)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.08f))
        ) {
            Text("Logout Session", color = ErrorRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = BackgroundWhite,
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryBlue.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextPrimary)
            Text(text = label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun UpgradeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PrimaryBlue,
        border = BorderStroke(1.dp, BackgroundWhite.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Premium AI Access 🚀", fontWeight = FontWeight.ExtraBold, color = BackgroundWhite, fontSize = 17.sp)
                Text(text = "Unlock all advanced features", fontSize = 13.sp, color = BackgroundWhite.copy(alpha = 0.8f))
            }
            Surface(
                onClick = { },
                shape = RoundedCornerShape(12.dp),
                color = BackgroundWhite
            ) {
                Text("Get Now", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Black, color = PrimaryBlue, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, icon: ImageVector, rightText: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = BackgroundWhite,
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(SurfaceGray.copy(alpha = 0.5f)).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary)
                }
                if (rightText != null) {
                    Text(text = rightText, color = PrimaryBlue, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isTag: Boolean = false, tagColor: Color = Color.Gray) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        if (isTag) {
            Surface(color = tagColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(text = value, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = tagColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        } else {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, icon: ImageVector, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
