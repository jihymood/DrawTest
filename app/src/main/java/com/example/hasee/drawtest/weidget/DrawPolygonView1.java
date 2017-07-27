package com.example.hasee.drawtest.weidget;

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
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class DrawPolygonView1 extends View {
    private static int HORIZONTAL = 0; //水平方向
    private static int VERTICAL = 1;   //垂直方向
    private static int ISNOTHORORVER = 7; //不是水平也不是垂直
    int orientation = ISNOTHORORVER;  //0表示水平方向,1表示垂直方向
    private Context context;
    private List<Point> points = new ArrayList<>();//添加点的集合
    private List<Point> intentPoints = new ArrayList<>();//传输点的集合
    private Paint mPaint = new Paint();//画笔
    private Path mPath = new Path();//路径
    float x;//上一个拐点x
    float y;//上一个拐点y
    //当前点
    float newX;
    float newY;
    private float startX;//开始点
    private float startY;//开始点
    private float lastX;//结束点
    private float lastY;//结束点
    //起点
    private float firstX;
    private float firstY;
    private List<Integer> flag;//判断手指移动方向的集合
    private boolean drawAble = true;//判断是否可画图
    boolean isRemoveStartPoint;//是否移除开始点
    private int downPosition;//确定按下的点是执行移动线还是移动view
    private Point duan1;//选中线的端点1
    private Point duan2;//选中线的端点2
    private float lStartX;//drawLine的开始点
    private float lStartY;
    private float lStopX;//drawLine的结束点
    private float lStopY;


    private List<Point> startPointList;  //原始坐标集合
    //    private Path testPath;
    private List<TwoPointDistance> distanceList = new ArrayList<>();
    float adsorbDis;  //吸附距离
    private Point first,second;
    private int paintWidth=10;


    public List<Point> getPoints() {
        return intentPoints;
    }

    public DrawPolygonView1(Context context) {
        super(context);
        init(context);
    }

    public DrawPolygonView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawPolygonView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        this.context = context;
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(paintWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        //初始化时设置存放标识方向的list
        flag = new ArrayList<>();
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);


        initData();
        adsorbDis = DensityUtil.px2dip(context, 100);
        Log.e("DrawPolygonView1", "adsorbDis:" + adsorbDis);
    }

    public void initData() {
        startPointList = new ArrayList<>();
        startPointList.add(new Point(50, 50));
        startPointList.add(new Point(100, 50));
        startPointList.add(new Point(100, 100));
        startPointList.add(new Point(200, 100));
        startPointList.add(new Point(200, 300));
        startPointList.add(new Point(50, 300));
    }

    @Override
    protected void onDraw(Canvas canvas) {  // TODO: 2017/7/26
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(mPath, mPaint);
        if (isDrawLine) {
            mPaint.setColor(Color.RED);
            canvas.drawLine(lStartX, lStartY, lStopX, lStopY, mPaint);
        }

        canvas.drawPath(mPath, mPaint);
        if (first != null && second != null) {
            mPaint.setColor(Color.RED);
            canvas.drawCircle(first.getX(), first.getY(), paintWidth / 2, mPaint);
            canvas.drawCircle(second.getX(), second.getY(), paintWidth / 2, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (drawAble) {
                    actionDown(e);
                } else {//已经完成绘图时判断落点位置
                    startX = e.getX();
                    startY = e.getY();
                    if (intentPoints.size() >= 2) {
                        downPosition = ensurePoint(startX, startY);
                    } else {
                        drawAble = true;
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (drawAble) {
                    actionMove(e);
                } else {//已经完成绘图时根据落点位置执行  移动线或view整体
                    lastX = e.getX();
                    lastY = e.getY();
                    moveLine(lastX - startX, lastY - startY);
                    startX = lastX;
                    startY = lastY;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (drawAble) {
                    actionUp(e);
                }
                drawAble = false;//抬手后不可编辑

                // TODO: 2017/7/26
                adsorbResult(intentPoints, startPointList);

                break;
        }
        // 更新绘制
        invalidate();
        return true;
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

            anewDraw(intentPoints, mPath);
            aOldDraw(startPointList, mPath);
            invalidate();
        }
    }


    /**
     * 确定点击点
     *
     * @param startX 按下时的X
     * @param startY 按下时的Y
     * @return
     */
    private int ensurePoint(float startX, float startY) {
        //形成的多边形要首尾相接
        List<Point> pp = new ArrayList<>();
        pp.addAll(intentPoints);
        pp.add(intentPoints.get(0));
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
     * 手指按下
     *
     * @param e
     */
    private void actionDown(MotionEvent e) {
        float mx = e.getX();
        float my = e.getY();
        mPath.reset();
        startX = mx;
        startY = my;
        mPath.moveTo(mx, my);
        x = mx;
        y = my;
        firstX = mx;
        firstY = my;
        points.add(new Point(startX, startY));
    }

    /**
     * 手指滑动
     *
     * @param e
     */

    int horizontalCount = 1;
    int verticalCount = 1;

    private void actionMove(MotionEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        //当前点与上一个拐点比较
        float dx = lastX - x;
        float dy = lastY - y;
        //两点之间的距离大于等于2时判读方向
        if (Math.abs(lastX - startX) > 2 || Math.abs(lastY - startY) > 2) {
            //判断水平方向
            if (Math.abs(lastX - startX) > Math.abs(lastY - startY)) {
                //上个方向是垂直且flag集合中都是垂直记录则方向发生变化
                if (orientation == VERTICAL && !flag.contains(HORIZONTAL)) {
                    //保存拐点
                    points.add(new Point(newX, newY));
                    //赋值成上一个拐点
                    x = newX;
                    y = newY;
                    //path重新绘制,既纠正前面的拐点
                    mPath.reset();
                    mPath.moveTo(firstX, firstY);
                    for (Point point : points) {
                        mPath.lineTo(point.getX(), point.getY());
                        invalidate();
                    }
                }
                //水平方向
                orientation = HORIZONTAL;
                //flag方向标识的添加和移除更新flag
                flag.add(0, orientation);
                Log.e("DrawPolygonView2", "水平方向:" + horizontalCount);
                horizontalCount++;
                flag.remove(flag.size() - 1);
                //当距离变化超过20时更新可能成为当前拐点的x,y
                if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                    newY = y;
                    newX = lastX;
                    mPath.lineTo(newX, newY);
                }
            } else if (Math.abs(lastX - startX) < Math.abs(lastY - startY)) {//垂直方向
                if (orientation == HORIZONTAL && !flag.contains(VERTICAL)) {
                    points.add(new Point(newX, newY));
                    //赋值上一个拐点
                    x = newX;
                    y = newY;
                    //path重新绘制,既纠正前面的拐点
                    mPath.reset();
                    mPath.moveTo(firstX, firstY);
                    for (Point point : points) {
                        mPath.lineTo(point.getX(), point.getY());
                        invalidate();
                    }
                }
                orientation = VERTICAL;
                flag.add(0, orientation);
                Log.e("DrawPolygonView2", "垂直方向:" + verticalCount);
                verticalCount++;
                flag.remove(flag.size() - 1);
                //当距离变化超过20时更新可能成为当前拐点的x,y
                if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                    newX = x;
                    newY = lastY;
                    mPath.lineTo(newX, newY);
                }
            }
        } else {

        }
        //更新一点的点
        startX = lastX;
        startY = lastY;
    }

    /**
     * 手指抬起
     *
     * @param e
     */
    private void actionUp(MotionEvent e) {
        if (points.size() >= 2) {
            closePolygon();
            intentPoints.clear();
            intentPoints.addAll(points);
            points.clear();
            //判断去除几个起点
            if (isRemoveStartPoint) {
                intentPoints.remove(0);
                intentPoints.remove(intentPoints.size() - 1);
            } else {
                intentPoints.remove(0);
            }
            anewDraw(intentPoints, mPath);
            aOldDraw(startPointList, mPath);
            movePoints.clear();
            movePoints.addAll(intentPoints);
        } else {
//            intentPoints.add(new Point(firstX, firstY));
//            if (!flag.contains(1)) {
//                intentPoints.add(new Point(lastX, y));
//            } else {
//                intentPoints.add(new Point(x, lastY));
//            }

        }
    }


    /**
     * 闭合多边形
     */
    public void closePolygon() {
        //多边形点的集合
        List<Point> polygonPoints = new ArrayList<>();
        polygonPoints.addAll(points);
        Point second = points.get(1);
//        if (!flag.contains(VERTICAL)) {//水平 // TODO: 2017/7/25
        if (!flag.subList(1, 5).contains(VERTICAL)) {//水平
            //形成多边形集合
            polygonPoints.add(new Point(lastX, y));   //抬手点
            polygonPoints.add(new Point(firstX, firstY));  //起点
            //起点和抬手点可以获得2个点
            Point p1 = new Point(firstX, y);
            Point p2 = new Point(lastX, firstY);
            Log.e("DrawPolygonView2", "水平方向" + "firstX:" + firstX + "/" + "y:" + y +
                    "\nlastX:" + lastX + "firstY:" + firstY);
            if (DrawUtils.PtInRegion(p2, polygonPoints) == -1) {//p2在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();//点p2和点(x,y)的线段
                t2.add(p2);
                t2.add(new Point(x, y));
                List<Point> z2 = new ArrayList<>();//p2和抬手点的线段
                z2.add(p2);
                z2.add(new Point(lastX, y));  //抬手点
                if (DrawUtils.PtInRegion(new Point(lastX, y), t2) != 0
                        && DrawUtils.PtInRegion(new Point(x, y), z2) != 0) {
                    points.add(new Point(lastX, y));
                }
                points.add(p2);
                points.add(new Point(firstX, firstY));

                //起点添加判断
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(second);
                List<Point> q2 = new ArrayList<>();
                q2.add(p2);
                q2.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0
                        || DrawUtils.PtInRegion(second, q2) == 0) {
                    isRemoveStartPoint = true;
                }
            } else {
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(x, y));
                List<Point> z1 = new ArrayList<>();//p1和抬手点的线段
                z1.add(p1);
                z1.add(new Point(lastX, y));
                if (DrawUtils.PtInRegion(new Point(lastX, y), t1) != 0
                        && DrawUtils.PtInRegion(new Point(x, y), z1) != 0) {
                    points.add(new Point(lastX, y));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                //起点添加判断
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(second);
                List<Point> q1 = new ArrayList<>();
                q1.add(p1);
                q1.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0
                        || DrawUtils.PtInRegion(second, q1) == 0) {
                    isRemoveStartPoint = true;
                }
            }
//        } else if (!flag.contains(HORIZONTAL)) {//垂直
        } else if (!flag.subList(1, 5).contains(HORIZONTAL)) {//垂直
            //形成多边形集合
            polygonPoints.add(new Point(x, lastY));
            polygonPoints.add(new Point(firstX, firstY));
            //获得2个点
            Point p1 = new Point(firstX, lastY);
            Point p2 = new Point(x, firstY);
            Log.e("DrawPolygonView2", "垂直方向" + "firstX:" + firstX + "/" + "lastY:" + lastY +
                    "\nx:" + x + "firstY:" + firstY);
            if (DrawUtils.PtInRegion(p2, polygonPoints) == -1) {//p2在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();
                t2.add(p2);
                t2.add(new Point(x, y));
                List<Point> z2 = new ArrayList<>();//p2和抬手点的线段
                z2.add(p2);
                z2.add(new Point(x, lastY));
                if (DrawUtils.PtInRegion(new Point(x, lastY), t2) != 0
                        && DrawUtils.PtInRegion(new Point(x, y), z2) != 0) {
                    points.add(new Point(x, lastY));
                }
                points.add(p2);
                points.add(new Point(firstX, firstY));
                //起点添加判断
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(second);
                List<Point> q2 = new ArrayList<>();
                q2.add(p2);
                q2.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0
                        || DrawUtils.PtInRegion(second, q2) == 0) {
                    isRemoveStartPoint = true;
                }
            } else {
                //判断是否添加抬手点
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(x, y));
                List<Point> z1 = new ArrayList<>();//p1和抬手点的线段
                z1.add(p1);
                z1.add(new Point(x, lastY));
                if (DrawUtils.PtInRegion(new Point(x, lastY), t1) != 0
                        && DrawUtils.PtInRegion(new Point(x, y), z1) != 0) {
                    points.add(new Point(x, lastY));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                //起点添加判断
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(second);
                List<Point> q1 = new ArrayList<>();
                q1.add(p1);
                q1.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0
                        || DrawUtils.PtInRegion(second, q1) == 0) {
                    isRemoveStartPoint = true;
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
     * 重绘选中直线后图形
     *
     * @param points
     */
    List<Point> linePoints = new ArrayList<>();
    boolean isDrawLine;//是否划线

    public void drawLine(List<Point> points) {
        isDrawLine = true;
        mPath.reset();
        mPath.moveTo(points.get(downPosition + 1).getX(), points.get(downPosition + 1).getY());
        linePoints.clear();
        for (int i = downPosition + 1; i < points.size(); i++) {  //画选中那条线（红线）的下半部分
            linePoints.add(points.get(i));
        }
        for (int i = 0; i < downPosition + 1; i++) { //画选中那条线（红线）的上半部分
            linePoints.add(points.get(i));
        }
        for (Point point : linePoints) {  //画红线
            mPath.lineTo(point.getX(), point.getY());
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
    public void moveView(float dx, float dy) {
        for (Point p : movePoints) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
        isDrawLine = false;
        anewDraw(movePoints, mPath);
        aOldDraw(startPointList, mPath);
        intentPoints.clear();
        intentPoints.addAll(movePoints);
    }

    /**
     * 移动线
     *
     * @param startX
     * @param startY
     * @param dx
     * @param dy
     */
    List<Point> movePoints = new ArrayList<>();//用于存放变化的点

    public void moveLine(float dx, float dy) {
        movePoints.clear();
        movePoints.addAll(intentPoints);
        int position = downPosition;
        //通过ensurePoint获得position,判断position
        if (position == -1) {   //选中倒数第二个点和起点的那条直线
            duan1 = movePoints.get(movePoints.size() - 1);
            duan2 = movePoints.get(0);
        } else if (position == -2) {  //选中整体图形
            moveView(dx, dy);
            return;
        } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
            return;
        } else {  //表示选中其他的边
            duan1 = movePoints.get(position);
            duan2 = movePoints.get(position + 1);
        }

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
        if (position == -1) {
            movePoints.set(movePoints.size() - 1, duan1);
            movePoints.set(0, duan2);
        } else {
            movePoints.set(position, duan1);
            movePoints.set(position + 1, duan2);
        }
        drawLine(movePoints);
        intentPoints.clear();
        intentPoints.addAll(movePoints);
    }

    /**
     * 清除画板
     */
    public void cleanDraw() {
        mPath.reset();
        movePoints.clear();
        intentPoints.clear();
        points.clear();
        orientation = ISNOTHORORVER;
        drawAble = true;
        isDrawLine = false;
        isRemoveStartPoint = false;
        flag.clear();
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        flag.add(orientation);
        invalidate();
    }
}
