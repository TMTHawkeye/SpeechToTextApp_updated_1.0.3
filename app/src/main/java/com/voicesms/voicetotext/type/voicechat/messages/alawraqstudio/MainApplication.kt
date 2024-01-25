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
        FirebaseApp.initializeApp(this);
//        registerActivityLifecycleCallbacks(this)

//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//        appOpenAdManager = AppOpenAdManager()

        Paper.init(applicationContext)


        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule)
        }

        adsClass = this
        initAds()

    }

    /*private void initBilling() {
        List<PurchaseItem> listPurchaseItem = new ArrayList<>();
        listPurchaseItem.add(new PurchaseItem("PRODUCT_ID", AppPurchase.TYPE_IAP.PURCHASE));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITH_FREE_TRIAL", "trial_id", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITHOUT_FREE_TRIAL", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        AppPurchase.getInstance().initBilling(this, listPurchaseItem);

    }*/



//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground() {
//        if(!AdManager.getInstance().isInterstitialShowing) {
//            currentActivity?.let { AdManager.getInstance().showAdIfAvailable(it) }
//        }
//        else{
//            Log.d("TAG", "onMoveToForeground: interstitial is showing so app open will not be shown")
//        }
//    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onMoveToForeground() {
//        if (!AdManager.getInstance().isInterstitialShowing) {
//            currentActivity?.let {
//                if (!isLauncherActivity(it)) {
//                    AdManager.getInstance().showAdIfAvailable(it)
//                } else {
//                    Log.d("TAG", "onMoveToForeground: Launcher activity, ad will not be shown.")
//                }
//            }
//        } else {
//            Log.d("TAG", "onMoveToForeground: Interstitial is showing, so app open will not be shown.")
//        }
//    }

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

//    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
//
//    }
//
//    override fun onActivityStarted(activity: Activity) {
//        if (!AdManager.getInstance().isShowingAd && !AdManager.getInstance().isInterstitialShowing) {
//            currentActivity = activity
//        }
//    }
//    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
//        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
//        // class.
//        AdManager.getInstance().showAdIfAvailable(activity, onShowAdCompleteListener)
//    }
//
//    interface OnShowAdCompleteListener {
//        fun onShowAdComplete()
//    }

//    fun loadAd(activity: Activity) {
//        if(!AdManager.getInstance().isInterstitialShowing) {
//            AdManager.getInstance().loadAd(activity)
//        }
//    }

//    override fun onActivityResumed(p0: Activity) {
//    }
//
//    override fun onActivityPaused(p0: Activity) {
//    }
//
//    override fun onActivityStopped(p0: Activity) {
//    }
//
//    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
//    }
//
//    override fun onActivityDestroyed(p0: Activity) {
//    }


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