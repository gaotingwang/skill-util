package com.gtw.split.config.source;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源类型
 */
@AllArgsConstructor
public enum DataSourceType {
    WRITE("write", "主库"),READ("read", "从库");

    @Getter
    private String type;
    @Getter
    private String name;
}
