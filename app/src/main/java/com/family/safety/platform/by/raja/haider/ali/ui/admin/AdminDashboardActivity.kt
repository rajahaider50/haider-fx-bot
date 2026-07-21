package com.family.safety.platform.by.raja.haider.ali.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<android.view.View>(R.id.layoutUsers).setOnClickListener {
            Toast.makeText(this, "Manage Users - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<android.view.View>(R.id.layoutAllDevices).setOnClickListener {
            Toast.makeText(this, "All Devices - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<android.view.View>(R.id.layoutAppSettings).setOnClickListener {
            Toast.makeText(this, "App Settings - Coming Soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<android.view.View>(R.id.layoutLogs).setOnClickListener {
            Toast.makeText(this, "System Logs - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }
}
