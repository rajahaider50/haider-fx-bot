package com.family.safety.platform.by.raja.haider.ali.model

data class Device(
    val deviceId: String = "",
    val deviceName: String = "",
    val deviceModel: String = "",
    val androidVersion: String = "",
    val appVersion: String = "",
    val ownerId: String = "",
    val isOnline: Boolean = false,
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val pairedAt: Long = System.currentTimeMillis(),
    val pairingCode: String = "",
    val isChild: Boolean = false,
    val childName: String = "",
    val avatarUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val screenTime: Long = 0L,
    val dailyScreenTimeLimit: Long = 0L,
    val isActive: Boolean = true
)
