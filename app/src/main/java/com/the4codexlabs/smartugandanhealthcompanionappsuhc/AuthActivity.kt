package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.auth.LoginScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.auth.SignupScreen
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme

/**
 * Authentication activity that hosts login and signup screens.
 */
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartUgandanHealthCompanionAppSUHCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavigation(
                        onAuthSuccess = {
                            // Navigate to MainActivity on successful authentication
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Close the auth activity
                        }
                    )
                }
            }
        }
    }
}

/**
 * Authentication navigation composable.
 * Sets up navigation between login and signup screens.
 */
@Composable
fun AuthNavigation(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = onAuthSuccess
            )
        }
        
        composable("signup") {
            SignupScreen(
                navController = navController,
                onSignupSuccess = onAuthSuccess
            )
        }
    }
}