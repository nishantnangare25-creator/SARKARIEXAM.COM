package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TestMode { MOCK, PYQ }

data class TestItem(
    val id: String,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val difficulty: String,
    val type: TestMode,
    val year: String? = null // only for PYQ
)

data class UserPerformance(
    val accuracy: Float = 84f,
    val testsAttempted: Int = 12,
    val lastScore: String = "142/200",
    val xpPoints: Int = 1450,
    val rank: String = "Gold Level",
    val streak: Int = 5
)

class MockTestViewModel : ViewModel() {

    // Dynamic Options
    val examsList = listOf("SSC CGL 2024", "UPSC Civil Services", "Banking (IBPS/SBI)", "Railway (RRB)")
    private val subjectMap = mapOf(
        "SSC CGL 2024" to listOf("All Subjects", "Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("All Subjects", "History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking (IBPS/SBI)" to listOf("All Subjects", "Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway (RRB)" to listOf("All Subjects", "Maths", "Reasoning", "General Science")
    )
    val pyqYears = listOf("All Years", "2023", "2022", "2021", "2020", "2019")

    // State
    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All Subjects")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _testMode = MutableStateFlow(TestMode.MOCK)
    val testMode: StateFlow<TestMode> = _testMode.asStateFlow()

    private val _selectedYear = MutableStateFlow("All Years")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _tests = MutableStateFlow<List<TestItem>>(emptyList())
    val tests: StateFlow<List<TestItem>> = _tests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _performance = MutableStateFlow(UserPerformance())
    val performance: StateFlow<UserPerformance> = _performance.asStateFlow()

    init {
        loadTests()
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: listOf("All Subjects")
        _availableSubjects.value = subjects
        _selectedSubject.value = "All Subjects"
        loadTests()
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        loadTests()
    }

    fun onModeToggle(mode: TestMode) {
        _testMode.value = mode
        loadTests()
    }

    fun onYearSelected(year: String) {
        _selectedYear.value = year
        loadTests()
    }

    private fun loadTests() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(600) // Simulate network/DB fetch

            val dummyTests = mutableListOf<TestItem>()
            val mode = _testMode.value
            val exam = _selectedExam.value
            val subject = _selectedSubject.value
            val year = _selectedYear.value

            if (mode == TestMode.MOCK) {
                for (i in 1..5) {
                    val subTitle = if (subject == "All Subjects") "Full Length" else subject
                    dummyTests.add(TestItem("mock_$i", "$exam $subTitle Mock $i", 100, 60, "Medium", TestMode.MOCK))
                }
            } else {
                for (i in 1..4) {
                    val targetYear = if (year == "All Years") (2024 - i).toString() else year
                    val subTitle = if (subject == "All Subjects") "Previous Paper" else subject
                    dummyTests.add(TestItem("pyq_$i", "$exam $subTitle", 100, 60, "Hard", TestMode.PYQ, targetYear))
                }
            }

            _tests.value = dummyTests
            _isLoading.value = false
        }
    }
}
