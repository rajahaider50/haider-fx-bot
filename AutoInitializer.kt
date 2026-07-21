package com.family.safety.platform.db

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * AutoInitializer — Runs on app first launch
 * Creates: admin account, default settings, database structure
 * Uses: Firebase Realtime Database (FREE) + Cloudinary for files
 */
object AutoInitializer {

    private const val TAG = "AutoInitializer"
    private val auth = FirebaseAuth.getInstance()
    private val rtdb = FirebaseDatabase.getInstance()

    // Default admin credentials
    private const val ADMIN_EMAIL = "rajahaider@gmail.com"
    private const val ADMIN_PASSWORD = "Admin@123"
    private const val ADMIN_NAME = "Super Admin"

    /**
     * Main initialization — call from Application.onCreate() or SplashActivity
     */
    suspend fun initialize() {
        try {
            Log.d(TAG, "Starting auto-initialization...")

            createDefaultSettings()
            createDefaultAdmin()
            createDefaultAdminPermissions()
            initializeDatabaseStructure()

            Log.d(TAG, "Auto-initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Auto-initialization failed: ${e.message}", e)
        }
    }

    // ==================== DEFAULT SETTINGS ====================
    private suspend fun createDefaultSettings() {
        val settingsRef = rtdb.getReference("settings")

        // Check if settings already exist
        val snapshot = settingsRef.limitToFirst(1).get().await()
        if (snapshot.exists()) {
            Log.d(TAG, "Settings already exist, skipping...")
            return
        }

        val settings = mapOf(
            "app_name" to "Family Safety",
            "max_devices_per_family" to 5,
            "max_children_per_family" to 10,
            "location_update_interval" to 30000,
            "geofence_radius_default" to 100,
            "screen_time_limit_default" to 120,
            "bedtime_start" to "22:00",
            "bedtime_end" to "06:00",
            "study_mode_start" to "08:00",
            "study_mode_end" to "15:00",
            "emergency_contacts_limit" to 5,
            "backup_auto_enabled" to true,
            "backup_interval_hours" to 24,
            "min_android_version" to 24,
            "force_update_enabled" to false,
            "maintenance_mode" to false,
            "debug_mode" to false,
            "notification_sound_enabled" to true,
            "notification_vibration_enabled" to true,
            "location_sharing_enabled" to true,
            "auto_sync_enabled" to true,
            "biometric_enabled" to false,
            "pin_lock_enabled" to false
        )

        settings.forEach { (key, value) ->
            settingsRef.child(key).setValue(mapOf(
                "key" to key,
                "value" to value,
                "updatedAt" to com.google.firebase.database.ServerValue.TIMESTAMP
            )).await()
        }

        Log.d(TAG, "Created ${settings.size} default settings")
    }

    // ==================== DEFAULT ADMIN ====================
    private suspend fun createDefaultAdmin() {
        try {
            // Check if admin already exists
            val adminCheck = rtdb.getReference("admin")
                .orderByChild("role")
                .equalTo("super_admin")
                .limitToFirst(1)
                .get()
                .await()

            if (adminCheck.exists()) {
                Log.d(TAG, "Admin already exists, skipping...")
                return
            }

            // Create Firebase Auth user
            val result = auth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
            val uid = result.user?.uid ?: return

            // Create user document in RTDB
            rtdb.getReference("users").child(uid).setValue(mapOf(
                "name" to ADMIN_NAME,
                "email" to ADMIN_EMAIL,
                "phone" to "",
                "photoUrl" to "",
                "role" to "admin",
                "isActive" to true,
                "lastLogin" to com.google.firebase.database.ServerValue.TIMESTAMP,
                "createdAt" to com.google.firebase.database.ServerValue.TIMESTAMP,
                "updatedAt" to com.google.firebase.database.ServerValue.TIMESTAMP
            )).await()

            // Create admin document
            rtdb.getReference("admin").child(uid).setValue(mapOf(
                "userId" to uid,
                "role" to "super_admin",
                "permissions" to mapOf(
                    "manage_users" to true,
                    "manage_families" to true,
                    "manage_devices" to true,
                    "manage_settings" to true,
                    "view_reports" to true,
                    "manage_admins" to true,
                    "system_settings" to true,
                    "delete_data" to true,
                    "export_data" to true
                ),
                "isActive" to true,
                "createdAt" to com.google.firebase.database.ServerValue.TIMESTAMP
            )).await()

            Log.d(TAG, "Default admin created: $ADMIN_EMAIL")
        } catch (e: Exception) {
            Log.e(TAG, "Admin creation failed: ${e.message}")
        }
    }

    // ==================== ADMIN PERMISSIONS ====================
    private suspend fun createDefaultAdminPermissions() {
        try {
            val permsRef = rtdb.getReference("system_config").child("permissions")
            val snapshot = permsRef.get().await()
            if (snapshot.exists()) return

            permsRef.setValue(mapOf(
                "key" to "permissions",
                "roles" to mapOf(
                    "super_admin" to mapOf(
                        "manage_users" to true,
                        "manage_families" to true,
                        "manage_devices" to true,
                        "manage_settings" to true,
                        "view_reports" to true,
                        "manage_admins" to true,
                        "system_settings" to true,
                        "delete_data" to true,
                        "export_data" to true
                    ),
                    "admin" to mapOf(
                        "manage_users" to true,
                        "manage_families" to true,
                        "manage_devices" to true,
                        "view_reports" to true,
                        "export_data" to true
                    ),
                    "staff" to mapOf(
                        "view_users" to true,
                        "view_devices" to true,
                        "view_reports" to true
                    )
                ),
                "updatedAt" to com.google.firebase.database.ServerValue.TIMESTAMP
            )).await()

            Log.d(TAG, "Default permissions created")
        } catch (e: Exception) {
            Log.e(TAG, "Permissions creation failed: ${e.message}")
        }
    }

    // ==================== DATABASE STRUCTURE ====================
    private suspend fun initializeDatabaseStructure() {
        try {
            val rootRef = rtdb.reference
            val snapshot = rootRef.get().await()

            // Only initialize if root is empty
            if (snapshot.exists() && snapshot.childrenCount > 0) {
                Log.d(TAG, "Database already initialized, skipping...")
                return
            }

            val updates = hashMapOf<String, Any>(
                "online_status/_placeholder" to true,
                "live_location/_placeholder" to true,
                "device_status/_placeholder" to true,
                "screen_state/_placeholder" to true,
                "commands_queue/_placeholder" to true,
                "active_sessions/_placeholder" to true,
                "presence/_placeholder" to true,
                "media/_placeholder" to true
            )

            rootRef.updateChildren(updates).await()
            Log.d(TAG, "Database structure initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Database structure setup failed: ${e.message}")
        }
    }
}
