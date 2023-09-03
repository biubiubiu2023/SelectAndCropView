package com.example.selectandcropview.utils

import android.view.View

/**
 * 判断是否重复点击
 */
fun View?.isDoubleClick(time:Int = 600):Boolean{
    if(null == this){
        return false
    }
    val tag: Any? = getTag(id)
    val beforeTimemiles = if (tag != null) tag as Long else 0
    val timeInMillis: Long = System.currentTimeMillis()
    setTag(id, timeInMillis)
    return timeInMillis - beforeTimemiles < time
}


/**
 * 设置圆角
 */
fun View.setRoundConner(radius:Int, radiusSide:Int = ViewHelper.RADIUS_ALL){
    ViewHelper.setViewOutline(this, ViewUtils.dp2px(radius.toFloat()),radiusSide)
}