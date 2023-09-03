package com.example.selectandcropview

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * 创建时间：2023/9/2
 * 编写人： 陈陈陈
 * 功能描述：
 */
class ImagesAdapter(var datas:List<Bitmap>?=null,var context:Context?=null) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return datas?.size?:0
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        datas?.get(position)?.let {
            holder.imageView?.setImageBitmap(it)
        }
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView:ImageView? = null
        init {
            imageView = view.findViewById(R.id.image)
        }
    }
}