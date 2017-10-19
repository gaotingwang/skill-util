package com.gtw.util.transactional.aspect;

import com.gtw.util.transactional.annotation.Transactional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 定义切面：Pointcut + Advice
 * Pointcut: 切入点，指定"何处"进行代理
 * Advice: 通知，在指定点增加具体代理功能
 *      1.@Before 在连接点（JoinPoint）之前执行的通知
 *      2.@After 在连接点（JoinPoint）退出的时候执行的通知，无论是否有异常都会执行
 *      3.@Around 包围一个连接点（JoinPoint）的通知，连接点之前执行的内容会先于@Before指定的内容，连接点之后执行的内容会先于@After指定的内容
 *      4.@AfterReturning 获取到返回结果之后执行，在@After之后执行，不包括发生异常
 *      5.@AfterThrowing 发生异常之后执行，在@After之后执行
 */
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
    // 通过@within和@annotation指定在方法和类级别同时开启
    @Pointcut("@within(com.gtw.util.transactional.annotation.Transactional) || @annotation(com.gtw.util.transactional.annotation.Transactional)")
    private void transactionalPoint() { }

    /**
     * 定制一个环绕通知
     * @param joinPoint 连接点是在应用执行过程中能够插入切面的一给点。
     * JoinPoint这个点可以是调用方法时，抛出异常时，甚至修改一个字段时。
     * 切面代码可以利用这些点插入到应用的正常流程中，并添加新的行为。
     */
    @Around("transactionalPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("-----开启事务-----");
        Object obj = joinPoint.proceed();
        System.out.println("-----提交事务-----");
        return obj;
    }

    /**
     * 退出切点之后执行
     */
    @After("transactionalPoint()")
    public void close() {
        System.out.println("-----关闭事务-----");
    }

    /**
     * 执行遇到异常执行
     */
    @AfterThrowing(pointcut = "transactionalPoint()", throwing = "error")
    public void rollback(JoinPoint joinPoint, Throwable error) throws Exception {
        if(error instanceof RuntimeException){
            System.out.println("-----运行时异常，回滚事务-----");
        }else {
            // 获取抛出异常的名称
            String exceptionName = error.getClass().getSimpleName();

            // 优先使用方法级别的注解
            Transactional methodAnnotation = this.getMethodTransactional4JoinPoint(joinPoint);
            if(isRollback4ExceptionName(exceptionName, methodAnnotation)){
                System.out.println("-----指定异常，回滚事务-----");
            }else {
                // 方法级别没有注解，则获取类级别注解
                Transactional classTransactional = this.getClassTransactional4JoinPoint(joinPoint);
                if(isRollback4ExceptionName(exceptionName, classTransactional)){
                    System.out.println("-----指定异常，回滚事务-----");
                }else {
                    System.out.println("-----非运行时异常，不回滚事务-----");
                }
            }
        }

        System.out.println("-----关闭事务-----");
    }

    /**
     * 是否为指定异常名称回滚
     * @param exceptionName 异常名称
     * @param transactional transactionalAnnotation
     * @return 是否为指定异常名称回滚
     */
    private boolean isRollback4ExceptionName(String exceptionName, Transactional transactional) {
        if(transactional != null) {
            String rollbackNames[] = transactional.rollbackForClassName();
            if(rollbackNames != null) {
                List<String> rollbackFor = Arrays.asList(rollbackNames);
                return rollbackFor.contains(exceptionName);
            }
        }
        return false;
    }

    /**
     * 获取连接点对应方法上的Transactional
     * @param joinPoint 连接点
     * @return 连接点对应的方法
     */
    private Transactional getMethodTransactional4JoinPoint(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature(); // 从接口返回方法
        Method method =  methodSignature.getMethod(); // 如果实现了接口，是返回接口中定义的方法

        Transactional methodAnnotation = method.getAnnotation(Transactional.class);
        if(methodAnnotation == null && method.getDeclaringClass().isInterface()) {
            // 如果接口中的方法上未加注解，则从实现类中寻找注解
            method = joinPoint.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            methodAnnotation = method.getAnnotation(Transactional.class);
        }

        return methodAnnotation;
    }

    /**
     * 获取连接点对应的类
     * @param joinPoint 连接点
     * @return 连接点对应的类
     */
    private Transactional getClassTransactional4JoinPoint(JoinPoint joinPoint) {
        Class targetClass = joinPoint.getTarget().getClass();
        return (Transactional) targetClass.getAnnotation(Transactional.class);
    }

}
