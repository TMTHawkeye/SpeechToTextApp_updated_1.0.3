package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
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


