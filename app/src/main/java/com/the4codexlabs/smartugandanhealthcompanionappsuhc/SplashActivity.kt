package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash screen activity that displays the app logo and tagline.
 * Automatically navigates to the main activity or login screen after a delay.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            SmartUgandanHealthCompanionAppSUHCTheme {
                SplashScreen()
            }
        }
        
        // Navigate to the appropriate screen after a delay
        lifecycleScope.launch {
            delay(2000) // 2 seconds delay
            navigateToNextScreen()
        }
    }
    
    /**
     * Navigate to the main activity or login screen based on authentication status.
     */
    private fun navigateToNextScreen() {
        // Check if user is logged in
        val currentUser = auth.currentUser
        
        // Navigate to appropriate screen
        val intent = if (currentUser != null) {
            // User is logged in, go to main activity
            Intent(this, MainActivity::class.java)
        } else {
            // User is not logged in, go to auth activity
            Intent(this, AuthActivity::class.java)
        }
        
        startActivity(intent)
        finish() // Close the splash activity
    }
}

/**
 * Composable function for the splash screen UI.
 */
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.baseline_add_moderator_24),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // App Tagline
            Text(
                text = "Your Health Companion in Uganda",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}