package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.speech.RecognizerIntent
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Arrays
import java.util.Locale


class VoiceSMSRepositoryImpl(var context: Context) {

    var file: File? = null
    var recorder: MediaRecorder? = null
    val TAG = "modeldloadTAG"
    var languagesList = ArrayList<LanguageModel>()

    //    private fun isFirstTime(): Boolean {
//        val preferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        return preferences.getBoolean("isFirstTime", true)
//    }
//
//    private fun setFirstTime(isFirstTime: Boolean) {
//        val preferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = preferences.edit()
//        editor.putBoolean("isFirstTime", isFirstTime)
//        editor.apply()
//    }
    val languagemodelClass: ArrayList<LanguageModel> = ArrayList()

    // Assuming languagemodelClass is a MutableList<LanguageModel>
    val uniqueDisplayNames = HashSet<String>()

    fun addUniqueLanguageModel(languageModel: LanguageModel) {
        if (uniqueDisplayNames.add(languageModel.languageName)) {
            languagemodelClass.add(languageModel)
        }
    }

    fun getListOfLanguagesFromLocale(): ArrayList<LanguageModel> {
        val languageList = Locale.getAvailableLocales()
        val uniqueDisplayNames = HashSet<String>()
        val languageModelList = ArrayList<LanguageModel>()

        languageList.forEach {
            if (!it.country.isEmpty() && !it.country.isBlank() &&
                (!it.country.equals("001") && !it.country.equals("150"))
            ) {
                val displayName = it.displayName
                if (!uniqueDisplayNames.contains(displayName)) {
                    Log.d("TAG", "Language : $displayName +${it.language}+ ${it.country}")
                    languageModelList.add(LanguageModel(displayName, it.language, it.country))
                    uniqueDisplayNames.add(displayName)
                }
            }
        }

        languagesList = languageModelList
        return languageModelList
    }
//
//
//    fun getLanguages(): ArrayList<LanguageModel> {
////        val json =  loadJSONFromAsset("languagesJson.json")
//
////        val list: ArrayList<LanguageModel> = ArrayList(GsonBuilder().create().fromJson(json, ArrayList<LanguageModel::class.java>))
//
//
//        var hotels = loadLang()//Gson().fromJson(json, Array<LanguageModel>::class.java).toList()
//
////        var list = Arrays.asList( GsonBuilder().create().fromJson(json, LanguageModel::class.java)) as ArrayList<LanguageModel>;
//
////        val languageList: List<LanguageModel> = Gson().fromJson(json, object : TypeToken<List<LanguageModel>>() {}.type)
//        hotels.forEach {
//            Log.d("TAG", "Language : ${it.languageName} + ${it.languageCode} + ${it.countryCode}")
//        }
//        return ArrayList(hotels)
//    }

    private fun loadJSONFromAsset(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
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

    private fun extractDisplayName(languageName: String): String {
        val parts: List<String> = languageName.split("(")
        val language = parts[0].trim { it <= ' ' }
        return language
    }

    fun downloadModelLanguage(sourceLanguage: String) {
        val modelManager = RemoteModelManager.getInstance()

// Get translation models stored on the device.
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                for (m in models) {
                    m.language
                }
                Log.d(TAG, "downloadModelLanguage: ${models.size}")
            }
            .addOnFailureListener {
            }

        val model = TranslateRemoteModel.Builder(sourceLanguage).build()
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        modelManager.download(model, conditions)
            .addOnSuccessListener {
                Log.d(TAG, "Model for $sourceLanguage have been dloaded successfully!!")
            }
            .addOnFailureListener {
                Log.d(TAG, "Model for $sourceLanguage have not dloaded!!")

            }
    }

    fun countryCodeToEmojiFlag(countryCode: String): String {
        return countryCode
            .uppercase(Locale.US)
            .map { char ->
                Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
            }
            .map { codePoint ->
                Character.toChars(codePoint)
            }
            .joinToString(separator = "") { charArray ->
                String(charArray)
            }
    }

    fun getSpeech(fromlanguageCode: String, callBack: (Intent) -> Unit) {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, fromlanguageCode)

            if (intent.resolveActivity(context.packageManager) != null) {
                callBack(intent)
            }
        } catch (e: Exception) {
            Log.d(TAG, "getSpeech: ${e.message}")
        }
    }


    fun getTranslation(
        textToTranslate: String,
        sourceLang: String,
        targetLang: String,
        callback: (String) -> Unit
    ) {
//        if (!isNetworkAvailable(context)) {
//            callback("No internet connection available!")
//
//        }

        Log.d("TAG", "getTranslation: source lang $sourceLang and target lang $targetLang")
        var translated_Text = ""
        val options: TranslatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()

        // Create a Translation client
        val translation = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translation.downloadModelIfNeeded(conditions)
            .addOnSuccessListener(
                OnSuccessListener {
                    Log.d("TAG", "Model dload: success")
                    translation.translate(textToTranslate)
                        .addOnSuccessListener(
                            OnSuccessListener<String> { translatedText ->
                                Log.d("TAG", "Text translation: success")
                                callback(translatedText)
                            })
                        .addOnFailureListener(
                            OnFailureListener { e ->
                                Log.d("TAG", "Text translation: failure")
                                callback(e.message!!)
                                Log.d("TAG", "getTranslation: ${e.message}")

                            })

                })
            .addOnFailureListener(
                OnFailureListener { e ->
                    translated_Text = e.message!!
                    Log.d("TAG", "Model dload: failure")
                    if (e.message?.contains("Connection error", true) == true) {
                        // Handle the specific case of no internet
                        callback("No internet connection available!")
                    } else {
                        // Handle other failure cases
                        callback("No translation found!")
                    }
                })


    }


}