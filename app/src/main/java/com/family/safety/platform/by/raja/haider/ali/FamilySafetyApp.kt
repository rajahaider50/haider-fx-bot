package com.family.safety.platform.by.raja.haider.ali

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.family.safety.platform.by.raja.haider.ali.service.SyncWorker

class FamilySafetyApp : Application() {

    companion object {
        lateinit var instance: FamilySafetyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }

        createNotificationChannels()
        SyncWorker.schedulePeriodic(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val locationChannel = NotificationChannel(
                "location_channel",
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background location tracking"
                setShowBadge(false)
            }

            val alertChannel = NotificationChannel(
                "alert_channel",
                "Safety Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Family safety alerts and notifications"
                enableVibration(true)
                setShowBadge(true)
            }

            val commandChannel = NotificationChannel(
                "command_channel",
                "Remote Commands",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Command execution status"
                setShowBadge(true)
            }

            val syncChannel = NotificationChannel(
                "sync_channel",
                "Cloud Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Data synchronization"
                setShowBadge(false)
            }

            manager.createNotificationChannels(listOf(
                locationChannel, alertChannel, commandChannel, syncChannel
            ))
        }
    }
}
