package com.sarkari.exam.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sarkari.exam.ui.navigation.AppDrawer
import com.sarkari.exam.ui.screens.auth.AuthScreen
import com.sarkari.exam.ui.screens.home.HomeScreen
import com.sarkari.exam.ui.screens.premium.PremiumScreen
import com.sarkari.exam.ui.screens.currentaffairs.CurrentAffairsScreen
import com.sarkari.exam.ui.screens.mocktest.MockTestDashboardScreen
import com.sarkari.exam.ui.screens.studyplanner.StudyPlannerScreen
import com.sarkari.exam.ui.screens.notes.NotesGeneratorScreen
import com.sarkari.exam.ui.screens.paperanalyzer.PaperAnalyzerScreen
import com.sarkari.exam.ui.screens.pyqpdfs.PyqPdfScreen
import com.sarkari.exam.ui.screens.riyaai.RiyaAiScreen
import com.sarkari.exam.ui.screens.analytics.AnalyticsScreen
import com.sarkari.exam.ui.screens.community.CommunityScreen
import com.sarkari.exam.ui.screens.studypartners.StudyPartnersScreen
import com.sarkari.exam.ui.screens.settings.SettingsScreen
import com.sarkari.exam.ui.theme.PrimaryBlue
import com.sarkari.exam.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

// Bottom Navigation Item Data
data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", "home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Tests", "mock_test", Icons.Filled.Quiz, Icons.Outlined.Quiz),
    BottomNavItem("Riya AI", "riya_ai", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    BottomNavItem("Community", "community", Icons.Filled.People, Icons.Outlined.People),
    BottomNavItem("Profile", "settings", Icons.Filled.Person, Icons.Outlined.Person)
)

// Routes where bottom bar should be visible
val bottomBarRoutes = setOf(
    "home", "mock_test", "riya_ai", "community", "settings",
    "current_affairs", "analytics", "planner", "notes_gen",
    "paper_analyzer", "pyq_pdfs", "study_partners"
)

@Composable
fun BananiAppMain() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    val userViewModel: UserViewModel = viewModel()
    val userProfile by userViewModel.userProfile.collectAsState()

    val showBottomBar = currentRoute in bottomBarRoutes
    val gesturesEnabled = currentRoute != "auth"

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            if (gesturesEnabled) {
                AppDrawer(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    closeDrawer = { scope.launch { drawerState.close() } },
                    userName = "Aarav Sharma",
                    userSubtitle = if (userProfile.isOnboarded) "Premium User" else "Free User"
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        item.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f),
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "auth",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("auth") {
                    AuthScreen(
                        onAuthSuccess = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigate = { route -> navController.navigate(route) },
                        userName = "Aarav",
                        targetExam = userProfile.exam.ifEmpty { "SSC CGL 2024" }
                    )
                }

                composable("premium") {
                    PremiumScreen(
                        onBackClick = { navController.popBackStack() },
                        onSubscriptionSuccess = {
                            userViewModel.setOnboarded(true)
                            navController.popBackStack()
                        }
                    )
                }

                composable("mock_test") {
                    MockTestDashboardScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("current_affairs") {
                    CurrentAffairsScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("planner") {
                    StudyPlannerScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("notes_gen") {
                    NotesGeneratorScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("paper_analyzer") {
                    PaperAnalyzerScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("pyq_pdfs") {
                    PyqPdfScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("riya_ai") {
                    RiyaAiScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("analytics") {
                    AnalyticsScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("community") {
                    CommunityScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable("study_partners") {
                    StudyPartnersScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onNavigateBack = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

