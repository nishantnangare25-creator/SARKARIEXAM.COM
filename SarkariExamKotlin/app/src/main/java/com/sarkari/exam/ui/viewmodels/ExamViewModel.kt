package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.local.AppPreferences
import com.sarkari.exam.data.models.ExamItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val appPreferences = AppPreferences(application)

    val exams = listOf(
        ExamItem("upsc", "UPSC Civil Services", "🏛"),
        ExamItem("mpsc", "MPSC", "📋"),
        ExamItem("ssc", "SSC CGL/CHSL", "✏"),
        ExamItem("banking", "Banking (IBPS/SBI)", "🏦"),
        ExamItem("railway", "Railway (RRB)", "🚂"),
        ExamItem("nda", "NDA", "🎖"),
        ExamItem("state_psc", "State PSC", "🗳")
    )

    val selectedExamCode: StateFlow<String> = appPreferences.examCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "UPSC Civil Services")

    fun selectExam(examName: String) {
        viewModelScope.launch {
            appPreferences.saveExamCode(examName)
        }
    }
}
