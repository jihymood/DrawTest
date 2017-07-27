package com.example.hasee.drawtest.weidget;

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
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.attr.x;
import static android.R.attr.y;
import static com.example.hasee.drawtest.utils.DrawUtils.PtInRegion;

/**
 * Created by HASEE on 2017/7/18 12:27
 * <p>
 * 三人合并功能
 */

public class DrawTestView_san extends View {

    private List<Point> startPointList;  //原始坐标集合
    private List<Point> curPtList = new ArrayList<>();  //移动后坐标集合

    private Paint paint;  //画笔
    private Paint extendPaint;//延长线的画笔
    private Canvas cacheCanvas;  //画布
    private Bitmap cachebBitmap;
    private Path path, exPath,testPath;
    private Context context;
    private Point downPoint;
    private List<Point> linePointList;//存储离触点最近的直线的两个端点
    int index;//标记需要改变坐标的两个点的下标。
    float dx = 0.0f, dy = 0.0f;//move的距离

    private List<Point> pointss = new ArrayList<>();
    private List<Point> points = new ArrayList<>();
    private List<Point> intentPoints = new ArrayList<>();
    private List<TwoPointDistance> distanceList =new ArrayList<>();
    int inflexionX;//上一个拐点x
    int inflexionY;//上一个拐点y
    //当前点
    int newX;
    int newY;
    private int startX;//开始点
    private int startY;//开始点
    private int lastX;//结束点
    private int lastY;//结束点
    int orientation = 7;//0表示水平方向,1表示垂直方向
    //起点
    private int firstX;
    private int firstY;
    List<Integer> flag;
    float judgeDis = 20;  //按下的触摸点与此距离进行比较
    float adsorbDis;  //吸附距离

    boolean isFirstX = true;  //记录移动x方向按下时候的点坐标
    boolean isFirstX1 = true;
    boolean isFirstY = true;
    boolean isFirstY1 = true;
    private boolean isComplete = false; //专注画图部分功能
    boolean isRemoveStartPiont;
    float x1, x2 = 0;  //按下时候记录下来的点坐标，全局变量。赋值后就不是0
    float y1, y2 = 0;  //按下时候记录下来的点坐标，全局变量。赋值后就不是0


    public DrawTestView_san(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawTestView_san(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DrawTestView_san(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
//        curPtList = new ArrayList<>();
//        for (Point point : startPointList) {
//            curPtList.add(new Point(point.getX(), point.getY()));
//        }
    }


    public void init() {
        initData();
        path = new Path();
        exPath = new Path();
        testPath = new Path();

        //多边形画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        // 延长线画笔
        extendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        extendPaint.setStyle(Paint.Style.STROKE);
        extendPaint.setColor(Color.GREEN);
        extendPaint.setStrokeWidth(10);
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

        flag = new ArrayList<>();
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);

        adsorbDis = DensityUtil.px2dip(context, 100);
        Log.e("DrawTestView_san", "adsorbDis:" + adsorbDis);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        testPath.moveTo(startPointList.get(0).getX(), startPointList.get(0).getY());
        for (int i = 1; i < startPointList.size(); i++) {
            Point point = startPointList.get(i);
            testPath.lineTo(point.getX(), point.getY());
        }
        testPath.close();

        /** 画图 */
        if (curPtList != null && curPtList.size() > 0) {
            exPath.reset();
            path.reset();

            path.moveTo(curPtList.get(0).getX(), curPtList.get(0).getY());
            for (int i = 1; i < curPtList.size(); i++) {
                Point point = curPtList.get(i);
                path.lineTo(point.getX(), point.getY());
            }
            path.lineTo(curPtList.get(0).getX(), curPtList.get(0).getY());
        }

        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
        canvas.drawPath(exPath, extendPaint);
        canvas.drawPath(testPath, paint);


    }

    private float cur_x, cur_y;
    private boolean isMoving;
    private List<Float> culDisList = new ArrayList<>(); //触摸点与各条直线长度集合，当所有直线都大于10时，作为移动整体的条件

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (intentPoints.size() > 0) {
            isComplete = true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                cur_x = x;
                cur_y = y;
                Log.e("DrawTestView", "DOWN" + cur_x + "/" + cur_y);

                if (!isComplete) { //刚开始画
                    int mx = (int) event.getX();
                    int my = (int) event.getY();
                    startX = mx;
                    startY = my;
                    path.moveTo(mx, my);
                    inflexionX = mx;
                    inflexionY = my;
                    firstX = mx;
                    firstY = my;
                    points.add(new Point(startX, startY));
                }


                isMoving = DrawUtils.isInside(path, event);
                Log.e("DrawTestView", "--判断点是否则范围内----" + isMoving);

                if (curPtList != null && curPtList.size() > 0) {
                    culDisList.clear();
                    downPoint = new Point((int) cur_x, (int) cur_y);
                    float culDis;//计算得出的点与一条直线的最短距离
                    linePointList = new ArrayList<>();
                    for (int i = 0; i < curPtList.size(); i++) {
                        if (i != (curPtList.size() - 1)) {
                            culDis = calDistanceByHelen(downPoint, curPtList.get(i), curPtList.get(i + 1));
                        } else {
                            culDis = calDistanceByHelen(downPoint, curPtList.get((curPtList.size()) - 1), curPtList
                                    .get(0));
                        }
                        culDisList.add(culDis);

                        if (culDis < judgeDis && culDis != 0) {
                            if (i == (curPtList.size() - 1)) {
                                linePointList.add(curPtList.get(0));
                                linePointList.add(curPtList.get(i));
//                                Log.e("DrawTestView_san", "linePointList中添加的坐标是：index= " + 1 + "------->" + "(" +
//                                        linePointList.get(1)
//                                                .getX() + "," + linePointList.get(1).getY() + ")" + "index=" + 0 +
//                                        "------->"
//                                        + "" +
//                                        "(" +
//                                        linePointList.get(0).getX() + "," + linePointList.get(0).getY() + ")");
                                drawExtendLine(curPtList.get(0), curPtList.get(curPtList.size() - 1));
                                index = -1;//获取需要改变的点的下标
//                                Log.e("DrawTestView_san", "index_DOWN_first=  " + index);
                            } else {
                                linePointList.add(curPtList.get(i));
                                linePointList.add(curPtList.get(i + 1));
//                                Log.e("DrawTestView_san", "linePointList中添加的坐标是：index= " + 0 + "------->" + "(" +
//                                        linePointList.get(0)
//                                                .getX() + "," + linePointList.get(0).getY() + ")" + "index=" + 1 +
//                                        "------->"
//                                        + "" +
//                                        "(" +
//                                        linePointList.get(1).getX() + "," + linePointList.get(1).getY() + ")");
                                index = i;//获取需要改变的点的下标
//                                Log.e("DrawTestView_san", "index_DOWN_second=  " + index);
                                /*再次做判断0和1*/
                                drawExtendLine(curPtList.get(i), curPtList.get(i + 1));
                            }
                        }
                    }
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.e("DrawTestView", "MOVE");

                if (!isComplete) {
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                    //当前点与上一个拐点比较
                    int dx1 = lastX - inflexionX;
                    int dy1 = lastY - inflexionY;
                    //两点之间的距离大于等于2时判读方向
                    if (Math.abs(lastX - startX) > 2 || Math.abs(lastY - startY) > 2) {
                        //判断水平方向
                        if (Math.abs(lastX - startX) > Math.abs(lastY - startY)) {
                            //上个方向是垂直且flag集合中都是垂直记录则方向发生变化
                            if (orientation == 1 && !flag.contains(0)) {
                                //保存拐点
                                points.add(new Point(newX, newY));
                                //赋值成上一个拐点
                                inflexionX = newX;
                                inflexionY = newY;
                                path.reset();
                                path.moveTo(firstX, firstY);
                                for (Point point : points) {
                                    path.lineTo(point.getX(), point.getY());
                                    invalidate();
                                }
                            }
                            //水平方向
                            orientation = 0;
                            //flag方向标识的添加和移除更新flag
                            flag.add(0, 0);
                            flag.remove(6);
                            //当距离变化超过20时更新可能成为当前拐点的x,y
                            if (Math.abs(dx1) > 20 || Math.abs(dy1) > 20) {
                                newY = inflexionY;
                                newX = lastX;
                                path.lineTo(newX, newY);
                            }
                        } else if (Math.abs(lastX - startX) < Math.abs(lastY - startY)) {//垂直方向
                            if (orientation == 0 && !flag.contains(1)) {
                                points.add(new Point(newX, newY));
                                //赋值上一个拐点
                                inflexionX = newX;
                                inflexionY = newY;
                                path.reset();
                                path.moveTo(firstX, firstY);
                                for (Point point : points) {
                                    path.lineTo(point.getX(), point.getY());
                                    invalidate();
                                }
                            }
                            orientation = 1;
                            flag.add(0, 1);
                            flag.remove(6);
                            if (Math.abs(dx1) > 20 || Math.abs(dy1) > 20) {
                                newX = inflexionX;
                                newY = lastY;
                                path.lineTo(newX, newY);
                            }
                        }
                    }
                    //更新一点的点
                    startX = lastX;
                    startY = lastY;
                } else {
                    //是否在图形内，且距离每条边的距离是否都大于20，是就拖拽滑动
                    int count = 0;
                    for (Float aFloat : culDisList) {
                        if (aFloat > judgeDis) {
                            count++;
                        }
                    }
                    //拖拽移动整体
                    if (isMoving && count == culDisList.size()) {
                        float dx = x - cur_x;
                        float dy = y - cur_y;
                        for (int i = 0; i < curPtList.size(); i++) {
                            curPtList.get(i).setX(pointss.get(i).getX() + dx);
                            curPtList.get(i).setY(pointss.get(i).getY() + dy);
//                            Log.e("DrawTestView_san", "拖拽时坐标点：" + pointss.size() + "/" + pointss.get(i).getX() + "/" +
//                                    pointss.get(i).getY());
                        }

                    } else {  //拖拽移动某一条直线
                        Point referPoint1 = null, referPoint2 = null;
                        float referPoint1X = 0, referPoint1Y = 0, referPoint2X = 0, referPoint2Y = 0;
                        if (linePointList.size() > 0) {
                            referPoint1 = linePointList.get(0);
                            referPoint2 = linePointList.get(1);
                            //两个旧端点坐标  即起点坐标
                            referPoint1X = referPoint1.getX();
                            referPoint1Y = referPoint1.getY();
                            referPoint2X = referPoint2.getX();
                            referPoint2Y = referPoint2.getY();
                        }
                        dx = Math.abs(x - cur_x);
                        dy = Math.abs(y - cur_y);
                        //两个新端点的坐标
                        float newReferPoint1X = referPoint1X, newReferPoint1Y = referPoint1Y,
                                newReferPoint2X = referPoint2X, newReferPoint2Y = referPoint2Y;
                        if (dx > dy) {
                            // 水平移动  y坐标不变 x坐标变化curX ,判断扩大还是缩小
                            //判断curX的范围，要保证curX在两个y坐标点之间
                            if (Math.min(referPoint1Y, referPoint2Y) < y && y < Math.max(referPoint1Y, referPoint2Y)) {
                                float dx1 = x - cur_x; //偏移量
                                newReferPoint1X = referPoint1X + dx1;
                                newReferPoint2X = referPoint2X + dx1;
                                if (index == -1) {
                                    Point point = curPtList.get(0);
                                    Point point1 = curPtList.get(curPtList.size() - 1);
                                    //获得按下时候的点坐标
                                    if (isFirstX) {
                                        x1 = point.getX();
                                        isFirstX = false;
                                    }
                                    //每次移动时候将按下时候记录下的值赋值给点，这样每次加偏移量就没有问题了
                                    point.setX(x1);
                                    point1.setX(x1);

                                    point.setX(point.getX() + dx1);
                                    point1.setX(point1.getX() + dx1);
                                    pointss.get(0).setX(point.getX());
                                    pointss.get(curPtList.size() - 1).setX(point1.getX());

//                                    Log.e("DrawTestView_san", "水平移动增大index == -1：" + index + " x：" + x
//                                            + " referPoint1X: " + referPoint1X);
                                } else {
                                    Point point = curPtList.get(index);
                                    Point point1 = curPtList.get(index + 1);
                                    //获得按下时候的点坐标
                                    if (isFirstX1) {
                                        x2 = point.getX();
                                        isFirstX1 = false;
                                    }
                                    //每次移动时候将按下时候记录下的值赋值给点，这样每次加偏移量就没有问题了
                                    point.setX(x2);
                                    point1.setX(x2);

                                    point.setX(point.getX() + dx1);
                                    point1.setX(point1.getX() + dx1);
                                    pointss.get(index).setX(point.getX());
                                    pointss.get(index + 1).setX(point1.getX());
//                                    Log.e("DrawTestView_san", "水平移动增大else" + "/referPoint1X：" + referPoint1X
//                                            + "/dx1：" + dx1 + "/x1：" + x1);
                                }
                            }
//                            exPath.moveTo(referPoint1X, referPoint1Y);
//                            exPath.lineTo(newReferPoint1X, referPoint1Y);
//                            exPath.moveTo(referPoint2X, referPoint2Y);
//                            exPath.lineTo(newReferPoint2X, referPoint2Y);
                        }
                        // 垂直移动 x坐标不动 y坐标变化curY, 判断扩大还是缩小
                        if (dx < dy) {
                            if (Math.min(referPoint1X, referPoint2X) < x && x < Math.max(referPoint1X, referPoint2X)) {
                                float dy1 = y - cur_y; //y方向偏移量
                                newReferPoint1Y = referPoint1Y + dy;
                                newReferPoint2Y = referPoint2Y + dy;

                                if (index == -1) {
                                    Point point = curPtList.get(0);
                                    Point point1 = curPtList.get(curPtList.size() - 1);
                                    //获得按下时候的点坐标
                                    if (isFirstY) {
                                        y1 = point.getY();
                                        isFirstY = false;
                                    }
                                    //每次移动时候将按下时候记录下的值赋值给点，这样每次加偏移量就没有问题了
                                    point.setY(y1);
                                    point1.setY(y1);

                                    point.setY(point.getY() + dy1);
                                    point1.setY(point1.getY() + dy1);
                                    pointss.get(0).setY(point.getY());
                                    pointss.get(curPtList.size() - 1).setY(point1.getY());
//                                    Log.e("DrawTestView_san", "垂直移动增大index == -1\n" + "point:" + point.getY() +
//                                            "/point1:" + point1.getY());
                                } else {
                                    Point point = curPtList.get(index);
                                    Point point1 = curPtList.get(index + 1);
                                    //获得按下时候的点坐标
                                    if (isFirstY1) {
                                        y2 = point.getY();
                                        isFirstY1 = false;
                                    }
                                    //每次移动时候将按下时候记录下的值赋值给点，这样每次加偏移量就没有问题了
                                    point.setY(y2);
                                    point1.setY(y2);

                                    point.setY(point.getY() + dy1);
                                    point1.setY(point1.getY() + dy1);
                                    pointss.get(index).setY(point.getY());
                                    pointss.get(index + 1).setY(point1.getY());
//                                    Log.e("DrawTestView_san", "垂直移动增大else\n" + "point:" + point.getY() +
//                                            "/point1:" + point1.getY());
                                }
                            }
                            exPath.moveTo(referPoint1X, referPoint1Y);
                            exPath.lineTo(referPoint1X, newReferPoint1Y);
                            exPath.moveTo(referPoint2X, referPoint2Y);
                            exPath.lineTo(referPoint2X, newReferPoint2Y);
                        }

                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (!isComplete) {
                    if (points.size() >= 2) {
                        closePolygon();
                        intentPoints.clear();
                        intentPoints.addAll(points);
                        isComplete = true; // TODO: 2017/7/23
                        anewDraw(points);
                        points.clear();

                        pointss = getPoints();
                        for (Point point : pointss) {
                            curPtList.add(new Point(point.getX(), point.getY()));
                            Log.e("DrawTestView_san", "原始坐标点pointss:" + pointss.size() + " /" + point.getX() + "/" +
                                    point.getY());
                        }
                    }
                } else {
                    int count = 0;
                    for (Float aFloat : culDisList) {
                        if (aFloat > judgeDis) {
                            count++;
                        }
                    }
                    //当移动某一条直线时，如果不进行触摸点到直线距离的判断会改变pointss的原始坐标
                    // 导致拖拽整体时出现偏移的情况
                    if (isMoving && count == culDisList.size()) {
                        Log.e("DrawTestView_san", "整体移动是否执行此方法");
                        moveSetData();

                        distanceList.clear();
                        for (int i = 0; i < pointss.size(); i++) {
                            Point pointI = pointss.get(i);
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
                            Collections.sort(distanceList, new Comparator<TwoPointDistance>() {
                                @Override
                                public int compare(TwoPointDistance o1, TwoPointDistance o2) {
                                    return (int)(o1.getDistance()-o2.getDistance());
                                }
                            });
                            for (TwoPointDistance twoPointDistance : distanceList) {
                                Log.e("DrawTestView_san", "twoPointDistance:" + twoPointDistance.toString());
                            }
                            xifuSetData(distanceList);
                        }else{  //没有靠近就不吸附
                            moveSetData();
                        }
                    } else {
                        isFirstX = true;
                        isFirstX1 = true;
                        isFirstY = true;
                        isFirstY1 = true;
                    }
                }
                break;
            }
        }

        // 通知刷新界面
        invalidate();
        return true;
    }

    /**
     * 移动后重置数据
     */
    public void moveSetData() {
        float dx1 = x - cur_x;
        float dy1 = y - cur_y;
//     Log.e("DrawTestView_san", "移动偏移量UP" + dx + "/" + dy);
        List<Point> newPointList1 = new ArrayList<>();
        for (Point point : pointss) {
            float newX = point.getX() + dx1;
            float newY = point.getY() + dy1;
            Point newPoint = new Point(newX, newY);
            newPointList1.add(newPoint);
//   Log.e("DrawTestView_san", "拖拽后坐标点pointss:" + pointss.size() + " /" + newPoint.getX() + "/" +
//     newPoint.getY());
        }
        pointss.clear();
        pointss.addAll(newPointList1);
    }

    /**
     * 吸附后重置数据
     */
    public void xifuSetData(List<TwoPointDistance> distanceList) {
        Point first = distanceList.get(0).first;
        Point second = distanceList.get(0).second;
        float dx_xifu = second.getX() - first.getX();
        float dy_xifu = second.getY() - first.getY();

        List<Point> newPointList = new ArrayList<>();
        for (Point point : pointss) {
            float newX = point.getX() + dx_xifu;
            float newY = point.getY() + dy_xifu;
            Point newPoint = new Point(newX, newY);
            newPointList.add(newPoint);
        }
        pointss.clear();
        pointss.addAll(newPointList);
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
        Log.e("DrawTestView_san", "drawExtendLine:" + point1.getX() + "/" + point1.getY() + "\n" +
                point2.getX() + "/" + point2.getY());
    }


    /**
     * 重新绘制
     *
     * @param points 点集合
     */
    public void anewDraw(List<Point> points) {
        path.reset();
        path.moveTo(firstX, firstY);
        for (Point point : points) {
            path.lineTo(point.getX(), point.getY());
            invalidate();
        }

    }

    public List<Point> getPoints() {
        if (isRemoveStartPiont) {
            intentPoints.remove(0);
            intentPoints.remove(intentPoints.size() - 1);
        } else {
            intentPoints.remove(0);
        }
        isRemoveStartPiont = false;
        return intentPoints;
    }


    public void closePolygon() {
        List<Point> polygonPoints = new ArrayList<>();
        polygonPoints.addAll(points);
        Point secendPoint = points.get(1);

        if (!flag.subList(1, 5).contains(1)) {
            //形成多边形集合
            polygonPoints.add(new Point(lastX, inflexionY));
            polygonPoints.add(new Point(firstX, firstY));

            //获得2个点
            Point p1 = new Point(firstX, inflexionY);
            Point p2 = new Point(lastX, firstY);
            if (PtInRegion(p2, polygonPoints) == -1) {//p1在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();
                t2.add(p2);
                t2.add(new Point(inflexionX, inflexionY));
                if (DrawUtils.PtInRegion(new Point(lastX, inflexionY), t2) != 0) {
                    points.add(new Point(lastX, inflexionY));
                }

                points.add(p2);
                points.add(new Point(firstX, firstY));
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(secendPoint);
                List<Point> q2 = new ArrayList<>();
                q2.add(p2);
                q2.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0
                        || DrawUtils.PtInRegion(secendPoint, q2) == 0) {
                    isRemoveStartPiont = true;
                }
            } else {
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(inflexionX, inflexionY));
                if (DrawUtils.PtInRegion(new Point(lastX, inflexionY), t1) != 0) {
                    points.add(new Point(lastX, inflexionY));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(secendPoint);
                List<Point> q1 = new ArrayList<>();
                q1.add(p2);
                q1.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0
                        || DrawUtils.PtInRegion(secendPoint, q1) == 0) {
                    isRemoveStartPiont = true;
                }
            }
        } else if (!flag.subList(1, 5).contains(0)) {
            //形成多边形集合
            polygonPoints.add(new Point(inflexionX, lastY));
            polygonPoints.add(new Point(firstX, firstY));
            //获得2个点
            Point p1 = new Point(firstX, lastY);
            Point p2 = new Point(inflexionX, firstY);
            if (DrawUtils.PtInRegion(p2, polygonPoints) == -1) {//p1在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();
                t2.add(p2);
                t2.add(new Point(inflexionX, inflexionY));
                if (DrawUtils.PtInRegion(new Point(inflexionX, lastY), t2) != 0) {
                    points.add(new Point(inflexionX, lastY));
                }
                points.add(p2);
                points.add(new Point(firstX, firstY));
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(secendPoint);
                List<Point> q2 = new ArrayList<>();
                q2.add(p2);
                q2.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0
                        || DrawUtils.PtInRegion(secendPoint, q2) == 0) {
                    isRemoveStartPiont = true;
                }
            } else {
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(inflexionX, inflexionY));
                if (DrawUtils.PtInRegion(new Point(inflexionX, lastY), t1) != 0) {
                    points.add(new Point(inflexionX, lastY));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(secendPoint);
                List<Point> q1 = new ArrayList<>();
                q1.add(p2);
                q1.add(new Point(firstX, firstY));
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0
                        || DrawUtils.PtInRegion(secendPoint, q1) == 0) {
                    isRemoveStartPiont = true;
                }
            }
        }
    }

    /**
     * 清除画板
     */
    public void cleanDraw() {
        path.reset();
        exPath.reset();
        intentPoints.clear();
        points.clear();
        pointss.clear();
        curPtList.clear();
        orientation = 7;
        isComplete = false;
        isRemoveStartPiont = false;
        flag.clear();
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        invalidate();
    }


}
