package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerformanceStats(
    val accuracy: String = "78%",
    val testsAttempted: String = "124",
    val studyTime: String = "45h",
    val rank: String = "2.5k"
)

data class SubjectPerformance(
    val subject: String,
    val percentage: Int,
    val statusColorHex: String // Use hex string to determine color in UI
)

data class TestHistoryItem(
    val id: String,
    val title: String,
    val score: String,
    val accuracy: String,
    val date: String
)

class AnalyticsViewModel : ViewModel() {

    // Dynamic Options
    val examsList = listOf("SSC CGL", "UPSC Civil Services", "Banking", "Railway")
    private val subjectMap = mapOf(
        "SSC CGL" to listOf("All Subjects", "Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("All Subjects", "History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking" to listOf("All Subjects", "Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway" to listOf("All Subjects", "Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All Subjects")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Time Filters
    val timeFilters = listOf("Last 7 Days", "Last 30 Days", "All Time")
    private val _selectedTimeFilter = MutableStateFlow(timeFilters[0])
    val selectedTimeFilter: StateFlow<String> = _selectedTimeFilter.asStateFlow()

    // Graph Toggle
    val graphToggles = listOf("Weekly", "Monthly")
    private val _selectedGraphToggle = MutableStateFlow(graphToggles[0])
    val selectedGraphToggle: StateFlow<String> = _selectedGraphToggle.asStateFlow()

    // Data States
    private val _stats = MutableStateFlow(PerformanceStats())
    val stats: StateFlow<PerformanceStats> = _stats.asStateFlow()

    private val _subjectPerformances = MutableStateFlow<List<SubjectPerformance>>(emptyList())
    val subjectPerformances: StateFlow<List<SubjectPerformance>> = _subjectPerformances.asStateFlow()

    private val _testHistory = MutableStateFlow<List<TestHistoryItem>>(emptyList())
    val testHistory: StateFlow<List<TestHistoryItem>> = _testHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: listOf("All Subjects")
        _availableSubjects.value = subjects
        _selectedSubject.value = "All Subjects"
        loadData()
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        loadData()
    }

    fun onTimeFilterSelected(filter: String) {
        _selectedTimeFilter.value = filter
        loadData()
    }

    fun onGraphToggleSelected(toggle: String) {
        _selectedGraphToggle.value = toggle
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(800) // Simulate network delay

            // Update stats based on filter
            if (_selectedTimeFilter.value == "Last 7 Days") {
                _stats.value = PerformanceStats("78%", "12", "15h", "2.5k")
                _subjectPerformances.value = listOf(
                    SubjectPerformance("Quantitative Aptitude", 78, "BLUE"),
                    SubjectPerformance("Reasoning", 85, "GREEN"),
                    SubjectPerformance("English", 62, "ORANGE"),
                    SubjectPerformance("General Awareness", 55, "RED")
                )
                _testHistory.value = listOf(
                    TestHistoryItem("1", "SSC CGL Quant Test 4", "45/50", "90%", "Yesterday"),
                    TestHistoryItem("2", "Full Length Mock 2", "130/200", "75%", "3 Days Ago")
                )
            } else {
                _stats.value = PerformanceStats("82%", "124", "45h", "1.2k")
                _subjectPerformances.value = listOf(
                    SubjectPerformance("Quantitative Aptitude", 85, "GREEN"),
                    SubjectPerformance("Reasoning", 88, "GREEN"),
                    SubjectPerformance("English", 70, "ORANGE"),
                    SubjectPerformance("General Awareness", 45, "RED")
                )
                _testHistory.value = listOf(
                    TestHistoryItem("1", "SSC CGL Quant Test 4", "45/50", "90%", "Yesterday"),
                    TestHistoryItem("2", "Full Length Mock 2", "130/200", "75%", "3 Days Ago"),
                    TestHistoryItem("3", "English Vocab Quiz", "15/20", "75%", "1 Week Ago")
                )
            }

            _isLoading.value = false
        }
    }
}
