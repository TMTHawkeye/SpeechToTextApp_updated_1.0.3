package org.smrtobjads.ads;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import org.smrtobjads.ads.ads.SmartAds;

import java.util.concurrent.atomic.AtomicBoolean;

public class AdsConsentManager {

    private ConsentInformation consentInformation;
    private Activity activity;
    private final AtomicBoolean isMobileAdsInitializeCalled;

    public AdsConsentManager(Activity activity) {
        AdsConsentManager var10000 = this;
        AdsConsentManager var10001 = this;

         isMobileAdsInitializeCalled = new AtomicBoolean(false);;
        var10000.activity = activity;
    }

    public static boolean getConsentResult(Context context) {
        String context1;
        String conscentResult = context1 = context.getSharedPreferences(context.getPackageName() + "_preferences", 0).getString("IABTCF_PurposeConsents", "");
        Log.d("d", "consentResult: " + context1);
        return !conscentResult.isEmpty() ? String.valueOf(context1.charAt(0)).equals("1") : true;
    }

    public void requestUMP(UMPResultListener umpResultListener, Boolean enableDebug) {
//        Boolean enableDebug = BuildConfig.DEBUG;//Boolean.TRUE;
        this.requestUMP(enableDebug, "", enableDebug, umpResultListener);
    }

    public void requestUMP(Boolean enableDebug, String testDevice, Boolean resetData, UMPResultListener umpResultListener) {
        Boolean isDebugMode = enableDebug;
        ConsentRequestParameters.Builder concentReqBuilder;
        concentReqBuilder = new ConsentRequestParameters.Builder();

        if (isDebugMode) {
            concentReqBuilder
                    .setConsentDebugSettings((new ConsentDebugSettings.Builder(this.activity))
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(testDevice).build());
        }

        ConsentRequestParameters requestParameters = concentReqBuilder.setTagForUnderAgeOfConsent(false).build();
        this.consentInformation = UserMessagingPlatform.getConsentInformation(this.activity);
        if (resetData) {
            this.consentInformation.reset();
        }

        ConsentInformation.OnConsentInfoUpdateSuccessListener infoUpdateSuccessListener = () -> {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this.activity, (loadAndShowError) -> {
                if (loadAndShowError != null) {
                    Object[] var3;
                    Object[] var10002 = var3 = new Object[2];
                    var3[0] = loadAndShowError.getErrorCode();
                    var10002[1] = loadAndShowError.getMessage();
                    Log.w("var10001", String.format("%s: %s", var3));
                    Bundle var5;
                    Bundle var6 = var5 = new Bundle();

                    var5.putInt("error_code", loadAndShowError.getErrorCode());
                    var6.putString("error_msg", loadAndShowError.getMessage());
                    FirebaseAnalyticsUtil.logEventTracking(this.activity, "ump_consent_failed", var5);
                }

                Bundle loadAndShowError1;
                Bundle var7 = loadAndShowError1 = new Bundle();

                var7.putBoolean("consent", getConsentResult(this.activity));
                FirebaseAnalyticsUtil.logEventTracking(this.activity, "ump_consent_result", loadAndShowError1);
                if (!this.isMobileAdsInitializeCalled.getAndSet(true)) {
                    umpResultListener.onCheckUMPSuccess(getConsentResult(this.activity));
                    SmartAds.getInstance().initAdsNetwork();
                }
            });
        };
        ConsentInformation.OnConsentInfoUpdateFailureListener updateFailureListener = (requestConsentError) -> {

            Object[] var3;
            Object[] var10003 = var3 = new Object[2];
            var3[0] = requestConsentError.getErrorCode();
            var10003[1] = requestConsentError.getMessage();
            Log.w("var10002", String.format("%s: %s", var3));

            Bundle bundle  = new Bundle();

            bundle.putInt("error_code", requestConsentError.getErrorCode());
            bundle.putString("error_msg", requestConsentError.getMessage());
            FirebaseAnalyticsUtil.logEventTracking(this.activity, "ump_request_failed", bundle);
            if (!this.isMobileAdsInitializeCalled.getAndSet(true)) {
                umpResultListener.onCheckUMPSuccess(getConsentResult(this.activity));
                SmartAds.getInstance().initAdsNetwork();
            }

        };
        consentInformation.requestConsentInfoUpdate(activity, requestParameters, infoUpdateSuccessListener, updateFailureListener);
        if (this.consentInformation.canRequestAds() && !this.isMobileAdsInitializeCalled.getAndSet(true)) {
            umpResultListener.onCheckUMPSuccess(getConsentResult(this.activity));
            SmartAds.getInstance().initAdsNetwork();
        }
    }

    public void showPrivacyOption(Activity activity, UMPResultListener umpResultListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, (formError) -> {
            if (formError != null) {

                Object[] var3;
                Object[] var10002 = var3 = new Object[2];
                var3[0] = formError.getErrorCode();
                var10002[1] = formError.getMessage();
                Log.w("var10001", String.format("%s: %s", var3));
                Bundle formError1;
                formError1 = new Bundle();
                FirebaseAnalyticsUtil.logEventTracking(activity, "ump_consent_failed", formError1);
            }

            if (getConsentResult(activity)) {
                SmartAds.getInstance().initAdsNetwork();
            }

            UMPResultListener var10000 = umpResultListener;
            Bundle umpResultListener1;
            Bundle var10003 = umpResultListener1 = new Bundle();

            var10003.putBoolean("consent", getConsentResult(activity));
            FirebaseAnalyticsUtil.logEventTracking(activity, "ump_consent_result", umpResultListener1);
            var10000.onCheckUMPSuccess(getConsentResult(activity));
        });
    }
}

