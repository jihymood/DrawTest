package com.example.hasee.drawtest.model;

import com.example.hasee.drawtest.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/7/31 14:27
 * 面向对象编程，将一个图形看成一个整体
 * 点到图形距离及对应的图形集合
 */

public class PoPoListModel implements Serializable {

    private int position;
    private List<Point> list;
    private boolean isShowLength;
    private List<Line> lines;
    private List<Point> centerPoints;

    public PoPoListModel(List<Point> list) {
        this.list = list;
    }

    public PoPoListModel() {
    }



    public boolean isShowLength() {
        return isShowLength;
    }

    public void setShowLength(boolean showLength) {
        isShowLength = showLength;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Point> getList() {
        return list;
    }

    public void setList(List<Point> list) {
        this.list = list;
    }

    public boolean isInside() {
        if (position == -2) {
            return true;
        }
        return false;
    }

    public boolean inPolygon(float x, float y) {
        //形成封闭多边形
        List<Point> pp = new ArrayList<>();
        pp.addAll(list);
        pp.add(list.get(0));
        if (Utils.PtInRegion(new Point(x, y), pp) == 1) {
            return true;//表示点击点在多边形内,执行移动view
        } else {
            return false;//表示点击点在多边形外,执行移动canvas
        }
    }

    public List<Line> getLines() {
        lines = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            lines.add(new Line(list.get(i), list.get(i + 1)));
        }
        lines.add(new Line(list.get(list.size() - 1), list.get(0)));
        return lines;
    }

    public List<Point> getLineCenterPoints() {
        centerPoints = new ArrayList<>();
        if (lines == null) {
            getLines();
        }
        for (Line l : lines) {
            float x = l.getP1().getX() + l.getP2().getX() / 2;
            float y = l.getP1().getY() + l.getP2().getY() / 2;
            centerPoints.add(new Point(x, y));
        }
        return centerPoints;
    }



}
