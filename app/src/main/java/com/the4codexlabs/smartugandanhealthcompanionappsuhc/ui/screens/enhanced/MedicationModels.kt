package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import java.util.*

data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val frequency: Frequency? = null,
    val times: List<TimeOfDay> = emptyList(),
    val startDate: Date? = null,
    val endDate: Date? = null,
    val notes: String? = null,
    val daysOfWeek: List<Int>? = null,  // Calendar.MONDAY, Calendar.TUESDAY, etc. for weekly frequency
    val daysOfMonth: List<Int>? = null, // 1-31 for monthly frequency
    val takenStatus: Map<String, Map<String, Boolean>> = emptyMap(), // Map of date string to map of time of day to boolean
    val isActive: Boolean = true // Whether the medication is currently active
)

enum class Frequency {
    DAILY, WEEKLY, MONTHLY, AS_NEEDED
}

data class TimeOfDay(
    val hour: Int = 0,
    val minute: Int = 0,
    val name: String = "MORNING"
) {
    companion object {
        val MORNING = TimeOfDay(8, 0, "MORNING")
        val AFTERNOON = TimeOfDay(13, 0, "AFTERNOON")
        val EVENING = TimeOfDay(18, 0, "EVENING")
        val NIGHT = TimeOfDay(22, 0, "NIGHT")
        
        fun fromString(name: String): TimeOfDay {
            return when (name.uppercase()) {
                "MORNING" -> MORNING
                "AFTERNOON" -> AFTERNOON
                "EVENING" -> EVENING
                "NIGHT" -> NIGHT
                else -> {
                    val parts = name.split(":")
                    if (parts.size == 2) {
                        try {
                            val hour = parts[0].toInt()
                            val minute = parts[1].toInt()
                            TimeOfDay(hour, minute, name)
                        } catch (e: NumberFormatException) {
                            MORNING
                        }
                    } else {
                        MORNING
                    }
                }
            }
        }
    }
    
    fun formatTime(): String {
        val isPM = hour >= 12
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val amPm = if (isPM) "PM" else "AM"
        return String.format("%d:%02d %s", displayHour, minute, amPm)
    }
    
    fun getDescription(): String {
        return when (name.uppercase()) {
            "MORNING" -> "Morning"
            "AFTERNOON" -> "Afternoon"
            "EVENING" -> "Evening"
            "NIGHT" -> "Night"
            else -> formatTime()
        }
    }
} 