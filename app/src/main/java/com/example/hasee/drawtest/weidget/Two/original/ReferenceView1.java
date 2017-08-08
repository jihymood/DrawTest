package com.example.hasee.drawtest.weidget.Two.original;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.hasee.drawtest.model.Line;
import com.example.hasee.drawtest.model.Point;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReferenceView1 extends View {
    Path path, exPath;
    //多边形的画笔
    private Paint polyPaint;
    //延长线的画笔
    private Paint extendPaint;
    private Paint textPaint;
    private int count;
    private int mColor = 0xFF000000;
    private int eColor = 0xFFFF0000;
    private int tColor = 0xFFFF0000;
    float curX, curY, downX, downY;
    private Point downPoint;
    private boolean intoGetReference;
    private boolean showReference;//是否显示参考线信息
    private List<Point> pointList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private List<Line> noSlopeLineList = new ArrayList<>();
    private List<Line> slope_0_LineList = new ArrayList<>();
    private List<Line> hasSlope_lineList = new ArrayList<>();
    private List<Point> rotateCenter = new ArrayList<>(pointList.size() + 1);//多边形旋转中心
    private List<Line> referLines = new ArrayList<>(pointList.size());//多边形旋转中心

    private float centerX, centerY;

    public ReferenceView1(Context context) {
        super(context);
        initPaint();
    }

    public ReferenceView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ReferenceView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取屏幕中心点
        centerX = w / 2;
        centerY = h / 2;
        Log.i("ss", "onSizeChanged:屏幕中心点坐标" + centerX + ",  " + centerY);
    }

    private void initPaint() {

        path = new Path();
        exPath = new Path();
        //        多边形画笔
        polyPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        polyPaint.setAntiAlias(true);
        polyPaint.setStyle(Paint.Style.STROKE);
        polyPaint.setColor(mColor);
        polyPaint.setStrokeWidth(3);
        //        延长线画笔
        extendPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        extendPaint.setStyle(Paint.Style.STROKE);
        extendPaint.setColor(eColor);
        extendPaint.setStrokeWidth(3);
        /*设置虚线*/
        DashPathEffect effect = new DashPathEffect(new float[]{10f, 5f}, 0);
        extendPaint.setPathEffect(effect);
//        绘制距离的画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(tColor);
        textPaint.setStrokeWidth(0.5f);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.SERIF);
//        textPaint.setTextSkewX(-0.5f);
        textPaint.setTextScaleX(0.7F);

        //        多边形各个端点point坐标
        //
        Point point1 = new Point(60, 0);
        Point point2 = new Point(500, 350);
        Point point3 = new Point(700, 400);
        Point point4 = new Point(100, 600);
        Point point5 = new Point(300, 550);
        Point point6 = new Point(200, 200);

        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        pointList.add(point5);
        pointList.add(point6);
        getRotateCenter(pointList);
        Log.d("ss", "getRotateCenter:" + rotateCenter.toString());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(pointList);
        canvas.drawPath(path, polyPaint);
        if (showReference) {
            getRefereceInfo(pointList);
            drawReference(lineList, canvas);
            Log.d("ss", "onDraw: 开始画标注了");
        }
    }

    private void drawPolygon(List<Point> list) {
        path.moveTo(list.get(0).getX(), list.get(0).getY());
        for (int i = 1; i < list.size(); i++) {
            path.lineTo(list.get(i).getX(), list.get(i).getY());
        }
        path.close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        curX = event.getX();
        curY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                showReference = false;
                downX = curX;
                downY = curY;
                downPoint = new Point(downX, downY);
                /*在多边形内部，显示各边的标注*/
                break;
            case MotionEvent.ACTION_UP:
                int result = PtInRegion(downPoint, pointList);
//                showReference=false;
                if (result != -1) {
                    Toast.makeText(getContext(), "点击在多边形内", Toast.LENGTH_SHORT).show();
                    showReference = true;
                    count = count + 1;
                    Log.d("ss", "onTouchEvent:count= " + count);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
        invalidate();// 通知刷新界面
        return true;
    }

    public int PtInRegion(Point p0, List<Point> plg) {
        int nowValue, lastValue, dValue, sumValue = 0;
        double tempMult;
        double eps = 0.0000000000000001;
        Point l1, l2;

        if (plg.get(0).getY() > p0.getY()) {
            lastValue = 1;
        } else if (plg.get(0).getY() < p0.getY()) {
            lastValue = -1;
        } else {
            lastValue = 0;
        }

        for (int i = 1; i < plg.size(); i++) {
            //计算矢量积
            l1 = plg.get(i - 1);
            l2 = plg.get(i);
            tempMult = xmult(l1, l2, p0);

            //判断点是否在边上
            if (Math.abs(tempMult) < eps && (l1.getX() - p0.getX()) * (l2.getX() - p0.getX()) < eps && (l1.getY() -
                    p0.getY()) * (l2.getY() - p0.getY()) < eps) {
                return 0;
            }

            //判断是否跨射线
            if (l2.getY() > p0.getY()) {
                nowValue = 1;
            } else if (l2.getY() < p0.getY()) {
                nowValue = -1;
            } else {
                nowValue = 0;
            }

            dValue = nowValue - lastValue;

            //判断是否与射线相交,相交则累加
            if (dValue != 0) {
                if (dValue > 0 && tempMult > 0) {
                    sumValue += dValue;
                } else if (dValue < 0 && tempMult < 0) {
                    sumValue += dValue;
                }
            }

            lastValue = nowValue;
        }

        //判断最终结果，如和为0，则在多边形外，否则在多边形内
        if (sumValue == 0) {
            return -1;
        } else {
            return 1;
        }
    }


    public static double xmult(Point p1, Point p2, Point p0) {
        return (p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2.getX() - p0.getX()) * (p1.getY() - p0.getY());
    }

    public void drawReferenceALL(Canvas canvas, Paint paint, String text, float angle, Point anchor) {

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
