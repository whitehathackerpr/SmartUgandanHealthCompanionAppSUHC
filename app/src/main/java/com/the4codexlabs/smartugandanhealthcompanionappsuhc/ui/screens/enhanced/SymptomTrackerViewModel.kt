package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model.Symptom
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.SymptomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class SymptomSummaryData(
    val totalSymptoms: Int = 0,
    val maxSeverity: Int = 0,
    val mostCommonSymptoms: List<String> = emptyList()
)

data class SymptomTrackerUiState(
    val symptoms: List<Symptom> = emptyList(),
    val selectedTimeRange: String = "week", // "week", "month", "year", "all"
    val showAddSymptomDialog: Boolean = false,
    val symptomToEdit: Symptom? = null,
    val summaryData: SymptomSummaryData = SymptomSummaryData(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SymptomTrackerViewModel(
    private val repository: SymptomRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SymptomTrackerUiState())
    val uiState: StateFlow<SymptomTrackerUiState> = _uiState.asStateFlow()

    init {
        loadSymptoms()
    }

    fun loadSymptoms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val symptoms = repository.getSymptoms()
                updateSymptomsList(symptoms)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load symptoms") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setTimeRange(range: String) {
        _uiState.update { it.copy(selectedTimeRange = range) }
        loadSymptoms()
    }

    fun showAddSymptomDialog(show: Boolean, symptom: Symptom? = null) {
        _uiState.update { it.copy(showAddSymptomDialog = show, symptomToEdit = symptom) }
    }

    fun addSymptom(
        name: String,
        severity: Int,
        date: Date,
        notes: String?,
        relatedSymptoms: List<String>?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val symptom = Symptom(
                    id = _uiState.value.symptomToEdit?.id ?: UUID.randomUUID().toString(),
                    name = name,
                    severity = severity,
                    date = date,
                    notes = notes,
                    relatedSymptoms = relatedSymptoms
                )
                
                repository.saveSymptom(symptom)
                loadSymptoms()
                showAddSymptomDialog(false)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to save symptom") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteSymptom(symptomId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteSymptom(symptomId)
                loadSymptoms()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to delete symptom") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun updateSymptomsList(symptoms: List<Symptom>) {
        val filteredSymptoms = when (_uiState.value.selectedTimeRange) {
            "week" -> symptoms.filter { 
                val diff = System.currentTimeMillis() - it.date.time
                diff <= 7 * 24 * 60 * 60 * 1000 
            }
            "month" -> symptoms.filter { 
                val diff = System.currentTimeMillis() - it.date.time
                diff <= 30 * 24 * 60 * 60 * 1000 
            }
            "year" -> symptoms.filter { 
                val diff = System.currentTimeMillis() - it.date.time
                diff <= 365 * 24 * 60 * 60 * 1000 
            }
            else -> symptoms
        }

        val summaryData = SymptomSummaryData(
            totalSymptoms = filteredSymptoms.size,
            maxSeverity = filteredSymptoms.maxOfOrNull { it.severity } ?: 0,
            mostCommonSymptoms = filteredSymptoms
                .groupBy { it.name }
                .mapValues { it.value.size }
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }
        )

        _uiState.update { 
            it.copy(
                symptoms = filteredSymptoms,
                summaryData = summaryData
            )
        }
    }
} 