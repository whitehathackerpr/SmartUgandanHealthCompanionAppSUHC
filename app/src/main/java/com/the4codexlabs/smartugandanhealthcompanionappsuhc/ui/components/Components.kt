package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.ButtonShape
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.CardShape
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.CircularCornerRadius
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.ExtraLargeElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GlassDark
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GlassLight
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GoldGradientEnd
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GoldGradientStart
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GreenGradientEnd
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.GreenGradientStart
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.LargeElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.MediumElevation
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.SOSRed

/**
 * A card with a glassmorphism effect (semi-transparent background with blur)
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    elevation: Dp = MediumElevation,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val glassColor = if (isDarkTheme) GlassDark else GlassLight
    
    Card(
        modifier = modifier
            .shadow(elevation, shape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = glassColor
        )
    ) {
        content()
    }
}

/**
 * A card with a gradient background and elevation (drop shadow)
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    elevation: Dp = MediumElevation,
    gradientColors: List<Color> = listOf(GreenGradientStart, GreenGradientEnd),
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
        ) {
            content()
        }
    }
}

/**
 * A card with an accent color border and subtle elevation
 */
@Composable
fun AccentBorderCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    elevation: Dp = MediumElevation,
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .border(borderWidth, borderColor, shape)
            .shadow(elevation, shape, spotColor = borderColor.copy(alpha = 0.5f)),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        content()
    }
}

/**
 * A button with a pulsing animation effect (for the SOS button)
 */
@Composable
fun AnimatedPulseButton(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    color: Color = SOSRed,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var buttonPressStartTime by remember { mutableStateOf(0L) }
    var buttonPressed by remember { mutableStateOf(false) }
    
    // Animation for pulsing effect when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Animation for press effect
    val pressScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "pressScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale * pressScale)
            .shadow(
                elevation = if (isActive) ExtraLargeElevation else LargeElevation,
                shape = CircleShape,
                spotColor = color
            )
            .clip(CircleShape)
            .background(color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                buttonPressed = true
                buttonPressStartTime = System.currentTimeMillis()
                onClick()
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * A rounded button with an icon and text
 */
@Composable
fun RoundedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    colors: Pair<Color, Color> = Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.first,
            contentColor = colors.second
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * A section header with a title and optional subtitle
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * A feature card for the dashboard with an icon, title, and description
 */
@Composable
fun FeatureCard(
    onClick: () -> Unit,
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colors: List<Color> = listOf(GreenGradientStart, GreenGradientEnd)
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(
                elevation = MediumElevation,
                shape = CardShape,
                spotColor = colors.first().copy(alpha = 0.5f)
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = colors.first()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(0.8f)
            )
        }
    }
}