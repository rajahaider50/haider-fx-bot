package com.family.safety.platform.by.raja.haider.ali.model

data class DailyReport(
    val reportId: String = "",
    val deviceId: String = "",
    val date: String = "",
    val totalScreenTime: Long = 0L,
    val unlockCount: Int = 0,
    val topApps: List<AppUsage> = emptyList(),
    val locationCount: Int = 0,
    val alertCount: Int = 0,
    val batteryStats: BatteryStats = BatteryStats()
)

data class AppUsage(
    val packageName: String = "",
    val appName: String = "",
    val usageMinutes: Long = 0L,
    val openCount: Int = 0
)

data class BatteryStats(
    val averageLevel: Int = 0,
    val chargeCycles: Int = 0,
    val timeCharging: Long = 0L
)
