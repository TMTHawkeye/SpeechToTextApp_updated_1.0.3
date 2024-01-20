package org.smrtobjads.ads.ads.models;


import android.view.View;

import com.google.android.gms.ads.nativead.NativeAd;

import org.smrtobjads.ads.adsutils.AdsStatus;

public class AdmobNative extends ApAdBase {
    private int layoutCustomNative;
    private View nativeView;
    private NativeAd admobNativeAd;

    public AdmobNative(AdsStatus status) {
        super(status);
    }

    public AdmobNative(int layoutCustomNative, View nativeView) {
        this.layoutCustomNative = layoutCustomNative;
        this.nativeView = nativeView;
        status = AdsStatus.AD_LOADED;
    }

    public AdmobNative(int layoutCustomNative, NativeAd admobNativeAd) {
        this.layoutCustomNative = layoutCustomNative;
        this.admobNativeAd = admobNativeAd;
        status = AdsStatus.AD_LOADED;
    }

    public NativeAd getAdmobNativeAd() {
        return admobNativeAd;
    }

    public void setAdmobNativeAd(NativeAd admobNativeAd) {
        this.admobNativeAd = admobNativeAd;
        if (admobNativeAd != null)
            status = AdsStatus.AD_LOADED;
    }

    public AdmobNative() {
    }


    @Override
    boolean isReady() {
        return nativeView != null || admobNativeAd != null;
    }


    public int getLayoutCustomNative() {
        return layoutCustomNative;
    }

    public void setLayoutCustomNative(int layoutCustomNative) {
        this.layoutCustomNative = layoutCustomNative;
    }

    public View getNativeView() {
        return nativeView;
    }

    public void setNativeView(View nativeView) {
        this.nativeView = nativeView;
    }

    public String toString(){
        return "Status:"+ status + " == nativeView:"+nativeView + " == admobNativeAd:"+admobNativeAd;
    }

}
