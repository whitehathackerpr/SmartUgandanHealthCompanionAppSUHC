package com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model

import java.util.UUID

data class Symptom(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val severity: Int = 1, // 1-5 scale
    val duration: String = "",
    val frequency: String = "",
    val associatedSymptoms: List<String> = emptyList(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 