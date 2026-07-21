package com.family.safety.platform.by.raja.haider.ali.auth

import android.content.Intent
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirm: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirm: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        initViews()
        observeState()
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirm = findViewById(R.id.tilConfirm)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirm)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        btnRegister.setOnClickListener { attemptRegister() }

        findViewById<TextView>(R.id.tvLogin).setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirm = etConfirm.text.toString().trim()

        tilName.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirm.error = null

        var valid = true
        if (!Validators.isValidName(name)) {
            tilName.error = getString(R.string.error_name)
            valid = false
        }
        if (!Validators.isValidEmail(email)) {
            tilEmail.error = getString(R.string.error_email)
            valid = false
        }
        if (!Validators.isValidPassword(password)) {
            tilPassword.error = getString(R.string.error_password)
            valid = false
        }
        if (!Validators.passwordsMatch(password, confirm)) {
            tilConfirm.error = getString(R.string.error_password_match)
            valid = false
        }

        if (valid) viewModel.register(name, email, password)
    }

    private fun observeState() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnRegister.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OtpVerificationActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
