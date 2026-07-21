package com.family.safety.platform.by.raja.haider.ali.ui.settings

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager

class ThemeSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_settings)

        val prefManager = PreferenceManager(this)
        val rbDark = findViewById<RadioButton>(R.id.rbDark)
        val rbLight = findViewById<RadioButton>(R.id.rbLight)

        if (prefManager.themeMode == "light") {
            rbLight.isChecked = true
            rbDark.isChecked = false
        }

        rbDark.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                prefManager.themeMode = "dark"
                Toast.makeText(this, "Dark mode applied", Toast.LENGTH_SHORT).show()
            }
        }

        rbLight.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                prefManager.themeMode = "light"
                Toast.makeText(this, "Light mode applied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
