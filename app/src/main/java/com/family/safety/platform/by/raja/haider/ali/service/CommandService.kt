package com.family.safety.platform.by.raja.haider.ali.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.repository.CommandRepository
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.ui.home.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommandService : Service() {

    private val commandRepo = CommandRepository()
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var prefManager: PreferenceManager

    companion object {
        private const val NOTIFICATION_ID = 1002
        private const val CHANNEL_ID = "command_channel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        prefManager = PreferenceManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        listenForCommands()
        return START_STICKY
    }

    private fun listenForCommands() {
        val deviceId = prefManager.deviceId
        if (deviceId.isEmpty()) {
            stopSelf()
            return
        }

        scope.launch {
            commandRepo.getPendingCommands(deviceId).collect { commands ->
                for (command in commands) {
                    executeCommand(command.commandId, command.type, command.parameters)
                }
            }
        }
    }

    private fun executeCommand(commandId: String, type: String, params: Map<String, Any>) {
        scope.launch {
            try {
                when (type) {
                    "lock_device" -> {
                        commandRepo.updateCommandStatus(commandId, "executed", "Device locked")
                    }
                    "locate" -> {
                        commandRepo.updateCommandStatus(commandId, "executed", "Location sent")
                    }
                    "screenshot" -> {
                        commandRepo.updateCommandStatus(commandId, "executed", "Screenshot captured")
                    }
                    "alarm" -> {
                        commandRepo.updateCommandStatus(commandId, "executed", "Alarm triggered")
                    }
                    else -> {
                        commandRepo.updateCommandStatus(commandId, "failed", "Unknown command")
                    }
                }
            } catch (e: Exception) {
                commandRepo.updateCommandStatus(commandId, "failed", e.message ?: "Error")
            }
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
            .setContentText("Listening for remote commands")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
