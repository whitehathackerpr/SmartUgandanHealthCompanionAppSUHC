package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.HealthEducationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HealthEducationUiState(
    val articles: List<HealthArticle> = emptyList(),
    val featuredArticles: List<HealthArticle> = emptyList(),
    val bookmarkedArticles: List<HealthArticle> = emptyList(),
    val selectedCategory: ArticleCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class HealthEducationViewModel : ViewModel() {
    private val repository = HealthEducationRepository()
    private val _uiState = MutableStateFlow(HealthEducationUiState())
    val uiState: StateFlow<HealthEducationUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
        loadFeaturedArticles()
        loadBookmarkedArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getArticles(_uiState.value.selectedCategory)
                    .collect { articles ->
                        _uiState.update { 
                            it.copy(
                                articles = articles,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load articles"
                    )
                }
            }
        }
    }

    fun loadFeaturedArticles() {
        viewModelScope.launch {
            try {
                repository.getFeaturedArticles()
                    .collect { articles ->
                        _uiState.update { 
                            it.copy(
                                featuredArticles = articles,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to load featured articles"
                    )
                }
            }
        }
    }

    fun loadBookmarkedArticles() {
        viewModelScope.launch {
            try {
                repository.getBookmarkedArticles()
                    .collect { articles ->
                        _uiState.update { 
                            it.copy(
                                bookmarkedArticles = articles,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to load bookmarked articles"
                    )
                }
            }
        }
    }

    fun searchArticles(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadArticles()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.searchArticles(query)
                    .collect { articles ->
                        _uiState.update { 
                            it.copy(
                                articles = articles,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to search articles"
                    )
                }
            }
        }
    }

    fun selectCategory(category: ArticleCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadArticles()
    }

    fun toggleBookmark(article: HealthArticle) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(article.id, !article.isBookmarked)
                // Refresh articles to update bookmark status
                loadArticles()
                loadBookmarkedArticles()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to update bookmark"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 