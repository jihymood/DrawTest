package com.example.hasee.drawtest.model;

import java.util.Comparator;

/**
 * Created by HASEE on 2017/7/24 16:23
 */

public class MyComparator implements Comparator<TwoPointDistance> {

    @Override
    public int compare(TwoPointDistance o1, TwoPointDistance o2) {
        return (int) (o1.getDistance() - o2.getDistance());
    }
}
