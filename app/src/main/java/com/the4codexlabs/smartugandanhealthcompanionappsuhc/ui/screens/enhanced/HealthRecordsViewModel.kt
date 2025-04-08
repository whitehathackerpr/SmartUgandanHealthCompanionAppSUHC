package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.HealthRecordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class HealthRecordsUiState(
    val records: List<HealthRecord> = emptyList(),
    val selectedType: RecordType? = null,
    val showAddRecordDialog: Boolean = false,
    val recordToEdit: HealthRecord? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class HealthRecordsViewModel : ViewModel() {
    private val repository = HealthRecordsRepository()
    private val _uiState = MutableStateFlow(HealthRecordsUiState())
    val uiState: StateFlow<HealthRecordsUiState> = _uiState.asStateFlow()

    init {
        loadHealthRecords()
    }

    fun loadHealthRecords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val type = _uiState.value.selectedType
                
                if (type == null) {
                    repository.getHealthRecords().collect { records ->
                        _uiState.update { 
                            it.copy(
                                records = records,
                                isLoading = false,
                                error = null
                            ) 
                        }
                    }
                } else {
                    repository.getHealthRecordsByType(type).collect { records ->
                        _uiState.update { 
                            it.copy(
                                records = records,
                                isLoading = false,
                                error = null
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load health records"
                    ) 
                }
            }
        }
    }

    fun searchRecords(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadHealthRecords()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.searchHealthRecords(query).collect { records ->
                    _uiState.update { 
                        it.copy(
                            records = records,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to search records"
                    ) 
                }
            }
        }
    }

    fun selectRecordType(type: RecordType?) {
        _uiState.update { it.copy(selectedType = type) }
        loadHealthRecords()
    }

    fun addHealthRecord(
        title: String,
        type: RecordType,
        date: Date,
        description: String? = null,
        doctor: String? = null,
        location: String? = null,
        attachments: List<String> = emptyList()
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showAddRecordDialog = false) }
            try {
                val record = _uiState.value.recordToEdit?.copy(
                    title = title,
                    type = type,
                    date = date,
                    description = description,
                    doctor = doctor,
                    location = location,
                    attachments = attachments
                ) ?: HealthRecord(
                    title = title,
                    type = type,
                    date = date,
                    description = description,
                    doctor = doctor,
                    location = location,
                    attachments = attachments
                )
                
                if (record.id.isEmpty()) {
                    repository.addHealthRecord(record)
                } else {
                    repository.updateHealthRecord(record)
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        recordToEdit = null,
                        error = null
                    ) 
                }
                // Records will be automatically updated via Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save health record"
                    ) 
                }
            }
        }
    }

    fun deleteHealthRecord(recordId: String) {
        viewModelScope.launch {
            try {
                repository.deleteHealthRecord(recordId)
                // Records will be automatically updated via Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to delete health record") 
                }
            }
        }
    }

    fun showAddRecordDialog(show: Boolean, recordToEdit: HealthRecord? = null) {
        _uiState.update { 
            it.copy(
                showAddRecordDialog = show,
                recordToEdit = recordToEdit
            ) 
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 