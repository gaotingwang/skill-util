package com.gtw.split.config.source;

import lombok.extern.slf4j.Slf4j;

/**
 * 本地线程，数据源上下文
 */
@Slf4j
public class DataSourceContextHolder {
    private static final ThreadLocal<String> local = new ThreadLocal<>();

    public static ThreadLocal<String> getLocal() {
        return local;
    }

    /**
     * 写只有一个库
     */
    public static void writeSource() {
        local.set(DataSourceType.WRITE.getType());
        log.info("dataSource切换到：Write");
    }

    /**
     * 读可能是多个从库
     */
    public static void readSource() {
        local.set(DataSourceType.READ.getType());
        log.info("dataSource切换到：Read");
    }

    public static String getJdbcType() {
        return local.get();
    }
}
