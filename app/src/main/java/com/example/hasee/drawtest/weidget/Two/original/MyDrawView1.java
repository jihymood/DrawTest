package com.example.hasee.drawtest.weidget.Two.original;

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
 * 原始
 */

public class MyDrawView1 extends View {

    private Path path;
    private Paint paint;
    private List<List<Point>> twofoldList;
    private float startX, startY, lastX, lastY;
    private int downPosition;
    private List<TwoPointDistance> distanceList;
    private List<PoPoListModel> pointModelsList;
    float adsorbDis;  //吸附距离
    private Point first, second;
    private Point duan1;//选中线的端点1
    private Point duan2;//选中线的端点2
    private float lStartX;//drawLine的开始点
    private float lStartY;
    private float lStopX;//drawLine的结束点
    private float lStopY;

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
        paint = new Paint();
        path = new Path();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        twofoldList = new ArrayList<>();
        distanceList = new ArrayList<>();
        pointModelsList = new ArrayList<>();
        adsorbDis = DensityUtil.px2dip(context, 100);
        Log.e("DrawPolygonView1", "adsorbDis:" + adsorbDis);

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
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = e.getX();
                startY = e.getY();
                pointModelsList.clear();
                for (List<Point> points : twofoldList) {
                    downPosition = ensurePoint(points, startX, startY);
                    PoPoListModel poPoListModel = new PoPoListModel();
                    poPoListModel.setPosition(downPosition);
                    poPoListModel.setList(points);
                    pointModelsList.add(poPoListModel);
                    Log.e("MyDrawView", "downPosition:" + downPosition);
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
//                adsorbResult(intentPoints, startPointList);
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
        if (minL <= 40) {
            return position;//根据position获取要移动线的 端点
        } else if (DrawUtils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > 40) {
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
        for (int i = 0; i < list.size(); i++) {
            PoPoListModel poPoListModel = list.get(i);
            int position = poPoListModel.getPosition();
            if (position == -3) {  //表示点击点在多边形外,不执行任何操作
                return;
            } else if (position == -2) {  //选中整体图形
                moveView(pointModelsList, dx, dy);
            }/* else if (position == -1) {  //选中倒数第二个点和起点的那条直线
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
            if (pointListModel.getPosition() == -2) {
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
    public void adsorbResult(List<Point> intentPoints, List<Point> startPointList) {
        distanceList.clear();
        for (int i = 0; i < intentPoints.size(); i++) {
            Point pointI = intentPoints.get(i);
            for (int j = 0; j < startPointList.size(); j++) {
                Point pointJ = startPointList.get(j);
                float distance = DrawUtils.calTwoPointDistance(pointI, pointJ);
                if (distance < adsorbDis) {
                    TwoPointDistance twoPointDistance = new TwoPointDistance(pointI, pointJ, distance);
                    distanceList.add(twoPointDistance);
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


}
