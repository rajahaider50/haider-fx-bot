package com.family.safety.platform.by.raja.haider.ali.ui.devices

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.model.Device
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.util.DeviceUtils
import com.family.safety.platform.by.raja.haider.ali.util.Validators
import com.family.safety.platform.by.raja.haider.ali.viewmodel.AuthState
import com.family.safety.platform.by.raja.haider.ali.viewmodel.DeviceViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PairDeviceActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var prefManager: PreferenceManager
    private lateinit var tilCode: TextInputLayout
    private lateinit var etCode: TextInputEditText
    private lateinit var etChildName: TextInputEditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair_device)

        prefManager = PreferenceManager(this)
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]

        tilCode = findViewById(R.id.tilCode)
        etCode = findViewById(R.id.etCode)
        etChildName = findViewById(R.id.etChildName)
        progressBar = findViewById(R.id.progressBar)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnPair).setOnClickListener {
            val code = etCode.text.toString().trim()
            val childName = etChildName.text.toString().trim()

            if (code.length < 4) {
                tilCode.error = "Enter valid pairing code"
                return@setOnClickListener
            }

            val device = Device(
                deviceModel = DeviceUtils.getDeviceModel(),
                androidVersion = DeviceUtils.getAndroidVersion(),
                ownerId = prefManager.userId,
                isChild = true,
                childName = childName
            )

            deviceViewModel.pairDevice(device, code)
        }

        deviceViewModel.pairResult.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }
}
