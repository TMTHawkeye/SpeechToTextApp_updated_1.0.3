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
import org.smrtobjads.ads.SmartAds

class GuideActivity : BaseActivity() {
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isInternetAvailable(this@GuideActivity)) {
            binding.welcomeNativecontainer.visibility=View.VISIBLE
//            val welcomeAdContainer = binding.welcomeNativecontainer.findViewById<View>(R.id.welcomeBannerAd)
//            val fl_adplaceholder = welcomeAdContainer.findViewById<FrameLayout>(org.smrtobjads.ads.R.id.banner_container)
//            val shimmerFrameLayout = welcomeAdContainer.findViewById<ShimmerFrameLayout>(org.smrtobjads.ads.R.id.shimmer_container_banner)
            SmartAds.getInstance().loadBanner(this@GuideActivity,BuildConfig.categoriesScreen_colapsible_Banner)

//            SmartAds.getInstance().loadBanner(this@GuideActivity,BuildConfig.categoriesScreen_colapsible_Banner,fl_adplaceholder,shimmerFrameLayout)
        }
        else{
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

    fun navigateToNextFragment() {
        binding.viewPager.currentItem = 1
    }

    fun navigateToPreviousFragment() {
        binding.viewPager.currentItem = 0
    }




}
