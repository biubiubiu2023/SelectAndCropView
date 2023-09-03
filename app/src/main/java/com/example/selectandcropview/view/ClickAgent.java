package com.example.selectandcropview.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.selectandcropview.R;
import com.example.selectandcropview.data.SelectedRect;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2023/2/5
 * 编写人： 陈陈陈
 * 功能描述：
 */
class ClickAgent {
    private  Context mContext;
    public ClickAgent(Context context) {
        mContext = context;
    }
    //用户是否操作过
    public boolean isPerformed = false;
    /*边框画笔*/private Paint mRectStrokePaint;
    /*矩形画笔*/private Paint mRectPaint;

    /*屏幕像素密度*/private float mDensity;
    /*矩形列表*/ private List<SelectedRect> mRects;
    public List<SelectedRect> getRects(){
        return mRects;
    }

    public void initPaint() {
        mDensity = mContext.getResources().getDisplayMetrics().density;

        /*边框画笔*/
        /**初始化*/mRectStrokePaint = new Paint();
        /**设置画笔颜色*/mRectStrokePaint.setColor(ContextCompat.getColor(mContext, R.color.color_f7d));
        /**设置画笔样式*/mRectStrokePaint.setStyle(Paint.Style.STROKE);
        /**设置画笔粗细*/mRectStrokePaint.setStrokeWidth(1 * mDensity);
        /**使用抗锯齿*/mRectStrokePaint.setAntiAlias(true);
        /**使用防抖动*/mRectStrokePaint.setDither(true);

        /**初始化*/mRectPaint = new Paint();
        /**设置画笔颜色*/mRectPaint.setColor(ContextCompat.getColor(mContext, R.color.color_f7d_33));
        /**设置画笔样式*/mRectPaint.setStyle(Paint.Style.FILL);
        /**使用抗锯齿*/mRectPaint.setAntiAlias(true);
        /**使用防抖动*/mRectPaint.setDither(true);
    }

    public void setRects(List<SelectedRect> lists) {
        if(null == mRects){
            mRects = new ArrayList<>();
        }
        mRects.clear();
        mRects.addAll(lists);
    }

    public void onDraw(Canvas canvas, View view) {
        if(null != mRects && mRects.size()>0){
            for(int i=0;i<mRects.size();i++){
                if(mRects.get(i).isSelected){
                    canvas.drawRect(mRects.get(i).getRect(), mRectPaint);
                }
                canvas.drawRect(mRects.get(i).getRect(), mRectStrokePaint);
            }
        }
    }


    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    private boolean mIsClick;
    public boolean onTouchEvent(MotionEvent event, View view) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                mIsClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mIsClick = !isTouchMove(mViewDownX, event.getX(), mViewDownY, event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mIsClick){
                    for(int i=0;i<mRects.size();i++){
                        SelectedRect selectedRect = mRects.get(i);
                        if(selectedRect.rect.contains((int) mViewDownX, (int) mViewDownY)){
                            isPerformed = true;
                            selectedRect.isSelected = !selectedRect.isSelected;
                            view.invalidate();
                            break;
                        }
                    }
                }
            default:
                break;
        }
        return true;
    }



    /**
     * 判断用户是否移动了，判断标准以下：
     * 根据手指按下和抬起时的坐标进行判断，不能根据有没有 move 事件来判断
     * 因为在有些机型上面，就算用户没有手指没有移动也会产生 move 事件
     *
     * @param downX         手指按下时的 x 坐标
     * @param upX           手指抬起时的 x 坐标
     * @param downY         手指按下时的 y 坐标
     * @param upY           手指抬起时的 y 坐标
     */
    protected boolean isTouchMove(float downX, float upX, float downY, float upY) {
        float minTouchSlop = getMinTouchDistance();
        return Math.abs(downX - upX) >= minTouchSlop || Math.abs(downY - upY) >= minTouchSlop;
    }

    /**
     * 获取最小触摸距离
     */
    protected float getMinTouchDistance() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                Resources.getSystem().getDisplayMetrics());
    }

}
