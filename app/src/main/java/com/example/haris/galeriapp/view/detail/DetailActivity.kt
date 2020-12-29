package com.example.haris.galeriapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.haris.galeriapp.R
import com.example.haris.galeriapp.model.Item
import com.example.haris.galeriapp.view.main.MainActivity.Companion.EXTRA_ITEM
import com.example.haris.galeriapp.view.main.MainActivity.Companion.TYPE_VIDEO
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.videoView
import kotlinx.android.synthetic.main.item_gallery.view.*


class DetailActivity : AppCompatActivity() {

    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        loadData()
    }

    private fun loadData() {
        val item = intent.getParcelableExtra<Item>(EXTRA_ITEM) as Item
        if (item.type == TYPE_VIDEO) {
            videoView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            videoView.setVideoPath(item.path)
            videoView.start()
        } else {
            videoView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            Glide.with(this@DetailActivity)
                .load(item.path)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }

    override fun onPause() {
        position = videoView.currentPosition
        super.onPause()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("Position", position)
        videoView.pause()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")
        videoView.seekTo(position)
    }
}