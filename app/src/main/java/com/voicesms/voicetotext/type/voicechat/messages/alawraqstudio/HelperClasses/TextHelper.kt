package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel

fun copyToClipbard(context: Context, text: String) {
    val clipboard: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("copiedText", text)
    if (!text.isEmpty()) {
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT)
            .show()
    } else {
        Toast.makeText(context, "Empty text can not be copied!", Toast.LENGTH_SHORT)
            .show()
    }
}

fun shareText(context:Context,message: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, message)

    context.startActivity(Intent.createChooser(intent, "Share via"))
}

 fun getCountryName(languageList: ArrayList<LanguageModel>, targetCode:String, targetName:String): Int {
    for ((index, language) in languageList.withIndex()) {
        if (language.countryCode == targetCode) {
            return index
        }
    }
     if(targetName.equals("ur")) {
         return 56
     }
     else{
         return 11
     }
}



 fun extractLanguageFromCountry(listLanguages:ArrayList<LanguageModel>,position:Int):String{
    val fullString = listLanguages.get(position).languageName

    val parts: List<String> = fullString.split("(")
    val language = parts[0].trim { it <= ' ' }
    return language
}



 fun dloadLanguage(sourceLanguage: String, callback: (Boolean) -> Unit) {
    val modelManager = RemoteModelManager.getInstance()
    val model = TranslateRemoteModel.Builder(sourceLanguage).build()
    val conditions = DownloadConditions.Builder()
        .build()
    modelManager.download(model, conditions)
        .addOnSuccessListener {
            Log.d("TAG", "Model for $sourceLanguage have been dloaded successfully!!")
            callback(true)
        }
        .addOnFailureListener {
            Log.d("TAG", "Model for $sourceLanguage have not dloaded!!")
            callback(false)
        }
}

 fun handleSpannableString(fullText:String): SpannableString {
    val spannableString = SpannableString(fullText)

    // Set color for "VOICE"
    val voiceColor = Color.YELLOW
    spannableString.setSpan(ForegroundColorSpan(voiceColor), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Set color for "SMS"
    val smsColor = Color.WHITE
    spannableString.setSpan(ForegroundColorSpan(smsColor), 6, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    return spannableString
}


 fun isInternetAvailable(context:Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
    return networkInfo?.isConnectedOrConnecting == true
}


fun loadLang(): ArrayList<LanguageModel> {

    var langLIst = ArrayList<LanguageModel>()

    langLIst.add(LanguageModel("af", "Afrikaans", "ZA"))
    langLIst.add(LanguageModel("ar", "Arabic", "SA"))
    langLIst.add(LanguageModel("be", "Belarusian", "BY"))
    langLIst.add(LanguageModel("bg", "Bulgarian", "BG"))
    langLIst.add(LanguageModel("bn", "Bengali", "BD"))
    langLIst.add(LanguageModel("ca", "Catalan", "ES"))
    langLIst.add(LanguageModel("cs", "Czech", "CZ"))
    langLIst.add(LanguageModel("cy", "Welsh", "GB"))
    langLIst.add(LanguageModel("da", "Danish", "DK"))
    langLIst.add(LanguageModel("de", "German", "DE"))
    langLIst.add(LanguageModel("el", "Greek", "GR"))
    langLIst.add(LanguageModel("en", "English", "US"))
    langLIst.add(LanguageModel("eo", "Esperanto", ""))
    langLIst.add(LanguageModel("es", "Spanish", "ES"))
    langLIst.add(LanguageModel("et", "Estonian", "EE"))
    langLIst.add(LanguageModel("fa", "Persian", "IR"))
    langLIst.add(LanguageModel("fi", "Finnish", "FI"))
    langLIst.add(LanguageModel("fr", "French", "FR"))
    langLIst.add(LanguageModel("ga", "Irish", "IE"))
    langLIst.add(LanguageModel("gl", "Galician", "ES"))
    langLIst.add(LanguageModel("gu", "Gujarati", "IN"))
    langLIst.add(LanguageModel("he", "Hebrew", "IL"))
    langLIst.add(LanguageModel("hi", "Hindi", "IN"))
    langLIst.add(LanguageModel("hr", "Croatian", "HR"))
    langLIst.add(LanguageModel("ht", "Haitian Creole", "HT"))
    langLIst.add(LanguageModel("hu", "Hungarian", "HU"))
    langLIst.add(LanguageModel("id", "Indonesian", "ID"))
    langLIst.add(LanguageModel("is", "Icelandic", "IS"))
    langLIst.add(LanguageModel("it", "Italian", "IT"))
    langLIst.add(LanguageModel("ja", "Japanese", "JP"))
    langLIst.add(LanguageModel("ka", "Georgian", "GE"))
    langLIst.add(LanguageModel("kn", "Kannada", "IN"))
    langLIst.add(LanguageModel("ko", "Korean", "KR"))
    langLIst.add(LanguageModel("lt", "Lithuanian", "LT"))
    langLIst.add(LanguageModel("lv", "Latvian", "LV"))
    langLIst.add(LanguageModel("mk", "Macedonian", "MK"))
    langLIst.add(LanguageModel("mr", "Marathi", "IN"))
    langLIst.add(LanguageModel("ms", "Malay", "MY"))
    langLIst.add(LanguageModel("mt", "Maltese", "MT"))
    langLIst.add(LanguageModel("nl", "Dutch", "NL"))
    langLIst.add(LanguageModel("no", "Norwegian", "NO"))
    langLIst.add(LanguageModel("pl", "Polish", "PL"))
    langLIst.add(LanguageModel("pt", "Portuguese", "PT"))
    langLIst.add(LanguageModel("ro", "Romanian", "RO"))
    langLIst.add(LanguageModel("ru", "Russian", "RU"))
    langLIst.add(LanguageModel("sk", "Slovak", "SK"))
    langLIst.add(LanguageModel("sl", "Slovenian", "SI"))
    langLIst.add(LanguageModel("sq", "Albanian", "AL"))
    langLIst.add(LanguageModel("sv", "Swedish", "SE"))
    langLIst.add(LanguageModel("sw", "Swahili", "KE"))
    langLIst.add(LanguageModel("ta", "Tamil", "IN"))
    langLIst.add(LanguageModel("te", "Telugu", "IN"))
    langLIst.add(LanguageModel("th", "Thai", "TH"))
    langLIst.add(LanguageModel("tl", "Tagalog", "PH"))
    langLIst.add(LanguageModel("tr", "Turkish", "TR"))
    langLIst.add(LanguageModel("uk", "Ukrainian", "UA"))
    langLIst.add(LanguageModel("ur", "Urdu", "PK"))
    langLIst.add(LanguageModel("vi", "Vietnamese", "VN"))
    langLIst.add(LanguageModel("zh", "Chinese", "CN"))
    return langLIst
}


