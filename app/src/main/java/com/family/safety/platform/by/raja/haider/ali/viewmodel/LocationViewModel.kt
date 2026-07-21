package com.family.safety.platform.by.raja.haider.ali.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.safety.platform.by.raja.haider.ali.model.*
import com.family.safety.platform.by.raja.haider.ali.repository.LocationRepository
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val repo = LocationRepository()

    private val _locations = MutableLiveData<List<LocationData>>()
    val locations: LiveData<List<LocationData>> = _locations

    private val _currentLocation = MutableLiveData<LocationData?>()
    val currentLocation: LiveData<LocationData?> = _currentLocation

    private val _geofences = MutableLiveData<List<Geofence>>()
    val geofences: LiveData<List<Geofence>> = _geofences

    fun loadLocationHistory(deviceId: String) {
        viewModelScope.launch {
            repo.getLocationHistory(deviceId).collect { _locations.postValue(it) }
        }
    }

    fun observeCurrentLocation(deviceId: String) {
        viewModelScope.launch {
            repo.getCurrentLocation(deviceId).collect { _currentLocation.postValue(it) }
        }
    }

    fun loadGeofences(uid: String) {
        viewModelScope.launch {
            repo.getGeofences(uid).collect { _geofences.postValue(it) }
        }
    }

    fun addGeofence(geofence: Geofence) {
        viewModelScope.launch { repo.saveGeofence(geofence) }
    }

    fun deleteGeofence(id: String) {
        viewModelScope.launch { repo.deleteGeofence(id) }
    }

    fun toggleGeofence(id: String, active: Boolean) {
        viewModelScope.launch { repo.toggleGeofence(id, active) }
    }
}
