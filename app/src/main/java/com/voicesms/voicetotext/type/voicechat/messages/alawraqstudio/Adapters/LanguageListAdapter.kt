package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dloadLanguage
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemLanguageBinding
import io.paperdb.Paper

class LanguageListAdapter(
    val context: Context,
    val listLanguage: ArrayList<LanguageModel>?,
    var downloadedList: ArrayList<String>?,
    val viewModel: SMSViewModel
) :
    RecyclerView.Adapter<LanguageListAdapter.viewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    private var filteredList: ArrayList<LanguageModel> = ArrayList()
    lateinit var binding: ItemLanguageBinding

    interface OnItemClickListener {
        fun onItemClick(selectedLanguage: String?, selectedCode: String?, countryName: String?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    fun filterList(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(listLanguage!!)
        } else {
            val lowerCaseQuery = query.lowercase()

            for (language in listLanguage!!) {
                if (language.languageName.lowercase().contains(lowerCaseQuery)) {
                    filteredList.add(language)
                }
            }
        }

        notifyDataSetChanged()
    }


    inner class viewHolder(var binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filteredList.addAll(listLanguage!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context))
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val languageCode = filteredList[position].languageCode
        val language = "${filteredList[position].languageName}"
        val downloadState = Paper.book().read<Boolean>("${languageCode}_download_state")

        if (downloadState != null && downloadState) {
            Log.d("TAG", "dloadstate")
            holder.binding.dloadTxtview.visibility = View.GONE
            holder.binding.progressDloadLang.visibility = View.VISIBLE
            holder.binding.dloadLanguageTickIcon.visibility = View.GONE
        } else {
            if (downloadedList?.contains(languageCode) == true) {
                Log.d("TAG", "not dloadstate")
                holder.binding.dloadTxtview.visibility = View.GONE
                holder.binding.progressDloadLang.visibility = View.GONE
                holder.binding.dloadLanguageTickIcon.visibility = View.VISIBLE
            } else {
                holder.binding.dloadTxtview.visibility = View.VISIBLE
                holder.binding.progressDloadLang.visibility = View.GONE
                holder.binding.dloadLanguageTickIcon.visibility = View.GONE
            }
        }

        holder.binding.textLanguage.setText(language)
        holder.binding.flagFromEmoji.setText(
            viewModel.countryCodeToEmojiFlag(
                filteredList.get(
                    position
                ).countryCode
            )
        )
        holder.binding.dloadTxtview.setOnClickListener {
            holder.binding.dloadTxtview.visibility = View.GONE
            holder.binding.progressDloadLang.visibility = View.VISIBLE
            Paper.book().write("${languageCode}_download_state", true)

            if (!holder.binding.dloadTxtview.text.equals(context.getString(R.string.unavailable))) {
                dloadLanguage(listLanguage?.get(position)?.languageCode!!) {
                    Paper.book().write("${languageCode}_download_state", false)

                    if (it) {
                        Toast.makeText(
                            context,
                            "${filteredList.get(position).languageName} downloaded!",
                            Toast.LENGTH_SHORT
                        ).show()
                        holder.binding.progressDloadLang.visibility = View.GONE
                        holder.binding.dloadLanguageTickIcon.visibility = View.VISIBLE


                    } else {
                        holder.binding.progressDloadLang.visibility = View.GONE
                        holder.binding.dloadTxtview.visibility = View.VISIBLE
                        holder.binding.dloadTxtview.text =
                            context.getString(R.string.unavailable)
                        Toast.makeText(context, "Language unavailble!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }


        }

        holder.itemView.setOnClickListener {
            Log.d(
                "clicked language pos",
                "position: $position of ${filteredList.get(position).languageName}"
            )

            if (holder.binding.dloadLanguageTickIcon.visibility == View.VISIBLE) {
                onItemClickListener?.onItemClick(
                    language,
                    filteredList.get(position).countryCode,
                    filteredList.get(position).languageCode
                )
            } else {
                Toast.makeText(context, "Please download the language first!", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }


}