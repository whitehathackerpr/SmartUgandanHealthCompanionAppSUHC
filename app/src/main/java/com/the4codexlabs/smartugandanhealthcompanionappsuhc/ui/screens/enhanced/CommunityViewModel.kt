package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class CommunityUiState(
    val posts: List<CommunityPost> = emptyList(),
    val groups: List<CommunityGroup> = emptyList(),
    val selectedTab: Int = 0,
    val showCreatePostDialog: Boolean = false,
    val showCreateGroupDialog: Boolean = false,
    val groupSearchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedGroupId: String? = null,
    val groupPosts: List<CommunityPost> = emptyList()
)

class CommunityViewModel : ViewModel() {
    private val repository = CommunityRepository()
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
        loadGroups()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getPosts().collect { posts ->
                    _uiState.update { 
                        it.copy(
                            posts = posts,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load posts"
                    ) 
                }
            }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getGroups().collect { groups ->
                    _uiState.update { 
                        it.copy(
                            groups = groups,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load groups"
                    ) 
                }
            }
        }
    }

    fun searchGroups(query: String) {
        _uiState.update { it.copy(groupSearchQuery = query) }
        if (query.isBlank()) {
            loadGroups()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.searchGroups(query).collect { groups ->
                    _uiState.update { 
                        it.copy(
                            groups = groups,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to search groups"
                    ) 
                }
            }
        }
    }

    fun createPost(content: String, tags: List<String>, imageUrls: List<String> = emptyList()) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showCreatePostDialog = false) }
            try {
                val groupId = _uiState.value.selectedGroupId
                if (groupId != null) {
                    repository.createGroupPost(groupId, content, tags, imageUrls)
                } else {
                    repository.createPost(content, tags, imageUrls)
                }
                _uiState.update { it.copy(isLoading = false, error = null) }
                // Posts will be automatically updated via the Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create post"
                    ) 
                }
            }
        }
    }

    fun createGroup(name: String, description: String, imageUrl: String? = null) {
        if (name.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showCreateGroupDialog = false) }
            try {
                repository.createGroup(name, description, imageUrl)
                _uiState.update { it.copy(isLoading = false, error = null) }
                // Groups will be automatically updated via the Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create group"
                    ) 
                }
            }
        }
    }

    fun togglePostLike(post: CommunityPost) {
        viewModelScope.launch {
            try {
                repository.togglePostLike(post.id, !post.isLiked)
                // Posts will be automatically updated via the Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to update post like") 
                }
            }
        }
    }

    fun toggleGroupJoin(group: CommunityGroup) {
        viewModelScope.launch {
            try {
                repository.toggleGroupJoin(group.id, !group.isJoined)
                // Groups will be automatically updated via the Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to update group membership") 
                }
            }
        }
    }

    fun selectGroup(groupId: String?) {
        _uiState.update { it.copy(selectedGroupId = groupId) }
        
        if (groupId != null) {
            loadGroupPosts(groupId)
        } else {
            _uiState.update { it.copy(groupPosts = emptyList()) }
        }
    }

    fun loadGroupPosts(groupId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getGroupPosts(groupId).collect { posts ->
                    _uiState.update { 
                        it.copy(
                            groupPosts = posts,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load group posts"
                    ) 
                }
            }
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun showCreatePostDialog(show: Boolean) {
        _uiState.update { it.copy(showCreatePostDialog = show) }
    }

    fun showCreateGroupDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateGroupDialog = show) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 