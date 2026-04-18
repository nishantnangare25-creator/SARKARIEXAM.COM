package com.sarkari.exam.ui.screens.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.theme.*

data class BlogPost(
    val id: String,
    val title: String,
    val excerpt: String,
    val date: String,
    val imageColors: List<Color>,
    val isNew: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(navController: NavController) {
    
    val posts = listOf(
        BlogPost("1", "UPSC 2026 Prelims Strategy: The Ultimate Guide", "A comprehensive month-by-month strategy to clear UPSC Civil Services Prelims.", "18 Apr 2026", listOf(PrimaryBlue, AccentGreen), true),
        BlogPost("2", "Understanding the New Tax Slabs in India", "Breaking down the latest economic budget updates and what they mean for exams.", "16 Apr 2026", listOf(AccentSaffron, PrimaryBlue), false),
        BlogPost("3", "SSC CGL Tier 1 Math Shortcuts", "Save crucial seconds in the exam hall with these proven quantitative aptitude shortcuts.", "14 Apr 2026", listOf(SurfaceTertiary, AccentRed), false),
        BlogPost("4", "The Hindu Editorial Analysis format", "How to effectively read and make notes from newspaper editorials in under 45 minutes.", "12 Apr 2026", listOf(AccentGreen, PrimaryBlue), false),
        BlogPost("5", "Mastering Indian Polity Articles", "Memory tricks to remember all important articles of the Indian Constitution.", "09 Apr 2026", listOf(AccentRed, AccentSaffron), false),
        BlogPost("6", "Banking IBPS PO Interview Tips", "What panel members look for and how to dress and speak for banking interviews.", "05 Apr 2026", listOf(PrimaryBlue, BackgroundBody), false)
    )

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sarkari AI Hub", fontWeight = FontWeight.Bold) },
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
                Text("Resources & Updates", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Expert guides to supercharge your prep.", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search articles...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = MaterialTheme.colorScheme.surface, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant)
                )
            }
            
            val filteredPosts = posts.filter { it.title.contains(searchQuery, ignoreCase = true) || it.excerpt.contains(searchQuery, ignoreCase = true) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Determine screen constraints. A real tablet UI might use GridCells.Adaptive(300.dp)
                items(filteredPosts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column {
                            // Image placeholder mock
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(androidx.compose.ui.graphics.Brush.linearGradient(post.imageColors))
                            ) {
                                if (post.isNew) {
                                    Surface(
                                        color = AccentGreen,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                                    ) {
                                        Text("NEW", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(post.excerpt, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(post.date, fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

