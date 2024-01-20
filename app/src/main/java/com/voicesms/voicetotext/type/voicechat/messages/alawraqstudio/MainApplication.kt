package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DependencyInjection.appModule
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses.AdManager
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import io.paperdb.Paper
import org.jetbrains.annotations.NotNull
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication :Application(),Application.ActivityLifecycleCallbacks,LifecycleObserver {
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
        FirebaseApp.initializeApp(this);
        registerActivityLifecycleCallbacks(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//        appOpenAdManager = AppOpenAdManager()

        Paper.init(applicationContext)


        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule)
        }

    }


//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground() {
//        if(!AdManager.getInstance().isInterstitialShowing) {
//            currentActivity?.let { AdManager.getInstance().showAdIfAvailable(it) }
//        }
//        else{
//            Log.d("TAG", "onMoveToForeground: interstitial is showing so app open will not be shown")
//        }
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        if (!AdManager.getInstance().isInterstitialShowing) {
            currentActivity?.let {
                if (!isLauncherActivity(it)) {
                    AdManager.getInstance().showAdIfAvailable(it)
                } else {
                    Log.d("TAG", "onMoveToForeground: Launcher activity, ad will not be shown.")
                }
            }
        } else {
            Log.d("TAG", "onMoveToForeground: Interstitial is showing, so app open will not be shown.")
        }
    }

    private fun isLauncherActivity(activity: Activity): Boolean {
        val packageName = activity.packageName
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = activity.packageManager
        val resolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)

        for (info in resolveInfoList) {
            if (info.activityInfo.packageName == packageName && info.activityInfo.name == activity.javaClass.name) {
                return true
            }
        }

        return false
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (!AdManager.getInstance().isShowingAd&&!AdManager.getInstance().isInterstitialShowing) {
            currentActivity = activity
        }
    }
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        AdManager.getInstance().showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    fun loadAd(activity: Activity) {
        if(!AdManager.getInstance().isInterstitialShowing) {
            AdManager.getInstance().loadAd(activity)
        }
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }


}