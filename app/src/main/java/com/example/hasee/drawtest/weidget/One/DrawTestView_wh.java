package com.example.hasee.drawtest.weidget.One;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/7/18 12:27
 *
 * 黄继海+王宝帅 部分功能
 */

public class DrawTestView_wh extends View {

    private List<Point> startPointList = new ArrayList<>();  //原始坐标集合
    private List<Point> curPtList;  //移动后坐标集合

    private Paint paint;  //画笔
    private Paint extendPaint;//延长线的画笔
    private Canvas cacheCanvas;  //画布
    private Bitmap cachebBitmap;
    private Path path, exPath;
    private Context context;
    private Point downPoint;
    private List<Point> linePointList;//存储离触点最近的直线的两个端点
    int index;//标记需要改变坐标的两个点的下标。
    float dx = 0.0f, dy = 0.0f;//move的距离

    float judgeDis = 10;
    float culDis;//计算得出的点与一条直线的最短距离
    float culDis1;


    public DrawTestView_wh(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawTestView_wh(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DrawTestView_wh(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void initData() {
        startPointList = new ArrayList<>();
        startPointList.add(new Point(50, 50));
        startPointList.add(new Point(100, 50));
        startPointList.add(new Point(100, 100));
        startPointList.add(new Point(200, 100));
        startPointList.add(new Point(200, 300));
        startPointList.add(new Point(50, 300));

        curPtList = new ArrayList<>();

        for (Point point : startPointList) {
            curPtList.add(new Point(point.getX(), point.getY()));
        }
    }


    public void init() {
        initData();

        path = new Path();
        exPath = new Path();

        //多边形画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        //        延长线画笔
        extendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        extendPaint.setStyle(Paint.Style.STROKE);
        extendPaint.setColor(Color.GREEN);
        extendPaint.setStrokeWidth(5);
        paint.setFilterBitmap(true);

        /*设置虚线*/
        DashPathEffect effect = new DashPathEffect(new float[]{10f, 5f}, 0);
        extendPaint.setPathEffect(effect);

        int[] screenSize = DrawUtils.getScreenSize(context);
        int i = screenSize[0];
        Log.e("DrawTestView", "横屏宽度:" + i);

        cachebBitmap = Bitmap.createBitmap(screenSize[0], 800, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(Color.CYAN);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        exPath.reset();
        path.moveTo(curPtList.get(0).getX(), curPtList.get(0).getY());
        for (int i = 1; i < curPtList.size(); i++) {

            Point point = curPtList.get(i);
            path.lineTo(point.getX(), point.getY());
        }
        path.close();

        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
        canvas.drawPath(exPath, extendPaint);

    }

    private float cur_x, cur_y;
    private boolean isMoving;

    private List<Float> culDisList = new ArrayList<>();
    private boolean isMine;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                cur_x = x;
                cur_y = y;
                Log.e("DrawTestView", "DOWN" + cur_x + "/" + cur_y);

                isMoving = DrawUtils.isInside(path, event);
                Log.e("DrawTestView", "--判断点是否则范围内----" + isMoving);

                downPoint = new Point((int) cur_x, (int) cur_y);
                linePointList = new ArrayList<>();
                culDisList.clear();
                for (int i = 0; i < curPtList.size(); i++) {
                    if (i != (curPtList.size() - 1)) {
                        culDis = calDistanceByHelen(downPoint, curPtList.get(i), curPtList.get(i + 1));
                    } else {
                        culDis = calDistanceByHelen(downPoint, curPtList.get((curPtList.size()) - 1), curPtList.get(0));
                    }

                    culDisList.add(culDis);

                    if (culDis < judgeDis && culDis != 0) {
                        culDis1 = culDis;
                        if (i == (curPtList.size() - 1)) {
                            linePointList.add(curPtList.get(0));
                            linePointList.add(curPtList.get(i));
                            Log.d("ss", "linePointList中添加的坐标是：index= " + 1 + "------->" + "(" + linePointList.get(1)
                                    .getX() + "," + linePointList.get(1).getY() + ")" + "index=" + 0 + "------->" + "" +
                                    "(" +
                                    linePointList.get(0).getX() + "," + linePointList.get(0).getY() + ")");
                            drawExtendLine(curPtList.get(0), curPtList.get(curPtList.size() - 1));
                            index = -1;//获取需要改变的点的下标
                            Log.d("ss", "index=  " + index);
                        } else {
                            linePointList.add(curPtList.get(i));
                            linePointList.add(curPtList.get(i + 1));
                            Log.d("ss", "linePointList中添加的坐标是：index= " + 0 + "------->" + "(" + linePointList.get(0)
                                    .getX() + "," + linePointList.get(0).getY() + ")" + "index=" + 1 + "------->" + "" +
                                    "(" +
                                    linePointList.get(1).getX() + "," + linePointList.get(1).getY() + ")");
                            index = i;//获取需要改变的点的下标
                            Log.d("ss", "index=  " + index);
                                /*再次做判断0和1*/
                            drawExtendLine(curPtList.get(i), curPtList.get(i + 1));
                        }
                    }
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.e("DrawTestView", "MOVE");
                int count = 0;
                for (Float aFloat : culDisList) {
                    if (aFloat > 10) {
                        count++;
                    }
                }
                if (isMoving && count == culDisList.size()) {
                    float dx = x - cur_x;
                    float dy = y - cur_y;
                    Log.e("DrawTestView", "UP" + dx + "/" + dy);
                    for (int i = 0; i < curPtList.size(); i++) {
                        curPtList.get(i).setX(startPointList.get(i).getX() + dx);
                        curPtList.get(i).setY(startPointList.get(i).getY() + dy);
                    }


                } else {
                    Point referPoint1 = null, referPoint2 = null;
                    float referPoint1X = 0, referPoint1Y = 0, referPoint2X = 0, referPoint2Y = 0;
                    if (linePointList.size() > 0) {
                        referPoint1 = linePointList.get(0);
                        referPoint2 = linePointList.get(1);
                        Log.d("ss", "从linePointList取出的点的坐标是：" + "(" + linePointList.get(0).getX() + "," + linePointList
                                .get(0)
                                .getY() + ")" + "和  （" + linePointList.get(1).getX() + "," + linePointList.get(1)
                                .getY() +
                                ")");
                             /*两个旧端点坐标  即起点坐标*/
                        referPoint1X = referPoint1.getX();
                        referPoint1Y = referPoint1.getY();
                        referPoint2X = referPoint2.getX();
                        referPoint2Y = referPoint2.getY();
                    }
                    float dx, dy;
                    dx = Math.abs(event.getX() - cur_x);
                    dy = Math.abs(event.getY() - cur_y);
               /*    *//**//*两个新端点的坐标*//**//**/
                    float newReferPoint1X = referPoint1X, newReferPoint2X = referPoint2X, newReferPoint1Y =
                            referPoint1Y,
                            newReferPoint2Y = referPoint2Y;
                    if (dx > dy) {
                 /* 水平移动  y坐标不变 x坐标变化curX*/
                /*判断扩大还是缩小*/
                /*判断curX的范围，要保证curX在两个y坐标点之间*/
                        if (Math.min(referPoint1Y, referPoint2Y) < y && y < Math.max(referPoint1Y, referPoint2Y)) {
                            if (x > referPoint1X) {
                                newReferPoint1X = referPoint1X + dx;
                                newReferPoint2X = referPoint2X + dx;
                                if (index == -1) {
                                    curPtList.get(0).setX(curPtList.get(0).getX() + dx);
                                    curPtList.get(curPtList.size() - 1).setX(curPtList.get(curPtList.size() - 1).getX()
                                            + dx);

                                    startPointList.get(0).setX(curPtList.get(0).getX());
                                    startPointList.get(curPtList.size() - 1).setX(curPtList.get(curPtList.size() - 1)
                                            .getX());

                                } else {
                                    curPtList.get(index).setX(curPtList.get(index).getX() + dx);
                                    curPtList.get(index + 1).setX(curPtList.get(index + 1).getX() + dx);

                                    startPointList.get(index).setX(curPtList.get(index).getX());
                                    startPointList.get(index + 1).setX(curPtList.get(index + 1).getX());
                                }
                            } else {
                                newReferPoint1X = referPoint1X - dx;
                                newReferPoint2X = referPoint2X - dx;
                                if (index == -1) {
                                    curPtList.get(0).setX(curPtList.get(0).getX() - dx);
                                    curPtList.get(curPtList.size() - 1).setX(curPtList.get(curPtList.size() - 1).getX()
                                            - dx);

                                    startPointList.get(0).setX(curPtList.get(0).getX());
                                    startPointList.get(curPtList.size() - 1).setX(curPtList.get(curPtList.size() - 1)
                                            .getX()
                                    );
                                } else {
                                    curPtList.get(index).setX(curPtList.get(index).getX() - dx);
                                    curPtList.get(index + 1).setX(curPtList.get(index + 1).getX() - dx);

                                    startPointList.get(index).setX(curPtList.get(index).getX());
                                    startPointList.get(index + 1).setX(curPtList.get(index + 1).getX());
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
                        if (Math.min(referPoint1X, referPoint2X) < x && x < Math.max(referPoint1X, referPoint2X)) {
                            if (y > referPoint1Y) {
                                newReferPoint1Y = referPoint1Y + dy;
                                newReferPoint2Y = referPoint2Y + dy;
                                if (index == -1) {
                                    curPtList.get(0).setY(curPtList.get(0).getY() + dy);
                                    curPtList.get(curPtList.size() - 1).setY(curPtList.get(curPtList.size() - 1).getY()
                                            + dy);

                                    startPointList.get(0).setY(curPtList.get(0).getY());
                                    startPointList.get(curPtList.size() - 1).setY(curPtList.get(curPtList.size() - 1)
                                            .getY()
                                    );
                                } else {
                                    curPtList.get(index).setY(curPtList.get(index).getY() + dy);
                                    curPtList.get(index + 1).setY(curPtList.get(index + 1).getY() + dy);

                                    startPointList.get(index).setY(curPtList.get(index).getY());
                                    startPointList.get(index + 1).setY(curPtList.get(index + 1).getY());
                                }
                            } else {
                                newReferPoint1Y = referPoint1Y - dy;
                                newReferPoint2Y = referPoint2Y - dy;
                                if (index == -1) {
                                    curPtList.get(0).setY(curPtList.get(0).getY() - dy);
                                    curPtList.get(curPtList.size() - 1).setY(curPtList.get(curPtList.size() - 1).getY()
                                            - dy);

                                    startPointList.get(0).setY(curPtList.get(0).getY());
                                    startPointList.get(curPtList.size() - 1).setY(curPtList.get(curPtList.size() - 1)
                                            .getY()
                                    );
                                } else {
                                    curPtList.get(index).setY(curPtList.get(index).getY() - dy);
                                    curPtList.get(index + 1).setY(curPtList.get(index + 1).getY() - dy);

                                    startPointList.get(index).setY(curPtList.get(index).getY());
                                    startPointList.get(index + 1).setY(curPtList.get(index + 1).getY());
                                }
                            }
                        }

                        exPath.moveTo(referPoint1X, referPoint1Y);
                        exPath.lineTo(referPoint1X, newReferPoint1Y);
                        exPath.moveTo(referPoint2X, referPoint2Y);
                        exPath.lineTo(referPoint2X, newReferPoint2Y);
                    }

                }

                break;
            }

            case MotionEvent.ACTION_UP: {

                int count = 0;
                for (Float aFloat : culDisList) {
                    if (aFloat > 10) {
                        count++;
                    }
                }
                if (isMoving && count == culDisList.size()) {
                    float dx = x - cur_x;
                    float dy = y - cur_y;
                    Log.e("DrawTestView", "UP" + dx + "/" + dy);

                    List<Point> newPointList = new ArrayList<>();
                    for (Point point : startPointList) {
                        float newX = point.getX() + dx;
                        float newY = point.getY() + dy;
                        Point newPoint = new Point(newX, newY);
                        newPointList.add(newPoint);
                    }
                    startPointList.clear();
                    startPointList.addAll(newPointList);
                }
                break;
            }
        }

        // 通知刷新界面
        invalidate();
        return true;
    }

    /**
     * 海伦公式求点到直线的距离  原理：求出三点围成的三角形的面积除以底边（直线的长度）
     * 得到三角形的高（bug是理论上可能存在点在直线上 的情况）
     */
    public float calDistanceByHelen(Point point, Point first, Point second) {
        float firstX = first.getX();
        float firstY = first.getY();

        float secondX = second.getX();
        float secondY = second.getY();

        float pointX = point.getX();
        float pointY = point.getY();
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

        exPath.moveTo(point1.getX(), point1.getY());
        exPath.lineTo(point2.getX(), point2.getY());
    }


}
