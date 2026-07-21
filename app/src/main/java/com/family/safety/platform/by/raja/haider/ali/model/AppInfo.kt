package com.family.safety.platform.by.raja.haider.ali.model

data class AppInfo(
    val packageName: String = "",
    val appName: String = "",
    val isBlocked: Boolean = false,
    val dailyLimit: Long = 0L,
    val usageToday: Long = 0L,
    val category: String = "",
    val iconUrl: String = ""
)
