package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.LanguageAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityLangungeBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import java.util.Locale

class LangungeActivity : AppCompatActivity() {
    lateinit var binding:ActivityLangungeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLangungeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AdManager.getInstance().loadNativeAd(this@LangungeActivity, BuildConfig.language_Screen_Native,binding.adViewContainer)

        val languagesList=getLanguagesList()
        val adapter=LanguageAdapter(this@LangungeActivity,languagesList)
        binding.languagesRV.layoutManager= GridLayoutManager(this, 3)
        binding.languagesRV.adapter=adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }

        })

        binding.backBtn.setOnClickListener {
            finish()
        }


    }

    fun getLanguagesList():ArrayList<Language>{
        var languageList=ArrayList<Language>()
        languageList.add(Language("English",getDrawable(R.drawable.english_flag),"en"))
        languageList.add(Language("Spanish",getDrawable(R.drawable.spain_flag),"es"))
        languageList.add(Language("French",getDrawable(R.drawable.france_flag),"fr"))
        languageList.add(Language("German",getDrawable(R.drawable.germany_flag),"de"))
        languageList.add(Language("Hindi",getDrawable(R.drawable.india_flag),"hi"))
        languageList.add(Language("Italian",getDrawable(R.drawable.italy_flag),"it"))
        languageList.add(Language("Arabic",getDrawable(R.drawable.saudi_arabia_flag),"ar"))
        languageList.add(Language("Russian",getDrawable(R.drawable.russian_flag),"ru"))
//        languageList.add(Language("Korean",getDrawable(R.drawable.korean_flag),"ar"))
//        languageList.add(Language("Urdu",getDrawable(R.drawable.urdu_flag),"ar"))
//        languageList.add(Language("Portugues",getDrawable(R.drawable.portugal_flag),"ar"))
        return languageList
    }

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("en")
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }



}

data class Language(val languageName:String?,val languageDrawable: Drawable?,val languageCode:String?)