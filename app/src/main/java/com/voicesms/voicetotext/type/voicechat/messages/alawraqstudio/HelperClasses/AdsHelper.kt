package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.Context

private fun isBannerAdLoaded(context: Context): Boolean {
    val preferences = context.getSharedPreferences("AdPrefs", Context.MODE_PRIVATE)
    return preferences.getBoolean("isBannerAdLoaded", false)
}