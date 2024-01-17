package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Adapters.CategoryAdapter
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceSearchViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.ActivityVoiceSearchCategoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
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
        binding=ActivityVoiceSearchCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        adView = AdView(this)
//        binding.adViewContainer.addView(adView)
//
//        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
//            if (!initialLayoutComplete.getAndSet(true)) {
//                loadBanner()
//            }
//        }

        val categoryName=intent.getStringExtra("category")
        if(categoryName?.equals(getString(R.string.communication_title))!!){

        }
        binding.categoryTitle.text=categoryName
        v_model.getListofCategories(categoryName){
            binding.categoryRV.layoutManager=GridLayoutManager(this, 3)
            binding.categoryRV.adapter= CategoryAdapter(this,it)
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
}
