package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class DetailedStudyPartner(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val initials: String,
    val targetExam: String,
    val subjects: List<String>,
    val isOnline: Boolean,
    val matchScore: Int,
    val accuracy: String,
    val streak: Int,
    val dailyHours: String
)

data class MyProfileSummary(
    val name: String = "Aspirant",
    val targetExam: String = "SSC CGL 2024",
    val accuracy: String = "78%",
    val streak: Int = 12,
    val dailyHours: String = "4.5h"
)

class StudyPartnersViewModel : ViewModel() {

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

    // Filters
    val filterOptions = listOf("Online", "Same Exam", "Same Subject")
    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Data
    private val _myProfile = MutableStateFlow(MyProfileSummary())
    val myProfile: StateFlow<MyProfileSummary> = _myProfile.asStateFlow()

    private val _allPartners = MutableStateFlow<List<DetailedStudyPartner>>(emptyList())
    private val _filteredPartners = MutableStateFlow<List<DetailedStudyPartner>>(emptyList())
    val filteredPartners: StateFlow<List<DetailedStudyPartner>> = _filteredPartners.asStateFlow()

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
        applyFilters()
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun toggleFilter(filter: String) {
        val current = _selectedFilters.value.toMutableSet()
        if (current.contains(filter)) {
            current.remove(filter)
        } else {
            current.add(filter)
        }
        _selectedFilters.value = current
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = _allPartners.value
        val query = _searchQuery.value.lowercase()
        val filters = _selectedFilters.value

        if (query.isNotEmpty()) {
            filteredList = filteredList.filter { 
                it.name.lowercase().contains(query) || it.targetExam.lowercase().contains(query) 
            }
        }

        if (filters.contains("Online")) {
            filteredList = filteredList.filter { it.isOnline }
        }
        if (filters.contains("Same Exam")) {
            filteredList = filteredList.filter { it.targetExam.contains(_selectedExam.value) }
        }
        if (filters.contains("Same Subject") && _selectedSubject.value != "All Subjects") {
            filteredList = filteredList.filter { it.subjects.contains(_selectedSubject.value) }
        }

        // Sort by match score descending
        _filteredPartners.value = filteredList.sortedByDescending { it.matchScore }
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(600)
            
            _allPartners.value = listOf(
                DetailedStudyPartner(
                    name = "Ramesh Kumar", initials = "RK", targetExam = "SSC CGL", 
                    subjects = listOf("Quantitative Aptitude", "Reasoning"), 
                    isOnline = true, matchScore = 85, accuracy = "82%", streak = 15, dailyHours = "5h"
                ),
                DetailedStudyPartner(
                    name = "Sneha Sharma", initials = "SS", targetExam = "UPSC Civil Services", 
                    subjects = listOf("History", "Polity"), 
                    isOnline = false, matchScore = 45, accuracy = "70%", streak = 5, dailyHours = "3h"
                ),
                DetailedStudyPartner(
                    name = "Amit Singh", initials = "AS", targetExam = "SSC CGL", 
                    subjects = listOf("English", "General Awareness"), 
                    isOnline = true, matchScore = 75, accuracy = "65%", streak = 8, dailyHours = "4h"
                ),
                DetailedStudyPartner(
                    name = "Priya Das", initials = "PD", targetExam = "Banking", 
                    subjects = listOf("Quant", "Reasoning"), 
                    isOnline = true, matchScore = 60, accuracy = "88%", streak = 21, dailyHours = "6h"
                )
            )
            
            applyFilters()
            _isLoading.value = false
        }
    }
}
