package com.example.hasee.drawtest;

import com.example.hasee.drawtest.model.Point;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Point point = new Point(11, 12);
        Point point1 = new Point(11, 12);
        TwoPointDistance u1 = new TwoPointDistance(point,point1,63);
        TwoPointDistance u2 = new TwoPointDistance(point,point1,23);
        TwoPointDistance u3 = new TwoPointDistance(point,point1,73);
        TwoPointDistance u4 = new TwoPointDistance(point,point1,32);
        TwoPointDistance u5 = new TwoPointDistance(point,point1,30);

        List list = new ArrayList();
        list.add(u1);
        list.add(u2);
        list.add(u3);
        list.add(u4);
        list.add(u5);
        Object[] users = list.toArray();
        System.out.println("排序前。。。。");
        for (int i = 0; i < users.length; i++) {
            System.out.println(users[i]);
        }
        System.out.println("*******************************");
        System.out.println("排序后。。。。。");
        //把排序规则交给sort方法。该方法就回按照你自定义的规则进行排序

        Arrays.sort(users, new MyComparator());

        for (int i = 0; i < users.length; i++) {
            System.out.println(users[i]);
        }


    }


    class TwoPointDistance {
        public Point first;
        public Point second;
        public float distance;

        public Point getFirst() {
            return first;
        }

        public void setFirst(Point first) {
            this.first = first;
        }

        public Point getSecond() {
            return second;
        }

        public void setSecond(Point second) {
            this.second = second;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public TwoPointDistance(Point first, Point second, float distance) {
            this.first = first;
            this.second = second;
            this.distance = distance;
        }

        public TwoPointDistance() {
        }

        @Override
        public String toString() {
            return "TwoPointDistance{" +
                    "first=" + first +
                    ", second=" + second +
                    ", distance=" + distance +
                    '}';
        }
    }

    class MyComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
            TwoPointDistance u1 = (TwoPointDistance) obj1;
            TwoPointDistance u2 = (TwoPointDistance) obj2;
            if (u1.getDistance() > u2.getDistance()) {
                return 1;
            } else if (u1.getDistance() < u2.getDistance()) {
                return -1;
            } else {
                //利用String自身的排序方法。
                //如果年龄相同就按名字进行排序
//                return u1.name.compareTo(u2.name);
                return 1;
            }
        }
    }

}