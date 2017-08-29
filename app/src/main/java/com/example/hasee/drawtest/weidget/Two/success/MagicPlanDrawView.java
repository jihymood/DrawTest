package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class MagicPlanDrawView extends View {
    private Context context;
    private List<Point> points = new ArrayList<>();
    private List<Point> movePoints = new ArrayList<>();
    private List<PoPoListModel> showPolygons = new ArrayList<>();
    private List<BaseView.ImageGroup> imageGroups = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBZPaint = new Paint();
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

    /**
     * 是否华正方形
     */
    private boolean isDrawSquare;
    /**
     * 是否折线
     */
    private boolean isDrawPolygonalLine;
    /**
     * 是否是第一次画正方形
     */
    private boolean isFirstDrawSquare;
    /**
     * 不规则图形的重心
     */
    private Point gravityPoint;

    /**
     * 画笔
     */
    private Paint writingPaint;

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

    public List<BaseView.ImageGroup> getImageGroups() {
        return imageGroups;
    }

    public void setImageGroups(List<BaseView.ImageGroup> imageGroups) {
        this.imageGroups = imageGroups;
        invalidate();
    }

    public MagicPlanDrawView(Context context) {
        super(context);
        init(context);
    }

    public MagicPlanDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MagicPlanDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextSize(20);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(5f);
        circlePaint.setStyle(Paint.Style.STROKE);
        mBZPaint.setAntiAlias(true);
        mBZPaint.setColor(Color.BLACK);
        mBZPaint.setStrokeWidth(5);
        mBZPaint.setColor(Color.RED);
        mBZPaint.setStyle(Paint.Style.STROKE);

        //添加文字画笔
        writingPaint = new Paint();
        writingPaint.setColor(Color.BLACK);
        writingPaint.setStrokeWidth(15);
        writingPaint.setStyle(Paint.Style.FILL);
        writingPaint.setAntiAlias(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        midX = w / 2;
        midY = h / 2;
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

                    if (polygon.getText() != null) {
                        gravityPoint = DrawUtils.getCenterOfGravityPoint(polygon.getList());
                        canvas.drawText(polygon.getText(), this.gravityPoint.getX(), this.gravityPoint.getY(), writingPaint);
                    }

                }
            }
        }

        /**
         * 绘制图标，感觉在这个view中画多此一举
         */
//        if (imageGroups.size() > 0 && imageGroups != null) {
//            for (BaseView.ImageGroup imageGroup : imageGroups) {
//                imageGroup.bitmap = Utils.getBitmap(imageGroup.getPic());
//                Matrix matrix = new Matrix();
//                matrix.setValues(imageGroup.getValues());
//                imageGroup.matrix = matrix;
//
//                float[] points = getBitmapPoints(imageGroup.bitmap, imageGroup.matrix);
//                float x1 = points[0];
//                float y1 = points[1];
//                float x2 = points[2];
//                float y2 = points[3];
//                float x3 = points[4];
//                float y3 = points[5];
//                float x4 = points[6];
//                float y4 = points[7];
//
//                canvas.drawLine(x1, y1, x2, y2, pathPaint);
//                canvas.drawLine(x2, y2, x4, y4, pathPaint);
//                canvas.drawLine(x4, y4, x3, y3, pathPaint);
//                canvas.drawLine(x3, y3, x1, y1, pathPaint);
//                canvas.drawCircle(x2, y2, 20, pathPaint);
//                canvas.drawBitmap(imageGroup.bitmap, x2 - imageGroup.bitmap.getWidth() / 2, y2 - imageGroup.bitmap.getHeight() / 2,
//                        pathPaint); //右上角叉叉
//                canvas.drawBitmap(imageGroup.bitmap, imageGroup.matrix, pathPaint); //贴纸
//            }
//        }



        //画折线
        if (isDrawPolygonalLine) {
            if (points.size() >= 2) {
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p = points.get(i);
                    Point p1 = points.get(i + 1);
                    mCanvas.drawLine(p.getX(), p.getY(), p1.getX(), p1.getY(), mPaint);
                    drawBz(p, p1, canvas);
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
        // TODO: 2017/8/23
        //画正方形
        if (isDrawSquare ) {
            if (isFirstDrawSquare) {
                canvas.drawRect(new Rect(300, 300, 600, 600), pathPaint);
                points.clear();
                points.add(new Point(300, 300));
                points.add(new Point(600, 300));
                points.add(new Point(600, 600));
                points.add(new Point(300, 600));
                lastSecond = new Point(600, 600);
                isFirstDrawSquare = false;
            }else{
                path.reset();
                if (points != null && points.size() >= 2) {
                    path.moveTo(points.get(0).getX(), points.get(0).getY());
                    for (int i = 1; i < points.size(); i++) {
                        path.lineTo(points.get(i).getX(), points.get(i).getY());
                    }
                    path.close();
                    canvas.drawPath(path, pathPaint);
                }
            }
        }
    }

    protected float[] getBitmapPoints(Bitmap bitmap, Matrix matrix) {
        float[] dst = new float[8];
        float[] src = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()
        };

        matrix.mapPoints(dst, src);
        return dst;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                startX = (e.getX() - midX) / curScale + midX;
                startY = (e.getY() - midY) / curScale + midY;
                if (points.size() > 0 && points != null) {
                    if (Utils.lineSpace((int) startX, (int) startY, (int) points.get(points.size() - 1).getX(), (int)
                            (points.get(points.size() - 1).getY())) < 80 / curScale) {
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
                        double l = Utils.lineSpace(startX, startY, points.get(points.size() - 1).getX(), (points.get
                                (points.size() - 1).getY()));
                        if (l > 80) {
                            if (points.size() > 2) {
                                flags.clear();
                                for (int i = 0; i < points.size() - 2; i++) {
                                    flag = Utils.segIntersect(points.get(points.size() - 1), new Point(startX,
                                            startY), points.get(i), points.get(i + 1));
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
//        isDrawSquare = false;
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


    private void drawBz(Point p, Point p1, Canvas canvas) {
        double degree = Utils.getDegreeByK(p, p1);
        double l = Utils.lineSpace(p, p1);
        NumberFormat format = new DecimalFormat("0.00");
        String length = format.format(l / 50);
        Point newP = Utils.getNewPByDegree(p, p1, degree);
        float nx = newP.getX();
        float ny = newP.getY();
        float centerX = (nx + p.getX()) / 2;
        float centerY = (ny + p.getY()) / 2 - 40;
        Point xnc = new Point(centerX, centerY);
        Point c = Utils.getNewPByDegree(p, xnc, 360 - degree);
        Point xnd1 = new Point(p.getX(), p.getY() - 40);
        Point d1 = Utils.getNewPByDegree(p, xnd1, 360 - degree);
        Point xnd2 = new Point(newP.getX(), newP.getY() - 40);
        Point d2 = Utils.getNewPByDegree(p, xnd2, 360 - degree);
        Point xnLeftT = new Point(xnc.getX() - 40, newP.getY() - 20);
        Point leftT = Utils.getNewPByDegree(p, xnLeftT, 360 - degree);
        Point xnRightB = new Point(xnc.getX() + 40, newP.getY() + 20);
        Point rightB = Utils.getNewPByDegree(p, xnRightB, 360 - degree);
        canvas.save();
        canvas.rotate((float) degree, p.getX(), p.getY());
        canvas.drawText(length, xnc.getX(), xnc.getY(), mTextPaint);
        canvas.restore();
        int textWith = Utils.getTextWidth(mTextPaint, length);
        Point xnTextLeft = new Point(xnc.getX() - textWith / 2, xnc.getY());
        Point textLeft = Utils.getNewPByDegree(p, xnTextLeft, 360 - degree);
        Point xnTextRight = new Point(xnc.getX() + textWith / 2, xnc.getY());
        Point textRight = Utils.getNewPByDegree(p, xnTextRight, 360 - degree);
        canvas.drawLine(p.getX(), p.getY(), d1.getX(), d1.getY(), mBZPaint);
        canvas.drawLine(p1.getX(), p1.getY(), d2.getX(), d2.getY(), mBZPaint);
        canvas.drawLine(d1.getX(), d1.getY(), textLeft.getX(), textLeft.getY(), mBZPaint);
        canvas.drawLine(d2.getX(), d2.getY(), textRight.getX(), textRight.getY(), mBZPaint);
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
            if (Utils.segIntersect(points.get(0), points.get(points.size() - 1), points.get(points.size() - 1),
                    lastSecond) == 0
                    || Utils.pointToLine((int) points.get(0).getX(), (int) points.get(0).getY(), points.get(points
                    .size() - 1), lastSecond) < 20) {
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

    /**
     * 复原功能
     */
    public void recover() {
        if (points.size() > 0) {
            points.remove(points.size() - 1);
            if (points.size() > 0) {
                cx = points.get(points.size() - 1).getX();
                cy = points.get(points.size() - 1).getY();
                x = cx;
                y = cy;
            } else {
                isFirst = true;
            }
        } else {
            isFirst = true;
        }
        postInvalidate();
    }


    /**
     * 添加正方形
     */
    public void addSquare(boolean isDrawSquare,boolean isFirstDrawSquare,boolean isDrawPolygonalLine) {
        this.isDrawSquare = isDrawSquare;
        this.isFirstDrawSquare = isFirstDrawSquare;
        this.isDrawPolygonalLine = isDrawPolygonalLine;
        invalidate();
    }

    /**
     * 添加不规则图形
     */
    public void addpolygonalLine(boolean isDrawPolygonalLine,boolean isDrawSquare) {
        this.isDrawPolygonalLine = isDrawPolygonalLine;
        this.isDrawSquare = isDrawSquare;
        invalidate();
    }




}
