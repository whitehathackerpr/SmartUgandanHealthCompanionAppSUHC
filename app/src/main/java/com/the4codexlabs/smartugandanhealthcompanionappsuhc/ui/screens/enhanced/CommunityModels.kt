package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import java.util.*

data class CommunityPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String? = null,
    val content: String = "",
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val tags: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val groupId: String? = null
)

data class CommunityGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val memberCount: Int = 0,
    val imageUrl: String? = null,
    val isJoined: Boolean = false,
    val createdBy: String = "",
    val createdAt: Date = Date()
)

data class CommunityEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: Date = Date(),
    val organizerId: String = "",
    val organizerName: String = "",
    val attendeeCount: Int = 0,
    val isAttending: Boolean = false,
    val imageUrl: String? = null
) 