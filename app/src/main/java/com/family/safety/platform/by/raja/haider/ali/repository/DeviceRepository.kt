package com.family.safety.platform.by.raja.haider.ali.repository

import com.family.safety.platform.by.raja.haider.ali.model.Device
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DeviceRepository {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun pairDevice(device: Device, pairingCode: String): Result<String> {
        return try {
            val key = db.child("devices").push().key ?: throw Exception("Failed to generate key")
            val deviceWithCode = device.copy(deviceId = key, pairingCode = pairingCode)
            db.child("devices").child(key).setValue(deviceWithCode).await()
            db.child("users").child(device.ownerId).child("devices").child(key).setValue(true).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptPairing(deviceId: String, uid: String): Result<Unit> {
        return try {
            db.child("devices").child(deviceId).child("ownerId").setValue(uid).await()
            db.child("devices").child(deviceId).child("isActive").setValue(true).await()
            db.child("users").child(uid).child("devices").child(deviceId).setValue(true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDevicesByOwner(uid: String): Flow<List<Device>> = callbackFlow {
        val ref = db.child("devices").orderByChild("ownerId").equalTo(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val devices = mutableListOf<Device>()
                for (child in snapshot.children) {
                    child.getValue(Device::class.java)?.let { devices.add(it) }
                }
                trySend(devices)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getChildDevices(uid: String): Flow<List<Device>> = callbackFlow {
        val ref = db.child("devices").orderByChild("ownerId").equalTo(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val devices = mutableListOf<Device>()
                for (child in snapshot.children) {
                    child.getValue(Device::class.java)?.let {
                        if (it.isChild) devices.add(it)
                    }
                }
                trySend(devices)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getDeviceFlow(deviceId: String): Flow<Device?> = callbackFlow {
        val ref = db.child("devices").child(deviceId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Device::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateDeviceLocation(deviceId: String, lat: Double, lng: Double, address: String) {
        val updates = mapOf(
            "latitude" to lat,
            "longitude" to lng,
            "locationName" to address,
            "lastSeen" to System.currentTimeMillis()
        )
        db.child("devices").child(deviceId).updateChildren(updates).await()
    }

    suspend fun updateDeviceStatus(deviceId: String, isOnline: Boolean, battery: Int) {
        val updates = mapOf(
            "isOnline" to isOnline,
            "batteryLevel" to battery,
            "lastSeen" to System.currentTimeMillis()
        )
        db.child("devices").child(deviceId).updateChildren(updates).await()
    }

    suspend fun removeDevice(deviceId: String, uid: String) {
        db.child("devices").child(deviceId).removeValue().await()
        db.child("users").child(uid).child("devices").child(deviceId).removeValue().await()
    }

    suspend fun updateScreenTimeLimit(deviceId: String, limitMs: Long) {
        db.child("devices").child(deviceId).child("dailyScreenTimeLimit").setValue(limitMs).await()
    }
}
