package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.nativead.NativeAd
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.LanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.SourceLanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdsInterCallBack
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.PreloadAdsUtils
//import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.copyToClipbard
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.extractLanguageFromCountry
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.getCountryName
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.loadLang
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.shareText
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.LanguageSelectionListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceSMSBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class VoiceSMSFragment : Fragment(), LanguageSelectionListener {

    lateinit var binding: FragmentVoiceSMSBinding
    var speechRecognitionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val speechResult: ArrayList<String>? =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.fromTextId.setText(
                    speechResult?.get(0)
                )
            }
        }
    val m_viewmodel: SMSViewModel by viewModel()
    lateinit var listLanguages: ArrayList<LanguageModel>
    lateinit var selectedCode: String
    lateinit var sourceselectedCode: String
    lateinit var selectedCountryName: String
    lateinit var sourceselectedCountryName: String
    private var speakText: TextToSpeech? = null
    private var isBottomSheetVisible = false
    private var isSourceBottomSheetVisible = false
    private var isRotationInProgress = false
    private val handler = Handler()
    var mExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    var mHandler = Handler(Looper.getMainLooper())
    var searchedText = ""
    lateinit var progressDialog: ProgressDialog


    private var currentNativeAd: NativeAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceSMSBinding.inflate(layoutInflater, container, false)
//        loadAd()
//        AdManager.getInstance()
//            .loadInterstitial(requireContext(), BuildConfig.Translate_Button_inter)
        splashNativeAd()
        PreloadAdsUtils.getInstance().loadIntersAlternate(requireContext(), BuildConfig.Translate_Button_inter, BuildConfig.Translate_Button_inter, 2)

        loadVoiceRecNative()
        loadVoiceSearchNative()
        initLanguages()

//        loadNativeAd()
//        AdManager.getInstance().loadNativeAd(
//            requireContext(),
//            BuildConfig.native_voice_SMS,
//            binding.adFrame,
//            binding.shimmerViewContainer
//        )

        speakText = TextToSpeech(requireContext(), OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = speakText!!.setLanguage(Locale("id", "ID"))
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e("TTS", "Language not supported")
                } else {
                    binding.speakerIdFrom.setEnabled(true)
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        })

        binding.speakIdFrom.setOnClickListener {
            m_viewmodel.getSpeech(sourceselectedCountryName) {
                speechRecognitionLauncher.launch(it)
            }
        }

        binding.reverseLangId.setOnClickListener {
            if (!isRotationInProgress) {
                performRotation()
            }

        }

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

        binding.translateFromTo.setOnClickListener {
            if (!binding.fromTextId.text.isNullOrEmpty() && !searchedText.equals(binding.fromTextId.text.toString())) {
//                getTranslation()
                searchedText = binding.fromTextId.text.toString()
                if (isInternetAvailable(requireContext())) {
//                    loadInterstitialTranslateBtn()
//                    progressDialog = ProgressDialog(requireContext())
//                    progressDialog.setMessage("Loading Interstitial...")
//                    progressDialog.setCancelable(false)
//                    if(AdManager.getInstance().interstitialAd!=null) {
//                        showLoadingDialog(progressDialog)
//                    }
//
//                    handler.postDelayed({
//                        AdManager.getInstance()
//                            .showInterstitial(
//                                requireActivity(),
//                                BuildConfig.Translate_Button_inter
//                            ) {
//                                dismissLoadingDialog(progressDialog)
//                                getTranslation()
//                            }
//                    }, 1000)

                    PreloadAdsUtils.getInstance().showInterAlternateByForce(
                        requireContext(),
                        MainApplication.getAdApplication().getStorageCommon().splashInterstitial,
                        false,
                        object : AdsInterCallBack {
                            override fun onInterstitialPriorityShowed() {

                            }

                            override fun onInterstitialNormalShowed() {
                            }

                            override fun onInterstitialShowed() {
                                Log.d("TAG_interst", "onInterstitialShowed: Ad showed")
                            }

                            override fun onAdClosed() {
                                Log.d("TAG_interst", "onAdClosed: Ad Closed")
                                getTranslation()
                                MainApplication.getAdApplication().getStorageCommon().splashInterstitial=null
                                PreloadAdsUtils.getInstance().loadIntersAlternate(requireContext(), BuildConfig.Translate_Button_inter, BuildConfig.Translate_Button_inter, 2)

                            }

                            override fun onAdClicked() {
                                Log.d("TAG_interst", "onAdClicked: Ad clicked")
                            }

                            override fun onNextAction() {
                            }
                        })
                }
            }
            hideKeyboard()
        }

        binding.copyIdFrom.setOnClickListener {
            val copied_text = binding.fromTextId.text.toString()
            copyToClipbard(requireContext(), copied_text)
        }

        binding.copyIdTo.setOnClickListener {
            val copied_text = binding.toTextId.text.toString()
            copyToClipbard(requireContext(), copied_text)
        }

        binding.deleteIdFrom.setOnClickListener {
            binding.fromTextId.text = null
//            binding.translateToLanguageId.visibility = View.GONE

            binding.toCard.visibility = View.GONE
        }

        binding.deleteIdTo.setOnClickListener {
            binding.toTextId.text = null
            binding.toSendId.setImageAlpha(128)

//            binding.translateToLanguageId.visibility = View.GONE

            binding.toCard.visibility = View.GONE

        }

        binding.speakerIdFrom.setOnClickListener {
            speak(binding.fromTextId.getText().toString())
        }

        binding.speakerIdTo.setOnClickListener {
            speak(binding.toTextId.getText().toString())
        }

        binding.sendId.setOnClickListener {
            if (!binding.sendId.imageAlpha.equals(128)) {
                shareText(requireContext(), binding.fromTextId.text.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.can_t_share_empty_text), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.toSendId.setOnClickListener {
            if (!binding.toSendId.imageAlpha.equals(128)) {

                shareText(requireContext(), binding.toTextId.text.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.can_t_share_empty_text),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.fromTextId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
//                binding.fromTextId.setSelection(binding.fromTextId.text.length)
                val alpha =
                    if (editable.length > 0) 255 else 128

                if (editable.length == 0) {
                    binding.toTextId.text = null
                    binding.lottieTranslateFromTo.cancelAnimation()


                } else {
                    binding.lottieTranslateFromTo.repeatCount = LottieDrawable.INFINITE
                    binding.lottieTranslateFromTo.playAnimation()
                }

                binding.sendId.setImageAlpha(alpha)
                binding.toSendId.setImageAlpha(alpha)

//                binding.translateFromTo.setImageAlpha(alpha)
            }
        })
        return binding.root
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
        val tempToTranslateToLanguage = binding.translateToLanguageId.text.toString()

        binding.toLanguage.text = binding.fromLanguage.text
        binding.toFlagId.text = binding.fromFlagId.text
        binding.translateToLanguageId.text = binding.translateFromLanguageId.text

        binding.fromLanguage.text = tempToLanguage
        binding.correspondingFromLanguageId.text = tempToLanguage
        binding.fromFlagId.text = tempToFlag
        binding.translateFromLanguageId.text = tempToTranslateToLanguage

        val tempSelectedCode = selectedCode
        val tempSelectedCountryName = selectedCountryName

        selectedCode = sourceselectedCode
        selectedCountryName = sourceselectedCountryName

        sourceselectedCode = tempSelectedCode
        sourceselectedCountryName = tempSelectedCountryName

        var tempEditText = binding.fromTextId.text.toString()
        binding.fromTextId.setText(binding.toTextId.text.toString())
        binding.toTextId.setText(tempEditText)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rootView = requireView().rootView

        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            private var isKeyboardVisible = false

            override fun onPreDraw(): Boolean {
                val insets = Rect()
                rootView.getWindowVisibleDisplayFrame(insets)

                val screenHeight = rootView.height
                val keypadHeight = screenHeight - insets.bottom

                val isKeyboardVisibleNow = keypadHeight > screenHeight * 0.15

                if (isKeyboardVisible != isKeyboardVisibleNow) {
                    isKeyboardVisible = isKeyboardVisibleNow
                    if (isKeyboardVisible) {
                        hideToolbarWithDelay()
                    } else {
                        if (isAdded) {
                            (requireActivity() as MainActivity).showCustomToolbar()
                        }
                    }
                }
                return true
            }
        })
    }

    private fun hideToolbarWithDelay() {
        if (isAdded) {
            (requireActivity() as MainActivity).hideCustomToolbar()
        }
    }



    private fun initLanguages() {
        try {
//            mExecutor.execute {
            listLanguages = loadLang()
//                mHandler.post {

//                    sourceselectedCountryName = Locale.getDefault().language
//                    sourceselectedCode = Locale.getDefault().country

            sourceselectedCountryName = "en"
            sourceselectedCode = "US"

            Log.d(
                "TAG",
                "initLanguages: $sourceselectedCode and $sourceselectedCountryName"
            )

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
                m_viewmodel.countryCodeToEmojiFlag(listLanguages.get(usEnglishPosition).countryCode)
            binding.toLanguage.text =
                extractLanguageFromCountry(listLanguages, pkUrduPosition)
            binding.toFlagId.text =
                m_viewmodel.countryCodeToEmojiFlag(listLanguages.get(pkUrduPosition).countryCode)
            binding.translateFromLanguageId.text =
                getString(R.string.translateFrom) + " " + listLanguages.get(
                    usEnglishPosition
                ).languageName
            binding.translateToLanguageId.text =
                getString(R.string.translateTo) + " " + listLanguages.get(pkUrduPosition).languageName

            selectedCode = listLanguages.get(pkUrduPosition).countryCode
            selectedCountryName = listLanguages.get(pkUrduPosition).languageCode

            binding.sendId.setImageAlpha(128)
//                    binding.translateFromTo.setImageAlpha(128)
            binding.toSendId.setImageAlpha(128)
//                }
//            }
        } catch (e: Exception) {
            Log.d("TAG", "initLanguages: Failed, ${e.message}")

            initLanguages()
        }
    }

    fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.fromTextId.windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }

    override fun onDestroy() {
        super.onDestroy()
//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        binding.root.clearFocus()
    }

    private fun speak(text: String) {
        if (!text.isEmpty()) {
            try {
                var pitch = 1.0f
                var speed = 1.0f
                if (pitch < 0.1) pitch = 0.1f
                if (speed < 0.1) speed = 0.1f
                speakText!!.setPitch(pitch)
                speakText!!.setSpeechRate(speed)
                speakText = TextToSpeech(requireContext(), object : TextToSpeech.OnInitListener {
                    override fun onInit(status: Int) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speakText!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            speakText!!.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            } catch (e: Exception) {
                Log.d("TAG", "speak: ${e.message}")
            }
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.can_t_speak_for_empty_text), Toast.LENGTH_SHORT
            )
                .show()
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
            binding.toFlagId.setText(m_viewmodel.countryCodeToEmojiFlag(selectedCode))
            binding.translateToLanguageId.text =
                getString(R.string.translateTo) + " " + selectedLanguage
//            binding.correspondingToLanguageId.text = selectedLanguage.split("(").first()
        } else {

            binding.fromLanguage.setText(selectedLanguage.split("(").first())
            this.sourceselectedCode = selectedCode
            this.sourceselectedCountryName = countryName
            binding.fromFlagId.setText(m_viewmodel.countryCodeToEmojiFlag(selectedCode))
            binding.translateFromLanguageId.text =
                getString(R.string.translateTo) + " " + selectedLanguage
            binding.correspondingFromLanguageId.text = selectedLanguage.split("(").first()
        }
    }

    override fun onDismisBottomSheet(isDismissed: Boolean) {
        if (isDismissed) {
            isBottomSheetVisible = false
            isSourceBottomSheetVisible = false
        }
    }

    fun getTranslation() {
        binding.toTextId.text = getString(R.string.translating)
        m_viewmodel.getTranslation(
            binding.fromTextId.text.toString(),
            sourceselectedCountryName,
            selectedCountryName
        ) {
            binding.toTextId.text = it
            binding.toSendId.setImageAlpha(255)
            binding.translateToLanguageId.visibility = View.VISIBLE
            binding.toCard.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        // Destroy the native ad to prevent memory leaks
        currentNativeAd?.destroy()
        handler.removeCallbacksAndMessages(null)

        super.onDestroyView()
    }


    private fun splashNativeAd(){
        MainApplication.getAdApplication()?.getStorageCommon()?.voiceSMSNative.let { appNative->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(requireContext(),
                    BuildConfig.native_voice_SMS,  org.smrtobjads.ads.R.layout.custom_native_admob_free_size, object :
                        AperoAdCallback(){
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(requireContext(), nativeAd, binding.adFrame, binding.splashNativeAd.shimmerContainerNative)
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.adFrame.visibility = View.GONE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.adFrame.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()

                        }
                    })
            }else{
                SmartAds.getInstance().populateNativeAdView(
                    requireContext(),
                    appNative.value,
                    binding.adFrame,
                    binding.splashNativeAd.shimmerContainerNative)
            }
        }

    }

    fun loadVoiceRecNative(nativeAdId: String = BuildConfig.native_voice_Rec) {
        if (MainApplication.getAdApplication().getStorageCommon()?.voiceRecNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                requireContext(),
                nativeAdId,
                org.smrtobjads.ads.R.layout.custom_native_admob_free_size_btn_bottom,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        MainApplication.getAdApplication()?.getStorageCommon()?.voiceRecNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        MainApplication.getAdApplication()?.getStorageCommon()?.voiceRecNative?.postValue(null)
                    }
                }
            )
        }
    }
    fun loadVoiceSearchNative(nativeAdId: String = BuildConfig.Voice_Search_Screen_Native) {
        if (MainApplication.getAdApplication().getStorageCommon()?.voiceSearchNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                requireContext(),
                nativeAdId,
                org.smrtobjads.ads.R.layout.custom_native_admob_free_size_btn_bottom,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        MainApplication.getAdApplication()?.getStorageCommon()?.voiceSearchNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        MainApplication.getAdApplication()?.getStorageCommon()?.voiceSearchNative?.postValue(null)
                    }
                }
            )
        }
    }

    fun loadInterstitialTranslateBtn() {
//        if (MainApplication.getAdApplication()
//                .getStorageCommon()?.splashInterstitial?.getValue() == null
//            && !AppPurchase.getInstance().isPurchased
//        ) {
        SmartAds.getInstance().loadSplashInterstitialAds(
            requireContext(),
            BuildConfig.Translate_Button_inter,
            2000,
            500,
            object : AperoAdCallback() {

                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
                    Log.d("TAG_interst", "onInterstitialLoad: adLoaded")
//                        MainApplication.getAdApplication()
//                            ?.getStorageCommon()?.splashInterstitial?.postValue(interstitialAd)
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    Log.d("TAG_interst", "onAdFailedToLoad: $adError")
                    getTranslation()
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                        MainApplication.getAdApplication()
//                            ?.getStorageCommon()?.splashInterstitial?.postValue(null)
                }

                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    Log.d("TAG_interst", "onAdFailedToLoad: $adError")
                    getTranslation()
//                    MainApplication.getAdApplication()
//                        ?.getStorageCommon()?.splashInterstitial?.postValue(null)
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d("TAG_interst", "onAdClosed: Ad Closed")
                    getTranslation()
                }
            })
    }



}