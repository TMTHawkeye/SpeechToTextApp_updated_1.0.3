package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DAO.ConversationDao
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities.ConversationEntity

@Database(entities = [ConversationEntity::class], version = 1, exportSchema = false)
abstract class ConversationDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
}