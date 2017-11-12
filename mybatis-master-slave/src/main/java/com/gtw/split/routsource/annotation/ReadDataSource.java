package com.gtw.split.routsource.annotation;

import java.lang.annotation.*;

/**
 * 选择从库数据源
 * @see com.gtw.split.routsource.aspect.DataSourceAspect
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReadDataSource {
}
