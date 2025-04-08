package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class MedicationReminderUiState(
    val medications: List<Medication> = emptyList(),
    val todayMedications: List<Medication> = emptyList(),
    val selectedStatusFilter: String = "all", // "all", "taken", "not_taken"
    val showAddMedicationDialog: Boolean = false,
    val medicationToEdit: Medication? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MedicationReminderViewModel : ViewModel() {
    private val repository = MedicationRepository()
    private val _uiState = MutableStateFlow(MedicationReminderUiState())
    val uiState: StateFlow<MedicationReminderUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
        loadTodayMedications()
    }

    fun loadMedications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getMedications().collect { medications ->
                    _uiState.update { 
                        it.copy(
                            medications = medications,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load medications"
                    ) 
                }
            }
        }
    }

    fun loadTodayMedications() {
        viewModelScope.launch {
            try {
                repository.getTodayMedications().collect { medications ->
                    _uiState.update { 
                        it.copy(
                            todayMedications = medications,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to load today's medications"
                    ) 
                }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { it.copy(selectedStatusFilter = status) }
        
        viewModelScope.launch {
            try {
                repository.getMedicationsByStatus(status).collect { medications ->
                    _uiState.update { 
                        it.copy(
                            medications = medications,
                            error = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to filter medications"
                    ) 
                }
            }
        }
    }

    fun saveMedication(
        name: String,
        dosage: String,
        frequency: Frequency,
        times: List<TimeOfDay>,
        startDate: Date? = null,
        endDate: Date? = null,
        notes: String? = null,
        daysOfWeek: List<Int>? = null,
        daysOfMonth: List<Int>? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showAddMedicationDialog = false) }
            try {
                val medication = _uiState.value.medicationToEdit?.copy(
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    times = times,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    daysOfWeek = daysOfWeek,
                    daysOfMonth = daysOfMonth
                ) ?: Medication(
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    times = times,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    daysOfWeek = daysOfWeek,
                    daysOfMonth = daysOfMonth
                )
                
                repository.saveMedication(medication)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        medicationToEdit = null,
                        error = null
                    ) 
                }
                // Medications will be updated automatically via Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save medication"
                    ) 
                }
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            try {
                repository.deleteMedication(medicationId)
                // Medications will be updated automatically via Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to delete medication") 
                }
            }
        }
    }

    fun toggleMedicationStatus(medication: Medication, timeOfDay: TimeOfDay) {
        viewModelScope.launch {
            try {
                val today = Calendar.getInstance().apply { 
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val todayKey = today.time.toString()
                val todayStatus = medication.takenStatus[todayKey]
                val isTaken = todayStatus?.get(timeOfDay.name) ?: false
                
                repository.toggleMedicationStatus(medication.id, timeOfDay, !isTaken)
                // Medications will be updated automatically via Flow
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to update medication status") 
                }
            }
        }
    }

    fun showAddMedicationDialog(show: Boolean, medicationToEdit: Medication? = null) {
        _uiState.update { 
            it.copy(
                showAddMedicationDialog = show,
                medicationToEdit = medicationToEdit
            ) 
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun getNextDoseTime(): TimeOfDay? {
        val medications = _uiState.value.todayMedications
        if (medications.isEmpty()) return null
        
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        
        // Get all times from all medications due today
        val allTimes = medications.flatMap { it.times }
        
        // Find the next time after current time
        return allTimes.filter { timeOfDay ->
            timeOfDay.hour > currentHour || (timeOfDay.hour == currentHour && timeOfDay.minute > currentMinute)
        }.minByOrNull { timeOfDay ->
            timeOfDay.hour * 60 + timeOfDay.minute
        }
    }
} 