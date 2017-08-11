package com.example.hasee.drawtest.model;

/**
 * Created by HASEE on 2017/7/24 14:31
 */

public class TwoPointDistance  {
    public Point first; //两个吸附点
    public Point second; //两个吸附点
    public float distance;  //吸附点之间的距离

    public Point getFirst() {
        return first;
    }

    public void setFirst(Point first) {
        this.first = first;
    }

    public Point getSecond() {
        return second;
    }

    public void setSecond(Point second) {
        this.second = second;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public TwoPointDistance(Point first, Point second, float distance) {
        this.first = first;
        this.second = second;
        this.distance = distance;
    }

    public TwoPointDistance() {
    }

    @Override
    public String toString() {
        return "TwoPointDistance{" +
                "first=" + first.getX()+"/"+first.getY() +
                ", second=" + second.getX()+"/"+second.getY() +
                ", distance=" + distance +
                '}';
    }

}
