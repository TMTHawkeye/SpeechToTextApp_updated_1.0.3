package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Activities.LaunchrScreen;

import org.jetbrains.annotations.NotNull;
import org.smrtobjads.ads.AdsParent;
import org.smrtobjads.ads.SmartAds;
import org.smrtobjads.ads.ads.AppOpenManager;
import org.smrtobjads.ads.ads.SmartAdsConfig;
import org.smrtobjads.ads.ads.SmartObjAdmob;
import org.smrtobjads.ads.adsutils.StorageCommon;

public class AdsClass extends AdsParent {
//    @NotNull
    public MutableLiveData<Boolean> isAdCloseSplash =new MutableLiveData<Boolean>();


    @Override
    public void onCreate() {
        super.onCreate();
        adsClass = this;
        initAds();
    }

    private void initAds() {
        storageCommons = new StorageCommon();
        AppOpenManager.getInstance().setSplashAdId(BuildConfig.app_open_launcher);
        AppOpenManager.getInstance().init(this, BuildConfig.app_open_others);
        AppOpenManager.getInstance().enableAppResume();
        AppOpenManager.getInstance().disableAppResumeWithActivity(LaunchrScreen.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        String environment = BuildConfig.env_dev ? SmartAdsConfig.ENVIRONMENT_DEVELOP : SmartAdsConfig.ENVIRONMENT_PRODUCTION;
        smartAdsConfig = new SmartAdsConfig(this, SmartAdsConfig.PROVIDER_ADMOB, environment);
        smartAdsConfig.setIdAdResume(BuildConfig.app_open_others);

        SmartAds.getInstance().init(this, smartAdsConfig, false);

        SmartObjAdmob.getInstance().setDisableAdResumeWhenClickAds(true);
        SmartObjAdmob.getInstance().setOpenActivityAfterShowInterAds(false);

        AppOpenManager.getInstance().setResumeAdsRequestLimit(6);
        SmartAds.getInstance().setCountClickToShowAds(2);

        // initBilling();
    }


    /*private void initBilling() {
        List<PurchaseItem> listPurchaseItem = new ArrayList<>();
        listPurchaseItem.add(new PurchaseItem("PRODUCT_ID", AppPurchase.TYPE_IAP.PURCHASE));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITH_FREE_TRIAL", "trial_id", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITHOUT_FREE_TRIAL", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        AppPurchase.getInstance().initBilling(this, listPurchaseItem);

    }*/

    static AdsClass adsClass;

    @NotNull
    public static AdsClass getAdApplication() {
        return adsClass;
    }

    StorageCommon storageCommons;


    public StorageCommon getStorageCommon() {
        return storageCommons;
    }
}