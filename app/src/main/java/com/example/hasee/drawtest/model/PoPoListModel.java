package com.example.hasee.drawtest.model;

import java.util.List;

/**
 * Created by HASEE on 2017/7/31 14:27
 * 面向对象编程，将一个图形看成一个整体
 * 点到图形距离及对应的图形集合
 */

public class PoPoListModel {

    private int position;
    private List<Point> list;

    public PoPoListModel(int position, List<Point> list) {
        this.position = position;
        this.list = list;
    }

    public PoPoListModel() {
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
}
