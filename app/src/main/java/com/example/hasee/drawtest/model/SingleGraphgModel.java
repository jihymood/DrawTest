package com.example.hasee.drawtest.model;

import java.util.List;

/**
 * Created by HASEE on 2017/7/31 16:45
 * 单个图形
 */

public class SingleGraphgModel {
    private int position;
    private List<Point> list;

    public SingleGraphgModel(int position, List<Point> list) {
        this.position = position;
        this.list = list;
    }

    public SingleGraphgModel() {
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
