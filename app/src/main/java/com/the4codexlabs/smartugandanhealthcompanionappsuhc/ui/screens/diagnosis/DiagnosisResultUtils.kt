package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.diagnosis

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Data class representing a diagnosis result.
 */
data class DiagnosisResult(
    val condition: String,
    val recommendation: String,
    val confidence: Int
)
