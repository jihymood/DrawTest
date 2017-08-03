package com.example.hasee.drawtest.weidget.One;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawPolygonView2 drawPloygonView2;
    private Button clear;
    private TextView textView;
    private List<Point> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        drawPloygonView2 = (DrawPolygonView2) findViewById(R.id.drawPloygonView2);
        clear = (Button) findViewById(R.id.clear);
        textView = (TextView) findViewById(R.id.textView);

        clear.setOnClickListener(this);

        setOrderPointList();
//        Point movePoint = new Point(0, 1);  //移动线抬起时的鼠标的点坐标
//        Point movePoint = new Point(6, 7);  //移动线抬起时的鼠标的点坐标
        Point movePoint = new Point(4, 2);  //移动线抬起时的鼠标的点坐标
        getPoint(list, movePoint);


    }


    /**
     * 该集合点必须是顺序的 （顺时针），
     * 第0个为移动线的坐标
     * 第1个为移动线的坐标
     * 第2个为第一条相交线另一个坐标
     * 第3个为第二条相交线另一个坐标
     */
    public void setOrderPointList() {
        list = new ArrayList<>();  //顺时针添加
//        Point point = new Point(5, 6);  //移动线的坐标 ：集合中第0个
//        Point point1 = new Point(8, 7);  //移动线的坐标
//        Point point2 = new Point(10, 0);  //第一条相交线另一个坐标
//        Point point3 = new Point(2, 1);  //第二条相交线另一个坐标
//        list.add(point);
//        list.add(point1);
//        list.add(point2);
//        list.add(point3);

//        Point point = new Point(0, 0);  //移动线的坐标 ：集合中第0个
//        Point point1 = new Point(2, 2);  //移动线的坐标
//        Point point2 = new Point(1, 0);  //第一条相交线另一个坐标
//        Point point3 = new Point(1, 0);  //第二条相交线另一个坐标

        Point point = new Point(3, 2);  //移动线的坐标 ：集合中第0个
        Point point1 = new Point(5, 5);  //移动线的坐标
        Point point2 = new Point(1, 6);  //第一条相交线另一个坐标
        Point point3 = new Point(1, 6);  //第二条相交线另一个坐标
        list.add(point);
        list.add(point1);
        list.add(point2);
        list.add(point3);
    }

    public List<Point> getPoint(List<Point> list, Point movePoint) {

        List<Point> moveinwardPoint = new ArrayList<>();
        float moveLineK = DrawUtils.calSlope(list.get(0), list.get(1)); //移动线的斜率
        float crossLineK = DrawUtils.calSlope(list.get(1), list.get(2)); //与移动线相交的其他两条线的斜率

        float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
        float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);

        float crossLineK1 = DrawUtils.calSlope(list.get(0), list.get(3));  //与移动线相交的其他两条线的斜率
        float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
        float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);


        Log.e("FirstActivity", "moveLineK:" + moveLineK + "/" + "crossLineK:" + crossLineK + "/" +
                "crosspointX:" + crosspointX + "/ " + "crosspointY:" + crosspointY);
        Log.e("FirstActivity", "moveLineK:" + moveLineK + "/" + "crossLineK1:" + crossLineK1 + "/" +
                "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);

        moveinwardPoint.add(new Point(crosspointX, crosspointY));
        moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
        return moveinwardPoint;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                drawPloygonView2.cleanDraw();
                break;
            default:
                break;
        }

    }
}
