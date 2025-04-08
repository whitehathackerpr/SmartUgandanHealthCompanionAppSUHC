package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import java.util.*

data class HealthRecord(
    val id: String = "",
    val title: String = "",
    val type: RecordType = RecordType.GENERAL,
    val date: Date = Date(),
    val description: String? = null,
    val doctor: String? = null,
    val location: String? = null,
    val attachments: List<String> = emptyList()  // URLs of attachments
)

enum class RecordType {
    GENERAL, DOCTOR_VISIT, HOSPITALIZATION, LAB_TEST, IMMUNIZATION, 
    MEDICATION, SURGERY, ALLERGY, CHRONIC_CONDITION,
    // Additional types used in HealthRecordsScreen
    VACCINATION, LAB_RESULT, PRESCRIPTION, APPOINTMENT
}