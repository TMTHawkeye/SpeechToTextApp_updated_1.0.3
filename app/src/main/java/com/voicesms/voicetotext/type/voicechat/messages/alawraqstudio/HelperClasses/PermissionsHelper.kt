package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.content.Context
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.R

public var IS_LANGUAGE_SELECTED = "IS_LANGUAGE_SELECTED"


 fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}
fun isMicrophoneAllowed(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
}

fun isReadStorageAllowed(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun isWriteStorageAllowed(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestStoragePermission(activity: Activity) {
    //android 13
    if (Build.VERSION.SDK_INT >= 33) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,

            ),
            activity.getString(R.string.storagePermissionCode).toInt()
        )
        //android 12 or lesser
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            activity.getString(R.string.storagePermissionCode).toInt()
        )
    }
}

fun requestMicPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                android.Manifest.permission.RECORD_AUDIO,

                ),
            activity.getString(R.string.mic_permissions).toInt()
        )

}



fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$context.packageName")
    context.startActivity(intent)
}
