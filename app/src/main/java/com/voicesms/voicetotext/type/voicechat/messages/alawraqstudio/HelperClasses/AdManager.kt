package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.NativeAdTemplateBinding
import java.util.Date

class AdManager private constructor() {

     var interstitialAd: InterstitialAd? = null
     var isInterstitialShowing: Boolean = false
     var currentNativeAd: NativeAd? = null
//     var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    var adView: AdView?=null

    companion object {
        private var INSTANCE: AdManager? = null

        fun getInstance(): AdManager {
            if (INSTANCE == null) {
                INSTANCE = AdManager()
            }
            return INSTANCE!!
        }
    }

    fun loadInterstitial(context: Context, adUnitId: String) {
        if (adIsLoading || interstitialAd != null) {
            return
        }
        adIsLoading = true

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("InterstitialAdManager", adError.message)
                    interstitialAd = null
                    adIsLoading = false
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("InterstitialAdManager", "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, adUnitId: String, onAdDismissed: (boolean:Boolean) -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("InterstitialAdManager", "Ad was dismissed.")
                        interstitialAd = null
                        isInterstitialShowing=false
                        onAdDismissed.invoke(true)

                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d("InterstitialAdManager", "Ad failed to show.")
                        interstitialAd = null
                        isInterstitialShowing=false

                        onAdDismissed.invoke(true)

                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("InterstitialAdManager", "Ad showed fullscreen content.")
                        interstitialAd = null
                        isInterstitialShowing=true
                    }
                }
            interstitialAd?.show(activity)
        } else {
            loadInterstitial(activity, adUnitId)
        }
    }

     fun loadNativeAd(
         context: Context,
         adUnitId: String,
         adFrame: FrameLayout,
         shimmerViewContainer: ShimmerFrameLayout
     ) {
        val builder = AdLoader.Builder(context,adUnitId)

        builder.forNativeAd { nativeAd ->
                handleNativeAd(context,nativeAd,adFrame,shimmerViewContainer)
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad loading failure
                    Log.e("TAG", "Failed to load native ad with error: $loadAdError")
                    shimmerViewContainer.stopShimmer()
                    shimmerViewContainer.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun handleNativeAd(context: Context, nativeAd: NativeAd, adFrame: FrameLayout, shimmerView: ShimmerFrameLayout) {
        // Destroy the previous native ad, if any
        currentNativeAd?.destroy()

        // Save the new native ad
        currentNativeAd = nativeAd

//        shimmerView.stopShimmer()
//        shimmerView.visibility = View.GONE

        // Inflate and populate the native ad view
        val unifiedAdBinding = NativeAdTemplateBinding.inflate(LayoutInflater.from(context))
        populateNativeAdView(nativeAd, unifiedAdBinding)

        // Add the native ad view to your layout
        adFrame.addView(unifiedAdBinding.root)


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

    fun loadBanner(context:Context,adUnitId:String,adSize: AdSize) {
        adView?.adUnitId = adUnitId
        adView?.setAdSize(adSize)
        adView?.background = context.getDrawable(R.color.white)
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }










    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    private var loadTime: Long = 0


    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            BuildConfig.app_open_others,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d("LOG_TAG", "onAdLoaded.")
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d("LOG_TAG", "onAdFailedToLoad: " + loadAdError.message)
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : MainApplication.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: MainApplication.OnShowAdCompleteListener) {
        if (isShowingAd) {
            Log.d("LOG_TAG", "The app open ad is already showing.")
            return
        }
        if (!isAdAvailable()) {
            Log.d("LOG_TAG", "The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
//                if (googleMobileAdsConsentManager.canRequestAds) {
            loadAd(activity)
//                }
            return
        }

        Log.d("LOG_TAG", "Will show ad.")

        appOpenAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d("LOG_TAG", "onAdDismissedFullScreenContent.")

                    onShowAdCompleteListener.onShowAdComplete()
//                        if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
//                        }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d("LOG_TAG", "onAdFailedToShowFullScreenContent: " + adError.message)

                    onShowAdCompleteListener.onShowAdComplete()
//                        if (googleMobileAdsConsentManager.canRequestAds) {
                    loadAd(activity)
//                        }
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("LOG_TAG", "onAdShowedFullScreenContent.")
                }
            }
        isShowingAd = true
        appOpenAd?.show(activity)
    }




}
