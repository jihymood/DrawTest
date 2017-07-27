package com.example.hasee.drawtest.weidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MIUSHUKI on 2017/7/18.
 * <p>
 * 王宝帅部分功能
 */

public class MyPolygonEvent extends View {
    //-------------View相关-------------
    //View自身的宽和高
    private float mHeight;
    private float mWidth;

    //    屏幕中心点坐标
    private float centerX, centerY;
    private float downX, downY;
    private List<Point> linePointList;//存储离触点最近的直线的两个端点
    List<Point> dest1;
    private Point downPoint;
    float dx = 0.0f, dy = 0.0f;//move的距离
    int index;//标记需要改变坐标的两个点的下标。
    float curX;
    float curY;
    Path path, exPath;


    //-------------画笔相关-------------
    //多边形的画笔
    private Paint polyPaint;
    //延长线的画笔
    private Paint extendPaint;

    //-------------颜色相关-------------
    //多边形颜色
    private int mColor = 0xFF000000;
    //直线被选中后的颜色（extendLine的颜色）
    private int eColor = 0xFFFF0000;
    //-------------多边形各端点的集合-------------
    private List<Point> pointList = new ArrayList<>();

    public MyPolygonEvent(Context context) {
        super(context);
        initPaint();
    }

    public MyPolygonEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public MyPolygonEvent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化画笔
        //        initPaint();
        //画布移到中心点
        //        canvas.translate(mWidth / 2, mHeight / 2);
        //画n边形
//        exPath.reset();
//        path.reset();
        drawPolygon(pointList);
        canvas.drawPath(path, polyPaint);
        canvas.drawPath(exPath, extendPaint);
    }

    private void initPaint() {

        path = new Path();
        exPath = new Path();
        //        多边形画笔
        polyPaint = new Paint();
        polyPaint.setAntiAlias(true);
        polyPaint.setStyle(Paint.Style.STROKE);
        polyPaint.setColor(mColor);
        polyPaint.setStrokeWidth(3);
        polyPaint.setFilterBitmap(true);
        //        延长线画笔
        extendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        extendPaint.setStyle(Paint.Style.STROKE);
        extendPaint.setColor(eColor);
        extendPaint.setStrokeWidth(5);
        polyPaint.setFilterBitmap(true);
        /*设置虚线*/
        DashPathEffect effect = new DashPathEffect(new float[]{10f, 5f}, 0);
        extendPaint.setPathEffect(effect);
        //        多边形各个端点point坐标
        Point point1 = new Point(50, 50);
        Point point2 = new Point(100, 50);
        Point point3 = new Point(100, 100);
        Point point4 = new Point(200, 100);
        Point point5 = new Point(200, 300);
        Point point6 = new Point(50, 300);

        pointList = new ArrayList<>();
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        pointList.add(point5);
        pointList.add(point6);
        //        将源多边形的list数据拷贝到新list
        dest1 = new ArrayList();
        Collections.addAll(dest1, new Point[pointList.size()]);
        Collections.copy(dest1, pointList);
        Log.d("ss", dest1.toString());
    }

    /*原始多边形的画法*/
    private void drawPolygon(List<Point> list) {
        path.moveTo(list.get(0).x, list.get(0).y);
        for (int i = 1; i < list.size(); i++) {
            path.lineTo(list.get(i).x, list.get(i).y);
        }
        path.close();
    }

    //    获取屏幕中心点坐标
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeigt = MeasureSpec.getSize(heightMeasureSpec);
        //获取屏幕中心点
        centerX = measureWidth / 2;
        centerY = measureHeigt / 2;
        Log.i("ss", "onMeasure:屏幕中心点坐标" + centerX + ",  " + centerY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        curX = event.getX();
        curY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = curX;
                downY = curY;
                Log.d("ss", "触点的坐标是 " + downX + "   ," + downY);
                downPoint = new Point((int) curX, (int) curY);
                float judgeDis = 10;
                float culDis;//计算得出的点与一条直线的最短距离
                linePointList = new ArrayList<>();
                for (int i = 0; i < pointList.size(); i++) {
                    if (i != (pointList.size() - 1)) {
                        culDis = calDistanceByHelen(downPoint, pointList.get(i), pointList.get(i + 1));
                    } else {
                        culDis = calDistanceByHelen(downPoint, pointList.get((pointList.size()) - 1), pointList.get(0));
                    }

                    if (culDis < judgeDis && culDis != 0) {
                        if (i == (pointList.size() - 1)) {
                            linePointList.add(pointList.get(0));
                            linePointList.add(pointList.get(i));
                            Log.d("ss", "linePointList中添加的坐标是：index= " + 1 + "------->" + "(" + linePointList.get(1)
                                    .x + "," + linePointList.get(1).y + ")" + "index=" + 0 + "------->" + "(" +
                                    linePointList.get(0).x + "," + linePointList.get(0).y + ")");
                            drawExtendLine(pointList.get(0), pointList.get(pointList.size() - 1));
                            index = -1;//获取需要改变的点的下标
                            Log.d("ss", "index=  " + index);
                        } else {
                            linePointList.add(pointList.get(i));
                            linePointList.add(pointList.get(i + 1));
                            Log.d("ss", "linePointList中添加的坐标是：index= " + 0 + "------->" + "(" + linePointList.get(0)
                                    .x + "," + linePointList.get(0).y + ")" + "index=" + 1 + "------->" + "(" +
                                    linePointList.get(1).x + "," + linePointList.get(1).y + ")");
                            index = i;//获取需要改变的点的下标
                            Log.d("ss", "index=  " + index);
                                /*再次做判断0和1*/
                            drawExtendLine(pointList.get(i), pointList.get(i + 1));
                        }

                    }
                }
                break;


            case MotionEvent.ACTION_MOVE:
              /*  *//**//*指导思想：从linePointList取出所有点遍历。最多三个点。要么两点x坐标相等，要么两点y坐标相等
                * 根据move手势采集dx或者dy。当dy大于dx时，说明是垂直移动，找到x坐标相同的两个点。y坐标都移动y+dy。然后画图。否则，找到坐标相同的两个点。x坐标都移动x+dx。*//**//**/
                Point referPoint1 = null, referPoint2 = null;
                float referPoint1X = 0, referPoint1Y = 0, referPoint2X = 0, referPoint2Y = 0;
                if (linePointList.size() > 0) {
                    referPoint1 = linePointList.get(0);
                    referPoint2 = linePointList.get(1);
                    Log.d("ss", "从linePointList取出的点的坐标是：" + "(" + linePointList.get(0).x + "," + linePointList.get(0)
                            .y + ")" + "和  （" + linePointList.get(1).x + "," + linePointList.get(1).y + ")");
                             /*两个旧端点坐标  即起点坐标*/
                    referPoint1X = referPoint1.x;
                    referPoint1Y = referPoint1.y;
                    referPoint2X = referPoint2.x;
                    referPoint2Y = referPoint2.y;
                }
                dx = Math.abs(curX - downX);
                dy = Math.abs(curY - downY);

            /*    *//**//*两个新端点的坐标*//**//**/
                float newReferPoint1X = referPoint1X, newReferPoint2X = referPoint2X, newReferPoint1Y = referPoint1Y,
                        newReferPoint2Y = referPoint2Y;
                if (dx > dy) {
                 /* 水平移动  y坐标不变 x坐标变化curX*/
                /*判断扩大还是缩小*/
                /*判断curX的范围，要保证curX在两个y坐标点之间*/
                    if (Math.min(referPoint1Y, referPoint2Y) < curY && curY < Math.max(referPoint1Y, referPoint2Y)) {

//                        float dx1 = curX - downX;
//                        float temp = dx1;
//                        float dx2 = curX - dx1;
//                        newReferPoint1X = referPoint1X + dx1;
//                        newReferPoint2X = referPoint2X + dx1;
//                        if (index == -1) {
//
//                            pointList.get(0).x = (int) (pointList.get(0).x + dx1);
//                            pointList.get(pointList.size() - 1).x = (int) (pointList.get(pointList.size() - 1).x
//                                    + dx1);
//                            Log.e("DrawTestView_san", "水平移动增大index == -1" + dx1);
////                            Log.e("DrawTestView_san", "水平移动增大index == -1\n" + "point:" + pointList.get(0).x +
////                                    "/point1:" + pointList.get(pointList.size() - 1).x);
//                        } else {
//                            pointList.get(index).x = (int) (pointList.get(index).x + dx1);
//                            pointList.get(index + 1).x = (int) (pointList.get(index + 1).x + dx1);
//                            Log.e("DrawTestView_san", "水平移动增大index == -1" + dx1);
////                            Log.e("DrawTestView_san", "水平移动增大else\n" + "point:" + pointList.get(index).x +
////                                    "/point1:" + pointList.get(index + 1).x);
//                        }


                        if (curX > referPoint1X) {
                            newReferPoint1X = referPoint1X + dx;
                            newReferPoint2X = referPoint2X + dx;
                            if (index == -1) {
                                pointList.get(0).x = (int) (pointList.get(0).x + dx);
                                pointList.get(pointList.size() - 1).x = (int) (pointList.get(pointList.size() - 1).x
                                        + dx);
                                Log.e("DrawTestView_san", "水平移动增大index == -1\n" + "point:" + pointList.get(0).x +
                                        "/point1:" + pointList.get(pointList.size() - 1).x);
                            } else {
                                pointList.get(index).x = (int) (pointList.get(index).x + dx);
                                pointList.get(index + 1).x = (int) (pointList.get(index + 1).x + dx);
                                Log.e("DrawTestView_san", "水平移动增大else\n" + "point:" + pointList.get(index).x +
                                        "/point1:" + pointList.get(index + 1).x);
                            }
                        } else {
                            newReferPoint1X = referPoint1X - dx;
                            newReferPoint2X = referPoint2X - dx;
                            if (index == -1) {
                                pointList.get(0).x = (int) (pointList.get(0).x - dx);
                                pointList.get(pointList.size() - 1).x = (int) (pointList.get(pointList.size() - 1).x
                                        - dx);
                                Log.e("DrawTestView_san", "水平移动减小index == -1\n" + "point:" + pointList.get(0).x +
                                        "/point1:" + pointList.get(pointList.size() - 1).x);

                            } else {
                                pointList.get(index).x = (int) (pointList.get(index).x - dx);
                                pointList.get(index + 1).x = (int) (pointList.get(index + 1).x - dx);
                                Log.e("DrawTestView_san", "水平移动减小else\n" + "point:" + pointList.get(index).x +
                                        "/point1:" + pointList.get(index + 1).x);
                            }
                        }



                    }
                    exPath.moveTo(referPoint1X, referPoint1Y);
                    exPath.lineTo(newReferPoint1X, referPoint1Y);
                    exPath.moveTo(referPoint2X, referPoint2Y);
                    exPath.lineTo(newReferPoint2X, referPoint2Y);
                }
                /* /* 垂直移动 x坐标不动 y坐标变化curY*/
              /*判断扩大还是缩小*/
                if (dx < dy) {
                    if (Math.min(referPoint1X, referPoint2X) < curX && curX < Math.max(referPoint1X, referPoint2X)) {
                        if (curY > referPoint1Y) {
                            newReferPoint1Y = referPoint1Y + dy;
                            newReferPoint2Y = referPoint2Y + dy;
                            if (index == -1) {
                                pointList.get(0).y = (int) (pointList.get(0).y + dy);
                                pointList.get(pointList.size() - 1).y = (int) (pointList.get(pointList.size() - 1).y
                                        + dy);
                            } else {
                                pointList.get(index).y = (int) (pointList.get(index).y + dy);
                                pointList.get(index + 1).y = (int) (pointList.get(index + 1).y + dy);
                            }
                        } else {
                            newReferPoint1Y = referPoint1Y - dy;
                            newReferPoint2Y = referPoint2Y - dy;
                            if (index == -1) {
                                pointList.get(0).y = (int) (pointList.get(0).y - dy);
                                pointList.get(pointList.size() - 1).y = (int) (pointList.get(pointList.size() - 1).y
                                        - dy);
                            } else {
                                pointList.get(index).y = (int) (pointList.get(index).y - dy);
                                pointList.get(index + 1).y = (int) (pointList.get(index + 1).y - dy);
                            }
                        }
                    }

                    exPath.moveTo(referPoint1X, referPoint1Y);
                    exPath.lineTo(referPoint1X, newReferPoint1Y);
                    exPath.moveTo(referPoint2X, referPoint2Y);
                    exPath.lineTo(referPoint2X, newReferPoint2Y);
                }
                break;

            case MotionEvent.ACTION_UP:
                exPath.reset();
                path.reset();
                drawPolygon(pointList);
                break;
            default:
                break;

        }
        invalidate();// 通知刷新界面
        return true;

    }

    /*判断点到直线的距离 不适用与水平和垂直的直线*/
    public float calDistanceToLine(Point point, Point first, Point second) {
            /*直线中点坐标*/
        //        Point pointw=new Point((first.x+second.x)/2,(first.y+second.y)/2);
        float midX = (first.x + second.x) / 2;
        float midY = (first.y + second.y) / 2;
        /*直线的斜率*/
        float slope1 = (second.y - first.y) / (first.x - second.x);
        /*直线垂线的斜率*/
        float slope2 = -1 / slope1;
        /*垂足坐标*/
        float pedalX = (midY - first.y + slope1 * first.x - midX * slope2) / (slope1 - slope2);
        float pedalY = midY + (pedalX - midX) * slope2;

        return (float) Math.sqrt((midY - pedalY) * (midY - pedalY) + (midY - pedalX) * (midX - pedalX));
    }

    /*海伦公式求点到直线的距离  原理：求出三点围成的三角形的面积除以底边（直线的长度）得到三角形的高（bug是理论上可能存在点在直线上 的情况）*/
    public float calDistanceByHelen(Point point, Point first, Point second) {
        float firstX = first.x;
        float firstY = first.y;

        float secondX = second.x;
        float secondY = second.y;

        float pointX = point.x;
        float pointY = point.y;
        /*直线的长度*/
        float lineDis = (float) Math.sqrt(Math.pow(secondX - firstX, 2) + Math.pow(secondY - firstY, 2));
        /*另外两边的长度*/
        float dis1 = (float) Math.sqrt(Math.pow(pointX - firstX, 2) + Math.pow(pointY - firstY, 2));
        float dis2 = (float) Math.sqrt(Math.pow(pointX - secondX, 2) + Math.pow(pointY - secondY, 2));
        /*三角形周长的一半*/
        float perimeterHalf = (lineDis + dis1 + dis2) / 2;
        /*面积*/
        float area = (float) Math.sqrt(perimeterHalf * (perimeterHalf - lineDis) * (perimeterHalf - dis1) *
                (perimeterHalf - dis2));
        return area * 2 / lineDis;

    }

    /*判断点是否在直线的延长线上 筛选出来的点两两的X坐标或者Y坐标相等*/
    //    public boolean pointInLine(Point downPoint,Point first,Point second){ }
    private void drawExtendLine(Point point1, Point point2) {

        exPath.moveTo(point1.x, point1.y);
        exPath.lineTo(point2.x, point2.y);
    }


}
