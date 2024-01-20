package org.smrtobjads.ads.ads;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.smrtobjads.ads.R;

public class AdsLoadingDialoge extends Dialog {

    public AdsLoadingDialoge(Context context) {
        super(context, R.style.AppTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_loading_dialog);
    }

    public void hideLoadingAdsText() {
        findViewById(R.id.loading_dialog_tv).setVisibility(View.INVISIBLE);
    }
}
