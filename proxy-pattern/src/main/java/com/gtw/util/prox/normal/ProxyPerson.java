package com.gtw.util.prox.normal;

import com.gtw.util.prox.model.Person;

/**
 * 手动实现一个静态代理模式
 * 优点:
 *      1.职责清晰:真实角色实现具体业务逻辑,不关心非本职工作
 *      2.能够协调调用者和被调用者，在一定程度上降低了系统的耦合度
 * 缺点:
 *      1.有些类型的代理模式可能会造成请求的处理速度变慢
 *      2.实现代理模式需要额外的工作，有些代理模式的实现非常复杂
 */
public class ProxyPerson implements Person {
    private Person person;

    /**
     * 静态代理需要传入被代理的实际对象
     * @param person 被代理的实际对象
     */
    public ProxyPerson(Person person) {
        this.person = person;
    }

    public String sayHello(String name) {
        System.out.println("代理前面干的事。。。");
        String sayWords = person.sayHello(name);
        System.out.println("代理后面干的事。。。");
        return sayWords;
    }

    public void eat(String food) {
        System.out.println("代理前面干的事。。。");
        person.eat(food);
        System.out.println("代理后面干的事。。。");
    }
}
