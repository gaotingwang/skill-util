package com.gtw.util.prox;

import com.gtw.util.prox.cglib.CglibDynamicProxy;
import com.gtw.util.prox.jdk.JDKDynamicProxy;
import com.gtw.util.prox.model.American;
import com.gtw.util.prox.model.Chinese;
import com.gtw.util.prox.model.Person;
import com.gtw.util.prox.normal.ProxyPerson;

public class ProxyMain {

    public static void main(String[] args) {
        // 普通静态代理
        System.out.println("##############################");
        Person proxyPerson = new ProxyPerson(new Chinese());
        System.out.println("静态代理：" + proxyPerson.getClass().getName());
        proxyPerson.sayHello("张三");
        System.out.println();
        proxyPerson.eat("苹果");
        System.out.println("##############################");

        // 基于jdk的动态代理
        JDKDynamicProxy handler = new JDKDynamicProxy();
        Person proxyChinese = (Person) handler.newProxyInstance(new Chinese());
        System.out.println("JDK动态代理：" + proxyChinese.getClass().getName());
        // 通过代理对象来调用方法的时候，起实际就是委托由其关联到的 handler 对象的invoke方法中来调用，并不是自己来真实调用，而是通过代理的方式来调用的。
        proxyChinese.sayHello("李四");
        System.out.println();
        proxyChinese.eat("香蕉");
        System.out.println("##############################");

        // 基于cglib的动态代理
        CglibDynamicProxy cglib = new CglibDynamicProxy();
        American proxyAmerican = (American) cglib.getProxyInstance(new American());
        System.out.println("CGLIB动态代理：" + proxyAmerican.getClass().getName());
        // proxyAmerican 是通过Enhancer动态生成的American对象
        proxyAmerican.sayHello("Tom");
        System.out.println();
        proxyAmerican.eat("apple");
        System.out.println("##############################");
    }
}
