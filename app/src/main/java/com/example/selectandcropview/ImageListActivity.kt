package com.example.selectandcropview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selectandcropview.data.SelectedRect
import com.example.selectandcropview.utils.ViewUtils

/**
 * 创建时间：2023/9/2
 * 编写人： 陈陈陈
 * 功能描述：
 */
class ImageListActivity : AppCompatActivity() {
    companion object{
        fun start(context: Context,datas :List<SelectedRect>) {
            val intent = Intent(context,ImageListActivity::class.java)
            intent.putExtra("datas",datas as java.io.Serializable)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val datas = intent.getSerializableExtra("datas") as List<SelectedRect>
        val bitmaps = mutableListOf<Bitmap>()

        val opts = BitmapFactory.Options()
        opts.inScaled = false
        val decodeResource = BitmapFactory.decodeResource(resources,R.drawable.background,opts)
        for(rect in datas){
            val clip = ViewUtils.clip(
                decodeResource,
                rect.points[0].x,
                rect.points[0].y,
                rect.points[2].x - rect.points[0].x,
                rect.points[2].y - rect.points[0].y,
                false
            )
            clip?.let {
                bitmaps.add(it)
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ImagesAdapter(bitmaps,this@ImageListActivity)
        }
    }


}