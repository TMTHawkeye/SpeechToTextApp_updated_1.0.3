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
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdsConsentManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isFirstTimeLaunch
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Interfaces.UMPResultListener
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityLauncherBinding
import io.paperdb.Paper
import java.util.concurrent.atomic.AtomicBoolean

class LauncherActivity : BaseActivity() , UMPResultListener{
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
        Paper.init(this@LauncherActivity)

        enableUmp=loadConsentState()

        if (isInternetAvailable(this@LauncherActivity)) {
            if (enableUmp) {
                val adsConsentManager = AdsConsentManager(this)
                adsConsentManager.requestUMP({
                    Log.d("TAG_response", "onCreate: $it")
                    runOnUiThread {
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
                                            startMainActivity()
                                        }
                                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                            appOpenAd = null
                                            Log.d(
                                                "LOG_TAG",
                                                "onAdFailedToShowFullScreenContent: " + adError.message
                                            )
                                            startMainActivity()
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
                }, false)
            } else {
                startMainActivity()
            }
        }
        else{
            startMainActivity()
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
        return preferences.getBoolean(CONSENT_PREFERENCE_KEY, true)
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
                    Log.e("AppOpenAd", "App open loaded successfully!")

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad loading failure
                    Log.e("AppOpenAd", "Error loading app open ad: $loadAdError")
                    appOpenAd = null

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
        appOpenAd = null
    }

    override fun onResume() {
        super.onResume()
        appOpenAd = null

    }

    override fun onDestroy() {
        super.onDestroy()
        appOpenAd = null
    }

    override fun onCheckUMPSuccess(consentResult: Boolean) {
        saveConsentState(consentResult)
    }


}