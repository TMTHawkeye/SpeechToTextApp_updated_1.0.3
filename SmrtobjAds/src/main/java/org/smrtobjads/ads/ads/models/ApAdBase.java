package org.smrtobjads.ads.ads.models;

import org.smrtobjads.ads.adsutils.AdsStatus;

public abstract class ApAdBase {
    protected AdsStatus status = AdsStatus.AD_INIT;

    public ApAdBase(AdsStatus status) {
        this.status = status;
    }

    public ApAdBase() {
    }

    public AdsStatus getStatus() {
        return status;
    }

    public void setStatus(AdsStatus status) {
        this.status = status;
    }


    abstract boolean isReady();

    public boolean isNotReady(){
        return !isReady();
    }

    public boolean isLoading(){
        return status == AdsStatus.AD_LOADING;
    }
    public boolean isLoadFail(){
        return status == AdsStatus.AD_LOAD_FAIL;
    }
}

