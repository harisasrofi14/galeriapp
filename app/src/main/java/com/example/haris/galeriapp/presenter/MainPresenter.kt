package com.example.haris.galeriapp.presenter

import android.content.Context
import android.os.Environment
import com.example.haris.galeriapp.view.main.MainActivity
import com.example.haris.galeriapp.model.Item
import com.example.haris.galeriapp.view.main.MainView
import java.io.File

class MainPresenter(private val view: MainView) {

    fun getAllImage(context: Context) {
        val listItem = ArrayList<Item>()
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ""
        )
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    val file = files[i]
                    if (file.isFile) {
                        listItem.add(Item(file.path, file.name, MainActivity.TYPE_IMAGE))
                    }
                }
                view.showAllImage(listItem)
            }
        }
    }

    fun getAllVideo(context: Context) {
        val listItemVideo = ArrayList<Item>()
        val movieDirectory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), ""
        )
        if (movieDirectory.exists()) {
            val files = movieDirectory.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    val file = files[i]
                    if (file.isFile) {
                        listItemVideo.add(Item(file.path, file.name, MainActivity.TYPE_VIDEO))
                    }
                }
                view.showAllVideo(listItemVideo)
            }
        }
    }
}