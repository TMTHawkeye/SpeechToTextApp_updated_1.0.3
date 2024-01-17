package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.GuideActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.SplashActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentSplashGetStartedBinding

class SplashFragmentGetStarted : Fragment() {

    var binding: FragmentSplashGetStartedBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashGetStartedBinding.inflate(LayoutInflater.from(context),container,false)

        binding?.nextBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), SplashActivity::class.java))
        }
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as GuideActivity).navigateToPreviousFragment()
            }

        })
    }

}