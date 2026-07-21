package com.family.safety.platform.by.raja.haider.ali.model

data class RemoteCommand(
    val commandId: String = "",
    val deviceId: String = "",
    val type: String = "",
    val parameters: Map<String, Any> = emptyMap(),
    val status: String = "pending",
    val issuedBy: String = "",
    val issuedAt: Long = System.currentTimeMillis(),
    val executedAt: Long = 0L,
    val result: String = ""
)
