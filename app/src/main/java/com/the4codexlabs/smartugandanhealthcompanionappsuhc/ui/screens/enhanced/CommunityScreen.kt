package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
// Import the data classes from CommunityModels.kt instead of redefining them
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.CommunityPost
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.CommunityGroup
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced.CommunityEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showErrorSnackbar = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community") },
                actions = {
                    IconButton(onClick = { /* TODO: Implement settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = uiState.selectedTab
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = { Text("Feed") },
                    icon = { Icon(Icons.Default.DynamicFeed, contentDescription = null) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = { Text("Groups") },
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    text = { Text("Events") },
                    icon = { Icon(Icons.Default.Event, contentDescription = null) }
                )
            }

            // Tab Content
            when (uiState.selectedTab) {
                0 -> FeedTab(
                    posts = uiState.posts,
                    isLoading = uiState.isLoading,
                    onCreatePost = { viewModel.showCreatePostDialog(true) },
                    onLikePost = { viewModel.togglePostLike(it) }
                )
                1 -> GroupsTab(
                    groups = uiState.groups,
                    isLoading = uiState.isLoading,
                    searchQuery = uiState.groupSearchQuery,
                    onSearchChange = { viewModel.searchGroups(it) },
                    onJoinGroup = { viewModel.toggleGroupJoin(it) }
                )
                2 -> EventsTab()
            }
        }
    }

    // Create Post Dialog
    if (uiState.showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { viewModel.showCreatePostDialog(false) },
            onCreatePost = { content, tags -> 
                viewModel.createPost(content, tags)
            }
        )
    }

    // Error Snackbar
    if (showErrorSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { 
                    showErrorSnackbar = false
                    viewModel.clearError()
                }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(uiState.error ?: "An error occurred")
        }
    }
}

@Composable
fun FeedTab(
    posts: List<CommunityPost>,
    isLoading: Boolean,
    onCreatePost: () -> Unit,
    onLikePost: (CommunityPost) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (posts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Forum,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No posts yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Be the first to share with the community!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onCreatePost
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Post")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Button(
                        onClick = onCreatePost,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Post")
                    }
                }

                items(posts) { post ->
                    PostCard(
                        post = post,
                        onLike = { onLikePost(post) }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupsTab(
    groups: List<CommunityGroup>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onJoinGroup: (CommunityGroup) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search groups...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (groups.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            "No groups found for '$searchQuery'",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        Text(
                            "No groups available",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(groups) { group ->
                    GroupCard(
                        group = group,
                        onJoin = { onJoinGroup(group) }
                    )
                }
            }
        }
    }
}

@Composable
fun EventsTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No upcoming events",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Check back soon for community events!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PostCard(
    post: CommunityPost,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Author info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        post.authorName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        formatDate(post.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                post.content,
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text("#$tag") }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onLike) {
                        Icon(
                            if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (post.isLiked) "Unlike" else "Like",
                            tint = if (post.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        "${post.likes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    IconButton(onClick = { /* TODO: Show comments */ }) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = "Comments"
                        )
                    }
                    Text(
                        "${post.comments}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                IconButton(onClick = { /* TODO: Share post */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share"
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    group: CommunityGroup,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Group icon placeholder
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            group.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${group.memberCount} members",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isJoined) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (group.isJoined) "Leave" else "Join")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                group.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreatePost: (content: String, tags: List<String>) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Post") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("What's on your mind?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 7
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("health, tips, question, etc") },
                    singleLine = true
                )
                
                /* TODO: Add image upload functionality
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilledTonalIconButton(onClick = { /* TODO: Add image */ }) {
                        Icon(Icons.Default.Image, contentDescription = "Add Image")
                    }
                }
                */
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tags = tagsInput.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    onCreatePost(content, tags)
                },
                enabled = content.isNotEmpty()
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time
    
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
    }
} 