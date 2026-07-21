package com.family.safety.platform.by.raja.haider.ali.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.family.safety.platform.by.raja.haider.ali.service.LocationService
import com.family.safety.platform.by.raja.haider.ali.service.CommandService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val locationIntent = Intent(context, LocationService::class.java)
            context.startForegroundService(locationIntent)

            val commandIntent = Intent(context, CommandService::class.java)
            context.startForegroundService(commandIntent)
        }
    }
}
