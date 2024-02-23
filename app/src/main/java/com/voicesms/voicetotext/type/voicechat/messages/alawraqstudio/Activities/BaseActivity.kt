package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.AdsClass
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.PreloadAdsUtils
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceSearchViewModel
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.smrtobjads.ads.AdsInterCallBack
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    override fun getDelegate() = localeDelegate.getAppCompatDelegate(super.getDelegate())

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)

    }

    override fun onStop() {
        super.onStop()
        hideNavBar()

    }

    override fun onResume() {
        super.onResume()
        hideNavBar()

        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()
        hideNavBar()

        localeDelegate.onPaused()
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocaleHelper.onAttach(context)
    }

    override fun getApplicationContext(): Context =
        localeDelegate.getApplicationContext(super.getApplicationContext())

    open fun updateLocale(context: Activity, locale: Locale) {
        localeDelegate.setLocale(context, locale)
    }

    fun hideNavBar() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
//        insetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        insetsController.hide(WindowInsetsCompat.Type.statusBars())
//        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }



    var isReload = true
    fun showInterstitialAdByTimes(tag: String,onNextAction: (closed: Any?) -> Unit) {

        PreloadAdsUtils.getInstance().showInterAlternateByTime(
            this,
            AdsClass.getAdApplication()!!.getStorageCommon().interstitialAd,
            isReload,
            object : AdsInterCallBack {

                override fun onInterstitialShowed() {
                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interstitialAd = null
                    }
                }

                override fun onAdClosed() {
//                        onNextAction.invoke(true)
                }

                override fun onInterstitialNormalShowed() {
                    Log.e("AdsInterCommon", "onInterstitialNormalShowed: ")

                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interNormal = null
                    }
                }

                override fun onInterstitialPriorityShowed() {
                    Log.e("AdsInterCommon", "onInterstitialPriorityShowed: ")
                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interPriority = null
                    }
                }
                override fun onAdClicked() {
                }

                override fun onNextAction() {
                    onNextAction.invoke(false)
                }
            }
        )

    }
    fun forceShowInterstitial(tag: String, onNextAction: (closed: Any?) -> Unit) {
        PreloadAdsUtils.getInstance().showInterAlternateByForce(this,
            AdsClass.getAdApplication()!!.getStorageCommon().interstitialAd, isReload,
            object : AdsInterCallBack {

                override fun onInterstitialShowed() {
                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interstitialAd = null
                    }
                }
                override fun onAdClosed() {
//                    onNextAction.invoke(true)
                }

                override fun onInterstitialNormalShowed() {
                    Log.e("AdsInterCommon", "onInterstitialNormalShowed: ")

                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interNormal = null
                    }
                }

                override fun onInterstitialPriorityShowed() {
                    Log.e("AdsInterCommon", "onInterstitialPriorityShowed: ")

                    if (!isReload) {
                        AdsClass.getAdApplication()!!.getStorageCommon().interPriority = null
                    }
                }

                override fun onAdClicked() {

                }

                override fun onNextAction() {
                    onNextAction.invoke(false)
                }
            }
        )
    }


}