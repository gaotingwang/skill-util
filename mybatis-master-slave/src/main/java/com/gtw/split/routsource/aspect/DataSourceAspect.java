package com.gtw.split.routsource.aspect;

import com.gtw.split.routsource.config.source.DataSourceContextHolder;
import com.gtw.split.routsource.config.source.DataSourceType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * 数据源选择切面
 */
@Order(1) // 必须在事务@EnableTransactionManagement 之前执行，所以order的值越小，越先执行
@Component
@Aspect
public class DataSourceAspect {

    /*
    此中方法废除，因为在dao层决定数据源，service层就不能使用事务，否则出问题
    因为在service打开事务打开时，就会调用determineCurrentLookupKey()决定数据库源，而此时还没执行到dao层此处的aop拦截，还未设置LookupKey
    @Before("execution(* com.gtw.split.mapper.*.find*(..)) || execution(* com.gtw.split.mapper.*.get*(..))")
    public void setReadDataSourceType() {
        DataSourceContextHolder.readSource();
    }

    @Before("execution(* com.gtw.split.mapper.*.insert*(..)) || execution(* com.gtw.split.mapper.*.update*(..))")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.writeSource();
    }*/

    @Before("@annotation(com.gtw.split.routsource.annotation.WriteDataSource)")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.writeSource();
    }

    @Before("@annotation(com.gtw.split.routsource.annotation.ReadDataSource)")
    public void setReadDataSourceType() {
        // 如果已经开启写事务了，继续使用写库，即之后的所有读都从写库读
        if(!DataSourceType.WRITE.getType().equals(DataSourceContextHolder.getJdbcType())){
            DataSourceContextHolder.readSource();
        }
    }

    /**
     * 判断@Transactional是否用了readOnly = true属性，如果是选从库，否则选主库
     */
    @Transactional
    @Before("@within(org.springframework.transaction.annotation.Transactional) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public void dataSourceType4Transactional(JoinPoint joinPoint) throws NoSuchMethodException {
        // 优先使用方法级别的注解
        Transactional methodAnnotation = this.getMethodTransactional4JoinPoint(joinPoint);
        if(methodAnnotation != null){
            if(methodAnnotation.readOnly()){
                DataSourceContextHolder.readSource();
            }else {
                DataSourceContextHolder.writeSource();
            }
        }else {
            // 方法级别没有注解，则获取类级别注解
            Transactional classTransactional = this.getClassTransactional4JoinPoint(joinPoint);
            if(classTransactional != null){
                if(classTransactional.readOnly()){
                    DataSourceContextHolder.readSource();
                }else {
                    DataSourceContextHolder.writeSource();
                }
            }
            // 如果classTransactional也为空，不设置数据库类型，交由RoutingDataSource决定在
        }
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
     * 获取连接点对应类上的Transactional
     * @param joinPoint 连接点
     * @return 连接点对应的类
     */
    private Transactional getClassTransactional4JoinPoint(JoinPoint joinPoint) {
        Class targetClass = joinPoint.getTarget().getClass();
        return (Transactional) targetClass.getAnnotation(Transactional.class);
    }

}
