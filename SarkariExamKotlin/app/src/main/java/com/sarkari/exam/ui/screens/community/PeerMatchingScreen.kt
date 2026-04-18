package com.sarkari.exam.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.theme.*

data class PeerItem(
    val id: String, val name: String, val exam: String, val level: String, val avatarParams: String, val subjects: List<String>, val studyHours: Int, val streak: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerMatchingScreen(navController: NavController) {
    
    val demoPeers = listOf(
        PeerItem("1", "Priya Sharma", "UPSC", "Intermediate", "👩‍🎓", listOf("History", "Polity"), 6, 12),
        PeerItem("2", "Rohit Kumar", "UPSC", "Beginner", "👨‍💻", listOf("Geography", "Economy"), 4, 8),
        PeerItem("3", "Sneha Patil", "MPSC", "Advanced", "👩‍💼", listOf("Marathi", "History"), 8, 30),
        PeerItem("4", "Amit Deshmukh", "SSC", "Intermediate", "🧑‍🎓", listOf("Reasoning", "Math"), 5, 15),
        PeerItem("5", "Fatima Khan", "Banking", "Beginner", "👩‍🏫", listOf("Quantitative", "English"), 3, 5),
        PeerItem("6", "Arjun Reddy", "NDA", "Intermediate", "💂", listOf("Math", "Physics"), 7, 22)
    )
    
    var connectedPeers by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peer Matching", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.People, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(10.dp).size(28.dp))
                    }
                    Column {
                        Text("Find Study Partners", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Connect with focused aspirants.", fontSize = 13.sp, color = TextSecondary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(demoPeers) { peer ->
                        val isConnected = connectedPeers.contains(peer.id)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.background, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(peer.avatarParams, fontSize = 24.sp)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(peer.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${peer.exam} • ${peer.level}", fontSize = 13.sp, color = TextSecondary)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    peer.subjects.forEach { subject ->
                                        Surface(color = AccentGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                            Text(subject, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${peer.studyHours} hrs/day", fontSize = 13.sp, color = TextSecondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentSaffron)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${peer.streak} Days", fontSize = 13.sp, color = TextSecondary)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Button(
                                    onClick = { 
                                        connectedPeers = if (isConnected) connectedPeers - peer.id else connectedPeers + peer.id 
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = if (isConnected) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant, contentColor = TextPrimary) 
                                             else ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(if (isConnected) Icons.Default.Check else Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isConnected) "Connected" else "Connect", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

