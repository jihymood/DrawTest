package com.example.hasee.drawtest.weidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 *
 * 同瑞部分功能
 */

public class DrawPolygonView extends View {
    private Context context;
    private List<Point> points = new ArrayList<>();
    private List<Point> intentPoints = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Paint linePaint = new Paint();
    private Path mPath = new Path();
    int x;//上一个拐点x
    int y;//上一个拐点y
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
    List<Integer> flag;//判断手指移动方向的集合
    boolean drawAble = true;//判断是否可画图
    List<Point> movePoints = new ArrayList<>();//用于存放变化的点
    boolean isRemoveStartPiont;

    public void setDrawAble(boolean drawAble) {
        this.drawAble = drawAble;
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

    public DrawPolygonView(Context context) {
        super(context);
        init(context);
    }

    public DrawPolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawPolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        this.context = context;
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        flag = new ArrayList<>();
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
        flag.add(7);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!drawAble) {
            return false;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                int mx = (int) e.getX();
                int my = (int) e.getY();
                startX = mx;
                startY = my;
                mPath.moveTo(mx, my);
                x = mx;
                y = my;
                firstX = mx;
                firstY = my;
                points.add(new Point(startX, startY));
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = (int) e.getX();
                lastY = (int) e.getY();
                //当前点与上一个拐点比较
                int dx = lastX - x;
                int dy = lastY - y;
                //两点之间的距离大于等于2时判读方向
                if (Math.abs(lastX - startX) > 2 || Math.abs(lastY - startY) > 2) {
                    //判断水平方向
                    if (Math.abs(lastX - startX) > Math.abs(lastY - startY)) {
                        //上个方向是垂直且flag集合中都是垂直记录则方向发生变化
                        if (orientation == 1 && !flag.contains(0)) {
                            //保存拐点
                            points.add(new Point(newX, newY));
                            //赋值成上一个拐点
                            x = newX;
                            y = newY;
                            mPath.reset();
                            mPath.moveTo(firstX, firstY);
                            for (Point point : points) {
                                mPath.lineTo(point.getX(), point.getY());
                                invalidate();
                            }
                        }
                        //水平方向
                        orientation = 0;
                        //flag方向标识的添加和移除更新flag
                        flag.add(0, 0);
                        flag.remove(6);
                        //当距离变化超过20时更新可能成为当前拐点的x,y
                        if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                            newY = y;
                            newX = lastX;
                            mPath.lineTo(newX, newY);
                        }
                    } else if (Math.abs(lastX - startX) < Math.abs(lastY - startY)) {//垂直方向
                        if (orientation == 0 && !flag.contains(1)) {
                            points.add(new Point(newX, newY));
                            //赋值上一个拐点
                            x = newX;
                            y = newY;
                            mPath.reset();
                            mPath.moveTo(firstX, firstY);
                            for (Point point : points) {
                                mPath.lineTo(point.getX(), point.getY());
                                invalidate();
                            }
                        }
                        orientation = 1;
                        flag.add(0, 1);
                        flag.remove(6);
                        if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                            newX = x;
                            newY = lastY;
                            mPath.lineTo(newX, newY);
                        }
                    }
                }
                //更新一点的点
                startX = lastX;
                startY = lastY;
                break;
            case MotionEvent.ACTION_UP:
                if (points.size() > 2) {
                    closePolygon();
                    anewDraw(points);
                    intentPoints.clear();
                    intentPoints.addAll(points);
                    points.clear();
                    drawAble = false;
                }
                break;
        }
        // 更新绘制
        invalidate();
        return drawAble;

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 清除画板
     */
    public void cleanDraw() {
        mPath.reset();
        intentPoints.clear();
        points.clear();
        orientation = 7;
        drawAble = true;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    public void closePolygon() {
        List<Point> polygonPoints = new ArrayList<>();
        polygonPoints.addAll(points);
        Point second = points.get(1);

        if (!flag.subList(1, 5).contains(1)) {
            //形成多边形集合
            polygonPoints.add(new Point(lastX, y));
            polygonPoints.add(new Point(firstX, firstY));
            //获得2个点
            Point p1 = new Point(firstX, y);
            Point p2 = new Point(lastX, firstY);
            if (DrawUtils.PtInRegion(p2, polygonPoints) == -1) {//p2在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();
                t2.add(p2);
                t2.add(new Point(x, y));
                if (DrawUtils.PtInRegion(new Point(lastX, y), t2) != 0) {
                    points.add(new Point(lastX, y));
                }
                points.add(p2);
                points.add(new Point(firstX, firstY));
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(second);
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0) {
                    isRemoveStartPiont = true;
                }
            } else {
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(x, y));
                if (DrawUtils.PtInRegion(new Point(lastX, y), t1) != 0) {
                    points.add(new Point(lastX, y));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(second);
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0) {
                    isRemoveStartPiont = true;
                }
            }
        } else if (!flag.subList(1, 5).contains(0)) {
            //形成多边形集合
            polygonPoints.add(new Point(x, lastY));
            polygonPoints.add(new Point(firstX, firstY));
            //获得2个点
            Point p1 = new Point(firstX, lastY);
            Point p2 = new Point(x, firstY);
            if (DrawUtils.PtInRegion(p2, polygonPoints) == -1) {//p2在多变形外
                //判断是否添加抬手点
                List<Point> t2 = new ArrayList<>();
                t2.add(p2);
                t2.add(new Point(x, y));
                if (DrawUtils.PtInRegion(new Point(x, lastY), t2) != 0) {
                    points.add(new Point(x, lastY));
                }
                points.add(p2);
                points.add(new Point(firstX, firstY));
                List<Point> s2 = new ArrayList<>();
                s2.add(p2);
                s2.add(second);
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s2) == 0) {
                    isRemoveStartPiont = true;
                }
            } else {
                //判断是否添加抬手点
                List<Point> t1 = new ArrayList<>();
                t1.add(p1);
                t1.add(new Point(x, y));
                if (DrawUtils.PtInRegion(new Point(x, lastY), t1) != 0) {
                    points.add(new Point(x, lastY));
                }
                points.add(p1);
                points.add(new Point(firstX, firstY));
                List<Point> s1 = new ArrayList<>();
                s1.add(p1);
                s1.add(second);
                if (DrawUtils.PtInRegion(new Point(firstX, firstY), s1) == 0) {
                    isRemoveStartPiont = true;
                }
            }
        }
    }

    /**
     * 重新绘制
     *
     * @param points 点集合
     */
    public void anewDraw(List<Point> points) {
        mPath.reset();
        mPath.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point point : points) {
            mPath.lineTo(point.getX(), point.getY());
            invalidate();
        }
    }


}
