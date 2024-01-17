package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep

@Keep
@Entity(tableName = "conversation_table")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val translatedText:String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: Int
)
