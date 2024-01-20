package org.smrtobjads.ads;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;

import org.smrtobjads.ads.callbacks.AdCallback;
import org.smrtobjads.ads.ads.SmartObjAdmob;
import org.smrtobjads.ads.ads.SmartAdsConfig;
import org.smrtobjads.ads.ads.AppOpenManager;
import org.smrtobjads.ads.ads.models.ApAdError;
import org.smrtobjads.ads.ads.models.ApInterstitialAd;
import org.smrtobjads.ads.ads.models.AdmobNative;
import org.smrtobjads.ads.ads.models.ApRewardAd;
import org.smrtobjads.ads.ads.models.ApRewardItem;
import org.smrtobjads.ads.billings.AppPurchase;
import org.smrtobjads.ads.adsutils.AppUtil;
import org.smrtobjads.ads.callbacks.RewardCallback;
import org.smrtobjads.ads.adsutils.DataStoreUtils;
import org.smrtobjads.ads.callbacks.AperoAdCallback;
import org.smrtobjads.ads.callbacks.AperoInitCallback;

import java.util.concurrent.atomic.AtomicBoolean;

public class SmartAds {

    public static final String TAG = "SmartAds";
    private static volatile SmartAds INSTANCE;
    private SmartAdsConfig adConfig;
    private AperoInitCallback initCallback;
    private Boolean initAdSuccess = false;

    public static synchronized SmartAds getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SmartAds();
        }
        return INSTANCE;
    }

    public SmartAds(){
        isMobileAdsInitializeCalled = new AtomicBoolean(false);
    }

    /**
     * Set count click to show ads interstitial when call showInterstitialAdByTimes()
     *
     * @param countClickToShowAds - default = 3
     */
    public void setCountClickToShowAds(int countClickToShowAds) {
        SmartObjAdmob.getInstance().setNumToShowAds(countClickToShowAds);

    }


    public void setCountClickToShowAds(int countClickToShowAds, int currentClicked) {
        SmartObjAdmob.getInstance().setNumToShowAds(countClickToShowAds, currentClicked);
    }
    private final AtomicBoolean isMobileAdsInitializeCalled;

    public void initAdsNetwork() {
        if (!this.isMobileAdsInitializeCalled.getAndSet(true)) {
            if (adConfig == null) {
                throw new RuntimeException("cant not set SmartAdsConfig null");
            }
            this.adConfig = adConfig;
            AppUtil.VARIANT_DEV = adConfig.isVariantDev();
            Log.i(TAG, "Config variant dev: " + AppUtil.VARIANT_DEV);

            SmartObjAdmob.getInstance().init(adConfig.getApplication().getApplicationContext(), adConfig.getListDeviceTest());
            if (adConfig.isEnableAdResume()) {
                AppOpenManager.getInstance().init(adConfig.getApplication(), adConfig.getIdAdResume());
            }
        }
    }

    public void init(Application context, SmartAdsConfig adConfig) {
        init(context, adConfig, false);
    }

    /**
     * @param context
     * @param adConfig             SmartAdsConfig object used for SDK initialisation
     * @param enableDebugMediation set show Mediation Debugger - use only for Max Mediation
     */
    public void init(Application context, SmartAdsConfig adConfig, Boolean enableDebugMediation) {
        if (adConfig == null ) {
            throw new RuntimeException("cant not set SmartAdsConfig null");
        }
        this.adConfig = adConfig;
        AppUtil.VARIANT_DEV = adConfig.isVariantDev();
        Log.i(TAG, "Config variant dev: " + AppUtil.VARIANT_DEV);

        SmartObjAdmob.getInstance().init(context, adConfig.getListDeviceTest());
        if (adConfig.isEnableAdResume()) {
            AppOpenManager.getInstance().init(adConfig.getApplication(), adConfig.getIdAdResume());
        }

        initAdSuccess = true;
        if (initCallback != null)
            initCallback.initAdSuccess();
    }

    public int getMediationProvider() {
        return adConfig.getMediationProvider();
    }

    public void setInitCallback(AperoInitCallback initCallback) {
        this.initCallback = initCallback;
        if (initAdSuccess)
            initCallback.initAdSuccess();
    }

    public SmartAdsConfig getAdConfig() {
        return adConfig;
    }

    public void loadBanner(final Activity mActivity, String id) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadBanner(mActivity, id);
                break;

        }
    }

    public void loadBanner(final Activity mActivity, String id, final AperoAdCallback adCallback) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadBanner(mActivity, id, new AdCallback() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adCallback.onAdLoaded();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        adCallback.onAdClicked();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        adCallback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        adCallback.onAdImpression();
                    }
                });
                break;

        }
    }

    public void loadCollapsibleBanner(final Activity activity, String id, String gravity, AdCallback adCallback) {
        SmartObjAdmob.getInstance().loadCollapsibleBanner(activity, id, gravity, adCallback);
    }

    public void loadBannerFragment(final Activity mActivity, String id, final View rootView) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadBannerFragment(mActivity, id, rootView);
                break;

        }
    }

    public void loadBannerFragment(final Activity mActivity, String id, final View rootView, final AdCallback adCallback) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadBannerFragment(mActivity, id, rootView, adCallback);
                break;

        }
    }

    public void loadCollapsibleBannerFragment(final Activity mActivity, String id, final View rootView, String gravity, AdCallback adCallback) {
        SmartObjAdmob.getInstance().loadCollapsibleBannerFragment(mActivity, id, rootView, gravity, adCallback);
    }

    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, AperoAdCallback adListener) {
        loadSplashInterstitialAds(context, id, timeOut, timeDelay, true, adListener);
    }

    public void loadSplashInterstitialAds(final Context context, String id, long timeOut, long timeDelay, boolean showSplashIfReady, AperoAdCallback adListener) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadSplashInterstitialAds(context, id, timeOut, timeDelay, showSplashIfReady, new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        adListener.onAdClosed();
                    }

                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        adListener.onNextAction();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        adListener.onAdFailedToLoad(new ApAdError(i));

                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        adListener.onAdFailedToShow(new ApAdError(adError));

                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adListener.onAdLoaded();
                    }

                    @Override
                    public void onAdSplashReady() {
                        super.onAdSplashReady();
                        adListener.onAdSplashReady();
                    }


                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        if (adListener != null) {
                            adListener.onAdClicked();
                        }
                    }
                });
                break;

        }
    }

    public void onShowSplash(AppCompatActivity activity, AperoAdCallback adListener) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().onShowSplash(activity, new AdCallback() {
                            @Override
                            public void onAdFailedToShow(@Nullable AdError adError) {
                                super.onAdFailedToShow(adError);
                                adListener.onAdFailedToShow(new ApAdError(adError));
                            }

                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                adListener.onAdClosed();
                            }

                            @Override
                            public void onNextAction() {
                                super.onNextAction();
                                adListener.onNextAction();
                            }


                        }
                );
                break;


        }
    }


    public void onCheckShowSplashWhenFail(AppCompatActivity activity, AperoAdCallback callback,
                                          int timeDelay) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().onCheckShowSplashWhenFail(activity, new AdCallback() {
                    @Override
                    public void onNextAction() {
                        super.onAdClosed();
                        callback.onNextAction();
                    }


                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        callback.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        callback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        callback.onAdFailedToShow(new ApAdError(adError));
                    }
                }, timeDelay);
                break;

        }
    }


    public ApInterstitialAd getInterstitialAds(Context context, String id, AperoAdCallback adListener) {
        ApInterstitialAd apInterstitialAd = new ApInterstitialAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().getInterstitialAds(context, id, new AdCallback() {
                    @Override
                    public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);
                        Log.d(TAG, "Admob onInterstitialLoad");
                        apInterstitialAd.setInterstitialAd(interstitialAd);
                        adListener.onInterstitialLoad(apInterstitialAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        adListener.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        adListener.onAdFailedToShow(new ApAdError(adError));
                    }

                });
                return apInterstitialAd;

            default:
                return apInterstitialAd;
        }
    }


    public ApInterstitialAd getInterstitialAds(Context context, String id) {
        ApInterstitialAd apInterstitialAd = new ApInterstitialAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().getInterstitialAds(context, id, new AdCallback() {
                    @Override
                    public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);
                        Log.d(TAG, "Admob onInterstitialLoad: ");
                        apInterstitialAd.setInterstitialAd(interstitialAd);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                    }

                });
                return apInterstitialAd;

            default:
                return apInterstitialAd;
        }
    }


    public void forceShowInterstitial(Context context, ApInterstitialAd mInterstitialAd,
                                      final AperoAdCallback callback) {
        forceShowInterstitial(context, mInterstitialAd, callback, false);
    }


    public void forceShowInterstitial(@NonNull Context context, ApInterstitialAd mInterstitialAd,
                                      @NonNull final AperoAdCallback callback, boolean shouldReloadAds) {
        if (System.currentTimeMillis() - DataStoreUtils.getLastImpressionInterstitialTime(context)
                < SmartAds.getInstance().adConfig.getIntervalInterstitialAd() * 1000L
        ) {
            Log.i(TAG, "forceShowInterstitial: ignore by interval impression interstitial time");
            callback.onNextAction();
            return;
        }
        if (mInterstitialAd == null || mInterstitialAd.isNotReady()) {
            Log.e(TAG, "forceShowInterstitial: ApInterstitialAd is not ready");
            callback.onNextAction();
            return;
        }
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                AdCallback adCallback = new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d(TAG, "onAdClosed: ");
                        callback.onAdClosed();
                        if (shouldReloadAds) {
                            SmartObjAdmob.getInstance().getInterstitialAds(context, mInterstitialAd.getInterstitialAd().getAdUnitId(), new AdCallback() {
                                @Override
                                public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                                    super.onInterstitialLoad(interstitialAd);
                                    Log.d(TAG, "Admob shouldReloadAds success");
                                    mInterstitialAd.setInterstitialAd(interstitialAd);
                                    callback.onInterstitialLoad(mInterstitialAd);
                                }

                                @Override
                                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                                    super.onAdFailedToLoad(i);
                                    mInterstitialAd.setInterstitialAd(null);
                                    callback.onAdFailedToLoad(new ApAdError(i));
                                }

                                @Override
                                public void onAdFailedToShow(@Nullable AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    callback.onAdFailedToShow(new ApAdError(adError));
                                }

                            });
                        } else {
                            mInterstitialAd.setInterstitialAd(null);
                        }
                    }

                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        Log.d(TAG, "onNextAction: ");
                        callback.onNextAction();
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        Log.d(TAG, "onAdFailedToShow: ");
                        callback.onAdFailedToShow(new ApAdError(adError));
                        if (shouldReloadAds) {
                            SmartObjAdmob.getInstance().getInterstitialAds(context, mInterstitialAd.getInterstitialAd().getAdUnitId(), new AdCallback() {
                                @Override
                                public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                                    super.onInterstitialLoad(interstitialAd);
                                    Log.d(TAG, "Admob shouldReloadAds success");
                                    mInterstitialAd.setInterstitialAd(interstitialAd);
                                    callback.onInterstitialLoad(mInterstitialAd);
                                }

                                @Override
                                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                                    super.onAdFailedToLoad(i);
                                    callback.onAdFailedToLoad(new ApAdError(i));
                                }

                                @Override
                                public void onAdFailedToShow(@Nullable AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    callback.onAdFailedToShow(new ApAdError(adError));
                                }

                            });
                        } else {
                            mInterstitialAd.setInterstitialAd(null);
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        callback.onAdClicked();
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                        callback.onInterstitialShow();
                    }
                };
                SmartObjAdmob.getInstance().forceShowInterstitial(context, mInterstitialAd.getInterstitialAd(), adCallback);
                break;

        }
    }

    /**
     * Called force show ApInterstitialAd when reach the number of clicks show ads
     *
     * @param context
     * @param mInterstitialAd
     * @param callback
     * @param shouldReloadAds auto reload ad when ad close
     */
    public void showInterstitialAdByTimes(Context context, ApInterstitialAd mInterstitialAd,
                                          final AperoAdCallback callback, boolean shouldReloadAds) {
        if (mInterstitialAd.isNotReady()) {
            Log.e(TAG, "forceShowInterstitial: ApInterstitialAd is not ready");
            callback.onAdFailedToShow(new ApAdError("ApInterstitialAd is not ready"));
            return;
        }
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                AdCallback adCallback = new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d(TAG, "onAdClosed: ");
                        callback.onAdClosed();
                        if (shouldReloadAds) {
                            SmartObjAdmob.getInstance().getInterstitialAds(context, mInterstitialAd.getInterstitialAd().getAdUnitId(), new AdCallback() {
                                @Override
                                public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                                    super.onInterstitialLoad(interstitialAd);
                                    Log.d(TAG, "Admob shouldReloadAds success");
                                    mInterstitialAd.setInterstitialAd(interstitialAd);
                                    callback.onInterstitialLoad(mInterstitialAd);
                                }

                                @Override
                                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                                    super.onAdFailedToLoad(i);
                                    mInterstitialAd.setInterstitialAd(null);
                                    callback.onAdFailedToLoad(new ApAdError(i));
                                }

                                @Override
                                public void onAdFailedToShow(@Nullable AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    callback.onAdFailedToShow(new ApAdError(adError));
                                }

                            });
                        } else {
                            mInterstitialAd.setInterstitialAd(null);
                        }
                    }

                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        Log.d(TAG, "onNextAction: ");
                        callback.onNextAction();
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        Log.d(TAG, "onAdFailedToShow: ");
                        callback.onAdFailedToShow(new ApAdError(adError));
                        if (shouldReloadAds) {
                            SmartObjAdmob.getInstance().getInterstitialAds(context, mInterstitialAd.getInterstitialAd().getAdUnitId(), new AdCallback() {
                                @Override
                                public void onInterstitialLoad(@Nullable InterstitialAd interstitialAd) {
                                    super.onInterstitialLoad(interstitialAd);
                                    Log.d(TAG, "Admob shouldReloadAds success");
                                    mInterstitialAd.setInterstitialAd(interstitialAd);
                                    callback.onInterstitialLoad(mInterstitialAd);
                                }

                                @Override
                                public void onAdFailedToLoad(@Nullable LoadAdError i) {
                                    super.onAdFailedToLoad(i);
                                    callback.onAdFailedToLoad(new ApAdError(i));
                                }

                                @Override
                                public void onAdFailedToShow(@Nullable AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    callback.onAdFailedToShow(new ApAdError(adError));
                                }

                            });
                        } else {
                            mInterstitialAd.setInterstitialAd(null);
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        if (callback != null) {
                            callback.onAdClicked();
                        }
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                        if (callback != null) {
                            callback.onInterstitialShow();
                        }
                    }
                };
                SmartObjAdmob.getInstance().showInterstitialAdByTimes(context, mInterstitialAd.getInterstitialAd(), adCallback);
                break;

        }
    }

    public void loadNativeAd(final Activity activity, String id,
                             int layoutCustomNative) {
        FrameLayout adPlaceHolder = activity.findViewById(R.id.fl_adplaceholder);
        ShimmerFrameLayout containerShimmerLoading = activity.findViewById(R.id.shimmer_container_native);

        if (AppPurchase.getInstance().isPurchased(activity)) {
            if (containerShimmerLoading != null) {
                containerShimmerLoading.stopShimmer();
                containerShimmerLoading.setVisibility(View.GONE);
            }
            return;
        }
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadNativeAd(((Context) activity), id, new AdCallback() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        populateNativeAdView(activity, new AdmobNative(layoutCustomNative, unifiedNativeAd), adPlaceHolder, containerShimmerLoading);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        Log.e(TAG, "onAdFailedToLoad : NativeAd");
                    }
                });
                break;

        }
    }

    /**
     * Load native ad and auto populate ad to adPlaceHolder and hide containerShimmerLoading
     *
     * @param activity
     * @param id
     * @param layoutCustomNative
     * @param adPlaceHolder
     * @param containerShimmerLoading
     */
    public void loadNativeAd(final Activity activity, String id,
                             int layoutCustomNative, FrameLayout adPlaceHolder, ShimmerFrameLayout
                                     containerShimmerLoading) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadNativeAd(((Context) activity), id, new AdCallback() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        populateNativeAdView(activity, new AdmobNative(layoutCustomNative, unifiedNativeAd), adPlaceHolder, containerShimmerLoading);
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        Log.e(TAG, "onAdFailedToLoad : NativeAd");
                    }
                });
                break;

        }
    }

    /**
     * Load native ad and auto populate ad to adPlaceHolder and hide containerShimmerLoading
     *
     * @param activity
     * @param id
     * @param layoutCustomNative
     * @param adPlaceHolder
     * @param containerShimmerLoading
     */
    public void loadNativeAd(final Activity activity, String id,
                             int layoutCustomNative, FrameLayout adPlaceHolder, ShimmerFrameLayout
                                     containerShimmerLoading, AperoAdCallback callback) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadNativeAd(((Context) activity), id, new AdCallback() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        callback.onNativeAdLoaded(new AdmobNative(layoutCustomNative, unifiedNativeAd));
                        populateNativeAdView(activity, new AdmobNative(layoutCustomNative, unifiedNativeAd), adPlaceHolder, containerShimmerLoading);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        callback.onAdImpression();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        callback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        callback.onAdFailedToShow(new ApAdError(adError));
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        callback.onAdClicked();
                    }
                });
                break;

        }
    }

    /**
     * Result a ApNativeAd in onUnifiedNativeAdLoaded when native ad loaded
     *
     * @param activity
     * @param id
     * @param layoutCustomNative
     * @param callback
     */
    public void loadNativeAdResultCallback(final Context activity, String id,
                                           int layoutCustomNative, AperoAdCallback callback) {
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().loadNativeAd(((Context) activity), id, new AdCallback() {
                    @Override
                    public void onUnifiedNativeAdLoaded(@NonNull NativeAd unifiedNativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd);
                        callback.onNativeAdLoaded(new AdmobNative(layoutCustomNative, unifiedNativeAd));
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable LoadAdError i) {
                        super.onAdFailedToLoad(i);
                        callback.onAdFailedToLoad(new ApAdError(i));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable AdError adError) {
                        super.onAdFailedToShow(adError);
                        callback.onAdFailedToShow(new ApAdError(adError));
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        callback.onAdClicked();
                    }
                });
                break;

        }
    }

    /**
     * Populate Unified Native Ad to View
     *
     * @param activity
     * @param admobNative
     * @param adPlaceHolder
     * @param containerShimmerLoading
     */
    public void populateNativeAdView(Context activity, AdmobNative admobNative, FrameLayout
            adPlaceHolder, ShimmerFrameLayout containerShimmerLoading) {
        if (admobNative.getAdmobNativeAd() == null && admobNative.getNativeView() == null) {
            containerShimmerLoading.setVisibility(View.GONE);
            Log.e(TAG, "populateNativeAdView failed : native is not loaded ");
            return;
        }
        @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) LayoutInflater.from(activity).inflate(admobNative.getLayoutCustomNative(), null);
        containerShimmerLoading.stopShimmer();
        containerShimmerLoading.setVisibility(View.GONE);
        adPlaceHolder.setVisibility(View.VISIBLE);
        SmartObjAdmob.getInstance().populateUnifiedNativeAdView(admobNative.getAdmobNativeAd(), adView);
        adPlaceHolder.removeAllViews();
        adPlaceHolder.addView(adView);
    }


    public ApRewardAd getRewardAd(Context activity, String id) {
        ApRewardAd apRewardAd = new ApRewardAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().initRewardAds(activity, id, new AdCallback() {

                    @Override
                    public void onRewardAdLoaded(RewardedAd rewardedAd) {
                        super.onRewardAdLoaded(rewardedAd);
                        Log.i(TAG, "getRewardAd AdLoaded: ");
                        apRewardAd.setAdmobReward(rewardedAd);
                    }
                });
                break;

        }
        return apRewardAd;
    }

    public ApRewardAd getRewardAdInterstitial(Context activity, String id) {
        ApRewardAd apRewardAd = new ApRewardAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().getRewardInterstitial(activity, id, new AdCallback() {

                    @Override
                    public void onRewardAdLoaded(RewardedInterstitialAd rewardedAd) {
                        super.onRewardAdLoaded(rewardedAd);
                        Log.i(TAG, "getRewardAdInterstitial AdLoaded: ");
                        apRewardAd.setAdmobReward(rewardedAd);
                    }
                });
                break;

        }
        return apRewardAd;
    }

    public ApRewardAd getRewardAd(Context activity, String id, AperoAdCallback callback) {
        ApRewardAd apRewardAd = new ApRewardAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().initRewardAds(activity, id, new AdCallback() {
                    @Override
                    public void onRewardAdLoaded(RewardedAd rewardedAd) {
                        super.onRewardAdLoaded(rewardedAd);
                        apRewardAd.setAdmobReward(rewardedAd);
                        callback.onAdLoaded();
                    }
                });
                return apRewardAd;

        }
        return apRewardAd;
    }

    public ApRewardAd getRewardInterstitialAd(Context activity, String id, AperoAdCallback callback) {
        ApRewardAd apRewardAd = new ApRewardAd();
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                SmartObjAdmob.getInstance().getRewardInterstitial(activity, id, new AdCallback() {
                    @Override
                    public void onRewardAdLoaded(RewardedInterstitialAd rewardedAd) {
                        super.onRewardAdLoaded(rewardedAd);
                        apRewardAd.setAdmobReward(rewardedAd);
                        callback.onAdLoaded();
                    }
                });
                return apRewardAd;

        }
        return apRewardAd;
    }

    public void forceShowRewardAd(Activity activity, ApRewardAd apRewardAd, AperoAdCallback
            callback) {
        if (!apRewardAd.isReady()) {
            Log.e(TAG, "forceShowRewardAd fail: reward ad not ready");
            callback.onNextAction();
            return;
        }
        switch (adConfig.getMediationProvider()) {
            case SmartAdsConfig.PROVIDER_ADMOB:
                if (apRewardAd.isRewardInterstitial()) {
                    SmartObjAdmob.getInstance().showRewardInterstitial(activity, apRewardAd.getAdmobRewardInter(), new RewardCallback() {

                        @Override
                        public void onUserEarnedReward(RewardItem var1) {
                            callback.onUserEarnedReward(new ApRewardItem(var1));
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            apRewardAd.clean();
                            callback.onNextAction();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int codeError) {
                            apRewardAd.clean();
                            callback.onAdFailedToShow(new ApAdError(new AdError(codeError, "note msg", "Reward")));
                        }

                        @Override
                        public void onAdClicked() {
                            if (callback != null) {
                                callback.onAdClicked();
                            }
                        }
                    });
                } else {
                    SmartObjAdmob.getInstance().showRewardAds(activity, apRewardAd.getAdmobReward(), new RewardCallback() {

                        @Override
                        public void onUserEarnedReward(RewardItem var1) {
                            callback.onUserEarnedReward(new ApRewardItem(var1));
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            apRewardAd.clean();
                            callback.onNextAction();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int codeError) {
                            apRewardAd.clean();
                            callback.onAdFailedToShow(new ApAdError(new AdError(codeError, "note msg", "Reward")));
                        }

                        @Override
                        public void onAdClicked() {
                            if (callback != null) {
                                callback.onAdClicked();
                            }
                        }
                    });
                }
                break;

        }
    }


}

