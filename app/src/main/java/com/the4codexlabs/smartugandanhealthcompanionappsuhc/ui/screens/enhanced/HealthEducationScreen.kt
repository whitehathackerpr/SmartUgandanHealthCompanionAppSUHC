package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

data class HealthArticle(
    val id: String,
    val title: String,
    val category: ArticleCategory,
    val summary: String,
    val content: String,
    val imageUrl: String? = null,
    val author: String,
    val publishDate: Date,
    val readTime: Int, // in minutes
    val isBookmarked: Boolean = false
)

enum class ArticleCategory {
    GENERAL_HEALTH, NUTRITION, MENTAL_HEALTH, WOMENS_HEALTH, CHILDREN_HEALTH, 
    CHRONIC_DISEASES, FIRST_AID, PREVENTIVE_CARE, LOCAL_HEALTH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthEducationScreen(
    viewModel: HealthEducationViewModel = viewModel()
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
                title = { Text("Health Education") },
                actions = {
                    IconButton(onClick = { /* TODO: Implement settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search bar
                item {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::searchArticles,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                // Featured articles
                if (uiState.featuredArticles.isNotEmpty()) {
                    item {
                        Text(
                            "Featured Articles",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.featuredArticles) { article ->
                                FeaturedArticleCard(
                                    article = article,
                                    onBookmarkClick = { viewModel.toggleBookmark(article) }
                                )
                            }
                        }
                    }
                }

                // Category filters
                item {
                    CategoryFilters(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = viewModel::selectCategory,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Articles list
                if (uiState.articles.isEmpty()) {
                    item {
                        EmptyState(
                            message = if (uiState.searchQuery.isNotBlank()) {
                                "No articles found for '${uiState.searchQuery}'"
                            } else {
                                "No articles available"
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(uiState.articles) { article ->
                        ArticleCard(
                            article = article,
                            onBookmarkClick = { viewModel.toggleBookmark(article) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showErrorSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showErrorSnackbar = false }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(uiState.error ?: "An error occurred")
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search articles...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
private fun CategoryFilters(
    selectedCategory: ArticleCategory?,
    onCategorySelected: (ArticleCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Use a scrollable row instead of FlowRow
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // "All" filter
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") }
                )
            }
            
            // Category filters
            items(ArticleCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.name) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedArticleCard(
    article: HealthArticle,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    article.category.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (article.isBookmarked) "Remove bookmark" else "Add bookmark"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                article.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                article.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    article.author,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    formatDate(article.publishDate),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ArticleCard(
    article: HealthArticle,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    article.category.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (article.isBookmarked) "Remove bookmark" else "Add bookmark"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                article.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                article.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    article.author,
                    style = MaterialTheme.typography.labelSmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "${article.readTime} min read",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Article,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
} 