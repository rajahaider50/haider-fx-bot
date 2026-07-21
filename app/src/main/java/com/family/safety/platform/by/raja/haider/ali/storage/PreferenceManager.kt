package com.family.safety.platform.by.raja.haider.ali.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferenceManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "family_safety_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val simplePrefs: SharedPreferences =
        context.getSharedPreferences("family_safety_simple", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var userId: String
        get() = prefs.getString("user_id", "") ?: ""
        set(value) = prefs.edit().putString("user_id", value).apply()

    var authToken: String
        get() = prefs.getString("auth_token", "") ?: ""
        set(value) = prefs.edit().putString("auth_token", value).apply()

    var onboardingComplete: Boolean
        get() = simplePrefs.getBoolean("onboarding_complete", false)
        set(value) = simplePrefs.edit().putBoolean("onboarding_complete", value).apply()

    var userRole: String
        get() = prefs.getString("user_role", "parent") ?: "parent"
        set(value) = prefs.edit().putString("user_role", value).apply()

    var isAdmin: Boolean
        get() = userRole == "super_admin" || userRole == "admin"
        set(_) {}

    var themeMode: String
        get() = simplePrefs.getString("theme_mode", "dark") ?: "dark"
        set(value) = simplePrefs.edit().putString("theme_mode", value).apply()

    var biometricEnabled: Boolean
        get() = prefs.getBoolean("biometric_enabled", false)
        set(value) = prefs.edit().putBoolean("biometric_enabled", value).apply()

    var selectedLanguage: String
        get() = simplePrefs.getString("language", "en") ?: "en"
        set(value) = simplePrefs.edit().putString("language", value).apply()

    var notificationEnabled: Boolean
        get() = simplePrefs.getBoolean("notifications_enabled", true)
        set(value) = simplePrefs.edit().putBoolean("notifications_enabled", value).apply()

    var locationTrackingEnabled: Boolean
        get() = simplePrefs.getBoolean("location_tracking", true)
        set(value) = simplePrefs.edit().putBoolean("location_tracking", value).apply()

    var lastKnownLat: Double
        get() = prefs.getFloat("last_lat", 0f).toDouble()
        set(value) = prefs.edit().putFloat("last_lat", value.toFloat()).apply()

    var lastKnownLng: Double
        get() = prefs.getFloat("last_lng", 0f).toDouble()
        set(value) = prefs.edit().putFloat("last_lng", value.toFloat()).apply()

    var deviceId: String
        get() = prefs.getString("device_id", "") ?: ""
        set(value) = prefs.edit().putString("device_id", value).apply()

    var familyId: String
        get() = prefs.getString("family_id", "") ?: ""
        set(value) = prefs.edit().putString("family_id", value).apply()

    fun clearSession() {
        prefs.edit().clear().apply()
        simplePrefs.edit().putBoolean("onboarding_complete", true).apply()
    }
}
