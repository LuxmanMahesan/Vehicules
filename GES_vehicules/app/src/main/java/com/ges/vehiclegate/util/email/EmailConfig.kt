package com.ges.vehiclegate.util.email

import android.content.Context
import android.content.SharedPreferences

object EmailConfig {
    private const val PREFS_NAME = "email_config"
    private const val KEY_FROM_EMAIL = "from_email"
    private const val KEY_APP_PASSWORD = "app_password"
    private const val KEY_TO_EMAIL = "to_email"

    private const val DEFAULT_TO_EMAIL = "ges.vehicule@gmail.com"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveEmailConfig(context: Context, fromEmail: String, appPassword: String) {
        getPrefs(context).edit().apply {
            putString(KEY_FROM_EMAIL, fromEmail)
            putString(KEY_APP_PASSWORD, appPassword)
            apply()
        }
    }

    fun getFromEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_FROM_EMAIL, null)
    }

    fun getAppPassword(context: Context): String? {
        return getPrefs(context).getString(KEY_APP_PASSWORD, null)
    }

    fun getToEmail(context: Context): String {
        return getPrefs(context).getString(KEY_TO_EMAIL, DEFAULT_TO_EMAIL) ?: DEFAULT_TO_EMAIL
    }

    fun isConfigured(context: Context): Boolean {
        val fromEmail = getFromEmail(context)
        val password = getAppPassword(context)
        return !fromEmail.isNullOrBlank() && !password.isNullOrBlank()
    }

    fun clearConfig(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
