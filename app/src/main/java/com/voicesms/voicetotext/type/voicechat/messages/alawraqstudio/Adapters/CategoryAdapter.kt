package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.SubCategorySearchActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.CategoryModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemCategoryBinding

class CategoryAdapter(var context: Context, var categoryList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.viewHolder>() {
    lateinit var binding: ItemCategoryBinding
    lateinit var progressDialog: ProgressDialog

    inner class viewHolder(var binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        binding.categoryIconId.setImageDrawable(context.getDrawable(categoryList.get(position).categoryIcon))
        binding.categoryNameId.text = categoryList.get(position).categoryName

        holder.itemView.setOnClickListener {
            holder.itemView.background=context.getDrawable(R.drawable.card_bg_selected)
            val delayMillis = 500L
            holder.binding.categoryIconId.setColorFilter(R.color.green)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                holder.binding.categoryIconId.clearColorFilter()
                holder.itemView.background = context.getDrawable(R.drawable.card_bg)
            }, delayMillis)

            context.startActivity(
                Intent(
                    context,
                    SubCategorySearchActivity::class.java
                ).putExtra("subcategory", categoryList.get(position).categoryName)
            )
        }

    }
}