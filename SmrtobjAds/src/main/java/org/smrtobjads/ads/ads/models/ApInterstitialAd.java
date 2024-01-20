package org.smrtobjads.ads.ads.models;

import com.google.android.gms.ads.interstitial.InterstitialAd;

import org.smrtobjads.ads.adsutils.AdsStatus;

public class ApInterstitialAd extends ApAdBase{
    private InterstitialAd interstitialAd;

    public ApInterstitialAd(AdsStatus status) {
        super(status);
    }

    public ApInterstitialAd() {
    }

    public ApInterstitialAd(InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
        status = AdsStatus.AD_LOADED;
    }
    public void setInterstitialAd(InterstitialAd interstitialAd) {
        this.interstitialAd = interstitialAd;
        status = AdsStatus.AD_LOADED;
    }



    @Override
    public boolean isReady(){
        return interstitialAd != null;
    }


    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }


}
