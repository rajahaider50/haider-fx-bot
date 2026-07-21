package com.family.safety.platform.by.raja.haider.ali.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.safety.platform.by.raja.haider.ali.model.*
import com.family.safety.platform.by.raja.haider.ali.repository.DeviceRepository
import kotlinx.coroutines.launch

class DeviceViewModel : ViewModel() {

    private val repo = DeviceRepository()

    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    private val _childDevices = MutableLiveData<List<Device>>()
    val childDevices: LiveData<List<Device>> = _childDevices

    private val _selectedDevice = MutableLiveData<Device?>()
    val selectedDevice: LiveData<Device?> = _selectedDevice

    private val _pairResult = MutableLiveData<AuthState>()
    val pairResult: LiveData<AuthState> = _pairResult

    fun loadDevices(uid: String) {
        viewModelScope.launch {
            repo.getDevicesByOwner(uid).collect { _devices.postValue(it) }
        }
    }

    fun loadChildDevices(uid: String) {
        viewModelScope.launch {
            repo.getChildDevices(uid).collect { _childDevices.postValue(it) }
        }
    }

    fun observeDevice(deviceId: String) {
        viewModelScope.launch {
            repo.getDeviceFlow(deviceId).collect { _selectedDevice.postValue(it) }
        }
    }

    fun pairDevice(device: Device, code: String) {
        _pairResult.value = AuthState.Loading
        viewModelScope.launch {
            repo.pairDevice(device, code).fold(
                onSuccess = { _pairResult.postValue(AuthState.Success("Device paired")) },
                onFailure = { _pairResult.postValue(AuthState.Error(it.message ?: "Pairing failed")) }
            )
        }
    }

    fun removeDevice(deviceId: String, uid: String) {
        viewModelScope.launch {
            repo.removeDevice(deviceId, uid)
        }
    }

    fun updateScreenTimeLimit(deviceId: String, limitMs: Long) {
        viewModelScope.launch {
            repo.updateScreenTimeLimit(deviceId, limitMs)
        }
    }
}
