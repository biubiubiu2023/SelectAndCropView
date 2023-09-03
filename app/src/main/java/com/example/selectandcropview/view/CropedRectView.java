package com.example.selectandcropview.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.example.selectandcropview.R;
import com.example.selectandcropview.data.SelectedRect;
import com.example.selectandcropview.utils.ExtentionsKt;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2023/2/9
 * 编写人： 陈陈陈
 * 功能描述：框选好的错题
 */
public class CropedRectView extends View {
    public CropedRectView(Context context) {
        super(context,null);
    }

    public CropedRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
       init();
    }


    private Paint mRectStrokePaint;
    private Paint mTextPaint;
    private Paint mRectPaint;
    private float mDensity;
    public List<SelectedRect> mRects = new ArrayList<>();
    public List<SelectedRect> getRects(){
        return mRects;
    }
    private String text = "错题";
    private float rightRectHeight;
    private float rightRectWidth;
    private Rect rightRect = new Rect();

    private float  rightTextMarginLeft;
    private float  rightTextMarginBottom;

    private void init() {
        mDensity = getContext().getResources().getDisplayMetrics().density;
        rightRectWidth = mDensity * 25;
        rightRectHeight = mDensity * 15;
        rightTextMarginLeft = mDensity * 2;
        rightTextMarginBottom = mDensity * 5;

        /*边框画笔*/
        /**初始化*/mRectStrokePaint = new Paint();
        /**设置画笔颜色*/mRectStrokePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_f7d));
        /**设置画笔样式*/mRectStrokePaint.setStyle(Paint.Style.STROKE);
        /**设置画笔粗细*/mRectStrokePaint.setStrokeWidth(1 * mDensity);
        /**使用抗锯齿*/mRectStrokePaint.setAntiAlias(true);
        /**使用防抖动*/mRectStrokePaint.setDither(true);

        /**初始化*/mRectPaint = new Paint();
        /**设置画笔颜色*/mRectPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_f7d));
        /**设置画笔样式*/mRectPaint.setStyle(Paint.Style.FILL);
        /**使用抗锯齿*/mRectPaint.setAntiAlias(true);
        /**使用防抖动*/mRectPaint.setDither(true);

        /**初始化*/mTextPaint = new Paint();
        /**设置画笔颜色*/mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        /**使用抗锯齿*/mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(11*mDensity);
        /**使用防抖动*/mTextPaint.setDither(true);

    }


    /**
     * @param lists 错题位置列表
     * @param oriImageSize  原图大小
     * @param imageRealPosition 展示在Imageview中的图片的位置和大小
     */
    public void setRects(List<SelectedRect> lists,int[] oriImageSize, RectF imageRealPosition) {
        if(ObjectUtils.isEmpty(lists) || ObjectUtils.isEmpty(oriImageSize) || null == imageRealPosition) return;
        Log.d("错题裁剪","原图1：width=="+oriImageSize[0]+"/height=="+oriImageSize[1]);
        Log.d("错题裁剪", "图片位置1：=="+imageRealPosition.toString());

        if(null == mRects){
            mRects = new ArrayList<>();
        }
        mRects.clear();
        mRects.addAll(lists);
        for(int i=0;i<mRects.size();i++){
            mRects.get(i).setRectBy(oriImageSize,imageRealPosition);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(null != mRects && mRects.size()>0){
            for(int i=0;i<mRects.size();i++){
                rightRect.set((int) (mRects.get(i).rect.right-rightRectWidth),mRects.get(i).rect.top,mRects.get(i).rect.right, (int) (mRects.get(i).rect.top+rightRectHeight));
                canvas.drawRect(rightRect, mRectPaint);
                canvas.drawRect(mRects.get(i).getRect(), mRectStrokePaint);
                canvas.drawText(text,rightRect.left + rightTextMarginLeft,rightRect.bottom - rightTextMarginBottom,mTextPaint);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtils.dTag("onTouchEvent","onTouchEvent");
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
                    LogUtils.dTag("CropedRectView","mIsClick");
                    for(int i=0;i<mRects.size();i++){
                        SelectedRect selectedRect = mRects.get(i);
                        if(selectedRect.rect.contains((int) mViewDownX, (int) mViewDownY)){
                            if(!ExtentionsKt.isDoubleClick(this,600)) {
                                if(null != mListener){
                                    mListener.OnItemClick(i,selectedRect);
                                }
                            }
                            break;
                        }
                    }
                }
            default:
                break;
        }
        return true;
    }


    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    private boolean mIsClick;



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
        // 疑问一：为什么要使用 1dp 来作为最小触摸距离？
        //        这是因为用户点击的时候，手指 down 和 up 的坐标不相等，会存在一点误差
        //        在有些手机上面，误差会比较小，还有一些手机上面，误差会比较大
        //        经过拿不同的手机测试和验证，这个误差值可以锁定在 1dp 内
        //        当然我的结论不一定正确，你要是有发现新的问题也可以找我反馈，我会持续优化这个问题
        // 疑问二：为什么不使用 ViewConfiguration.get(context).getScaledTouchSlop() ？
        //        这是因为这个 API 获取到的数值太大了，有一定概率会出现误判，同样的手机上面
        //        用 getScaledTouchSlop 获取到的是 24，而系统 1dp 获取的到是 3，
        //        两者相差太大，因为 getScaledTouchSlop API 默认获取的是 8dp * 3 = 24px
        // 疑问三：为什么要用 Resources.getSystem 来获取，而不是 context.getResources？
        //        这是因为如果用了 AutoSize 这个框架，上下文中的 1dp 就不是 3dp 了
        //        使用 Resources.getSystem 能够保证 Resources 对象不被第三方框架篡改
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                Resources.getSystem().getDisplayMetrics());
    }

    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public interface OnItemClickListener{
        void OnItemClick(int position,SelectedRect sRect);
    }
}
