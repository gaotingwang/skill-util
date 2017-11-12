//package com.gtw.split.routsource.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.ParameterMapping;
//import org.apache.ibatis.mapping.ParameterMode;
//import org.apache.ibatis.plugin.*;
//import org.apache.ibatis.reflection.MetaObject;
//import org.apache.ibatis.session.Configuration;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//import org.apache.ibatis.type.TypeHandlerRegistry;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import java.util.regex.Matcher;
//
///**
// * 将mybatis要执行的sql拦截打印出来
// */
//@Intercepts
//({
//    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
//    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
//})
//@Slf4j
//public class SqlPrintInterceptor implements Interceptor {
//    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    /*@Bean(name="sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactorys() throws Exception {
//        log.info("--------------------  sqlSessionFactory init ---------------------");
//        try {
//            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//            //     sessionFactoryBean.setDataSource(roundRobinDataSouce);
//            sessionFactoryBean.setDataSource(roundRobinDataSouceProxy());
//
//            // 读取配置
//            sessionFactoryBean.setTypeAliasesPackage("com.fei.springboot.domain");
//
//            //设置mapper.xml文件所在位置
//            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
//            sessionFactoryBean.setMapperLocations(resources);
//            //设置mybatis-config.xml配置文件位置
//            sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
//
//            //添加分页插件、打印sql插件
//            Interceptor[] plugins = new Interceptor[]{pageHelper(),new SqlPrintInterceptor()};
//            sessionFactoryBean.setPlugins(plugins);
//
//            return sessionFactoryBean.getObject();
//        } catch (IOException e) {
//            log.error("mybatis resolver mapper*xml is error",e);
//            return null;
//        } catch (Exception e) {
//            log.error("mybatis sqlSessionFactoryBean create error",e);
//            return null;
//        }
//    }*/
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//        Object parameterObject = null;
//        if (invocation.getArgs().length > 1) {
//            parameterObject = invocation.getArgs()[1];
//        }
//
//        long start = System.currentTimeMillis();
//
//        Object result = invocation.proceed();
//
//        String statementId = mappedStatement.getId();
//        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
//        Configuration configuration = mappedStatement.getConfiguration();
//        String sql = getSql(boundSql, parameterObject, configuration);
//
//        long end = System.currentTimeMillis();
//        long timing = end - start;
//        if(log.isInfoEnabled()){
//            log.info("执行sql耗时:" + timing + " ms" + " - id:" + statementId + " - Sql:" );
//            log.info("   "+sql);
//        }
//
//        return result;
//    }
//
//    @Override
//    public Object plugin(Object target) {
//        if (target instanceof Executor) {
//            return Plugin.wrap(target, this);
//        }
//        return target;
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//    }
//
//    private String getSql(BoundSql boundSql, Object parameterObject, Configuration configuration) {
//        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
//        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
//        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
//        if (parameterMappings != null) {
//            for (ParameterMapping parameterMapping : parameterMappings) {
//                if (parameterMapping.getMode() != ParameterMode.OUT) {
//                    Object value;
//                    String propertyName = parameterMapping.getProperty();
//                    if (boundSql.hasAdditionalParameter(propertyName)) {
//                        value = boundSql.getAdditionalParameter(propertyName);
//                    } else if (parameterObject == null) {
//                        value = null;
//                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
//                        value = parameterObject;
//                    } else {
//                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
//                        value = metaObject.getValue(propertyName);
//                    }
//                    sql = replacePlaceholder(sql, value);
//                }
//            }
//        }
//        return sql;
//    }
//
//    private String replacePlaceholder(String sql, Object propertyValue) {
//        String result;
//        if (propertyValue != null) {
//            if (propertyValue instanceof String) {
//                result = "'" + propertyValue + "'";
//            } else if (propertyValue instanceof Date) {
//                result = "'" + DATE_FORMAT.format(propertyValue) + "'";
//            } else {
//                result = propertyValue.toString();
//            }
//        } else {
//            result = "null";
//        }
//        return sql.replaceFirst("\\?", Matcher.quoteReplacement(result));
//    }
//}
