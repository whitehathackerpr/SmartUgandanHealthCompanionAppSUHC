package com.the4codexlabs.smartugandanhealthcompanionappsuhc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.main.MainNavigation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.isAppInDarkTheme

/**
 * Main activity for the Smart Ugandan Health Companion App.
 * Handles navigation between different screens and the bottom navigation bar.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Use the MainNavigation composable which handles theme and language
            MainNavigation()
        }
    }
}