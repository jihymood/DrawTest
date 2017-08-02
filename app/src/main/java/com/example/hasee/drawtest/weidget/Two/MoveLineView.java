package com.example.hasee.drawtest.weidget.Two;

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

import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.utils.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HASEE on 2017/7/27 16:04
 */

public class MoveLineView extends View {

    private Path path;
    private Paint paint;
    private List<Point> list;
    private boolean isDraw = true;
    private int downPosition;
    private float startX, startY, lastX, lastY;
    private Point duan1, duan2;

    public MoveLineView(Context context) {
        super(context);
        init();
    }

    public MoveLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paint = new Paint();
        path = new Path();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        list = new ArrayList<>();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        if (list != null && list.size() > 0) {
            path.moveTo(list.get(0).getX(), list.get(0).getY());
            for (int i = 1; i < list.size(); i++) {
                path.lineTo(list.get(i).getX(), list.get(i).getY());

            }
        }
        path.close();
        canvas.drawPath(path, paint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isDraw) {
                    float x = event.getX();
                    float y = event.getY();
                    list.add(new Point(x, y));
                } else {
                    startX = event.getX();
                    startY = event.getY();
                    downPosition = ensurePoint(startX, startY);
                }


                break;
            case MotionEvent.ACTION_MOVE:
                lastX = event.getX();
                lastY = event.getY();
                if (isDraw) {

                } else {
                    moveLine(lastX, lastY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (list.size() == 4) {
                    isDraw = false;
                }

                break;

        }

        invalidate();
        return true;


    }

    public void moveLine(float lastX, float lastY) {
        List<Point> newList = new ArrayList<>();

        int position = downPosition;
        if (list.size() == 3) {  //三角形

            if (position == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = list.get(0);
                duan2 = list.get(list.size() - 1);

                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(1));
                newList.add(list.get(1));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(list.size() - 1, pointList.get(1));

            } else if (position == -2) {  //选中整体图形){
//                moveView(dx, dy);
            } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
                return;
            } else if (position == 0) {
                duan1 = list.get(position);
                duan2 = list.get(position + 1);

                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(2));
                newList.add(list.get(2));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(position + 1, pointList.get(1));
            } else if (position == 1) {
                duan1 = list.get(position);
                duan2 = list.get(position + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(0));
                newList.add(list.get(0));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(position + 1, pointList.get(1));
            }


        } else if (list.size() > 3) {   //表示多边形
            //通过ensurePoint获得position,判断position
            if (position == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = list.get(0);
                duan2 = list.get(list.size() - 1);

                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(list.size() - 2));
                newList.add(list.get(1));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(list.size() - 1, pointList.get(1));

            } else if (position == -2) {  //选中整体图形
//            moveView(dx, dy);
                return;
            } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
                return;
            } else if (position == 0) {  //index=-1旁边的直线
                duan1 = list.get(position);
                duan2 = list.get(position + 1);

                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(position + 2));
                newList.add(list.get(list.size() - 1));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(position + 1, pointList.get(1));

            } else if (position == list.size() - 2) { //index=-1旁边的直线

                duan1 = list.get(position);
                duan2 = list.get(position + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(0));
                newList.add(list.get(position - 1));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(position + 1, pointList.get(1));

            } else {
                duan1 = list.get(position);
                duan2 = list.get(position + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(list.get(position + 2));
                newList.add(list.get(position - 1));

                List<Point> pointList = getPoint(newList, new Point(lastX, lastY));
                list.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                list.set(position + 1, pointList.get(1));
            }

        }

        invalidate();
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
        float moveLineK = DrawUtils.calSlope(list.get(0), list.get(1)); //移动线的斜率
        float crossLineK = DrawUtils.calSlope(list.get(1), list.get(2)); //与移动线相交的其他两条线的斜率
        float crossLineK1 = DrawUtils.calSlope(list.get(0), list.get(3));  //与移动线相交的其他两条线的斜率

        Log.e("MoveLineView", "moveLineK:" + moveLineK + "/" + "crossLineK:" + crossLineK + "/" +
                "crossLineK1:" + crossLineK1);

        if (Float.isInfinite(moveLineK) || Float.isNaN(moveLineK)) {  //无穷大
//        if (Float.isNaN(moveLineK)) {  //无穷大
            float crosspointX = movePoint.getX();
            float crosspointY = list.get(0).getY();
            float crosspointY1 = list.get(1).getY();

            moveinwardPoint.add(new Point(crosspointX, crosspointY));
            moveinwardPoint.add(new Point(crosspointX, crosspointY1));

        } else {
            float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
            float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);

            float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
            float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);

            Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);

            moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
            moveinwardPoint.add(new Point(crosspointX, crosspointY));
        }


        return moveinwardPoint;

    }


    private int ensurePoint(float startX, float startY) {
        //形成的多边形要首尾相接
        int position = -1;
        double minL = DrawUtils.pointToLine(startX, startY, list.get(list.size() - 1), list.get(0));
        for (int i = 0; i < list.size() - 1; i++) {
            double l1 = DrawUtils.pointToLine(startX, startY, list.get(i), list.get(i + 1));
            if (minL < l1) {
            } else if (minL > l1) {
                position = i;
                minL = l1;
            }
        }
        if (minL <= 40) {
            return position;//根据position获取要移动线的 端点
        } else if (DrawUtils.PtInRegion(new Point(startX, startY), list) == 1 && minL > 40) {
            return -2;//表示点击点在多边形内,执行移动view
        } else {
            return -3;//表示点击点在多边形外,不执行任何操作
        }
    }


}
