package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dloadLanguage
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySplashBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.NativeAdTemplateBinding
import kotlinx.coroutines.launch


class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    private val handler = Handler()


    private var currentNativeAd: NativeAd? = null
    private val delayedVisibilityChange = Runnable {
        binding.progressSplash.visibility = android.view.View.GONE
        binding.cardStart.visibility = android.view.View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        refreshAd()
        AdManager.getInstance().loadNativeAd(this@SplashActivity, BuildConfig.native_splash,binding.adFrame)

        downloadInitialModel()
        AdManager.getInstance().loadInterstitial(this, BuildConfig.welcome_Screen_inter)


        handler.postDelayed({
            binding.progressSplash.visibility = android.view.View.GONE
            binding.cardStart.visibility = android.view.View.VISIBLE
        }, 3000)

        binding.cardStart.setOnClickListener {
//            showInterstitial()
            AdManager.getInstance().loadInterstitial(this, BuildConfig.welcome_Screen_inter)
            AdManager.getInstance().showInterstitial(this,BuildConfig.welcome_Screen_inter) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }


        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun downloadInitialModel() {
        val sourceselectedCountryName = "ur"
//        val sourceselectedCode = "PK"

        val isModelDownloaded = getLanguageModelDownloadStatus(sourceselectedCountryName)

        if (!isModelDownloaded) {
            lifecycleScope.launch {
                dloadLanguage(sourceselectedCountryName) { success ->
                    if (success) {
                        setLanguageModelDownloadStatus(sourceselectedCountryName, true)
                        Log.d("TAG", "Initial Model Download: success")
                    } else {
                        Log.d("TAG", "Initial Model Download: failure")
                    }
                }
            }
        }


    }

    private fun getLanguageModelDownloadStatus(languageCode: String): Boolean {
        val preferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        return preferences.getBoolean("ModelDownloaded_$languageCode", false)
    }

    private fun setLanguageModelDownloadStatus(languageCode: String, status: Boolean) {
        val preferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("ModelDownloaded_$languageCode", status)
        editor.apply()
    }


    private fun refreshAd() {
        val builder = AdLoader.Builder(this, BuildConfig.native_splash)

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
            binding.adFrame.removeAllViews()
            binding.adFrame.addView(unifiedAdBinding.root)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(binding.adFrame.isClickable).build()
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


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(delayedVisibilityChange)
    }


}