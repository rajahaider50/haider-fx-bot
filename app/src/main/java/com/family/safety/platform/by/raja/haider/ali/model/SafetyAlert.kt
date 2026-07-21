package com.family.safety.platform.by.raja.haider.ali.model

data class SafetyAlert(
    val alertId: String = "",
    val deviceId: String = "",
    val type: String = "",
    val title: String = "",
    val message: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val severity: String = "medium"
)
