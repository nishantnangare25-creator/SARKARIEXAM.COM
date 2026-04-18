package com.sarkari.exam.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    // Main bottom nav screens
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object MockTest : Screen("mock_test", "Tests", Icons.Default.LibraryBooks)
    object Tutor : Screen("tutor", "AI Tutor", Icons.Default.Face)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Assessment)
    object StudyPlanner : Screen("study_planner")
    object PYQLibrary : Screen("pyq_library")
    object PYQTest : Screen("pyq_test")
    object NotesGenerator : Screen("notes_generator")
    object PastPaperAnalyzer : Screen("past_paper_analyzer")
    object CurrentAffairs : Screen("current_affairs")
    object Blog : Screen("blog")
    object Forum : Screen("forum")
    object PeerMatching : Screen("peer_matching")
    object Settings : Screen("settings")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.MockTest,
    Screen.Tutor,
    Screen.Analytics
)

@Composable
fun AppNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Check if current screen should show bottom bar and drawer capabilities (exclude login, onboarding)
    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar, // Only allow swiping drawer on main screens
        drawerContent = {
            if (showBottomBar) {
                com.sarkari.exam.ui.components.SarkariSidebar(
                    navController = navController,
                    currentRoute = currentRoute,
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                                label = { Text(screen.title!!) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    if (!isSelected) {
                                        navController.navigate(screen.route) {
                                            if (screen.route == Screen.Dashboard.route) {
                                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                                                launchSingleTop = true
                                            } else {
                                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    com.sarkari.exam.ui.screens.dashboard.DashboardScreen(navController) {
                        scope.launch { drawerState.open() }
                    }
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
                composable(Screen.PYQLibrary.route) {
                    com.sarkari.exam.ui.screens.pyq.PYQLibraryScreen(navController)
                }
                composable(Screen.PYQTest.route) {
                    com.sarkari.exam.ui.screens.pyq.PYQTestScreen(navController)
                }
                composable(Screen.NotesGenerator.route) {
                    com.sarkari.exam.ui.screens.planner.NotesGeneratorScreen(navController)
                }
                composable(Screen.PastPaperAnalyzer.route) {
                    com.sarkari.exam.ui.screens.planner.PastPaperAnalyzerScreen(navController)
                }
                composable(Screen.CurrentAffairs.route) {
                    com.sarkari.exam.ui.screens.community.CurrentAffairsScreen(navController)
                }
                composable(Screen.Blog.route) {
                    com.sarkari.exam.ui.screens.community.BlogScreen(navController)
                }
                composable(Screen.Forum.route) {
                    com.sarkari.exam.ui.screens.community.ForumScreen(navController)
                }
                composable(Screen.PeerMatching.route) {
                    com.sarkari.exam.ui.screens.community.PeerMatchingScreen(navController)
                }
                composable(Screen.Settings.route) {
                    com.sarkari.exam.ui.screens.community.SettingsScreen(navController)
                }
                // Placeholder routes
                composable("coming_soon") {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(16.dp))
                            Text("This feature is coming soon!", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

