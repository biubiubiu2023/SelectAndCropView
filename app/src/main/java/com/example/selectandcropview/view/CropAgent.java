package com.example.selectandcropview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
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
public class CropAgent {
    public static final String TAG = "CropAgent";

    Context mContext;
    CropAgent(Context context){
        mContext = context;

        mDensity = mContext.getResources().getDisplayMetrics().density;
        mCornerLength = 15f * mDensity;
        mCornerLineHeight = 2.6f * mDensity;
        mCornerOffset = mCornerLineHeight/2f;
        minHeight = mCornerLength * 1.5f;
        minWidth = mCornerLength * 4f;
        cornerRange = mCornerLength * 1.5f;
        extentions = (int) (-10 * mDensity);
    }
    //用户是否操作过
    public boolean isPerformed = false;
    /**
     * Rect 外层可触控区域，用于触控优化
     */
    private int extentions = 0;
    /*屏幕像素密度*/private float mDensity;

    /*自定框相关*/
    /*矩形列表*/ private List<Rect> mRects;
    /*边角线长度*/private float mCornerLength;

    /*边角线高度*/private float mCornerLineHeight;
    /*边角线偏移值*/private float mCornerOffset;

    /*透明框画笔*/
    private Paint transPaint;
    /*边框画笔*/private Paint mRectPaint;
    /*边角线画笔*/private Paint mCornerPaint;
    /*测绘线画笔*/private Paint mMappingLinePaint;
    /*背景画笔*/private Paint mBackgroundPaint;


    /*0-不动 1-拖动 2-边角缩放 3-边框缩放*/
    /*矩形操作状态*/private int mOperatingStatus = 0;

    /*0-左 1-上 2-右 3-下*/
    /*边框线点击-操作状态*/private int mBorderlineStatus = -1;

    /*0-左上角 1-左下角 2-右上角 3-右下角*/
    /*边角点击-操作状态*/private int mCornerStatus = -1;

    /*是否绘制测绘线*/private boolean mISDrawMapLine;

    /*限制范围*/
    private Rect mLimitedRect = null;


    /*当前点击的矩形的位置*/
    private int mCurrentPointRectIndex = -1;
    /*上一次按下的X坐标*/ float mLastPressX;
    /*上一次按下的Y坐标*/ float mLastPressY;

    /*矩形最小高度*/
    private float minHeight;
    /* 矩形最小宽度 */
    private float minWidth ;

    /*边角范围*/
    private float cornerRange;

    /*初始化画笔*/
    public void initPaint() {

        /* 背景画笔 */
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.parseColor("#22000000"));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        /* 透明区域画笔 */
        transPaint = new Paint();
        transPaint.setColor(Color.parseColor("#00000000"));
        transPaint.setStyle(Paint.Style.FILL);
        transPaint.setAntiAlias(true);
        transPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        /*边框画笔*/
        /**初始化*/mRectPaint = new Paint();
        /**设置画笔颜色*/mRectPaint.setColor(ContextCompat.getColor(mContext, R.color.color_f7d));
        /**设置画笔样式*/mRectPaint.setStyle(Paint.Style.STROKE);
        /**设置画笔粗细*/mRectPaint.setStrokeWidth(1 * mDensity);
        /**使用抗锯齿*/mRectPaint.setAntiAlias(true);
        /**使用防抖动*/mRectPaint.setDither(true);
//        /**设置笔触样式-圆*/mRectPaint.setStrokeCap(Paint.Cap.ROUND);
//        /**设置结合处为圆弧*/mRectPaint.setStrokeJoin(Paint.Join.ROUND);

        /*边角画笔*/
        /**初始化*/mCornerPaint = new Paint();
        /**设置画笔颜色*/mCornerPaint.setColor(ContextCompat.getColor(mContext, R.color.color_f7d));
        /**设置画笔样式*/mCornerPaint.setStyle(Paint.Style.FILL);
        /**设置画笔粗细*/mCornerPaint.setStrokeWidth(mCornerLineHeight);
        /**使用抗锯齿*/mCornerPaint.setAntiAlias(true);
        /**使用防抖动*/mCornerPaint.setDither(true);

        /*测绘线画笔*/
        /**初始化*/mMappingLinePaint = new Paint();
        /**设置画笔颜色*/mMappingLinePaint.setColor(ContextCompat.getColor(mContext, R.color.color_f7d));
        /**设置画笔样式*/mMappingLinePaint.setStyle(Paint.Style.FILL);
        /**设置画笔粗细*/mMappingLinePaint.setStrokeWidth(1 * mDensity);
        /**使用抗锯齿*/mMappingLinePaint.setAntiAlias(true);
        /**使用防抖动*/mMappingLinePaint.setDither(true);
//        /**设置笔触样式-圆*/mMappingLinePaint.setStrokeCap(Paint.Cap.ROUND);
//        /**设置结合处为圆弧*/mMappingLinePaint.setStrokeJoin(Paint.Join.ROUND);

    }

    public void setLimitedRect(Rect rect){
        mLimitedRect = rect;
    }

    /**
     * 原始数据
     */
    List<SelectedRect> oriDatas = new ArrayList<>();
    public List<SelectedRect> getDatas(){
        for(int i=0;i<oriDatas.size();i++){
            oriDatas.get(i).setRect(mRects.get(i));
            oriDatas.get(i).colectParams();
        }
        return  oriDatas;
    }
    /**
     * 设置 矩形 数据列表
     * @param lists
     */
    public void setRects(List<SelectedRect> lists){
        oriDatas = lists;
        if(null == mRects){
            mRects = new ArrayList<>();
        }
        mRects.clear();
        for(int i=0;i<oriDatas.size();i++){
            mRects.add(oriDatas.get(i).getRect());
        }
    }


    public void onDraw(Canvas canvas, View view) {
        //绘制蒙层 和 镂空区域
        if(null != mLimitedRect){
            int i1 = canvas.saveLayer(0, 0, view.getWidth(), view.getHeight(), null, Canvas.ALL_SAVE_FLAG);
            canvas.drawRect(mLimitedRect,mBackgroundPaint);
            if(null != mRects && mRects.size()>0){
                for(int i=0;i<mRects.size();i++){
                    canvas.drawRect(mRects.get(i), transPaint);
                }
            }
            canvas.restoreToCount(i1);
        }

        if(null != mRects && mRects.size()>0){
            for(int i=0;i<mRects.size();i++){
                drawRect(canvas,mRects.get(i));
            }
        }
    }

    /**
     * 绘制Rect
     * @param mRect
     */
    private void drawRect(Canvas canvas,Rect mRect) {
        if(null == mRect){
            return;
        }
        /*绘制边框*/
        canvas.drawRect(mRect, mRectPaint);

        /*绘制边角*/
        /*左上-横*/
        canvas.drawLine(mRect.left - mCornerOffset, mRect.top
                , mRect.left + mCornerLength, mRect.top, mCornerPaint);
        /*左上-竖*/
        canvas.drawLine(mRect.left, mRect.top - mCornerOffset
                , mRect.left, mRect.top + mCornerLength, mCornerPaint);
        /*左下-横*/
        canvas.drawLine(mRect.left - mCornerOffset, mRect.bottom
                , mRect.left + mCornerLength, mRect.bottom, mCornerPaint);
        /*左上下-竖*/
        canvas.drawLine(mRect.left, mRect.bottom - mCornerLength
                , mRect.left, mRect.bottom + mCornerOffset, mCornerPaint);
        /*右上-横*/
        canvas.drawLine(mRect.right - mCornerLength, mRect.top
                , mRect.right + mCornerOffset, mRect.top, mCornerPaint);
        /*右上-竖*/
        canvas.drawLine(mRect.right, mRect.top - mCornerOffset
                , mRect.right, mRect.top + mCornerLength, mCornerPaint);
        /*右下-横*/
        canvas.drawLine(mRect.right - mCornerLength, mRect.bottom
                , mRect.right + mCornerOffset, mRect.bottom, mCornerPaint);
        /*右下-竖*/
        canvas.drawLine(mRect.right, mRect.bottom - mCornerLength
                , mRect.right, mRect.bottom + mCornerOffset, mCornerPaint);

        if (mISDrawMapLine) {
            toolDrawMapLine(canvas,mRect);
        }
    }

    /**
     * 绘制测绘线
     */
    private void toolDrawMapLine(Canvas canvas,Rect mRect) {
        /*绘制横线*/
        /*绘制第一根线-位于矩形框的3分之一处*/
        canvas.drawLine(mRect.left
                , mRect.top + (mRect.bottom - mRect.top) / 3
                , mRect.right
                , mRect.top + (mRect.bottom - mRect.top) / 3
                , mMappingLinePaint);
        /*绘制第二根线-位于矩形框的3分之二处*/
        canvas.drawLine(mRect.left
                , mRect.top + (mRect.bottom - mRect.top) / 3 * 2
                , mRect.right
                , mRect.top + (mRect.bottom - mRect.top) / 3 * 2
                , mMappingLinePaint);

        /*绘制竖线*/
        /*绘制第一根线-位于矩形框的3分之一处*/
        canvas.drawLine(mRect.left + (mRect.right - mRect.left) / 3
                , mRect.top
                , mRect.left + (mRect.right - mRect.left) / 3
                , mRect.bottom
                , mMappingLinePaint);
        /*绘制第二根线-位于矩形框的3分之二处*/
        canvas.drawLine(mRect.left + (mRect.right - mRect.left) / 3 * 2
                , mRect.top
                , mRect.left + (mRect.right - mRect.left) / 3 * 2
                , mRect.bottom
                , mMappingLinePaint);
    }



    public boolean onTouchEvent(MotionEvent event, View view) {
        switch (event.getAction()) {
            /*按下*/
            case MotionEvent.ACTION_DOWN:
                /**当前按下的X坐标*/float mPressX = event.getX();
                /**当前按下的Y坐标*/float mPressY = event.getY();
                mCurrentPointRectIndex = toolPointIsInRect(mPressX, mPressY);

                if(mCurrentPointRectIndex > -1){
                    /*判断按下的点是否在边角上*/
                    if (toolPointIsInCorner(mRects.get(mCurrentPointRectIndex),mPressX, mPressY)) {
                        mOperatingStatus = 2;//边角的范围是一个长宽等于边角线长的矩形范围内
                    }
                    /*判断按下的点是都在边界线上*/
                    else if (toolPointIsInBorderline(mRects.get(mCurrentPointRectIndex),mPressX, mPressY)) {
                        mOperatingStatus = 3;
                    }
                    /*按下的点在矩形内*/
                    else {
                        mOperatingStatus = 1;
                        Log.d(TAG,"拖动 == " +"矩形");
                    }

                    mLastPressX = mPressX;
                    mLastPressY = mPressY;
                }

                break;
            /*移动*/
            case MotionEvent.ACTION_MOVE:
                if(mCurrentPointRectIndex > -1){
                    isPerformed = true;
                    changePointRect(mRects.get(mCurrentPointRectIndex),event);
                    /**重绘*/view.invalidate();
                    /**保存上一次按下的点X坐标*/mLastPressX = event.getX();
                    /**保存上一次按下的点Y坐标*/mLastPressY = event.getY();
                }
                break;
            /*松开*/
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mCurrentPointRectIndex = -1;
                /**恢复静止*/mOperatingStatus = 0;
                mBorderlineStatus = -1;
                mCornerStatus = -1;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断按下的点是否在矩形内
     */
    private int toolPointIsInRect(float x, float y) {
        int index = -1;
        if(null != mRects && mRects.size()>0){
            for(int i=0;i<mRects.size();i++){
                if(mRects.get(i).contains((int) x, (int) y)) {
                    index = i;
                    break;
                }
            }
        }
        if(index == -1){
            if(null != mRects && mRects.size()>0){
                for(int i=0;i<mRects.size();i++){
                    Rect rect = new Rect(mRects.get(i));
                    rect.inset(extentions,extentions);
                    if(rect.contains((int) x, (int) y)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    /**
     * 改变矩形 大小 位置
     * @param mRect
     * @param event
     */
    private void changePointRect(Rect mRect,MotionEvent event) {
        /*移动-改变矩形四个点坐标*/
        if (mOperatingStatus == 1) {
            mRect.offset((int) (event.getX() - mLastPressX), (int) (event.getY() - mLastPressY));
            limitMove(mRect);

        }
        /*边角缩放*/
        else if (mOperatingStatus == 2) {
            //小小边框 左边的情况
            if(mCornerStatus == 4){
                //暂停事件过滤
                if(Math.abs(event.getY() - mLastPressY) < 2){
                    return;
                }
                if(event.getY() > mLastPressY){
                    //向下滑动，设置成左下边角
                    mCornerStatus = 1;
                }else{
                    //否则设置成左上边角
                    mCornerStatus = 0;
                }
            }
            //小小边框 右边的情况
            if(mCornerStatus == 5){
                //暂停事件过滤
                if(Math.abs(event.getY() - mLastPressY) < 2){
                    return;
                }
                if(event.getY() > mLastPressY){
                    //向下滑动，设置成右下边角
                    mCornerStatus = 3;
                }else{
                    //否则设置成右上边角
                    mCornerStatus = 2;
                }
            }

            /*判断点击的是哪一个角*/
            /*点击了左上角*/
            if (mCornerStatus == 0) {
                mRect.set((int) (mRect.left+(event.getX() - mLastPressX)), (int) (mRect.top+(event.getY() - mLastPressY)),mRect.right,mRect.bottom);


                checkXLeftValue(mRect);
                checkYTopValue(mRect);
            }
            /*点击了左下角*/
            else if (mCornerStatus == 1) {
                mRect.set((int) (mRect.left+(event.getX() - mLastPressX)), mRect.top,mRect.right, (int) (mRect.bottom+(event.getY() - mLastPressY)));


                checkXLeftValue(mRect);
                checkYBottom(mRect);

            }
            /*点击了右上角*/
            else if (mCornerStatus == 2) {
                mRect.set(mRect.left, (int) (mRect.top+(event.getY() - mLastPressY)), (int) (mRect.right+(event.getX() - mLastPressX)),mRect.bottom);


                checkXRight(mRect);
                checkYTopValue(mRect);
            }
            /*点击了右下角*/
            else if (mCornerStatus == 3) {
                mRect.set(mRect.left,mRect.top, (int) (mRect.right+(event.getX() - mLastPressX)), (int) (mRect.bottom+(event.getY() - mLastPressY)));

                checkXRight(mRect);
                checkYBottom(mRect);
            }

        }
        /*边框缩放*/
        else if (mOperatingStatus == 3) {

            if (mBorderlineStatus == 0) {
                Log.e("自定义", "边框线-左");
                mRect.set((int) (mRect.left+(event.getX() - mLastPressX)),mRect.top,mRect.right,mRect.bottom);

                checkXLeftValue(mRect);

            } else if (mBorderlineStatus == 1) {
                Log.e("自定义", "边框线-上");
                mRect.set(mRect.left, (int) (mRect.top+( event.getY() - mLastPressY)),mRect.right,mRect.bottom);

                checkYTopValue(mRect);

            } else if (mBorderlineStatus == 2) {
                Log.e("自定义", "边框线-右");
                mRect.set(mRect.left,mRect.top, (int) (mRect.right+(event.getX() - mLastPressX)),mRect.bottom);
                checkXRight(mRect);

            } else if (mBorderlineStatus == 3) {
                Log.e("自定义", "边框线-下");
                mRect.set(mRect.left,mRect.top,mRect.right, (int) (mRect.bottom+(event.getY() - mLastPressY)));

                checkYBottom(mRect);

            }

        }

    }
    private void limitMove(Rect mRect){
//        if(mRect.left < mLimitedRect.left){
//            mRect.set(mLimitedRect.left, mRect.top,mRect.right, mRect.bottom);
//        }
//        if(mRect.top < mLimitedRect.top){
//            mRect.set(mRect.left, mLimitedRect.top,mRect.right, mRect.bottom);
//        }
//        if(mRect.right > mLimitedRect.right){
//            mRect.set(mRect.left, mRect.top,mLimitedRect.right, mRect.bottom);
//        }
//        if(mRect.bottom > mLimitedRect.bottom){
//            mRect.set(mRect.left, mRect.top,mRect.right, mLimitedRect.bottom);
//        }
        checkYBottom(mRect);
        checkXRight(mRect);
        checkYTopValue(mRect);
        checkXLeftValue(mRect);
    }

    /**
     * 限制 高度 手势从 下 -> 上 的情况
     * @param mRect
     */
    private void checkYBottom(Rect mRect) {
        if(mRect.bottom < mRect.top+minHeight){
            mRect.set(mRect.left,mRect.top,mRect.right, (int) (mRect.top+minHeight));
        }
        if(mRect.bottom > mLimitedRect.bottom){
            mRect.set(mRect.left, mRect.top,mRect.right, mLimitedRect.bottom);
        }
    }

    /**
     * 限制 宽度 手势从 右 -> 左 的情况
     * @param mRect
     */
    private void checkXRight(Rect mRect) {
        if(mRect.right < mRect.left+minWidth){
            mRect.set(mRect.left,mRect.top, (int) (mRect.left+minWidth), mRect.bottom);
        }
        if(mRect.right > mLimitedRect.right){
            mRect.set(mRect.left, mRect.top,mLimitedRect.right, mRect.bottom);
        }
    }

    /**
     * 限制 高度 手势从 上 -> 下 的情况
     * @param mRect
     */
    private void checkYTopValue(Rect mRect) {
        if(mRect.top > mRect.bottom-minHeight){
            mRect.set(mRect.left, (int) (mRect.bottom-minHeight),mRect.right, mRect.bottom);
        }

        if(mRect.top < mLimitedRect.top){
            mRect.set(mRect.left, mLimitedRect.top,mRect.right, mRect.bottom);
        }
    }

    /**
     * 限制 宽度 手势从  左 -> 右 的情况
     * @param mRect
     */
    private void checkXLeftValue(Rect mRect) {
        if(mRect.left > mRect.right-minWidth){
            mRect.set((int) (mRect.right-minWidth), mRect.top,mRect.right, mRect.bottom);
        }
        if(mRect.left < mLimitedRect.left){
            mRect.set(mLimitedRect.left, mRect.top,mRect.right, mRect.bottom);
        }
    }


    /**
     * 判断按下的点是否在边角范围内
     */
    private boolean toolPointIsInCorner(Rect mRect ,float x, float y) {
        /**
         * 小小框的情况下 只分左右两种情况，在 move 滑动的时候才确定具体类型
         */
        if(isLittleRect(mRect)){
            if( x < mRect.left + cornerRange){
                //在左边
                mCornerStatus = 4;
                Log.d(TAG,"拖动 == " +"左 边角");
                return true;
            }
            if(x > mRect.right - cornerRange){
                //在右边
                mCornerStatus = 5;
                Log.d(TAG,"拖动 == " +"右 边角");
                return true;
            }

            return false;
        }

        /**
         * 正常大小的框，分 四个情况
         */
        if (x < mRect.left + cornerRange
                && y < mRect.top + cornerRange) {
            mCornerStatus = 0;
            Log.d(TAG,"拖动 == " +"左上 边角");
            return true;
        } else if (x < mRect.left+ cornerRange
                && y > mRect.bottom - cornerRange
                ) {
            mCornerStatus = 1;
            Log.d(TAG,"拖动 == " +"左下 边角");
            return true;
        } else if (x > mRect.right - cornerRange
                && y < mRect.top + cornerRange) {
            mCornerStatus = 2;
            Log.d(TAG,"拖动 == " +"右上 边角");
            return true;
        } else if (x > mRect.right - cornerRange
                && y > mRect.bottom - cornerRange
                ) {
            mCornerStatus = 3;
            Log.d(TAG,"拖动 == " +"右下 边角");
            return true;
        }
        return false;
    }


    /**
     * 判断是否是小小框
     * @param mRect
     * @return
     */
    private boolean isLittleRect(Rect mRect){
        return mRect.bottom - mRect.top < mCornerLength*2.5f;
    }
    /**
     * 判断按下的点是否在边框线范围内
     */
    private boolean toolPointIsInBorderline(Rect mRect,float x, float y) {
        //小小框
//        if(isLittleRect(mRect)) {
//            return false;
//        }

        float leftRightRange = mRect.width()/4f;
        float topBottomRange = mRect.height()/4f;

        if (x < mRect.left + leftRightRange) {
            mBorderlineStatus = 0;
            Log.d(TAG,"拖动 == " +"左 边线");
            return true;
        } else if ( y < mRect.top + topBottomRange) {
            mBorderlineStatus = 1;
            Log.d(TAG,"拖动 == " +"上 边线");
            return true;
        } else if (x > mRect.right - leftRightRange) {
            mBorderlineStatus = 2;
            Log.d(TAG,"拖动 == " +"右 边线");
            return true;
        } else if (y > mRect.bottom -topBottomRange) {
            mBorderlineStatus = 3;
            Log.d(TAG,"拖动 == " +"下 边线");
            return true;
        }

        return false;
    }


    public void setISDrawMapLine(boolean mISDrawMapLine) {
        this.mISDrawMapLine = mISDrawMapLine;
    }
}
