package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.SavedFilesAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.RecordingsCallback
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.FileData
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.RecordingViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySavedFilesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SavedFilesActivity : BaseActivity(), RecordingsCallback {
    lateinit var binding: ActivitySavedFilesBinding
    val m_viewmodel: RecordingViewModel by viewModel()
    val TAG = "Files Size Tag"
    lateinit var adapter: SavedFilesAdapter
    lateinit var listofFiles: ArrayList<FileData>

    override fun onPause() {
        super.onPause()
        AdManager.getInstance().currentNativeAd=null
        AdManager.getInstance().interstitialAd=null
    }
    override fun onDestroy() {
        super.onDestroy()
        AdManager.getInstance().currentNativeAd=null
        AdManager.getInstance().interstitialAd=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val folderPath = File(getExternalFilesDir("recordings"), "")
        m_viewmodel.getListofFilesFromStorage(folderPath.absolutePath) {
            Log.d(TAG, "size of saved file list: ${it?.size}")
            if (it!!.size != 0) {
                AdManager.getInstance().loadNativeAd(
                    this@SavedFilesActivity,
                    BuildConfig.Save_file_Screen_Native,
                    binding.adViewContainer,
                    binding.shimmerViewContainer
                )
                binding.adViewContainer.visibility=View.VISIBLE
                binding.shimmerViewContainer.visibility=View.VISIBLE
                listofFiles = it
                binding.noItemId.visibility = View.GONE
                binding.savedFilesRV.visibility = View.VISIBLE
                binding.savedFilesRV.layoutManager = LinearLayoutManager(this)
                adapter = SavedFilesAdapter(this@SavedFilesActivity, it, this)
                binding.savedFilesRV.adapter = adapter
            } else {
                showNoItem()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.selectAllId.setOnClickListener {
            if (adapter.selectedItems.size < listofFiles.size) {
                binding.selectAllId.setImageDrawable(getDrawable(R.drawable.all_selected_icon))
//                adapter.selectedItems.clear()
                for (i in 0 until adapter.itemCount) {
                    adapter.selectedItems.add(i)
                }
                adapter.isSelectionMode=true
                binding.constrainToolbar.visibility = View.INVISIBLE
                binding.constrainToolbarSelected.visibility = View.VISIBLE
                binding.noOfSelectedItemId.text =
                    adapter.selectedItems.size.toString() + " " + getString(R.string.selectedItems)

            } else {
                binding.selectAllId.setImageDrawable(getDrawable(R.drawable.select_icon))
                adapter.selectedItems.clear()
                adapter.isSelectionMode=false
                binding.constrainToolbar.visibility = View.VISIBLE
                binding.constrainToolbarSelected.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()
        }

        binding.shareFileId.setOnClickListener {
            val selectedFiles = adapter.selectedItems.map { listofFiles[it] }
            if (selectedFiles.isNotEmpty()) {
                shareMP3Files(selectedFiles)
            }
        }

        binding.deleteFileId.setOnClickListener {
            val selectedFiles = adapter.selectedItems.map { listofFiles[it] }
            if (selectedFiles.isNotEmpty()) {
                showDeleteConfirmationDialog(selectedFiles)
            }
        }

        binding.cancelBtn.setOnClickListener {
            val selectedFiles = adapter.selectedItems.map { listofFiles[it] }
            if (selectedFiles.isNotEmpty()) {
                resetRecyclerView()
            }
        }
    }
    private fun showDeleteConfirmationDialog(selectedFiles: List<FileData>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_files))
        builder.setMessage(getString(R.string.delete_dialog_text))

        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            deleteFiles(selectedFiles)
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            resetRecyclerView()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun resetRecyclerView() {
        binding.selectAllId.setImageDrawable(getDrawable(R.drawable.select_icon))
        adapter.selectedItems.clear()
        adapter.isSelectionMode = false
        binding.constrainToolbar.visibility = View.VISIBLE
        binding.constrainToolbarSelected.visibility = View.INVISIBLE
        adapter.notifyDataSetChanged()
    }
    private fun deleteFiles(files: List<FileData>) {
        for (fileData in files) {
            val file = File(fileData.filePath)
            if (file.exists()) {
                file.delete()
                listofFiles.remove(fileData)
                adapter.selectedItems.clear()
                binding.constrainToolbar.visibility = View.VISIBLE
                binding.constrainToolbarSelected.visibility = View.INVISIBLE
                adapter.notifyDataSetChanged()
            }
        }
        if (listofFiles.size == 0) {
            showNoItem()
        }
        Toast.makeText(this@SavedFilesActivity,
            getString(R.string.file_deleted_successfully), Toast.LENGTH_SHORT)
            .show()
    }
    private fun shareMP3Files(files: List<FileData>) {
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .build()
        )

        val mp3Files = files.filter { it.filePath.endsWith(".mp3", ignoreCase = true) }

        if (mp3Files.isNotEmpty()) {
            val uris = mp3Files.map {
                FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    File(it.filePath)
                )
            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "audio/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, getString(R.string.share_mp3_files)))
        }
    }
    private fun showNoItem() {
        binding.adViewContainer.visibility=View.GONE
        binding.shimmerViewContainer.visibility=View.GONE
        binding.savedFilesRV.visibility = View.INVISIBLE
        binding.selectAllId.visibility = View.INVISIBLE
        binding.noItemId.visibility = View.VISIBLE
    }

    override fun selectedRecordings() {
        if (adapter.isSelectionMode) {
            binding.constrainToolbar.visibility = View.INVISIBLE
            binding.constrainToolbarSelected.visibility = View.VISIBLE
            binding.noOfSelectedItemId.text =
                adapter.selectedItems.size.toString() + " " + getString(R.string.selectedItems)
            if (adapter.selectedItems.size.equals(listofFiles.size)) {
                binding.selectAllId.setImageDrawable(getDrawable(R.drawable.all_selected_icon))
            }
            else{
                binding.selectAllId.setImageDrawable(getDrawable(R.drawable.select_icon))
            }
        } else {
            binding.constrainToolbar.visibility = View.VISIBLE
            binding.constrainToolbarSelected.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
    }

}