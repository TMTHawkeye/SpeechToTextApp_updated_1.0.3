package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.app.ProgressDialog
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
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dismissLoadingDialog
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dloadLanguage
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.showLoadingDialog
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivitySplashBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.NativeAdTemplateBinding
import io.paperdb.Paper
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    private val handler = Handler()

    private lateinit var adView: AdView
    lateinit var progressDialog:ProgressDialog

    //    private var currentNativeAd: NativeAd? = null
    private val delayedVisibilityChange = Runnable {
        binding.progressSplash.visibility = android.view.View.GONE
        binding.cardStart.visibility = android.view.View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adView = AdView(this)


        if (isInternetAvailable(this@SplashActivity)) {
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.adFrame.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmer()
            AdManager.getInstance().loadNativeAd(
                this@SplashActivity,
                BuildConfig.native_splash,
                binding.adFrame,
                binding.shimmerViewContainer
            )
        } else {
            binding.shimmerViewContainer.visibility = View.GONE
            binding.adFrame.visibility = View.GONE
        }
//        refreshAd()
//        binding.shimmerViewContainer.startShimmer()


        downloadInitialModel()
        if (AdManager.getInstance().interstitialAd == null) {
            AdManager.getInstance().loadInterstitial(this, BuildConfig.welcome_Screen_inter)
        }


        handler.postDelayed({
            binding.progressSplash.visibility = android.view.View.GONE
            binding.cardStart.visibility = android.view.View.VISIBLE
        }, 3000)

        binding.cardStart.setOnClickListener {
            if (isInternetAvailable(this@SplashActivity)) {
                if (AdManager.getInstance().interstitialAd == null) {
                    AdManager.getInstance()
                        .loadInterstitial(this, BuildConfig.welcome_Screen_inter)
                }
                 progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Loading Interstitial...")
                progressDialog.setCancelable(false)
                if (AdManager.getInstance().interstitialAd != null) {
                    showLoadingDialog(progressDialog)
                } else {
                    dismissLoadingDialog(progressDialog)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }

//            showInterstitial()
                handler.postDelayed({
                    if (AdManager.getInstance().interstitialAd == null) {
                        dismissLoadingDialog(progressDialog)

                        AdManager.getInstance()
                            .loadInterstitial(this, BuildConfig.welcome_Screen_inter)
                    }
                    AdManager.getInstance()
                        .showInterstitial(this, BuildConfig.welcome_Screen_inter) {
                            dismissLoadingDialog(progressDialog)
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        }
                }, 1000)
            } else {
                dismissLoadingDialog(progressDialog)
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
        adView.pause()

        AdManager.getInstance().currentNativeAd = null
        AdManager.getInstance().interstitialAd = null
    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
        adView.resume()

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
        handler.removeCallbacks(delayedVisibilityChange)
        AdManager.getInstance().currentNativeAd = null
        AdManager.getInstance().interstitialAd = null
    }


}