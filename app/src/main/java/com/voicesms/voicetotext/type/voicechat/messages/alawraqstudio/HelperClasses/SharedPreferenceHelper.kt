package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.Context
import io.paperdb.Paper

fun isFirstTimeLaunch(): Boolean {
     val lang_Staus=Paper.book().read<Boolean>("LangPref",true)
    return lang_Staus!!
}