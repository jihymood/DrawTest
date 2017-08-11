package com.example.hasee.drawtest.model;

import java.util.Comparator;

/**
 * Created by HASEE on 2017/7/24 16:23
 * 比较器：对吸附点集合进行从小到大排序，选中距离最近的吸附点对象
 */

public class MyComparator implements Comparator<TwoPointDistance> {

    @Override
    public int compare(TwoPointDistance o1, TwoPointDistance o2) {
        return (int) (o1.getDistance() - o2.getDistance());
    }
}
