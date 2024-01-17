package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.copyToClipbard
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySubCategorySearchBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.NativeAdTemplateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SubCategorySearchActivity : BaseActivity() {
    lateinit var binding: ActivitySubCategorySearchBinding
    var subcategoryName = ""
    val m_viewmodel: SMSViewModel by viewModel()

    var speechRecognitionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val speechResult: ArrayList<String>? =
                    result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.fromTextId.setText(
                    binding.fromTextId.text.toString() + speechResult?.get(0)
                )
            }
        }

    private var currentNativeAd: NativeAd? = null

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
        binding = ActivitySubCategorySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        refreshAd()

        AdManager.getInstance().loadNativeAd(
            this@SubCategorySearchActivity,
            BuildConfig.Sub_categories_native,
            binding.adViewContainer,
            binding.shimmerViewContainer
        )

        subcategoryName = intent.getStringExtra("subcategory")!!
        binding.subcategoryTitle.text = subcategoryName
        binding.searchId.setImageAlpha(128);

        binding.copyIdFrom.setOnClickListener {
            copyToClipbard(this, binding.fromTextId.text.toString())
        }

        binding.deleteIdFrom.setOnClickListener {
            binding.fromTextId.text = null
        }

        binding.speakIdFrom.setOnClickListener {
            m_viewmodel.getSpeech("en") {
                speechRecognitionLauncher.launch(it)
            }
        }

        binding.searchId.setOnClickListener {
            if (!binding.searchId.imageAlpha.equals(128)) {
                generateQuery(
                    subcategoryName,
                    binding.fromTextId.text.toString()
                )
            } else {
                Toast.makeText(
                    this@SubCategorySearchActivity,
                    "Can't search empty text!",
                    Toast.LENGTH_SHORT
                ).show()
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
                val alpha =
                    if (editable.length > 0) 255 else 128

                binding.searchId.setImageAlpha(alpha)
            }
        })

        binding.backBtn.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun refreshAd() {
        val builder = AdLoader.Builder(this, BuildConfig.Sub_categories_native)

        builder.forNativeAd { nativeAd ->
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = isDestroyed
            }
            if (activityDestroyed || isFinishing || isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            val unifiedAdBinding = NativeAdTemplateBinding.inflate(layoutInflater)
            populateNativeAdView(nativeAd, unifiedAdBinding)
            binding.adViewContainer.removeAllViews()
            binding.adViewContainer.addView(unifiedAdBinding.root)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(binding.adViewContainer.isClickable).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader =
            builder
                .withAdListener(
                    object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            val error =
                                """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
//                            Toast.makeText(
//                                this@SplashActivity,
//                                "Failed to load native ad with error $error",
//                                Toast.LENGTH_SHORT
//                            )
//                                .show()
                        }
                    }
                )
                .build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    private fun populateNativeAdView(nativeAd: NativeAd, unifiedAdBinding: NativeAdTemplateBinding) {
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

    private fun generateQuery(platform: String, query: String) {
        val url: String = when (platform) {
            getString(R.string.reddit) -> getString(R.string.reddit_baseurl) + Uri.encode(query)
            getString(R.string.quora) -> getString(R.string.quora_baseurl) + Uri.encode(query)
            getString(R.string.flipboard) -> getString(R.string.flipboard_baseurl) + Uri.encode(
                query
            )

            getString(R.string.msn) -> getString(R.string.msn_baseurl) + Uri.encode(query)

            getString(R.string.alibaba) -> getString(R.string.alibaba__baseurl) + Uri.encode(query)
            getString(R.string.amazon) -> getString(R.string.amazon_baseurl) + Uri.encode(query)
            getString(R.string.daraz) -> getString(R.string.daraz_baseurl) + Uri.encode(query)
            getString(R.string.olx) -> getString(R.string.olx_baseurl) + Uri.encode(query)
            getString(R.string.ebay) -> getString(R.string.ebay_baseurl) + Uri.encode(query)
            getString(R.string.aliexpress) -> getString(R.string.aliexpress_baseurl) + Uri.encode(
                query
            ) + ".html"

            getString(R.string.youtube) -> getString(R.string.youtube_baseurl) + Uri.encode(query)
            getString(R.string.insta) -> getString(R.string.insta_baseurl) + Uri.encode(query)
            getString(R.string.pintrest) -> getString(R.string.pintrest_baseurl) + Uri.encode(query)
            getString(R.string.facebook) -> getString(R.string.facebook_baseurl) + Uri.encode(query)
            getString(R.string.twitter) -> getString(R.string.twitter_baseurl) + Uri.encode(query)
            getString(R.string.tiktok) -> getString(R.string.tiktok_baseurl) + Uri.encode(query)

            getString(R.string.google) -> getString(R.string.google_baseurl) + Uri.encode(query)
            getString(R.string.bing) -> getString(R.string.bing_baseurl) + Uri.encode(query)
            getString(R.string.duckgo) -> getString(R.string.duckgo_baseurl) + Uri.encode(query)
            getString(R.string.yahoo) -> getString(R.string.yahoo_baseurl) + Uri.encode(query)
            getString(R.string.wikipedia) -> getString(R.string.wikipedia_baseurl) + Uri.encode(
                query
            )

            getString(R.string.playstore) -> getString(R.string.playstore_baseurl) + Uri.encode(
                query
            )

            getString(R.string.weather) -> getString(R.string.weather_baseurl) + Uri.encode(query)
            getString(R.string.location) -> getString(R.string.location_baseurl) + Uri.encode(query)
            getString(R.string.imdb) -> getString(R.string.imdb_baseurl) + Uri.encode(query)

            else ->
                getString(R.string.google_baseurl) + Uri.encode(query)
        }
//        callback(url)

        val chromePackageName = "com.android.chrome"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.`package` = chromePackageName
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
    }

}