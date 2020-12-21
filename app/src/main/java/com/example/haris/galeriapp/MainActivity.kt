package com.example.haris.galeriapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isAllFabVisible: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAllFabVisible = false

        fab_add_item.shrink()
        fab_add_item.setOnClickListener {
            if (isAllFabVisible == false) {
                isAllFabVisible = true
                fab_add_from_gallery.show()
                tv_gallery.visibility = View.VISIBLE
                fab_add_from_camera.show()
                tv_camera.visibility = View.VISIBLE
                fab_add_item.extend()
            } else {
                fab_add_from_gallery.hide()
                tv_gallery.visibility = View.GONE
                fab_add_from_camera.hide()
                tv_camera.visibility = View.GONE
                fab_add_item.shrink()
                isAllFabVisible = false
            }
        }
    }
}