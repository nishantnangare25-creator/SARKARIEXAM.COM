package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnalyzerTab { UPLOAD_PDF, SELECT_PYQ, ENTER_QUESTIONS }

data class TopicTrend(val topic: String, val percentage: Int)

data class PaperAnalysisResult(
    val totalQuestions: Int,
    val overallDifficulty: String,
    val topTopic: String,
    val easyPercent: Int,
    val mediumPercent: Int,
    val hardPercent: Int,
    val trends: List<TopicTrend>,
    val importantQuestions: List<String>,
    val aiInsight: String
)

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val result: PaperAnalysisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

class PaperAnalyzerViewModel : ViewModel() {

    // Input Tabs
    private val _currentTab = MutableStateFlow(AnalyzerTab.UPLOAD_PDF)
    val currentTab: StateFlow<AnalyzerTab> = _currentTab.asStateFlow()

    // Dynamic Options
    val examsList = listOf("SSC CGL 2024", "UPSC Civil Services", "Banking (IBPS/SBI)", "Railway (RRB)")
    private val subjectMap = mapOf(
        "SSC CGL 2024" to listOf("Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking (IBPS/SBI)" to listOf("Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway (RRB)" to listOf("Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow(_availableSubjects.value.firstOrNull() ?: "")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Year Filter
    val yearsList = listOf("2024", "2023", "2022", "2021", "2020")
    private val _selectedYear = MutableStateFlow("2024")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    // Analysis State
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    fun setTab(tab: AnalyzerTab) {
        _currentTab.value = tab
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: emptyList()
        _availableSubjects.value = subjects
        if (subjects.isNotEmpty()) {
            _selectedSubject.value = subjects[0]
        }
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
    }

    fun onYearSelected(year: String) {
        _selectedYear.value = year
    }

    fun analyzePaper() {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            delay(2000) // Simulate AI processing

            val mockResult = PaperAnalysisResult(
                totalQuestions = 100,
                overallDifficulty = "Medium-Hard",
                topTopic = "Arithmetic",
                easyPercent = 40,
                mediumPercent = 35,
                hardPercent = 25,
                trends = listOf(
                    TopicTrend("Percentage Questions", 35),
                    TopicTrend("Ratio & Proportion", 20),
                    TopicTrend("Number System", 15),
                    TopicTrend("Data Interpretation", 15),
                    TopicTrend("Other Topics", 15)
                ),
                importantQuestions = listOf(
                    "A can do a piece of work in 10 days... (Asked 4 times since 2021)",
                    "Find the ratio of... (Consistently asked in Tier 1)"
                ),
                aiInsight = "This paper focuses heavily on Arithmetic. Expect similar pattern in upcoming exams. Focus on speed in Data Interpretation."
            )

            _analysisState.value = AnalysisState.Success(mockResult)
        }
    }

    fun resetState() {
        _analysisState.value = AnalysisState.Idle
    }
}
