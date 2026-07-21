package com.family.safety.platform.by.raja.haider.ali.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.ui.devices.DevicesFragment
import com.family.safety.platform.by.raja.haider.ali.ui.location.LocationFragment
import com.family.safety.platform.by.raja.haider.ali.ui.reports.ReportsFragment
import com.family.safety.platform.by.raja.haider.ali.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_devices -> DevicesFragment()
                R.id.nav_location -> LocationFragment()
                R.id.nav_reports -> ReportsFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
