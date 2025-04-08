package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import java.util.*

data class Symptom(
    val id: String = "",
    val name: String = "",
    val severity: Int = 1,  // 1-10 scale
    val date: Date = Date(),
    val notes: String? = null,
    val relatedSymptoms: List<String>? = null
)

data class SymptomSummary(
    val totalSymptoms: Int = 0,
    val averageSeverity: Double = 0.0,
    val maxSeverity: Int = 0,
    val mostCommonSymptoms: Map<String, Int> = emptyMap(),
    val mostCommonRelatedSymptoms: Map<String, Int> = emptyMap()
) 