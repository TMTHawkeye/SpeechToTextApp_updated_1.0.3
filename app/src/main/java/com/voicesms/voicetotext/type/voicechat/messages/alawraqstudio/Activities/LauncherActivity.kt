package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import com.airbnb.lottie.LottieDrawable
import com.example.djmixer.objects.GPDRDJ
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isFirstTimeLaunch
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityLauncherBinding
import java.util.concurrent.atomic.AtomicBoolean

class LauncherActivity : BaseActivity() {
    lateinit var binding: ActivityLauncherBinding
    var appOpenAd: AppOpenAd? = null
    private val handler = Handler()
    var isLauncherAppOpenLoaded: Boolean = false
    private val CONSENT_PREFERENCE_KEY = "user_consent"


    private lateinit var consentInformation: ConsentInformation

    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.launcherLottieId.repeatCount = LottieDrawable.INFINITE
        binding.launcherLottieId.playAnimation()
        val shar: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (isInternetAvailable(this@LauncherActivity)) {
            val gpdrDJ = GPDRDJ(shar, this@LauncherActivity) { hasConsent ->
                if (hasConsent) {
                    loadAppOpenAd(BuildConfig.app_open_launcher)

                    handler.postDelayed({
                        binding.progressSplash.visibility = android.view.View.GONE
                        if (appOpenAd != null) {
                            appOpenAd?.show(this)
                            appOpenAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        appOpenAd = null
                                        Log.d("LOG_TAG", "onAdDismissedFullScreenContent.")
//                        if (googleMobileAdsConsentManager.canRequestAds) {
//                        }
                                        startMainActivity()

                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        appOpenAd = null
                                        Log.d("LOG_TAG", "onAdFailedToShowFullScreenContent: " + adError.message)
                                        startMainActivity()

//                        if (googleMobileAdsConsentManager.canRequestAds) {
//                        }
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        Log.d("LOG_TAG", "onAdShowedFullScreenContent.")

                                    }
                                }
                        } else {
                            startMainActivity()
                        }
                    }, 5000)
                }


            }
//        }
//            showConsentForm()
//            loadAppOpenAd(BuildConfig.app_open_launcher)
        } else {
//
////            handler.postDelayed({
////                binding.progressSplash.visibility = android.view.View.GONE
////                if (appOpenAd != null) {
////                    appOpenAd?.show(this)
////                } else {
            startMainActivity()
//                }
////            }, 3000)
        }


    }


    private fun saveConsentState(consentGiven: Boolean) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean(CONSENT_PREFERENCE_KEY, consentGiven)
        editor.apply()
    }

    private fun loadConsentState(): Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return preferences.getBoolean(CONSENT_PREFERENCE_KEY, false)
    }

    private fun loadAppOpenAd(adUnitId: String) {
        val adRequest = AdRequest.Builder().build()

        AppOpenAd.load(
            this,
            adUnitId,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    this@LauncherActivity.appOpenAd = appOpenAd
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad loading failure
                    Log.e("AppOpenAd", "Error loading app open ad: $loadAdError")
                    appOpenAd=null

                }
            }
        )
    }

    private fun startMainActivity() {

        binding.launcherLottieId.cancelAnimation()
        this.appOpenAd = null
        val intent = if (isFirstTimeLaunch()) {
            Intent(this@LauncherActivity, LangungeActivity::class.java)
        } else {
            Intent(this@LauncherActivity, SplashActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        appOpenAd=null
    }

    override fun onResume() {
        super.onResume()
        appOpenAd=null

    }

    override fun onDestroy() {
        super.onDestroy()
        appOpenAd=null
    }


}