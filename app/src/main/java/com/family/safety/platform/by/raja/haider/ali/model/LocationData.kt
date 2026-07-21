package com.family.safety.platform.by.raja.haider.ali.model

data class LocationData(
    val locationId: String = "",
    val deviceId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f,
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val batteryLevel: Int = 0
)
