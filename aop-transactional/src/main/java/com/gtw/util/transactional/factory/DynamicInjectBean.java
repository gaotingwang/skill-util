//package com.gtw.util.transactional.factory;
//
//import com.gtw.util.transactional.service.IUserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * 动态注册Bean到容器中
// */
//@Component
//public class DynamicInjectBean implements ApplicationContextAware {
//    private Logger logger = LoggerFactory.getLogger(DynamicInjectBean.class);
//    private DefaultListableBeanFactory beanFactory = null;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationcontext)
//            throws BeansException {
//        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationcontext;
//        beanFactory = (DefaultListableBeanFactory) configurableApplicationContext
//                .getBeanFactory();
//    }
//
//    public void inject(Class classzz) {
//        try {
//            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactory.class);
//            beanFactory.registerBeanDefinition(classzz.getName(),
//                    builder.getBeanDefinition());
//            logger.info("初始化时注册" + classzz.getName() + "到Spring容器中...");
//        } catch (Exception e) {
//            throw new RuntimeException("创建service" + classzz.getSimpleName()
//                    + "异常:", e);
//        }
//    }
//
//    @PostConstruct
//    public void init() {
//        Class classzz = IUserService.class;
//        inject(classzz);
//    }
//}
