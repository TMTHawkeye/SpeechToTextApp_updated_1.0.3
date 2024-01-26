package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

//import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdsInterCallBack
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.PreloadAdsUtils
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dloadLanguage
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySplashBinding
import io.paperdb.Paper
import kotlinx.coroutines.launch
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback


class SplashActivity : BaseActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val handler = Handler()

//    private lateinit var adView: AdView
//    var progressDialog:ProgressDialog=ProgressDialog(this)

    //    private var currentNativeAd: NativeAd? = null
    private val delayedVisibilityChange = Runnable {
        binding.progressSplash.visibility = android.view.View.GONE
        binding.cardStart.visibility = android.view.View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        adView = AdView(this)
        splashNativeAd()
        loadVoiceSMSNative()
//        loadVoiceRecNative()
//        loadVoiceSearchNative()
//        if (isInternetAvailable(this@SplashActivity)) {
////            binding.shimmerViewContainer.visibility = View.VISIBLE
//            binding.frameLayout.visibility = View.VISIBLE
////            binding.shimmerViewContainer.startShimmer()
////            AdManager.getInstance().loadNativeAd(
////                this@SplashActivity,
////                BuildConfig.native_splash,
////                binding.adFrame,
////                binding.shimmerViewContainer
////            )
//        } else {
////            binding.shimmerViewContainer.visibility = View.GONE
//            binding.frameLayout.visibility = View.GONE
//        }
//        refreshAd()
//        binding.shimmerViewContainer.startShimmer()


        downloadInitialModel()
//        if (AdManager.getInstance().interstitialAd == null) {
//            AdManager.getInstance().loadInterstitial(this, BuildConfig.welcome_Screen_inter)
//        }


        handler.postDelayed({
            binding.progressSplash.visibility = android.view.View.GONE
            binding.cardStart.visibility = android.view.View.VISIBLE
        }, 3000)

        binding.cardStart.setOnClickListener {

//            if (isInternetAvailable(this@SplashActivity)) {


//                if (AdManager.getInstance().interstitialAd == null) {
//                    AdManager.getInstance()
//                        .loadInterstitial(this, BuildConfig.welcome_Screen_inter)
//                }
//                 progressDialog =
//                progressDialog.setMessage("Loading Interstitial...")
//                progressDialog.setCancelable(false)
//                if (AdManager.getInstance().interstitialAd != null) {
//                    showLoadingDialog(progressDialog)
//                } else {
//                    dismissLoadingDialog(progressDialog)


//            SmartAds.getInstance()
//                .forceShowInterstitial(this@SplashActivity,MainApplication.getAdApplication()
//                    .getStorageCommon()?.splashInterstitial?.getValue() , object : AperoAdCallback() {
//                    override fun onAdClosed() {
//                        super.onAdClosed()
//                        Log.d("TAG_interst", "onAdClosed: Ad Closed")
//                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    }
//                })
            if(MainApplication.getAdApplication().getStorageCommon().splashInterstitial!=null) {
                PreloadAdsUtils.getInstance().showInterAlternateByForce(
                    this@SplashActivity,
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
                            MainApplication.getAdApplication()
                                .getStorageCommon().splashInterstitial = null
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        }

                        override fun onAdClicked() {
                            Log.d("TAG_interst", "onAdClicked: Ad clicked")
                        }

                        override fun onNextAction() {
                        }

                    })
            }
            else{
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
//            loadInterstitialSplash()

//                }

//            showInterstitial()
//                handler.postDelayed({
//                    if (AdManager.getInstance().interstitialAd == null) {
//                        dismissLoadingDialog(progressDialog)
//
//                        AdManager.getInstance()
//                            .loadInterstitial(this, BuildConfig.welcome_Screen_inter)
//                    }
//                    AdManager.getInstance()
//                        .showInterstitial(this, BuildConfig.welcome_Screen_inter) {
//                            dismissLoadingDialog(progressDialog)
//                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                        }
//                }, 1000)
//            } else {
////                dismissLoadingDialog(progressDialog)
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            }
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
                        Paper.book().write("initialModel", success)
                    } else {
                        Log.d("TAG", "Initial Model Download: failure")
                        Paper.book().write("initialModel", !success)

                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
//        adView.pause()

//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }

    override fun onResume() {
        super.onResume()
//        (application as MainApplication).loadAd(this)
//        adView.resume()

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


    override fun onDestroy() {
        super.onDestroy()
//        handler.removeCallbacks(delayedVisibilityChange)
//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }


    private fun splashNativeAd() {

        MainApplication.getAdApplication()?.getStorageCommon()?.welcomeNative.let { appNative ->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(this@SplashActivity,
                    BuildConfig.native_splash,
                    org.smrtobjads.ads.R.layout.custom_native_admob_free_size,
                    object :
                        AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(
                                this@SplashActivity,
                                nativeAd,
                                binding.frameLayout,
                                binding.splashNativeAd.shimmerContainerNative
                            )
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.frameLayout.visibility = View.GONE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.frameLayout.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()

                        }
                    })
            } else {
                SmartAds.getInstance().populateNativeAdView(
                    this@SplashActivity,
                    appNative.value,
                    binding.frameLayout,
                    binding.splashNativeAd.shimmerContainerNative
                )
            }
        }

    }

    fun loadVoiceSMSNative(nativeAdId: String = BuildConfig.native_voice_SMS) {
        if (MainApplication.getAdApplication()
                .getStorageCommon()?.voiceSMSNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                org.smrtobjads.ads.R.layout.custom_native_admob_free_size_btn_bottom,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        MainApplication.getAdApplication()
                            ?.getStorageCommon()?.voiceSMSNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        MainApplication.getAdApplication()
                            ?.getStorageCommon()?.voiceSMSNative?.postValue(null)
                    }
                }
            )
        }
    }

//    fun getInterstitialAds(context: Context?, id: String?): ApInterstitialAd? {
//        val apInterstitialAd = ApInterstitialAd()
//        return when (adConfig.getMediationProvider()) {
//            SmartAdsConfig.PROVIDER_ADMOB -> {
//                SmartObjAdmob.getInstance().getInterstitialAds(context, id, object : AdCallback() {
//                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {
//                        super.onInterstitialLoad(interstitialAd)
//                        Log.d(SmartAds.TAG, "Admob onInterstitialLoad: ")
//                        apInterstitialAd.interstitialAd = interstitialAd
//                    }
//
//                    override fun onAdFailedToLoad(i: LoadAdError?) {
//                        super.onAdFailedToLoad(i)
//                    }
//
//                    override fun onAdFailedToShow(adError: AdError?) {
//                        super.onAdFailedToShow(adError)
//                    }
//                })
//                apInterstitialAd
//            }
//
//            else -> apInterstitialAd
//        }
//    }


    fun loadInterstitialSplash() {
//        if (MainApplication.getAdApplication()
//                .getStorageCommon()?.splashInterstitial?.getValue() == null
//            && !AppPurchase.getInstance().isPurchased
//        ) {
        SmartAds.getInstance().loadSplashInterstitialAds(
            this@SplashActivity,
            BuildConfig.welcome_Screen_inter,
            2000,
            500,
            object : AperoAdCallback() {

                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
//                        MainApplication.getAdApplication()
//                            ?.getStorageCommon()?.splashInterstitial?.postValue(interstitialAd)
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    Log.d("TAG_interst", "onAdFailedToLoad: $adError")
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                        MainApplication.getAdApplication()
//                            ?.getStorageCommon()?.splashInterstitial?.postValue(null)
                }

                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    Log.d("TAG_interst", "onAdFailedToLoad: $adError")
//                    MainApplication.getAdApplication()
//                        ?.getStorageCommon()?.splashInterstitial?.postValue(null)
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d("TAG_interst", "onAdClosed: Ad Closed")
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            })
    }
//    }


}