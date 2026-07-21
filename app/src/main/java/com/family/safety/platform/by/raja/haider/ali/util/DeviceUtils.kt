package com.family.safety.platform.by.raja.haider.ali.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings

object DeviceUtils {

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    fun getAndroidVersion(): String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    fun generatePairingCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    fun generateId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val timestamp = System.currentTimeMillis().toString(36)
        val random = (1..8).map { chars.random() }.joinToString("")
        return "${timestamp}_${random}"
    }
}
