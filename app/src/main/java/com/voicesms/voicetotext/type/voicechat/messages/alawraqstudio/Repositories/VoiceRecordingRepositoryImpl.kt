package com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.Repositories

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import com.voicesms.voicetotext.type.voicechat.messages.alawraqstudio.ModelClasses.FileData
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class VoiceRecordingRepositoryImpl(var context: Context) {

    var file: File? = null
    var recorder: MediaRecorder? = null

    //create a temp file in cache folder.
    fun createTempFile(callback: (String?) -> Unit) {
        try {
            // Specify a fixed file name
            val fileName = "temp_voice.mp3"

            // Create a File object with the fixed file name in the external cache directory
            val tempFile = File(context.externalCacheDir, fileName)
            if (!tempFile.exists()) {
                tempFile.mkdir()
            }
            Log.d("TAG", "Recording createTempAudioFile Path: ${tempFile.absolutePath}")

            // Check if the temporary file was created successfully
            if (tempFile.exists()) {
                Log.d("TAG", "Recording createTempAudioFile Creation: Success")
                callback(tempFile.absolutePath)
            } else {
                Log.d("TAG", "Recording createTempAudioFile Creation: Failed")
                callback(null)
            }
        } catch (e: Exception) {
            Log.d("TAG", "Recording createTempAudioFile Exception: ${e.message}")
            callback(null)
        }
    }

    //Function to release the recorder if it is already in use.
    private fun freeRecorder() {
        if (recorder != null) {
            recorder!!.release()
            Log.d("TAG", "Recording released success")
        }
        else{
            Log.d("TAG", "Recording released failure")
        }
    }

    //Function to start the recording.
    fun beginRecording(filePath: String) {
        freeRecorder()
        val outFile = File(filePath)
        if (outFile.exists()) {
            outFile.delete()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(context)
        }
        else{
            recorder = MediaRecorder()
        }
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setAudioSamplingRate(16000);
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.setOutputFile(filePath)
        try {
            recorder!!.prepare()
            recorder!!.start()
            Log.d("TAG", "Recording started")
        } catch (e: IOException) {
            Log.e("TAG", "Recording failed: ${e.message}")
        }

    }

//    fun stopRecording(callback: (Boolean?) -> Unit) {
//        if (recorder != null) {
//            try {
//                recorder!!.stop();
//                recorder!!.reset();
//                freeRecorder()
//                callback(true)
//            } catch (e: Exception) {
//                Log.e("TAG", "Recording stopRecording: ${e.message}")
//                callback(false)
//            }
//        } else {
//            callback(false)
//        }
//    }


    fun stopRecording(isRecording:Boolean,callback: (Boolean?,MediaRecorder?) -> Unit) {
        if (recorder != null) {
            try {
                // Check if the recorder is in the proper state
                if (isRecording) {
                    recorder?.stop()
                    Log.e("TAG", "Recording stopRecording: success")
                    callback(true,recorder)

                }

            } catch (e: IllegalStateException) {
                Log.e("TAG", "Recording stopRecording Illegal: ${e.message}")
                callback(false,recorder)
            } catch (e: Exception) {
                Log.e("TAG", "Recording stopRecording: ${e.message}")
                callback(false,recorder)
            }
//            finally {

//            }
        } else {
            Log.d("TAG", "stopRecording: recorder is null")
            callback(false,recorder)
        }
    }
    // Function to copy the content of one file to another with a new name
    fun copyFile(
        sourceFile: File,
        destinationDirectory: File,
        newFileName: String,
        fileExt: String
    ): Boolean {
        return try {
            // Check if the destination directory exists, create if not
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs()
            }

            // Create a File object with the new file name in the destination directory
            val destinationFile = File(destinationDirectory, newFileName + fileExt)

            // Check if there is enough storage space before copying
            if (hasEnoughStorageSpace(destinationFile.length())) {
                // Copy the content of the source file to the new file
                sourceFile.copyTo(destinationFile)

                // Return true to indicate success
                true
            } else {
                // Log an error message if there is not enough storage space
                Log.d("TAG", "Not enough storage space.")
                false
            }
        } catch (e: Exception) {
            // Log any exceptions and return false to indicate failure
            Log.d("TAG", "Copy File Exception: ${e.message}")
            false
        }
    }

    private fun hasEnoughStorageSpace(fileSize: Long): Boolean {
        // Check if there is enough free space on the external storage
        val availableSpace = Environment.getExternalStorageDirectory().freeSpace
        return availableSpace > fileSize
    }


    fun getListofFilesFromStorage(folderPath: String, callback: (ArrayList<FileData>?) -> Unit) {
        val mp3FilePaths = ArrayList<FileData>()

        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.let { files ->
                files.filter { it.isFile && it.extension.equals("mp3", ignoreCase = true) }
                    .sortedByDescending { it.lastModified() }
                    .forEach { file ->
                        mp3FilePaths.add(
                            FileData(
                                file.absolutePath,
                                (file.length() / 1024).toString() + " KBs",
                                getTimeAgo(file.lastModified())
                            )
                        )
                    }

                callback(mp3FilePaths)
            } ?: run {
                callback(null)
            }
        } else {
            callback(null)
        }
    }




    fun getTimeAgo(lastModifiedTime: Long): String {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - lastModifiedTime

        val days = TimeUnit.MILLISECONDS.toDays(difference)
        val hours = TimeUnit.MILLISECONDS.toHours(difference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(difference)

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "just now"
        }
    }

}