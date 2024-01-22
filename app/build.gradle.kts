plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")


}

android {
    signingConfigs {
        getByName("debug") {
            keyAlias = "alawraq"
            keyPassword = "alawraq"
            storePassword = "alawraq"
            storeFile =
                file("C:\\Users\\H.A.R\\Desktop\\SpeechToTextApp1.3-master\\KeyStoreValues/speechToTextKey.jks")
        }
        create("release") {
            storeFile =
                file("C:\\Users\\H.A.R\\Desktop\\SpeechToTextApp1.3-master\\KeyStoreValues/speechToTextKey.jks")
            storePassword = "alawraq"
            keyPassword = "alawraq"
            keyAlias = "alawraq"
        }
    }
    namespace = "com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.3"
        setProperty("archivesBaseName", "alawraq-voicetotext-$versionName")
        signingConfig = signingConfigs.getByName("debug")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        vectorDrawables {
//            useSupportLibrary = true
//        }

    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false

        }


    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle{
        language {
            enableSplit = true
        }
    }


    flavorDimensions += listOf("defaultFlavour")
    productFlavors {
        create("Prod") {
            dimension = "defaultFlavour"
            resValue("string", "admob_app_id", "\"ca-app-pub-4970195951959554~7719432673\"")





            buildConfigField(
                "String",
                "banner_voice_talk",
                "\"ca-app-pub-4970195951959554/2109225769\""
            )
            buildConfigField(
                "String",
                "guide_Screen_banner",
                "\"ca-app-pub-4970195951959554/4543817415\""
            )

            buildConfigField(
                "String",
                "categoriesScreen_colapsible_Banner",
                "\"ca-app-pub-4970195951959554/6978409066\""
            )







            buildConfigField(
                "String",
                "native_splash",
                "\"ca-app-pub-4970195951959554/7387008141\""
            )
            buildConfigField(
                "String",
                "native_voice_SMS",
                "\"ca-app-pub-4970195951959554/6597450909\""
            )

            buildConfigField(
                "String",
                "native_voice_Rec",
                "\"ca-app-pub-4970195951959554/1370859164\""
            )

            buildConfigField(
                "String",
                "Sub_categories_native",
                "\"ca-app-pub-4970195951959554/9987715784\""
            )

            buildConfigField(
                "String",
                "Voice_Search_Screen_Native",
                "\"ca-app-pub-4970195951959554/8044078721\""
            )

            buildConfigField(
                "String",
                "Save_file_Screen_Native",
                "\"ca-app-pub-4970195951959554/9057777493\""
            )

            buildConfigField(
                "String",
                "language_Screen_Native",
                "\"ca-app-pub-4970195951959554/2275062517\""
            )





            buildConfigField(
                "String",
                "Translate_Button_inter",
                "\"ca-app-pub-4970195951959554/9579309252\""
            )

            buildConfigField(
                "String",
                "welcome_Screen_inter",
                "\"ca-app-pub-4970195951959554/1562430858\""
            )
            buildConfigField(
                "String",
                "interstitial_voice_rec_save_btn",
                "\"ca-app-pub-4970195951959554/9579309252\""
            )

            buildConfigField(
                "String",
                "interstitial_voice_search_category",
                "\"ca-app-pub-4970195951959554/9579309252\""
            )








            buildConfigField(
                "String",
                "app_open_others",
                "\"ca-app-pub-4970195951959554/1039825328\""
            )

            buildConfigField(
                "String",
                "app_open_launcher",
                "\"ca-app-pub-4970195951959554/2900968221\""
            )





            buildConfigField("Boolean", "env_dev", "false")
            signingConfig = signingConfigs.getByName("release")

        }
        create("Dev") {
            dimension = "defaultFlavour"

            resValue("string", "admob_app_id", "\"ca-app-pub-3940256099942544~3347511713\"")



            buildConfigField(
                "String",
                "banner_voice_talk",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "guide_Screen_banner",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )

            buildConfigField(
                "String",
                "categoriesScreen_colapsible_Banner",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )










            buildConfigField(
                "String",
                "native_splash",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
            buildConfigField(
                "String",
                "native_voice_SMS",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )

            buildConfigField(
                "String",
                "native_voice_Rec",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )

            buildConfigField(
                "String",
                "Sub_categories_native",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )

            buildConfigField(
                "String",
                "Voice_Search_Screen_Native",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )

            buildConfigField(
                "String",
                "Save_file_Screen_Native",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )

            buildConfigField(
                "String",
                "language_Screen_Native",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )











            buildConfigField(
                "String",
                "Translate_Button_inter",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )

            buildConfigField(
                "String",
                "welcome_Screen_inter",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )

            buildConfigField(
                "String",
                "interstitial_voice_rec_save_btn",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )

            buildConfigField(
                "String",
                "interstitial_voice_search_category",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )









            buildConfigField(
                "String",
                "app_open_others",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )


            buildConfigField(
                "String",
                "app_open_launcher",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )




            buildConfigField("Boolean", "env_dev", "true")
            signingConfig = signingConfigs.getByName("debug")

        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.compose.ui:ui-android:1.5.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

//    implementation("com.google.android.gms:play-services-vision:20.1.3")
//    implementation("com.github.mahimrocky:TextRecognizer:1.0.0"){
//        exclude("com.google.android.gms", "play-services-vision-common")
//    }

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.gms:play-services-mlkit-text-recognition:16.0.0")

    // Koin for Android
    implementation("io.insert-koin:koin-android:3.5.0")

    //sdp and ssp library
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //viewpager
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //lottieAnimation
    implementation("com.airbnb.android:lottie:6.1.0")

    // Room components
    implementation("androidx.room:room-ktx:2.6.0")
    kapt ("androidx.room:room-compiler:2.6.0")
    androidTestImplementation ("androidx.room:room-testing:2.6.0")

    //live data
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")

    implementation("com.google.mlkit:translate:17.0.2")

    implementation("io.github.pilgr:paperdb:2.7.2")

    // Import the Firebase BoM
//    implementation(platform("com.google.firebase:firebase-bom:32.4.1"))
//
//    //firebase crashlytics
//    implementation("com.google.firebase:firebase-crashlytics")
//    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-crashlytics:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    //firebase messaging
//    implementation("com.google.firebase:firebase-messaging")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation("com.google.android.gms:play-services-ads:22.6.0")

    implementation("com.google.android.ump:user-messaging-platform:2.1.0")


    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-process:2.6.2")
    kapt ("androidx.lifecycle:lifecycle-compiler:2.6.2")

    implementation ("com.zeugmasolutions.localehelper:locale-helper-android:1.5.1")

    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

//    implementation(files("libs/SmrtobjAds-libs.aar"))
//    implementation(files("libs/SmrtobjAds.aar"))
//    implementation(project(":SmrtobjAds"))
}