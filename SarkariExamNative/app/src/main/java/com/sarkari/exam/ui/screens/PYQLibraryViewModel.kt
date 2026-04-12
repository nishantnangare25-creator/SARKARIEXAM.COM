package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class PyqPdf(
    val id: Int,
    val title: String,
    val examId: String,
    val year: Int,
    val size: String,
    val type: String
)

class PYQLibraryViewModel : ViewModel() {
    val allPdfs = mutableStateListOf<PyqPdf>()
    
    val searchQuery = mutableStateOf("")
    val filterExam = mutableStateOf("")
    val filterYear = mutableStateOf("")

    init {
        loadMockPdfs()
    }

    private fun loadMockPdfs() {
        var idCounter = 1
        val currentYear = 2024
        
        val examsData = listOf(
            Pair("upsc", listOf("UPSC Civil Services Prelims GS 1", "UPSC Prelims CSAT", "UPSC Mains GS 1")),
            Pair("ssc", listOf("SSC CGL Tier 1 Quantitative", "SSC CGL Tier 1 General Awareness")),
            Pair("banking", listOf("IBPS PO Prelims Reasoning", "IBPS PO Prelims Quant")),
            Pair("railway", listOf("RRB NTPC Stage 1 CBT", "RRB Group D Science"))
        )

        val newPdfs = mutableListOf<PyqPdf>()
        for (exam in examsData) {
            for (year in currentYear downTo 2014) {
                exam.second.forEach { title ->
                    newPdfs.add(
                        PyqPdf(
                            id = idCounter++,
                            title = title,
                            examId = exam.first,
                            year = year,
                            size = String.format("%.1f MB", (Math.random() * 3 + 1.2)),
                            type = if (year % 4 == 0) "Question Paper + Solution" else "Question Paper"
                        )
                    )
                }
            }
        }
        allPdfs.addAll(newPdfs)
    }

    fun getFilteredPdfs(): List<PyqPdf> {
        return allPdfs.filter { pdf ->
            val matchExam = filterExam.value.isEmpty() || pdf.examId == filterExam.value
            val matchYear = filterYear.value.isEmpty() || pdf.year.toString() == filterYear.value
            val matchSearch = pdf.title.contains(searchQuery.value, ignoreCase = true)
            matchExam && matchYear && matchSearch
        }
    }
}
