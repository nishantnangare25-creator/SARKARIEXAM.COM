package com.sarkari.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerMatchingScreen(
    onNavigateBack: () -> Unit,
    viewModel: PeerMatchingViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peer Matching", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Find Study Partners", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(bottom = 8.dp))
                Text("Connect with peers preparing for similar exams and boost your motivation.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp > 600) 3 else 2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.peers) { peer ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(peer.avatar, fontSize = 32.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(peer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                                Text("${peer.exam} • ${peer.level}", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                                    peer.subjects.take(2).forEach { subject ->
                                        Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(4.dp)) {
                                            Text(subject, fontSize = 9.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                
                                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${peer.studyHours}h", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2563EB))
                                        Text("Daily", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${peer.streak}🔥", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD97706))
                                        Text("Streak", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                                
                                Button(
                                    onClick = { viewModel.toggleConnect(peer.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (peer.isConnected) Color(0xFFE2E8F0) else Color(0xFF2563EB),
                                        contentColor = if (peer.isConnected) Color.Black else Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    if (peer.isConnected) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Connected", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
