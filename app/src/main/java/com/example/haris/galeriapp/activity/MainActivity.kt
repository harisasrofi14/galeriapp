package com.example.haris.galeriapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.haris.galeriapp.R
import com.example.haris.galeriapp.adapter.GalleryAdapter
import com.example.haris.galeriapp.adapter.GalleryItemClickListener
import com.example.haris.galeriapp.helper.Utils
import com.example.haris.galeriapp.model.Item
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
        const val TYPE_VIDEO = "Video"
        const val TYPE_IMAGE = "Image"
        private const val SPAN_COUNT = 4
        private const val REQUEST_VIDEO_CAPTURE = 1000
        private const val REQUEST_IMAGE_CAPTURE = 2000

    }

    private val itemList = ArrayList<Item>()
    lateinit var galleryAdapter: GalleryAdapter
    private var isAllFabVisible: Boolean? = null
    private var videoStoragePathUri: Uri? = null
    private var imageStoragePathUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        galleryAdapter = GalleryAdapter(itemList)
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter

        loadVideoData()
        loadImageData()

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
        fab_add_from_gallery.setOnClickListener {
            if (checkAndRequestPermissions()) {
//                Toast.makeText(this, "ADD from gallery", Toast.LENGTH_SHORT).show()
//                val cameraIntent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
//                startActivityForResult(cameraIntent, pic_id)
                takePhoto()
            }
        }
        fab_add_from_camera.setOnClickListener {
            if (checkAndRequestPermissions()) {
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraIntent, pic_id)
                recordVideo()
            }
        }
        galleryAdapter.setOnItemClickCallback(object : GalleryItemClickListener {
            override fun onClick(data: Item) {
                Toast.makeText(this@MainActivity, data.title, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.CAMERA
        )
        val write = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(packageManager) != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                    this,
                    this.packageName.toString() + ".provider",
                    Utils.createVideoFile(this)
                )
                videoStoragePathUri = contentUri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoStoragePathUri)
            }
            startActivityForResult(
                intent,
                REQUEST_VIDEO_CAPTURE
            )
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                    this,
                    this.packageName.toString() + ".provider",
                    //  createImageFile(this)
                    Utils.createImageFile(this)
                )
                imageStoragePathUri = contentUri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageStoragePathUri)
            }
            startActivityForResult(
                intent,
                REQUEST_IMAGE_CAPTURE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val path = videoStoragePathUri?.path
            itemList.add(
                Item(
                    path.toString(),
                    videoStoragePathUri?.pathSegments?.last().toString(),
                    TYPE_VIDEO
                )
            )
            galleryAdapter.notifyDataSetChanged()
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val path = imageStoragePathUri?.path
            itemList.add(
                Item(
                    path.toString(),
                    imageStoragePathUri?.pathSegments?.last().toString(),
                    TYPE_IMAGE
                )
            )
            galleryAdapter.notifyDataSetChanged()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun loadVideoData() {
        val dir = File(
            this.getExternalFilesDir(Environment.DIRECTORY_MOVIES), ""
        )
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    val file = files[i]
                    if (file.isFile) {
                        Toast.makeText(this, file.absolutePath, Toast.LENGTH_SHORT).show()
                        itemList.add(Item(file.path, file.name, TYPE_VIDEO))
                    }
                }
                galleryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadImageData() {
        val dir = File(
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ""
        )
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    val file = files[i]
                    if (file.isFile) {
                        Toast.makeText(this, file.absolutePath, Toast.LENGTH_SHORT).show()
                        itemList.add(Item(file.path, file.name, TYPE_IMAGE))
                    }
                }
                galleryAdapter.notifyDataSetChanged()
            }
        }
    }
}