package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PyqPdf(
    val id: String,
    val title: String,
    val subject: String,
    val year: String,
    val size: String,
    val exam: String,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f
)

class PyqPdfViewModel : ViewModel() {

    // Dynamic Options
    val examsList = listOf("SSC CGL", "UPSC Civil Services", "Banking (IBPS/SBI)", "Railway (RRB)")
    private val subjectMap = mapOf(
        "SSC CGL" to listOf("All Subjects", "Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("All Subjects", "History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking (IBPS/SBI)" to listOf("All Subjects", "Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway (RRB)" to listOf("All Subjects", "Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All Subjects")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Filters
    val yearsList = listOf("All Years", "2024", "2023", "2022", "2021", "2020")
    private val _selectedYear = MutableStateFlow("All Years")
    val selectedYear: StateFlow<String> = _selectedYear.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // PDFs
    private val _allPdfs = MutableStateFlow<List<PyqPdf>>(emptyList())
    private val _filteredPdfs = MutableStateFlow<List<PyqPdf>>(emptyList())
    val filteredPdfs: StateFlow<List<PyqPdf>> = _filteredPdfs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPdfs()
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: listOf("All Subjects")
        _availableSubjects.value = subjects
        _selectedSubject.value = "All Subjects"
        applyFilters()
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        applyFilters()
    }

    fun onYearSelected(year: String) {
        _selectedYear.value = year
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun downloadPdf(id: String) {
        viewModelScope.launch {
            // Update to downloading state
            _allPdfs.value = _allPdfs.value.map {
                if (it.id == id) it.copy(isDownloading = true, downloadProgress = 0f) else it
            }
            applyFilters()

            // Simulate progress
            for (progress in 10..100 step 20) {
                delay(400)
                _allPdfs.value = _allPdfs.value.map {
                    if (it.id == id) it.copy(downloadProgress = progress / 100f) else it
                }
                applyFilters()
            }

            // Finish download
            _allPdfs.value = _allPdfs.value.map {
                if (it.id == id) it.copy(isDownloading = false, isDownloaded = true, downloadProgress = 1f) else it
            }
            applyFilters()
        }
    }

    private fun applyFilters() {
        val exam = _selectedExam.value
        val subject = _selectedSubject.value
        val year = _selectedYear.value
        val query = _searchQuery.value.lowercase()

        val filtered = _allPdfs.value.filter {
            val matchesExam = it.exam.contains(exam)
            val matchesSubject = subject == "All Subjects" || it.subject.contains(subject) || it.title.contains(subject)
            val matchesYear = year == "All Years" || it.year == year
            val matchesQuery = query.isEmpty() || it.title.lowercase().contains(query) || it.subject.lowercase().contains(query)

            matchesExam && matchesSubject && matchesYear && matchesQuery
        }

        _filteredPdfs.value = filtered
    }

    private fun loadPdfs() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            val dummyList = listOf(
                PyqPdf("1", "SSC CGL 2023 Tier 1 (Shift 1)", "Quantitative Aptitude", "2023", "2.5 MB", "SSC CGL"),
                PyqPdf("2", "SSC CGL 2023 Tier 1 (Shift 2)", "English", "2023", "1.8 MB", "SSC CGL"),
                PyqPdf("3", "SSC CGL 2022 Tier 2 Full Paper", "All Subjects", "2022", "5.2 MB", "SSC CGL", isDownloaded = true),
                PyqPdf("4", "UPSC GS Paper 1", "History & Geography", "2023", "3.4 MB", "UPSC Civil Services"),
                PyqPdf("5", "UPSC CSAT Paper 2", "CSAT", "2023", "2.1 MB", "UPSC Civil Services"),
                PyqPdf("6", "IBPS PO Prelims 2024", "Quant & Reasoning", "2024", "2.8 MB", "Banking (IBPS/SBI)")
            )
            _allPdfs.value = dummyList
            applyFilters()
            _isLoading.value = false
        }
    }
}
