package org.smrtobjads.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class FirebaseAnalyticsUtil {
    private static final String TAG = "FirebaseAnalyticsUtil";

  /*  public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName) {
        logEventWithAds(context, (float) adValue.getValueMicros(), adValue.getPrecisionType(), adUnitId, mediationAdapterClassName);
    }

    public static void logPaidAdImpression(Context context, MaxAd adValue) {
        logEventWithAds(context, (float) adValue.getRevenue(), 0, adValue.getAdUnitId(), adValue.getNetworkName());
    }
*/

    public static void logEventWithAds(Context context, Bundle params) {
//        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression", params);
    }

    public static void logPaidAdImpressionValue(Context context, Bundle bundle, int mediationProvider) {
        /*if (mediationProvider == AperoAdConfig.PROVIDER_MAX)
            FirebaseAnalytics.getInstance(context).logEvent("max_paid_ad_impression_value", bundle);
        else
            FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression_value", bundle);*/
    }

    public static void logClickAdsEvent(Context context, Bundle bundle) {
//        FirebaseAnalytics.getInstance(context).logEvent("event_user_click_ads", bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName, Bundle bundle) {
//        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void logTotalRevenue001Ad(Context context, Bundle bundle) {
//        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression_value_001", bundle);
    }

    public static void logEventTracking(Activity activity, String umpConsentResult, Bundle loadAndShowError1) {

    }
}