package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DAO

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: ConversationEntity)

    @Delete
    suspend fun delete(conversation: ConversationEntity)

    @Query("SELECT * FROM conversation_table")
    fun getAllConversations(): Flow<List<ConversationEntity>>
}