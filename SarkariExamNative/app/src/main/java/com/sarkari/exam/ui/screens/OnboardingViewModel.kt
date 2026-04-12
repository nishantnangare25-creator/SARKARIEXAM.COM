package com.sarkari.exam.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {
    private val _step = mutableStateOf(1)
    val step: State<Int> = _step

    private val _exam = mutableStateOf("")
    val exam: State<String> = _exam

    private val _language = mutableStateOf("en")
    val language: State<String> = _language

    private val _hours = mutableStateOf(4)
    val hours: State<Int> = _hours

    private val _level = mutableStateOf("beginner")
    val level: State<String> = _level

    private val _weakSubjects = mutableStateOf<List<String>>(emptyList())
    val weakSubjects: State<List<String>> = _weakSubjects

    private val _strongSubjects = mutableStateOf<List<String>>(emptyList())
    val strongSubjects: State<List<String>> = _strongSubjects

    fun setExam(id: String) { _exam.value = id }
    fun setLanguage(code: String) { _language.value = code }
    fun setHours(h: Int) { _hours.value = h }
    fun setLevel(lvl: String) { _level.value = lvl }

    fun toggleSubject(subject: String, isWeak: Boolean) {
        if (isWeak) {
            val current = _weakSubjects.value.toMutableList()
            if (current.contains(subject)) current.remove(subject) else current.add(subject)
            _weakSubjects.value = current
            // Remove from strong if exists
            val sCurrent = _strongSubjects.value.toMutableList()
            sCurrent.remove(subject)
            _strongSubjects.value = sCurrent
        } else {
            val current = _strongSubjects.value.toMutableList()
            if (current.contains(subject)) current.remove(subject) else current.add(subject)
            _strongSubjects.value = current
            // Remove from weak if exists
            val wCurrent = _weakSubjects.value.toMutableList()
            wCurrent.remove(subject)
            _weakSubjects.value = wCurrent
        }
    }

    fun nextStep() { if (_step.value < 4) _step.value++ }
    fun previousStep() { if (_step.value > 1) _step.value-- }
    
    fun getSubjectsForExam(): List<String> {
        return com.sarkari.exam.domain.Constants.SUBJECTS[_exam.value] ?: emptyList()
    }
}
