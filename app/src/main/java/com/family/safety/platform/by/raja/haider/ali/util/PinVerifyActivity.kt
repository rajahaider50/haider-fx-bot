package com.family.safety.platform.by.raja.haider.ali.util

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.family.safety.platform.by.raja.haider.ali.R

class PinVerifyActivity : AppCompatActivity() {

    private lateinit var etPin1: EditText
    private lateinit var etPin2: EditText
    private lateinit var etPin3: EditText
    private lateinit var etPin4: EditText
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_verify)

        etPin1 = findViewById(R.id.etPin1)
        etPin2 = findViewById(R.id.etPin2)
        etPin3 = findViewById(R.id.etPin3)
        etPin4 = findViewById(R.id.etPin4)
        tvError = findViewById(R.id.tvError)

        setupPinFields()

        val tvBiometric = findViewById<TextView>(R.id.tvBiometric)
        if (BiometricHelper.isBiometricAvailable(this)) {
            tvBiometric.visibility = View.VISIBLE
            tvBiometric.setOnClickListener {
                BiometricHelper.showBiometricPrompt(
                    this,
                    onSuccess = { setResultAndFinish() },
                    onError = { msg -> showError(msg) }
                )
            }
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVerify).setOnClickListener {
            val pin = etPin1.text.toString() + etPin2.text.toString() +
                    etPin3.text.toString() + etPin4.text.toString()

            if (pin.length < 4) {
                showError("Enter complete PIN")
                return@setOnClickListener
            }

            if (SecurityUtils.verifyPin(this, pin)) {
                setResultAndFinish()
            } else {
                showError("Incorrect PIN")
                etPin1.text.clear()
                etPin2.text.clear()
                etPin3.text.clear()
                etPin4.text.clear()
                etPin1.requestFocus()
            }
        }
    }

    private fun setupPinFields() {
        val fields = arrayOf(etPin1, etPin2, etPin3, etPin4)
        for (i in fields.indices) {
            fields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < fields.lastIndex) {
                        fields[i + 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun showError(msg: String) {
        tvError.text = msg
        tvError.visibility = View.VISIBLE
    }

    private fun setResultAndFinish() {
        setResult(RESULT_OK)
        finish()
    }
}
