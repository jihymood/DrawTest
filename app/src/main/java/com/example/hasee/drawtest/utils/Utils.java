package com.example.hasee.drawtest.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.hasee.drawtest.model.Point;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public class Utils {
    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 判断坐标点是否落在指定的多边形区域内
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
            if (Math.abs(tempMult) < eps && (l1.getX() - p0.getX()) * (l2.getX() - p0.getX()) < eps && (l1.getY() - p0.getY()) * (l2.getY() - p0.getY()) < eps) {
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
            return -1;//外
        } else {
            return 1;//内
        }
    }

    public static double xmult(Point p1, Point p2, Point p0) {
        return (p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2.getX() - p0.getX()) * (p1.getY() - p0.getY());
    }

    // 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
    public static double pointToLine(float x0, float y0, Point point1, Point point2) {
        float x1 = point1.getX();
        float y1 = point1.getY();
        float x2 = point2.getX();
        float y2 = point2.getY();
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    // 计算两点之间的距离
    public static double lineSpace(float x1, float y1, float x2, float y2) {
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return lineLength;
    }


    /**
     * 判断两条线是否相交 a 线段1起点坐标 b 线段1终点坐标 c 线段2起点坐标 d 线段2终点坐标 intersection 相交点坐标
     * reutrn 是否相交: 0 : 两线平行 -1 : 不平行且未相交 1 : 两线相交 2:线段相交于端点上
     */
    public static int segIntersect(Point A, Point B, Point C, Point D) {
        Point intersection = new Point();
        if (Math.abs((int) B.getY() - (int) A.getY()) + Math.abs((int) B.getX() - (int) A.getX()) + Math.abs((int) D.getY() - (int) C.getY())
                + Math.abs((int) D.getX() - (int) C.getX()) == 0) {
            if (((int) C.getX() - (int) A.getX()) + ((int) C.getY() - (int) A.getY()) == 0) {
                System.out.println("ABCD是同一个点！");
            } else {
                System.out.println("AB是一个点，CD是一个点，且AC不同！");
            }
            return 0;
        }
        if (Math.abs((int) B.getY() - (int) A.getY()) + Math.abs((int) B.getX() - (int) A.getX()) == 0) {
            if (((int) A.getX() - (int) D.getX()) * ((int) C.getY() - (int) D.getY()) - ((int) A.getY() - (int) D.getY()) * ((int) C.getX() - (int) D.getX()) == 0) {
                System.out.println("A、B是一个点，且在CD线段上！");
            } else {
                System.out.println("A、B是一个点，且不在CD线段上！");
            }
            return 0;
        }
        if (Math.abs((int) D.getY() - (int) C.getY()) + Math.abs((int) D.getX() - (int) C.getX()) == 0) {
            if (((int) D.getX() - (int) B.getX()) * ((int) A.getY() - (int) B.getY()) - ((int) D.getY() - (int) B.getY()) * ((int) A.getX() - (int) B.getX()) == 0) {
                System.out.println("C、D是一个点，且在AB线段上！");
            } else {
                System.out.println("C、D是一个点，且不在AB线段上！");
            }
            return 0;
        }
        if (((int) B.getY() - (int) A.getY()) * ((int) C.getX() - (int) D.getX()) - ((int) B.getX() - (int) A.getX()) * ((int) C.getY() - (int) D.getY()) == 0) {
//   System.out.println("线段平行，无交点！");
            return 0;
        }

        intersection
                .setX((((int) B.getX() - (int) A.getX()) * ((int) C.getX() - (int) D.getX())
                        * ((int) C.getY() - (int) A.getY()) - (int) C.getX()
                        * ((int) B.getX() - (int) A.getX()) * ((int) C.getY() - (int) D.getY()) + (int) A
                        .getX() * ((int) B.getY() - (int) A.getY()) * ((int) C.getX() - (int) D.getX()))
                        / (((int) B.getY() - (int) A.getY()) * ((int) C.getX() - (int) D.getX()) - ((int) B
                        .getX() - (int) A.getX()) * ((int) C.getY() - (int) D.getY())));
        intersection
                .setY((((int) B.getY() - (int) A.getY()) * ((int) C.getY() - (int) D.getY())
                        * ((int) C.getX() - (int) A.getX()) - (int) C.getY()
                        * ((int) B.getY() - (int) A.getY()) * ((int) C.getX() - (int) D.getX()) + (int) A
                        .getY() * ((int) B.getX() - (int) A.getX()) * ((int) C.getY() - (int) D.getY()))
                        / (((int) B.getX() - (int) A.getX()) * ((int) C.getY() - (int) D.getY()) - ((int) B
                        .getY() - (int) A.getY()) * ((int) C.getX() - (int) D.getX())));
        if (((int) intersection.getX() - (int) A.getX()) * ((int) intersection.getX() - (int) B.getX()) <= 0
                && ((int) intersection.getX() - (int) C.getX())
                * ((int) intersection.getX() - (int) D.getX()) <= 0
                && ((int) intersection.getY() - (int) A.getY())
                * ((int) intersection.getY() - (int) B.getY()) <= 0
                && ((int) intersection.getY() - (int) C.getY())
                * ((int) intersection.getY() - (int) D.getY()) <= 0) {
            if (((int) A.getX() == (int) C.getX() && (int) A.getY() == (int) C.getY()) || ((int) A.getX() == (int) D.getX() && (int) A.getY() == (int) D.getY())
                    || ((int) B.getX() == (int) C.getX() && (int) B.getY() == (int) C.getY()) || ((int) B.getX() == (int) D.getX() && (int) B.getY() == (int) D.getY())) {

                System.out.println("线段相交于端点上");
                return 2;

            } else {
                System.out.println("线段相交于点(" + intersection.getX() + ","
                        + intersection.getY() + ")！");
                return 1; // '相交
            }

        } else {
//   System.out.println("线段相交于虚交点(" + intersection.getX() + ","
//     + intersection.getY() + ")！");
            return -1; // '相交但不在线段上
        }
    }

    /**
     * 画箭头
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    public static void drawAL(int sx, int sy, int ex, int ey, Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(5);
        double H = 8; // 箭头高度
        double L = 3.5; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        canvas.drawLine(sx, sy, ex, ey, paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint);

    }

    // 计算
    public static double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    public static List<Point> getLabelPoint(Point duan1, Point duan2) {
        Point p = null;
        List<Point> labelPoints = new ArrayList<>();
        for (float x = duan1.getX() - 10; x <= duan1.getX() + 10; x++) {
            for (float y = duan1.getY() - 10; y <= duan1.getY() + 10; y++) {
                if (pointToLine(x, y, duan1, duan2) == lineSpace(x, y, duan1.getX(), duan1.getY()) && lineSpace(x, y, duan1.getX(), duan1.getY()) == 10 && lineSpace(x, y, duan1.getX(), duan1.getY()) == 10) {
                    p = new Point(x, y);
                    labelPoints.add(p);
                }

            }
        }
        for (float x = duan2.getX() - 10; x < duan2.getX() + 10; x++) {
            for (float y = duan2.getY() - 10; y <= duan2.getY() + 10; y++) {
                if (pointToLine(x, y, duan1, duan2) == lineSpace(x, y, duan2.getX(), duan2.getY()) && lineSpace(x, y, duan2.getX(), duan2.getY()) == 10 && lineSpace(x, y, duan2.getX(), duan2.getY()) == 10) {
                    p = new Point(x, y);
                    labelPoints.add(p);
                }
            }
        }
        return labelPoints;
    }

    //计算两条直线的交点
    public static Point getCross(Point p1, Point p2, Point q1, Point q2) {

        //第一条直线
        double x1 = p1.getX(), y1 = p1.getY(), x2 = p1.getX(), y2 = p2.getY();
        double a = (y1 - y2) / (x1 - x2);
        double b = (x1 * y2 - x2 * y1) / (x1 - x2);
        System.out.println("求出该直线方程为: y=" + a + "x + " + b);

//第二条
        double x3 = q1.getX(), y3 = q1.getY(), x4 = q2.getX(), y4 = q2.getY();
        double c = (y3 - y4) / (x3 - x4);
        double d = (x3 * y4 - x4 * y3) / (x3 - x4);
        // System.out.println("求出该直线方程为: y=" + c + "x + " + d);

        double x = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));

        double y = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        Point crossP = new Point((int) x, (int) y);
        //  System.out.println("他们的交点为: (" + x + "," + y + ")");
        return crossP;
    }

    public static Point getCrossPoint(Point a1, Point a2, Point b1, Point b2) {
        float x;
        float y;
        float x1 = a1.getX();
        float y1 = a1.getY();
        float x2 = a2.getX();
        float y2 = a2.getY();
        float x3 = b1.getX();
        float y3 = b1.getY();
        float x4 = b2.getX();
        float y4 = b2.getY();
        float k1 = Float.MAX_VALUE;
        float k2 = Float.MAX_VALUE;
        boolean flag1 = false;
        boolean flag2 = false;

        if ((x1 - x2) == 0)
            flag1 = true;
        if ((x3 - x4) == 0)
            flag2 = true;

        if (!flag1)
            k1 = (y1 - y2) / (x1 - x2);
        if (!flag2)
            k2 = (y3 - y4) / (x3 - x4);

        if (k1 == k2)
            return null;

        if (flag1) {
            if (flag2)
                return null;
            x = x1;
            if (k2 == 0) {
                y = y3;
            } else {
                y = k2 * (x - x4) + y4;
            }
        } else if (flag2) {
            x = x3;
            if (k1 == 0) {
                y = y1;
            } else {
                y = k1 * (x - x2) + y2;
            }
        } else {
            if (k1 == 0) {
                y = y1;
                x = (y - y4) / k2 + x4;
            } else if (k2 == 0) {
                y = y3;
                x = (y - y2) / k1 + x2;
            } else {
                x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2);
                y = k1 * (x - x2) + y2;
            }
        }
        if (between(x1, x2, x) && between(y1, y2, y) && between(x3, x4, x) && between(y3, y4, y)) {
            Point point = new Point();
            point.setX((int) x);
            point.setY((int) y);
            if (point.equals(a1) || point.equals(a2))
                return null;
            return point;
        } else {
            return null;
        }
    }

    public static boolean between(float a, float b, float target) {
        if (target >= a - 0.01 && target <= b + 0.01 || target <= a + 0.01 && target >= b - 0.01)
            return true;
        else
            return false;
    }

    /**
     * 计算直线斜率
     */
    public static float calSlope(Point point, Point point1) {

        float k;
        float y1 = point1.getY();
        float y = point.getY();
        float x1 = point1.getX();
        float x = point.getX();

//        if (y1 - y < 0 && x1 - x > 0 || y1 - y > 0 && x1 - x < 0) {
//            k = -((y1 - y) / (x1 - x));
//        } else {
//            k = -((y1 - y) / (x1 - x));
//        }
        k = (y1 - y) / (x1 - x);
        return k;
    }

    /**
     * 根据 不同直线上的两个点 求两条直线的相交点x坐标
     *
     * @param moveLineK   移动线斜率
     * @param crossLineK  相交线斜率
     * @param movePoint   移动线抬起时鼠标点坐标
     * @param inwardPoint 相交线的两个坐标中任意一个
     * @return
     */
    public static float calCrosspointX(float moveLineK, float crossLineK, Point movePoint, Point inwardPoint) {
        float x, temp;
        float movePointX = movePoint.getX();
        float movePointY = movePoint.getY();
        float inwardPointX = inwardPoint.getX();
        float inwardPointY = inwardPoint.getY();

        temp = moveLineK * movePointX - crossLineK * inwardPointX + inwardPointY - movePointY;
        x = temp / (moveLineK - crossLineK);
        return x;
    }

    /**
     * 根据相交点x坐标求已经方程经过该点的y坐标
     */
    public static float calBeelineEquation(float k, float x, Point point) {
        float y;
        y = k * (x - point.getX()) + point.getY();
        return y;
    }

    /**
     * 计算直线斜率
     */
    public static List<Point> verticalFC(Point p1, Point p2) {
//y=kx+b
        float k;
        float y2 = p2.getY();
        float y1 = p1.getY();
        float x2 = p2.getX();
        float x1 = p1.getX();
        k = ((y2 - y1) / (x2 - x1));
        float vk = -1 / k;
        float b1 = y1 - vk * x1;
        //垂直的方程为y=vk*x+b1;
        //平移后的方程为y=k*x+b1+Math.sqrt()
        Math.sqrt(20 * 20 * (k * k + 1));
        //由2个方程的x;
        double qx1 = -Math.sqrt(20 * 20 * (k * k + 1)) * k / (k * k + 1);
        double qy1 = vk * qx1 + b1;
        List<Point> points = new ArrayList<>();
        points.add(new Point((int) qx1, (int) qy1));
        float b2 = y2 - vk * x2;
        //垂直的方程为y=vk*x+b2;
        //平移后的方程为y=k*x+b1+Math.sqrt()
        double qx2 = (-Math.sqrt(20 * 20 * (k * k + 1)) - b1 + b2) / (k - vk);
        double qy2 = vk * qx2 + b2;
        points.add(new Point((int) qx2, (int) qy2));
        return points;
    }

}
