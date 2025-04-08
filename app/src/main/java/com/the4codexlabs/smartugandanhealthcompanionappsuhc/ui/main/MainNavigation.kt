package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.dashboard.DashboardScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.profile.ProfileScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.*
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.ThemeProvider
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.isAppInDarkTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.AppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.LanguageProvider
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.StringResources
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.currentAppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.getTranslation

sealed class Screen(
    val route: String,
    val title: Map<AppLanguage, String>,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", StringResources.dashboard, Icons.Outlined.Dashboard)
    object HealthRecords : Screen("health_records", StringResources.healthRecords, Icons.Outlined.Folder)
    object MedicationReminder : Screen("medication_reminder", StringResources.medicationReminders, Icons.Outlined.Medication)
    object SymptomTracker : Screen("symptom_tracker", StringResources.symptomTracker, Icons.Outlined.Analytics)
    object HealthEducation : Screen("health_education", StringResources.healthEducation, Icons.Outlined.MenuBook)
    object Community : Screen("community", StringResources.community, Icons.Outlined.Group)
    object Wearables : Screen("wearables", StringResources.wearables, Icons.Outlined.Watch)
    object Notifications : Screen("notifications", StringResources.notifications, Icons.Outlined.Notifications)
    object Profile : Screen("profile", StringResources.profile, Icons.Outlined.Person)
}

val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.MedicationReminder,
    Screen.HealthRecords,
    Screen.Community,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val currentLanguage = currentAppLanguage()
    
    SmartUgandanHealthCompanionAppSUHCTheme {
        Scaffold(
            bottomBar = { MainBottomBar(navController = navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onFeatureClick = { route -> navController.navigate(route) },
                        onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                        onProfileClick = { navController.navigate(Screen.Profile.route) }
                    )
                }
                
                composable(Screen.HealthRecords.route) {
                    HealthRecordsScreen(navController = navController)
                }
                
                composable(Screen.MedicationReminder.route) {
                    MedicationReminderScreen(navController = navController)
                }
                
                composable(Screen.SymptomTracker.route) {
                    SymptomTrackerScreen(navController = navController)
                }
                
                composable(Screen.HealthEducation.route) {
                    HealthEducationScreen()
                }
                
                composable(Screen.Community.route) {
                    CommunityScreen()
                }
                
                composable(Screen.Wearables.route) {
                    WearablesScreen(navController = navController)
                }
                
                composable(Screen.Notifications.route) {
                    NotificationsScreen()
                }
                
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLanguageChange = { language -> 
                            LanguageProvider.setLanguage(language)
                        },
                        onThemeChange = { themeMode ->
                            ThemeProvider.setThemeMode(themeMode)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavScreens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = screen.icon, 
                        contentDescription = getTranslation(screen.title)
                    ) 
                },
                label = { 
                    Text(
                        text = getTranslation(screen.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
} 