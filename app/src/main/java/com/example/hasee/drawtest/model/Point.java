package com.example.hasee.drawtest.model;

import java.io.Serializable;

/**
 * Created by HASEE on 2017/7/18 12:27
 */

public class Point implements Serializable{
    private float x;
    private float y;

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
