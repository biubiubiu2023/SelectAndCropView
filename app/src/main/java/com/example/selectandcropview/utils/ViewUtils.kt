package com.example.selectandcropview.utils

import android.graphics.RectF
import android.widget.ImageView

/**
 * 创建时间：2023/9/2
 * 编写人： 陈陈陈
 * 功能描述：
 */
object ViewUtils {

    /**
     * 获取bitmap 在ImageView控件的真正位置
     */
    fun getImageRealPosition(view: ImageView): RectF {
        val imageMatrix = view.imageMatrix
        val drawable = view.drawable
        val rectf = RectF()
        if (drawable != null) {
            rectf.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            imageMatrix.mapRect(rectf)
        }
        return rectf
    }

}