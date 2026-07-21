package com.family.safety.platform.by.raja.haider.ali.repository

import com.family.safety.platform.by.raja.haider.ali.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CommandRepository {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun sendCommand(command: RemoteCommand): Result<String> {
        return try {
            val key = db.child("commands").push().key ?: throw Exception("Key failed")
            val cmd = command.copy(commandId = key)
            db.child("commands").child(key).setValue(cmd).await()
            db.child("device_commands").child(command.deviceId).child(key).setValue(cmd).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDeviceCommands(deviceId: String): Flow<List<RemoteCommand>> = callbackFlow {
        val ref = db.child("device_commands").child(deviceId)
            .orderByChild("issuedAt").limitToLast(20)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commands = mutableListOf<RemoteCommand>()
                for (child in snapshot.children) {
                    child.getValue(RemoteCommand::class.java)?.let { commands.add(it) }
                }
                trySend(commands.reversed())
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getPendingCommands(deviceId: String): Flow<List<RemoteCommand>> = callbackFlow {
        val ref = db.child("device_commands").child(deviceId)
            .orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commands = mutableListOf<RemoteCommand>()
                for (child in snapshot.children) {
                    child.getValue(RemoteCommand::class.java)?.let { commands.add(it) }
                }
                trySend(commands)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateCommandStatus(commandId: String, status: String, result: String = "") {
        val updates = mapOf(
            "status" to status,
            "executedAt" to System.currentTimeMillis(),
            "result" to result
        )
        db.child("commands").child(commandId).updateChildren(updates).await()
    }

    suspend fun sendAlert(alert: SafetyAlert): Result<String> {
        return try {
            val key = db.child("alerts").push().key ?: throw Exception("Key failed")
            val a = alert.copy(alertId = key)
            db.child("alerts").child(key).setValue(a).await()
            db.child("device_alerts").child(alert.deviceId).child(key).setValue(a).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDeviceAlerts(deviceId: String): Flow<List<SafetyAlert>> = callbackFlow {
        val ref = db.child("device_alerts").child(deviceId)
            .orderByChild("timestamp").limitToLast(50)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alerts = mutableListOf<SafetyAlert>()
                for (child in snapshot.children) {
                    child.getValue(SafetyAlert::class.java)?.let { alerts.add(it) }
                }
                trySend(alerts.reversed())
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun markAlertRead(alertId: String) {
        db.child("alerts").child(alertId).child("isRead").setValue(true).await()
    }

    suspend fun deleteAlert(alertId: String, deviceId: String) {
        db.child("alerts").child(alertId).removeValue().await()
        db.child("device_alerts").child(deviceId).child(alertId).removeValue().await()
    }
}
