package com.gtw.split.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 配置多数据源
 */
@Configuration
@Slf4j
public class DataSourceConfiguration {

    /**
     * 此处采用 com.alibaba.druid.pool.DruidDataSource
     */
    @Value("${demo.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    /**
     * 写库主数据源
     */
    @Primary
    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix="demo.datasource.master")
    public DataSource masterDataSource() {
        log.info("-------------------- Master Data Source Init ---------------------");
//        return DataSourceBuilder.create().type(dataSourceType).build();
        return new com.alibaba.druid.pool.DruidDataSource(); // 直接返回DruidDataSource数据源是不是更好
    }

    /**
     * 从数据源1
     */
    @Bean(name = "readDataSource1")
    @Qualifier("readDataSource1")
    @ConfigurationProperties(prefix="demo.datasource.slave1")
    public DataSource readDataSource1() {
        log.info("-------------------- Slave Data Source1 Init ---------------------");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    /**
     * 从数据源2
     */
    @Bean(name = "readDataSource2")
    @Qualifier("readDataSource2")
    @ConfigurationProperties(prefix="demo.datasource.slave2")
    public DataSource readDataSource2() {
        log.info("-------------------- Slave Data Source2 Init ---------------------");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }
}
