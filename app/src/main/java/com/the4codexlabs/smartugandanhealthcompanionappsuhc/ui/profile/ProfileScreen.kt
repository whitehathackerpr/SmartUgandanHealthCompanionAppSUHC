package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.ThemeMode
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.ThemeProvider
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.theme.isAppInDarkTheme
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.AppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.LanguageProvider
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.StringResources
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.currentAppLanguage
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.util.getTranslation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (ThemeMode) -> Unit
) {
    val currentLanguage = currentAppLanguage()
    val currentTheme = if (isAppInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Animation for background color changes
    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.background,
        animationSpec = tween(durationMillis = 500),
        label = "backgroundColor"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getTranslation(StringResources.profile)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile image placeholder
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // User name
                        Text(
                            text = "John Doe", // Replace with actual user name
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Email
                        Text(
                            text = "johndoe@example.com", // Replace with actual email
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Edit profile button
                        Button(
                            onClick = { /* TODO: Add edit profile functionality */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Edit Profile")
                        }
                    }
                }
            }
            
            // Settings section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Language setting
                        SettingsItem(
                            icon = Icons.Outlined.Language,
                            title = "Language",
                            subtitle = when (currentLanguage) {
                                AppLanguage.ENGLISH -> "English"
                                AppLanguage.LUGANDA -> "Luganda"
                                AppLanguage.SWAHILI -> "Swahili"
                            },
                            onClick = { showLanguageDialog = true }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Theme setting
                        SettingsItem(
                            icon = if (currentTheme == ThemeMode.DARK) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            title = "Theme",
                            subtitle = when (currentTheme) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.SYSTEM -> "System Default"
                            },
                            onClick = { showThemeDialog = true }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Notifications setting
                        SettingsItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Notifications",
                            subtitle = "Manage notification settings",
                            onClick = { /* TODO: Add notification settings */ }
                        )
                    }
                }
            }
            
            // Account section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Privacy
                        SettingsItem(
                            icon = Icons.Outlined.Lock,
                            title = "Privacy",
                            subtitle = "Manage your data and privacy",
                            onClick = { /* TODO: Add privacy settings */ }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Help & Support
                        SettingsItem(
                            icon = Icons.Outlined.Help,
                            title = "Help & Support",
                            subtitle = "Get help or contact support",
                            onClick = { /* TODO: Add help and support */ }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        // About
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            title = "About",
                            subtitle = "App version and information",
                            onClick = { /* TODO: Add about section */ }
                        )
                    }
                }
            }
            
            // Logout button
            item {
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Space at the bottom for better UX
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Language selection dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = currentLanguage,
                onLanguageSelected = { language ->
                    onLanguageChange(language)
                    showLanguageDialog = false
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
        
        // Theme selection dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    onThemeChange(theme)
                    showThemeDialog = false
                },
                onDismiss = { showThemeDialog = false }
            )
        }
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // TODO: Implement logout functionality
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // English option
                LanguageOption(
                    language = "English",
                    isSelected = currentLanguage == AppLanguage.ENGLISH,
                    onClick = { onLanguageSelected(AppLanguage.ENGLISH) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Luganda option
                LanguageOption(
                    language = "Luganda",
                    isSelected = currentLanguage == AppLanguage.LUGANDA,
                    onClick = { onLanguageSelected(AppLanguage.LUGANDA) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Swahili option
                LanguageOption(
                    language = "Swahili",
                    isSelected = currentLanguage == AppLanguage.SWAHILI,
                    onClick = { onLanguageSelected(AppLanguage.SWAHILI) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                  else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Theme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Light theme option
                ThemeOption(
                    theme = "Light",
                    icon = Icons.Outlined.LightMode,
                    isSelected = currentTheme == ThemeMode.LIGHT,
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Dark theme option
                ThemeOption(
                    theme = "Dark",
                    icon = Icons.Outlined.DarkMode,
                    isSelected = currentTheme == ThemeMode.DARK,
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // System default option
                ThemeOption(
                    theme = "System Default",
                    icon = Icons.Outlined.SettingsSuggest,
                    isSelected = currentTheme == ThemeMode.SYSTEM,
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    theme: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                 else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = theme,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                  else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 