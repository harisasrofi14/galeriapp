package com.example.haris.galeriapp.helper

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.*

object Utils {
    @Throws(IOException::class)
    fun createVideoFile(context: Context): File {
        val newFileName = UUID.randomUUID().toString() + ".mp4"
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), ""
        )
        if (!dir.exists()) {
            val wasSuccessful = dir.mkdirs()
            if (!wasSuccessful) {
                Log.e("createImageFile", "cannot create picture directory")
            }
        }
        return File(dir, newFileName)
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val newFileName = UUID.randomUUID().toString() + ".jpg"
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ""
        )
        if (!dir.exists()) {
            val wasSuccessful = dir.mkdirs()
            if (!wasSuccessful) {
                Log.e("createImageFile", "cannot create picture directory")
            }
        }
        return File(dir, newFileName)
    }
}