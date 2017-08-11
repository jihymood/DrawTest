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

    private int position; //选中图形的状态 ：-2选中图形 -3在图形外面 -1/0/1/2...选中图形的边
    private List<Point> list; //点的集合
    private boolean isShowLength; //是否显示标注
    private List<Line> lines; //线的集合
    private List<Point> centerPoints;
    private boolean isMOveCanvas; //是否移动画布


    public PoPoListModel(List<Point> list) {
        this.list = list;
    }

    public PoPoListModel() {
    }

    public boolean isMOveCanvas() {
        return isMOveCanvas;
    }

    public void setIsMOveCanvas(boolean MOveCanvas) {
        isMOveCanvas = MOveCanvas;
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
