package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces

interface LanguageSelectionListener {
    fun onLanguageSelected(selectedLanguage: String,selectedCode:String,countryName:String,from:String)
    fun onDismisBottomSheet(isDismissed:Boolean)
}