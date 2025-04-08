package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.model.Symptom
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.SymptomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SymptomUiState(
    val symptoms: List<Symptom> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddSymptomDialog: Boolean = false,
    val selectedSymptom: Symptom? = null
)

@HiltViewModel
class SymptomViewModel @Inject constructor(
    private val symptomRepository: SymptomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomUiState())
    val uiState: StateFlow<SymptomUiState> = _uiState.asStateFlow()

    init {
        loadSymptoms()
    }

    private fun loadSymptoms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            symptomRepository.getSymptoms()
                .onSuccess { symptoms ->
                    _uiState.update { it.copy(
                        symptoms = symptoms,
                        isLoading = false
                    ) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        error = error.message,
                        isLoading = false
                    ) }
                }
        }
    }

    fun showAddSymptomDialog() {
        _uiState.update { it.copy(showAddSymptomDialog = true) }
    }

    fun hideAddSymptomDialog() {
        _uiState.update { it.copy(showAddSymptomDialog = false) }
    }

    fun saveSymptom(symptom: Symptom) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            symptomRepository.saveSymptom(symptom)
                .onSuccess {
                    _uiState.update { it.copy(
                        isLoading = false,
                        showAddSymptomDialog = false
                    ) }
                    loadSymptoms()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        error = error.message,
                        isLoading = false
                    ) }
                }
        }
    }

    fun deleteSymptom(symptomId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            symptomRepository.deleteSymptom(symptomId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadSymptoms()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        error = error.message,
                        isLoading = false
                    ) }
                }
        }
    }

    fun selectSymptom(symptom: Symptom) {
        _uiState.update { it.copy(selectedSymptom = symptom) }
    }

    fun clearSelectedSymptom() {
        _uiState.update { it.copy(selectedSymptom = null) }
    }
} 