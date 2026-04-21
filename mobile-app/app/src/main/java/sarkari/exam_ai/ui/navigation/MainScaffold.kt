package sarkari.exam_ai.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.launch
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import sarkari.exam_ai.R
import sarkari.exam_ai.ui.components.SarkariTopAppBar
import sarkari.exam_ai.ui.theme.*
import androidx.compose.ui.Alignment

// ─── Bottom nav: Home, Tests, Study, AI + Menu ────────────────────────────────
sealed class BottomNavItem(val route: String, val titleRes: Int, val icon: ImageVector, val tint: Color) {
    object Dashboard  : BottomNavItem(Screen.Dashboard.route,    R.string.nav_home,  Icons.Default.Home,         PrimaryBlue)
    object MockTest   : BottomNavItem(Screen.MockTest.route,     R.string.nav_tests, Icons.Default.Assignment,   Color(0xFF111827))
    object StudyTools : BottomNavItem(Screen.StudyPlanner.route, R.string.nav_study, Icons.Default.AutoFixHigh,  AccentGreen)
    object Tutor      : BottomNavItem(Screen.Tutor.route,        R.string.nav_ai,    Icons.Default.SupportAgent, PrimaryBlue)
}

data class DrawerFeature(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
    val tint: Color,
    val sectionRes: Int? = null
)

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // Standard LEFT-side drawer (original behaviour)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showNavigation = currentDestination?.route != null &&
        currentDestination.route !in listOf(
            Screen.Onboarding.route, Screen.Splash.route,
            Screen.Login.route, Screen.Signup.route
        )

    val routeTitleResMap = mapOf(
        Screen.Dashboard.route         to R.string.title_dashboard,
        Screen.MockTest.route          to R.string.title_mock_test,
        Screen.Tutor.route             to R.string.title_tutor,
        Screen.Analytics.route         to R.string.title_analytics,
        Screen.StudyPlanner.route      to R.string.title_study_planner,
        Screen.PyqLibrary.route        to R.string.title_pyq_library,
        Screen.NotesGenerator.route    to R.string.title_notes_generator,
        Screen.CurrentAffairs.route    to R.string.title_current_affairs,
        Screen.Settings.route          to R.string.title_settings,
        Screen.Blog.route              to R.string.title_blog,
        Screen.PastPaperAnalyzer.route to R.string.title_past_paper,
        Screen.Pdfs.route              to R.string.title_pdf_library,
        Screen.Community.route         to R.string.title_community
    )
    val currentTitle = stringResource(routeTitleResMap[currentDestination?.route] ?: R.string.title_dashboard)

    // ── LEFT drawer features (Social + Analytics moved here from bottom bar) ──
    val drawerFeatures = listOf(
        // MAIN
        DrawerFeature(Screen.CurrentAffairs.route,    R.string.menu_current_affairs, Icons.Default.Newspaper,     AccentOrange,   R.string.section_main),
        DrawerFeature(Screen.Blog.route,              R.string.menu_blog,            Icons.Default.Article,       AccentGreen),
        // TESTS
        DrawerFeature(Screen.PyqLibrary.route,        R.string.menu_pyq_practice,    Icons.Default.HistoryEdu,    AccentSaffron,  R.string.section_tests),
        DrawerFeature(Screen.Pdfs.route,              R.string.menu_pdfs,            Icons.Default.PictureAsPdf,  AccentOrange),
        // STUDY TOOLS
        DrawerFeature(Screen.StudyPlanner.route,      R.string.menu_study_planner,   Icons.Default.CalendarMonth, AccentGreen,    R.string.section_study),
        DrawerFeature(Screen.NotesGenerator.route,    R.string.menu_notes_generator, Icons.Default.AutoFixHigh,   AccentGreen),
        DrawerFeature(Screen.PastPaperAnalyzer.route, R.string.menu_past_paper,      Icons.Default.Analytics,     AccentOrange),
        // SOCIAL (moved from bottom bar)
        DrawerFeature(Screen.Community.route,         R.string.menu_community,       Icons.Default.Forum,         AccentOrange,   R.string.section_social),
        DrawerFeature(Screen.Community.route,         R.string.menu_study_partner,   Icons.Default.Group,         AccentSaffron),
        // ANALYTICS (moved from bottom bar)
        DrawerFeature(Screen.Analytics.route,         R.string.menu_analytics,       Icons.Default.TrendingUp,    PrimaryBlue,    R.string.section_account),
        DrawerFeature(Screen.Settings.route,          R.string.menu_settings,        Icons.Default.Settings,      TextSecondary)
    )

    val bottomItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.MockTest,
        BottomNavItem.StudyTools,
        BottomNavItem.Tutor
    )

    if (showNavigation) {
        // Standard LEFT-side drawer — no RTL wrapper needed
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        androidx.compose.foundation.Image(
                                            painter = androidx.compose.ui.res.painterResource(
                                                id = sarkari.exam_ai.R.drawable.app_logo
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Icon(Icons.Default.Close, "Close", tint = TextSecondary)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            drawerFeatures.forEach { feature ->
                                feature.sectionRes?.let { sRes ->
                                    item {
                                        Spacer(Modifier.height(8.dp))
                                        DrawerSectionHeader(stringResource(sRes))
                                    }
                                }
                                item {
                                    DrawerItem(
                                        label       = stringResource(feature.titleRes),
                                        icon        = feature.icon,
                                        route       = feature.route,
                                        currentRoute= currentDestination?.route,
                                        navController = navController,
                                        scope       = scope,
                                        drawerState = drawerState,
                                        tint        = feature.tint
                                    )
                                }
                            }
                        }

                        // Footer
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            color = PrimaryBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.size(8.dp).background(AccentGreen, CircleShape))
                                Spacer(Modifier.width(12.dp))
                                Text("Active", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    SarkariTopAppBar(
                        titleText = currentTitle,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLoginClick = { navController.navigate(Screen.Login.route) }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        // ── Home, Tests, Study, AI ────────────────────────
                        bottomItems.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(item.icon, contentDescription = stringResource(item.titleRes), modifier = Modifier.size(22.dp))
                                },
                                label = { Text(stringResource(item.titleRes), fontSize = 9.sp) },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor   = item.tint,
                                    selectedTextColor   = item.tint,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor      = Color.Transparent
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                content(paddingValues)
            }
        }
    } else {
        Scaffold { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    route: String,
    currentRoute: String?,
    navController: NavHostController,
    scope: kotlinx.coroutines.CoroutineScope,
    drawerState: DrawerState,
    tint: Color
) {
    val isSelected = currentRoute == route
    Surface(
        onClick = {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
            scope.launch { drawerState.close() }
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryBg else Color.Transparent
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(32.dp), color = tint.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = if (isSelected) PrimaryBlue else tint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryBlue else TextPrimary
            )
        }
    }
}
