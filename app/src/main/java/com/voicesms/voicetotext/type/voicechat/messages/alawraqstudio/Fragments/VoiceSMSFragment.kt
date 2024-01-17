package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.animation.Animator
import android.animation.ObjectAnimator
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.LanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BottomSheets.SourceLanguageBottomSheet
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.copyToClipbard
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.extractLanguageFromCountry
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.getCountryName
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.shareText
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.LanguageSelectionListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.LanguageModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceSMSBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.NativeAdTemplateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
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


    private var currentNativeAd: NativeAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceSMSBinding.inflate(layoutInflater, container, false)
//        loadAd()
        AdManager.getInstance().loadInterstitial(requireContext(), BuildConfig.interstitial_home)

        initLanguages()

//        loadNativeAd()
        AdManager.getInstance().loadNativeAd(requireContext(), BuildConfig.native_voice_SMS,binding.adFrame)

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
            if (!binding.fromTextId.text.isNullOrEmpty()) {
                getTranslation()
                AdManager.getInstance().showInterstitial(requireActivity(),BuildConfig.interstitial_home) {
                    getTranslation()
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
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.can_t_share_empty_text), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.toSendId.setOnClickListener {
            if (!binding.toSendId.imageAlpha.equals(128)) {

                shareText(requireContext(), binding.toTextId.text.toString())
            } else {
                Toast.makeText(requireContext(), requireContext().getString(R.string.can_t_share_empty_text), Toast.LENGTH_SHORT)
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
                    binding.translateFromTo.visibility = View.GONE
                    binding.lottieTranslateFromTo.cancelAnimation()


                } else {
                    binding.translateFromTo.visibility = View.VISIBLE
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
            mExecutor.execute {
                listLanguages = m_viewmodel.getLanguages()
                mHandler.post {

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
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "initLanguages: Failed, ${e.message}")
            Toast.makeText(
                requireContext(),
                getString(R.string.failed_to_load_languages_trying_again),
                Toast.LENGTH_SHORT
            ).show()
            initLanguages()
        }
    }

    fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.fromTextId.windowToken, 0)
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
            Toast.makeText(requireContext(),
                requireContext().getString(R.string.can_t_speak_for_empty_text), Toast.LENGTH_SHORT)
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

    private fun loadNativeAd() {
        val builder = AdLoader.Builder(requireContext(), BuildConfig.native_voice_SMS)

        builder.forNativeAd { nativeAd ->
            // Check if the fragment is still active
            if (isAdded) {
                // You can handle the loaded native ad here
                handleNativeAd(nativeAd)
            } else {
                // The fragment is no longer active, destroy the native ad
                nativeAd.destroy()
            }
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad loading failure
                    Log.e("TAG", "Failed to load native ad with error: $loadAdError")
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun handleNativeAd(nativeAd: NativeAd) {
        // Destroy the previous native ad, if any
        currentNativeAd?.destroy()

        // Save the new native ad
        currentNativeAd = nativeAd

        // Inflate and populate the native ad view
        val unifiedAdBinding = NativeAdTemplateBinding.inflate(layoutInflater)
        populateNativeAdView(nativeAd, unifiedAdBinding)

        // Add the native ad view to your layout
        binding.adFrame.addView(unifiedAdBinding.root)


//        val unifiedAdBinding = NativeAdTemplateBinding.inflate(layoutInflater)
//        populateNativeAdView(nativeAd, unifiedAdBinding)
//        binding.adFrame.removeAllViews()
//        binding.adFrame.addView(unifiedAdBinding.root)
    }

    private fun populateNativeAdView(
        nativeAd: NativeAd,
        unifiedAdBinding: NativeAdTemplateBinding
    ) {
        val nativeAdView = unifiedAdBinding.root

        // Set the media view.
        nativeAdView.mediaView = unifiedAdBinding.adMedia

        // Set other ad assets.
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
//        nativeAdView.priceView = unifiedAdBinding.adPrice
//        nativeAdView.starRatingView = unifiedAdBinding.adStars
//        nativeAdView.storeView = unifiedAdBinding.adStore
        nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        unifiedAdBinding.adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.setMediaContent(it) }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            unifiedAdBinding.adBody.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            unifiedAdBinding.adAppIcon.visibility = View.GONE
        } else {
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

//        if (nativeAd.price == null) {
////            unifiedAdBinding.adPrice.visibility = View.INVISIBLE
//        } else {
////            unifiedAdBinding.adPrice.visibility = View.VISIBLE
////            unifiedAdBinding.adPrice.text = nativeAd.price
//        }

//        if (nativeAd.store == null) {
//            unifiedAdBinding.adStore.visibility = View.INVISIBLE
//        } else {
//            unifiedAdBinding.adStore.visibility = View.VISIBLE
//            unifiedAdBinding.adStore.text = nativeAd.store
//        }

//        if (nativeAd.starRating == null) {
//            unifiedAdBinding.adStars.visibility = View.INVISIBLE
//        } else {
//            unifiedAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
//            unifiedAdBinding.adStars.visibility = View.VISIBLE
//        }

        if (nativeAd.advertiser == null) {
            unifiedAdBinding.adAdvertiser.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adAdvertiser.text = nativeAd.advertiser
            unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(nativeAd)


    }


    override fun onDestroyView() {
        // Destroy the native ad to prevent memory leaks
        currentNativeAd?.destroy()
        handler.removeCallbacksAndMessages(null)

        super.onDestroyView()
    }


}