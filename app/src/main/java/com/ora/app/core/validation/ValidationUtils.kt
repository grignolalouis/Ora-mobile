package com.ora.app.core.validation

import android.util.Patterns

object ValidationConstants {
    const val MAX_NAME_LENGTH = 50
    const val MIN_PASSWORD_LENGTH = 8
}

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= ValidationConstants.MIN_PASSWORD_LENGTH
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length <= ValidationConstants.MAX_NAME_LENGTH
    }
}
