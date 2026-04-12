package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Peer(
    val id: String,
    val name: String,
    val exam: String,
    val level: String,
    val subjects: List<String>,
    val avatar: String,
    val studyHours: Int,
    val streak: Int,
    var isConnected: Boolean = false
)

class PeerMatchingViewModel : ViewModel() {
    val peers = mutableStateListOf<Peer>()

    init {
        peers.addAll(listOf(
            Peer("1", "Priya Sharma", "UPSC", "Intermediate", listOf("History", "Polity"), "👩‍🎓", 6, 12),
            Peer("2", "Rohit Kumar", "UPSC", "Beginner", listOf("Geography", "Economy"), "👨‍💻", 4, 8),
            Peer("3", "Sneha Patil", "MPSC", "Advanced", listOf("Marathi", "History"), "👩‍💼", 8, 30),
            Peer("4", "Amit Deshmukh", "SSC", "Intermediate", listOf("Reasoning", "Math"), "🧑‍🎓", 5, 15),
            Peer("5", "Fatima Khan", "Banking", "Beginner", listOf("Quantitative", "English"), "👩‍🏫", 3, 5),
            Peer("6", "Arjun Reddy", "NDA", "Intermediate", listOf("Math", "Physics"), "💂", 7, 22)
        ))
    }

    fun toggleConnect(peerId: String) {
        val index = peers.indexOfFirst { it.id == peerId }
        if (index != -1) {
            val peer = peers[index]
            peers[index] = peer.copy(isConnected = !peer.isConnected)
        }
    }
}
