package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
 
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.BuildConfig;
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication;

import org.smrtobjads.ads.AdFuntionsKt;
import org.smrtobjads.ads.SmartAds;
import org.smrtobjads.ads.ads.models.ApAdError;
import org.smrtobjads.ads.ads.models.ApInterstitialAd;
import org.smrtobjads.ads.billings.AppPurchase;
import org.smrtobjads.ads.callbacks.AdCallback;
import org.smrtobjads.ads.callbacks.AperoAdCallback;
 

public class PreloadAdsUtils {
    private static final String TAG = "PreloadAdsUtils";
    private static PreloadAdsUtils instance;

    public static PreloadAdsUtils getInstance() {
        if (instance == null) {
            instance = new PreloadAdsUtils();
        }
        return instance;
    }

    private int loadTimesFailHigh = 0;
    private int loadTimesFailMedium = 0;
    private int loadTimesFailNormal = 0;

    private int loadTimesFialedInters = 0;
    private final int limitLoad = 2;

    public void loadInterSameTime(final Context context, String idAdInterPriority, String idAdInterNormal, AperoAdCallback adListener) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            return;
        }

        loadTimesFailHigh = 0;
        loadTimesFailNormal = 0;
        if (MainApplication.getAdApplication().getStorageCommon().interPriority == null) {
            loadInterPriority(context, idAdInterPriority, adListener);
        }
        if (MainApplication.getAdApplication().getStorageCommon().interNormal == null) {
            loadInterNormal(context, idAdInterNormal, adListener);
        }
    }

    public void loadIntersAlternate(final Context context, String idAdInterPriority, String idAdInterNormal, int interPriority ) {
        if (AppPurchase.getInstance().isPurchased(context) || MainApplication.getAdApplication().getStorageCommon().splashInterstitial != null) {
            return;
        }

        AdFuntionsKt.getInterstitialAdObject(context,"",idAdInterPriority, idAdInterNormal, interPriority, new AperoAdCallback(){
            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                MainApplication.getAdApplication().getStorageCommon().splashInterstitial = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                super.onAdFailedToLoad(adError);
                MainApplication.getAdApplication().getStorageCommon().splashInterstitial = null;
            }

            @Override
            public void onInterstitialShow() {
                super.onInterstitialShow();
            }
        });
        
    }

    private void loadInterNormal(Context context, String idAdInterNormal, AperoAdCallback adListener) {
        Log.e(TAG, "loadInterNormal: ");
        SmartAds.getInstance().getInterstitialAds(context, idAdInterNormal, new AperoAdCallback() {
            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                adListener.onInterstitialLoad(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                super.onAdFailedToLoad(adError);
                Log.e(TAG, "onAdFailedToLoad: Normal");
                if (loadTimesFailNormal < limitLoad) {
                    loadTimesFailNormal++;
                    loadInterNormal(context, idAdInterNormal, adListener);
                }
            }
        });
    }

    private void loadInterPriority(Context context, String idAdInterPriority, AperoAdCallback adListener) {
        Log.e(TAG, "loadInterPriority: ");
        SmartAds.getInstance().getInterstitialAds(context, idAdInterPriority, new AperoAdCallback() {
            @Override
            public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                adListener.onInterPriorityLoaded(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@Nullable ApAdError adError) {
                super.onAdFailedToLoad(adError);
                Log.e(TAG, "onAdFailedToLoad: Priority");
                if (loadTimesFailHigh < limitLoad) {
                    loadTimesFailHigh++;
                    loadInterPriority(context, idAdInterPriority, adListener);
                }
            }
        });
    }

    public void showInterSameTime(
            Context context,
            ApInterstitialAd interPriority,
            ApInterstitialAd interNormal,
            Boolean reload,
            AdsInterCallBack adCallback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adCallback != null) {
                adCallback.onNextAction();
            }
            return;
        }
        if (interPriority != null) {
            Log.e(TAG, "showInterSameTime: Ad priority");
            SmartAds.getInstance().showInterstitialAdByTimes(
                    context,
                    interPriority,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        @Override
                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialPriorityShowed();
                        }
                    },
                    reload);
        } else if (interNormal != null) {
            Log.e(TAG, "showInterSameTime: Ad normal");
            SmartAds.getInstance().showInterstitialAdByTimes(
                    context,
                    interNormal,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();

                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialNormalShowed();
                        }
                    },
                    reload);
        } else {
            adCallback.onNextAction();
        }
    }

    public void showInterAlternateByForce(
            Context context,
            ApInterstitialAd interstitialAd,
            Boolean reload,
            AdsInterCallBack adCallback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adCallback != null) {
                adCallback.onNextAction();
            }
            return;
        }
          if (interstitialAd != null) {
            Log.e(TAG, "showInterSameTime: Ad normal");
            SmartAds.getInstance().forceShowInterstitial(
                    context,
                    interstitialAd,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialShowed();
                        }
                    },
                    reload);
        } else {
              loadIntersAlternate(context, BuildConfig.welcome_Screen_inter, BuildConfig.welcome_Screen_inter, 2);
            adCallback.onNextAction();
        }
    }

    public void showInterAlternateByTime(
            Context context,
            ApInterstitialAd interstitialAd,
            Boolean reload,
            AdsInterCallBack adCallback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adCallback != null) {
                adCallback.onNextAction();
            }
            return;
        }
          if (interstitialAd != null) {
            Log.e(TAG, "showInterSameTime: Ad normal");
            SmartAds.getInstance().showInterstitialAdByTimes(
                    context,
                    interstitialAd,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialShowed();
                        }
                    },
                    reload);
        } else {
              loadIntersAlternate(context, BuildConfig.welcome_Screen_inter, BuildConfig.welcome_Screen_inter, 2);
            adCallback.onNextAction();
        }
    }

    public void showIntersameTimeByForce(
            Context context,
            ApInterstitialAd interPriority,
            ApInterstitialAd interNormal,
            Boolean reload,
            AdsInterCallBack adCallback) {
        if (AppPurchase.getInstance().isPurchased(context)) {
            if (adCallback != null) {
                adCallback.onNextAction();
            }
            return;
        }
        if (interPriority != null) {
            Log.e(TAG, "showInterSameTime: Ad priority");
            SmartAds.getInstance().forceShowInterstitial(
                    context,
                    interPriority,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();
                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        @Override
                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialPriorityShowed();
                        }
                    },
                    reload);
        } else if (interNormal != null) {
            Log.e(TAG, "showInterSameTime: Ad normal");
            SmartAds.getInstance().forceShowInterstitial(
                    context,
                    interNormal,
                    new AperoAdCallback() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            adCallback.onAdClosed();
                        }

                        @Override
                        public void onNextAction() {
                            super.onNextAction();

                            adCallback.onNextAction();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                            adCallback.onAdClicked();
                        }

                        public void onInterstitialShow() {
                            super.onInterstitialShow();
                            adCallback.onInterstitialNormalShowed();
                        }
                    },
                    reload);
        } else {
            adCallback.onNextAction();
        }
    }




}
