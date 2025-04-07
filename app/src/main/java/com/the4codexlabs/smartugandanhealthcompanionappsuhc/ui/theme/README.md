# Smart Ugandan Health Companion App - Design System

This document outlines the design system for the Smart Ugandan Health Companion App, providing guidelines for implementing a consistent, beautiful, and professional UI across all screens.

## 🎨 Color System

### Brand Colors
- **Primary**: DeepGreen (#3A8D5D) - Used for primary actions, app bar, and key UI elements
- **Accent**: SunGold (#F4B400) - Used for highlighting important elements, CTAs, and accents

### Light Theme
- **Background**: White (#FFFFFF) - Main background color
- **Surface**: LightSurface (#F8F8F8) - Card and surface backgrounds
- **SurfaceVariant**: LightSurfaceVariant (#EEF2EF) - Alternative surface for variety
- **Text**: LightText (#333333) - Primary text color
- **TextSecondary**: LightTextSecondary (#666666) - Secondary text color
- **Outline**: LightOutline (#DDDDDD) - Borders and dividers

### Dark Theme
- **Background**: DarkBackground (#121212) - Main background color
- **Surface**: DarkSurface (#1E1E1E) - Card and surface backgrounds
- **SurfaceVariant**: DarkSurfaceVariant (#2A3C30) - Alternative surface for variety
- **Text**: DarkText (#FAFAFA) - Primary text color
- **TextSecondary**: DarkTextSecondary (#AAAAAA) - Secondary text color
- **Outline**: DarkOutline (#444444) - Borders and dividers

### Semantic Colors
- **SOSRed**: (#F44336) - Emergency and alert actions
- **SuccessGreen**: (#4CAF50) - Success states and positive feedback
- **WarningAmber**: (#FFB74D) - Warning states and cautionary feedback
- **InfoBlue**: (#2196F3) - Informational elements

### Glassmorphism Colors
- **GlassLight**: (#CCFFFFFF) - Semi-transparent white for light theme
- **GlassDark**: (#CC1E1E1E) - Semi-transparent dark for dark theme

### Gradient Colors
- **GreenGradientStart**: (#2E7D4B) - Darker green for gradients
- **GreenGradientEnd**: (#4CAF50) - Lighter green for gradients
- **GoldGradientStart**: (#F4B400) - Sun gold start
- **GoldGradientEnd**: (#FFD54F) - Lighter gold end

## 📝 Typography

The app uses Google Fonts with a combination of Poppins for headings and Nunito for body text to create a professional and readable typography system.

### Font Families
- **Poppins**: Used for headings, titles, and labels
- **Nunito**: Used for body text for better readability

### Type Scale
- **Display Large**: 57sp, Poppins Light
- **Display Medium**: 45sp, Poppins Light
- **Display Small**: 36sp, Poppins Normal
- **Headline Large**: 32sp, Poppins Medium
- **Headline Medium**: 28sp, Poppins Medium
- **Headline Small**: 24sp, Poppins Medium
- **Title Large**: 22sp, Poppins Bold
- **Title Medium**: 16sp, Poppins Bold
- **Title Small**: 14sp, Poppins Medium
- **Body Large**: 16sp, Nunito Medium
- **Body Medium**: 14sp, Nunito Normal
- **Body Small**: 12sp, Nunito Normal
- **Label Large**: 14sp, Poppins Medium
- **Label Medium**: 12sp, Poppins Medium
- **Label Small**: 11sp, Poppins Medium

## 🔶 Shape System

The app uses a consistent shape system with rounded corners for all UI elements to create a friendly and modern look.

### Corner Radius
- **ExtraSmall**: 4dp - Used for small elements like chips
- **Small**: 8dp - Used for buttons and small cards
- **Medium**: 16dp - Used for standard cards and dialogs
- **Large**: 24dp - Used for large cards and bottom sheets
- **ExtraLarge**: 32dp - Used for floating elements
- **Circular**: 50% - Used for circular elements like FABs and profile pictures

### Elevation
- **None**: 0dp - Flat elements
- **ExtraSmall**: 1dp - Subtle elevation for cards
- **Small**: 2dp - Standard elevation for cards
- **Medium**: 4dp - Elevated cards and dialogs
- **Large**: 8dp - Floating elements like FABs
- **ExtraLarge**: 16dp - Modal dialogs and sheets

### Component Shapes
- **CardShape**: Large rounded corners (16dp)
- **ButtonShape**: Medium rounded corners (8dp)
- **InputFieldShape**: Medium rounded corners (8dp)
- **BottomSheetShape**: Rounded top corners (24dp)
- **TopAppBarShape**: Rounded bottom corners (16dp)
- **FloatingActionButtonShape**: Circular
- **SOSButtonShape**: Circular

## 🧩 Component System

### Cards

#### Standard Card
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 2.dp
    )
) {
    // Content
}
```

#### Glassmorphic Card
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(
            if (isLightTheme) GlassLight else GlassDark,
            shape = RoundedCornerShape(16.dp)
        )
        .border(
            width = 1.dp,
            color = if (isLightTheme) 
                Color.White.copy(alpha = 0.5f) 
            else 
                Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp)
        )
) {
    // Content with blur effect
}
```

#### Gradient Card
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(
            brush = Brush.linearGradient(
                colors = listOf(GreenGradientStart, GreenGradientEnd),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
) {
    // Content
}
```

### Buttons

#### Primary Button
```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    shape = RoundedCornerShape(8.dp),
    elevation = ButtonDefaults.buttonElevation(
        defaultElevation = 2.dp,
        pressedElevation = 8.dp
    )
) {
    Text(
        text = "Button Text",
        style = MaterialTheme.typography.labelLarge
    )
}
```

#### Secondary Button
```kotlin
OutlinedButton(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    ),
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
) {
    Text(
        text = "Button Text",
        style = MaterialTheme.typography.labelLarge
    )
}
```

#### Animated Pulse Button (SOS)
```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "SOSPulse")
val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.1f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "SOSPulseScale"
)

Box(
    modifier = Modifier
        .size(150.dp)
        .scale(scale)
        .clip(CircleShape)
        .background(SOSRed)
        .clickable { /* action */ },
    contentAlignment = Alignment.Center
) {
    // Content
}
```

### Input Fields

#### Text Field
```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Label") },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
)
```

### Navigation

#### Bottom Navigation
```kotlin
NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    tonalElevation = 8.dp
) {
    // Navigation items
}
```

#### Top App Bar
```kotlin
TopAppBar(
    title = {
        Text(
            text = "Screen Title",
            style = MaterialTheme.typography.titleLarge
        )
    },
    navigationIcon = {
        IconButton(onClick = { /* action */ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
    )
)
```

## 📱 Screen-Specific Guidelines

### Splash Screen
- Use a full-screen animation with the app logo
- Apply a gradient background with primary brand colors
- Use Lottie animation for smooth transitions
- Display app name with Display Large typography
- Show a subtle loading indicator

### Login/Register
- Use a glassmorphic card for the login form
- Apply subtle animations for field focus and transitions
- Use large, elevated buttons for primary actions
- Include social login options with recognizable icons
- Implement password visibility toggle with animation

### Dashboard
- Use a grid layout with large, colorful cards
- Apply elevation and shadows for depth
- Use icons with labels for clear navigation
- Include a welcome message with the user's name
- Show health summary with visual indicators

### Track Health
- Use vertical scrolling with section headers
- Implement animated progress bars and charts
- Use sliders and pickers for input
- Apply color coding for different health metrics
- Include a floating save button with ripple effect

### AI Diagnosis
- Use a step-by-step interface with progress indicator
- Implement animated transitions between steps
- Use checkboxes and toggles for symptom selection
- Display results with confidence indicators
- Include recommendations in cards with icons

### SOS
- Center the large red SOS button with pulsing animation
- Use high contrast for emergency context
- Include a map view for location awareness
- List emergency contacts with quick-action buttons
- Implement haptic feedback for button presses

### Profile/Settings
- Use a card-based layout for different setting categories
- Implement toggle switches with animation
- Use dropdown menus for selection options
- Include user profile information with edit options
- Apply consistent spacing and alignment

## 🎬 Animation Guidelines

### Transitions
- Use fadeIn/fadeOut for page transitions
- Apply slide animations for hierarchical navigation
- Use expand/shrink for revealing/hiding content
- Keep animations under 300ms for responsiveness

### Feedback
- Use ripple effects for touch feedback
- Implement scale animations for button presses
- Apply color transitions for state changes
- Use progress indicators for loading states

### Micro-interactions
- Animate icons for state changes
- Use subtle animations for focus states
- Implement progress animations for tasks
- Apply pulsing animations for attention

## 📱 Responsive Design

### Screen Sizes
- Design for 5" to 7" screens as primary target
- Use ConstraintLayout or Columns/Rows for flexible layouts
- Apply different paddings based on screen size
- Adjust typography scale for different densities

### Orientation
- Support both portrait and landscape orientations
- Reorganize content for landscape mode
- Maintain consistent navigation in both orientations
- Adjust card layouts for different aspect ratios

## 🌓 Dark Mode

### Implementation
- Use Material Theme's dynamic dark/light theme switching
- Maintain sufficient contrast in both themes
- Use semantic colors that adapt to the theme
- Test all screens in both light and dark modes

### Considerations
- Avoid pure black (#000000) in dark mode
- Use softer colors for large surfaces
- Maintain readability with proper contrast
- Apply consistent elevation and shadows

## 🧪 Implementation Testing

### Visual Testing
- Check all screens in both light and dark modes
- Verify typography hierarchy and readability
- Ensure consistent spacing and alignment
- Test animations and transitions for smoothness

### Accessibility Testing
- Verify color contrast meets WCAG standards
- Test with different font sizes
- Ensure touch targets are at least 48x48dp
- Verify screen reader compatibility

## 🚀 Getting Started

To implement this design system:

1. Use the updated Color.kt, Type.kt, and Shape.kt files
2. Apply the component guidelines for new UI elements
3. Follow the screen-specific guidelines for each screen
4. Test the implementation against the design principles
5. Iterate based on user feedback and testing results

## 💻 Implementation Examples

### Complete SplashScreen Implementation

Here's a complete implementation of the enhanced SplashScreen that demonstrates many of the design principles:

```kotlin
package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.R
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GoldGradientEnd
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GoldGradientStart
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GreenGradientEnd
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GreenGradientStart
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SmartUgandanHealthCompanionAppSUHCTheme
import kotlinx.coroutines.delay

/**
 * Splash screen composable.
 * Displays an animated logo, app name, and subtitle before navigating to the login screen.
 */
@Composable
fun SplashScreen(navController: NavController) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Alpha Animation"
    )
    
    // Logo scale animation
    val scale = remember { Animatable(0.3f) }
    
    // Pulse animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse Animation"
    )
    
    // Gradient background
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            GreenGradientStart,
            GreenGradientEnd
        )
    )
    
    // Start animation after composition
    LaunchedEffect(key1 = true) {
        startAnimation = true
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = LinearEasing
            )
        )
        // Navigate to login screen after delay
        delay(3000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }
    
    // Splash screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .alpha(alphaAnim.value)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value * pulse)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Replace with your app logo
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name
            Text(
                text = "Smart Ugandan Health Companion",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle with accent color
            Text(
                text = "Track your health. Stay safe. Get answers.",
                style = MaterialTheme.typography.titleMedium,
                color = GoldGradientEnd,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = GoldGradientStart,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeWidth = 4.dp
            )
        }
    }
}

/**
 * Preview function for the splash screen.
 */
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SmartUgandanHealthCompanionAppSUHCTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SplashScreen(rememberNavController())
        }
    }
}
```

### Enhanced Login Screen Example

Here's a partial implementation of the enhanced Login Screen:

```kotlin
@Composable
fun LoginScreen(navController: NavController) {
    // Gradient background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
        )
    )
    
    // State for text fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Glassmorphic card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = if (isSystemInDarkTheme()) 
                        GlassDark 
                    else 
                        GlassLight,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isSystemInDarkTheme()) 
                        Color.White.copy(alpha = 0.1f) 
                    else 
                        Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Login form content
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                // Password field with visibility toggle
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) 
                                    Icons.Default.Visibility 
                                else 
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) 
                                    "Hide password" 
                                else 
                                    "Show password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Forgot password
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* Action */ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Login button with elevation and gradient
                Button(
                    onClick = { /* Login action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Or divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Google Sign-In button
                OutlinedButton(
                    onClick = { /* Google sign-in action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Google icon would go here
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Register prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { /* Navigate to register */ }
                    )
                }
            }
        }
    }
}
```

## 📚 Resources

- [Material Design 3 Guidelines](https://m3.material.io/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Google Fonts](https://fonts.google.com/)
- [Lottie Animations](https://airbnb.design/lottie/)

## 🛠️ Implementation Guide

This section provides a step-by-step guide for applying the design system to the existing files in the project.

### Step 1: Update Theme Files

1. **Color.kt**: Already updated with the new color scheme.
2. **Type.kt**: Already updated with Google Fonts typography.
3. **Shape.kt**: Already created with shape definitions.
4. **Theme.kt**: Already updated to use the new colors, typography, and shapes.

### Step 2: Update Existing Screens

#### SOSScreen.kt

1. Update the top app bar:
```kotlin
TopAppBar(
    title = {
        Text(
            text = "Emergency SOS",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    },
    navigationIcon = {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = SOSRed,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White
    )
)
```

2. Enhance the SOS button with improved animation:
```kotlin
// Pulse animation for the SOS button
val infiniteTransition = rememberInfiniteTransition(label = "SOSPulse")
val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.1f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "SOSPulseScale"
)

// SOS button
Box(
    modifier = Modifier
        .size(if (isSosActive) 180.dp else 150.dp)
        .scale(if (isSosActive) scale else 1f)
        .clip(CircleShape)
        .background(
            color = if (isSosActive) SOSRed else SOSRed.copy(alpha = 0.9f),
            shape = CircleShape
        )
        .border(
            width = if (isSosActive) 4.dp else 2.dp,
            color = Color.White,
            shape = CircleShape
        )
        .clickable {
            if (!isSosActive) {
                sosButtonPressed = true
                sosButtonPressStartTime = System.currentTimeMillis()
            }
        },
    contentAlignment = Alignment.Center
) {
    Text(
        text = if (isSosActive) "ACTIVE" else "SOS",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}
```

3. Enhance the emergency contacts section:
```kotlin
// Emergency contacts section
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp
    )
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Emergency Contacts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Emergency contacts list
        emergencyContacts.forEach { contact ->
            EmergencyContactItem(contact = contact)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
```

#### DashboardScreen.kt

1. Update the welcome section:
```kotlin
// Welcome section
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp
    )
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Welcome, ${userData.name}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

2. Enhance the quick actions grid:
```kotlin
// Quick actions grid
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp
    )
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Grid of quick actions
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(quickActions) { action ->
                QuickActionItem(
                    icon = action.icon,
                    title = action.title,
                    onClick = action.onClick
                )
            }
        }
    }
}

// Quick action item
@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
```

### Step 3: Create New Screens

For new screens like the SplashScreen and LoginScreen, use the complete implementations provided in the examples section.

### Step 4: Add Animations and Effects

1. Add Lottie animations for loading states:
```kotlin
// Add this dependency to build.gradle.kts
// implementation("com.airbnb.android:lottie-compose:6.0.0")

// Usage in a composable
LottieAnimation(
    composition = composition,
    progress = { progress },
    modifier = Modifier.size(200.dp)
)
```

2. Add glassmorphism effects to cards:
```kotlin
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(
            color = if (isSystemInDarkTheme()) 
                GlassDark 
            else 
                GlassLight,
            shape = RoundedCornerShape(16.dp)
        )
        .border(
            width = 1.dp,
            color = if (isSystemInDarkTheme()) 
                Color.White.copy(alpha = 0.1f) 
            else 
                Color.White.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        )
) {
    // Content
}
```

### Step 5: Test and Refine

1. Test all screens in both light and dark modes
2. Verify typography hierarchy and readability
3. Ensure consistent spacing and alignment
4. Test animations and transitions for smoothness
5. Verify color contrast meets accessibility standards

By following these steps, you can systematically apply the design system to the existing app, creating a beautiful, professional, and visually engaging UI that meets all the requirements.