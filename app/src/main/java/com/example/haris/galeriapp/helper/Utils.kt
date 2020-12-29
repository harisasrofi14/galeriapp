package com.example.haris.galeriapp.helper

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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

    fun getRealPathFromURI(context: Context, contentURI: Uri?): String? {
        val result: String?
        val cursor: Cursor? =
            contentURI?.let { context.contentResolver.query(it, null, null, null, null) }
        if (cursor == null) {
            result = contentURI?.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    @Throws(IOException::class)
    fun copy(src: FileInputStream, dst: File?) {
        src.use { `in` ->
            FileOutputStream(dst).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }


}