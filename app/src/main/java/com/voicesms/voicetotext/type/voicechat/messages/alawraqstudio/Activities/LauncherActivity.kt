package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {
    lateinit var binding: ActivityLauncherBinding
    private var appOpenAd: AppOpenAd? = null
    private val handler = Handler()

    //    private val delayedVisibilityChange = Runnable {
//        binding.progressSplash.visibility = android.view.View.GONE
////        binding.cardStart.visibility = android.view.View.VISIBLE
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.launcherLottieId.repeatCount = LottieDrawable.INFINITE
        binding.launcherLottieId.playAnimation()
        loadAppOpenAd()

        handler.postDelayed({
            binding.progressSplash.visibility = android.view.View.GONE
            if (appOpenAd != null) {
                appOpenAd?.show(this)
            } else {
                startMainActivity()
            }
        }, 3000)


    }

    private fun loadAppOpenAd() {
        val adRequest = AdRequest.Builder().build()

        AppOpenAd.load(
            this,
            BuildConfig.app_open_launcher,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    this@LauncherActivity.appOpenAd = appOpenAd
                    appOpenAd.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Proceed to the main activity after the ad is dismissed
                            startMainActivity()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Handle ad showing failure
                            Log.e("AppOpenAd", "Error showing app open ad: $adError")
                            startMainActivity()
                        }
                    })
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle ad loading failure
                    Log.e("AppOpenAd", "Error loading app open ad: $loadAdError")
                    startMainActivity()

                }
            }
        )
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        handler.removeCallbacks(delayedVisibilityChange)
//    }

    private fun startMainActivity() {

        binding.launcherLottieId.cancelAnimation()

        val intent = if (isFirstTimeLaunch()) {
            Intent(this@LauncherActivity, LangungeActivity::class.java)
        } else {
            Intent(this@LauncherActivity, SplashActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun isFirstTimeLaunch(): Boolean {
        val preferences = getSharedPreferences("LaunchPrefs", Context.MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            val editor = preferences.edit()
            editor.putBoolean("isFirstTime", false)
            editor.apply()
        }
        return isFirstTime
    }

}