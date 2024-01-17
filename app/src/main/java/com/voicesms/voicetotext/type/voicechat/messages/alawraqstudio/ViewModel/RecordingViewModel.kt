package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ViewModel

import android.media.MediaRecorder
import androidx.lifecycle.ViewModel
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.FileData
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories.VoiceRecordingRepositoryImpl
import org.koin.core.component.KoinComponent
import java.io.File

class RecordingViewModel(var repository: VoiceRecordingRepositoryImpl) : ViewModel() {

    fun createTempFile(callback:(String?)->Unit){
        repository.createTempFile(callback)
    }

    fun beginRecording(filePath:String){
        repository.beginRecording(filePath)
    }
    fun stopRecording(isRecording:Boolean,callback: (Boolean?,MediaRecorder?) -> Unit){
        repository.stopRecording(isRecording,callback)
    }

    fun saveRecordingToFile(sourceFilePath: File, destFilePath: File,fileName:String,fileExt:String):Boolean{
        return repository.copyFile(sourceFilePath,destFilePath,fileName,fileExt)
    }

    fun getListofFilesFromStorage(folderpath:String,callback: (ArrayList<FileData>?) -> Unit){
         repository.getListofFilesFromStorage(folderpath,callback)
    }


}