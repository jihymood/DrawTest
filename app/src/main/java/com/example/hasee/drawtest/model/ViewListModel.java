package com.example.hasee.drawtest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/8/9 19:35
 */

public class ViewListModel {

    private static ViewListModel viewModel;
    private List<ViewModel> pointAndBitmapList;


    public static ViewListModel getInstance() {
        if (viewModel == null) {
            viewModel = new ViewListModel();
        }
        return viewModel;
    }

    public List<ViewModel> getPointAndBitmapList() {
        return pointAndBitmapList;
    }

    public void setPointAndBitmapList(List<ViewModel> pointAndBitmapList) {
        this.pointAndBitmapList = pointAndBitmapList;
    }

    public void addPointAndBitmapList(ViewModel viewModel) {
        pointAndBitmapList.add(viewModel);
    }


    //    public static void setPointList(List<PoPoListModel> pointList) {
//        viewModel.pointAndBitmapList = pointList;
//    }
//
//    public List<PoPoListModel> getPointList() {
//        return pointAndBitmapList;
//    }


//    public List<PoPoListModel> getPointAndBitmapList() {
//        return pointAndBitmapList;
//    }
//
//    public void setPointAndBitmapList(List<PoPoListModel> pointAndBitmapList) {
//        this.pointAndBitmapList = pointAndBitmapList;
//    }

    public ViewListModel() {
        pointAndBitmapList = new ArrayList<>();
    }
}
