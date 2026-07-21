package com.family.safety.platform.by.raja.haider.ali.ui.devices

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.util.DateUtils
import com.family.safety.platform.by.raja.haider.ali.viewmodel.DeviceViewModel

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var prefManager: PreferenceManager
    private var deviceId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        deviceId = intent.getStringExtra("device_id") ?: return
        prefManager = PreferenceManager(this)
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]

        deviceViewModel.observeDevice(deviceId)

        deviceViewModel.selectedDevice.observe(this) { device ->
            device?.let {
                findViewById<TextView>(R.id.tvDeviceName).text = it.deviceName.ifEmpty { it.deviceModel }
                findViewById<TextView>(R.id.tvDeviceModel).text = "${it.deviceModel} • ${it.androidVersion}"
                findViewById<TextView>(R.id.tvBattery).text = "${it.batteryLevel}%"
                findViewById<TextView>(R.id.tvScreenTime).text = DateUtils.formatScreenTime(it.screenTime)
                findViewById<TextView>(R.id.tvLastSeen).text = DateUtils.timeAgo(it.lastSeen)
                findViewById<View>(R.id.viewStatusDot).setBackgroundResource(
                    if (it.isOnline) R.drawable.status_online else R.drawable.status_offline
                )
            }
        }

        findViewById<android.view.View>(R.id.btnRemove).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Remove Device")
                .setMessage("Are you sure you want to remove this device?")
                .setPositiveButton("Remove") { _, _ ->
                    deviceViewModel.removeDevice(deviceId, prefManager.userId)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        findViewById<android.view.View>(R.id.btnLock).setOnClickListener {
            Toast.makeText(this, "Lock command sent", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.btnLocation).setOnClickListener {
            Toast.makeText(this, "Opening location...", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.btnAlert).setOnClickListener {
            Toast.makeText(this, "Alert sent to device", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }
}
