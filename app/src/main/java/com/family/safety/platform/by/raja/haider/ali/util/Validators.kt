package com.family.safety.platform.by.raja.haider.ali.util

import android.content.Context
import android.util.Patterns

object Validators {
    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun passwordsMatch(p1: String, p2: String): Boolean = p1 == p2

    fun isValidName(name: String): Boolean = name.trim().length >= 2

    fun isValidPin(pin: String): Boolean = pin.length == 4 && pin.all { it.isDigit() }

    fun getPasswordStrength(password: String): PasswordStrength {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return when {
            score <= 2 -> PasswordStrength.WEAK
            score <= 3 -> PasswordStrength.MEDIUM
            score <= 4 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}

enum class PasswordStrength(val label: String, val color: Int) {
    WEAK("Weak", 0xFFFF5252.toInt()),
    MEDIUM("Medium", 0xFFFFD600.toInt()),
    STRONG("Strong", 0xFF00E676.toInt()),
    VERY_STRONG("Very Strong", 0xFF00E676.toInt())
}
