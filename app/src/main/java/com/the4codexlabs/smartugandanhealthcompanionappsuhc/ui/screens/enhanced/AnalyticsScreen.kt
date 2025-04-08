package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.random.Random

data class HealthMetric(
    val name: String,
    val value: String,
    val trend: String,
    val color: Color
)

@Composable
fun AnalyticsScreen(navController: NavController) {
    // Capture current theme colors - this happens in composable context
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    
    // Use mutable state for metrics list
    val metrics = remember { mutableStateListOf<HealthMetric>() }
    
    // Update metrics when theme colors change
    LaunchedEffect(primaryColor, secondaryColor, tertiaryColor) {
        metrics.clear()
        metrics.addAll(
            listOf(
                HealthMetric(
                    "Blood Pressure",
                    "120/80",
                    "Stable",
                    primaryColor
                ),
                HealthMetric(
                    "Blood Sugar",
                    "95 mg/dL",
                    "Improving",
                    tertiaryColor
                ),
                HealthMetric(
                    "Weight",
                    "68 kg",
                    "Decreasing",
                    secondaryColor
                ),
                HealthMetric(
                    "Sleep",
                    "7.5 hrs",
                    "Stable",
                    primaryColor
                )
            )
        )
    }

    BaseEnhancedScreen(
        navController = navController,
        title = "Health Analytics"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Health Summary",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your health metrics are looking good!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Metrics Grid
            Text(
                text = "Key Metrics",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                metrics.take(2).forEach { metric ->
                    MetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                metrics.drop(2).forEach { metric ->
                    MetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Blood Pressure Chart
            Text(
                text = "Blood Pressure Trend",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LineChartPlaceholder(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            // Blood Sugar Chart
            Text(
                text = "Blood Sugar Trend",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LineChartPlaceholder(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            // Export Button
            Button(
                onClick = { /* Handle export */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Health Report")
            }
        }
    }
}

@Composable
fun MetricCard(
    metric: HealthMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = metric.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleLarge,
                color = metric.color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when (metric.trend) {
                        "Improving" -> Icons.Default.TrendingUp
                        "Decreasing" -> Icons.Default.TrendingDown
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = when (metric.trend) {
                        "Improving" -> Color.Green
                        "Decreasing" -> Color.Red
                        else -> Color.Gray
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = metric.trend,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LineChartPlaceholder(
    color: Color,
    modifier: Modifier = Modifier
) {
    val random = Random(0)  // Fixed seed for consistent preview
    val points = remember {
        List(7) { random.nextFloat() * 0.5f + 0.25f }
    }
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val pointWidth = width / (points.size - 1)
        
        // Draw line chart
        for (i in 0 until points.size - 1) {
            val startX = i * pointWidth
            val startY = height * (1 - points[i])
            val endX = (i + 1) * pointWidth
            val endY = height * (1 - points[i + 1])
            
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        
        // Draw points
        points.forEachIndexed { index, point ->
            val x = index * pointWidth
            val y = height * (1 - point)
            
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}