package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.CategoryAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.CategoryModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityVoiceSearchCategoryBinding
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AdCallback
import org.smrtobjads.ads.callbacks.AperoAdCallback
import java.util.concurrent.atomic.AtomicBoolean

class VoiceSearchCategoryActivity : BaseActivity() {
    lateinit var binding: ActivityVoiceSearchCategoryBinding
//    private lateinit var adView: AdView
    private val initialLayoutComplete = AtomicBoolean(false)

//    private val adSize: AdSize
//        get() {
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)
//
//            val density = outMetrics.density
//
//            var adWidthPixels = binding.adViewContainer.width.toFloat()
//            if (adWidthPixels == 0f) {
//                adWidthPixels = outMetrics.widthPixels.toFloat()
//            }
//
//            val adWidth = (adWidthPixels / density).toInt()
//            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceSearchCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val welcomeAdContainer = binding.welcomeNativecontainer.findViewById<View>(R.id.welcomeBannerAd)
        val fl_adplaceholder = welcomeAdContainer.findViewById<FrameLayout>(org.smrtobjads.ads.R.id.banner_container)
        val shimmerFrameLayout = welcomeAdContainer.findViewById<ShimmerFrameLayout>(org.smrtobjads.ads.R.id.shimmer_container_banner)
        SmartAds.getInstance().loadCollapsibleBanner(this@VoiceSearchCategoryActivity,BuildConfig.categoriesScreen_colapsible_Banner,"bottom",fl_adplaceholder,shimmerFrameLayout,object : AdCallback(){

        })


        loadsubCategoryNative()
//        SmartAds.getInstance().loadCollapsibleBanner(this, BuildConfig.categoriesScreen_colapsible_Banner,"bottom",fl_adplaceholder,shimmerFrameLayout,AdCallback())

//        binding.adViewContainer.addView(adView)
//        if (isInternetAvailable(this@VoiceSearchCategoryActivity)) {
//            binding.shimmerLayout.visibility=View.VISIBLE
//            binding.adViewContainer.visibility=View.VISIBLE
//            binding.shimmerLayout.startShimmer()
//            loadBanner()
//        }
//        else{
//            binding.shimmerLayout.visibility=View.GONE
//            binding.adViewContainer.visibility=View.GONE
//        }


        val categoryName = intent.getStringExtra("category")
//        if (categoryName?.equals(getString(R.string.communication_title))!!) {
//
//        }

        Log.d("TAG", "LanguageData: $categoryName")
        binding.categoryTitle.text = categoryName
//        v_model.getListofCategories(categoryName) {
//            binding.categoryRV.layoutManager = GridLayoutManager(this, 3)
        getListofCategories(categoryName){
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
//        (application as MainApplication).loadAd(this)
    }

//    private fun loadBanner() {
//        adView.adUnitId = BuildConfig.categoriesScreen_colapsible_Banner
//        adView.setAdSize(adSize)
//        adView.setBackgroundColor(getColor(R.color.light_green))
//
//        val extras = Bundle()
//        extras.putString("collapsible", "bottom")
//        extras.putString("collapsible_request_id", UUID.randomUUID().toString());
//        val adRequest = AdRequest.Builder()
//            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//            .build()
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
////                binding.shimmerLayout.visibility=View.GONE
//                binding.adViewContainer.visibility=View.GONE
//            }
//        }
//        adView.loadAd(adRequest)
//
//        binding.adViewContainer.addView(adView)
//
//    }

    fun getListofCategories(categoryName: String?, callback:(ArrayList<CategoryModel>)->Unit) {

        var listOfCategories = ArrayList<CategoryModel>()

        val categoryMap = mapOf(
            getString(R.string.communication_title) to listOf(
                CategoryModel(getString(R.string.reddit), R.drawable.reddit_icon),
                CategoryModel(getString(R.string.quora), R.drawable.quora_icon),
                CategoryModel(getString(R.string.flipboard), R.drawable.flipboard_icon),
                CategoryModel(getString(R.string.msn), R.drawable.msn_icon)
            ),
            getString(R.string.shopping_title) to listOf(
                CategoryModel(getString(R.string.alibaba), R.drawable.alibaba_icon),
                CategoryModel(getString(R.string.amazon), R.drawable.amazon_icon),
                CategoryModel(getString(R.string.daraz), R.drawable.daraz_icon),
                CategoryModel(getString(R.string.olx), R.drawable.olx_icon),
                CategoryModel(getString(R.string.ebay), R.drawable.ebay_icon),
                CategoryModel(getString(R.string.aliexpress), R.drawable.aliexpress_icon)
            ),
            getString(R.string.social_title) to listOf(
                CategoryModel(getString(R.string.youtube), R.drawable.youtube_icon),
                CategoryModel(getString(R.string.insta), R.drawable.insta_icon),
                CategoryModel(getString(R.string.pintrest), R.drawable.pintrest_icon),
                CategoryModel(getString(R.string.facebook), R.drawable.facebook_icon),
                CategoryModel(getString(R.string.twitter), R.drawable.twitter_icon),
                CategoryModel(getString(R.string.tiktok), R.drawable.tiktok_icon)
            ),
            getString(R.string.searchEngine_title) to listOf(
                CategoryModel(getString(R.string.google), R.drawable.google_icon),
                CategoryModel(getString(R.string.bing), R.drawable.bing_icon),
                CategoryModel(getString(R.string.duckgo), R.drawable.duckgo_icon),
                CategoryModel(getString(R.string.yahoo), R.drawable.yahoo_icon),
                CategoryModel(getString(R.string.wikipedia), R.drawable.wikipedia_icon),
            ),
            getString(R.string.more_title) to listOf(
                CategoryModel(getString(R.string.playstore), R.drawable.playstore_icon),
                CategoryModel(getString(R.string.weather), R.drawable.weather_icon),
                CategoryModel(getString(R.string.location), R.drawable.location_icon),
                CategoryModel(getString(R.string.imdb), R.drawable.imdb_icon),
            )

        )

        categoryName?.let {
            if (categoryMap.containsKey(it)) {
                listOfCategories.addAll(categoryMap[it]!!)
            }
        }

        callback(listOfCategories)
    }

    fun loadsubCategoryNative(nativeAdId: String = BuildConfig.Sub_categories_native) {
        if (MainApplication.getAdApplication().getStorageCommon()?.subCategoryNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                org.smrtobjads.ads.R.layout.custom_native_admob_free_size_btn_bottom,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        MainApplication.getAdApplication()?.getStorageCommon()?.subCategoryNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        MainApplication.getAdApplication()?.getStorageCommon()?.subCategoryNative?.postValue(null)
                    }
                }
            )
        }
    }



}
