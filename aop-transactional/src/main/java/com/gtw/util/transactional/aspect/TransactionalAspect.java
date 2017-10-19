package com.gtw.util.transactional.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TransactionalAspect {
    /**
     * 定义一个切点Pointcut，在有Transactional的地方加入切面
     * Pointcut定义了“何处”，切点有助于缩小切面所通知的连接点范围
     * ("execution(* com.test.spring.aop.pointcutexp..JoinPointObjP2.*(..))")
     * ("within(com.test.spring.aop.pointcutexp..*)") pointcutexp包和子包里的任意类
     * ("this(com.test.spring.aop.pointcutexp.Intf)") 实现了Intf接口的所有类,如果Intf不是接口,限定Intf单个类.
     * ("target(com.test.spring.aop.pointcutexp.Intf)")
     * ("args(String)") 参数为String类型(运行是决定)的方法
     * ("@within(org.springframework.transaction.annotation.Transactional)") 带有Transactional注解的类的所有方法
     * ("@annotation(org.springframework.transaction.annotation.Transactional)") 带有Transactional注解的方法
     * ("@args(org.springframework.transaction.annotation.Transactional)") 参数带有@Transactional标注的方法
     */
    @Pointcut("@within(com.gtw.util.transactional.annotation.Transactional) || @annotation(com.gtw.util.transactional.annotation.Transactional)")
    private void transactionalPoint() { }

    /**
     * 定制一个环绕通知
     * @param joinPoint 连接点是在应用执行过程中能够插入切面的一给点。
     * JoinPoint这个点可以是调用方法时，抛出异常时，甚至修改一个字段时。
     * 切面代码可以利用这些点插入到应用的正常流程中，并添加新的行为。
     */
    @Around("transactionalPoint()")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知之开始");
        Object obj = joinPoint.proceed();
        System.out.println("环绕通知之结束");
        return obj;
    }

    /**
     * 在进入切点之前执行
     */
    @Before("transactionalPoint()")
    public void open() {
        System.out.println("-----开启事务-----");
    }

    /**
     * 退出切点之后执行
     */
    @After("transactionalPoint()")
    public void close() {
        System.out.println("-----提交事务-----");
        System.out.println("-----关闭事务-----");
    }

    /**
     * 执行遇到异常执行
     */
    @AfterThrowing(pointcut = "transactionalPoint()", throwing = "error")
    public void rollback(JoinPoint joinPoint, Throwable error){
        if(error instanceof RuntimeException){
            System.out.println("-----运行时异常，回滚事务-----");
        }else {
            System.out.println("-----非运行时异常，不回滚事务-----");
        }
        System.out.println("-----关闭事务-----");
    }

    /**
     * 获取返回结果，对返回结果可以做一些操作
     * 是在after之后执行，若发生异常这个方法不会被执行
     */
    @AfterReturning(pointcut = "transactionalPoint()", returning = "returnVal")
    public void close(Object returnVal){
        System.out.println("返回结果:" + returnVal);
    }

}
