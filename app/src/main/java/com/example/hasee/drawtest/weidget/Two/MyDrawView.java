package com.example.hasee.drawtest.weidget.Two;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.hasee.drawtest.model.MyComparator;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HASEE on 2017/7/27 16:04
 */

public class MyDrawView extends View {

    private Path path;
    private Paint paint;
    private float startX, startY, lastX, lastY;
    private int downPosition;
    private List<List<Point>> twofoldList;
    private List<TwoPointDistance> distanceList;
    private List<Point> intentPoints, startPointList;
    private List<PoPoListModel> pointModelsList, startModelList;
    private float adsorbDis;  //吸附距离
    private float toSidebDis;  //吸附距离
    private Point first, second;
    private Point duan1;//选中线的端点1
    private Point duan2;//选中线的端点2
    private float lStartX;//drawLine的开始点
    private float lStartY;
    private float lStopX;//drawLine的结束点
    private float lStopY;
    private int paintWidth = 10; //红色圆的半径


    public MyDrawView(Context context) {
        super(context);
        init(context);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        twofoldList = new ArrayList<>();
        distanceList = new ArrayList<>();
        pointModelsList = new ArrayList<>();
        intentPoints = new ArrayList<>();
        startPointList = new ArrayList<>();
        startModelList = new ArrayList<>();
        adsorbDis = DensityUtil.px2dip(context, 100);
        toSidebDis = DensityUtil.px2dip(context, 20);
        Log.e("MyDrawView", "adsorbDis:" + adsorbDis + "/toSidebDis:" + toSidebDis);

    }

    public void setTwofoldList(List<Point> pointList) {
        twofoldList.add(pointList);
    }

    public void setAllList(List<List<Point>> list) {
        this.twofoldList = list;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        if (twofoldList != null && twofoldList.size() > 0) {
            paint.setColor(Color.BLACK);
            for (List<Point> points : twofoldList) {
                if (points != null && points.size() > 0) {
                    path.moveTo(points.get(0).getX(), points.get(0).getY());
                    for (int i = 1; i < points.size(); i++) {
                        path.lineTo(points.get(i).getX(), points.get(i).getY());
                    }
                }
                path.close();
            }
        }
        canvas.drawPath(path, paint);

        if (first != null && second != null) {
            paint.setColor(Color.RED);
//            canvas.drawCircle(first.getX(), first.getY(), paintWidth / 2, mPaint);
            canvas.drawCircle(second.getX(), second.getY(), paintWidth / 2, paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = e.getX();
                startY = e.getY();
                pointModelsList.clear();
                for (List<Point> points : twofoldList) {
                    if (points != null && points.size() > 0) {
                        downPosition = ensurePoint(points, startX, startY);
                        PoPoListModel poPoListModel = new PoPoListModel();
                        poPoListModel.setPosition(downPosition);
                        poPoListModel.setList(points);
                        pointModelsList.add(poPoListModel);
                        Log.e("MyDrawView", "downPosition:" + downPosition);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = e.getX();
                lastY = e.getY();
                moveLine(pointModelsList, lastX - startX, lastY - startY);
                startX = lastX;
                startY = lastY;

                break;
            case MotionEvent.ACTION_UP:
                // TODO: 2017/7/26
                adsorbResult(pointModelsList);
                break;
        }
        // 更新绘制
        invalidate();
        return true;
    }


    /**
     * 确定点击点
     *
     * @param startX 按下时的X
     * @param startY 按下时的Y
     * @return
     */
    private int ensurePoint(List<Point> movePoints, float startX, float startY) {
        //形成的多边形要首尾相接
        List<Point> pp = new ArrayList<>();
        pp.addAll(movePoints);
        pp.add(movePoints.get(0));
        int position = -1;
        double minL = DrawUtils.pointToLine(startX, startY, movePoints.get(movePoints.size() - 1), movePoints.get(0));
        for (int i = 0; i < movePoints.size() - 1; i++) {
            double l1 = DrawUtils.pointToLine(startX, startY, movePoints.get(i), movePoints.get(i + 1));
            if (minL < l1) {
            } else if (minL > l1) {
                position = i;
                minL = l1;
            }
        }
        if (minL <= toSidebDis) {
            return position;//根据position获取要移动线的 端点
        } else if (DrawUtils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > toSidebDis) {
            return -2;//表示点击点在多边形内,执行移动view
        } else {
            return -3;//表示点击点在多边形外,不执行任何操作
        }
    }

    /**
     * 移动线
     */
    List<Point> movePoints = new ArrayList<>();//用于存放变化的点

    public void moveLine(List<PoPoListModel> list, float dx, float dy) {
        movePoints.clear();
        List<Point> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PoPoListModel poPoListModel = list.get(i);
            boolean isInside = poPoListModel.isInside();
            List<Point> singlePointList = poPoListModel.getList(); //点的集合
            int position = poPoListModel.getPosition();

            if (singlePointList.size() == 3) {  //三角形
                if (position == -1) {   //选中倒数第二个点和起点的那条直线
                    duan1 = singlePointList.get(0);
                    duan2 = singlePointList.get(singlePointList.size() - 1);

                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(1));
                    newList.add(singlePointList.get(1));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(singlePointList.size() - 1, pointList.get(1));

                } else if (position == -2) {  //选中整体图形{
                    moveView(pointModelsList, dx, dy);
                } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
//                    return;
                } else if (position == 0) {
                    duan1 = singlePointList.get(position);
                    duan2 = singlePointList.get(position + 1);

                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(2));
                    newList.add(singlePointList.get(2));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(position + 1, pointList.get(1));
                } else if (position == 1) {
                    duan1 = singlePointList.get(position);
                    duan2 = singlePointList.get(position + 1);
                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(0));
                    newList.add(singlePointList.get(0));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(position + 1, pointList.get(1));
                }
            } else if (singlePointList.size() > 3) {   //表示多边形
                //通过ensurePoint获得position,判断position
                if (position == -1) {   //选中倒数第二个点和起点的那条直线
                    duan1 = singlePointList.get(0);
                    duan2 = singlePointList.get(singlePointList.size() - 1);

                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(singlePointList.size() - 2));
                    newList.add(singlePointList.get(1));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(singlePointList.size() - 1, pointList.get(1));

                } else if (position == -2) {  //选中整体图形
                    moveView(pointModelsList, dx, dy);
                    return;
                } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
//                    return;
                } else if (position == 0) {  //index=-1旁边的直线
                    duan1 = singlePointList.get(position);
                    duan2 = singlePointList.get(position + 1);

                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(position + 2));
                    newList.add(singlePointList.get(singlePointList.size() - 1));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(position + 1, pointList.get(1));

                } else if (position == singlePointList.size() - 2) { //index=-1旁边的直线

                    duan1 = singlePointList.get(position);
                    duan2 = singlePointList.get(position + 1);
                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(0));
                    newList.add(singlePointList.get(position - 1));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(position + 1, pointList.get(1));

                } else {
                    duan1 = singlePointList.get(position);
                    duan2 = singlePointList.get(position + 1);
                    newList.add(duan1);
                    newList.add(duan2);
                    newList.add(singlePointList.get(position + 2));
                    newList.add(singlePointList.get(position - 1));

                    List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                    singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                    singlePointList.set(position + 1, pointList.get(1));
                }

            }


//            if (position == -3) {  //表示点击点在多边形外,不执行任何操作
////                return;
//            } else if (position == -2) {  //选中整体图形
//                moveView(pointModelsList, dx, dy);
//            }



            /* else if (position == -1) {  //选中倒数第二个点和起点的那条直线
                movePoints.addAll(poPoListModel.getList());
                duan1 = movePoints.get(movePoints.size() - 1);
                duan2 = movePoints.get(0);

                //线段垂直,只能左右平移
                if (duan1.getX() == duan2.getX()) {
                    duan1.setX(duan1.getX() + dx);
                    duan2.setX(duan2.getX() + dx);
                }
                //线段水平,只能上下平移
                if (duan1.getY() == duan2.getY()) {
                    duan1.setY(duan1.getY() + dy);
                    duan2.setY(duan2.getY() + dy);
                }
                movePoints.set(movePoints.size() - 1, duan1);
                movePoints.set(0, duan2);
            } else {  //表示选中其他的边
                movePoints.addAll(poPoListModel.getList());
                duan1 = movePoints.get(position);
                duan2 = movePoints.get(position + 1);

                //线段垂直,只能左右平移
                if (duan1.getX() == duan2.getX()) {
                    duan1.setX(duan1.getX() + dx);
                    duan2.setX(duan2.getX() + dx);
                }
                //线段水平,只能上下平移
                if (duan1.getY() == duan2.getY()) {
                    duan1.setY(duan1.getY() + dy);
                    duan2.setY(duan2.getY() + dy);
                }
                movePoints.set(position, duan1);
                movePoints.set(position + 1, duan2);

            }*/
        }
//        drawLine(movePoints);
//        twofoldList.clear();
//        twofoldList.add(movePoints);
    }


    /**
     * 重绘选中直线后图形
     *
     * @param points
     */
    List<Point> linePoints = new ArrayList<>();
    boolean isDrawLine;//是否划线

    public void drawLine(List<Point> points) {
        isDrawLine = true;
        path.reset();
        path.moveTo(points.get(downPosition + 1).getX(), points.get(downPosition + 1).getY());
        linePoints.clear();
        for (int i = downPosition + 1; i < points.size(); i++) {  //画选中那条线（红线）的下半部分
            linePoints.add(points.get(i));
        }
        for (int i = 0; i < downPosition + 1; i++) { //画选中那条线（红线）的上半部分
            linePoints.add(points.get(i));
        }
        for (Point point : linePoints) {  //画红线
            path.lineTo(point.getX(), point.getY());
        }
        lStartX = duan1.getX();
        lStartY = duan1.getY();
        lStopX = duan2.getX();
        lStopY = duan2.getY();
        invalidate();
    }

    /**
     * 移动VIEW
     *
     * @param dx
     * @param dy
     */
    public void moveView(List<PoPoListModel> pointListModels, float dx, float dy) {
        for (PoPoListModel pointListModel : pointListModels) {
            int position = pointListModel.getPosition();
            if (position == -2) {
                List<Point> list = pointListModel.getList();
                for (Point p : list) {
                    p.setX(p.getX() + dx);
                    p.setY(p.getY() + dy);
                }
                anewDraw(list, path);
            } else {
                List<Point> list = pointListModel.getList();
//                aOldDraw(list, path);
            }
        }
//        isDrawLine = false;
//        twofoldList.clear();
//        twofoldList.add(movePoints);
    }


    /**
     * 吸附后重置数据
     */
//    public void adsorbResult(List<Point> intentPoints, List<Point> startPointList) {
    public void adsorbResult(List<PoPoListModel> pointModelsList) {
        startModelList.clear();
        for (int k = 0; k < pointModelsList.size(); k++) {
            PoPoListModel poPoListModel = pointModelsList.get(k);
            int position = poPoListModel.getPosition();
            if (position == -2) {
                intentPoints = poPoListModel.getList();
                pointModelsList.remove(k);
                for (PoPoListModel poListModel : pointModelsList) {
                    startModelList.add(poListModel);
                }
                distanceList.clear();
                for (int i = 0; i < intentPoints.size(); i++) {
                    Point pointI = intentPoints.get(i);
                    for (PoPoListModel poListModel : startModelList) {
                        startPointList = poListModel.getList();
                        for (int j = 0; j < startPointList.size(); j++) {
                            Point pointJ = startPointList.get(j);
                            float distance = DrawUtils.calTwoPointDistance(pointI, pointJ);
                            if (distance < adsorbDis) {
                                TwoPointDistance twoPointDistance = new TwoPointDistance(pointI, pointJ, distance);
                                distanceList.add(twoPointDistance);
                            }
                        }
                    }
                }
                if (distanceList != null && distanceList.size() > 0) {
                    Collections.sort(distanceList, new MyComparator());
                    for (TwoPointDistance twoPointDistance : distanceList) {
                        Log.e("DrawTestView1", "twoPointDistance:" + twoPointDistance.toString());
                    }
                    first = distanceList.get(0).getFirst();
                    second = distanceList.get(0).getSecond();
                    float dx_xifu = second.getX() - first.getX();
                    float dy_xifu = second.getY() - first.getY();

                    List<Point> newPointList = new ArrayList<>();
                    for (Point point : intentPoints) {
                        float newX = point.getX() + dx_xifu;
                        float newY = point.getY() + dy_xifu;
                        Point newPoint = new Point(newX, newY);
                        newPointList.add(newPoint);
                    }

                    intentPoints.clear();
                    intentPoints.addAll(newPointList);
                    anewDraw(intentPoints, path);
                    aOldDraw(startPointList, path);
                    invalidate();
                }
            }


//        distanceList.clear();
//        for (int i = 0; i < intentPoints.size(); i++) {
//            Point pointI = intentPoints.get(i);
//            for (int j = 0; j < startPointList.size(); j++) {
//                Point pointJ = startPointList.get(j);
//                float distance = DrawUtils.calTwoPointDistance(pointI, pointJ);
//                if (distance < adsorbDis) {
//                    TwoPointDistance twoPointDistance = new TwoPointDistance(pointI, pointJ, distance);
//                    distanceList.add(twoPointDistance);
//                }
//            }
//        }
//        if (distanceList != null && distanceList.size() > 0) {
//            Collections.sort(distanceList, new MyComparator());
//            for (TwoPointDistance twoPointDistance : distanceList) {
//                Log.e("DrawTestView1", "twoPointDistance:" + twoPointDistance.toString());
//            }
//            first = distanceList.get(0).getFirst();
//            second = distanceList.get(0).getSecond();
//            float dx_xifu = second.getX() - first.getX();
//            float dy_xifu = second.getY() - first.getY();
//
//            List<Point> newPointList = new ArrayList<>();
//            for (Point point : intentPoints) {
//                float newX = point.getX() + dx_xifu;
//                float newY = point.getY() + dy_xifu;
//                Point newPoint = new Point(newX, newY);
//                newPointList.add(newPoint);
//            }
//            intentPoints.clear();
//            intentPoints.addAll(newPointList);
//
//            anewDraw(intentPoints, path);
//            aOldDraw(startPointList, path);
//            invalidate();
        }
    }

    /**
     * 选中整体图形后移动并重新绘制
     *
     * @param points 点集合
     */
    public void anewDraw(List<Point> points, Path mPath) {
        mPath.reset();
        mPath.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point point : points) {
            mPath.lineTo(point.getX(), point.getY());
        }
        mPath.lineTo(points.get(0).getX(), points.get(0).getY());
        invalidate();
    }

    public void aOldDraw(List<Point> startPointList, Path testPath) {
        // TODO: 2017/7/26
        testPath.moveTo(startPointList.get(0).getX(), startPointList.get(0).getY());
        for (int i = 1; i < startPointList.size(); i++) {
            Point point = startPointList.get(i);
            testPath.lineTo(point.getX(), point.getY());
        }
        testPath.close();
    }


    /**
     * 求改变后的两个新坐标
     *
     * @param list
     * @param movePoint
     * @return
     */
    public List<Point> getPoint(List<Point> list, Point movePoint) {
        float nan = Float.NaN;

        List<Point> moveinwardPoint = new ArrayList<>();
        float moveLineK = DrawUtils.calSlope(list.get(0), list.get(1)); //移动线的斜率
        float crossLineK = DrawUtils.calSlope(list.get(1), list.get(2)); //与移动线相交的其他两条线的斜率
        float crossLineK1 = DrawUtils.calSlope(list.get(0), list.get(3));  //与移动线相交的其他两条线的斜率

        Log.e("MoveLineView", "moveLineK:" + moveLineK + "/" + "crossLineK:" + crossLineK + "/" +
                "crossLineK1:" + crossLineK1);

        if (Float.isInfinite(moveLineK) || Float.isNaN(moveLineK)) {  //竖线，水平方向移动  isNaN无穷大  isInfinite无意义的(分母为0)
            if (crossLineK == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = DrawUtils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = list.get(1).getY();
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            } else if (crossLineK1 == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = list.get(0).getY();
                float crosspointY1 = DrawUtils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            } else if (crossLineK == 0 && crossLineK1 == 0) {  //两条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = list.get(0).getY();
                float crosspointY1 = list.get(1).getY();
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            } else {   //两条相交线既不水平也不垂直
                float crosspointX = movePoint.getX();
                float crosspointY = DrawUtils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = DrawUtils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            }
        } else if (moveLineK == 0) {  //选中的线是横线，垂直方向移动
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {
//                crossLineK1 = 0;
//                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
//                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
//                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
                float crosspointY1 = list.get(0).getY() + dy;
//                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            } else {
                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
                float crosspointY = list.get(1).getY() + dy;
//                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            } else {
                float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            }

        } else {  //选中的边既不垂直也不水平
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {
//                crossLineK1 = 0;
//                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
//                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
//                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
//                float crosspointY1 = list.get(0).getY() + dy;
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            } else {
                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
//                float crosspointY = list.get(1).getY() + dy;
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            } else {
                float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            }
        }

        return moveinwardPoint;

    }
//    public List<Point> getPoint(List<Point> list, Point movePoint) {
//        float nan = Float.NaN;
//
//        List<Point> moveinwardPoint = new ArrayList<>();
//        float moveLineK = DrawUtils.calSlope(list.get(0), list.get(1)); //移动线的斜率
//        float crossLineK = DrawUtils.calSlope(list.get(1), list.get(2)); //与移动线相交的其他两条线的斜率
//        float crossLineK1 = DrawUtils.calSlope(list.get(0), list.get(3));  //与移动线相交的其他两条线的斜率
//
//        Log.e("MoveLineView", "moveLineK:" + moveLineK + "/" + "crossLineK:" + crossLineK + "/" +
//                "crossLineK1:" + crossLineK1);
//
////        if (Float.isNaN(moveLineK)) {  //无穷大
//        if (Float.isInfinite(moveLineK) || Float.isNaN(moveLineK)) {  //无穷大
//            float crosspointX = movePoint.getX();
//            float crosspointY = list.get(0).getY();
//            float crosspointY1 = list.get(1).getY();
//
//            moveinwardPoint.add(new Point(crosspointX, crosspointY));
//            moveinwardPoint.add(new Point(crosspointX, crosspointY1));
//
//        } else {
//            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {
//                crossLineK1 = 0;
//                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
//                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
//                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
//                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
//            }else{
//                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
//                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
//                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
//                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
//            }
//            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {
//
//                float dy = lastY - startY;
//                float crosspointX = list.get(0).getX();
//                float crosspointY = list.get(0).getY()+dy;
////                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
//                moveinwardPoint.add(new Point(crosspointX, crosspointY));
//                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
//            }else{
//                float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
//                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
//                moveinwardPoint.add(new Point(crosspointX, crosspointY));
//                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
//            }
//        }
//
//        return moveinwardPoint;
//
//    }

}
