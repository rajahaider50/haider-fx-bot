package com.family.safety.platform.by.raja.haider.ali.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.repository.AuthRepository
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.ui.onboarding.OnboardingActivity
import com.family.safety.platform.by.raja.haider.ali.ui.home.MainActivity
import com.family.safety.platform.by.raja.haider.ali.auth.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefManager = PreferenceManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                !prefManager.onboardingComplete -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
                authRepo.isLoggedIn -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            finish()
        }, 2200)
    }
}
