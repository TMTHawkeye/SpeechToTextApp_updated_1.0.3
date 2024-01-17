package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.LanguageListAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.LanguageSelectionListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentLanguageBottomSheetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SourceLanguageBottomSheet(var ctxt: Context) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentLanguageBottomSheetBinding

    lateinit var languageSelectionListener: LanguageSelectionListener
    lateinit var adapter: LanguageListAdapter
    val viewModel: SMSViewModel by viewModel()

    var mExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var mHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            getDownloadedmodels()
        }
        mExecutor.execute {
            val langList = viewModel.getLanguages()
            mHandler.post {
                adapter = LanguageListAdapter(ctxt, langList, null, viewModel)
                recyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : LanguageListAdapter.OnItemClickListener {
                    override fun onItemClick(
                        selectedLanguage: String?,
                        selectedCode: String?,
                        countryName: String?
                    ) {
                        languageSelectionListener.onLanguageSelected(
                            selectedLanguage!!,
                            selectedCode!!,
                            countryName!!,
                            "from_source"
                        )
                        dismiss()
                    }
                })

                binding.editTextSearch.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        adapter.filterList(s.toString())
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // Not needed
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        // Not needed
                    }
                })
            }
        }

    }

    // Set the listener for the hosting fragment/activity
    fun setListener(listener: LanguageSelectionListener) {
        this.languageSelectionListener = listener
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        languageSelectionListener.onDismisBottomSheet(true)
    }

    override fun onStart() {
        super.onStart()
        hideNavBar()
    }

    override fun onDestroy() {
        super.onDestroy()

        hideNavBar()
    }

    fun hideNavBar() {
//        window.decorView.postDelayed({

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        val insetsController = WindowCompat.getInsetsController(requireActivity().window, requireActivity().window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.statusBars())
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())

//        }, 300)
    }

    fun getDownloadedmodels() {
        // Get an instance of RemoteModelManager
        val modelManager = RemoteModelManager.getInstance()

        // Get downloaded models of type TranslateRemoteModel
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                var languageModels = ArrayList<String>()
                models.forEach {
                    languageModels.add(it.language)
                }
                adapter.downloadedList = languageModels
                adapter.notifyDataSetChanged()
                // Log the size of the downloaded models list
                Log.d("TAG", "downloadModelLanguage: ${models.size}")
            }
            .addOnFailureListener {
                // Handle download failure here (add logging or error handling)
                Log.e("TAG", "Failed to download models", it)
            }
    }


}