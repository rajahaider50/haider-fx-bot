package com.family.safety.platform.by.raja.haider.ali.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.ui.home.MainActivity
import com.google.android.material.button.MaterialButton

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText
    private lateinit var btnVerify: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        initViews()
    }

    private fun initViews() {
        etOtp1 = findViewById(R.id.etOtp1)
        etOtp2 = findViewById(R.id.etOtp2)
        etOtp3 = findViewById(R.id.etOtp3)
        etOtp4 = findViewById(R.id.etOtp4)
        etOtp5 = findViewById(R.id.etOtp5)
        etOtp6 = findViewById(R.id.etOtp6)
        btnVerify = findViewById(R.id.btnVerify)
        progressBar = findViewById(R.id.progressBar)

        setupOtpFields()

        btnVerify.setOnClickListener {
            val otp = getOtp()
            if (otp.length == 6) {
                progressBar.visibility = View.VISIBLE
                btnVerify.isEnabled = false

                com.google.firebase.auth.FirebaseAuth.getInstance()
                    .currentUser?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Email verified!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }
                    ?.addOnFailureListener {
                        progressBar.visibility = View.GONE
                        btnVerify.isEnabled = true
                        Toast.makeText(this, "Verification failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tvResend).setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance()
                .currentUser?.sendEmailVerification()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Verification email resent", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupOtpFields() {
        val fields = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)
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

    private fun getOtp(): String {
        return etOtp1.text.toString() + etOtp2.text.toString() +
                etOtp3.text.toString() + etOtp4.text.toString() +
                etOtp5.text.toString() + etOtp6.text.toString()
    }
}
