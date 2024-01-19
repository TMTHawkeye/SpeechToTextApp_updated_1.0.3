package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import Language
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.BaseActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.GuideActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.LangungeActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.SplashActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemCategoryBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import io.paperdb.Paper
import java.util.Locale

class LanguageAdapter(val ctxt: Context, val languagesList: ArrayList<Language>,var selectedPosition:Int?) :
    RecyclerView.Adapter<LanguageAdapter.viewHolder>() {
    var languageCode="en"
    var savedPosition=selectedPosition

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
            setSelectedPosition(position)

        }

//        holder.itemView.setBackgroundResource(
//            if (position == selectedPosition){
//                R.drawable.selected_language_bg
//            } else 0
//        )

        if(position==selectedPosition){
            holder.itemView.setBackgroundResource(
                R.drawable.selected_card_bg
            )
        }
        else{
            holder.itemView.setBackgroundResource(
                R.drawable.card_bg
            )
        }

    }

    private fun setSelectedPosition(position: Int) {
        selectedPosition = position
        Paper.book().write<Int?>("LANG_POS",position)
        notifyDataSetChanged()
    }

    fun changeLanguage(languageCode: String) {
       this.languageCode=languageCode
    }


}