package org.smrtobjads.ads.ads;

import android.app.Application;

import org.smrtobjads.ads.adsutils.AppUtil;
import org.smrtobjads.ads.adsutils.DataStoreUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AdsParent extends Application {

    protected SmartAdsConfig smartAdsConfig;
    protected List<String> listTestDevice ;
    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        smartAdsConfig = new SmartAdsConfig(this);
        if (DataStoreUtils.getInstallTime(this) == 0) {
            DataStoreUtils.setInstallTime(this);
        }
        AppUtil.currentTotalRevenue001Ad = DataStoreUtils.getCurrentTotalRevenue001Ad(this);
    }

}
