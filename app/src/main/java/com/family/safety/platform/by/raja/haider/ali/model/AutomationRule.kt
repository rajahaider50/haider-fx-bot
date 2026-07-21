package com.family.safety.platform.by.raja.haider.ali.model

data class AutomationRule(
    val ruleId: String = "",
    val name: String = "",
    val triggerType: String = "",
    val triggerValue: String = "",
    val actionType: String = "",
    val actionValue: String = "",
    val isActive: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val targetDeviceIds: List<String> = emptyList(),
    val scheduleDays: List<Int> = emptyList(),
    val scheduleStart: String = "",
    val scheduleEnd: String = ""
)