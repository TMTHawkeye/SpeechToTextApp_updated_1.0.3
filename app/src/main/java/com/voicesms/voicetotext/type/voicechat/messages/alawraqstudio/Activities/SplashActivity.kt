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
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.AdsClass
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dloadLanguage
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySplashBinding
import io.paperdb.Paper
import kotlinx.coroutines.launch
import org.smrtobjads.ads.SmartAds
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

/*    private val delayedVisibilityChange = Runnable {
        binding.progressSplash.visibility = android.view.View.GONE
        binding.cardStart.visibility = android.view.View.VISIBLE
    }*/

    var welcomeInterstitialAd: ApInterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        splashNativeAd()

        loadHomeScreenVoiceSmsNative()

//        loadVoiceRecNative()
//        loadVoiceSearchNative()
        downloadInitialModel()
        welcomeInterstitialAd = AdsClass.getAdApplication().getStorageCommon()?.welcomeInterstitialAd
        if (welcomeInterstitialAd == null){
            Log.d("TAG_inter", "interstitial value : null")
            loadWelcomeInter()
        }

        handler.postDelayed({
            binding.progressSplash.visibility = android.view.View.GONE
            binding.cardStart.visibility = android.view.View.VISIBLE
        }, 3000)

/*
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
//                .forceShowInterstitial(this@SplashActivity,AdsClass.getAdApplication()
//                    .getStorageCommon()?.splashInterstitial?.getValue() , object : AperoAdCallback() {
//                    override fun onAdClosed() {
//                        super.onAdClosed()
//                        Log.d("TAG_interst", "onAdClosed: Ad Closed")
//                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    }
//                })
            if(AdsClass.getAdApplication().getStorageCommon().interNormal!=null) {
                PreloadAdsUtils.getInstance().showInterAlternateByForce(
                    this@SplashActivity,
                    AdsClass.getAdApplication().getStorageCommon().interNormal,
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
                            AdsClass.getAdApplication()
                                .getStorageCommon().interNormal = null
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
*/

        binding.cardStart.setOnClickListener {

            welcomeInterstitialAd.let {
                if (it == null) {
                    gotoHome()
                } else {
                    if (it.isReady) {
                        SmartAds.getInstance()
                            .forceShowInterstitial(this, it, object : AperoAdCallback() {
                                override fun onNextAction() {
                                    Log.i("ad", "onAdClosed: start content and finish main")
                                    gotoHome()
                                }

                                override fun onAdFailedToShow(adError: ApAdError?) {
                                    super.onAdFailedToShow(adError)
                                    Log.i("ad", "onAdFailedToShow:" + adError!!.message)
                                }

                                override fun onAdImpression() {
                                    super.onAdImpression()

                                }

                            }, false)
                    } else {
                        gotoHome()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun gotoHome() {
        AdsClass.getAdApplication().getStorageCommon().welcomeInterstitialAd = null
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }

    private fun loadWelcomeInter() {

        SmartAds.getInstance()
            .getInterstitialAds(this, BuildConfig.welcome_Screen_inter, object : AperoAdCallback() {
                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
                    welcomeInterstitialAd = interstitialAd
                    AdsClass.getAdApplication().getStorageCommon().welcomeInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
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
    private fun splashNativeAd() {
        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative.let { appNative ->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(this@SplashActivity,
                    BuildConfig.native_splash,
                    R.layout.native_ad_template,
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

    fun loadHomeScreenVoiceSmsNative(nativeAdId: String = BuildConfig.native_voice_SMS) {
        if (AdsClass.getAdApplication().getStorageCommon()?.homeNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.native_ad_template,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()
                            ?.getStorageCommon()?.welcomeNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()
                            ?.getStorageCommon()?.welcomeNative?.postValue(null)
                    }
                }
            )
        }
    }



}