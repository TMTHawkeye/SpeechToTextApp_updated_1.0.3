package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import Language
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.BaseActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.GuideActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.LangungeActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.SplashActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemCategoryBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import java.util.Locale

class LanguageAdapter(val ctxt: Context, val languagesList: ArrayList<Language>) :
    RecyclerView.Adapter<LanguageAdapter.viewHolder>() {

    lateinit var binding: ItemCategoryBinding

    inner class viewHolder(var binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languagesList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.categoryNameId.text = languagesList.get(position).languageName
        holder.binding.categoryIconId.setImageDrawable(languagesList.get(position).languageDrawable)

        holder.itemView.setOnClickListener {
//            ctxt.startActivity(Intent(ctxt,GuideActivity::class.java))
            changeLanguage(languagesList.get(position).languageCode!!)

        }
    }

    fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration:Configuration=ctxt.resources.configuration
        configuration.locale=locale
        configuration.setLayoutDirection(locale)
       val lang=ctxt as BaseActivity
        lang.localeDelegate.setLocale(ctxt,locale)
        LocaleHelper.setLocale(ctxt, locale)
        BaseActivity().updateLocale(ctxt as LangungeActivity, locale)

//        val intent = Intent(ctxt, GuideActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        ctxt.startActivity(intent)

        val intent = if (isFirstTimeLaunch()) {
            Intent(ctxt, GuideActivity::class.java)
        } else {
            Intent(ctxt, MainActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        ctxt.startActivity(intent)


    }

    private fun isFirstTimeLaunch(): Boolean {
        val preferences = ctxt.getSharedPreferences("LangPrefs", Context.MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            val editor = preferences.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
        }
        return isFirstTime
    }
}