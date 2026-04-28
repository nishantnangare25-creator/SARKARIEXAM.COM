package com.sarkari.exam.ui.screens.language

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.viewmodels.UserViewModel

data class LanguageInfo(val name: String, val nativeName: String, val code: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val languages = listOf(
        LanguageInfo("English", "English", "en"),
        LanguageInfo("Hindi", "हिन्दी", "hi"),
        LanguageInfo("Marathi", "मराठी", "mr"),
        LanguageInfo("Bengali", "বাংলা", "bn"),
        LanguageInfo("Tamil", "தமிழ்", "ta"),
        LanguageInfo("Telugu", "తెలుగు", "te"),
        LanguageInfo("Gujarati", "ગુજરાતી", "gu"),
        LanguageInfo("Kannada", "ಕನ್ನಡ", "kn"),
        LanguageInfo("Malayalam", "മലയാളം", "ml"),
        LanguageInfo("Punjabi", "ਪੰਜਾਬੀ", "pa")
    )

    val userProfile by userViewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Select Language",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        
        Text(
            text = "Choose your preferred state language",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(languages) { lang ->
                LanguageCard(
                    lang = lang,
                    isSelected = userProfile.language == lang.code,
                    onClick = {
                        userViewModel.updateLanguage(lang.code)
                        onLanguageSelected()
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LanguageCard(lang: LanguageInfo, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryBlue else Color(0xFFF8FAFC),
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = lang.nativeName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isSelected) Color.White else Color(0xFF1E293B)
            )
            Text(
                text = lang.name,
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
            )
        }
    }
}
