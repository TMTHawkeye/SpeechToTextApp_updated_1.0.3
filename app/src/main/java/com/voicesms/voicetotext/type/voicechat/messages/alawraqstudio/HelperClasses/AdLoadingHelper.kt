package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.HelperClasses

import android.app.ProgressDialog

fun showLoadingDialog(progressDialog: ProgressDialog) {
    if (!progressDialog.isShowing) {
        progressDialog.show()
    }
}

fun dismissLoadingDialog(progressDialog: ProgressDialog) {
    if (progressDialog.isShowing) {
        progressDialog.dismiss()
    }
}