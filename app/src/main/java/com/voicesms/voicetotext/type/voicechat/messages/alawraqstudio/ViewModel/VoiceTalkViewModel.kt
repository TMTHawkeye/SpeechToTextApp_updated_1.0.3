package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DAO.ConversationDao
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Entities.ConversationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class VoiceTalkViewModel(private val conversationDao: ConversationDao) : ViewModel() {

    fun saveConversation(text: String,translatedText:String, source: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val conversationEntity = ConversationEntity(text = text, translatedText = translatedText, source = source)
            conversationDao.insert(conversationEntity)
        }
    }

    fun deleteConversation(conversationEntity: ConversationEntity){
        viewModelScope.launch(Dispatchers.IO){
            conversationDao.delete(conversationEntity)
        }
    }

    fun getAllConversations(): Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()
            .flowOn(Dispatchers.IO)


}