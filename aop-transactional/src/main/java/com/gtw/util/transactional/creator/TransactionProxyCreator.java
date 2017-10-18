package com.gtw.util.transactional.creator;

import com.gtw.util.transactional.annotation.Transactional1;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 当Spring启动并实例化该类的时候，会调用afterPropertiesSet方法
 */
@Component
public class TransactionProxyCreator implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private List<String> transactionalRollBackAttributeValues = new ArrayList<>();


    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scanClassTransactional();
        scanMethodTransactional();
        System.out.println(transactionalRollBackAttributeValues);
    }

    /**
     * 查找用 Transactional1 注解的类
     */
    private void scanClassTransactional() throws BeansException {
        // 获取标有Transactional1注解的类
        final Map<String, Object> transactionalMap = applicationContext.getBeansWithAnnotation(Transactional1.class);
        for (final Object transactional : transactionalMap.values()) {
            final Class<? extends Object> transactionalClass = transactional.getClass();
            // 获取类上Transactional1注解的属性值
            final Transactional1 transactionalAnnotation = transactionalClass.getAnnotation(Transactional1.class);
            if(transactionalAnnotation != null) {
                transactionalRollBackAttributeValues.addAll(Arrays.asList(transactionalAnnotation.rollbackForClassName()));
            }
        }
    }

    /**
     * 查找用 Service 注解的类下面用 Transactional1 注解的方法
     */
    private void scanMethodTransactional() throws BeansException{
        // 获取标有Service注解的类
        final Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(Service.class);
        for (final Object serviceObject : serviceMap.values()) {
            final Class<? extends Object> serviceClass = serviceObject.getClass();
            for (Method method : serviceClass.getDeclaredMethods()) {
                // 判断方法中是否有Transactional1注解，有获取注解属性值
                Transactional1 transactionalAnnotation =  method.getAnnotation(Transactional1.class);
                if (transactionalAnnotation != null) {
                    transactionalRollBackAttributeValues.addAll(Arrays.asList(transactionalAnnotation.rollbackForClassName()));
                }
            }
        }
    }
}
