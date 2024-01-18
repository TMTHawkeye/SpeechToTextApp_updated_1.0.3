package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.CategoryAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceSearchViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityVoiceSearchCategoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class VoiceSearchCategoryActivity : BaseActivity() {
    lateinit var binding: ActivityVoiceSearchCategoryBinding
    val v_model: VoiceSearchViewModel by viewModel()
    private lateinit var adView: AdView
    private val initialLayoutComplete = AtomicBoolean(false)

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
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceSearchCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adView = AdView(this@VoiceSearchCategoryActivity)
//        binding.adViewContainer.addView(adView)
        if (!isInternetAvailable(this@VoiceSearchCategoryActivity)) {
            binding.shimmerLayout.visibility=View.VISIBLE
            binding.adViewContainer.visibility=View.VISIBLE
            binding.shimmerLayout.startShimmer()
            loadBanner()
        }
        else{
            binding.shimmerLayout.visibility=View.GONE
            binding.adViewContainer.visibility=View.GONE
        }


        val categoryName = intent.getStringExtra("category")
//        if (categoryName?.equals(getString(R.string.communication_title))!!) {
//
//        }

        Log.d("TAG", "LanguageData: $categoryName")
        binding.categoryTitle.text = categoryName
        v_model.getListofCategories(categoryName) {
            binding.categoryRV.layoutManager = GridLayoutManager(this, 3)
            binding.categoryRV.adapter = CategoryAdapter(this, it)
            val fixedSpacing = 8
            val spacing = (fixedSpacing * resources.displayMetrics.density).toInt()
            binding.categoryRV.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    outRect.left = spacing
                    outRect.right = spacing
                    outRect.bottom = spacing
                    outRect.top = spacing
                }
            })
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

//    private fun loadBanner() {
//        adView.adUnitId = BuildConfig.banner_voice_talk
//        adView.setAdSize(adSize)
//        adView.background=getDrawable(R.color.white)
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
    }

    private fun loadBanner() {
        adView.adUnitId = BuildConfig.categoriesScreen_colapsible_Banner
        adView.setAdSize(adSize)
        adView.setBackgroundColor(getColor(R.color.light_green))

        val extras = Bundle()
        extras.putString("collapsible", "bottom")
        extras.putString("collapsible_request_id", UUID.randomUUID().toString());
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()

        adView.adListener = object : AdListener() {
            override fun onAdOpened() {
                val layoutParams = adView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.setMargins(
                    layoutParams.leftMargin,
                    layoutParams.topMargin,
                    layoutParams.rightMargin,
                    20
                )
                adView.layoutParams = layoutParams
            }

            override fun onAdClosed() {
                val layoutParams = adView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.setMargins(
                    layoutParams.leftMargin,
                    layoutParams.topMargin,
                    layoutParams.rightMargin,
                    0
                )
                adView.layoutParams = layoutParams
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE

            }
        }
        adView.loadAd(adRequest)

        binding.adViewContainer.addView(adView)

    }

}
