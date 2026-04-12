package com.sarkari.exam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sarkari.exam.ui.screens.LoginScreen
import com.sarkari.exam.ui.screens.DashboardScreen
import com.sarkari.exam.ui.screens.MockTestScreen
import com.sarkari.exam.ui.screens.OnboardingScreen
import com.sarkari.exam.ui.screens.StudyPlannerScreen
import com.sarkari.exam.ui.screens.SettingsScreen
import com.sarkari.exam.ui.screens.InteractiveTutorScreen
import com.sarkari.exam.ui.screens.CurrentAffairsScreen
import com.sarkari.exam.ui.screens.CurrentAffairsScreen
import com.sarkari.exam.ui.screens.AnalyticsScreen
import com.sarkari.exam.ui.screens.PYQLibraryScreen
import com.sarkari.exam.ui.screens.PastPaperAnalyzerScreen
import com.sarkari.exam.ui.screens.ForumScreen
import com.sarkari.exam.ui.screens.PeerMatchingScreen
import com.sarkari.exam.ui.screens.BlogScreen
import com.sarkari.exam.ui.screens.NotesGeneratorScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onNavigateToMockTest = { navController.navigate("mock_test") },
                onNavigateToStudyPlanner = { navController.navigate("study_planner") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToTutor = { navController.navigate("interactive_tutor") },
                onNavigateToNews = { navController.navigate("current_affairs") },
                onNavigateToAnalytics = { navController.navigate("analytics") },
                onNavigateToPyq = { navController.navigate("pyq_library") },
                onNavigateToForum = { navController.navigate("forum") },
                onNavigateToPeers = { navController.navigate("peers") },
                onNavigateToNotes = { navController.navigate("notes_generator") },
                onNavigateToBlog = { navController.navigate("blog") }
            )
        }
        composable("mock_test") {
            MockTestScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("study_planner") {
            StudyPlannerScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("interactive_tutor") {
            InteractiveTutorScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("current_affairs") {
            CurrentAffairsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("analytics") {
            AnalyticsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("pyq_library") {
            PYQLibraryScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToMockTest = { navController.navigate("mock_test") },
                onNavigateToAnalyzer = { navController.navigate("past_paper_analyzer") }
            )
        }
        composable("past_paper_analyzer") {
            PastPaperAnalyzerScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("forum") {
            ForumScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("peers") {
            PeerMatchingScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("notes_generator") {
            NotesGeneratorScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("blog") {
            BlogScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
