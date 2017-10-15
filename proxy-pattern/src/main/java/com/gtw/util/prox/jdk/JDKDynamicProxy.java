package com.gtw.util.prox.jdk;

import com.gtw.util.prox.model.Person;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK的动态代理是基于接口的
 * 每一个动态代理类都必须要实现InvocationHandler这个接口，并且每个代理类的实例都关联到了一个handler
 */
public class JDKDynamicProxy implements InvocationHandler {
    /**
     * 这个就是我们要代理的真实对象
     */
    private Object subject;

    /**
     * 创建代理对象
     * @param target 被代理的对象
     * @return 代理对象
     */
    public Object newProxyInstance(Object target){
        this.subject = target;
		/*
		 * 通过Proxy的newProxyInstance方法来创建我们的代理对象，看看其三个参数
 		 * 第一个参数handler.getClass().getClassLoader()，这里使用handler这个类的ClassLoader对象来加载我们的代理对象
		 * 第二个参数realSubject.getClass().getInterfaces()，这里为代理对象提供的接口是真实对象所实行的接口，表示我要代理的是该真实对象，这样就能调用这组接口中的方法了
		 * 第三个参数handler，我们这里将这个代理对象关联到了上方的 InvocationHandler 这个对象上
		 *
		 * Proxy.newProxyInstance 创建的代理对象是在jvm运行时动态生成的一个对象，它并不是我们的InvocationHandler类型
		 * 也不是我们定义的那组接口的类型，而是在运行是动态生成的一个对象，并且命名方式都是这样的形式，以$开头，proxy为中，最后一个数字表示对象的标号。
		 */
        return Proxy.newProxyInstance(this.subject.getClass().getClassLoader(),
                // 传递的是实现类所实现的接口 targetObject.getClass().getInterfaces()，所以JDK只能对于接口进行做代理
                this.subject.getClass().getInterfaces(),
                this);
    }

    /**
     * 当我们通过代理对象调用一个方法的时候，这个方法的调用就会被转发为由InvocationHandler这个接口的invoke方法来进行调用。
     * @param proxy 指代我们所代理的那个真实对象
     * @param method 指代的是我们所要调用真实对象的某个方法的Method对象
     * @param args 指代的是调用真实对象某个方法时接受的参数
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在代理真实对象前我们可以添加一些自己的操作
        System.out.println("前置代理");
        System.out.println("Method:" + method);
        // 当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
        method.invoke(subject, args);
        // 在代理真实对象后我们也可以添加一些自己的操作
        System.out.println("后置代理");
        return null;
    }
}
