package com.the4codexlabs.smartugandanhealthcompanionappsuhc.ui.screens.enhanced

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class HealthFacility(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val address: String = "",
    val services: List<String> = emptyList(),
    val location: GeoPoint? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val operatingHours: Map<String, String> = emptyMap(),
    val rating: Double = 0.0,
    val distance: Double = 0.0
)

data class HealthcareMapUiState(
    val facilities: List<HealthFacility> = emptyList(),
    val userLocation: Location? = null,
    val searchQuery: String = "",
    val selectedFacility: HealthFacility? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HealthcareMapViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(HealthcareMapUiState())
    val uiState: StateFlow<HealthcareMapUiState> = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        loadFacilities()
    }

    fun setLocationClient(client: FusedLocationProviderClient) {
        fusedLocationClient = client
        getUserLocation()
    }

    private fun getUserLocation() {
        viewModelScope.launch {
            try {
                val location = withContext(Dispatchers.IO) {
                    fusedLocationClient.lastLocation.await()
                }
                _uiState.update { it.copy(userLocation = location) }
                if (location != null) {
                    updateFacilityDistances(location)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to get location: ${e.message}") }
            }
        }
    }

    private fun loadFacilities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val facilitiesSnapshot = firestore.collection("health_facilities")
                    .get()
                    .await()

                val facilities = facilitiesSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthFacility::class.java)?.copy(id = doc.id)
                }

                _uiState.update { 
                    it.copy(
                        facilities = facilities,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load facilities: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchFacilities(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadFacilities()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val facilitiesSnapshot = firestore.collection("health_facilities")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + '\uf8ff')
                    .get()
                    .await()

                val facilities = facilitiesSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthFacility::class.java)?.copy(id = doc.id)
                }

                _uiState.update { 
                    it.copy(
                        facilities = facilities,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to search facilities: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectFacility(facility: HealthFacility?) {
        _uiState.update { it.copy(selectedFacility = facility) }
    }

    private fun updateFacilityDistances(userLocation: Location) {
        val facilities = _uiState.value.facilities.map { facility ->
            facility.location?.let { geoPoint ->
                val facilityLocation = Location("").apply {
                    latitude = geoPoint.latitude
                    longitude = geoPoint.longitude
                }
                val distance = userLocation.distanceTo(facilityLocation)
                facility.copy(distance = distance)
            } ?: facility
        }.sortedBy { it.distance }

        _uiState.update { it.copy(facilities = facilities) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 