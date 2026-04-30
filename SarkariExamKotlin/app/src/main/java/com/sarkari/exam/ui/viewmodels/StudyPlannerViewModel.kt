package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class Priority { HIGH, MEDIUM, LOW }

data class StudyTask(
    val id: String,
    val title: String,
    val durationMin: Int,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val subject: String
)

class StudyPlannerViewModel : ViewModel() {

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

    // Weekly Schedule
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val _selectedDay = MutableStateFlow("Mon")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    // Tasks State
    private val _tasks = MutableStateFlow<List<StudyTask>>(emptyList())
    val tasks: StateFlow<List<StudyTask>> = _tasks.asStateFlow()

    init {
        loadDummyTasks()
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

    fun onDaySelected(day: String) {
        _selectedDay.value = day
        // Ideally we would fetch tasks for this day from DB
    }

    fun toggleTaskCompletion(taskId: String) {
        val updatedList = _tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
        _tasks.value = updatedList
    }

    fun addTask(title: String, duration: Int, priority: Priority) {
        val newTask = StudyTask(
            id = UUID.randomUUID().toString(),
            title = title,
            durationMin = duration,
            priority = priority,
            subject = _selectedSubject.value
        )
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(newTask)
        _tasks.value = currentTasks
    }

    private fun loadDummyTasks() {
        _tasks.value = listOf(
            StudyTask("1", "Quantitative Aptitude Practice", 45, true, Priority.HIGH, "Quantitative Aptitude"),
            StudyTask("2", "Read The Hindu Editorial", 30, false, Priority.MEDIUM, "English"),
            StudyTask("3", "Polity Revision (Fundamental Rights)", 60, false, Priority.HIGH, "Polity"),
            StudyTask("4", "Current Affairs Daily Quiz", 15, false, Priority.LOW, "General Awareness")
        )
    }

    fun getCompletedTasksCount(): Int = _tasks.value.count { it.isCompleted }
    fun getTotalStudyHours(): Float = _tasks.value.sumOf { it.durationMin } / 60f
}
