package com.family.safety.platform.by.raja.haider.ali.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.auth.LoginActivity
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.ui.admin.AdminDashboardActivity

class SettingsFragment : Fragment() {

    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        view.findViewById<TextView>(R.id.tvProfileName).text = "Parent"
        view.findViewById<TextView>(R.id.tvProfileEmail).text = "user@family.com"

        val switchNotif = view.findViewById<Switch>(R.id.switchNotifications)
        switchNotif.isChecked = prefManager.notificationEnabled
        switchNotif.setOnCheckedChangeListener { _, checked ->
            prefManager.notificationEnabled = checked
        }

        view.findViewById<View>(R.id.layoutAdmin).setOnClickListener {
            startActivity(Intent(requireContext(), AdminDashboardActivity::class.java))
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnLogout).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ ->
                    prefManager.clearSession()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
