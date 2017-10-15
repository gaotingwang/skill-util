package com.gtw.util.prox.model;

public class Chinese implements Person {

    public String sayHello(String name) {
        System.out.println("-- 正在执行 sayHello 方法 --");
        return name;
    }

    public void eat(String food) {
        System.out.println("我正在吃 :"+ food);
    }
}
