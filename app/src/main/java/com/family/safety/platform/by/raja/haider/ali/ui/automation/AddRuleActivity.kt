package com.family.safety.platform.by.raja.haider.ali.ui.automation

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.model.AutomationRule
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.util.DeviceUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class AddRuleActivity : AppCompatActivity() {

    private lateinit var prefManager: PreferenceManager
    private var triggerType = ""
    private var actionType = ""

    private val triggerTypes = listOf(
        "Time of Day", "Battery Level Below", "Location Enter", "Location Exit",
        "App Usage Exceeds", "Device Offline", "Screen Time Exceeds"
    )

    private val actionTypes = listOf(
        "Lock Device", "Send Alert", "Block App", "Send Notification",
        "Start Recording", "Capture Screenshot", "Enable Location"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rule)

        prefManager = PreferenceManager(this)

        val btnTrigger = findViewById<MaterialButton>(R.id.btnTriggerType)
        val btnAction = findViewById<MaterialButton>(R.id.btnActionType)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)
        val etName = findViewById<TextInputEditText>(R.id.etRuleName)
        val etTriggerValue = findViewById<TextInputEditText>(R.id.etTriggerValue)
        val etActionValue = findViewById<TextInputEditText>(R.id.etActionValue)

        btnTrigger.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Trigger")
                .setItems(triggerTypes.toTypedArray()) { _, which ->
                    triggerType = triggerTypes[which]
                    btnTrigger.text = triggerType
                }
                .show()
        }

        btnAction.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Action")
                .setItems(actionTypes.toTypedArray()) { _, which ->
                    actionType = actionTypes[which]
                    btnAction.text = actionType
                }
                .show()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val triggerValue = etTriggerValue.text.toString().trim()
            val actionValue = etActionValue.text.toString().trim()

            if (name.isEmpty() || triggerType.isEmpty() || actionType.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rule = AutomationRule(
                name = name,
                triggerType = triggerType,
                triggerValue = triggerValue,
                actionType = actionType,
                actionValue = actionValue,
                createdBy = prefManager.userId,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )

            val db = FirebaseDatabase.getInstance().reference
            val key = db.child("automation_rules").push().key
            if (key != null) {
                rule.copy(ruleId = key).let { r ->
                    db.child("automation_rules").child(key).setValue(r)
                }
                Toast.makeText(this, "Rule saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
