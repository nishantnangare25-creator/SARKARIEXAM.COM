package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.*
import sarkari.exam_ai.ui.theme.*
import sarkari.exam_ai.ui.viewmodels.AuthViewModel
import sarkari.exam_ai.ui.viewmodels.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController, 
    paddingValues: PaddingValues,
    viewModel: CommunityViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isPosting by remember { mutableStateOf(false) }
    
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser = authViewModel.currentUser

    val categories = listOf("All", "Current Affairs", "Exam Strategies", "Doubts", "Resources")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- 1. Header (Image 5) ---
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Community",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Discuss, learn, and grow together",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // --- 2. Category Tabs ---
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { 
                                selectedCategory = category
                                viewModel.fetchPosts(category)
                            },
                            label = { Text(category) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                selectedLabelColor = PrimaryBlue,
                                containerColor = BgSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                selected = (category == selectedCategory),
                                enabled = true,
                                borderColor = BorderColor,
                                selectedBorderColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            // --- 3. New Discussion CTA ---
            item {
                SarkariButton(
                    text = "New Discussion",
                    onClick = { isPosting = true },
                    modifier = Modifier.wrapContentWidth(),
                    variant = ButtonVariant.Primary,
                    icon = { Icon(Icons.Default.Add, null) }
                )
            }

            // --- 4. Compose Section (Image 5 Form) ---
            item {
                SarkariCard(padding = 20.dp, borderColor = PrimaryBlue.copy(alpha = 0.2f)) {
                    Text("New Discussion", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SarkariTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Title",
                        placeholder = "Enter discussion title"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Displaying current user hint
                    Text(
                        text = "Posting as: ${currentUser?.displayName ?: "Anonymous"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SarkariTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = "What's on your mind?",
                        modifier = Modifier.height(120.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SarkariButton(
                            text = "Post",
                            onClick = { 
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    viewModel.createPost(
                                        title = title,
                                        content = content,
                                        category = if (selectedCategory == "All") "General" else selectedCategory,
                                        authorName = currentUser?.displayName ?: "Student",
                                        authorId = currentUser?.uid ?: ""
                                    )
                                    title = ""
                                    content = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            variant = ButtonVariant.Primary,
                            enabled = title.isNotBlank() && content.isNotBlank()
                        )
                        SarkariButton(
                            text = "Clear",
                            onClick = { title = ""; content = "" },
                            modifier = Modifier.weight(1f),
                            variant = ButtonVariant.Outline
                        )
                    }
                }
            }

            // --- 5. Discussion Feed ---
            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else if (posts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No discussions yet. Start one!", color = TextMuted)
                    }
                }
            } else {
                items(posts) { post ->
                    DiscussionItem(
                        tag = post.category.uppercase(),
                        time = "Recently", // You can add actual time formatting later
                        title = post.title,
                        author = post.authorName
                    )
                }
            }
        }
}

@Composable
fun DiscussionItem(tag: String, time: String, title: String, author: String) {
    SarkariCard(padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SarkariBadge(tag, BadgeVariant.Primary)
            Text(time, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).background(PrimaryBlue, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(author, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
