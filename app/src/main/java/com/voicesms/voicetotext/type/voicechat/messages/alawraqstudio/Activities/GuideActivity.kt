// SplashActivity.kt

package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.SplashPagerAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentGetStarted
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentNext
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityGuideBinding
import kotlinx.coroutines.launch
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.callbacks.AperoAdCallback

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

        if (isInternetAvailable(this@GuideActivity)) {

//            SmartAds.getInstance().loadBanner(this, BuildConfig.guide_Screen_banner)
            SmartAds.getInstance().loadBanner(this@GuideActivity,BuildConfig.guide_Screen_banner,AperoAdCallback())
        }
        else{
//            binding.shimmerLayout.visibility=View.GONE
            binding.adViewContainer.visibility=View.GONE
        }

        val fragmentList = listOf(SplashFragmentNext(), SplashFragmentGetStarted())
        val adapter = SplashPagerAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.adapter = adapter


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }

        })
    }

//    private fun loadBanner() {
//        adView.adUnitId = BuildConfig.guide_Screen_banner
//        adView.setAdSize(adSize)
//        adView.background = getDrawable(R.color.white)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//
//        adView.adListener = object : AdListener() {
//            override fun onAdOpened() {
//                val layoutParams = adView.layoutParams as ConstraintLayout.LayoutParams
//                layoutParams.setMargins(
//                    layoutParams.leftMargin,
//                    layoutParams.topMargin,
//                    layoutParams.rightMargin,
//                    20
//                )
//                adView.layoutParams = layoutParams
//            }
//
//            override fun onAdClosed() {
//                val layoutParams = adView.layoutParams as ConstraintLayout.LayoutParams
//                layoutParams.setMargins(
//                    layoutParams.leftMargin,
//                    layoutParams.topMargin,
//                    layoutParams.rightMargin,
//                    0
//                )
//                adView.layoutParams = layoutParams
//            }
//
//            override fun onAdLoaded() {
//                super.onAdLoaded()
////                binding.shimmerLayout.stopShimmer()
////                binding.shimmerLayout.visibility = View.GONE
//
//            }
//
//            override fun onAdFailedToLoad(p0: LoadAdError) {
//                super.onAdFailedToLoad(p0)
////                binding.shimmerLayout.visibility = View.GONE
////                binding.adViewContainer.visibility = View.GONE
//
//            }
//        }
//
//    }

    fun navigateToNextFragment() {
        binding.viewPager.currentItem = 1
    }

    fun navigateToPreviousFragment() {
        binding.viewPager.currentItem = 0
    }




}
