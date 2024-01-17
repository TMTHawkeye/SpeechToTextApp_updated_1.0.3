package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.content.Context

import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities.ConversationEntity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.shareText
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.ConversationAdapterListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceTalkViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(
    var context: Context,
    conversationsList: List<ConversationEntity>?,
    private val mViewModel: VoiceTalkViewModel,
    private val listener: ConversationAdapterListener
) : RecyclerView.Adapter<ConversationAdapter.viewHolder>() {

    var list_of_conversations: List<ConversationEntity>? = conversationsList

    inner class viewHolder(var binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        binding.shareIcon.translationX = -binding.shareIcon.width.toFloat()

        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list_of_conversations?.size ?: 0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val message = list_of_conversations!![position]
        holder.binding.translatedTextFromId.text = message.translatedText
        holder.binding.speakTextFromId.text = message.text
//        holder.binding.currTimeAndDateId.text = formatTimestamp(message.timestamp)
        holder.binding.deleteIcon.visibility = View.GONE
        holder.binding.shareIcon.visibility = View.GONE

        val paramsText =
            LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        val paramsLayout = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

        if (message.source == 0) {
            paramsText.marginStart =
                context.resources.getDimensionPixelSize(R.dimen.margin_message_start)
            paramsText.marginEnd =
                context.resources.getDimensionPixelSize(R.dimen.margin_message_end)
            paramsLayout.marginStart =0
            paramsLayout.addRule(RelativeLayout.ALIGN_PARENT_START)

            holder.binding.linearConversation.setBackgroundResource(R.drawable.messenger_bg_left)
            holder.binding.translatedTextFromId.setTextColor(context.getColor(R.color.white))
            holder.binding.speakTextFromId.setTextColor(context.getColor(R.color.white))
//            holder.binding.currTimeAndDateId.setTextColor(context.getColor(R.color.white))
            holder.binding.deleteIcon.setColorFilter(context.getColor(R.color.white))
            holder.binding.shareIcon.setColorFilter(context.getColor(R.color.white))

//            // Set share icon to the right of linear_conversation
//            val paramsShareIcon =
//                holder.binding.shareIcon.layoutParams as RelativeLayout.LayoutParams
//            paramsShareIcon.addRule(RelativeLayout.END_OF, R.id.linear_conversation)
//            paramsShareIcon.addRule(RelativeLayout.CENTER_VERTICAL)
//            holder.binding.shareIcon.layoutParams = paramsShareIcon
        } else {
            paramsText.marginEnd =
                context.resources.getDimensionPixelSize(R.dimen.margin_message_start)
            paramsText.marginStart =
                context.resources.getDimensionPixelSize(R.dimen.margin_message_end)
            paramsLayout.marginEnd =0
            paramsLayout.marginStart = screenWidth / 4
            paramsLayout.addRule(RelativeLayout.ALIGN_PARENT_END)

            holder.binding.linearConversation.setBackgroundResource(R.drawable.messenger_bg_right)
            holder.binding.translatedTextFromId.setTextColor(context.getColor(R.color.green))
            holder.binding.speakTextFromId.setTextColor(context.getColor(R.color.green))
//            holder.binding.currTimeAndDateId.setTextColor(context.getColor(R.color.green))
            holder.binding.deleteIcon.setColorFilter(context.getColor(R.color.green))
            holder.binding.shareIcon.setColorFilter(context.getColor(R.color.green))

//            // Set share icon to the left of linear_conversation
//            val paramsShareIcon =
//                holder.binding.shareIcon.layoutParams as RelativeLayout.LayoutParams
//            paramsShareIcon.addRule(RelativeLayout.ALIGN_PARENT_START)
//            paramsShareIcon.addRule(RelativeLayout.CENTER_VERTICAL)
//            holder.binding.shareIcon.layoutParams = paramsShareIcon
        }


        holder.binding.translatedTextFromId.layoutParams = paramsText
        holder.binding.speakTextFromId.layoutParams = paramsText
        holder.binding.linearConversation.layoutParams = paramsLayout

        holder.binding.linearConversation.setOnLongClickListener {
            holder.binding.deleteIcon.visibility = View.VISIBLE
            holder.binding.shareIcon.visibility = View.VISIBLE
            holder.binding.deleteIcon.translationY = 0f
            holder.binding.shareIcon.translationY = 0f

            holder.binding.deleteIcon.animate()
                .translationY(10f)
                .setDuration(300)
                .start()

            holder.binding.shareIcon.animate()
                .translationY(10f)
                .setDuration(300)
                .start()
            true
        }

        holder.binding.linearConversation.setOnClickListener {
            holder.binding.deleteIcon.visibility = View.GONE
            holder.binding.shareIcon.visibility = View.GONE
            true
        }

        holder.binding.deleteIcon.setOnClickListener {
            listener.onDeleteItem(position)
        }

        holder.binding.shareIcon.setOnClickListener {
            var mergedText =
                holder.binding.speakTextFromId.text.toString() + " is translated to " + holder.binding.translatedTextFromId.text.toString()
            shareText(context,mergedText)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm a , dd/MM/yy", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }


    fun setConversation(conversations: List<ConversationEntity>) {
        list_of_conversations = conversations
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mViewModel.deleteConversation(list_of_conversations!!.get(position))
        list_of_conversations = list_of_conversations?.toMutableList()?.apply {
            removeAt(position)
        }
        notifyItemRemoved(position)
    }

}
