package com.example.selectandcropview.data;


import java.io.Serializable;

/**
 * 创建时间：2023/2/18
 * 创建人： 陈群
 * 功能描述：
 */
public class Pos implements Serializable {
    public  int x;
    public int y;

    public Pos(){}

    public Pos(int x, int y){
        this.x = x;
        this.y = y;
    }
}
