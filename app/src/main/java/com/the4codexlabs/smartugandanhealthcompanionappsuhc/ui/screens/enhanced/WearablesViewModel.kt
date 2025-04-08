package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.the4codexlabs.smartugandanhealthcompanionappsuhc.data.repository.WearablesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class WearablesUiState(
    val devices: List<WearableDevice> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddingDevice: Boolean = false,
    val isSyncingDevice: String? = null
)

class WearablesViewModel : ViewModel() {
    private val repository = WearablesRepository()
    
    private val _uiState = MutableStateFlow(WearablesUiState())
    val uiState: StateFlow<WearablesUiState> = _uiState.asStateFlow()
    
    init {
        loadDevices()
    }
    
    private fun loadDevices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                repository.getDevices().collect { devices ->
                    _uiState.update { it.copy(devices = devices, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to load devices: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun addDevice(name: String, type: DeviceType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingDevice = true, error = null) }
            
            try {
                val newDevice = WearableDevice(
                    id = "",
                    name = name,
                    type = type,
                    isConnected = false,
                    batteryLevel = 0
                )
                
                repository.addDevice(newDevice)
                _uiState.update { it.copy(isAddingDevice = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isAddingDevice = false, 
                        error = "Failed to add device: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun syncDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingDevice = deviceId, error = null) }
            
            try {
                repository.syncDevice(deviceId)
                _uiState.update { it.copy(isSyncingDevice = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSyncingDevice = null, 
                        error = "Failed to sync device: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun toggleDeviceConnection(deviceId: String, isConnected: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateDeviceConnectionStatus(deviceId, isConnected)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to update device connection: ${e.message}") 
                }
            }
        }
    }
    
    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                repository.deleteDevice(deviceId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to delete device: ${e.message}") 
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 