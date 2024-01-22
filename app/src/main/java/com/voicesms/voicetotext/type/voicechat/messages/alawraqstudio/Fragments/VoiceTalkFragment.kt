package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.ConversationAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.LanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.SourceLanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities.ConversationEntity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.extractLanguageFromCountry
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.getCountryName
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.ConversationAdapterListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.LanguageSelectionListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceTalkViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceTalkBinding
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class VoiceTalkFragment : Fragment(), LanguageSelectionListener, ConversationAdapterListener {
    lateinit var binding: FragmentVoiceTalkBinding
    var voice_source = ""
    lateinit var adapter: ConversationAdapter
    lateinit var selectedCode: String
    lateinit var sourceselectedCode: String
    lateinit var selectedCountryName: String
    lateinit var sourceselectedCountryName: String
    lateinit var listLanguages: ArrayList<LanguageModel>

    val m_model: SMSViewModel by viewModel()
    val m_viewmodel: SMSViewModel by viewModel()
    val talk_vmodel: VoiceTalkViewModel by viewModel()
    var isBottomSheetVisible = false
    var isSourceBottomSheetVisible = false
    var isRotationInProgress = false

    var mExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var mHandler = Handler(Looper.getMainLooper())

    private lateinit var adView: AdView
    private val adSize: AdSize
        get() {
            val display = requireActivity().windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                requireContext(),
                adWidth
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceTalkBinding.inflate(layoutInflater, container, false)

        adView = AdView(requireContext())

        initLanguages()
        binding.adViewContainer.visibility=View.VISIBLE
        binding.adViewContainer.addView(adView)
        loadBanner()
        //     AdManager.getInstance().loadBanner(requireContext(),BuildConfig.banner_voice_talk,adSize)


        binding.conversationRV.layoutManager = LinearLayoutManager(requireContext())
        adapter = ConversationAdapter(requireContext(), null, talk_vmodel, this)
        binding.conversationRV.adapter = adapter
        getListFromRoom()

//        if(isListAvailable){
//            binding.adViewContainer.visibility=View.VISIBLE
//            binding.adViewContainer.addView(adView)
//            binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//                if (!initialLayoutComplete.getAndSet(true)) {
//                    loadBanner()
////                                AdManager.getInstance().loadBanner(requireContext(),BuildConfig.banner_voice_talk,adSize)
//                }
//            }
//        }
//        else{
//            binding.adViewContainer.visibility=View.GONE
//        }


        binding.linearTo.setOnClickListener {
            if (!isBottomSheetVisible) {
                val bottomSheetFragment = LanguageBottomSheet(requireContext())
                bottomSheetFragment.setListener(this)
                bottomSheetFragment.show(childFragmentManager, "List_of_Languages")
                isBottomSheetVisible = true
            }
        }

        binding.linearFrom.setOnClickListener {
            if (!isSourceBottomSheetVisible) {
                val bottomSheetFragment = SourceLanguageBottomSheet(requireContext())
                bottomSheetFragment.setListener(this)
                bottomSheetFragment.show(childFragmentManager, "List_of_Languages")
                isSourceBottomSheetVisible = true
            }
        }

        binding.speakIdFrom.setOnClickListener {
//            val isDloaded=Paper.book().read<Boolean>("initialModel",false)
//            if(isDloaded!!) {
                m_model.getSpeech(sourceselectedCountryName) {
                    speechRecognitionLauncherFrom.launch(it)
                }
//            }
//            else{
//                Toast.makeText(requireContext(), "Model is being downloaded", Toast.LENGTH_SHORT).show()
//            }
        }

        binding.reverseLangId.setOnClickListener {
            if (!isRotationInProgress) {
                performRotation()
            }
        }

        binding.speakIdTo.setOnClickListener {
//            val isDloaded=Paper.book().read<Boolean>("initialModel",false)
//            if(isDloaded!!) {
                m_model.getSpeech(selectedCountryName) {
                    speechRecognitionLauncherTo.launch(it)
                }
//            }
//            else{
//                Toast.makeText(requireContext(), "Model is being downloaded", Toast.LENGTH_SHORT).show()
//            }
        }

        return binding.root
    }



    override fun onDestroy() {
        binding.adViewContainer.removeView(adView)
        // Destroy the AdView to release resources
        adView.destroy()
        super.onDestroy()

    }

    private fun performRotation() {
        val rotationAnimator = ObjectAnimator.ofFloat(binding.reverseLangId, "rotation", 0f, 360f)
        rotationAnimator.duration = 1000
        rotationAnimator.interpolator = LinearInterpolator()

        rotationAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isRotationInProgress = true
            }

            override fun onAnimationEnd(animation: Animator) {
                isRotationInProgress = false
                swapLanguages()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        rotationAnimator.start()
    }

    private fun swapLanguages() {
        val tempToLanguage = binding.toLanguage.text.toString()
        val tempToFlag = binding.toFlagId.text.toString()
        val tempToTranslateToLanguage = binding.correspondingToLanguageId.text.toString()

        binding.toLanguage.text = binding.fromLanguage.text
        binding.toFlagId.text = binding.fromFlagId.text
        binding.correspondingToLanguageId.text = binding.correspondingFromLanguageId.text

        binding.fromLanguage.text = tempToLanguage
        binding.fromFlagId.text = tempToFlag
        binding.correspondingFromLanguageId.text = tempToTranslateToLanguage

        val tempSelectedCode = selectedCode
        val tempSelectedCountryName = selectedCountryName

        selectedCode = sourceselectedCode
        selectedCountryName = sourceselectedCountryName

        sourceselectedCode = tempSelectedCode
        sourceselectedCountryName = tempSelectedCountryName
    }

    private fun getListFromRoom() {
        binding.conversationRV.visibility = View.VISIBLE
        binding.noConversationFoundId.visibility = View.GONE
        lifecycleScope.launch(Dispatchers.IO) {
            talk_vmodel.getAllConversations().collect { conversations: List<ConversationEntity> ->
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "getListFromRoom: ${conversations.size.toString()}")
                    adapter.setConversation(conversations)
                    if (conversations.isNotEmpty()) {
//                        binding.adViewContainer.visibility = View.VISIBLE
                        binding.conversationRV.scrollToPosition(conversations.size - 1)
//                        binding.adViewContainer.addView(adView)
                        binding.adViewContainer.visibility = View.VISIBLE

//                        isListAvailable = true

                    } else {
//                        isListAvailable = false
                        binding.conversationRV.visibility = View.GONE
                        binding.noConversationFoundId.visibility = View.VISIBLE
                        binding.adViewContainer.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onLanguageSelected(
        selectedLanguage: String,
        selectedCode: String,
        countryName: String,
        from: String
    ) {
        if (!from.equals("from_source")) {
            binding.toLanguage.setText(selectedLanguage.split("(").first())
            this.selectedCode = selectedCode
            this.selectedCountryName = countryName
            binding.toFlagId.text = m_viewmodel.countryCodeToEmojiFlag(selectedCode)
            binding.correspondingToLanguageId.text = selectedLanguage.split("(").first()
        } else {
            binding.fromLanguage.setText(selectedLanguage.split("(").first())
            this.sourceselectedCode = selectedCode
            this.sourceselectedCountryName = countryName
            binding.fromFlagId.text = m_viewmodel.countryCodeToEmojiFlag(selectedCode)
            binding.correspondingFromLanguageId.text = selectedLanguage.split("(").first()
        }
    }

    override fun onDismisBottomSheet(isDismissed: Boolean) {
        if (isDismissed) {
            isBottomSheetVisible = false
            isSourceBottomSheetVisible = false
        }
    }

    var speechRecognitionLauncherFrom =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val speechResult: ArrayList<String>? =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//                adapter.setConversation(speechResult?.get(0) ?: "", 0) // Assuming 0 for left side
                val usEnglishPosition =
                    getCountryName(listLanguages, sourceselectedCode, sourceselectedCountryName)
                m_viewmodel.getTranslation(
                    speechResult?.get(0) ?: "",
                    listLanguages.get(usEnglishPosition).languageCode,
                    selectedCountryName
                ) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        requireActivity().runOnUiThread {
                            binding.adViewContainer.visibility = View.VISIBLE
                            binding.conversationRV.visibility = View.VISIBLE
                            binding.noConversationFoundId.visibility = View.GONE
                        }
                        talk_vmodel.saveConversation(speechResult?.get(0) ?: "", it, 0)
                    }
                }
            }
        }

    var speechRecognitionLauncherTo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val speechResult: ArrayList<String>? =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//                adapter.setConversation(speechResult?.get(0) ?: "", 0) // Assuming 0 for left side
                val usEnglishPosition =
                    getCountryName(listLanguages, selectedCode, selectedCountryName)
                m_viewmodel.getTranslation(
                    speechResult?.get(0) ?: "",
                    listLanguages.get(usEnglishPosition).languageCode,
                    sourceselectedCountryName
                ) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        requireActivity().runOnUiThread {
                            binding.adViewContainer.visibility = View.VISIBLE
                            binding.conversationRV.visibility = View.VISIBLE
                            binding.noConversationFoundId.visibility = View.GONE
                        }
                        talk_vmodel.saveConversation(speechResult?.get(0) ?: "", it, 1)
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.error_listning_to_voice), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    private fun initLanguages() {
        try {
//            mExecutor.execute {
                listLanguages = m_viewmodel.getLanguages()
//                mHandler.post {
//                    sourceselectedCountryName = Locale.getDefault().language
//                    sourceselectedCode = Locale.getDefault().country

                    sourceselectedCountryName = "en"
                    sourceselectedCode = "US"

                    val usEnglishPosition =
                        getCountryName(
                            listLanguages,
                            "$sourceselectedCode",
                            "$sourceselectedCountryName"
                        )
                    val pkUrduPosition = getCountryName(listLanguages, "PK", "ur")

                    binding.fromLanguage.text =
                        extractLanguageFromCountry(listLanguages, usEnglishPosition)
                    binding.correspondingFromLanguageId.text =
                        extractLanguageFromCountry(listLanguages, usEnglishPosition)
                    binding.fromFlagId.text =
                        m_viewmodel.countryCodeToEmojiFlag(listLanguages[usEnglishPosition].countryCode)
                    binding.toLanguage.text =
                        extractLanguageFromCountry(listLanguages, pkUrduPosition)
                    binding.toFlagId.text =
                        m_viewmodel.countryCodeToEmojiFlag(listLanguages[pkUrduPosition].countryCode)

                    selectedCode = listLanguages[pkUrduPosition].countryCode
                    selectedCountryName = listLanguages[pkUrduPosition].languageCode

//        sourceselectedCode = listLanguages[usEnglishPosition].countryCode
//        sourceselectedCountryName = listLanguages[usEnglishPosition].languageName


                    binding.correspondingToLanguageId.text =
                        extractLanguageFromCountry(listLanguages, pkUrduPosition)
//                }
//            }
        } catch (e: Exception) {
            Log.d("TAG", "initLanguages Voice Talk Fragment: ${e.message}")
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.failed_to_load_languages_trying_again),
                Toast.LENGTH_SHORT
            ).show()
            initLanguages()
        }
    }

    override fun onDeleteItem(position: Int) {
        adapter.deleteItem(position)
    }

    override fun onResume() {
        super.onResume()
        binding.root.clearFocus()
//        binding.adViewContainer.visibility = View.VISIBLE

//        if (isListAvailable) {
//            binding.adViewContainer.visibility = View.VISIBLE
//            binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//                if (!initialLayoutComplete.getAndSet(true)) {
//                    loadBanner()
////                                AdManager.getInstance().loadBanner(requireContext(),BuildConfig.banner_voice_talk,adSize)
//                }
//            }
//        } else {
//            binding.adViewContainer.visibility = View.GONE
//        }
//        binding.adViewContainer.visibility=View.VISIBLE
//        binding.adViewContainer.addView(adView)
//        loadBanner()
    }

    private fun loadBanner() {
        adView.adUnitId = BuildConfig.banner_voice_talk
        adView.setAdSize(adSize)
        adView.background = requireContext().getDrawable(R.color.white)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}
