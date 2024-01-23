package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses;


public interface AdsInterCallBack {

    void onInterstitialPriorityShowed();

    void onInterstitialNormalShowed();
    void onInterstitialShowed();

    void onAdClosed();

    void onAdClicked();

    void onNextAction();
}

