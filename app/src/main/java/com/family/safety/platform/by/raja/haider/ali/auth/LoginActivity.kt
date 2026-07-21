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
import com.family.safety.platform.by.raja.haider.ali.ui.home.MainActivity
import com.family.safety.platform.by.raja.haider.ali.util.Validators
import com.family.safety.platform.by.raja.haider.ali.viewmodel.AuthState
import com.family.safety.platform.by.raja.haider.ali.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        initViews()
        setupListeners()
        observeState()
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        findViewById<TextView>(R.id.tvForgot).setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (Validators.isValidEmail(email)) {
                viewModel.forgotPassword(email)
            } else {
                tilEmail.error = getString(R.string.error_email)
            }
        }

        findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        tilEmail.error = null
        tilPassword.error = null

        var valid = true
        if (!Validators.isValidEmail(email)) {
            tilEmail.error = getString(R.string.error_email)
            valid = false
        }
        if (!Validators.isValidPassword(password)) {
            tilPassword.error = getString(R.string.error_password)
            valid = false
        }

        if (valid) {
            viewModel.login(email, password)
        }
    }

    private fun observeState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                }
                is AuthState.Success -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is AuthState.Error -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
