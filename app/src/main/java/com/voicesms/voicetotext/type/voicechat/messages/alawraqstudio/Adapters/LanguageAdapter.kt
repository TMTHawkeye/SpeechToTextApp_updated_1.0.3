package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.GuideActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.Language
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.LangungeActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemCategoryBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import java.util.Locale

class LanguageAdapter(val ctxt: Context, val languagesList: ArrayList<Language>) : RecyclerView.Adapter<LanguageAdapter.viewHolder>() {

    lateinit var binding:ItemCategoryBinding

    inner class viewHolder(var binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languagesList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.categoryNameId.text=languagesList.get(position).languageName
        holder.binding.categoryIconId.setImageDrawable(languagesList.get(position).languageDrawable)

        holder.itemView.setOnClickListener {
//            ctxt.startActivity(Intent(ctxt,GuideActivity::class.java))
            changeLanguage(languagesList.get(position).languageCode!!)

        }
    }

    fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        LocaleHelper.setLocale(ctxt, locale)

        val intent = Intent(ctxt, GuideActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        ctxt.startActivity(intent)

    }
}