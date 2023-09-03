package com.example.selectandcropview.data;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.blankj.utilcode.util.SizeUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间：2023/2/5
 * 编写人： 陈陈陈
 * 功能描述：
 */
public class SelectedRect implements Serializable {
    public CustomRect rect;
    public boolean isSelected = false;

    public void setRect(Rect r){
        rect = new CustomRect(r.left,r.top,r.right,r.bottom);
    }
    public Rect getRect(){
        return new Rect(rect.left,rect.top,rect.right,rect.bottom);
    }
    public List<Pos> points;
    int[] mImageSize;
    public CustomRect mImageRealPosition;
    private int minHeight = SizeUtils.dp2px(22.5f);
    private int minWidth = SizeUtils.dp2px(60f);


    public SelectedRect(List<Pos> pos, int[] imageSize, RectF imageRealPosition) {
        points = pos;
        checkPos();
        setRectBy(imageSize,imageRealPosition);
    }

    public SelectedRect(List<Pos> pos, int[] imageSizeOri, int[] imageSizeProcessed, RectF imageRealPosition) {
        points = pos;
        resetPos(imageSizeOri,imageSizeProcessed);
        checkPos();
        setRectBy(imageSizeOri,imageRealPosition);
    }

    private void resetPos(int[] imageSizeOri, int[] imageSizeProcessed) {
        if(null != points && points.size()==4){
            points.get(0).x = points.get(0).x * imageSizeOri[0] / imageSizeProcessed[0];
            points.get(0).y = points.get(0).y * imageSizeOri[1] / imageSizeProcessed[1];
            points.get(1).x = points.get(1).x * imageSizeOri[0] / imageSizeProcessed[0];
            points.get(1).y = points.get(1).y * imageSizeOri[1] / imageSizeProcessed[1];
            points.get(2).x = points.get(2).x * imageSizeOri[0] / imageSizeProcessed[0];
            points.get(2).y = points.get(2).y * imageSizeOri[1] / imageSizeProcessed[1];
            points.get(3).x = points.get(3).x * imageSizeOri[0] / imageSizeProcessed[0];
            points.get(3).y = points.get(3).y * imageSizeOri[1] / imageSizeProcessed[1];
        }
    }

    private void checkPos() {
        if(points.get(0).x > points.get(3).x){
            points.get(0).x = points.get(3).x;
        }
        if(points.get(0).y > points.get(1).y){
            points.get(0).y = points.get(1).y;
        }
        if(points.get(2).x < points.get(1).x){
            points.get(2).x = points.get(1).x;
        }
        if(points.get(2).y < points.get(3).y){
            points.get(2).y = points.get(3).y;
        }
        if(points.get(2).x - points.get(0).x < minWidth){
            points.get(2).x = points.get(0).x + minWidth;
        }
        if(points.get(2).y - points.get(0).y < minHeight){
            points.get(2).y = points.get(0).y + minHeight;
        }
    }

    public void setRectBy( int[] imageSize, RectF imageRealPosition){
        if(mImageSize == null){
            mImageSize = imageSize;
        }

        mImageRealPosition = new CustomRect(imageRealPosition);

        Log.d("错题裁剪", "裁剪位置：=="+new CustomRect(points.get(0).x,points.get(0).y,points.get(2).x,points.get(2).y).toShortString());

        float rateW = mImageRealPosition.width() / mImageSize[0];
        float rateH = mImageRealPosition.height() / mImageSize[1];

        if(null != rect){
            Log.d("错题裁剪", "计算后位置1：=="+rect.toShortString());
        }

        rect = new CustomRect((int) (points.get(0).x*rateW + mImageRealPosition.left)
                , (int) (points.get(0).y*rateH + mImageRealPosition.top)
                , (int) (points.get(2).x*rateW + mImageRealPosition.left)
                , (int) (points.get(2).y*rateH + mImageRealPosition.top));

        Log.d("错题裁剪", "计算后位置2：=="+rect.toShortString());

    }

    /**
     * 从框选页面回到图片处理页面
     */
    public void colectParams() {
        if(null != mImageRealPosition && null != rect && null != points){
            Log.d("错题裁剪", "colectParams1：=="+new CustomRect(points.get(0).x,points.get(0).y,points.get(2).x,points.get(2).y).toShortString());
            float rateW = mImageRealPosition.width() / mImageSize[0];
            float rateH = mImageRealPosition.height() / mImageSize[1];
            points.get(0).x = (int) ((rect.left - mImageRealPosition.left)/rateW);
            points.get(0).y = (int) ((rect.top - mImageRealPosition.top)/rateH);
            points.get(1).x = (int) ((rect.right - mImageRealPosition.left)/rateW);
            points.get(1).y = (int) ((rect.top - mImageRealPosition.top)/rateH);
            points.get(2).x = (int) ((rect.right - mImageRealPosition.left)/rateW);
            points.get(2).y = (int) ((rect.bottom - mImageRealPosition.top)/rateH);
            points.get(3).x = (int) ((rect.left - mImageRealPosition.left)/rateW);
            points.get(3).y = (int) ((rect.bottom - mImageRealPosition.top)/rateH);

            Log.d("错题裁剪", "colectParams2：=="+new CustomRect(points.get(0).x,points.get(0).y,points.get(2).x,points.get(2).y).toShortString());

        }
    }
}
