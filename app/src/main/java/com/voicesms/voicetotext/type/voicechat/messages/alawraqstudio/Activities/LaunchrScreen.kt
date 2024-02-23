package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.AdsClass
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdsConsentManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.PreloadAdsUtils
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isFirstTimeLaunch
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.UMPResultListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityLauncherBinding
import io.paperdb.Paper
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.AppOpenManager
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AdCallback
import org.smrtobjads.ads.callbacks.AperoAdCallback

class LaunchrScreen : BaseActivity(), UMPResultListener {
    lateinit var binding: ActivityLauncherBinding
    var appOpenAd: AppOpenAd? = null
    private val handler = Handler()
    private val CONSENT_PREFERENCE_KEY = "user_consent"
    var enableUmp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.launcherLottieId.repeatCount = LottieDrawable.INFINITE
        binding.launcherLottieId.playAnimation()
        Paper.init(this@LaunchrScreen)

/*//        enableUmp=loadConsentState()

//        if (isInternetAvailable(this@LauncherActivity)) {*/
            if (enableUmp) {
                val adsConsentManager = AdsConsentManager(this)
                adsConsentManager.requestUMP({
                    Log.d("TAG_response", "onCreate: $it")
                    runOnUiThread {
                        PreloadAdsUtils.getInstance().loadIntersAlternate(
                            this,
                            BuildConfig.interstitial_voice_rec_save_btn,
                            BuildConfig.Translate_Button_inter,
                            2
                        )
                        if (isFirstTimeLaunch()) {
                            loadLanguageNative()
                        }
                        loadSplashNative()

                        handleFetchedRemoteConfig()
                        loadWelcomeInter()
                    }
                }, false)
            } else {
                PreloadAdsUtils.getInstance().loadIntersAlternate(
                    this,
                    BuildConfig.interstitial_voice_rec_save_btn,
                    BuildConfig.Translate_Button_inter,
                    2)

                if (isFirstTimeLaunch()) {
                    loadLanguageNative()
                }
                handleFetchedRemoteConfig()
                loadSplashNative()
                loadWelcomeInter()
            }
    }
    private fun handleFetchedRemoteConfig() {
        AppPurchase.getInstance().setBillingListener({ integer: Int -> loadAdsSplash() }, 1000)
    }
    private fun loadWelcomeInter() {
        SmartAds.getInstance()
            .getInterstitialAds(this, BuildConfig.welcome_Screen_inter, object : AperoAdCallback() {
                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
                    AdsClass.getAdApplication()?.getStorageCommon()?.welcomeInterstitialAd =
                        interstitialAd
                }
            })
    }

    private val TIMEOUT_SPLASH: Long = 30000
    private val TIME_DELAY_SPLASH: Long = 5000

    //    private val typeAdsSplash = "inter"
    private val typeAdsSplash = "app_open_start"
    private fun loadAdsSplash() {
        if (AppPurchase.getInstance().isPurchased(this)) {
            AdsClass.getAdApplication().isAdCloseSplash?.postValue(true)
            startMainActivity()
        } else {
            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(false)

//            if (typeAdsSplash == "app_open_start"){
            AppOpenManager.getInstance().loadOpenAppAdSplash(
                this,
                TIMEOUT_SPLASH,
                TIME_DELAY_SPLASH,
                false,
                object : AdCallback() {
                    override fun onAdSplashReady() {
                        super.onAdSplashReady()
                        Log.d("TAG_appOpen", "onAdSplashReady: splash ready app open")

                        if (isDestroyed || isFinishing) return
                        showAdsOpenAppSplash()
                    }

                    override fun onNextAction() {
                        super.onNextAction()
                        Log.d("TAG_appOpen", "onNextAction: nextaction app open")

                        if (isDestroyed || isFinishing) return
                        AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                        startMainActivity()
                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        Log.d("TAG_appOpen", "onAdFailedToLoad: failed_to load app open")
                        if (isDestroyed || isFinishing) return
                        AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                        startMainActivity()
                    }
                }
            )
//            }else{
//                SmartAds.getInstance().loadSplashInterstitialAds(
//                    this,
//                    BuildConfig.splash_intersitial_id,
//                    TIMEOUT_SPLASH,
//                    TIME_DELAY_SPLASH,
//                    false,
//                    object : AperoAdCallback() {
//                        override fun onAdFailedToLoad(adError: ApAdError?) {
//                            super.onAdFailedToLoad(adError)
//                            if (isDestroyed || isFinishing) return
//                            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
//                        }
//
//                        override fun onNextAction() {
//                            super.onNextAction()
//                            if (isDestroyed || isFinishing) return
//                            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
//                            LoadTheNextActivity()
//                        }
//
//                        override fun onAdSplashReady() {
//                            super.onAdSplashReady()
//                            if (isDestroyed || isFinishing) return
//                            showInterSplash()
//                        }
//                    }
//                )
//            }
        }
    }
    private fun showAdsOpenAppSplash() {
        AppOpenManager.getInstance().showAppOpenSplash(
            this,
            object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    if (isDestroyed || isFinishing) return
                    startMainActivity()
                }

                override fun onAdFailedToShow(adError: AdError?) {
                    super.onAdFailedToShow(adError)
                    if (isDestroyed || isFinishing) return
                    AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    startMainActivity()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    if (isDestroyed || isFinishing) return
                    AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    startMainActivity()
                }
            }
        )
    }
    fun loadLanguageNative(nativeAdId: String = BuildConfig.language_Screen_Native) {
        if (AdsClass.getAdApplication().getStorageCommon()?.nativeAdsLanguage?.getValue() == null
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
                            ?.getStorageCommon()?.nativeAdsLanguage?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()
                            ?.getStorageCommon()?.nativeAdsLanguage?.postValue(null)
                    }
                }
            )
        }
    }
    fun loadSplashNative(nativeAdId: String = BuildConfig.native_splash) {
        if (AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.native_ad_template,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(
                            nativeAd
                        )
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(
                            null
                        )
                    }
                }
            )
        }
    }
    private fun saveConsentState(consentGiven: Boolean) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean(CONSENT_PREFERENCE_KEY, consentGiven)
        editor.apply()
    }

    /*   private fun loadConsentState(): Boolean {
           val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
           return preferences.getBoolean(CONSENT_PREFERENCE_KEY, true)
       }*/

    /*    private fun loadAppOpenAd(adUnitId: String) {
            val adRequest = AdRequest.Builder().build()

            AppOpenAd.load(
                this,
                adUnitId,
                adRequest,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(appOpenAd: AppOpenAd) {
                        this@LauncherActivity.appOpenAd = appOpenAd
                        Log.e("LOG_TAG", "App open loaded for launcher, successfully!")

                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle ad loading failure
                        Log.e("LOG_TAG", "Error loading app open for launcher ad: $loadAdError")
                        appOpenAd = null

                    }
                }
            )
        }*/
    private fun startMainActivity() {
        binding.launcherLottieId.cancelAnimation()
        this.appOpenAd = null
        val intent = if (isFirstTimeLaunch()) {
            Intent(this@LaunchrScreen, LangungeActivity::class.java)
        } else {
            Intent(this@LaunchrScreen, SplashActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
    override fun onCheckUMPSuccess(consentResult: Boolean) {
        saveConsentState(consentResult)
    }

    /*    override fun onResume() {
            super.onResume()
            if (isFirstRunApp) {
                isFirstRunApp = false
                return
            }
            if (typeAdsSplash == "inter") {
                SmartAds.getInstance().onCheckShowSplashWhenFail(this, object : AperoAdCallback() {
                    override fun onAdFailedToShow(adError: ApAdError?) {
                        super.onAdFailedToShow(adError)
                        if (isDestroyed || isFinishing) return
                       AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    }

                    override fun onNextAction() {
                        super.onNextAction()
                        if (isDestroyed || isFinishing) return
                        startMainActivity()
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        if (isDestroyed || isFinishing) return
                       AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    }
                }, 1000)
            } else {
                AppOpenManager.getInstance().onCheckShowAppOpenSplashWhenFail(
                    this,
                    object : AdCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            if (isDestroyed || isFinishing) return
                            startMainActivity()
                        }

                        override fun onAdFailedToShow(adError: AdError?) {
                            super.onAdFailedToShow(adError)
                            if (isDestroyed || isFinishing) return
                           AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                            startMainActivity()
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            if (isDestroyed || isFinishing) return
                           AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                            startMainActivity()
                        }
                    },
                    1000
                )
            }
        }*/

}