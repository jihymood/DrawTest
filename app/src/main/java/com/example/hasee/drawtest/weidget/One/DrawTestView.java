package com.example.hasee.drawtest.weidget.One;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Created by HASEE on 2017/7/18 12:27
 *
 * 黄继海部分功能
 */

public class DrawTestView extends View {

    private List<Point> startPointList;  //原始坐标集合
    private List<Point> curPtList;  //移动后坐标集合

    private Paint paint;  //画笔
    private Canvas cacheCanvas;  //画布
    private Bitmap cachebBitmap;
    private Path path;
    private Context context;


    public DrawTestView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DrawTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

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

        path = new Path();
        path.moveTo(curPtList.get(0).getX(), curPtList.get(0).getY());
        for (int i = 1; i < curPtList.size(); i++) {
            Point point = curPtList.get(i);
            path.lineTo(point.getX(), point.getY());
        }
        path.close();
        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);


    }

    private float cur_x, cur_y;
    private boolean isMoving;

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

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.e("DrawTestView", "MOVE");

                if (!isMoving)
                    break;  //return

//                path.reset();
//                path.moveTo(x, y);
                float dx = x - cur_x;
                float dy = y - cur_y;
                Log.e("DrawTestView", "UP" + dx + "/" + dy);

                for (int i = 0; i < curPtList.size(); i++) {
                    curPtList.get(i).setX(startPointList.get(i).getX() + dx);
                    curPtList.get(i).setY(startPointList.get(i).getY() + dy);
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                if (!isMoving)
                    break;  //return
//                path.reset();
//                path.moveTo(x, y);
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
                break;
            }
        }

        // 通知刷新界面
        invalidate();

        return true;
    }


}
