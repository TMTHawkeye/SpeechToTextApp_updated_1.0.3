package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import com.google.firebase.FirebaseApp
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DependencyInjection.appModule
//import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : AdsClass() {
    private val localeAppDelegate = LocaleHelperApplicationDelegate()

    private var currentActivity: Activity? = null




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



    }



}