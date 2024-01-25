package org.smrtobjads.ads

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.smrtobjads.ads.ads.SmartAds
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.callbacks.AperoAdCallback


/*fun loadFbInterstitial(
    context: Context,
    facebookId: String,
    tag: String,
    onAdClosed: (interstitial: Any?) -> Unit,
    onResult: (Any?) -> Unit
) {
    val fbInterstitialAd = InterstitialAd(context, facebookId)
    val interstitialAdListener = object : InterstitialAdListener {

        override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
            p1?.errorMessage?.let { Log.e("error", it) }
        }

        override fun onAdLoaded(p0: Ad?) {
            onResult(fbInterstitialAd)
        }

        override fun onInterstitialDismissed(p0: Ad?) {
            onAdClosed(fbInterstitialAd)
        }

        override fun onInterstitialDisplayed(p0: Ad?) {

        }

        override fun onAdClicked(p0: Ad?) {
        }

        override fun onLoggingImpression(p0: Ad?) {

        }
    }
    fbInterstitialAd.loadAd(
        fbInterstitialAd.buildLoadAdConfig()
            .withAdListener(interstitialAdListener)
            .build()
    )
}*/

private  var loadTimesFailHigh = 0
private  val loadTimesFailMedium = 0
private var loadTimesFailNormal = 0

private const val loadTimesFialedInters = 0
private const val limitLoad = 2

var TAG = "ExFunctions"
private fun loadInterNormal(
    context: Context,
    idAdInterNormal: String,
    adListener: AperoAdCallback) {
    Log.e(TAG, "loadInterNormal: ")
   SmartAds.getInstance().getInterstitialAds(context, idAdInterNormal, object : AperoAdCallback() {
        override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
            super.onInterstitialLoad(interstitialAd)
            adListener.onInterstitialLoad(interstitialAd)
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            super.onAdFailedToLoad(adError)
            Log.e(TAG, "onAdFailedToLoad: Normal")
            if (loadTimesFailNormal < limitLoad) {
                loadTimesFailNormal++
                loadInterNormal(context, idAdInterNormal, adListener)
            }
        }
    })
}

private fun loadInterPriority(
    context: Context,
    idAdInterPriority: String,
    adListener: AperoAdCallback
) {
    Log.e(TAG, "loadInterPriority: ")
    SmartAds.getInstance()
        .getInterstitialAds(context, idAdInterPriority, object : AperoAdCallback() {
            override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                super.onInterstitialLoad(interstitialAd)
                adListener.onInterstitialLoad(interstitialAd)
            }

            override fun onAdFailedToLoad(adError: ApAdError?) {
                super.onAdFailedToLoad(adError)
                Log.e(TAG, "onAdFailedToLoad: Priority")
                if (loadTimesFailHigh < limitLoad) {
                    loadTimesFailHigh++
                    loadInterPriority(context, idAdInterPriority, adListener)
                }
            }
        })
}

fun Context.getInterstitialAdObject(
    tag: String,
    idAdInterPriority: String,
    idAdInterNormal: String,
    periority: Int,
    adListener: AperoAdCallback) {
    val context = this

    loadTimesFailHigh = 0
    loadTimesFailNormal = 0

    when (periority) {
        0 -> {
            loadInterPriority(this, idAdInterNormal, adListener)
        }
        1 -> {
            loadInterNormal(this, idAdInterPriority, adListener)
        }
        2 -> {
            SmartAds.getInstance()
                .getInterstitialAds(context, idAdInterNormal, object : AperoAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        adListener.onInterstitialLoad(interstitialAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        SmartAds.getInstance()
                            .getInterstitialAds(context, idAdInterPriority, object : AperoAdCallback() {
                                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                                    super.onInterstitialLoad(interstitialAd)
                                    adListener.onInterstitialLoad(interstitialAd)
                                }

                                override fun onAdFailedToLoad(adError: ApAdError?) {
                                    super.onAdFailedToLoad(adError)
                                    Log.e(TAG, "onAdFailedToLoad: Priority")
                                    adListener.onAdFailedToLoad(adError)
                                }
                            })
                    }
                })
        }
        3 -> {
            SmartAds.getInstance()
                .getInterstitialAds(context, idAdInterPriority, object : AperoAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        adListener.onInterstitialLoad(interstitialAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        Log.e(TAG, "onAdFailedToLoad: Priority")
                        SmartAds.getInstance()
                            .getInterstitialAds(context, idAdInterNormal, object : AperoAdCallback() {
                                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                                    super.onInterstitialLoad(interstitialAd)
                                    adListener.onInterstitialLoad(interstitialAd)
                                }
                                override fun onAdFailedToLoad(adError: ApAdError?) {
                                    super.onAdFailedToLoad(adError)
                                    Log.e(TAG, "onAdFailedToLoad: Priority")
                                    adListener.onAdFailedToLoad(adError)
                                }
                            })
                    }
                })

        }
    }
}

public fun loadAdmobInterstitialCallback(
    context: Context,
    admobId: String,
    tag: String,
    onAdClosed: (interstitial: Any?) -> Unit,
    onResult: (Any?) -> Unit
) {
    val adRequest: AdRequest = AdRequest.Builder().build()

    InterstitialAd.load(context, admobId, adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
                // an ad is loaded.
                var fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(tag, "Ad was dismissed.")
                        onAdClosed(interstitialAd)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(tag, "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(tag, "Ad showed fullscreen content.")
                    }
                }
                interstitialAd?.fullScreenContentCallback = fullScreenContentCallback
                onResult(interstitialAd)


            }

            override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                onResult(null)
            }
        })
}









