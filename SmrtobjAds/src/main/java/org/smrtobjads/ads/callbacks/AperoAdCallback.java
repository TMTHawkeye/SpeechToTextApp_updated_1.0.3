package org.smrtobjads.ads.callbacks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smrtobjads.ads.ads.models.ApAdError;
import org.smrtobjads.ads.ads.models.ApInterstitialAd;
import org.smrtobjads.ads.ads.models.AdmobNative;
import org.smrtobjads.ads.ads.models.ApRewardItem;

public class AperoAdCallback {
    public void onNextAction() {
    }

    public void onAdClosed() {
    }

    public void onAdFailedToLoad(@Nullable ApAdError adError) {
    }

    public void onAdFailedToShow(@Nullable ApAdError adError) {
    }

    public void onAdLeftApplication() {
    }

    public void onAdLoaded() {

    }

    // ad splash loaded when showSplashIfReady = false
    public void onAdSplashReady() {

    }

    public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {

    }

    public void onAdClicked() {
    }

    public void onAdImpression() {
    }


    public void onNativeAdLoaded(@NonNull AdmobNative nativeAd) {

    }

    public void onUserEarnedReward(@NonNull ApRewardItem rewardItem) {

    }

    public void onInterstitialShow() {

    }
}
