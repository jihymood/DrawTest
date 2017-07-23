package com.example.hasee.drawtest.utils;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.hasee.drawtest.model.Point;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工具类
 * Created by HASEE on 2017/7/19 18:11
 */

public class DrawUtils {


    /**
     * 判断点击的点是否在图形内
     */
    static Region re = new Region();

    public static boolean isInside(Path path, MotionEvent event) {
        //构造一个区域对象，左闭右开的。
        RectF r = new RectF();
        //计算控制点的边界
        path.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        re.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        //在封闭的path内返回true 不在返回false
        return re.contains((int) event.getX(), (int) event.getY());
    }

    /**标点是否落在指定的多边形区域内
     * 判断坐
     */
    public static int PtInRegion(Point p0, List<Point> plg) {
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





    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param str 字符是否为空
     */
    public static boolean isEmpty(String str) {
        if (null == str || str.trim().length() == 0
                || str.trim().equals("null")) {
            return true;
        }
        return false;
    }

    /**
     * 保留指定位数
     *
     * @param num 保留位数
     */
    public static String formatString(Object obj, int num) {
        try {
            BigDecimal bd = new BigDecimal(obj + "");
            bd = bd.setScale(num, BigDecimal.ROUND_DOWN);
            return bd.toString();
        } catch (Exception e) {
            return obj.toString();
        }
    }

    /**
     * 获取屏幕宽高
     *
     * @return size[0]:width size[1]:height
     */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mg = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mg.getDefaultDisplay().getMetrics(dm);
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    /**
     * 获取控件左上角和右下角的点
     */
    public static Point[] getPointWithView(View v) {
        Point[] coordinate = new Point[2];
        int left = v.getLeft();
        int top = v.getTop();
        int right = v.getRight();
        int bottom = v.getBottom();
        Point lt = new Point(left, top);
        Point rb = new Point(right, bottom);
        coordinate[0] = lt;
        coordinate[1] = rb;
        return coordinate;
    }


}
