package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.NotificationsRepository
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.StringResources
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.getTranslation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    MEDICATION, APPOINTMENT, SYSTEM, HEALTH_ALERT, GENERAL
}

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class NotificationsViewModel : ViewModel() {
    private val repository = NotificationsRepository()
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        viewModelScope.launch {
            // Create sample notifications for first-time users
            repository.createSampleNotifications()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            repository.getNotifications()
                .catch { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to load notifications: ${error.localizedMessage}"
                    ) }
                }
                .collect { notifications ->
                    _uiState.update { it.copy(
                        notifications = notifications,
                        isLoading = false
                    ) }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to mark notification as read"
                ) }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to mark all notifications as read"
                ) }
            }
        }
    }

    fun clearNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.clearNotification(notificationId)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    error = "Failed to clear notification"
                ) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackPress: () -> Unit = {},
    viewModel: NotificationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Error handling
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Handle error here - in real app you might show a snackbar
            // and clear the error after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = getTranslation(StringResources.notifications)) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.notifications.any { !it.isRead }) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Mark all as read")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.notifications.isEmpty()) {
                EmptyNotifications(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onMarkAsRead = { viewModel.markAsRead(notification.id) },
                            onClear = { viewModel.clearNotification(notification.id) }
                        )
                    }
                }
            }

            // Error message if any
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onClear: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon based on notification type
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = getNotificationColor(notification.type).copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationColor(notification.type),
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = dateFormatter.format(notification.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (!notification.isRead) {
                    IconButton(onClick = onMarkAsRead) {
                        Icon(
                            imageVector = Icons.Outlined.MarkEmailRead,
                            contentDescription = "Mark as read",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Clear notification",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Notifications",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You don't have any notifications at the moment.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.MEDICATION -> Icons.Outlined.Medication
        NotificationType.APPOINTMENT -> Icons.Outlined.Event
        NotificationType.SYSTEM -> Icons.Outlined.Info
        NotificationType.HEALTH_ALERT -> Icons.Outlined.HealthAndSafety
        NotificationType.GENERAL -> Icons.Outlined.Notifications
    }
}

@Composable
fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.MEDICATION -> Color(0xFF1CADE4)
        NotificationType.APPOINTMENT -> Color(0xFF019267)
        NotificationType.SYSTEM -> MaterialTheme.colorScheme.primary
        NotificationType.HEALTH_ALERT -> Color(0xFFBD4F6C)
        NotificationType.GENERAL -> Color(0xFF4A6572)
    }
} 