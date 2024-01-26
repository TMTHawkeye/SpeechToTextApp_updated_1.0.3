package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.LauncherActivity
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DependencyInjection.appModule
//import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import io.paperdb.Paper
import org.jetbrains.annotations.NotNull
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.smrtobjads.ads.ads.AdsParent
import org.smrtobjads.ads.ads.AppOpenManager
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.ads.SmartAdsConfig
import org.smrtobjads.ads.ads.models.SmartObjAdmob
import org.smrtobjads.ads.adsutils.StorageCommon

class MainApplication : AdsParent() {
    private val localeAppDelegate = LocaleHelperApplicationDelegate()
    var isAdCloseSplash = MutableLiveData<Boolean>()

    private var currentActivity: Activity? = null


    companion object {
        private lateinit var adsClass: MainApplication

        @JvmStatic
        @NotNull
        fun getAdApplication(): MainApplication {
            return adsClass
        }
    }

    private lateinit var storageCommon: StorageCommon


    fun getStorageCommon(): StorageCommon {
        return storageCommon
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context =
        LocaleHelper.onAttach(super.getApplicationContext())

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Paper.init(applicationContext)


        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule)
        }

        adsClass = this
        initAds()

    }


    private fun initAds() {
        storageCommon = StorageCommon()
        AppOpenManager.getInstance().setSplashAdId(BuildConfig.app_open_launcher)
        AppOpenManager.getInstance().init(this, BuildConfig.app_open_others)
        AppOpenManager.getInstance().enableAppResume()
        AppOpenManager.getInstance().disableAppResumeWithActivity(LauncherActivity::class.java)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val environment: String
        environment = if (BuildConfig.env_dev) {
            SmartAdsConfig.ENVIRONMENT_DEVELOP
        } else {
            SmartAdsConfig.ENVIRONMENT_PRODUCTION
        }
        smartAdsConfig = SmartAdsConfig(this, SmartAdsConfig.PROVIDER_ADMOB, environment)
        smartAdsConfig.idAdResume = BuildConfig.app_open_others
        SmartAds.getInstance().init(this, smartAdsConfig, false)
        SmartObjAdmob.getInstance().setDisableAdResumeWhenClickAds(true)
        SmartObjAdmob.getInstance().setOpenActivityAfterShowInterAds(false)
        SmartAds.getInstance().setCountClickToShowAds(1)
//        initBilling();
    }


}