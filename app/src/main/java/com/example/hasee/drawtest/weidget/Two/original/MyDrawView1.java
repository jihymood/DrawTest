package com.example.hasee.drawtest.weidget.Two.original;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.hasee.drawtest.model.Line;
import com.example.hasee.drawtest.model.MyComparator;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HASEE on 2017/7/27 16:04
 * 在MyDrawView1基础上修改
 * 移动边成功View
 */

public class MyDrawView1 extends View {

    private Path path;
    private Paint paint,textPaint;
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


    private int mColor = 0xFF000000;
    private int eColor = 0xFFFF0000;
    private int tColor = 0xFFFF0000;
    private boolean showParallelLine, showLength, showCutOffLine;//是否显示截止线，平行参考线，长度
    private boolean flag = true;//默认多边形移动的一边相对原点是扩大的
    private List<Line> lineList = new ArrayList<>();
    private List<Line> noSlopeLineList = new ArrayList<>();
    private List<Line> slope_0_LineList = new ArrayList<>();
    private List<Line> hasSlope_lineList = new ArrayList<>();
    private int count;
    private boolean intoGetReference;
    private boolean showReference;//是否显示参考线信息
    private List<Point> pointList = new ArrayList<>();
    private List<Point> rotateCenter = new ArrayList<Point>(pointList.size() + 1);//多边形旋转中心
    private List<Line> referLines = new ArrayList<Line>(pointList.size());//多边形旋转中心

    public MyDrawView1(Context context) {
        super(context);
        init(context);
    }

    public MyDrawView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyDrawView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        //绘制距离的画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(tColor);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.SERIF);

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
        //画各个图形
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

        //画吸附后的红点
        if (first != null && second != null) {
            paint.setColor(Color.RED);
            canvas.drawCircle(second.getX(), second.getY(), paintWidth / 2, paint);
        }

        //画标注
        if (pointModelsList != null && pointModelsList.size() > 0) {
            for (PoPoListModel poPoListModel : pointModelsList) {
                lineList.clear();
                List<Point> list = poPoListModel.getList();
                int position = poPoListModel.getPosition();
                boolean showLength = poPoListModel.isShowLength();
                if (position == -2 && showLength == true) {
                    getRotateCenter(list);
                    getRefereceInfo(list);
                    drawReference(lineList, canvas);
                }
            }
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
                        if (downPosition == -2) {
                            poPoListModel.setShowLength(true);
                        }
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
        }
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
    }


    /**
     * 吸附后重置数据
     */
//    public void adsorbResult(List<Point> intentPoints, List<Point> startPointList) {
    public void adsorbResult(List<PoPoListModel> pointModelsList) {
        if (pointModelsList != null && pointModelsList.size() > 1) {  //有多个图形的时候
//            intentPoints.clear();  //选中图形点集合
            startModelList.clear();

            for (int k = 0; k < pointModelsList.size(); k++) {
                PoPoListModel poModel = pointModelsList.get(k);
                int position = poModel.getPosition();
                if (position == -2) {
                    intentPoints = poModel.getList();
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
            }
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


    public double calDistanceByTwoPoint(Point p1, Point p2) {
        DecimalFormat df = new DecimalFormat("#.00");
        double result = (Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2)));
        return Double.parseDouble(df.format(result));
    }

    public double calLengthInLine(Line line) {
        Point p1 = line.getP1();
        Point p2 = line.getP2();
        return calDistanceByTwoPoint(p1, p2);
    }

    /*两点计算斜率 只限于斜率存在*/
    public double calSlope(Point p1, Point p2) {
        return (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    }

    /*直线两端点计算斜率*/
    public double calSlopeFromLine(Line line) {
        Point p1 = line.getP1();
        Point p2 = line.getP2();
        return (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    }


    public void judgeSlope(Line line, Point p1, Point p2) {
                /*判断斜率不存在的情况  即垂直直线*/
        if (p1.getX() == p2.getX()) {
            line.setSlope(-0.00);
            if (p1.getY() > p2.getY()) {//垂直线段从下往上绘制
                line.setDegree(270);
            } else {
                line.setDegree(90);//斜率不存在，设置degree为888，防止和斜率为0冲突默认值999
            }
            noSlopeLineList.add(line);
        }/*斜率为0的情况，即水平直线*/
        if (p1.getY() == p2.getY()) {
            line.setSlope(0);
            if (p1.getX() > p2.getX()) {//水平线段从右向左绘制
                line.setDegree(180);
            } else {
                line.setDegree(0);
            }
            slope_0_LineList.add(line);
        }
        if ((p1.getX() != p2.getX()) && (p1.getY() != p2.getY())) {//斜线
            double slope = calSlope(p1, p2);
            line.setSlope(slope);
            /*角度范围确保在0到360之间*/
            double degree = Math.toDegrees(Math.atan(slope));
            if (p1.getX() > p2.getX()) {
                if (p1.getY() < p2.getY()) {
                    if (degree < 0) {
                        degree = 180 + degree;
                    }
                } else {
                    if (degree > 0) {
                        degree = 180 + degree;
                    }

                }
            } else {
                if (p1.getY() < p2.getY()) {

                } else {
                    if (degree < 0) {
                        degree = 360 + degree;
                    }
                }
            }
            line.setDegree(degree);
            hasSlope_lineList.add(line);
        }
    }

    /*获取drawLine所需要的text，坐标点*/
    public void drawLengthText(List<Line> lines, Canvas canvas, Paint paint) {
        for (int i = 0; i < lines.size(); i++) {
            String length = String.valueOf(lines.get(i).getLength());
            float anchorX = (lines.get(i).getP1().getX() + lines.get(i).getP2().getX()) / 2;
            float anchorY = (lines.get(i).getP1().getY() + lines.get(i).getP2().getY()) / 2;
            canvas.drawText(length, anchorX, anchorY, paint);
        }
    }

    /*画标注的方法*/
    public void drawReference(List<Line> lines, Canvas canvas) {
        Log.e("ReferenceView", "drawReference()调用了 ");
        if (intoGetReference) {
            Log.e("ReferenceView", "drawReference()生效了 ");
        /*做排除判断*/
            Line line;
            Point p1 = null, p2 = null;
            String length = null;
            double degree = 999;//默认值999，不存在是888
            double slope = -0.0;//默认值-0.0不存在，不存在的时候显示-0.0
            for (int i = 0; i < lines.size(); i++) {
            /*因为所有标注线的绘制都是以第一条边为参考，所以要判断第一条边的位置状态*/
                if (i == 0) {
                    line = lines.get(0);
                    p1 = line.getP1();
                    p2 = line.getP2();
                    length = String.valueOf(line.getLength());
                    degree = line.getDegree();
                    slope = line.getSlope();
                    if ("-0.0".equals(String.valueOf(slope))) {//斜率不存在，即是垂直线段
                        drawReferLine(0, 90, length, p1, p2, textPaint, canvas);
                    }
                    if (slope == 0.0) {//水平线段
                        drawReferLine(0, 0, length, p1, p2, textPaint, canvas);
                    } else {//正常的斜率，包含正负
                        drawReferLine(0, degree, length, p1, p2, textPaint, canvas);
                    }
                /*其他边的绘制逻辑*/
                } else {
                /*角度叠加判断 判断上一条线段与x轴的夹角做叠加操作*/
                    line = lines.get(i);
                    p1 = line.getP1();
                    p2 = line.getP2();
                    length = String.valueOf(line.getLength());
                    double flagDegree = lines.get(i - 1).getDegree();//获取上一条线段与x轴的夹角
                    double nowDegree = lines.get(i).getDegree();//现在这条线段与x轴的夹角
                    degree = nowDegree - flagDegree;
                    drawReferLine(i, degree, length, p1, p2, textPaint, canvas);
                }
            }
//        drawReferLine(lines.size(),degree,length,p1,p2,textPaint,canvas);
        }
        canvas.restoreToCount(1);
    }

    /*显示标注的方法*/
    public void getRefereceInfo(List<Point> list) {
        intoGetReference = true;
        /*遍历集合得到List<Line>*/
        for (int i = 0; i < list.size(); i++) {
            Line line = new Line();
            if (i == list.size() - 1) {
                line.setIndex(list.size());
                Point p1 = list.get(list.size() - 1);
                Point p2 = list.get(0);
                judgeSlope(line, p1, p2);
                line.setP1(p1);
                line.setP2(p2);
                line.setLength(calDistanceByTwoPoint(list.get(0), list.get(list.size() - 1)));
                lineList.add(line);
            } else {
                line.setIndex(i + 1);
                Point p1 = list.get(i);
                Point p2 = list.get(i + 1);
                judgeSlope(line, p1, p2);
                line.setP1(p1);
                line.setP2(p2);
                line.setLength(calDistanceByTwoPoint(list.get(i), list.get(i + 1)));
                lineList.add(line);
            }
            Log.d("ss", i + ":" + lineList.get(i).toString());
        }
    }

    /*画每条边的通用方法 参数i表示lineList中line的下标*/
    public void drawReferLine(int i, double degree, String length, Point p1, Point p2, Paint textPaint, Canvas canvas) {
        float stopLength = 40;//截止线距离边的出自己距离
        float textHeight = 10;//长度text距离边的高度
        double mLength = Double.valueOf(length);//边长
        float x1, y1, x2, y2;
        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        x1 = rotateCenter.get(i).getX();
        y1 = rotateCenter.get(i).getY();
        if (i == 0) {
            if (y1 == y2) {//水平线段

                x2 = p2.getX();
                y2 = p2.getY();
            } else {//因为是第一条直线，所以垂直线段和斜线两种情况相等

                x2 = (float) (x1 + mLength);
                y2 = y1;
            }

        } else {
            x2 = (float) (x1 + mLength);
            y2 = y1;
        }
        canvas.rotate((float) degree, x1, y1);

        float width = textPaint.measureText(length);
        float offSetX = (float) (x1 + ((mLength - width) / 2));
        float offHeight = y1 - stopLength / 2;
        canvas.drawLine(x1, offHeight, offSetX, offHeight, textPaint);//画截止线内的延伸线
        canvas.drawLine(x2, offHeight, (float) (x2 - (mLength - width) / 2), offHeight, textPaint);//画截止线内的延伸线
        canvas.drawLine(x1, y1, x1, y1 - stopLength, textPaint);//画截止线
        canvas.drawLine(x2, y2, x2, y1 - stopLength, textPaint);//画截至线
        canvas.drawText(length, offSetX, y1 - textHeight, textPaint);//长度text居中显示
        canvas.save();
    }

    public void getRotateCenter(List<Point> pointList) {
        rotateCenter.clear();
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0) {
                rotateCenter.add(pointList.get(0));
            } else {
                rotateCenter.add(new Point((float) (rotateCenter.get(i - 1).getX() + calDistanceByTwoPoint(pointList
                        .get(i), pointList.get(i - 1))), rotateCenter.get(0).getY()));
            }
        }
        /*添加最后一条线段的旋转中心*/
//        rotateCenter.add(new Point((float) (rotateCenter.get(pointList.size()-1).getX()+calDistanceByTwoPoint(pointList.get(0),pointList.get(pointList.size()-1))),rotateCenter.get(0).getY()));
    }

}
