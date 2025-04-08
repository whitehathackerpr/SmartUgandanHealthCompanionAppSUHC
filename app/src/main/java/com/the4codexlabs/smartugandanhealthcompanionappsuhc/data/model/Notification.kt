package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "", // "group_message", "event", "medication", etc.
    val data: Map<String, String> = emptyMap(), // Additional data specific to notification type
    val read: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) 