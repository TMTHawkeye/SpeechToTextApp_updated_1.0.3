// SplashActivity.kt

package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.SplashPagerAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentGetStarted
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentNext
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityGuideBinding
import kotlinx.coroutines.launch

class GuideActivity : BaseActivity() {
    private lateinit var binding: ActivityGuideBinding
    private lateinit var adView: AdView
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                this@GuideActivity,
                adWidth
            )
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adView = AdView(this@GuideActivity)
        binding.adViewContainer.addView(adView)
        loadBanner()

        val fragmentList = listOf(SplashFragmentNext(), SplashFragmentGetStarted())
        val adapter = SplashPagerAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.adapter = adapter


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }

        })
    }

    private fun loadBanner() {
        adView.adUnitId = BuildConfig.banner_voice_talk
        adView.setAdSize(adSize)
        adView.background = getDrawable(R.color.white)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    fun navigateToNextFragment() {
        binding.viewPager.currentItem = 1
    }

    fun navigateToPreviousFragment() {
        binding.viewPager.currentItem = 0
    }




}
