package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

//import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.BaseActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.VoiceSearchCategoryActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.AdsClass
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceSearchBinding
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.callbacks.AperoAdCallback

class VoiceSearchFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentVoiceSearchBinding
    private val handler = Handler()
    lateinit var progressDialog: ProgressDialog
    var category=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceSearchBinding.inflate(layoutInflater, container, false)
        SearchScreenNative()

        binding.cardViewComunication.setOnClickListener(this)
        binding.cardViewShopping.setOnClickListener(this)
        binding.cardViewSocialmedia.setOnClickListener(this)
        binding.cardViewSearchEngine.setOnClickListener(this)
        binding.cardViewMore.setOnClickListener(this)
        return binding.root
    }

    private fun SearchScreenNative(){

                SmartAds.getInstance().loadNativeAdResultCallback(requireContext(),
                    BuildConfig.Voice_Search_Screen_Native,   R.layout.native_ad_template, object :
                        AperoAdCallback(){
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(requireContext(), nativeAd,binding.adViewContainer , binding.splashNativeAd.shimmerContainerNative)
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.adViewContainer.visibility = View.GONE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.adViewContainer.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()

                        }
                    })


    }


    override fun onClick(v: View?) {

        (activity as BaseActivity).showInterstitialAdByTimes(""){
            category = ""
            when (v!!.id) {
                R.id.cardView_comunication -> {
                    category = getString(R.string.communication_title)
                }

                R.id.cardView_shopping -> {
                    category = getString(R.string.shopping_title)
                }

                R.id.cardView_socialmedia -> {
                    category = getString(R.string.social_title)
                }

                R.id.cardView_searchEngine -> {
                    category = getString(R.string.searchEngine_title)
                }

                R.id.cardView_more -> {
                    category = getString(R.string.more_title)
                }
            }

            startActivity(
                Intent(
                    requireContext(),
                    VoiceSearchCategoryActivity::class.java
                ).putExtra("category", category)
            )
        }

//        if (AdManager.getInstance().interstitialAd == null) {
//            AdManager.getInstance()
//                .loadInterstitial(requireContext(), BuildConfig.interstitial_voice_search_category)
//        }

//        handler.postDelayed({

//        if (isInternetAvailable(requireContext())) {
//            if(AdsClass.getAdApplication().getStorageCommon().interNormal!=null) {

         /*       PreloadAdsUtils.getInstance().showInterAlternateByTime(
                    requireContext(),
                    AdsClass.getAdApplication().getStorageCommon().interNormal,
                    false,
                    object : AdsInterCallBack {
                        override fun onInterstitialPriorityShowed() {

                        }

                        override fun onInterstitialNormalShowed() {
                        }

                        override fun onInterstitialShowed() {
                            Log.d("TAG_interst", "onInterstitialShowed: Ad showed")
                        }

                        override fun onAdClosed() {
                            Log.d("TAG_interst", "onAdClosed: Ad Closed")
                            AdsClass.getAdApplication()
                                .getStorageCommon().interNormal = null
                            PreloadAdsUtils.getInstance().loadIntersAlternate(
                                requireContext(),
                                BuildConfig.interstitial_voice_search_category,
                                BuildConfig.Translate_Button_inter,
                                2
                            )
                            startActivity(
                                Intent(
                                    requireContext(),
                                    VoiceSearchCategoryActivity::class.java
                                ).putExtra("category", category)
                            )
                        }

                        override fun onAdClicked() {
                            Log.d("TAG_interst", "onAdClicked: Ad clicked")
                        }

                        override fun onNextAction() {
                        }
                    })*/


//            }
//            else{
//                startActivity(
//                    Intent(
//                        requireContext(),
//                        VoiceSearchCategoryActivity::class.java
//                    ).putExtra("category", category)
//                )
//            }

//                if (AdManager.getInstance().interstitialAd != null) {
//                    handler.postDelayed({
//                    AdManager.getInstance()
//                        .showInterstitial(
//                            requireActivity(),
//                            BuildConfig.interstitial_voice_search_category
//                        ) {
//                            dismissLoadingDialog(progressDialog)
//
//                            startActivity(
//                                Intent(
//                                    requireContext(),
//                                    VoiceSearchCategoryActivity::class.java
//                                ).putExtra("category", category)
//                            )
//                        }
//                    }, 1000)

//                }
//                else{
//                    startActivity(
//                        Intent(
//                            requireContext(),
//                            VoiceSearchCategoryActivity::class.java
//                        ).putExtra("category", category)
//                    )
//                }
//        } else {
//            startActivity(
//                Intent(
//                    requireContext(),
//                    VoiceSearchCategoryActivity::class.java
//                ).putExtra("category", category)
//            )
//        }
//        },500)
    }


    override fun onResume() {
        super.onResume()
//        if(AdsClass.getAdApplication()?.storageCommon?.interNormal==null) {

//        }

        binding.root.clearFocus()
    }








}