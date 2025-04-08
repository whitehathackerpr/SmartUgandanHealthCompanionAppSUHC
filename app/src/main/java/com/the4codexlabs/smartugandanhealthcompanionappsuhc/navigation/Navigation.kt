sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object QRMedicalID : Screen("qr_medical_id")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object About : Screen("about")
    object Privacy : Screen("privacy")
    object Terms : Screen("terms")
    object Contact : Screen("contact")
    object Feedback : Screen("feedback")
    object FAQ : Screen("faq")
    object Support : Screen("support")
    object Notifications : Screen("notifications")
    object Language : Screen("language")
    object Theme : Screen("theme")
    object Logout : Screen("logout")
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLanguageChange = { /* TODO: Handle language change */ },
                onThemeChange = { /* TODO: Handle theme change */ },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditProfile = { profile ->
                    navController.navigate(Screen.EditProfile.route)
                },
                onQRMedicalIDClick = {
                    navController.navigate(Screen.QRMedicalID.route)
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onSave = {
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.QRMedicalID.route) {
            QRMedicalIDScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Help.route) {
            HelpScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Privacy.route) {
            PrivacyScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Terms.route) {
            TermsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Contact.route) {
            ContactScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Feedback.route) {
            FeedbackScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.FAQ.route) {
            FAQScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Support.route) {
            SupportScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Language.route) {
            LanguageScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Theme.route) {
            ThemeScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Logout.route) {
            LogoutScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }
    }
} 