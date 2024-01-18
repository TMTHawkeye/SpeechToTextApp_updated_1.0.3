package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.RecordingsCallback
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.FileData
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ItemSavedRecordingsBinding
import java.io.File


class SavedFilesAdapter(
    var context: Context,
    var listofFiles: ArrayList<FileData>,
    var listner: RecordingsCallback
) :
    RecyclerView.Adapter<SavedFilesAdapter.viewHolder>() {
    lateinit var binding: ItemSavedRecordingsBinding
    var selectedItems: MutableSet<Int> = mutableSetOf()
    var isSelectionMode = false

    inner class viewHolder(var binding: ItemSavedRecordingsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemSavedRecordingsBinding.inflate(LayoutInflater.from(parent.context))
//        val v=LayoutInflater.from(parent.context).inflate(R.layout.item_saved_recordings,parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listofFiles.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var file = File(listofFiles.get(position).filePath)
        holder.binding.recNameId.text = file.name
        holder.binding.recSizeId.text = listofFiles.get(position).fileSize
        holder.binding.timeOfStoreId.text = listofFiles.get(position).timeAgo

        if (selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.light_green
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        }

        holder.binding.sendRecId.setOnClickListener {
            if(!isSelectionMode) {
//                StrictMode.setVmPolicy(
//                    StrictMode.VmPolicy.Builder()
//                        .penaltyLog()
//                        .build()
//                )

                val uri = Uri.fromFile(File(listofFiles.get(position).filePath))
//                val intent = Intent(Intent.ACTION_SEND).apply {
//                    type = "*/*"
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                }

                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    File(listofFiles.get(position).filePath)
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "*/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(Intent.createChooser(intent, "Share MP3 file"))
            }
        }

        holder.itemView.setOnLongClickListener {
            isSelectionMode=true
            if (!selectedItems.contains(position)) {
                selectedItems.add(position)
                listner.selectedRecordings()
            }
            notifyItemChanged(position)
            true
        }

        holder.itemView.setOnClickListener {
            if(isSelectionMode){
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position)
                    if(selectedItems.size==0){
                        isSelectionMode=false
                    }
                } else {
                    selectedItems.add(position)
                }
                listner.selectedRecordings()
                notifyItemChanged(position)
            }
        }

        holder.binding.playRecId.setOnClickListener {
            val file = File(listofFiles[position].filePath)
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.applicationContext.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "audio/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Cant' play the audio!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}


