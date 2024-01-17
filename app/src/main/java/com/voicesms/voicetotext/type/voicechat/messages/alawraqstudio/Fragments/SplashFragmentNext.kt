package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.GuideActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentSplashNextBinding

class SplashFragmentNext : Fragment() {

    var binding: FragmentSplashNextBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSplashNextBinding.inflate(LayoutInflater.from(context),container,false)

        binding?.nextBtn?.setOnClickListener {
            (activity as GuideActivity).navigateToNextFragment()
        }

        return binding?.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}