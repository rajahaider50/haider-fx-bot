package com.family.safety.platform.by.raja.haider.ali.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.repository.DeviceRepository
import com.family.safety.platform.by.raja.haider.ali.repository.LocationRepository
import com.family.safety.platform.by.raja.haider.ali.model.LocationData
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.ui.home.MainActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var prefManager: PreferenceManager
    private val locationRepo = LocationRepository()
    private val deviceRepo = DeviceRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_channel"
        private const val UPDATE_INTERVAL = 30000L
        private const val FASTEST_INTERVAL = 15000L
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        prefManager = PreferenceManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val deviceId = prefManager.deviceId
                    val uid = prefManager.userId

                    if (deviceId.isNotEmpty()) {
                        scope.launch {
                            val locData = LocationData(
                                deviceId = deviceId,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy,
                                batteryLevel = 0,
                                timestamp = System.currentTimeMillis()
                            )
                            locationRepo.saveLocation(locData)
                            deviceRepo.updateDeviceLocation(deviceId, location.latitude, location.longitude, "")
                        }
                    }

                    prefManager.lastKnownLat = location.latitude
                    prefManager.lastKnownLng = location.longitude
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            .setWaitForAccurateLocation(true)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            stopSelf()
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Family Safety")
            .setContentText("Tracking location in background")
            .setSmallIcon(R.drawable.ic_nav_location)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
