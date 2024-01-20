package org.smrtobjads.ads.ads.models;


import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;

import org.smrtobjads.ads.adsutils.AdsStatus;

public class ApRewardAd extends ApAdBase {
    private RewardedAd admobReward;
    private RewardedInterstitialAd admobRewardInter;

    public ApRewardAd() {
    }

    public ApRewardAd(AdsStatus status) {
        super(status);
    }

    public void setAdmobReward(RewardedAd admobReward) {
        this.admobReward = admobReward;
        status = AdsStatus.AD_LOADED;
    }

    public void setAdmobReward(RewardedInterstitialAd admobRewardInter) {
        this.admobRewardInter = admobRewardInter;
    }



    public ApRewardAd(RewardedInterstitialAd admobRewardInter) {
        this.admobRewardInter = admobRewardInter;
        status = AdsStatus.AD_LOADED;
    }

    public ApRewardAd(RewardedAd admobReward) {
        this.admobReward = admobReward;
        status = AdsStatus.AD_LOADED;
    }


    public RewardedAd getAdmobReward() {
        return admobReward;
    }

    public RewardedInterstitialAd getAdmobRewardInter() {
        return admobRewardInter;
    }


    /**
     * Clean reward when shown
     */
    public void clean() {
         admobReward = null;
        admobRewardInter = null;
    }

    @Override
    public boolean isReady() {
        return admobReward != null ||admobRewardInter != null ;
    }

    public boolean isRewardInterstitial(){
        return admobRewardInter != null;
    }
}

