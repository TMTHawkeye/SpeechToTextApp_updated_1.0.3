package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Fragments

 import android.Manifest
import android.Manifest.permission
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.nativead.NativeAd
 import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.BaseActivity
 import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.MainActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.SavedFilesActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.AdsClass
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.RecordingViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.CustomDialogSaveFileBinding
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.databinding.FragmentVoiceRecBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.callbacks.AperoAdCallback
import java.io.File


class VoiceRecFragment : Fragment() {
    lateinit var binding: FragmentVoiceRecBinding
    val m_viewmodel: RecordingViewModel by viewModel()
    var recordingThresh = 0
    lateinit var tempFilePath: String
    lateinit var progressDialog: ProgressDialog
    private val mhandler = Handler()

    private var isRecording = false
    private var startTimeMillis: Long = 0
    private val recordingTimeLimitMillis = 3600000

    var handler = Handler(Looper.getMainLooper())

    private var currentNativeAd: NativeAd? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVoiceRecBinding.inflate(layoutInflater, container, false)
        binding.recordingLottie.visibility = View.GONE
        retainInstance = true
        RecordingScreenNative()
//        loadNativeAd()


//        AdManager.getInstance().loadNativeAd(
//            requireContext(),
//            BuildConfig.native_voice_Rec,
//            binding.adFrame,
//            binding.shimmerViewContainer
//        )


        binding.recordVoice.setOnClickListener {
            checkPermission(permission.RECORD_AUDIO, getString(R.string.mic_permissions).toInt())
        }
        return binding.root
    }

    private fun RecordingScreenNative() {
        SmartAds.getInstance().loadNativeAdResultCallback(requireContext(),
                    BuildConfig.native_voice_Rec,
                    R.layout.native_ad_template,
                    object :
                        AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(
                                requireContext(),
                                nativeAd,
                                binding.adFrame,
                                binding.splashNativeAd.shimmerContainerNative
                            )
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.adFrame.visibility = View.GONE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.adFrame.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()

                        }
                    })


    }


    private fun recordVoice() {
        if (!isRecording) {
            binding.recordingLottie.repeatCount = LottieDrawable.INFINITE
            binding.recordingLottie.playAnimation()
            binding.recordingLottie.visibility = View.VISIBLE

            m_viewmodel.createTempFile {
                if (it != null) {
                    tempFilePath = it
                    Log.d("TAG", "onCreateView: $it")
                    m_viewmodel.beginRecording(it)
                    startTimeMillis = System.currentTimeMillis()
                    (activity as? MainActivity)?.setRecordingStatus(true)
                    isRecording = true
                    binding.recordingStatus.visibility = View.INVISIBLE
                    updateRecordingTime()
                    binding.recordVoice.setImageDrawable(requireContext().getDrawable(R.drawable.stop_recording_icon))
                }
            }
        } else {
//            Toast.makeText(requireContext(), "Stopping....", Toast.LENGTH_SHORT).show()
            stopRecordingDialog()

        }
    }

    override fun onPause() {
        super.onPause()
//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }

    override fun onDestroy() {
        super.onDestroy()
//        AdManager.getInstance().currentNativeAd = null
//        AdManager.getInstance().interstitialAd = null
    }

    private fun updateRecordingTime() {
//        lifecycleScope.launch(Dispatchers.IO) {
        val elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
        val remainingTimeMillis = recordingTimeLimitMillis - elapsedTimeMillis

        val elapsedSeconds = (elapsedTimeMillis / 1000).toInt()
        val remainingSeconds = (remainingTimeMillis / 1000).toInt()

        val elapsedMinutes = elapsedSeconds / 60
        val elapsedSecondsInMinute = elapsedSeconds % 60
        val elapsedMillis = elapsedTimeMillis % 1000

        val remainingMinutes = remainingSeconds / 60
        val remainingSecondsInMinute = remainingSeconds % 60

        val remainingMillis = remainingTimeMillis % 1000

        val formattedElapsedTime = String.format(
            "%02d:%02d:%02d",
            elapsedMinutes,
            elapsedSecondsInMinute,
            elapsedMillis / 10
        )

        val formattedRemainingTime = String.format(
            "%02d:%02d:%02d",
            remainingMinutes,
            remainingSecondsInMinute,
            remainingMillis / 10
        )
        Log.d("TAG", "updateRecordingTime: $elapsedSecondsInMinute")
        recordingThresh = elapsedSecondsInMinute
        binding.recordTime.text = formattedElapsedTime
        binding.recordTimeLimit.text = formattedRemainingTime

        handler.postDelayed({
            if (isRecording) {
                updateRecordingTime()
            }
        }, 100)

        if (elapsedTimeMillis > recordingTimeLimitMillis) {
            requireActivity().runOnUiThread {
                stopRecordingDialog()
            }
        }
//        }


    }

    fun stopRecordingDialog() {

        val pairDialog = showSaveFileDialog()

        val dialog = pairDialog.second
        val binding_dialog = pairDialog.first

        binding_dialog.closeDialog.setOnClickListener {
            dialog.dismiss()
//            val ctxt=(MainActivity::class.java as BaseActivity)
            (requireActivity() as? MainActivity)?.hideNavBar()
//            ctxt.hideNavBar()
        }

        m_viewmodel.stopRecording(isRecording) { isStopped, recorder ->
            if (isStopped!!) {
                recorder?.reset()
                recorder?.release()
                dialog.show()
                isRecording = false
                (requireActivity() as? MainActivity)?.setRecordingStatus(false)
                binding.recordingStatus.text = getString(R.string.status_recording_start)
                binding.recordTime.text = getString(R.string.recordingTime)
                binding.recordTimeLimit.text = getString(R.string.recordingTimeLimit)
                binding.recordVoice.setImageDrawable(requireContext().getDrawable(R.drawable.start_recording_icon))
                binding.recordingLottie.pauseAnimation()
                binding.recordingLottie.visibility = View.INVISIBLE
                binding.recordingStatus.visibility = View.VISIBLE
            } else {
                isRecording = true
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.error_while_stopping_trying_to_stop_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding_dialog.cardSaveFile.setOnClickListener {
            if (binding_dialog.filenameET.text!!.isNotEmpty() || binding_dialog.filenameET.text!!.isNotBlank()) {
                var tempFile = File(tempFilePath)
                val destFile = File(requireContext().getExternalFilesDir("recordings"), "")

                progressDialog = ProgressDialog(requireContext())
                progressDialog.setMessage("Loading Interstitial...")
                progressDialog.setCancelable(false)


                var isSaved = m_viewmodel.saveRecordingToFile(
                    tempFile,
                    destFile,
                    binding_dialog.filenameET.text.toString(),
                    binding_dialog.fileExtTextView.text.toString())
                if (isSaved) {

                    (activity as BaseActivity).forceShowInterstitial(""){
                        startActivity(
                            Intent(
                                requireContext(),
                                SavedFilesActivity::class.java
                            )
                        )
                    }
//                    mhandler.postDelayed({
//                        dismissLoadingDialog(progressDialog)
//                        if (AdManager.getInstance().interstitialAd != null) {
//
//                            lifecycleScope.launch(Dispatchers.Main) {
//                                AdManager.getInstance().showInterstitial(
//                                    requireActivity(),
//                                    BuildConfig.interstitial_voice_rec_save_btn
//                                ) {
//                                    startActivity(
//                                        Intent(
//                                            requireContext(),
//                                            SavedFilesActivity::class.java
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                        else{
//                        startActivity(
//                            Intent(
//                                requireContext(),
//                                SavedFilesActivity::class.java
//                            )
//                        )
////                        }
//                    }, 1000)
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.error_occured_while_saving_the_file_free_up_space_and_try_again),
                        Toast.LENGTH_SHORT
                    ).show()

                }

                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.file_name_cannot_be_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSaveFileDialog(): Pair<CustomDialogSaveFileBinding, Dialog> {
        var binding = CustomDialogSaveFileBinding.inflate(layoutInflater)
        var dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)
//        dialog.show()

        return Pair(binding, dialog)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode == getString(R.string.mic_permissions).toInt()) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recordVoice()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.RECORD_AUDIO
                    )
                ) {
                    // User denied permission, but not permanently (Don't ask again not selected)
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // User denied permission and selected "Don't ask again"
                    showSettingsDialog()
                }
            }
        }
    }

    private fun showSettingsDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_open_settings)

        dialog.findViewById<View>(R.id.btnOpenSettings).setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${requireContext().packageName}")
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(permission),
                requestCode
            )
        } else {
            recordVoice()
        }
    }

    override fun onResume() {
        super.onResume()
    }



}