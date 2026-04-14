package com.sarkari.exam.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarkari.exam.ui.theme.*

@Composable
fun SarkariHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SarkariRed)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sarkari Exam",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "अपडेट सबसे पहले",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun NewsTicker(text: String) {
    var tickerPosition by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "ticker")
    
    // Simplistic static ticker for now as real scrolling requires more complex state
    // But we'll style it to look like the website's high-priority bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SarkariRed)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "NEW",
            color = SarkariRed,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun SarkariSection(
    title: String,
    items: List<SarkariLink>,
    onItemClick: (SarkariLink) -> Unit,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerGray),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column {
            // Section Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderDark)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            // Link Items
            items.forEach { link ->
                SarkariLinkItem(link = link) { onItemClick(link) }
                Divider(color = DividerGray, thickness = 0.5.dp)
            }
            
            // View All Button
            Text(
                text = "View All »",
                color = SarkariRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewAllClick() }
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun SarkariLinkItem(link: SarkariLink, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = TextGray,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = link.title,
            color = LinkBlue,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
        if (link.isNew) {
            Text(
                text = "New",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(SarkariRed, RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

data class SarkariLink(
    val title: String,
    val url: String,
    val isNew: Boolean = false
)
