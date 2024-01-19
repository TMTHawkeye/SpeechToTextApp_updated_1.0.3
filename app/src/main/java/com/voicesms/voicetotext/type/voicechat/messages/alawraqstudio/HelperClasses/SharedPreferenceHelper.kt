package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.Context
import io.paperdb.Paper

fun isFirstTimeLaunch(): Boolean {
//    val preferences = context.getSharedPreferences("LangPrefs", Context.MODE_PRIVATE)
//    val isFirstTime = preferences.getBoolean("isFirstTime", true)
//    if (isFirstTime) {
//        val editor = preferences.edit()
//        editor.putBoolean("isFirstTime", false)
//        editor.apply()
//    }
//    return isFirstTime

     val lang_Staus=Paper.book().read<Boolean>("LangPref",true)
    return lang_Staus!!
}