package com.family.safety.platform.by.raja.haider.ali.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.safety.platform.by.raja.haider.ali.model.*
import com.family.safety.platform.by.raja.haider.ali.repository.CommandRepository
import kotlinx.coroutines.launch

class CommandViewModel : ViewModel() {

    private val repo = CommandRepository()

    private val _commands = MutableLiveData<List<RemoteCommand>>()
    val commands: LiveData<List<RemoteCommand>> = _commands

    private val _alerts = MutableLiveData<List<SafetyAlert>>()
    val alerts: LiveData<List<SafetyAlert>> = _alerts

    private val _unreadAlerts = MutableLiveData<Int>()
    val unreadAlerts: LiveData<Int> = _unreadAlerts

    fun loadCommands(deviceId: String) {
        viewModelScope.launch {
            repo.getDeviceCommands(deviceId).collect { _commands.postValue(it) }
        }
    }

    fun loadAlerts(deviceId: String) {
        viewModelScope.launch {
            repo.getDeviceAlerts(deviceId).collect { alerts ->
                _alerts.postValue(alerts)
                _unreadAlerts.postValue(alerts.count { !it.isRead })
            }
        }
    }

    fun sendCommand(command: RemoteCommand) {
        viewModelScope.launch { repo.sendCommand(command) }
    }

    fun markAlertRead(alertId: String) {
        viewModelScope.launch { repo.markAlertRead(alertId) }
    }

    fun deleteAlert(alertId: String, deviceId: String) {
        viewModelScope.launch { repo.deleteAlert(alertId, deviceId) }
    }
}
