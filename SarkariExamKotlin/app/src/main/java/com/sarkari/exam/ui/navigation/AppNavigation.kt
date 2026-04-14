package com.sarkari.exam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object MockTest : Screen("mock_test")
    object Tutor : Screen("tutor")
    object Analytics : Screen("analytics")
    object StudyPlanner : Screen("study_planner")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
             com.sarkari.exam.ui.screens.dashboard.DashboardScreen(navController)
        }
        composable(Screen.MockTest.route) {
            com.sarkari.exam.ui.screens.mocktest.MockTestScreen(navController)
        }
        composable(Screen.Tutor.route) {
            com.sarkari.exam.ui.screens.tutor.InteractiveTutorScreen(navController)
        }
        composable(Screen.Analytics.route) {
            com.sarkari.exam.ui.screens.analytics.AnalyticsScreen(navController)
        }
        composable(Screen.Login.route) {
            com.sarkari.exam.ui.screens.auth.LoginScreen(navController)
        }
        composable(Screen.StudyPlanner.route) {
            com.sarkari.exam.ui.screens.planner.StudyPlannerScreen(navController)
        }
    }
}
