package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.dashboard.DashboardScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.diagnosis.DiagnosisScreenFixed2
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.health.HealthTrackingScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.profile.EditProfileScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.profile.PrivacyScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.profile.ProfileScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.sos.SOSScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme

/**
 * Main activity for the Smart Ugandan Health Companion App.
 * Handles navigation between different screens and the bottom navigation bar.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartUgandanHealthCompanionAppSUHCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

/**
 * Sealed class representing the different screens in the app.
 */
sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", R.string.dashboard, Icons.Filled.Dashboard)
    object HealthTracking : Screen("health_tracking", R.string.health_tracking, Icons.Filled.Favorite)
    object Diagnosis : Screen("diagnosis", R.string.diagnosis, Icons.Filled.HealthAndSafety)
    object SOS : Screen("sos", R.string.sos, Icons.Filled.Warning)
    object Profile : Screen("profile", R.string.profile, Icons.Filled.AccountCircle)
}

/**
 * List of screens for the bottom navigation.
 */
val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.HealthTracking,
    Screen.Diagnosis,
    Screen.SOS,
    Screen.Profile
)

/**
 * Main composable function that sets up the app's navigation and UI structure.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController)
            }
            composable(Screen.HealthTracking.route) {
                HealthTrackingScreen(navController)
            }
            composable(Screen.Diagnosis.route) {
                DiagnosisScreenFixed2(navController)
            }
            composable(Screen.SOS.route) {
                SOSScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            
            // Secondary screens (not in bottom navigation)
            composable("edit_profile") {
                EditProfileScreen(navController)
            }
            
            composable("privacy_settings") {
                PrivacyScreen(navController)
            }
        }
    }
}

/**
 * Bottom navigation bar composable.
 */
@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.titleResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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

/**
 * Preview function for the main screen.
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        MainScreen()
    }
}