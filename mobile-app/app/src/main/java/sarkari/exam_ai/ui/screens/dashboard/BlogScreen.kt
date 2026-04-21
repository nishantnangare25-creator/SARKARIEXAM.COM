package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.theme.*

@Composable
fun BlogScreen(navController: NavController, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val blogs = listOf(
            Pair("How to crack UPSC in first attempt?", "Gain insights from toppers and start with our curated NCERT guide for foundational basics."),
            Pair("Top 10 Bank PO Interview Questions", "A comprehensive list of HR and technical questions frequently asked during SBI/IBPS interviews."),
            Pair("Time Management Strategies for SSC CGL", "A breakdown of time-per-section ensuring you maximize your attempts within 60 minutes.")
        )

        items(blogs.size) { index ->
            val blog = blogs[index]
            SarkariCard(onClick = { /* Open Blog */ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(blog.first, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(blog.second, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                    }
                    
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
        }
    }
}
