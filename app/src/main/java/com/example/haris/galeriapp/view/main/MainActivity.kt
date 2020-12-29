package com.example.haris.galeriapp.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
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
import com.example.haris.galeriapp.presenter.MainPresenter
import com.example.haris.galeriapp.view.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), MainView {
    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
        const val TYPE_VIDEO = "Video"
        const val TYPE_IMAGE = "Image"
        private const val SPAN_COUNT = 4
        private const val REQUEST_VIDEO_CAPTURE = 1000
        private const val REQUEST_IMAGE_CAPTURE = 2000
        private const val REQUEST_OPEN_GALLERY = 3000
        const val EXTRA_ITEM = "Item"
    }

    private lateinit var presenter: MainPresenter
    private val itemList = ArrayList<Item>()
    lateinit var galleryAdapter: GalleryAdapter
    private var isAllFabVisible: Boolean? = null
    private var videoStoragePathUri: Uri? = null
    private var imageStoragePathUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
        initPresenter()
        initFloatingButton()
    }

    private fun initAdapter() {
        galleryAdapter = GalleryAdapter()
        galleryAdapter.setOnItemClickCallback(object : GalleryItemClickListener {
            override fun onClick(data: Item) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_ITEM, data)
                startActivity(intent)
            }
        })
        recyclerView.layoutManager = GridLayoutManager(this, SPAN_COUNT)
        recyclerView.adapter = galleryAdapter
        recyclerView.setHasFixedSize(true)
    }

    private fun initPresenter() {
        presenter = MainPresenter(
            this
        )
        presenter.getAllImage(applicationContext)
        presenter.getAllVideo(applicationContext)
    }

    private fun initFloatingButton() {
        isAllFabVisible = false
        fab_add_item.shrink()
        fab_add_item.setOnClickListener {
            if (isAllFabVisible == false) {
                isAllFabVisible = true
                fab_add_from_gallery.show()
                tv_gallery.visibility = View.VISIBLE
                fab_add_from_camera.show()
                tv_camera.visibility = View.VISIBLE
                fab_add_from_video.show()
                tv_video.visibility = View.VISIBLE
                fab_add_item.extend()

            } else {
                fab_add_from_gallery.hide()
                tv_gallery.visibility = View.GONE
                fab_add_from_camera.hide()
                tv_camera.visibility = View.GONE
                fab_add_from_video.hide()
                tv_video.visibility = View.GONE
                fab_add_item.shrink()
                isAllFabVisible = false
            }
        }
        fab_add_from_gallery.setOnClickListener {
            openGallery()
        }
        fab_add_from_camera.setOnClickListener {
            if (checkAndRequestPermissions()) {
                takePhoto()
            }
        }
        fab_add_from_video.setOnClickListener {
            if (checkAndRequestPermissions()) {
                recordVideo()
            }
        }
    }

    override fun showAllImage(data: List<Item>) {
        galleryAdapter.setItem(data)
    }

    override fun showAllVideo(data: List<Item>) {
        galleryAdapter.setItem(data)
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

    private fun openGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/* video/*"
        if (pickIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(pickIntent, REQUEST_OPEN_GALLERY)
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
            galleryAdapter.setItem(itemList)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val path = imageStoragePathUri?.path
            itemList.add(
                Item(
                    path.toString(),
                    imageStoragePathUri?.pathSegments?.last().toString(),
                    TYPE_IMAGE
                )
            )
            galleryAdapter.setItem(itemList)
        } else if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {

            val selectedMediaUri: Uri? = data?.data

            val file = File(selectedMediaUri.let {
                Utils.getRealPathFromURI(this@MainActivity, it).toString()
            })
            val inputStream = FileInputStream(file)
            if (selectedMediaUri.toString().contains("image")) {
                val des = Utils.createImageFile(this)
                Utils.copy(inputStream, des)
                itemList.add(
                    Item(
                        des.path,
                        des.name,
                        TYPE_IMAGE
                    )
                )
            } else if (selectedMediaUri.toString().contains("video")) {
                val des = Utils.createVideoFile(this)
                Utils.copy(inputStream, des)
                itemList.add(
                    Item(
                        des.path,
                        des.name,
                        TYPE_VIDEO
                    )
                )
            }
            galleryAdapter.setItem(itemList)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}