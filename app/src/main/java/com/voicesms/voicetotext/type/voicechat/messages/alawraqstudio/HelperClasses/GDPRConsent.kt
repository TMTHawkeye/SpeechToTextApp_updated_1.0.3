package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.Context
import android.content.SharedPreferences

object GDPRConsent {
    private const val PREF_NAME = "GDPRConsentPrefs"
    private const val KEY_CONSENT_GIVEN = "consent_given"

    fun isConsentGiven(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(KEY_CONSENT_GIVEN, false)
    }

    fun giveConsent(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(KEY_CONSENT_GIVEN, true).apply()
    }

    fun showConsentDialog(context: Context, callback: ConsentCallback) {
        // Display your GDPR consent dialog here
        // You should implement the actual dialog or use the library's method to show it
        // Once the user gives or declines consent, call the appropriate callback method

        // For example, after the user gives consent:
        callback.onConsentGiven()

        // Or, if the user declines consent:
        // callback.onConsentDeclined()
    }
}

interface ConsentCallback {
    fun onConsentGiven()
    fun onConsentDeclined()
}
