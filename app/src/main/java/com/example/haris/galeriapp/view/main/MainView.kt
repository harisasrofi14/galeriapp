package com.example.haris.galeriapp.view.main

import com.example.haris.galeriapp.model.Item

interface MainView {
    fun showAllImage(data: List<Item>)
    fun showAllVideo(data: List<Item>)
}