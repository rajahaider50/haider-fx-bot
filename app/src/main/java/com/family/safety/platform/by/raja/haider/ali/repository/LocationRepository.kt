package com.family.safety.platform.by.raja.haider.ali.repository

import com.family.safety.platform.by.raja.haider.ali.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationRepository {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun saveLocation(location: LocationData): Result<String> {
        return try {
            val key = db.child("locations").push().key ?: throw Exception("Key generation failed")
            val loc = location.copy(locationId = key)
            db.child("locations").child(key).setValue(loc).await()
            db.child("device_locations").child(location.deviceId).child(key).setValue(loc).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLocationHistory(deviceId: String, limit: Int = 50): Flow<List<LocationData>> = callbackFlow {
        val ref = db.child("device_locations").child(deviceId)
            .orderByChild("timestamp")
            .limitToLast(limit)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = mutableListOf<LocationData>()
                for (child in snapshot.children) {
                    child.getValue(LocationData::class.java)?.let { locations.add(it) }
                }
                trySend(locations.reversed())
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getCurrentLocation(deviceId: String): Flow<LocationData?> = callbackFlow {
        val ref = db.child("devices").child(deviceId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                val lng = snapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                if (lat != 0.0 && lng != 0.0) {
                    trySend(LocationData(
                        deviceId = deviceId,
                        latitude = lat,
                        longitude = lng,
                        address = snapshot.child("locationName").getValue(String::class.java) ?: "",
                        timestamp = snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
                    ))
                } else {
                    trySend(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun saveGeofence(geofence: Geofence): Result<String> {
        return try {
            val key = db.child("geofences").push().key ?: throw Exception("Key failed")
            val geo = geofence.copy(geofenceId = key)
            db.child("geofences").child(key).setValue(geo).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getGeofences(uid: String): Flow<List<Geofence>> = callbackFlow {
        val ref = db.child("geofences").orderByChild("createdBy").equalTo(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val geofences = mutableListOf<Geofence>()
                for (child in snapshot.children) {
                    child.getValue(Geofence::class.java)?.let { geofences.add(it) }
                }
                trySend(geofences)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun deleteGeofence(geofenceId: String) {
        db.child("geofences").child(geofenceId).removeValue().await()
    }

    suspend fun toggleGeofence(geofenceId: String, isActive: Boolean) {
        db.child("geofences").child(geofenceId).child("isActive").setValue(isActive).await()
    }
}
