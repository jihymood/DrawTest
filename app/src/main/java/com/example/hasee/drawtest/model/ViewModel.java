package com.example.hasee.drawtest.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/8/9 19:35
 */

public class ViewModel implements Serializable{

//    private static ViewModel viewModel;
    private List<PoPoListModel> pointList;
    private Bitmap bitmap;

    private byte[] pic;

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public List<PoPoListModel> getPointList() {
        return pointList;
    }

    public void setPointList(List<PoPoListModel> pointList) {
        this.pointList = pointList;
    }


    public ViewModel() {
        pointList = new ArrayList<>();
    }

    /**
     * 把Bitmap转换成byte[ ]
     * @param bitmap
     * @return
     */
    public byte[] getBytes(Bitmap bitmap){
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }

    /**
     * byte[ ]转换回来Bitmap
     * @param data
     * @return
     */
    public Bitmap getBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }
}
