package com.example.djmixer.objects

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.ump.*
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.MainApplication

class GPDRDJ(var share:SharedPreferences, private val context: Context, private val consentDjCallback: (Boolean) -> Unit) {
    var applicationClass: MainApplication

    private var consentInformation: ConsentInformation? = null
    private var consentForm: ConsentForm? = null

    init {
        initializeConsentInformation()
        applicationClass= MainApplication()
    }
    private fun initializeConsentInformation() {
        consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val debugSettings = ConsentDebugSettings.Builder(context)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .build()

        val parameters = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation!!.requestConsentInfoUpdate(
            context as Activity, parameters,
            {
                if (consentInformation!!.isConsentFormAvailable) {
                    loadConsentForm()
                } else {
                    handleConsentStatus()
                }
            },
            { formError ->
                handleConsentInfoUpdateFailure(formError)
            }
        )
    }
    private fun loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(context,
            { consentForm ->
                this.consentForm = consentForm
                handleConsentStatus()
            },
            { formError ->
                handleConsentFormLoadFailure(formError)
            }
        )
    }
    private fun handleConsentStatus() {
        when (consentInformation!!.consentStatus) {
            ConsentInformation.ConsentStatus.REQUIRED -> showConsentForm()
            ConsentInformation.ConsentStatus.NOT_REQUIRED -> handleNotRequired()
            ConsentInformation.ConsentStatus.OBTAINED -> handleObtained()
            ConsentInformation.ConsentStatus.UNKNOWN -> handleUnknownStatus()
        }
    }

    private fun showConsentForm() {
        consentForm?.show(context as Activity) { formError ->
            handleConsentFormShownResult(formError)
        }
    }

    private fun handleConsentFormShownResult(formError: FormError?) {
        if (formError == null) {
            // Consent form shown successfully, now load the interstitial ad
//            loadInterstitialAd()
            consentDjCallback(true)
        } else {
            Log.e("GDPRUtil", "Consent form show failed: ${formError.message}")
            // Handle failure, e.g., retry or show default content
            consentDjCallback(false)
        }
    }

//    private fun loadInterstitialAd() {
//        if (consentInformation!!.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
//            // Consent already obtained, proceed to load the interstitial ad
//            // Your code to load interstitial ad goes here
//            // For example: AdMob interstitial ad loading code
//            // interstitialAd.loadAd(adRequest)
//            consentDjCallback(true)
//            initializeAds()
//            share.consentPrefernce(true,context)
//        } else {
//            // Consent not obtained, callback with false
//            share.consentPrefernce(false,context)
//            consentDjCallback(false)
//        }
//    }
//    private fun initializeAds() {
//        Eve.getInstance()
//
//        // Initialize and manage app open ads
//        val appOpenClass = AppopneClass(applicationClass)
//        appOpenClass.fetchAd()
//    }


    private fun handleNotRequired() {
        // Handle the case where consent is not required
        consentDjCallback(true)
    }

    private fun handleObtained() {
        // Handle the case where consent has already been obtained
        // You might not need to load the interstitial ad here, as it's already obtained
        consentDjCallback(true)
    }

    private fun handleUnknownStatus() {
        // Handle unknown status
        consentDjCallback(false)
    }

    private fun handleConsentInfoUpdateFailure(formError: FormError) {
        Log.e("GDPRUtil", "Consent info update failed: ${formError.message}")
        // Handle failure, e.g., retry or show default content
        consentDjCallback(false)
    }

    private fun handleConsentFormLoadFailure(formError: FormError) {
        Log.e("GDPRUtil", "Consent form load failed: ${formError.message}")
        // Handle failure, e.g., retry or show default content
        consentDjCallback(false)
    }
}
