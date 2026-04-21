package sarkari.exam_ai.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import sarkari.exam_ai.data.repository.AiRepository
import sarkari.exam_ai.ui.components.ButtonVariant
import sarkari.exam_ai.ui.components.SarkariButton
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.components.SarkariTextField
import sarkari.exam_ai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnalyzerViewModel : ViewModel() {
    private val repository = AiRepository()
    
    var examName by mutableStateOf("")
    var examYear by mutableStateOf("")
    var isAnalyzing by mutableStateOf(false)
    var analysisResult by mutableStateOf<List<Pair<String, Int>>?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun startAnalysis(language: String = "en") {
        if (examName.isBlank() || examYear.isBlank()) {
            errorMessage = "Please enter exam name and year"
            return
        }
        isAnalyzing = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val result = repository.generatePaperAnalysis(examName, examYear, language)
                analysisResult = result
            } catch (e: Exception) {
                errorMessage = e.message ?: "Analysis failed"
            } finally {
                isAnalyzing = false
            }
        }
    }
}

@Composable
fun PastPaperAnalyzerScreen(navController: NavController, paddingValues: PaddingValues, analyzerViewModel: AnalyzerViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .padding(20.dp)
    ) {
        SarkariCard {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("AI Paper Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Predict the most important topics based on historical trends.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                
                SarkariTextField(
                    value = analyzerViewModel.examName,
                    onValueChange = { analyzerViewModel.examName = it },
                    label = "Exam Name",
                    placeholder = "e.g. UPSC CSE Prelims"
                )
                SarkariTextField(
                    value = analyzerViewModel.examYear,
                    onValueChange = { analyzerViewModel.examYear = it },
                    label = "Recent Year",
                    placeholder = "e.g. 2024"
                )
                
                if (analyzerViewModel.errorMessage != null) {
                    Text(analyzerViewModel.errorMessage!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                SarkariButton(
                    text = "Analyze Paper Trend",
                    onClick = { analyzerViewModel.startAnalysis() },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                    isLoading = analyzerViewModel.isAnalyzing,
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (analyzerViewModel.analysisResult != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Topic Wise Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(analyzerViewModel.analysisResult!!) { (topic, percentage) ->
                    SarkariCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(topic, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LinearProgressIndicator(
                                        progress = percentage / 100f,
                                        modifier = Modifier.weight(1f).height(6.dp),
                                        color = PrimaryBlue,
                                        trackColor = PrimaryBlue.copy(alpha = 0.1f)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("$percentage%", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
