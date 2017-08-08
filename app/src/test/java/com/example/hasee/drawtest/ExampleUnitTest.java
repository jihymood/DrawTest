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

        paixu();
        removeYuansu();


    }

    public void paixu() {
        Point point = new Point(11, 12);
        Point point1 = new Point(11, 12);
        TwoPointDistance u1 = new TwoPointDistance(point, point1, 63);
        TwoPointDistance u2 = new TwoPointDistance(point, point1, 23);
        TwoPointDistance u3 = new TwoPointDistance(point, point1, 73);
        TwoPointDistance u4 = new TwoPointDistance(point, point1, 32);
        TwoPointDistance u5 = new TwoPointDistance(point, point1, 30);

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

    public void setYuansu() {
        List<String> list = new ArrayList<String>();
        list.add("保护环境");  //向列表中添加数据
        list.add("爱护地球");  //向列表中添加数据
        list.add("从我做起");  //向列表中添加数据
        String ret = list.set(1, "少用塑料袋");
        System.out.println("获取索引位置1替换前的内容：" + ret);
        //通过循环输出列表中的内容
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ":" + list.get(i));
        }
    }

    public void removeYuansu() {
        List<String> list = new ArrayList<String>();
        list.add("保护环境");  //向列表中添加数据
        list.add("爱护地球");  //向列表中添加数据
        list.add("从我做起");  //向列表中添加数据

        List<String> otherList = new ArrayList<String>();
        for (String s : list) {
            otherList.add(s);
        }
        list.remove(1);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ":" + list.get(i));
        }
        for (String s : otherList) {
            System.out.println(s);
        }

    }

    public void fuzhi() {
        List<Girl> otherGirlList = new ArrayList<>();
        List<Girl> girlList = new ArrayList<>();
        girlList.add(new Girl(12, "小萝莉", "北京"));
        girlList.add(new Girl(15, "静静", "南京"));
        girlList.add(new Girl(22, "大兰", "连云港"));
        girlList.add(new Girl(20, "夏夏", "滁州"));
        girlList.add(new Girl(20, "静静", "南京"));

        for (Girl girl : girlList) {  //复制一份集合
            otherGirlList.add(girl);
        }

        for (int i = 0; i < girlList.size(); i++) {
            Girl girl = girlList.get(i);
            String name = girl.getName();
            if (name == "静静") {
                otherGirlList.remove(i);
            }
        }
    }

    public void fuzhi1() {
        List<Girl> otherGirlList = new ArrayList<>();
        List<Girl> girlList = new ArrayList<>();
        girlList.add(new Girl(12, "小萝莉", "北京"));
        girlList.add(new Girl(15, "静静", "南京"));
        girlList.add(new Girl(22, "大兰", "连云港"));
        girlList.add(new Girl(20, "夏夏", "滁州"));

        for (Girl girl : girlList) {
            String name = girl.getName();
            if (name != "静静") {
                otherGirlList.add(girl);
            }
        }

        for (Girl girl : otherGirlList) {
            System.out.print(girl.toString() + "\n");
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