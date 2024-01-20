package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.VoiceSearchCategoryActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.dismissLoadingDialog
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.isInternetAvailable
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.showLoadingDialog
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceSearchBinding

class VoiceSearchFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentVoiceSearchBinding
    private val handler = Handler()
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceSearchBinding.inflate(layoutInflater, container, false)

        if (AdManager.getInstance().interstitialAd == null) {
            AdManager.getInstance()
                .loadInterstitial(requireContext(), BuildConfig.interstitial_voice_search_category)
        }
        AdManager.getInstance().loadNativeAd(
            requireContext(),
            BuildConfig.Voice_Search_Screen_Native,
            binding.adViewContainer,
            binding.shimmerViewContainer
        )

        binding.cardViewComunication.setOnClickListener(this)
        binding.cardViewShopping.setOnClickListener(this)
        binding.cardViewSocialmedia.setOnClickListener(this)
        binding.cardViewSearchEngine.setOnClickListener(this)
        binding.cardViewMore.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {


        var category = ""
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
        if (AdManager.getInstance().interstitialAd == null) {
            AdManager.getInstance()
                .loadInterstitial(requireContext(), BuildConfig.interstitial_voice_search_category)
        }

//        handler.postDelayed({

        if (isInternetAvailable(requireContext())) {
            progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("Loading Interstitial...")
            progressDialog.setCancelable(false)
            if (AdManager.getInstance().interstitialAd != null) {
                showLoadingDialog(progressDialog)
            }


                if (AdManager.getInstance().interstitialAd != null) {
                    handler.postDelayed({
                    AdManager.getInstance()
                        .showInterstitial(
                            requireActivity(),
                            BuildConfig.interstitial_voice_search_category
                        ) {
                            dismissLoadingDialog(progressDialog)

                            startActivity(
                                Intent(
                                    requireContext(),
                                    VoiceSearchCategoryActivity::class.java
                                ).putExtra("category", category)
                            )
                        }
                    }, 1000)

                }
                else{
                    startActivity(
                        Intent(
                            requireContext(),
                            VoiceSearchCategoryActivity::class.java
                        ).putExtra("category", category)
                    )
                }
        } else {
            startActivity(
                Intent(
                    requireContext(),
                    VoiceSearchCategoryActivity::class.java
                ).putExtra("category", category)
            )
        }
//        },500)
    }

    override fun onResume() {
        super.onResume()
        binding.root.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        AdManager.getInstance().currentNativeAd = null
        AdManager.getInstance().interstitialAd = null
    }

    override fun onPause() {
        super.onPause()
        AdManager.getInstance().currentNativeAd = null
        AdManager.getInstance().interstitialAd = null
    }

}