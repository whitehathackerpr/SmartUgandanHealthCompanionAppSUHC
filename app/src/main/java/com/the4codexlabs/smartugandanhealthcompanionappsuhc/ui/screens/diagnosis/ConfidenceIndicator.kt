package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.diagnosis

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Confidence indicator composable.
 * Displays a progress bar with color based on confidence level.
 */
@Composable
fun ConfidenceIndicator(confidence: Int) {
    val progressValue = confidence / 100f
    val progressColor = if (confidence > 70) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    
    LinearProgressIndicator(
        progress = progressValue,
        modifier = Modifier.fillMaxWidth(),
        color = progressColor
    )
}