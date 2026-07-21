package com.family.safety.platform.by.raja.haider.ali.auth

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.util.Validators
import com.family.safety.platform.by.raja.haider.ali.viewmodel.AuthState
import com.family.safety.platform.by.raja.haider.ali.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSend: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        initViews()
        observeState()
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        btnSend = findViewById(R.id.btnSend)
        progressBar = findViewById(R.id.progressBar)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (Validators.isValidEmail(email)) {
                viewModel.forgotPassword(email)
            } else {
                tilEmail.error = getString(R.string.error_email)
            }
        }

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }
    }

    private fun observeState() {
        viewModel.forgotState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnSend.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnSend.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    finish()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnSend.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
