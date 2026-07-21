package com.family.safety.platform.by.raja.haider.ali.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "parent",
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val familyId: String = "",
    val pin: String = "",
    val isBiometricEnabled: Boolean = false,
    val theme: String = "dark",
    val language: String = "en"
)
