package com.sarkari.exam.ui.screens.community

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.AccentOrange
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.TextDark
import com.sarkari.exam.ui.theme.TextMuted
import com.sarkari.exam.ui.viewmodels.CommunityTab
import com.sarkari.exam.ui.viewmodels.CommunityViewModel
import com.sarkari.exam.ui.viewmodels.DiscussionTopic
import com.sarkari.exam.ui.viewmodels.FeedPost
import com.sarkari.exam.ui.viewmodels.StudyPartner

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onOpenDrawer: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val selectedExam by viewModel.selectedExam.collectAsState()
    val availableSubjects by viewModel.availableSubjects.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    
    val currentTab by viewModel.currentTab.collectAsState()
    val feedPosts by viewModel.feedPosts.collectAsState()
    val discussions by viewModel.discussions.collectAsState()
    val studyPartners by viewModel.studyPartners.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Community 👥", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark)
                        Text("Connect & Learn Together", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            if (currentTab == CommunityTab.FEED) {
                FloatingActionButton(
                    onClick = { /* Create Post */ },
                    containerColor = PrimaryBlue,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Create Post", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // User Activity Badge & Trending
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AccentOrange.copy(alpha = 0.1f)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pro Scholar • Lvl 12", color = AccentOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Trending
                    Text("#SSC2024 #Quant", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Dropdowns
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CommunityDropdown(
                        label = "Target Exam",
                        options = viewModel.examsList,
                        selectedOption = selectedExam,
                        onOptionSelect = { viewModel.onExamSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                    CommunityDropdown(
                        label = "Subject",
                        options = availableSubjects,
                        selectedOption = selectedSubject,
                        onOptionSelect = { viewModel.onSubjectSelected(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs
            item {
                CommunityTabRow(currentTab = currentTab, onTabSelect = { viewModel.setTab(it) })
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else {
                // Content based on tab
                when (currentTab) {
                    CommunityTab.FEED -> {
                        items(feedPosts) { post ->
                            FeedPostCard(post, onLikeClick = { viewModel.toggleLike(post.id) })
                        }
                    }
                    CommunityTab.DISCUSSIONS -> {
                        items(discussions) { topic ->
                            DiscussionCard(topic)
                        }
                    }
                    CommunityTab.STUDY_PARTNERS -> {
                        items(studyPartners) { partner ->
                            StudyPartnerCard(partner)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) } // Space for FAB
        }
    }
}

@Composable
fun CommunityTabRow(currentTab: CommunityTab, onTabSelect: (CommunityTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5E7EB)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            val tabs = listOf(
                Pair(CommunityTab.FEED, "Feed"),
                Pair(CommunityTab.DISCUSSIONS, "Discussions"),
                Pair(CommunityTab.STUDY_PARTNERS, "Partners")
            )
            tabs.forEach { (tab, title) ->
                val isSelected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelect(tab) }
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) PrimaryBlue else TextMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(post: FeedPost, onLikeClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(PrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.authorInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Text(post.timeAgo, color = TextMuted, fontSize = 12.sp)
                }
                IconButton(onClick = { /* More options */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, color = TextDark, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onLikeClick() }.padding(4.dp)) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFE91E63) else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.likes.toString(), color = if (post.isLiked) Color(0xFFE91E63) else TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }.padding(4.dp)) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = TextMuted, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.comments.toString(), color = TextMuted, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }.padding(4.dp)) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextMuted, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(post.shares.toString(), color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DiscussionCard(topic: DiscussionTopic) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Forum, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${topic.repliesCount} replies", color = TextMuted, fontSize = 12.sp)
                }
                Text("Active ${topic.lastActive}", color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StudyPartnerCard(partner: StudyPartner) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(AccentOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(partner.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                // Online/Offline status dot
                val statusColor = if (partner.isOnline) Color(0xFF00B859) else Color.Gray
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(statusColor, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Text("Target: ${partner.targetExam}", color = TextMuted, fontSize = 12.sp)
            }
            
            Button(
                onClick = { /* Connect */ },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Connect", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryBlue
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
