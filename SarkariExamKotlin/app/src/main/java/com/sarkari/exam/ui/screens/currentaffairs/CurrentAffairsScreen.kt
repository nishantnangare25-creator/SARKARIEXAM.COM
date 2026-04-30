package com.sarkari.exam.ui.screens.currentaffairs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.sarkari.exam.ui.viewmodels.CurrentAffairsViewModel
import com.sarkari.exam.ui.viewmodels.NewsArticle

val BackgroundLight = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentAffairsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: CurrentAffairsViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val articles by viewModel.filteredArticles.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val filters = viewModel.filters

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Current Affairs 📰", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshNews() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryBlue)
                    }
                    IconButton(onClick = { /* Go to saved items */ }) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Saved", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search current affairs...", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                // Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filters) { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                modifier = Modifier.clickable { viewModel.onFilterSelect(filter) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PrimaryBlue else Color.White,
                                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE5E7EB)) else null
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // AI Summary Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryBlue,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Get AI Summary of Today's News", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Read all highlights in 2 minutes", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Surface(
                                color = AccentOrange,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { /* Generate Summary */ }
                            ) {
                                Text(
                                    text = "Generate 🤖", 
                                    color = Color.White, 
                                    fontWeight = FontWeight.Bold, 
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Weekly Compilation
                item {
                    Column {
                        Text("Weekly Compilation", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(3) { index ->
                                WeeklyCard(weekLabel = "Week ${3 - index}, Feb 2026")
                            }
                        }
                    }
                }

                // Today's Highlights List
                item {
                    Text("Today's Highlights", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                }

                items(articles, key = { it.id }) { article ->
                    NewsCard(
                        article = article,
                        onBookmarkClick = { viewModel.toggleBookmark(article.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(paddingValues),
                    color = PrimaryBlue,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun NewsCard(article: NewsArticle, onBookmarkClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Open Article */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = article.category,
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(article.date, color = TextMuted, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(article.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                article.summary,
                color = TextMuted,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFF3F4F6))

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Read More", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                IconButton(onClick = onBookmarkClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (article.isBookmarked) AccentOrange else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyCard(weekLabel: String) {
    Surface(
        modifier = Modifier.width(160.dp).clickable { /* Download PDF */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(48.dp).background(AccentOrange.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = AccentOrange)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(weekLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Download PDF", color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
