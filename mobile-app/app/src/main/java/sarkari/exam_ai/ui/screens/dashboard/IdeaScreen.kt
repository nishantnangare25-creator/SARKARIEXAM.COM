package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.ButtonVariant
import sarkari.exam_ai.ui.components.SarkariButton
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.components.SarkariTextField
import sarkari.exam_ai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IdeaScreen(navController: NavController, paddingValues: PaddingValues) {
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SarkariCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AccentSaffron)
                    Text("Get Strategy Ideas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Text("Describe your bottleneck or what you want to improve, and AI will provide a strategic idea.", color = TextSecondary)
                
                SarkariTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = "Your Query",
                    placeholder = "e.g. How to memorize polity articles better?"
                )

                SarkariButton(
                    text = "Generate Idea",
                    onClick = {
                        isLoading = true
                        scope.launch {
                            delay(1500)
                            isLoading = false
                            result = "To memorize Polity articles better, create flashcards mapping each article to a real-world scenario (e.g., Article 14 -> Equality -> Everyone in a queue gets same treatment). Group them in chunks of 5 and use the spaced repetition technique."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                    isLoading = isLoading
                )
            }
        }

        if (result != null) {
            SarkariCard(backgroundColor = AccentSaffron.copy(alpha = 0.05f)) {
                Column {
                    Text("Strategy Idea", fontWeight = FontWeight.Bold, color = AccentOrange, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = result!!, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, lineHeight = 22.sp)
                }
            }
        }
    }
}
