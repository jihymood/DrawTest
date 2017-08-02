package com.example.hasee.drawtest.weidget.Two;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hasee.drawtest.model.MyComparator;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.PointListModel;
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;
import com.example.hasee.drawtest.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 * 第一次改
 */

public class MagicPlanView extends View {
    private Context context;
    private List<Point> points = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Path pathDash = new Path();
    private Paint circlePaint = new Paint();
    private int startX, startY;
    private int lastX, lastY;
    private int x, y;//上一个折点
    private float dx, dy;//偏移
    private float cx, cy;//圆心
    private boolean isFirst = true;
    private Point lastSecond;//倒数第二个点
    private int flag;//线段相交标识
    private long downTime;//按下时间
    private long upTime;//抬起时间
    boolean drawAble = true;//判断是否可画图
    private int downPosition;//落点位置判断  移动线和图
    private boolean isDrawCir = true;//是否画圆
    private Point duan1;
    private Point duan2;
    private Point p1;
    private Point p2;
    private Point ys1;
    private Point ys2;
    private LinearLayout llLineSet;
    private EditText etLineLong;
    private Button bntOkLong;
    private ImageView ivClear;
    private Canvas mCanvas;


//    private Path path;
//    private Paint paint;
//    private float startX, startY, lastX, lastY;
//    private int downPosition;
//    private Point duan1;//选中线的端点1
//    private Point duan2;//选中线的端点2
    private List<List<Point>> twofoldList;
    private List<TwoPointDistance> distanceList;
    private List<Point> intentPoints, startPointList;
    private List<PoPoListModel> pointModelsList, startModelList;
    private float adsorbDis;  //吸附距离
    private float toSidebDis;  //吸附距离
    private Point first, second;
    private float lStartX;//drawLine的开始点
    private float lStartY;
    private float lStopX;//drawLine的结束点
    private float lStopY;
    private int paintWidth = 10; //红色圆的半径


    public void setSetView(LinearLayout llLineSet, EditText etLineLong, Button bntOkLong, ImageView ivClear) {
        this.llLineSet = llLineSet;
        this.etLineLong = etLineLong;
        this.bntOkLong = bntOkLong;
        this.ivClear = ivClear;
    }

    public List<Point> getPoints() {
        return movePoints;
    }

    public MagicPlanView(Context context) {
        super(context);
        init(context);
    }

    public MagicPlanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MagicPlanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{8, 10, 8, 10}, 1);
        mPaint.setPathEffect(effects);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(5f);
        circlePaint.setStyle(Paint.Style.STROKE);

        // TODO: 2017/8/1
        twofoldList = new ArrayList<>();
        distanceList = new ArrayList<>();
        pointModelsList = new ArrayList<>();
        intentPoints = new ArrayList<>();
        startPointList = new ArrayList<>();
        startModelList = new ArrayList<>();
        adsorbDis = DensityUtil.px2dip(context, 100);
        toSidebDis = DensityUtil.px2dip(context, 20);
        Log.e("MyDrawView", "adsorbDis:" + adsorbDis+"/toSidebDis:" + toSidebDis);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        if (points.size() >= 2) {
            for (int i = 0; i < points.size() - 1; i++) {
                Point p = points.get(i);
                Point p1 = points.get(i + 1);
                canvas.drawLine(p.getX(), p.getY(), p1.getX(), p1.getY(), mPaint);
            }
            //虚线
            pathDash.reset();
            pathDash.moveTo(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY());
            pathDash.lineTo(points.get(0).getX(), points.get(0).getY());
            canvas.drawPath(pathDash, mPaint);
        }

        if (isDrawCir && points.size() > 0) {
            canvas.drawCircle(cx, cy, 80, circlePaint);
        }


        // TODO: 2017/8/1

        pathDash.reset();
        if (twofoldList != null && twofoldList.size() > 0) {
            mPaint.setColor(Color.GREEN);
            for (List<Point> points : twofoldList) {
                if (points != null && points.size() > 0) {
                    pathDash.moveTo(points.get(0).getX(), points.get(0).getY());
                    for (int i = 1; i < points.size(); i++) {
                        pathDash.lineTo(points.get(i).getX(), points.get(i).getY());
                    }
                }
                pathDash.close();
            }
        }
        canvas.drawPath(pathDash, mPaint);

        if (first != null && second != null) {
            mPaint.setColor(Color.RED);
//            canvas.drawCircle(first.getX(), first.getY(), paintWidth / 2, mPaint);
            canvas.drawCircle(second.getX(), second.getY(), paintWidth / 2, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                PointListModel pointListModel = PointListModel.getInstance();
                twofoldList = pointListModel.getList();

                downTime = System.currentTimeMillis();
                if (drawAble) {
                    actionDown(e);
                } else {
                    startX = (int) e.getX();
                    startY = (int) e.getY();

                    pointModelsList.clear();
                    for (List<Point> points : twofoldList) {
                        if (points != null && points.size() > 0) {
                            downPosition = ensurePoint1(points, startX, startY);
                            PoPoListModel poPoListModel = new PoPoListModel();
                            poPoListModel.setPosition(downPosition);
                            poPoListModel.setList(points);
                            pointModelsList.add(poPoListModel);
                            Log.e("MyDrawView", "downPosition:" + downPosition);
                        }
                    }

                    if (points.size() >= 3) {
//                        downPosition = ensurePoint(startX, startY);
//                        if (downPosition == -2) {
//                            //moveView(lastX - startX, lastY - startY);
//                        } else if (downPosition == -3) {
//                        } else if (downPosition == -1) {
//                            duan1 = movePoints.get(movePoints.size() - 1);
//                            duan2 = movePoints.get(0);
////                            setView();
//                            ys1 = duan1;
//                            ys2 = duan2;
//                            p1 = lastSecond;
//                            p2 = movePoints.get(1);
//                        } else if (downPosition == 0) {
//                            duan1 = movePoints.get(downPosition);
//                            duan2 = movePoints.get(downPosition + 1);
////                            setView();
//                            ys1 = duan1;
//                            ys2 = duan2;
//                            p1 = movePoints.get(movePoints.size() - 1);
//                            p2 = movePoints.get(2);
//                        } else if (downPosition == movePoints.size() - 2) {
//                            duan1 = movePoints.get(downPosition);
//                            duan2 = movePoints.get(downPosition + 1);
////                            setView();
//                            ys1 = duan1;
//                            ys2 = duan2;
//                            p1 = movePoints.get(downPosition - 1);
//                            p2 = movePoints.get(0);
//                        } else {
//                            duan1 = movePoints.get(downPosition);
//                            duan2 = movePoints.get(downPosition + 1);
////                            setView();
//                            ys1 = duan1;
//                            ys2 = duan2;
//                            p1 = movePoints.get(downPosition - 1);
//                            p2 = movePoints.get(downPosition + 2);
//                        }
                    } else {
                        drawAble = true;
                    }
                }



                break;
            case MotionEvent.ACTION_MOVE:
                if (drawAble) {
                    if (Utils.lineSpace((int) startX, (int) startY, (int) points.get(points.size() - 1).getX(), (int)
                            (points.get(points.size() - 1).getY())) < 80) {
                        actionMove(e);
                    }
                } else {//已经完成绘图时根据落点位置执行  移动线或view整体
//                    lastX = (int) e.getX();
//                    lastY = (int) e.getY();
//                    if (downPosition == -2) {
//                        moveView(lastX - startX, lastY - startY);
//                    } else if (downPosition == -3) {
//                    } else {
//                        moveLine();
//                    }
//                    startX = lastX;
//                    startY = lastY;

                    lastX = (int) e.getX();
                    lastY = (int) e.getY();
                    moveLine1(pointModelsList, lastX - startX, lastY - startY);
                    startX = lastX;
                    startY = lastY;

                }
                break;
            case MotionEvent.ACTION_UP:
                if (drawAble) {
                    upTime = System.currentTimeMillis();
                    if (upTime - downTime <= 500) {//为点击
                    } else {//为移动
                    }
                    actionUp(e);
                }

                adsorbResult(pointModelsList);

                break;
        }

        invalidate();
        return true;
    }

//    private void setView() {
//        llLineSet.setVisibility(VISIBLE);
//        ScaleAnimation animation = new ScaleAnimation(1,1,0,1);
//        animation.setFillAfter(true);
//        animation.setDuration(1000);
//        llLineSet.setAnimation(animation);
//        etLineLong.setText(Math.sqrt((duan1.getY() - duan2.getY()) * (duan1.getY() - duan2.getY()) +
//                (duan1.getX() - duan2.getX()) * (duan1.getX() - duan2.getX())) + "");
//        bntOkLong.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, etLineLong.getText().toString() + "", Toast.LENGTH_SHORT).show();
//                ScaleAnimation animation = new ScaleAnimation(1,1,1,0);
//                animation.setFillAfter(true);
//                animation.setDuration(1000);
//                llLineSet.setAnimation(animation);
//                llLineSet.setVisibility(GONE);
//            }
//        });
//        ivClear.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                etLineLong.setText("");
//            }
//        });
//    }



    private void actionDown(MotionEvent e) {
        if (isFirst) {
            isFirst = false;
            startX = (int) e.getX();
            startY = (int) e.getY();
            x = startX;
            y = startY;
            cx = startX;
            cy = startY;
            points.add(new Point(x, y));
        } else {
            startX = (int) e.getX();
            startY = (int) e.getY();
            double l = Utils.lineSpace((int) startX, (int) startY, (int) points.get(points.size() - 1).getX(), (int)
                    (points.get(points.size() - 1).getY()));
            if (l > 80) {
                if (points.size() > 2) {
                    flags.clear();
                    for (int i = 0; i < points.size() - 2; i++) {
                        flag = Utils.segIntersect(points.get(points.size() - 1), new Point(startX, startY), points
                                .get(i), points.get(i + 1));
                        flags.add(flag);
                    }
                    if (!flags.contains(1)) {
                        x = startX;
                        y = startY;
                        cx = startX;
                        cy = startY;
                        points.add(new Point(x, y));
                        lineLocation();
                    }
                } else {
                    x = startX;
                    y = startY;
                    cx = startX;
                    cy = startY;
                    points.add(new Point(x, y));
                    lineLocation();
                }
            }
        }
    }

    List<Integer> flags = new ArrayList<>();

    /**
     * 滑动
     *
     * @param e
     */
    private void actionMove(MotionEvent e) {
        lastX = (int) e.getX();
        lastY = (int) e.getY();
        x = lastX;
        y = lastY;
        if (points.size() > 3) {
            flags.clear();
            for (int i = 0; i < points.size() - 3; i++) {
                flag = Utils.segIntersect(lastSecond, new Point(x, y), points.get(i), points.get(i + 1));
                flags.add(flag);
            }
            if (!flags.contains(1)) {
                lineLocation();
                //更新点
                startX = lastX;
                startY = lastY;
                cx = lastX;
                cy = lastY;
            }
        } else if (points.size() == 1) {
            points.get(0).setX(x);
            points.get(0).setY(y);
            //更新点
            startX = lastX;
            startY = lastY;
            cx = lastX;
            cy = lastY;
        } else {
            lineLocation();
            //更新点
            startX = lastX;
            startY = lastY;
            cx = lastX;
            cy = lastY;
        }
    }

    /**
     * 抬起
     *
     * @param e
     */
    private void actionUp(MotionEvent e) {
        x = lastX;
        y = lastY;
    }

    //线段纠正
    public void lineLocation() {
        if (points.size() >= 2) {
            lastSecond = points.get(points.size() - 2);
            dx = Math.abs(lastSecond.getX() - x);
            dy = Math.abs(lastSecond.getY() - y);
            if (dy < 40) {
                points.get(points.size() - 1).setX(x);
                points.get(points.size() - 1).setY(lastSecond.getY());
                cx = x;
                cy = lastSecond.getY();
            } else if (dx < 40) {
                points.get(points.size() - 1).setX(lastSecond.getX());
                points.get(points.size() - 1).setY(y);
                cx = lastSecond.getX();
                cy = y;
            } else {
                points.get(points.size() - 1).setX(x);
                points.get(points.size() - 1).setY(y);
            }
        }

    }

    /**
     * 确定点击点
     *
     * @param startX 按下时的X
     * @param startY 按下时的Y
     * @return
     */
    private int ensurePoint(int startX, int startY) {
        //形成的多边形要首尾相接
        List<Point> pp = new ArrayList<>();
        pp.addAll(points);
        int position = -1;
        double minL = Utils.pointToLine(startX, startY, movePoints.get(movePoints.size() - 1), movePoints.get(0));
        for (int i = 0; i < movePoints.size() - 1; i++) {
            double l1 = Utils.pointToLine(startX, startY, movePoints.get(i), movePoints.get(i + 1));
            if (minL < l1) {
            } else if (minL > l1) {
                position = i;
                minL = l1;
            }
        }
        if (minL <= 40) {
            return position;//根据position获取要移动线的 端点
        } else if (Utils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > 40) {
            return -2;//表示点击点在多边形内,执行移动view
        } else {
            return -3;//表示点击点在多边形外,不执行任何操作
        }
    }


    /**
     * 确定点击点
     *
     * @param startX 按下时的X
     * @param startY 按下时的Y
     * @return
     */
    private int ensurePoint1(List<Point> movePoints, float startX, float startY) {
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
     * view的移动
     *
     * @param dx 滑动的x偏移量
     * @param dy 滑动的y偏移量
     */
    public void moveView(int dx, int dy) {
        for (Point p : movePoints) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    /**
     * 移动VIEW
     *
     * @param dx
     * @param dy
     */
    public void moveView1(List<PoPoListModel> pointListModels, float dx, float dy) {
        for (PoPoListModel pointListModel : pointListModels) {
            int position = pointListModel.getPosition();
            if (position == -2) {
                List<Point> list = pointListModel.getList();
                for (Point p : list) {
                    p.setX(p.getX() + dx);
                    p.setY(p.getY() + dy);
                }
                anewDraw(list, pathDash);
            } else {
                List<Point> list = pointListModel.getList();
//                aOldDraw(list, path);
            }
        }
//        isDrawLine = false;
//        twofoldList.clear();
//        twofoldList.add(movePoints);
    }

    private void moveLine() {
//        duan1.setX(duan1.getX() + lastX - startX);
//        duan1.setY(duan1.getX() + lastY - startY);
//        duan2.setY(duan2.getX() + lastY - startY);
//        duan2.setY(duan2.getX() + lastY - startY);
//        Point c1 = Utils.getCrossPoint(duan1, duan2, p1, ys1);
//        Point c2 = Utils.getCrossPoint(duan1, duan2, p1, ys2);
////        Point c2 = Utils.getCrossPoint(new Point(duan1.getX() + lastX - startX, duan1.getY() + lastY - startY),
////                new Point(duan2.getX() + lastX - startX, duan2.getY() + lastY - startY), p2, ys2);
//        if (downPosition == -1) {
//            movePoints.set(movePoints.size() - 1, c1);
//            movePoints.set(0, c2);
//        } else {
//            movePoints.set(downPosition, c1);
//            movePoints.set(downPosition + 1, c2);
//        }
//        points.clear();
//        points.addAll(movePoints);
//        points.add(movePoints.get(0));


//        float mk = Utils.calSlope(duan1, duan2);
//        float k1 = Utils.calSlope(p1, duan1);
//        float k2 = Utils.calSlope(p2, duan2);
//        float mx1 = Utils.calCrosspointX(mk, k1, new Point(lastX, lastY), p1);
//        float my1 = Utils.calBeelineEquation(k1, mx1, new Point(lastX, lastY));
//        float mx2 = Utils.calCrosspointX(mk, k2, new Point(lastX, lastY), p2);
//        float my2 = Utils.calBeelineEquation(k2, mx2, new Point(lastX, lastY));
//        duan1.setX((int) mx1);
//        duan1.setY((int) my1);
//        duan2.setX((int) mx2);
//        duan2.setY((int) my2);
//        if (downPosition == -1) {
//            movePoints.set(movePoints.size() - 1, new Point((int) mx1, (int) my1));
//            movePoints.set(0, new Point((int) mx2, (int) my2));
//        } else {
//            movePoints.set(downPosition, new Point((int) mx1, (int) my1));
//            movePoints.set(downPosition + 1, new Point((int) mx2, (int) my2));
//        }
//        points.clear();
//        points.addAll(movePoints);
//        points.add(movePoints.get(0));

    }

    public void moveLine1(List<PoPoListModel> list, float dx, float dy) {
        movePoints.clear();
        for (int i = 0; i < list.size(); i++) {
            PoPoListModel poPoListModel = list.get(i);

            boolean isInside = poPoListModel.isInside();


            int position = poPoListModel.getPosition();
            if (position == -3) {  //表示点击点在多边形外,不执行任何操作
//                return;
            } else if (position == -2) {  //选中整体图形
                moveView1(pointModelsList, dx, dy);
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

    List<Point> movePoints = new ArrayList<>();
    List<Point> linePoints = new ArrayList<>();

    public void closeView() {
        //判断虚线是否有相交线段,相交则无法绘制
        flags.clear();
        for (int i = 0; i < points.size() - 1; i++) {
            flag = Utils.segIntersect(points.get(0), points.get(points.size() - 1), points.get(i), points.get(i + 1));
            flags.add(flag);
        }
        if (!flags.contains(1)) {
            //起点,最后一个点和倒数第二个点在一条直线上,或者第一个点到最后一条线的距离小于10则去掉最后一个点,完成绘制
            if (points != null && points.size() > 0) {
                if (Utils.segIntersect(points.get(0), points.get(points.size() - 1), points.get(points.size() - 1),
                        lastSecond) == 0
                        || Utils.pointToLine(points.get(0).getX(), points.get(0).getY(), points.get(points.size() -
                        1), lastSecond) < 20) {
                    pathDash.reset();
                    points.remove(points.get(points.size() - 1));
                    movePoints.addAll(points);
                    linePoints.addAll(points);
                    points.add(points.get(0));
                } else {
                    pathDash.reset();
                    movePoints.addAll(points);
                    linePoints.addAll(points);
                    points.add(points.get(0));
                }
            }
            invalidate();
            drawAble = false;
            isDrawCir = false;
        } else {
            Toast.makeText(context, "无法完成绘制请调整抬手点", Toast.LENGTH_SHORT).show();
        }
    }

    public void setDrawAgain() {
        drawAble = true;
        isFirst = true;
        isDrawCir = true;
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
                    anewDraw(intentPoints, pathDash);
                    aOldDraw(startPointList, pathDash);
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

}
