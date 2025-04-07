package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the Smart Ugandan Health Companion App
 * These shapes are used throughout the app for consistent UI elements
 */

// Standard corner radius values
val ExtraSmallCornerRadius = 4.dp
val SmallCornerRadius = 8.dp
val MediumCornerRadius = 16.dp
val LargeCornerRadius = 24.dp
val ExtraLargeCornerRadius = 32.dp
val CircularCornerRadius = 50.dp

// Elevation values for drop shadows
val NoElevation = 0.dp
val ExtraSmallElevation = 1.dp
val SmallElevation = 2.dp
val MediumElevation = 4.dp
val LargeElevation = 8.dp
val ExtraLargeElevation = 16.dp

// Shape definitions for the app
val AppShapes = Shapes(
    // Small components like chips, buttons, etc.
    small = RoundedCornerShape(SmallCornerRadius),
    
    // Medium components like cards, dialogs, etc.
    medium = RoundedCornerShape(MediumCornerRadius),
    
    // Large components like bottom sheets, etc.
    large = RoundedCornerShape(LargeCornerRadius)
)

// Additional custom shapes
val CardShape = RoundedCornerShape(LargeCornerRadius)
val ButtonShape = RoundedCornerShape(CircularCornerRadius)
val InputFieldShape = RoundedCornerShape(MediumCornerRadius)
val BottomSheetShape = RoundedCornerShape(
    topStart = LargeCornerRadius,
    topEnd = LargeCornerRadius,
    bottomStart = ExtraSmallCornerRadius,
    bottomEnd = ExtraSmallCornerRadius
)
val TopAppBarShape = RoundedCornerShape(
    bottomStart = MediumCornerRadius,
    bottomEnd = MediumCornerRadius
)
val FloatingActionButtonShape = RoundedCornerShape(CircularCornerRadius)
val SOSButtonShape = RoundedCornerShape(CircularCornerRadius)