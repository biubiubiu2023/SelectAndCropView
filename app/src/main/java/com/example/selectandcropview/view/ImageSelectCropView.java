package com.example.selectandcropview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ObjectUtils;
import com.example.selectandcropview.data.SelectedRect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 创建时间：2023/2/2
 * 编写人： 陈陈陈
 * 功能描述：
 */
public class ImageSelectCropView extends View {

    public static final String TAG = "MCustomZoomView";

    /**
     *  0：绘制点击框；
     *  1：绘制裁剪框；
     */
    public int type = 0;

    private CropAgent mCropAgent;
    public CropAgent getCropAgent(){
        return mCropAgent;
    }
    private ClickAgent mClickAgent;

    public ImageSelectCropView(Context context) {
        super(context,null);
    }

    public ImageSelectCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);//触摸获取焦点
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mClickAgent = new ClickAgent(context);
        mClickAgent.initPaint();

        mCropAgent = new CropAgent(context);
        mCropAgent.initPaint();
    }

    /**
     * 设置限制滑动的区域
     * @param rect
     */
    public void setLimitedRect(Rect rect){
        if(rect.left == 0 && rect.top==0 && rect.right==0 &&rect.bottom==0){
            rect.set(0,0,getWidth(),getHeight());
        }
        mCropAgent.setLimitedRect(rect);
        invalidate();
    }

    /**
     * 设置 矩形 数据列表
     * @param lists
     */
    public void setRects(List<SelectedRect> lists){
        mClickAgent.isPerformed = false;
        mCropAgent.isPerformed = false;
        type = 0;
        if(lists.size()>1){
            Collections.sort(lists, new Comparator<SelectedRect>() {
                @Override
                public int compare(SelectedRect o1, SelectedRect o2) {
                    return Math.abs(o2.rect.bottom-o2.rect.top)*Math.abs(o2.rect.right-o2.rect.left) - Math.abs(o1.rect.bottom-o1.rect.top)*Math.abs(o1.rect.right-o1.rect.left);
                }
            });
        }
        mClickAgent.setRects(lists);
        invalidate();
    }

    /**
     * 获取控件宽、高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 绘制框相关元素
     *
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        if(type == 0){
            mClickAgent.onDraw(canvas,this);
        }else{
            mCropAgent.onDraw(canvas,this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(type == 0){
            return mClickAgent.onTouchEvent(event,this);
        }else{
            return mCropAgent.onTouchEvent(event,this);
        }
    }

    public void setISDrawMapLine(boolean iSDrawMapLine) {
        mCropAgent.setISDrawMapLine(iSDrawMapLine);
        invalidate();
    }

    public boolean switchType(int t){
        if(t == 0){
            type = 0;
            invalidate();
            return true;
        }else{
            List<SelectedRect> rects = mClickAgent.getRects();
            if(ObjectUtils.isEmpty(rects)) return false;

            List<SelectedRect> selectedRects = new ArrayList<>();
            for(int i=0;i<rects.size();i++){
                if(rects.get(i).isSelected){
                    selectedRects.add(rects.get(i));
                }
            }
            if(selectedRects.size()>0){
                type = 1;
                mCropAgent.setRects(selectedRects);
                invalidate();
                return true;
            }
            return false;
        }
    }

    public boolean isPerformed (){
        return mClickAgent.isPerformed || mCropAgent.isPerformed;
    }

}

