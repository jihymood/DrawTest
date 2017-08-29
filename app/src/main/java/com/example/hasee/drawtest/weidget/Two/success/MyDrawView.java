package com.example.hasee.drawtest.weidget.Two.success;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.hasee.drawtest.R;
import com.example.hasee.drawtest.model.ImageGroupModel;
import com.example.hasee.drawtest.model.Line;
import com.example.hasee.drawtest.model.MyComparator;
import com.example.hasee.drawtest.model.PoPoListModel;
import com.example.hasee.drawtest.model.Point;
import com.example.hasee.drawtest.model.TwoPointDistance;
import com.example.hasee.drawtest.utils.DensityUtil;
import com.example.hasee.drawtest.utils.DrawUtils;
import com.example.hasee.drawtest.utils.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.hasee.drawtest.utils.DrawUtils.getCenterOfGravityPoint;

/**
 * Created by HASEE on 2017/7/27 16:04
 * 在MyDrawView2基础上修改
 * 移动线成功View + 边刻度功能
 */

public class MyDrawView extends BaseView {

    private Path path;
    private Paint paint, textPaint, writingPaint;
    private float startX, startY, lastX, lastY;
    private int downPosition;
    private List<List<Point>> twofoldList;  //所有图形的点的集合
    //    private List<PoPoListModel>
    private List<TwoPointDistance> distanceList;  //两个吸附点坐标和距离的对象集合
    private List<Point> intentPoints, startPointList;  //所选图形的集合、除所选图形外其他图形的集合
    private List<Point> singleList; //单个图形的点的集合
    private List<PoPoListModel> pointModelsList, startModelList; //点击点与图形之间联系的对象集合、除所选图形外其他图形的对象集合
    private float adsorbDis;  //吸附距离
    private float toSidebDis;  //点击点到边的距离
    private Point first, second; //吸附点1、吸附点2
    private int paintWidth = 10; //红色圆的半径

    private List<Line> lineList = new ArrayList<>();
    private List<Line> noSlopeLineList = new ArrayList<>();
    private List<Line> slope_0_LineList = new ArrayList<>();
    private List<Line> hasSlope_lineList = new ArrayList<>();
    private boolean intoGetReference;
    private List<Point> pointList = new ArrayList<>();
    private List<Point> rotateCenter = new ArrayList<Point>(pointList.size() + 1);//多边形旋转中心

    private int mode;
    private float preDistance;
    private float mScale = 1.0f;
    private float curScale = 1.0f;
    private int midX;
    private int midY;
    private float distance;
    private IShowChangeLongViewListener listener;
    private boolean isDrawDuan = false;
    private Double longNum;
    private Point duan1;
    private Point duan2;
    private Point p1;
    private Point p2;
    private Point moveP1 = new Point();
    private Point moveP2 = new Point();
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    /**
     * 不规则图形的重心
     */
    private Point gravityPoint;
    /**
     * 路由器
     */
    private Bitmap deleteIcon;
    private Paint mPaintForLineAndCircle;
    private int moveTag = 0, transformTag = 0, deleteTag = 0;
    private List<ImageGroup> mDecalImageGroupList = new ArrayList<>();
    private ImageGroupModel imageGroupModel;

    // 统计500ms内的点击次数
    TouchEventCountThread mInTouchEventCount = new TouchEventCountThread();
    // 根据TouchEventCountThread统计到的点击次数, perform单击还是双击事件
    TouchEventHandler mTouchEventHandler = new TouchEventHandler();


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

    public interface IShowChangeLongViewListener {
        void showView(String lineLong);

        void cancelView();
    }

    public MyDrawView(Context context) {
        super(context);
        init(context);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        //绘制距离的画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.GRAY);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.SERIF);

        //添加文字画笔
        writingPaint = new Paint();
        writingPaint.setColor(Color.GRAY);
        writingPaint.setStrokeWidth(15);
        writingPaint.setStyle(Paint.Style.FILL);
        writingPaint.setAntiAlias(true);
        writingPaint.setTextSize(40);
        writingPaint.setFakeBoldText(true);//true为粗体，false为非粗体

        singleList = new ArrayList<>();
        twofoldList = new ArrayList<>();
        distanceList = new ArrayList<>();
        pointModelsList = new ArrayList<>();
        intentPoints = new ArrayList<>();
        startPointList = new ArrayList<>();
        startModelList = new ArrayList<>();
        adsorbDis = DensityUtil.px2dip(context, 100);
        toSidebDis = DensityUtil.px2dip(context, 100);
        Log.e("MyDrawView", "adsorbDis:" + adsorbDis + "/toSidebDis:" + toSidebDis);

        deleteIcon = BitmapFactory.decodeResource(getResources(), R.drawable.chahao);
        mPaintForLineAndCircle = new Paint();
        mPaintForLineAndCircle.setAntiAlias(true);
        mPaintForLineAndCircle.setColor(Color.BLACK);
        mPaintForLineAndCircle.setAlpha(170);

        imageGroupModel = ImageGroupModel.getInstance();
    }

    /**
     * 获取所有图形集合方法
     */
    public List<PoPoListModel> getAllModelList() {
        return pointModelsList;
    }

    public void setAllModelList(List<PoPoListModel> list) {
        this.pointModelsList = list;
    }

//    public List<ImageGroup> getmDecalImageGroupList() {
//        return mDecalImageGroupList;
//    }
//
//    public void setmDecalImageGroupList(List<ImageGroup> mDecalImageGroupList) {
//        this.mDecalImageGroupList = mDecalImageGroupList;
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        midX = w / 2;
        midY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE); //设置背景颜色
        path.reset();
        canvas.scale(curScale, curScale, midX, midY);

        //画吸附后的红点
        if (first != null && second != null) {
            paint.setColor(Color.RED);
            canvas.drawCircle(second.getX(), second.getY(), paintWidth / 2, paint);
        }

        //画图形和标注
        if (pointModelsList != null && pointModelsList.size() > 0) {
            paint.setColor(Color.GRAY);
            for (PoPoListModel poPoListModel : pointModelsList) {
                List<Point> points = poPoListModel.getList();
                if (points != null && points.size() > 0) {
                    path.moveTo(points.get(0).getX(), points.get(0).getY());
                    for (int i = 1; i < points.size(); i++) {
                        path.lineTo(points.get(i).getX(), points.get(i).getY());
                    }
                }
                path.close();
                canvas.drawPath(path, paint);
                canvas.save();

                //添加文字
                if (poPoListModel.getText() != null) {
                    gravityPoint = getCenterOfGravityPoint(poPoListModel.getList());
                    canvas.drawText(poPoListModel.getText(), this.gravityPoint.getX()-50, this.gravityPoint.getY(),
                            writingPaint);
                }

                lineList.clear();
                int position = poPoListModel.getPosition();
                boolean showLength = poPoListModel.isShowLength();
                if (position == -2 && showLength == true) {
                    getRotateCenter(points);
                    getRefereceInfo(points);
                    drawReference(lineList, canvas);
                }
            }
        }

        mDecalImageGroupList = imageGroupModel.getImageGroupList();
        for (ImageGroup imageGroup : mDecalImageGroupList) {
            float[] points = getBitmapPoints(imageGroup);
            float x1 = points[0];
            float y1 = points[1];
            float x2 = points[2];
            float y2 = points[3];
            float x3 = points[4];
            float y3 = points[5];
            float x4 = points[6];
            float y4 = points[7];

            canvas.drawLine(x1, y1, x2, y2, mPaintForLineAndCircle);
            canvas.drawLine(x2, y2, x4, y4, mPaintForLineAndCircle);
            canvas.drawLine(x4, y4, x3, y3, mPaintForLineAndCircle);
            canvas.drawLine(x3, y3, x1, y1, mPaintForLineAndCircle);
            canvas.drawCircle(x2, y2, 20, mPaintForLineAndCircle);
            canvas.drawBitmap(deleteIcon, x2 - deleteIcon.getWidth() / 2, y2 - deleteIcon.getHeight() / 2,
                    mPaintForBitmap); //右上角叉叉

            canvas.drawBitmap(imageGroup.bitmap, imageGroup.matrix, mPaintForBitmap); //贴纸
        }

    }

    public Bitmap initBitmap() {
        mBufferBitmap = Bitmap.createBitmap(getWidth() / 3, getHeight() / 3, Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
        draw(mBufferCanvas);
        return mBufferBitmap;
    }

    boolean isChoosed;
    boolean isMoveSticker;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = (e.getX() - midX) / curScale + midX;
                startY = (e.getY() - midY) / curScale + midY;

                moveTag = decalCheck(startX, startY);
                deleteTag = deleteCheck(startX, startY);
                if (moveTag != -1 && deleteTag == -1) {
                    downMatrix.set(mDecalImageGroupList.get(moveTag).matrix);
                    mode = DRAG;
                    isMoveSticker = true;
                }else{
                    isMoveSticker = false;
                }

                if (!isMoveSticker) {
                    for (PoPoListModel poListModel : pointModelsList) {
                        singleList = poListModel.getList();  //全局变量
                        if (singleList != null && singleList.size() > 0) {
                            downPosition = ensurePoint(singleList, startX, startY);
                            poListModel.setPosition(downPosition);
                            if (downPosition == -2) {  //在多边形内
                                //计算点击该多边形的次数, TODO: 2017/8/23
                                if (0 == mInTouchEventCount.touchCount) { // 第一次按下时,开始统计
                                    isChoosed = true;
                                    mInTouchEventCount.poListModel = poListModel;
                                    postDelayed(mInTouchEventCount, 500);
                                }

                                exitByDoubleClick(poListModel);

                                poListModel.setShowLength(true);
                                poListModel.setIsMOveCanvas(false);
                            } else if (downPosition == -3) {
                                poListModel.setIsMOveCanvas(true);
                            } else {
                                poListModel.setIsMOveCanvas(false);
                            }


                            if (downPosition == -2) {
                                listener.cancelView();
                                isDrawDuan = false;
                            } else if (downPosition == -3) {
                                listener.cancelView();
                                isDrawDuan = false;
                            } else if (downPosition == -1) {
                                duan1 = singleList.get(singleList.size() - 1);
                                duan2 = singleList.get(0);
                                p1 = singleList.get(singleList.size() - 2);
                                p2 = singleList.get(1);
                                if (Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2, (duan1.getY() +
                                        duan2.getY()) / 2) <= 90
                                        && Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2,
                                        (duan1.getY
                                                () + duan2.getY()) / 2) >= 50) {
                                    setView();
                                    isDrawDuan = true;
                                    return true;
                                } else {
                                    listener.cancelView();
                                    isDrawDuan = false;
                                }
                            } else if (downPosition == 0) {
                                duan1 = singleList.get(0);
                                duan2 = singleList.get(1);
                                p1 = singleList.get(singleList.size() - 1);
                                p2 = singleList.get(2);
                                if (Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2, (duan1.getY() +
                                        duan2.getY()) / 2) <= 90
                                        && Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2,
                                        (duan1.getY
                                                () + duan2.getY()) / 2) >= 50) {
                                    setView();
                                    isDrawDuan = true;
                                    return true;
                                } else {
                                    listener.cancelView();
                                    isDrawDuan = false;
                                }
                            } else if (downPosition == singleList.size() - 2) {
                                duan1 = singleList.get(downPosition);
                                duan2 = singleList.get(downPosition + 1);
                                p1 = singleList.get(downPosition - 1);
                                p2 = singleList.get(0);
                                if (Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2, (duan1.getY() +
                                        duan2.getY()) / 2) <= 90
                                        && Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2,
                                        (duan1.getY
                                                () + duan2.getY()) / 2) >= 50) {
                                    setView();
                                    isDrawDuan = true;
                                    return true;
                                } else {
                                    listener.cancelView();
                                    isDrawDuan = false;
                                }
                            } else {
                                duan1 = singleList.get(downPosition);
                                duan2 = singleList.get(downPosition + 1);
                                p1 = singleList.get(downPosition - 1);
                                p2 = singleList.get(downPosition + 2);
                                if (Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2, (duan1.getY() +
                                        duan2.getY()) / 2) <= 90
                                        && Utils.lineSpace(startX, startY, (duan1.getX() + duan2.getX()) / 2,
                                        (duan1.getY
                                                () + duan2.getY()) / 2) >= 50) {
                                    setView();
                                    isDrawDuan = true;
                                    return true;
                                } else {
                                    listener.cancelView();
                                    isDrawDuan = false;
                                }
                            }
                        }
                    }

                }
                mode = 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveTag = decalCheck(e.getX(0), e.getY(0));
                transformTag = decalCheck(e.getX(1), e.getY(1));
                if (moveTag != -1 && transformTag == moveTag && deleteTag == -1) {
                    downMatrix.set(mDecalImageGroupList.get(moveTag).matrix);
                    mode = ZOOM;
                }
                oldDistance = getDistance(e);
                oldRotation = getRotation(e);
                midPoint = midPoint(e);

                preDistance = DrawUtils.getDistance(e);
                //当两指间距大于10时，计算两指中心点
                if (preDistance > 10f) {
                    mode = 2;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveTag != -1 && deleteTag == -1) {
                    if (mode == ZOOM) {
                        moveMatrix.set(downMatrix);
                        float newRotation = getRotation(e) - oldRotation;
                        float newDistance = getDistance(e);
                        float scale = newDistance / oldDistance;
                        moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y);// 縮放
                        moveMatrix.postRotate(newRotation, midPoint.x, midPoint.y);// 旋轉
                        if (moveTag != -1) {
                            mDecalImageGroupList.get(moveTag).matrix.set(moveMatrix);
                        }
                        invalidate();
                    } else if (mode == DRAG) {
                        moveMatrix.set(downMatrix);
                        moveMatrix.postTranslate(e.getX() - startX, e.getY() - startY);// 平移
                        if (moveTag != -1) {
                            mDecalImageGroupList.get(moveTag).matrix.set(moveMatrix);
                        }
                    }
                }

                if (!isMoveSticker) {
                    if (mode == 2) {
                        distance = DrawUtils.getDistance(e);
                        if (distance > 10f) {
                            curScale = mScale * (distance / preDistance);
                        }
                    } else if (mode == 1) {
                        lastX = (e.getX() - midX) / curScale + midX;
                        lastY = (e.getY() - midY) / curScale + midY;
                        moveLine(pointModelsList, new Point(lastX, lastY), lastX - startX, lastY - startY);
                        startX = lastX;
                        startY = lastY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                if (deleteTag != -1) {
                    mDecalImageGroupList.remove(deleteTag).release();
                }
                mode = NONE;

                // TODO: 2017/8/23
                if (isChoosed) {  //在多边形内
                    // 一次点击事件要有按下和抬起, 有抬起必有按下, 所以只需要在ACTION_UP中处理
                    mInTouchEventCount.touchCount++;
                    isChoosed = false;
                }
                mode = 0;
                adsorbResult(pointModelsList);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;

                mScale = curScale;
                mode = 0;
                break;

        }
        // 更新绘制
        invalidate();
        return true;
    }


    private void matrixFix() {
        float[] points = getBitmapPoints(mCropImageGroup.bitmap, moveMatrix);
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y3 = points[5];

        if (mCropImageGroup.bitmap.getWidth() <= mCropImageGroup.bitmap.getHeight()) {
            if ((x2 - x1) < getWidth()) {
                moveMatrix.set(matrixBig);
            }

            if ((y3 - y1) < getHeight()) {
                moveMatrix.set(matrixSmall);
            }
        } else if (mCropImageGroup.bitmap.getWidth() > mCropImageGroup.bitmap.getHeight()) {
            if ((y3 - y1) < getHeight()) {
                moveMatrix.set(matrixBig);
            }

            if ((x2 - x1) < getWidth()) {
                moveMatrix.set(matrixSmall);
            }
        }

        if (!moveMatrix.equals(matrixBig) && !moveMatrix.equals(matrixSmall)) {
            if (x1 >= targetRect.left) {
                moveMatrix.postTranslate(targetRect.left - x1, 0);
            }

            if (x2 <= targetRect.left + getWidth()) {
                moveMatrix.postTranslate(getWidth() - x2, 0);
            }

            if (y1 >= targetRect.top) {
                moveMatrix.postTranslate(0, targetRect.top - y1);
            }

            if (y3 <= targetRect.top + getHeight()) {
                moveMatrix.postTranslate(0, targetRect.top + getHeight() - y3);
            }
        }

        mCropImageGroup.matrix.set(moveMatrix);
        invalidate();
    }

    private void setView() {
        double lineLong = Utils.lineSpace(duan1.getX(), duan1.getY(), duan2.getX(), duan2.getY());
        NumberFormat format = new DecimalFormat("0.00");
        listener.showView(format.format(lineLong / 50).toString());
    }


    /**
     * 移动画布
     *
     * @param showPolygons 点的集合
     * @param dx           x偏移量
     * @param dy           y偏移量
     */
    private void moveCanvas(List<Point> showPolygons, float dx, float dy) {
        for (Point point : showPolygons) {
            point.setX(point.getX() + dx);
            point.setY(point.getY() + dy);
        }
        startX = lastX;
        startY = lastY;
    }

    /**
     * 确定点击点
     *
     * @param startX 按下时的X
     * @param startY 按下时的Y
     * @return
     */
    private int ensurePoint(List<Point> movePoints, float startX, float startY) {
        //形成的多边形要首尾相接
        List<Point> pp = new ArrayList<>();
        pp.addAll(movePoints);
        pp.add(movePoints.get(0));
        int position = -1;
        double minL = DrawUtils.pointToLine(startX, startY, movePoints.get(movePoints.size() - 1), movePoints.get(0));
        for (int i = 0; i < movePoints.size() - 1; i++) {
            double l1 = DrawUtils.pointToLine(startX, startY, movePoints.get(i), movePoints.get(i + 1));
            if (minL < l1) {
            } else if (minL > l1) {
                position = i;
                minL = l1;
            }
        }
//        if (minL <= toSidebDis) {
//            return position;//根据position获取要移动线的 端点
//        } else if (DrawUtils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > toSidebDis) {
//            return -2;//表示点击点在多边形内,执行移动view
//        } else {
//            return -3;//表示点击点在多边形外,不执行任何操作
//        }

        if (minL <= toSidebDis / 2) {
            return position;//根据position获取要移动线的 端点
        } else if (Utils.PtInRegion(new Point(startX, startY), pp) == 1 && minL > toSidebDis) {
            return -2;//表示点击点在多边形内,执行移动view
        } else if (Utils.PtInRegion(new Point(startX, startY), pp) == -1 && minL > toSidebDis / 2 && minL <
                toSidebDis * 2) {
            return position;//根据position获取要移动线的 端点
        } else {
            return -3;//表示点击点在多边形外,不执行任何操作
        }


    }

    /**
     * 移动线
     */
    List<Point> movePoints = new ArrayList<>();//用于存放变化的点

    public void moveLine(List<PoPoListModel> list, Point movePoint, float dx, float dy) {
        int count = 0;
        movePoints.clear();
        for (PoPoListModel poListModel : list) {
            if (poListModel.getPosition() != -3) {  //表示选中图形或者某条边
                count++;
            }
        }
        if (count == 0) { //没有选中任何图形或者边,将isMoveCanvas置为false进入-3的判断条件
            for (PoPoListModel poListModel : list) {
                poListModel.setIsMOveCanvas(false);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            List<Point> newList = new ArrayList<>();
            Point duan1;//选中线的端点1, 局部变量
            Point duan2;//选中线的端点2, 局部变量

            PoPoListModel poPoListModel = list.get(i);
            boolean isMoveCanvas = poPoListModel.isMOveCanvas();
            List<Point> singlePointList = poPoListModel.getList(); //点的集合
            int position = poPoListModel.getPosition();

            if (singlePointList.size() == 3) {  //三角形
                if (isMoveCanvas == false) {
                    if (position == -1) {   //选中倒数第二个点和起点的那条直线
                        duan1 = singlePointList.get(0);
                        duan2 = singlePointList.get(singlePointList.size() - 1);

                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(1));
                        newList.add(singlePointList.get(1));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(singlePointList.size() - 1, pointList.get(1));

                    } else if (position == -2) {  //选中整体图形{
                        moveView(poPoListModel, singlePointList, dx, dy);
                    } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
//                    return;
                        moveCanvas(singlePointList, dx, dy);
                    } else if (position == 0) {
                        duan1 = singlePointList.get(position);
                        duan2 = singlePointList.get(position + 1);

                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(2));
                        newList.add(singlePointList.get(2));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(position + 1, pointList.get(1));
                    } else if (position == 1) {
                        duan1 = singlePointList.get(position);
                        duan2 = singlePointList.get(position + 1);
                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(0));
                        newList.add(singlePointList.get(0));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(position + 1, pointList.get(1));
                    }
                }
            } else if (singlePointList.size() > 3) {   //表示多边形
                if (isMoveCanvas == false) {
                    //通过ensurePoint获得position,判断position
                    if (position == -1) {   //选中倒数第二个点和起点的那条直线
                        duan1 = singlePointList.get(0);
                        duan2 = singlePointList.get(singlePointList.size() - 1);

                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(singlePointList.size() - 2));
                        newList.add(singlePointList.get(1));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(0, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(singlePointList.size() - 1, pointList.get(1));

                    } else if (position == -2) {  //选中整体图形
                        moveView(poPoListModel, singlePointList, dx, dy);
                        return;
                    } else if (position == -3) {  //表示点击点在多边形外,不执行任何操作
//                    return;
                        moveCanvas(singlePointList, dx, dy);
                    } else if (position == 0) {  //index=-1旁边的直线
                        duan1 = singlePointList.get(position);
                        duan2 = singlePointList.get(position + 1);

                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(position + 2));
                        newList.add(singlePointList.get(singlePointList.size() - 1));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(position + 1, pointList.get(1));

                    } else if (position == singlePointList.size() - 2) { //index=-1旁边的直线

                        duan1 = singlePointList.get(position);
                        duan2 = singlePointList.get(position + 1);
                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(0));
                        newList.add(singlePointList.get(position - 1));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(position + 1, pointList.get(1));

                    } else {
                        duan1 = singlePointList.get(position);
                        duan2 = singlePointList.get(position + 1);
                        newList.add(duan1);
                        newList.add(duan2);
                        newList.add(singlePointList.get(position + 2));
                        newList.add(singlePointList.get(position - 1));

                        List<Point> pointList = getPoint(newList, movePoint);
                        singlePointList.set(position, pointList.get(0));  //替换掉原来元素中移动边的两个坐标点
                        singlePointList.set(position + 1, pointList.get(1));
                    }
                }
            }
//            addText(poPoListModel);
        }
    }

    public void moveLine(Point duan1, Point duan2, Point movePoint, int downPosition, float dx, float dy) {
        List<Point> newList = new ArrayList<>();
        if (singleList.size() == 3) {  //三角形
            if (downPosition == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = singleList.get(0);
                duan2 = singleList.get(singleList.size() - 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(1));
                newList.add(singleList.get(1));
                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(0).setX(pointList.get(0).getX());
                singleList.get(0).setY(pointList.get(0).getY());
                singleList.get(singleList.size() - 1).setX(pointList.get(1).getX());
                singleList.get(singleList.size() - 1).setY(pointList.get(1).getY());
            } else if (downPosition == -2) {  //选中整体图形{
//                moveView(singleList, dx, dy);
            } else if (downPosition == -3) {  //表示点击点在多边形外,不执行任何操作
                moveCanvas(singleList, dx, dy);
            } else if (downPosition == 0) {
                duan1 = singleList.get(downPosition);
                duan2 = singleList.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(2));
                newList.add(singleList.get(2));
                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(downPosition).setX(pointList.get(0).getX());
                singleList.get(downPosition).setY(pointList.get(0).getY());
                singleList.get(downPosition + 1).setX(pointList.get(1).getX());
                singleList.get(downPosition + 1).setY(pointList.get(1).getY());
            } else if (downPosition == 1) {
                duan1 = singleList.get(downPosition);
                duan2 = singleList.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(0));
                newList.add(singleList.get(0));
                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(downPosition).setX(pointList.get(0).getX());
                singleList.get(downPosition).setY(pointList.get(0).getY());
                singleList.get(downPosition + 1).setX(pointList.get(1).getX());//替换掉原来元素中移动边的两个坐标点
                singleList.get(downPosition + 1).setY(pointList.get(1).getY());
            }
        } else if (singleList.size() > 3) {   //表示多边形
            //通过ensurePoint获得downPosition,判断downPosition
            if (downPosition == -1) {   //选中倒数第二个点和起点的那条直线
                duan1 = singleList.get(0);
                duan2 = singleList.get(singleList.size() - 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(singleList.size() - 2));
                newList.add(singleList.get(1));

                List<Point> pointList = getPoint(newList, movePoint);
                pointList.get(0).setLock(true);
                pointList.get(1).setLock(true);
                singleList.get(0).setX(pointList.get(0).getX());
                singleList.get(0).setY(pointList.get(0).getY());
                singleList.get(singleList.size() - 1).setX(pointList.get(1).getX());
                singleList.get(singleList.size() - 1).setY(pointList.get(1).getY());

            } else if (downPosition == -2) {  //选中整体图形
//                moveView(singleList, dx, dy);
            } else if (downPosition == -3) {  //表示点击点在多边形外,不执行任何操作
                moveCanvas(singleList, dx, dy);
            } else if (downPosition == 0) {  //index=-1旁边的直线
                duan1 = singleList.get(downPosition);
                duan2 = singleList.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(downPosition + 2));
                newList.add(singleList.get(singleList.size() - 1));

                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(downPosition).setX(pointList.get(0).getX());
                singleList.get(downPosition).setY(pointList.get(0).getY());
                singleList.get(downPosition + 1).setX(pointList.get(1).getX());
                singleList.get(downPosition + 1).setY(pointList.get(1).getY());
            } else if (downPosition == singleList.size() - 2) { //index=-1旁边的直线
                duan1 = singleList.get(downPosition);
                duan2 = singleList.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(0));
                newList.add(singleList.get(downPosition - 1));

                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(downPosition).setX(pointList.get(0).getX());
                singleList.get(downPosition).setY(pointList.get(0).getY());
                singleList.get(downPosition + 1).setX(pointList.get(1).getX());
                singleList.get(downPosition + 1).setY(pointList.get(1).getY());
            } else {
                duan1 = singleList.get(downPosition);
                duan2 = singleList.get(downPosition + 1);
                newList.add(duan1);
                newList.add(duan2);
                newList.add(singleList.get(downPosition + 2));
                newList.add(singleList.get(downPosition - 1));
                List<Point> pointList = getPoint(newList, movePoint);
                singleList.get(downPosition).setX(pointList.get(0).getX());
                singleList.get(downPosition).setY(pointList.get(0).getY());
                singleList.get(downPosition + 1).setX(pointList.get(1).getX());
                singleList.get(downPosition + 1).setY(pointList.get(1).getY());
            }

        }
    }


    /**
     * 移动VIEW
     *
     * @param dx
     * @param dy
     */
    public void moveView(PoPoListModel poPoListModel, List<Point> pointList, float dx, float dy) {
        for (Point point : pointList) {
            point.setX(point.getX() + dx);
            point.setY(point.getY() + dy);
        }
//        addText(poPoListModel);
    }

    /**
     * 吸附后重置数据
     */
    public void adsorbResult(List<PoPoListModel> pointModelsList) {
//        intentPoints.clear();  //选中图形点集合,千万不能有这句否则有问题
        if (pointModelsList != null && pointModelsList.size() > 1) {  //有多个图形的时候
            startModelList.clear();  //除选中图形外的所有图形点集合
            for (int k = 0; k < pointModelsList.size(); k++) {
                PoPoListModel poModel = pointModelsList.get(k);
                int position = poModel.getPosition();
                if (position == -2) {  //表示选中该图形
                    intentPoints = poModel.getList();
                    for (PoPoListModel poListModel : pointModelsList) {
                        if (poListModel != poModel) {
                            startModelList.add(poListModel);
                        }
                    }
                    distanceList.clear();
                    for (int i = 0; i < intentPoints.size(); i++) {
                        Point pointI = intentPoints.get(i);
                        for (PoPoListModel poListModel : startModelList) {
                            startPointList = poListModel.getList();
                            for (int j = 0; j < startPointList.size(); j++) {
                                Point pointJ = startPointList.get(j);
                                float distance = DrawUtils.calTwoPointDistance(pointI, pointJ);
                                if (distance < adsorbDis) {
                                    TwoPointDistance twoPointDistance = new TwoPointDistance(pointI, pointJ, distance);
                                    distanceList.add(twoPointDistance);
                                }
                            }
                        }
                    }
                    if (distanceList != null && distanceList.size() > 0) {
                        Collections.sort(distanceList, new MyComparator());
                        for (TwoPointDistance twoPointDistance : distanceList) {
                            Log.e("DrawTestView1", "twoPointDistance:" + twoPointDistance.toString());
                        }
                        first = distanceList.get(0).getFirst();
                        second = distanceList.get(0).getSecond();
                        float dx_xifu = second.getX() - first.getX();
                        float dy_xifu = second.getY() - first.getY();

                        List<Point> newPointList = new ArrayList<>();
                        for (Point point : intentPoints) {
                            float newX = point.getX() + dx_xifu;
                            float newY = point.getY() + dy_xifu;
                            Point newPoint = new Point(newX, newY);
                            newPointList.add(newPoint);
                        }

                        intentPoints.clear();
                        intentPoints.addAll(newPointList);
                        anewDraw(intentPoints, path);
                        aOldDraw(startPointList, path);
                        invalidate();
                    }
                }
//                addText(poModel);
            }
        }
    }

    /**
     * 选中整体图形后移动并重新绘制
     *
     * @param points 点集合
     */
    public void anewDraw(List<Point> points, Path mPath) {
        mPath.reset();
        mPath.moveTo(points.get(0).getX(), points.get(0).getY());
        for (Point point : points) {
            mPath.lineTo(point.getX(), point.getY());
        }
        mPath.lineTo(points.get(0).getX(), points.get(0).getY());
        invalidate();
    }

    public void aOldDraw(List<Point> startPointList, Path testPath) {
        testPath.moveTo(startPointList.get(0).getX(), startPointList.get(0).getY());
        for (int i = 1; i < startPointList.size(); i++) {
            Point point = startPointList.get(i);
            testPath.lineTo(point.getX(), point.getY());
        }
        testPath.close();
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
        if (Float.isInfinite(moveLineK) || Float.isNaN(moveLineK)) {  //竖线，水平方向移动  isNaN无穷大  isInfinite无意义的(分母为0)
            if (crossLineK == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = DrawUtils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = list.get(1).getY();
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            } else if (crossLineK1 == 0) {  //一条相交线都是水平方向
                float crosspointX = movePoint.getX();
                float crosspointY = list.get(0).getY();
                float crosspointY1 = DrawUtils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
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
                float crosspointY = DrawUtils.calBeelineEquation(crossLineK1, crosspointX, list.get(0));
                float crosspointY1 = DrawUtils.calBeelineEquation(crossLineK, crosspointX, list.get(1));
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                moveinwardPoint.add(new Point(crosspointX, crosspointY1));
            }
        } else if (moveLineK == 0) {  //选中的线是横线，垂直方向移动
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {
                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
                float crosspointY1 = list.get(0).getY() + dy;
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            } else {
                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
                float crosspointY = list.get(1).getY() + dy;
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            } else {
                float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            }

        } else {  //选中的边既不垂直也不水平
            if (Float.isInfinite(crossLineK1) || Float.isNaN(crossLineK1)) {
                float dy = lastY - startY;
                float crosspointX1 = list.get(0).getX();
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            } else {
                float crosspointX1 = DrawUtils.calCrosspointX(moveLineK, crossLineK1, movePoint, list.get(3));
                float crosspointY1 = DrawUtils.calBeelineEquation(moveLineK, crosspointX1, movePoint);
                moveinwardPoint.add(new Point(crosspointX1, crosspointY1));
                Log.e("MoveLineView", "crosspointX1:" + crosspointX1 + "/" + "crosspointY1:" + crosspointY1);
            }
            if (Float.isInfinite(crossLineK) || Float.isNaN(crossLineK)) {

                float dy = lastY - startY;
                float crosspointX = list.get(1).getX();
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            } else {
                float crosspointX = DrawUtils.calCrosspointX(moveLineK, crossLineK, movePoint, list.get(1));
                float crosspointY = DrawUtils.calBeelineEquation(moveLineK, crosspointX, movePoint);
                moveinwardPoint.add(new Point(crosspointX, crosspointY));
                Log.e("MoveLineView", "crosspointX:" + crosspointX + "/" + "crosspointY:" + crosspointY);
            }
        }

        return moveinwardPoint;

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
            double slope = DrawUtils.calSlope(p1, p2);
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
        }
        canvas.restoreToCount(1);
        canvas.save();
    }

    /*一个点到坐标轴原点的距离*/
    public float disToZreo(Point point) {
        float x = point.getX();
        float y = point.getY();
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
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
        rotateCenter.clear();
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0) {
                rotateCenter.add(pointList.get(0));
            } else {
                rotateCenter.add(new Point((float) (rotateCenter.get(i - 1).getX() + calDistanceByTwoPoint(pointList
                        .get(i), pointList.get(i - 1))), rotateCenter.get(0).getY()));
            }
        }
        /*添加最后一条线段的旋转中心*/
//        rotateCenter.add(new Point((float) (rotateCenter.get(pointList.size()-1).getX()+calDistanceByTwoPoint
// (pointList.get(0),pointList.get(pointList.size()-1))),rotateCenter.get(0).getY()));
    }


    public void setLongNum(Double longNum) {
        this.longNum = longNum;
        double ll = Utils.lineSpace(duan1.getX(), duan1.getY(), duan2.getX(), duan2.getY());
        double l = (longNum * 50 - ll) / 2;
        float k = Utils.calSlope(duan1, duan2);
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
            position = singleList.size() - 2;
        } else {
            position = downPosition - 1;
        }
        moveLine(p1, duan1, moveP1, position, 0, 0);
        duan1.setX(moveP1.getX());
        duan1.setY(moveP1.getY());
        if (position == -1) {
            Point p = singleList.get(singleList.size() - 1);
            p1.setX(p.getX());
            p1.setY(p.getY());
        } else {
            Point p = singleList.get(position);
            p1.setX(p.getX());
            p1.setY(p.getY());
        }

        if (downPosition == singleList.size() - 2) {
            position = -1;
        } else {
            position = downPosition + 1;
        }
        moveLine(duan2, p2, moveP2, position, 0, 0);
        duan2.setX(moveP2.getX());
        duan2.setY(moveP2.getY());
        if (position == -1) {
            Point p = singleList.get(0);
            p2.setX(p.getX());
            p2.setY(p.getY());
        } else {
            Point p = singleList.get(position + 1);
            p2.setX(p.getX());
            p2.setY(p.getY());
        }
        duan1.setLock(true);
        duan2.setLock(true);

        //更新绘制
        invalidate();
    }


    public class TouchEventCountThread implements Runnable {
        public int touchCount = 0;
        public PoPoListModel poListModel = new PoPoListModel();

        @Override
        public void run() {
            Message msg = new Message(); // TODO: 2017/8/23  
            if (touchCount != 0) {
                msg.arg1 = touchCount;
                msg.obj = poListModel;
                mTouchEventHandler.sendMessage(msg);
                touchCount = 0;
            }
        }
    }

    public interface OnDoubleClickListener {
        void onDoubleClick(View v);
    }

    private OnDoubleClickListener mOnDoubleClickListener;

    public void setOnDoubleClickListener(OnDoubleClickListener l) {
        mOnDoubleClickListener = l;
    }

    public boolean performDoubleClick() {
        boolean result = false;
        if (mOnDoubleClickListener != null) {
            mOnDoubleClickListener.onDoubleClick(this);
            result = true;
        }
        return result;
    }


    private PoPoListModel poListModel;
    public class TouchEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (2 == msg.arg1) {
                // TODO: 2017/8/23
                poListModel = (PoPoListModel) msg.obj;
                performDoubleClick();
            }
        }
    }

    boolean isExit;
    private void exitByDoubleClick(PoPoListModel poListModel) {
        Timer tExit=null;
        if(!isExit){
            isExit=true;
            tExit=new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit=false;//取消退出
                }
            },1000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        }else{
            this.poListModel = poListModel;
            performDoubleClick();
//            Toast.makeText(getContext(),"退出程序--->Timer",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAddText; //是否折线
    private String editText;
    private boolean isAddRouter; //添加路由器

    /**
     * 是否添加文字
     */
    public void setAddText(boolean isAddText, String editText) {
        this.isAddText = isAddText;
        this.editText = editText;
        poListModel.setText(editText);
        invalidate();
    }

    /**
     * 添加文字
     */
    public void addText(PoPoListModel poListModel) {
        poListModel.setText(editText);
        gravityPoint = DrawUtils.getCenterOfGravityPoint(poListModel.getList());
        isAddText = true;
    }

    /**
     * 添加路由器
     */
//    public void addRouter(boolean isAddRouter) {
//        this.isAddRouter = isAddRouter;
//        invalidate();
//    }

    private boolean pointCheck(ImageGroup imageGroup, float x, float y) {
        float[] points = getBitmapPoints(imageGroup);
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y2 = points[3];
        float x3 = points[4];
        float y3 = points[5];
        float x4 = points[6];
        float y4 = points[7];

        float edge = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        if ((2 + Math.sqrt(2)) * edge >= Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2))
                + Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2))
                + Math.sqrt(Math.pow(x - x3, 2) + Math.pow(y - y3, 2))
                + Math.sqrt(Math.pow(x - x4, 2) + Math.pow(y - y4, 2))) {
            return true;
        }
        return false;
    }

    private boolean circleCheck(ImageGroup imageGroup, float x, float y) {
        float[] points = getBitmapPoints(imageGroup);
        float x2 = points[2];
        float y2 = points[3];

        int checkDis = (int) Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));

        if (checkDis < 40) {
            return true;
        }
        return false;
    }

    private int deleteCheck(float x, float y) {
        for (int i = 0; i < mDecalImageGroupList.size(); i++) {
            if (circleCheck(mDecalImageGroupList.get(i), x, y)) {
                return i;
            }
        }
        return -1;
    }

    private int decalCheck(float x, float y) {
        for (int i = 0; i < mDecalImageGroupList.size(); i++) {
            if (pointCheck(mDecalImageGroupList.get(i), x, y)) {
                return i;
            }
        }
        return -1;
    }

    public void addDecal(Bitmap bitmap) {
        ImageGroup imageGroupTemp = new ImageGroup();
        imageGroupTemp.bitmap = bitmap;
        if (imageGroupTemp.matrix == null) {
            imageGroupTemp.matrix = new Matrix();
        }
        float transX = (getWidth() - imageGroupTemp.bitmap.getWidth()) / 2;
        float transY = (getHeight() - imageGroupTemp.bitmap.getHeight()) / 2;
        imageGroupTemp.matrix.postTranslate(transX, transY);
        imageGroupTemp.matrix.postScale(0.5f, 0.5f, getWidth() / 2, getHeight() / 2);
//        mDecalImageGroupList.add(imageGroupTemp);
        imageGroupModel.addImageGroup(imageGroupTemp);

        invalidate();
    }


}
