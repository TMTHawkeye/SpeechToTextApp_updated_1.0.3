package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.DependencyInjection

import androidx.room.Room
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Databases.ConversationDatabase
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories.VoiceRecordingRepositoryImpl
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories.VoiceSMSRepositoryImpl
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories.VoiceSearchRepository
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.RecordingViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.SMSViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceSearchViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel.VoiceTalkViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            ConversationDatabase::class.java,
            "conversation_database"
        ).build()
    }
    single{
        get<ConversationDatabase>().conversationDao()
    }


    single {
        VoiceSMSRepositoryImpl(context = get())
    }

    single {
        VoiceRecordingRepositoryImpl(context = get())
    }
    single {
        VoiceSearchRepository(context = get())
    }

    viewModel {
        SMSViewModel(repository = get())
    }

    viewModel {
        RecordingViewModel(repository = get())
    }

    viewModel {
        VoiceSearchViewModel(repository = get())
    }

    viewModel {
        VoiceTalkViewModel(conversationDao = get())
    }


}
