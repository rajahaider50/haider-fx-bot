package com.family.safety.platform.by.raja.haider.ali.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefManager = PreferenceManager(this)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave).setOnClickListener {
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
