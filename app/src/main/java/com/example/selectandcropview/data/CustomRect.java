package com.example.selectandcropview.data;



import android.graphics.Rect;
import android.graphics.RectF;

import java.io.Serializable;

/**
 * 创建时间：2023/4/1
 * 创建人： 陈群
 * 功能描述：
 */
public class CustomRect implements Serializable {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public CustomRect() {}

    public CustomRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    public CustomRect(Rect r) {
        if (r == null) {
            left = top = right = bottom = 0;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }
    public CustomRect(RectF r) {
        if (r == null) {
            left = top = right = bottom = 0;
        } else {
            left = (int) r.left;
            top = (int) r.top;
            right = (int) r.right;
            bottom = (int) r.bottom;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomRect r = (CustomRect) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }
    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Rect("); sb.append(left); sb.append(", ");
        sb.append(top); sb.append(" - "); sb.append(right);
        sb.append(", "); sb.append(bottom); sb.append(")");
        return sb.toString();
    }
    public String toShortString() {
        return toShortString(new StringBuilder(32));
    }

    public String toShortString( StringBuilder sb) {
        sb.setLength(0);
        sb.append('['); sb.append(left); sb.append(',');
        sb.append(top); sb.append("]["); sb.append(right);
        sb.append(','); sb.append(bottom); sb.append(']');
        return sb.toString();
    }

    public boolean contains(int x, int y) {
        return left < right && top < bottom  // check for empty first
                && x >= left && x < right && y >= top && y < bottom;
    }
    public boolean contains(int left, int top, int right, int bottom) {
        // check for empty first
        return this.left < this.right && this.top < this.bottom
                // now check for containment
                && this.left <= left && this.top <= top
                && this.right >= right && this.bottom >= bottom;
    }
    public boolean contains( CustomRect r) {
        // check for empty first
        return this.left < this.right && this.top < this.bottom
                // now check for containment
                && left <= r.left && top <= r.top && right >= r.right && bottom >= r.bottom;
    }

    public final boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    public final float width() {
        return right - left;
    }

    public final float height() {
        return bottom - top;
    }
}
