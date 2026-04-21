package sarkari.exam_ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    object MockTest : Screen("mock_test")
    object Tutor : Screen("tutor")
    object Analytics : Screen("analytics")
    object StudyPlanner : Screen("study_planner")
    object PyqLibrary : Screen("pyq_library")
    object NotesGenerator : Screen("notes_generator")
    object CurrentAffairs : Screen("current_affairs")
    object Settings : Screen("settings")
    object AboutUs : Screen("about_us")
    object PrivacyPolicy : Screen("privacy_policy")
    object Blog : Screen("blog")
    object PastPaperAnalyzer : Screen("past_paper_analyzer")
    object Pdfs : Screen("pdfs")
    object Idea : Screen("idea")
    object Community : Screen("community")
}

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: sarkari.exam_ai.ui.viewmodels.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    MainScaffold(navController = navController) { paddingValues ->
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) {
                sarkari.exam_ai.ui.screens.onboarding.SplashScreen(navController)
            }
            composable(Screen.Onboarding.route) {
                sarkari.exam_ai.ui.screens.onboarding.OnboardingScreen(navController)
            }
            composable(Screen.Dashboard.route) {
                sarkari.exam_ai.ui.screens.dashboard.DashboardScreen(navController, paddingValues)
            }
            composable(Screen.MockTest.route) {
                sarkari.exam_ai.ui.screens.mocktest.MockTestScreen(navController, paddingValues)
            }
            composable(Screen.Tutor.route) {
                sarkari.exam_ai.ui.screens.tutor.InteractiveTutorScreen(navController, paddingValues)
            }
            composable(Screen.Analytics.route) {
                sarkari.exam_ai.ui.screens.analytics.AnalyticsScreen(navController, paddingValues)
            }
            composable(Screen.Login.route) {
                sarkari.exam_ai.ui.screens.auth.LoginScreen(navController, viewModel = authViewModel)
            }
            composable(Screen.Signup.route) {
                sarkari.exam_ai.ui.screens.auth.SignupScreen(navController, viewModel = authViewModel)
            }
            composable(Screen.StudyPlanner.route) {
                sarkari.exam_ai.ui.screens.planner.StudyPlannerScreen(navController, paddingValues)
            }
            composable(Screen.PyqLibrary.route) {
                sarkari.exam_ai.ui.screens.pyq.PyqLibraryScreen(navController, paddingValues)
            }
            composable(Screen.NotesGenerator.route) {
                sarkari.exam_ai.ui.screens.planner.NotesGeneratorScreen(navController, paddingValues)
            }
            composable(Screen.CurrentAffairs.route) {
                sarkari.exam_ai.ui.screens.dashboard.CurrentAffairsScreen(navController, paddingValues)
            }
            composable(Screen.Settings.route) {
                sarkari.exam_ai.ui.screens.dashboard.SettingsScreen(navController, paddingValues)
            }
            composable(Screen.AboutUs.route) {
                sarkari.exam_ai.ui.screens.dashboard.AboutUsScreen(navController, paddingValues)
            }
            composable(Screen.PrivacyPolicy.route) {
                sarkari.exam_ai.ui.screens.dashboard.PrivacyPolicyScreen(navController, paddingValues)
            }
            composable(Screen.Blog.route) {
                sarkari.exam_ai.ui.screens.dashboard.BlogScreen(navController, paddingValues)
            }
            composable(Screen.PastPaperAnalyzer.route) {
                sarkari.exam_ai.ui.screens.planner.PastPaperAnalyzerScreen(navController, paddingValues)
            }
            composable(Screen.Pdfs.route) {
                sarkari.exam_ai.ui.screens.dashboard.PdfsScreen(navController, paddingValues)
            }
            composable(Screen.Idea.route) {
                sarkari.exam_ai.ui.screens.dashboard.IdeaScreen(navController, paddingValues)
            }
            composable(Screen.Community.route) {
                sarkari.exam_ai.ui.screens.dashboard.CommunityScreen(navController, paddingValues)
            }
        }
    }
}
