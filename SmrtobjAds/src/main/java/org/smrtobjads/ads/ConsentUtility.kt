//package org.smrtobjads.ads
//
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import com.google.android.ump.*
//
//class ConsentUtility(private val context: Context) {
//
//    private var consentInformation: ConsentInformation? = null
//    private var consentForm: ConsentForm? = null
//
//    fun initializeConsentInformation() {
//        consentInformation = UserMessagingPlatform.getConsentInformation(context)
//        val debugSettings = ConsentDebugSettings.Builder(context)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .build()
//
//        val parameters = ConsentRequestParameters.Builder()
//            .setConsentDebugSettings(debugSettings)
//            .setTagForUnderAgeOfConsent(false)
//            .build()
//
//        consentInformation!!.requestConsentInfoUpdate(context as Activity, parameters, {
//                if (consentInformation!!.isConsentFormAvailable) {
//                    loadConsentForm()
//                }
//            },
//            { formError ->
//                handleConsentInfoUpdateFailure(formError)
//            }
//        )
//    }
//
//    private fun loadConsentForm() {
//        UserMessagingPlatform.loadConsentForm(context,
//            { consentForm -> // OnConsentFormLoadSuccessListener
//                this.consentForm = consentForm
//
//                when (consentInformation!!.consentStatus) {
//                    ConsentInformation.ConsentStatus.REQUIRED -> {
//                        showConsentForm()
//                    }
//                    ConsentInformation.ConsentStatus.NOT_REQUIRED -> {
//                        // Handle not required
//                    }
//                    ConsentInformation.ConsentStatus.OBTAINED -> {
//                        // Handle obtained
//                    }
//                    ConsentInformation.ConsentStatus.UNKNOWN -> {
//
//                    }
//                    else -> {
//                        Log.d("splash_Tag", "else CMP")
//                    }
//                }
//            },
//            { formError -> // OnConsentFormLoadFailureListener
//                handleConsentFormLoadFailure(formError)
//            }
//        )
//    }
//
//    private fun showConsentForm() {
//        consentForm?.show(context as Activity) { formError ->
//            // Handle form dismissal
//        }
//    }
//
//    private fun handleConsentInfoUpdateFailure(formError: FormError) {
//        Log.e("splash_Tag", "Consent info update failed: ${formError.message}")
//        // Handle failure, e.g., retry or show default content
//    }
//
//    private fun handleConsentFormLoadFailure(formError: FormError) {
//        Log.e("splash_Tag", "Consent form load failed: ${formError.message}")
//        // Handle failure, e.g., retry or show default content
//    }
//}