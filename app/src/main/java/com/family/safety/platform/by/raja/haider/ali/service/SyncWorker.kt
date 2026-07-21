package com.family.safety.platform.by.raja.haider.ali.service

import android.content.Context
import androidx.work.*
import com.family.safety.platform.by.raja.haider.ali.repository.DeviceRepository
import com.family.safety.platform.by.raja.haider.ali.repository.LocationRepository
import com.family.safety.platform.by.raja.haider.ali.model.LocationData
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefManager = PreferenceManager(applicationContext)
        val deviceRepo = DeviceRepository()
        val locationRepo = LocationRepository()

        val deviceId = prefManager.deviceId
        val userId = prefManager.userId

        if (deviceId.isEmpty() || userId.isEmpty()) {
            return Result.success()
        }

        try {
            deviceRepo.updateDeviceStatus(deviceId, true, 0)

            val lat = prefManager.lastKnownLat
            val lng = prefManager.lastKnownLng
            if (lat != 0.0 && lng != 0.0) {
                val locData = LocationData(
                    deviceId = deviceId,
                    latitude = lat,
                    longitude = lng,
                    timestamp = System.currentTimeMillis()
                )
                locationRepo.saveLocation(locData)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "family_safety_sync"

        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun triggerOneTime(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
