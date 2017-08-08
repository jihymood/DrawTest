package com.example.hasee.drawtest;

/**
 * Created by HASEE on 2017/8/7 10:26
 */

public class Girl {

    private int age;
    private String name;
    private String address;

    public Girl(int age, String name, String address) {
        this.age = age;
        this.name = name;
        this.address = address;
    }

    public Girl() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Girl{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
