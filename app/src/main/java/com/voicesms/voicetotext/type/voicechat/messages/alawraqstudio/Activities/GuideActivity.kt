// SplashActivity.kt

package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.SplashPagerAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentGetStarted
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments.SplashFragmentNext
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityGuideBinding
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.callbacks.AdCallback
import org.smrtobjads.ads.callbacks.AperoAdCallback

class GuideActivity : BaseActivity() {
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isInternetAvailable(this@GuideActivity)) {

//            SmartAds.getInstance().loadBanner(this, BuildConfig.guide_Screen_banner)
//            SmartAds.getInstance().loadBanner(this@GuideActivity,
//                BuildConfig.guide_Screen_banner,AperoAdCallback())


            val welcomeAdContainer = binding.welcomeNativecontainer.findViewById<View>(R.id.welcomeBannerAd)
            val fl_adplaceholder = welcomeAdContainer.findViewById<FrameLayout>(org.smrtobjads.ads.R.id.banner_container)
            val shimmerFrameLayout = welcomeAdContainer.findViewById<ShimmerFrameLayout>(org.smrtobjads.ads.R.id.shimmer_container_banner)
            SmartAds.getInstance().loadBanner(this@GuideActivity,BuildConfig.categoriesScreen_colapsible_Banner,fl_adplaceholder,shimmerFrameLayout)
        }
        else{
//            binding.shimmerLayout.visibility=View.GONE
            binding.welcomeNativecontainer.visibility=View.GONE
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
