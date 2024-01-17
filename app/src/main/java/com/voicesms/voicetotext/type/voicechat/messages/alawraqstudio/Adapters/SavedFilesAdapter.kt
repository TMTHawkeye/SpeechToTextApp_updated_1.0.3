package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                        .penaltyLog()
                        .build()
                )

                val uri = Uri.fromFile(File(listofFiles.get(position).filePath))
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "*/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                context.startActivity(Intent.createChooser(intent, "Share files"))
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

//        holder.itemView.setOnLongClickListener {
//            isSelectionMode = true
////            selectedItems.clear()
//            selectedItems.add(position)
//            notifyItemChanged(position)
//            true
//        }
//
//        holder.itemView.setOnClickListener {
//            if (isSelectionMode) {
//                // Handle click in selection mode
//                if (selectedItems.contains(position)) {
//                    selectedItems.remove(position)
//                } else {
//                    selectedItems.add(position)
//                }
//                notifyItemChanged(position)
//            }
//            else{
//                Toast.makeText(context, "Item clicked!", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}



//private fun openFile(file: File, context: Context) {
////    val uri = Uri.fromFile(file)
//    val uri = FileProvider.getUriForFile(
//        context,
//        "androidx.core.content.FileProvider",
//        file
//    )
//
//
//
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.setDataAndType(uri, "audio/mp3")
////    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
//
//    val authority = context.packageName + ".provider"
//    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    FileProvider.getUriForFile(context, authority, file)
//
//    try {
//        context.startActivity(intent)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//}


