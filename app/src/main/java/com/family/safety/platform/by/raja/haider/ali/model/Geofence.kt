package com.family.safety.platform.by.raja.haider.ali.model

data class Geofence(
    val geofenceId: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Float = 500f,
    val type: String = "safe",
    val isActive: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val notifyOnEnter: Boolean = true,
    val notifyOnExit: Boolean = true
)
