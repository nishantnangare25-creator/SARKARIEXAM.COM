package com.sarkari.exam.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sarkari.exam.ui.theme.BackgroundLight
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.theme.SecondaryOrange
import com.sarkari.exam.ui.theme.SurfaceWhite
import com.sarkari.exam.ui.theme.TextPrimary
import com.sarkari.exam.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(navController: NavController) {
    var selectedExam by remember { mutableStateOf("UPSC") }
    var selectedHours by remember { mutableStateOf("4 hrs") }
    var selectedLevel by remember { mutableStateOf("Beginner") }
    var loading by remember { mutableStateOf(false) }
    var plan by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("AI Study Planner", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PrimaryBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Personalized Roadmap",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Let our AI create the perfect schedule for your exam preparation.",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Selection Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Exam Dropdown (Simplified for prototype)
                    Text("Target Exam", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    DropdownPlaceholder(selectedExam) { selectedExam = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Study Hours per Day", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    DropdownPlaceholder(selectedHours) { selectedHours = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Current Level", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    DropdownPlaceholder(selectedLevel) { selectedLevel = it }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            loading = true
                            // Simulate AI API call
                            plan = "Day 1: Introduction to basics...\nDay 2: Mock Test and Review..."
                            loading = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Master Plan")
                        }
                    }
                }
            }

            if (plan != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Your Generated Plan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Card(
                    modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Text(
                        text = plan!!,
                        modifier = Modifier.padding(16.dp),
                        style = LocalTextStyle.current.copy(lineHeight = 22.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownPlaceholder(text: String, onSelect: (String) -> Unit) {
    // In a real app, this would be a Menu/Dropdown. For now, a simple box UI.
    Surface(
        onClick = { /* Open Menu */ },
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, modifier = Modifier.weight(1f))
            Icon(androidx.compose.material.icons.Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}
