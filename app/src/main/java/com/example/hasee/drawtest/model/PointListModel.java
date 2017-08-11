package com.example.hasee.drawtest.model;

import java.util.List;

/**
 * Created by HASEE on 2017/7/31 10:55
 */

public class PointListModel {
    private static PointListModel pointListModel;
    private static List<List<Point>> list; //图形点的集合
    private static List<PoPoListModel> listModels; //图形对象集合，应该使用面向对象的思想开发

    public List<PoPoListModel> getListModels() {
        return listModels;
    }

    public void setListModels(List<PoPoListModel> listModels) {
        this.listModels = listModels;
    }

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

//    public static PointListModel getInstance() {
//        if (pointListModel == null) {
//            list = new ArrayList<>();
//            listModels = new ArrayList<>();
//            pointListModel = new PointListModel();
//        }
//        return pointListModel;
//    }




}
