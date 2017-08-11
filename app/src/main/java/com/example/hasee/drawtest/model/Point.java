package com.example.hasee.drawtest.model;

import java.io.Serializable;

/**
 * Created by HASEE on 2017/7/18 12:27
 */

public class Point implements Serializable{
    private float x;  //x坐标
    private float y;  //y坐标
    private boolean isLock;

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
    }
}
