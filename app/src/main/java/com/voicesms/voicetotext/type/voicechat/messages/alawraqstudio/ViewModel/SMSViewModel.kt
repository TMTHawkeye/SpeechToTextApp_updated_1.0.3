package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories.VoiceSMSRepositoryImpl

class SMSViewModel(var repository: VoiceSMSRepositoryImpl) : ViewModel(){


    fun getListOfLanguages():ArrayList<LanguageModel>{
        return repository.getListOfLanguagesFromLocale()
    }

    fun getLanguages():ArrayList<LanguageModel>{
        return repository.loadLang()
    }

    fun countryCodeToEmojiFlag(countryCode:String):String{
        return repository.countryCodeToEmojiFlag(countryCode)
    }

    fun getSpeech(fromlanguageCode:String,callBack: (Intent)->Unit){
        repository.getSpeech(fromlanguageCode,callBack)
    }

    fun getTranslation(textToTranslate:String,source:String,target:String,callback:(String)->Unit){
        return repository.getTranslation(textToTranslate,source,target,callback)
    }

    fun downloadModelLanguage(sourceLanguage:String){
         repository.downloadModelLanguage(sourceLanguage)
    }

}