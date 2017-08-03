package com.example.hasee.drawtest.model;

/**
 * Created by MIUSHUKI on 2017/7/28.
 */

public class Line {
    private int index;
    private Point p1;
    private Point p2;
    private double length;
    private double slope;//直线的斜率
    private double degree;//直线与x轴的夹角

    public Line() {
    }

    public Line(Point p1, Point p2, double length) {
        this.p1 = p1;
        this.p2 = p2;
        this.length = length;
    }

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    @Override
    public String toString() {
        return "Line{" +
                "index=" + index +
                ", p1=" + p1 +
                ", p2=" + p2 +
                ", length=" + length +
                ", slope=" + slope +
                ", degree=" + degree +
                '}';
    }
}
