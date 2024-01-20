package org.smrtobjads.ads.adsutils;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.ads.nativead.NativeAd;

import org.smrtobjads.ads.ads.models.ApInterstitialAd;
import org.smrtobjads.ads.ads.models.AdmobNative;

public class StorageCommon {
    public MutableLiveData<AdmobNative> nativeAdsLanguage = new MutableLiveData<>();
    public ApInterstitialAd interPriority;
    public ApInterstitialAd interNormal;
    public AdmobNative admobNativeHigh;
    public AdmobNative admobNativeMedium;
    public AdmobNative admobNativeNormal;
    public NativeAd nativeAdHigh;
    public NativeAd nativeAdMedium;
    public NativeAd nativeAdNormal;
    public MutableLiveData<AdmobNative> welcomeNative;
}