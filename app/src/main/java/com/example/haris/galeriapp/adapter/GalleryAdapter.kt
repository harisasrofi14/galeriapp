package com.example.haris.galeriapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.haris.galeriapp.R
import com.example.haris.galeriapp.activity.MainActivity
import com.example.haris.galeriapp.model.Item
import kotlinx.android.synthetic.main.item_gallery.view.*


class GalleryAdapter(private val itemList: List<Item>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var context: Context? = null
    private var onItemClickCallback: GalleryItemClickListener? = null

    fun setOnItemClickCallback(onItemClickCallback: GalleryItemClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val item = itemList[adapterPosition]
            context?.let {
                Glide.with(it)
                    .load(item.path)
                    .thumbnail(0.1f)
                    .override(300, 300)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.iv_thumbnail)
            }
            itemView.setOnClickListener {
                onItemClickCallback?.onClick(item)
            }
            if (item.type == MainActivity.TYPE_VIDEO) {
                itemView.iv_play.visibility = View.VISIBLE
            }
        }
    }
}