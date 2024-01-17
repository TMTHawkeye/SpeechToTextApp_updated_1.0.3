package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses

import com.google.errorprone.annotations.Keep

@Keep
data class FileData(
    val filePath:String,
    val fileSize:String,
    val timeAgo: String
)
