package com.example.hasee.drawtest.weidget.kedutest;

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


public class ReferenceView extends View {
    Path path, exPath;
    //多边形的画笔
    private Paint polyPaint;
    //延长线的画笔
    private Paint extendPaint;
    private Paint textPaint;
    private Paint referencePain;
    private int mColor = 0xFF000000;
    private int eColor = 0xFFFF0000;
    private int tColor = 0xFFFF0000;
    float curX, curY, downX, downY;
    private Point downPoint;
    private boolean showParallelLine, showLength, showCutOffLine;//是否显示截止线，平行参考线，长度
    private boolean flag = true;//默认多边形移动的一边相对原点是扩大的
    private List<Point> pointList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private List<Line> noSlopeLineList = new ArrayList<>();
    private List<Line> slope_0_LineList = new ArrayList<>();
    private List<Line> hasSlope_lineList = new ArrayList<>();
    private List<Line> referLines = new ArrayList<>();//存储转变坐标后的实际直线
    private float centerX, centerY;

    public ReferenceView(Context context) {
        super(context);
        initPaint();
    }

    public ReferenceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ReferenceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(pointList);
        canvas.drawPath(path, polyPaint);
        /*
        /*测试*/
        /*水平线画法*/
/*        canvas.save();
        canvas.drawLine(200,400,400,400,polyPaint);
        canvas.drawLine(200,400,200,350,textPaint);//截止线
        canvas.drawLine(400,400,400,350,textPaint);//截止线
        canvas.drawText("200",300,380,textPaint);
        canvas.save();
//        垂线的画法
        canvas.rotate(90,400,400);//以上一段直线的末端点为中心旋转
        canvas.drawLine(400,400,800,400,polyPaint);
        canvas.drawLine(400,400,400,350,textPaint);//截止线
        canvas.drawLine(800,400,800,350,textPaint);//截止线
        canvas.drawText("400",600,380,textPaint);
        canvas.save();
//        斜线的画法
        canvas.rotate(-45,800,400);//旋转正角度顺时针，旋转负角度逆时针
        canvas.drawLine(800,400,1000,400,polyPaint);
        canvas.drawLine(800,400,800,350,textPaint);
        canvas.drawLine(1000,400,1000,350,textPaint);
        canvas.drawText("200",900,380,textPaint);
        canvas.save();
//        斜线的画法
        canvas.rotate(90,1000,400);
        canvas.drawLine(1000,400,1200,400,polyPaint);
        canvas.drawLine(1000,400,1000,350,textPaint);
        canvas.drawLine(1200,400,1200,350,textPaint);
        canvas.drawText("200",1100,380,textPaint);
        canvas.save();*/


        /*测试根据斜率获取角度*/
       /* double v = Math.toDegrees(Math.atan(-2));
        Log.e("ReferenceView", "onDraw:角度是 "+v);*/


        if (showLength) {
//            drawLengthText(lineList,canvas,textPaint);
            drawReference(lineList, canvas);
            Log.e("ReferenceView", "onDraw: 开始画标注了");
        }
        if (showCutOffLine) {

        }
        if (showParallelLine) {

        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取屏幕中心点
        centerX = w / 2;
        centerY = h / 2;
        Log.e("ReferenceView", "onSizeChanged:屏幕中心点坐标" + centerX + ",  " + centerY);
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
        //        polyPaint.setFilterBitmap(true);
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
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(tColor);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.SERIF);
      /*  textPaint.setTextSkewX(-0.5f);
        textPaint.setTextScaleX(0.7F);*/

        //        多边形各个端点point坐标
        //
        Point point1 = new Point(80, 50);
        Point point2 = new Point(100, 50);
        Point point3 = new Point(110, 160);
        Point point4 = new Point(220, 180);
        Point point5 = new Point(250, 300);
        Point point6 = new Point(100, 300);
//        Point point1 = new Point(50, 50);
//        Point point2 = new Point(100, 50);
//        Point point3 = new Point(100, 100);
//        Point point4 = new Point(200, 100);
//        Point point5 = new Point(200, 300);
//        Point point6 = new Point(50, 300);

        pointList = new ArrayList<>();
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        pointList.add(point5);
        pointList.add(point6);
        //        将源多边形的list数据拷贝到新list
  /*      dest1 = new ArrayList();
        Collections.addAll(dest1, new android.graphics.Point[pointList.size()]);
        Collections.copy(dest1, pointList);
        Log.e("ReferenceView", "copy后的集合是"+dest1.toString());*/

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
                downX = curX;
                downY = curY;
                downPoint = new Point(downX, downY);
                int result = PtInRegion(downPoint, pointList);
                /*在多边形内不，显示个边的标注*/
                if (result != -1) {
                    Toast.makeText(getContext(), "点击在多边形内", Toast.LENGTH_SHORT).show();
                    showReference(pointList);
                    showLength = true;
                }
                break;
            case MotionEvent.ACTION_UP:
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

    /*显示标注的方法*/
    public void showReference(List<Point> list) {
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

            Log.e("ReferenceView", i + ":" + lineList.get(i).toString());
        }
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
        /*TODO：此处的degree获取应该判断直线的方向。例如同样两条多边形的两条平行边，斜率相等，却因为绘制的顺序不同，产生的与x轴的夹角不同*/

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
            double degree = Math.toDegrees(Math.atan(slope));
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
                    drawLine(0, 90, length, p1, p2, textPaint, canvas);
                }
                if (slope == 0.0) {//水平线段
                    drawLine(0, 0, length, p1, p2, textPaint, canvas);
                } else {//正常的斜率，包含正负
                    drawLine(0, degree, length, p1, p2, textPaint, canvas);
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
                drawLine(i, degree, length, p1, p2, textPaint, canvas);
            }

        }
    }

    /*画每条边的通用方法 参数i表示lineList中line的下标*/
    public void drawLine(int i, double degree, String length, Point p1, Point p2, Paint textPaint, Canvas canvas) {
        float stopLength = 40;//截止线距离边的出自己距离
        float textHeight = 10;//长度text距离边的高度
        double mLength = Double.valueOf(length);//边长
        float x1, y1, x2, y2;
        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        /*坐标转换*/

        if (i == 0) {//第一条直线
            if (y1 == y2) {//水平线段
                x1 = p1.getX();
                y1 = p1.getY();
                x2 = p2.getX();
                y2 = p2.getY();
            } else {//因为是第一条直线，所以垂直线段和斜线两种情况相等
                x1 = p1.getX();
                y1 = p1.getY();
                x2 = (float) (x1 + mLength);
                y2 = y1;
            }
            /*存储第一条转变后坐标的直线的两端点*/
            referLines.add(new Line(new Point(x1, y1), new Point(x2, y2)));
        } else {//不是第一条直线，那么这条直线的开始端点要参考上一条直线的结束端点,先要判断上一条直线的原始状态

            x1 = referLines.get(i - 1).getP2().getX();
            y1 = referLines.get(i - 1).getP2().getY();
            x2 = (float) (x1 + mLength);
            y2 = y1;
            referLines.add(new Line(new Point(x1, y1), new Point(x2, y2)));
        }

        if (i == 0) {
            canvas.rotate((float) -degree, x1, y1);
        } else {
            canvas.rotate((float) degree, x1, y1);
        }
//        canvas.drawLine(x1, y1, x2, y2, paintLine);//画多边形的边
        canvas.drawLine(x1, y1, x1, y1 - stopLength, textPaint);//画截止线
        canvas.drawLine(x2, y2, x2, y2 - stopLength, textPaint);//画截至线
        canvas.drawText(length, (x1 + x2) / 2, y1 - textHeight, textPaint);
        canvas.save();
    }

    /*一个点到坐标轴原点的距离*/
    public float disToZreo(Point point) {
        float x = point.getX();
        float y = point.getY();
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

}
