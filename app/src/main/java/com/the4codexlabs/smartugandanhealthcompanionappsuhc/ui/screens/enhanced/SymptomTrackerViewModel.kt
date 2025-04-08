package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.SymptomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class SymptomTrackerUiState(
    val symptoms: List<Symptom> = emptyList(),
    val selectedTimeRange: String = "week", // "week", "month", "year", "all"
    val showAddSymptomDialog: Boolean = false,
    val symptomToEdit: Symptom? = null,
    val summaryData: SymptomSummary = SymptomSummary(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SymptomTrackerViewModel : ViewModel() {
    private val repository = SymptomRepository()
    private val _uiState = MutableStateFlow(SymptomTrackerUiState())
    val uiState: StateFlow<SymptomTrackerUiState> = _uiState.asStateFlow()

    init {
        loadSymptoms()
        loadSummaryData()
    }

    fun loadSymptoms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getSymptomsByTimeRange(_uiState.value.selectedTimeRange).collect { symptoms ->
                    _uiState.update { 
                        it.copy(
                            symptoms = symptoms,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load symptoms"
                    ) 
                }
            }
        }
    }

    fun loadSummaryData() {
        viewModelScope.launch {
            try {
                val summaryMap = repository.getSymptomSummary(_uiState.value.selectedTimeRange)
                
                val summary = SymptomSummary(
                    totalSymptoms = summaryMap["totalSymptoms"] as? Int ?: 0,
                    averageSeverity = summaryMap["averageSeverity"] as? Double ?: 0.0,
                    maxSeverity = summaryMap["maxSeverity"] as? Int ?: 0,
                    mostCommonSymptoms = summaryMap["symptomCounts"] as? Map<String, Int> ?: emptyMap(),
                    mostCommonRelatedSymptoms = summaryMap["relatedSymptomCounts"] as? Map<String, Int> ?: emptyMap()
                )
                
                _uiState.update { 
                    it.copy(
                        summaryData = summary,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to load summary data"
                    ) 
                }
            }
        }
    }

    fun setTimeRange(range: String) {
        _uiState.update { it.copy(selectedTimeRange = range) }
        loadSymptoms()
        loadSummaryData()
    }

    fun addSymptom(
        name: String,
        severity: Int,
        date: Date = Date(),
        notes: String? = null,
        relatedSymptoms: List<String>? = null
    ) {
        if (name.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showAddSymptomDialog = false) }
            try {
                val symptom = _uiState.value.symptomToEdit?.copy(
                    name = name,
                    severity = severity,
                    date = date,
                    notes = notes,
                    relatedSymptoms = relatedSymptoms
                ) ?: Symptom(
                    name = name,
                    severity = severity,
                    date = date,
                    notes = notes,
                    relatedSymptoms = relatedSymptoms
                )
                
                repository.saveSymptom(symptom)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        symptomToEdit = null,
                        error = null
                    ) 
                }
                // Symptoms will be automatically updated via Flow
                loadSummaryData() // Refresh summary data
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save symptom"
                    ) 
                }
            }
        }
    }

    fun deleteSymptom(symptomId: String) {
        viewModelScope.launch {
            try {
                repository.deleteSymptom(symptomId)
                // Symptoms will be automatically updated via Flow
                loadSummaryData() // Refresh summary data
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to delete symptom") 
                }
            }
        }
    }

    fun showAddSymptomDialog(show: Boolean, symptomToEdit: Symptom? = null) {
        _uiState.update { 
            it.copy(
                showAddSymptomDialog = show,
                symptomToEdit = symptomToEdit
            ) 
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 