package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.hasee.drawtest.model.Line;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.utils.DrawUtils;
import com.example.hasee.drawtest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class MagicPlanDrawView_copy extends View {
    private Context context;
    private List<Point> points = new ArrayList<>();
    private List<Point> movePoints = new ArrayList<>();
    private List<PoPoListModel> showPolygons = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Paint pathPaint = new Paint();
    private Path pathDash = new Path();
    private Path path = new Path();
    private Paint circlePaint = new Paint();
    private float startX, startY;
    private float lastX, lastY;
    private float x, y;//上一个折点
    private float dx, dy;//偏移
    private float cx, cy;//圆心
    private boolean isFirst = true;
    private Point lastSecond;//倒数第二个点
    private int flag;//线段相交标识
    private long downTime;//按下时间
    private long upTime;//抬起时间
    private int downPosition = 0;//落点位置判断  移动线和图
    private boolean isDrawCir = true;//是否画圆
    private Canvas mCanvas;
    private int mode;
    private float distance;
    private float preDistance;
    private int midX;
    private int midY;
    private float mScale = 1.0f;
    private float curScale = 1.0f;

    public float getmScale() {
        return mScale;
    }

    public void setmScale(float mScale) {
        this.mScale = mScale;
        curScale = mScale;
    }

    public List<Point> getMovePoints() {
        return movePoints;
    }

    public void setMovePoints(List<Point> movePoints) {
        this.movePoints = movePoints;
    }

    public List<PoPoListModel> getShowPolygons() {
        return showPolygons;

    }

    public void setShowPolygons(List<PoPoListModel> showPolygons) {
        this.showPolygons = showPolygons;
        invalidate();
    }


    public MagicPlanDrawView_copy(Context context) {
        super(context);
        init(context);
    }

    public MagicPlanDrawView_copy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MagicPlanDrawView_copy(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.GRAY);
        pathPaint.setStrokeWidth(10);
        pathPaint.setStyle(Paint.Style.STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(5f);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        midX = w / 2;
        midY = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                startX = (e.getX() - midX) / curScale + midX;
                startY = (e.getY() - midY) / curScale + midY;
                if (points.size() > 0 && points != null) {
                    if (Utils.lineSpace((int) startX, (int) startY, (int) points.get(points.size() - 1).getX(), (int) (points.get(points.size() - 1).getY())) < 80 / curScale) {
                        downPosition = 1;
                    } else {
                        downPosition = -1;
                    }
                }
                mode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                preDistance = DrawUtils.getDistance(e);
                //当两指间距大于10时，计算两指中心点
                if (preDistance > 10f) {
                    mode = 2;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mScale = curScale;
                mode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //当两指缩放，计算缩放比例
                if (mode == 2) {
                    distance = DrawUtils.getDistance(e);
                    if (distance > 10f) {
                        curScale = mScale * (distance / preDistance);
                    }
                } else if (mode == 1) {
                    if (points.size() > 0 && points != null) {
                        lastX = (e.getX() - midX) / curScale + midX;
                        lastY = (e.getY() - midY) / curScale + midY;
                        if (downPosition == 1) {
                            actionMove(e);
                        } else if (downPosition == -1) {
                            moveCanvas(e);
                        }
                    } else {
                        lastX = (e.getX() - midX) / curScale + midX;
                        lastY = (e.getY() - midY) / curScale + midY;
                        moveCanvas(e);
                        startX = lastX;
                        startY = lastY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                upTime = System.currentTimeMillis();
                float upX = (e.getX() - midX) / curScale + midX;
                float upY = (e.getY() - midY) / curScale + midY;
                if (upTime - downTime <= 150 && Utils.lineSpace(startX, startY, upX, upY) <= 1) {//为点击
                    for (PoPoListModel p : showPolygons) {
                        if (p.inPolygon((int) startX, (int) startY)) {
                            Toast.makeText(context, "您不可以在已有的房间内创建拐点", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (points.size() > 0) {
                            if (isHaveJDWithPolygons(new Point(startX, startY), points.get(points.size() - 1))) {
                                Toast.makeText(context, "您不可以在已有的房间内创建拐点", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }

                    }

                    if (isFirst) {
                        isFirst = false;
                        x = startX;
                        y = startY;
                        cx = startX;
                        cy = startY;
                        points.add(new Point(x, y));
                    } else {
                        double l = Utils.lineSpace(startX, startY, points.get(points.size() - 1).getX(),(points.get(points.size() - 1).getY()));
                        if (l > 80) {
                            if (points.size() > 2) {
                                flags.clear();
                                for (int i = 0; i < points.size() - 2; i++) {
                                    flag = Utils.segIntersect(points.get(points.size() - 1), new Point(startX, startY), points.get(i), points.get(i + 1));
                                    flags.add(flag);
                                }
                                if (!flags.contains(1)) {
                                    x = startX;
                                    y = startY;
                                    points.add(new Point(x, y));
                                    lineLocation();
                                }
                            } else {
                                x = startX;
                                y = startY;
                                points.add(new Point(x, y));
                                lineLocation();
                            }
                        }
                    }
                } else {//为移动
                }
                actionUp(e);
                break;
        }

        invalidate();
        return true;
    }

    private void moveCanvas(MotionEvent e) {
        for (PoPoListModel polygon : showPolygons) {
            for (Point point : polygon.getList()) {
                point.setX((int) (point.getX() + lastX - startX));
                point.setY((int) (point.getY() + lastY - startY));
            }
        }
        if (points.size() > 0) {
            for (Point p : points) {
                p.setX(p.getX() + lastX - startX);
                p.setY(p.getY() + lastY - startY);
            }
            cx = points.get(points.size() - 1).getX();
            cy = points.get(points.size() - 1).getY();
            startX = lastX;
            startY = lastY;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        mCanvas.save();
        mCanvas.scale(curScale, curScale, midX, midY);
        if (showPolygons.size() > 0 && showPolygons != null) {
            for (PoPoListModel polygon : showPolygons) {
                List<Point> getPoints = polygon.getList();
                if (getPoints.size() >= 2 && getPoints != null) {
                    path.reset();
                    path.moveTo(getPoints.get(0).getX(), getPoints.get(0).getY());
                    for (int i = 1; i < getPoints.size(); i++) {
                        path.lineTo(getPoints.get(i).getX(), getPoints.get(i).getY());
                    }
                    path.close();
                    mCanvas.drawPath(path, pathPaint);
                }
            }
        }

        if (points.size() >= 2) {
            for (int i = 0; i < points.size() - 1; i++) {
                Point p = points.get(i);
                Point p1 = points.get(i + 1);
                mCanvas.drawLine(p.getX(), p.getY(), p1.getX(), p1.getY(), mPaint);
            }
            //虚线
            pathDash.reset();
            pathDash.moveTo(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY());
            pathDash.lineTo(points.get(0).getX(), points.get(0).getY());
            mCanvas.drawPath(pathDash, mPaint);
        }
        if (isDrawCir && points.size() > 0) {
            circlePaint.setStrokeWidth(10 / curScale);
            mCanvas.drawCircle(cx, cy, 80 / curScale, circlePaint);
        }
    }


    List<Integer> flags = new ArrayList<>();

    /**
     * 滑动
     *
     * @param e
     */
    private void actionMove(MotionEvent e) {
        x = lastX;
        y = lastY;
        if (points.size() > 1) {
            flags.clear();
            for (int i = 0; i < showPolygons.size(); i++) {
                List<Line> lines = showPolygons.get(i).getLines();
                for (Line l : lines) {
                    flag = Utils.segIntersect(lastSecond, new Point(x, y), l.getP1(), l.getP2());
                    flags.add(flag);
                }
            }
            if (!flags.contains(1)) {
                if (points.size() > 3) {//判断点与画的线是否有交点
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
                    }
                } else if (isHaveJDWithPolygons(lastSecond, new Point(x, y))) {
                    lineLocation();
                    //更新点
                    startX = lastX;
                    startY = lastY;
                } else {
                    lineLocation();
                    //更新点
                    startX = lastX;
                    startY = lastY;
                }
            }
        } else if (points.size() == 1) {
            points.get(0).setX(x);
            points.get(0).setY(y);
            //更新点
            startX = lastX;
            startY = lastY;
            cx = lastX;
            cy = lastY;
        }

    }

    private boolean isHaveJDWithPolygons(Point p1, Point p2) {
        boolean isHave = false;
        for (int i = 0; i < showPolygons.size(); i++) {
            List<Line> lines = showPolygons.get(i).getLines();
            flags.clear();
            for (Line l : lines) {
                flag = Utils.segIntersect(p1, p2, l.getP1(), l.getP2());
                flags.add(flag);
            }
            if (flags.contains(1)) {
                isHave = true;//有交点 ,不绘制
            } else {
                isHave = false;//无交点 ,绘制
            }
        }
        return isHave;
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
            if (dy < 80) {
                points.get(points.size() - 1).setX(x);
                points.get(points.size() - 1).setY(lastSecond.getY());
                cx = x;
                cy = lastSecond.getY();
            } else if (dx < 80) {
                points.get(points.size() - 1).setX(lastSecond.getX());
                points.get(points.size() - 1).setY(y);
                cx = lastSecond.getX();
                cy = y;
            } else {
                points.get(points.size() - 1).setX(x);
                points.get(points.size() - 1).setY(y);
                cx = x;
                cy = y;
            }
        }

    }

    public boolean closeView() {
        //判断虚线是否有相交线段,相交则无法绘制
        flags.clear();
        movePoints.clear();
//        if (points != null && points.size() > 1) {
//
//        }
        if (points.size() <= 2) {
            return false;
        }
        for (int i = 0; i < points.size() - 1; i++) {
            flag = Utils.segIntersect(points.get(0), points.get(points.size() - 1), points.get(i), points.get(i + 1));
            flags.add(flag);
        }
        if (!flags.contains(1)) {
            //起点,最后一个点和倒数第二个点在一条直线上,或者第一个点到最后一条线的距离小于10则去掉最后一个点,完成绘制
            if (Utils.segIntersect(points.get(0), points.get(points.size() - 1), points.get(points.size() - 1), lastSecond) == 0
                    || Utils.pointToLine((int) points.get(0).getX(), (int) points.get(0).getY(), points.get(points.size() - 1), lastSecond) < 20) {
                pathDash.reset();
                points.remove(points.get(points.size() - 1));
                movePoints.addAll(points);
                points.add(points.get(0));
            } else {
                pathDash.reset();
                movePoints.addAll(points);
                points.add(points.get(0));
            }
            invalidate();
            isDrawCir = false;
            showPolygons.add(new PoPoListModel(movePoints));
            return true;
        } else {
            return true;
        }
    }

}
