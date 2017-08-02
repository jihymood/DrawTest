package com.example.hasee.drawtest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/7/31 10:55
 */

public class PointListModel {

    private static PointListModel pointListModel;

    private static List<List<Point>> list;

    public List<List<Point>> getList() {
        return list;
    }

    public void setList(List<List<Point>> list) {
        this.list = list;
    }

    public PointListModel() {
    }

    public void addList(List<Point> points) {
        list.add(points);
    }

    public static PointListModel getInstance() {
        if (pointListModel == null) {
            list = new ArrayList<>();
            pointListModel = new PointListModel();
        }
        return pointListModel;
    }

}
