package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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

public class MagicPlanEditorView extends View {
    private Context context;
    private List<Point> points = new ArrayList<>();//编辑的点集合
    private List<PoPoListModel> showPolygons = new ArrayList<>();//得到显示的多边型集合
    private Paint mPaint = new Paint();
    private Paint textPaint = new Paint();//text画笔
    private Paint cirPaint = new Paint();
    private Path path = new Path();//路径
    private float startX, startY;
    private float lastX, lastY;
    private int downPosition;//落点位置判断  移动线和图
    private Point duan1;
    private Point duan2;
    private Point p1;
    private Point p2;
    private Canvas mCanvas;
    private int mode;//表示屏幕上手指的个数模式
    private float preDistance;
    private float curDistance;
    private float mScale = 1f;
    private float curScale = 1f;
    private int midX;
    private int midY;
    private IShowChangeLongViewListener listener;
    private Double longNum;
    private Point moveP1 = new Point();
    private Point moveP2 = new Point();

    public Double getLongNum() {
        return longNum;
    }

    public void setLongNum(Double longNum) {
        this.longNum = longNum;
        double ll = Utils.lineSpace(duan1.getX(), duan1.getY(), duan2.getX(), duan2.getY());
        double l = (longNum * 50 - ll) / 2;
        float k = Utils.calSlope(duan1, duan2);
        if (!duan1.isLock() && !duan2.isLock() && !p1.isLock() && !p2.isLock()) {
            if (Float.isInfinite(k) || Float.isNaN(k) || duan1.getX() == duan2.getX()) {//垂直
                if (duan1.getY() < duan2.getY()) {
                    moveP1.setX(duan1.getX());
                    moveP1.setY((float) (duan1.getY() - l));
                    moveP2.setX(duan2.getX());
                    moveP2.setY((float) (duan2.getY() + l));
                } else if (duan1.getY() > duan2.getY()) {
                    moveP1.setX(duan1.getX());
                    moveP1.setY((float) (duan1.getY() + l));
                    moveP2.setX(duan2.getX());
                    moveP2.setY((float) (duan2.getY() - l));
                }
            } else if (duan1.getY() - duan2.getY() > -0.01 && duan1.getY() - duan2.getY() < 0.01) {//水平
                if (duan1.getX() < duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() - l));
                    moveP1.setY(duan1.getY());
                    moveP2.setX((float) (duan2.getX() + l));
                    moveP2.setY(duan2.getY());
                } else if (duan1.getX() > duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() + l));
                    moveP1.setY(duan1.getY());
                    moveP2.setX((float) (duan2.getX() - l));
                    moveP2.setY(duan2.getY());
                }
            } else {
                double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, l);
                double dx = dxAdy[0];
                double dy = dxAdy[1];
                if (duan1.getX() < duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() - dx));
                    moveP1.setY((float) (duan1.getY() - dy));
                    moveP2.setX((float) (duan2.getX() + dx));
                    moveP2.setY((float) (duan2.getY() + dy));
                } else if (duan1.getX() > duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() + dx));
                    moveP1.setY((float) (duan1.getY() + dy));
                    moveP2.setX((float) (duan2.getX() - dx));
                    moveP2.setY((float) (duan2.getY() - dy));
                }
            }
            int position;
            if (downPosition == -1) {
                position = points.size() - 2;
            } else {
                position = downPosition - 1;
            }
            moveLine(p1, duan1, moveP1, position, 0, 0);
            duan1.setX(moveP1.getX());
            duan1.setY(moveP1.getY());
            if (position == -1) {
                Point p = points.get(points.size() - 1);
                p1.setX(p.getX());
                p1.setY(p.getY());
            } else {
                Point p = points.get(position);
                p1.setX(p.getX());
                p1.setY(p.getY());
            }

            if (downPosition == points.size() - 2) {
                position = -1;
            } else {
                position = downPosition + 1;
            }
            moveLine(duan2, p2, moveP2, position, 0, 0);
            duan2.setX(moveP2.getX());
            duan2.setY(moveP2.getY());
            if (position == -1) {
                Point p = points.get(0);
                p2.setX(p.getX());
                p2.setY(p.getY());
            } else {
                Point p = points.get(position + 1);
                p2.setX(p.getX());
                p2.setY(p.getY());
            }
            duan1.setLock(true);
            duan2.setLock(true);
        } else if (duan1.isLock() && !duan2.isLock() && !p2.isLock()) {
            if (Float.isInfinite(k) || Float.isNaN(k) || duan1.getX() == duan2.getX()) {//垂直
                if (duan1.getY() < duan2.getY()) {
                    moveP2.setX(duan2.getX());
                    moveP2.setY((float) (duan2.getY() - 2 * l));
                } else if (duan1.getY() > duan2.getY()) {
                    moveP2.setX(duan2.getX());
                    moveP2.setY((float) (duan2.getY() + 2 * l));
                }
            } else if (duan1.getY() - duan2.getY() > -0.01 && duan1.getY() - duan2.getY() < 0.01) {//水平
                if (duan1.getX() < duan2.getX()) {
                    moveP2.setX((float) (duan2.getX() + 2 * l));
                    moveP2.setY(duan2.getY());
                } else if (duan1.getX() > duan2.getX()) {
                    moveP2.setX((float) (duan2.getX() - 2 * l));
                    moveP2.setY(duan2.getY());
                }
            } else {
                double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, 2 * l);
                double dx = dxAdy[0];
                double dy = dxAdy[1];
                if (duan1.getX() < duan2.getX()) {
                    moveP2.setX((float) (duan2.getX() + dx));
                    moveP2.setY((float) (duan2.getY() + dy));
                } else if (duan1.getX() > duan2.getX()) {
                    moveP2.setX((float) (duan2.getX() - dx));
                    moveP2.setY((float) (duan2.getY() - dy));
                }
            }
            int position;
            if (downPosition == points.size() - 2) {
                position = -1;
            } else {
                position = downPosition + 1;
            }
            moveLine(duan2, p2, moveP2, position, 0, 0);
            duan2.setX(moveP2.getX());
            duan2.setY(moveP2.getY());
            if (position == -1) {
                Point p = points.get(0);
                p2.setX(p.getX());
                p2.setY(p.getY());
            } else {
                Point p = points.get(position + 1);
                p2.setX(p.getX());
                p2.setY(p.getY());
            }
            duan2.setLock(true);
        } else if (!duan1.isLock() && duan2.isLock() && !p1.isLock()) {
            if (Float.isInfinite(k) || Float.isNaN(k) || duan1.getX() == duan2.getX()) {//垂直
                if (duan1.getY() < duan2.getY()) {
                    moveP1.setX(duan1.getX());
                    moveP1.setY((float) (duan1.getY() - 2 * l));
                } else if (duan1.getY() > duan2.getY()) {
                    moveP1.setX(duan1.getX());
                    moveP1.setY((float) (duan1.getY() + 2 * l));
                }
            } else if (duan1.getY() - duan2.getY() > -0.01 && duan1.getY() - duan2.getY() < 0.01) {//水平
                if (duan1.getX() < duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() - 2 * l));
                    moveP1.setY(duan1.getY());
                } else if (duan1.getX() > duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() + 2 * l));
                    moveP1.setY(duan1.getY());
                }
            } else {
                double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, 2 * l);
                double dx = dxAdy[0];
                double dy = dxAdy[1];
                if (duan1.getX() < duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() - dx));
                    moveP1.setY((float) (duan1.getY() - dy));
                } else if (duan1.getX() > duan2.getX()) {
                    moveP1.setX((float) (duan1.getX() + dx));
                    moveP1.setY((float) (duan1.getY() + dy));
                }
            }
            int position;
            if (downPosition == -1) {
                position = points.size() - 2;
            } else {
                position = downPosition - 1;
            }
            moveLine(p1, duan1, moveP1, position, 0, 0);
            duan1.setX(moveP1.getX());
            duan1.setY(moveP1.getY());
            if (position == -1) {
                Point p = points.get(points.size() - 1);
                p1.setX(p.getX());
                p1.setY(p.getY());
            } else {
                Point p = points.get(position);
                p1.setX(p.getX());
                p1.setY(p.getY());
            }
            duan1.setLock(true);
        } else if (duan1.isLock() && duan2.isLock()) {
            if (Float.isInfinite(k) || Float.isNaN(k) || duan1.getX() == duan2.getX()) {//垂直
                if (duan1.getY() < duan2.getY()) {
                    duan1.setX(duan1.getX());
                    duan1.setY((float) (duan1.getY() - l));
                    duan2.setX(duan2.getX());
                    duan2.setY((float) (duan2.getY() + l));
                } else if (duan1.getY() > duan2.getY()) {
                    duan1.setX(duan1.getX());
                    duan1.setY((float) (duan1.getY() + l));
                    duan2.setX(duan2.getX());
                    duan2.setY((float) (duan2.getY() - l));
                }
            } else if (duan1.getY() - duan2.getY() > -0.01 && duan1.getY() - duan2.getY() < 0.01) {//水平
                if (duan1.getX() < duan2.getX()) {
                    duan1.setX((float) (duan1.getX() - l));
                    duan1.setY(duan1.getY());
                    duan2.setX((float) (duan2.getX() + l));
                    duan2.setY(duan2.getY());
                } else if (duan1.getX() > duan2.getX()) {
                    duan1.setX((float) (duan1.getX() + l));
                    duan1.setY(duan1.getY());
                    duan2.setX((float) (duan2.getX() - l));
                    duan2.setY(duan2.getY());
                }
            } else {
                double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, l);
                double dx = dxAdy[0];
                double dy = dxAdy[1];
                if (duan1.getX() < duan2.getX()) {
                    duan1.setX((float) (duan1.getX() - dx));
                    duan1.setY((float) (duan1.getY() - dy));
                    duan2.setX((float) (duan2.getX() + dx));
                    duan2.setY((float) (duan2.getY() + dy));
                } else if (duan1.getX() > duan2.getX()) {
                    duan1.setX((float) (duan1.getX() + dx));
                    duan1.setY((float) (duan1.getY() + dy));
                    duan2.setX((float) (duan2.getX() - dx));
                    duan2.setY((float) (duan2.getY() - dy));
                }
            }
        } else if (duan1.isLock() && !duan2.isLock() && p2.isLock())

        {
            double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, 2 * l);
            double dx = dxAdy[0];
            double dy = dxAdy[1];
            if (duan1.getX() < duan2.getX()) {
                duan2.setX((float) (duan2.getX() + dx));
                duan2.setY((float) (duan2.getY() + dy));
            } else if (duan1.getX() > duan2.getX()) {
                duan2.setX((float) (duan2.getX() - dx));
                duan2.setY((float) (duan2.getY() - dy));
            }
            duan2.setLock(true);
        } else if (duan2.isLock() && !duan1.isLock() && p1.isLock())

        {
            double[] dxAdy = DrawUtils.getDxAndDy(duan1, duan2, l);
            double dx = dxAdy[0];
            double dy = dxAdy[1];
            if (duan1.getX() < duan2.getX()) {
                duan1.setX((float) (duan1.getX() - dx));
                duan1.setY((float) (duan1.getY() - dy));
            } else if (duan1.getX() > duan2.getX()) {
                duan1.setX((float) (duan1.getX() + dx));
                duan1.setY((float) (duan1.getY() + dy));
            }
            duan1.setLock(true);
        }

        //更新绘制
        invalidate();
    }

    public IShowChangeLongViewListener getListener() {
        return listener;
    }

    public void setListener(IShowChangeLongViewListener listener) {
        this.listener = listener;
    }

    public float getmScale() {
        return mScale;
    }

    public void setmScale(float mScale) {
        this.mScale = mScale;
        curScale = mScale;
    }


    public List<PoPoListModel> getPoints() {
        return showPolygons;
    }

    public void setPoints(List<PoPoListModel> showPolygons) {
        this.showPolygons = showPolygons;
        if (showPolygons.size() > 0) {
            points = showPolygons.get(showPolygons.size() - 1).getList();
        }
        invalidate();
    }

    public MagicPlanEditorView(Context context) {
        super(context);
        init(context);
    }

    public MagicPlanEditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MagicPlanEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(40);
        cirPaint.setAntiAlias(true);
        cirPaint.setColor(Color.GREEN);
        cirPaint.setStrokeWidth(5);
        cirPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        midX = w / 2;
        midY = h / 2;
    }

    private boolean isDrawDuan = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = (e.getX() - midX) / curScale + midX;
                startY = (e.getY() - midY) / curScale + midY;
                if (points.size() >= 3) {
                    downPosition = ensurePoint(startX, startY);
                    if (downPosition == -2) {
                        listener.cancelView();
                        isDrawDuan = false;
                    } else if (downPosition == -3) {
                        listener.cancelView();
                        isDrawDuan = false;
                    } else if (downPosition == -1) {
                        duan1 = points.get(points.size() - 1);
                        duan2 = points.get(0);
                        p1 = points.get(points.size() - 2);
                        p2 = points.get(1);
                        if (Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) <= 90
                                && Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) >= 50) {
                            setView();
                            isDrawDuan = true;
                            return true;
                        } else {
                            listener.cancelView();
                            isDrawDuan = false;
                        }
                    } else if (downPosition == 0) {
                        duan1 = points.get(0);
                        duan2 = points.get(1);
                        p1 = points.get(points.size() - 1);
                        p2 = points.get(2);
                        if (Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) <= 90
                                && Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) >= 50) {
                            setView();
                            isDrawDuan = true;
                            return true;
                        } else {
                            listener.cancelView();
                            isDrawDuan = false;
                        }
                    } else if (downPosition == points.size() - 2) {
                        duan1 = points.get(downPosition);
                        duan2 = points.get(downPosition + 1);
                        p1 = points.get(downPosition - 1);
                        p2 = points.get(0);
                        if (Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) <= 90
                                && Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) >= 50) {
                            setView();
                            isDrawDuan = true;
                            return true;
                        } else {
                            listener.cancelView();
                            isDrawDuan = false;
                        }
                    } else {
                        duan1 = points.get(downPosition);
                        duan2 = points.get(downPosition + 1);
                        p1 = points.get(downPosition - 1);
                        p2 = points.get(downPosition + 2);
                        if (Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) <= 90
                                && Utils.lineSpace((int) startX, (int) startY, ((int) duan1.getX() + (int) duan2.getX()) / 2, ((int) duan1.getY() + (int) duan2.getY()) / 2) >= 50) {
                            setView();
                            isDrawDuan = true;
                            return true;
                        } else {
                            listener.cancelView();
                            isDrawDuan = false;
                        }
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
            case MotionEvent.ACTION_MOVE:
                //当两指缩放，计算缩放比例
                if (mode == 2) {
                    curDistance = DrawUtils.getDistance(e);
                    if (curDistance > 10f) {
                        curScale = mScale * (curDistance / preDistance);
                    }
                    break;
                } else if (mode == 1) {
                    lastX = (e.getX() - midX) / curScale + midX;
                    lastY = (e.getY() - midY) / curScale + midY;
                    moveLine(duan1, duan2, new Point(lastX, lastY), downPosition, lastX - startX, lastY - startY);
                    startX = lastX;
                    startY = lastY;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mScale = curScale;
                mode = 0;
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;
                break;
        }

        invalidate();
        return true;
    }


    private void setView() {
        double lineLong = Utils.lineSpace(duan1.getX(), duan1.getY(), duan2.getX(), duan2.getY());
        NumberFormat format = new DecimalFormat("0.00");
        listener.showView(format.format(lineLong / 50).toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        mCanvas.save();
        mCanvas.scale(curScale, curScale, midX, midY);
        if (showPolygons.size() > 1) {
            for (int i = 0; i < showPolygons.size() - 1; i++) {
                List<Point> getPoints = showPolygons.get(i).getList();
                if (getPoints.size() >= 2 && getPoints != null) {
                    path.reset();
                    path.moveTo(getPoints.get(0).getX(), getPoints.get(0).getY());
                    for (int j = 1; j < getPoints.size(); j++) {
                        path.lineTo(getPoints.get(j).getX(), getPoints.get(j).getY());
                    }
                    path.close();
                    mPaint.setColor(Color.GRAY);
                    mCanvas.drawPath(path, mPaint);
                }
            }
        }

        if (points.size() >= 2) {
            path.reset();
            path.moveTo(points.get(0).getX(), points.get(0).getY());
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).getX(), points.get(i).getY());
            }
            path.close();
            mPaint.setColor(Color.BLACK);
            mCanvas.drawPath(path, mPaint);
        }

//        cirPaint.setStrokeWidth(5 / curScale);
//        if (isDrawDuan) {
//            mCanvas.drawCircle(duan1.getX(), duan1.getY(), 10 / curScale, cirPaint);
//            mCanvas.drawCircle(duan2.getX(), duan2.getY(), 10 / curScale, cirPaint);
//        }
//        if (isDrawLine) {
//            mPaint.setColor(Color.RED);
//            mCanvas.drawLine(duan1.getX(), duan1.getY(), duan2.getX(), duan2.getY(), mPaint);
//        }
    }

    private List<Point> linePoints = new ArrayList<>();
    private boolean isDrawLine;

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
        invalidate();
    }

    private void drawLineDistance(Canvas canvas) {
        PoPoListModel polygon = new PoPoListModel(points);
        List<Line> lines = polygon.getLines();
        List<Point> cp = polygon.getLineCenterPoints();
        for (int i = 0; i < lines.size(); i++) {
            canvas.save();
            Point p = DrawUtils.getLineP(lines.get(i));
            canvas.rotate(90, cp.get(i).getX(), cp.get(i).getY());
            canvas.drawText("50", p.getX(), p.getY(), textPaint);
            canvas.restore();
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
        pp.addAll(points);
        pp.add(points.get(0));
        int position = -1;
        double minL = Utils.pointToLine((int) startX, (int) startY, points.get(points.size() - 1), points.get(0));
        for (int i = 0; i < points.size() - 1; i++) {
            double l1 = Utils.pointToLine((int) startX, (int) startY, points.get(i), points.get(i + 1));
            if (minL < l1) {
            } else if (minL > l1) {
                position = i;
                minL = l1;
            }
        }
        if (minL <= 20) {
            return position;//根据position获取要移动线的 端点
        } else if (Utils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > 20) {
            return -2;//表示点击点在多边形内,执行移动view
        } else if (Utils.PtInRegion(new Point(startX, startY), pp) == -1 && minL > 40 && minL < 90) {
            return position;//根据position获取要移动线的 端点
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
        for (Point p : points) {
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    private void moveCanvas(int dx, int dy) {
        for (PoPoListModel polygon : showPolygons) {
            for (Point p : polygon.getList()) {
                p.setX(p.getX() + dx);
                p.setY(p.getY() + dy);
            }
        }
        startX = lastX;
        startY = lastY;
    }

    public void moveLine(Point duan1, Point duan2, Point movePoint, int downPosition, float dx, float dy) {
        List<Point> newList = new ArrayList<>();
        if (points.size() == 3) {  //三角形
            if (downPosition == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = points.get(0);
                duan2 = points.get(points.size() - 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(1));
                newList.add(points.get(1));
                List<Point> pointList = getPoint(newList, movePoint);
                points.get(0).setX(pointList.get(0).getX());
                points.get(0).setY(pointList.get(0).getY());
                points.get(points.size() - 1).setX(pointList.get(1).getX());
                points.get(points.size() - 1).setY(pointList.get(1).getY());
//                points.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(points.size() - 1, pointList.get(1));

            } else if (downPosition == -2) {  //选中整体图形{
                moveView((int) dx, (int) dy);
            } else if (downPosition == -3) {  //表示点击点在多边形外,不执行任何操作
                moveCanvas((int) dx, (int) dy);
            } else if (downPosition == 0) {
                duan1 = points.get(downPosition);
                duan2 = points.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(2));
                newList.add(points.get(2));
                List<Point> pointList = getPoint(newList, movePoint);
                points.get(downPosition).setX(pointList.get(0).getX());
                points.get(downPosition).setY(pointList.get(0).getY());
                points.get(downPosition + 1).setX(pointList.get(1).getX());
                points.get(downPosition + 1).setY(pointList.get(1).getY());
//                points.set(downPosition, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(downPosition + 1, pointList.get(1));
            } else if (downPosition == 1) {
                duan1 = points.get(downPosition);
                duan2 = points.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(0));
                newList.add(points.get(0));
                List<Point> pointList = getPoint(newList, movePoint);
                points.get(downPosition).setX(pointList.get(0).getX());
                points.get(downPosition).setY(pointList.get(0).getY());
                points.get(downPosition + 1).setX(pointList.get(1).getX());
                points.get(downPosition + 1).setY(pointList.get(1).getY());
//                points.set(downPosition, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(downPosition + 1, pointList.get(1));
            }
        } else if (points.size() > 3) {   //表示多边形
            //通过ensurePoint获得downPosition,判断downPosition
            if (downPosition == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = points.get(0);
                duan2 = points.get(points.size() - 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(points.size() - 2));
                newList.add(points.get(1));

                List<Point> pointList = getPoint(newList, movePoint);
                pointList.get(0).setLock(true);
                pointList.get(1).setLock(true);
                points.get(0).setX(pointList.get(0).getX());
                points.get(0).setY(pointList.get(0).getY());
                points.get(points.size() - 1).setX(pointList.get(1).getX());
                points.get(points.size() - 1).setY(pointList.get(1).getY());
//                points.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(points.size() - 1, pointList.get(1));

            } else if (downPosition == -2) {  //选中整体图形
                moveView((int) dx, (int) dy);
            } else if (downPosition == -3) {  //表示点击点在多边形外,不执行任何操作
                moveCanvas((int) dx, (int) dy);
            } else if (downPosition == 0) {  //index=-1旁边的直线
                duan1 = points.get(downPosition);
                duan2 = points.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(downPosition + 2));
                newList.add(points.get(points.size() - 1));

                List<Point> pointList = getPoint(newList, movePoint);
                points.get(downPosition).setX(pointList.get(0).getX());
                points.get(downPosition).setY(pointList.get(0).getY());
                points.get(downPosition + 1).setX(pointList.get(1).getX());
                points.get(downPosition + 1).setY(pointList.get(1).getY());
//                points.set(downPosition, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(downPosition + 1, pointList.get(1));

            } else if (downPosition == points.size() - 2) { //index=-1旁边的直线

                duan1 = points.get(downPosition);
                duan2 = points.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(0));
                newList.add(points.get(downPosition - 1));

                List<Point> pointList = getPoint(newList, movePoint);
                points.get(downPosition).setX(pointList.get(0).getX());
                points.get(downPosition).setY(pointList.get(0).getY());
                points.get(downPosition + 1).setX(pointList.get(1).getX());
                points.get(downPosition + 1).setY(pointList.get(1).getY());
//                points.set(downPosition, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(downPosition + 1, pointList.get(1));

            } else {
                duan1 = points.get(downPosition);
                duan2 = points.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(points.get(downPosition + 2));
                newList.add(points.get(downPosition - 1));
                List<Point> pointList = getPoint(newList, movePoint);
                points.get(downPosition).setX(pointList.get(0).getX());
                points.get(downPosition).setY(pointList.get(0).getY());
                points.get(downPosition + 1).setX(pointList.get(1).getX());
                points.get(downPosition + 1).setY(pointList.get(1).getY());
//                points.set(downPosition, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
//                points.set(downPosition + 1, pointList.get(1));
            }

        }
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
        float moveLineK = Utils.calSlope(list.get(0), list.get(1)); //移动线的斜率
        float crossLineK = Utils.calSlope(list.get(1), list.get(2)); //与移动线相交的其他两条线的斜率
        float crossLineK1 = Utils.calSlope(list.get(0), list.get(3));  //与移动线相交的其他两条线的斜率

        if (Float.isInfinite(moveLineK) || Float.isNaN(moveLineK)) {  //竖线，水平方向移动  isNaN无穷大  isInfinite无意义的(分母为0)
            if (crossLineK == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = Utils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = list.get(1).getY();
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            } else if (crossLineK1 == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = list.get(0).getY();
                float crosspointY1 = Utils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
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
                float crosspointY = Utils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = Utils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            }
        } else if (moveLineK == 0) {  //选中的线是横线，垂直方向移动
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {

                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
                float crosspointY1 = list.get(0).getY() + dy;
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
            } else {
                float crosspointX1 = Utils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = Utils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
                float crosspointY = list.get(1).getY() + dy;
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
            } else {
                float crosspointX = Utils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = Utils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
            }

        } else {  //选中的边既不垂直也不水平
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {

                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
                float crosspointY1 = Utils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
            } else {
                float crosspointX1 = Utils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = Utils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
                float crosspointY = Utils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
            } else {
                float crosspointX = Utils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = Utils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
            }
        }

        return moveinwardPoint;

    }


    public interface IShowChangeLongViewListener {
        void showView(String lineLong);

        void cancelView();
    }
}
