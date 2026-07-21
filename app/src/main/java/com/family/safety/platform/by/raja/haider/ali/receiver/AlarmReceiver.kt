package com.family.safety.platform.by.raja.haider.ali.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action") ?: return

        when (action) {
            "start_location" -> {
                val serviceIntent = Intent(context, com.family.safety.platform.by.raja.haider.ali.service.LocationService::class.java)
                context.startForegroundService(serviceIntent)
            }
            "stop_location" -> {
                context.stopService(Intent(context, com.family.safety.platform.by.raja.haider.ali.service.LocationService::class.java))
            }
        }
    }
}
