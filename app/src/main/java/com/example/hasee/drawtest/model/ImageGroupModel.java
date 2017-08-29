package com.example.hasee.drawtest.model;

import com.example.hasee.drawtest.weidget.Two.success.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/8/28 11:26
 * 保存添加的图标
 */

public class ImageGroupModel {
    private static ImageGroupModel imageGroupModel;
    private List<BaseView.ImageGroup> imageGroupList;

    public static ImageGroupModel getInstance() {
        if (imageGroupModel == null) {
            imageGroupModel = new ImageGroupModel();
//            imageGroupList = new ArrayList<>(); //static方法，所以直接将初始化步骤写在构造函数里
        }
        return imageGroupModel;
    }

    public ImageGroupModel() {
        imageGroupList = new ArrayList<>();
    }

    public List<BaseView.ImageGroup> getImageGroupList() {
        return imageGroupList;
    }

    public void setImageGroupList(List<BaseView.ImageGroup> imageGroupList) {
        this.imageGroupList = imageGroupList;
    }

    public void addImageGroup(BaseView.ImageGroup imageGroup) {
        imageGroupList.add(imageGroup);
    }

    public void cleanGroupList() {
        if (imageGroupList!=null) {
            imageGroupList.clear();
        }
    }

}
