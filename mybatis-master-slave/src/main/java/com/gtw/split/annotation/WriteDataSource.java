package com.gtw.split.annotation;

import java.lang.annotation.*;

/**
 * 选择写库数据源
 * @see com.gtw.split.aspect.DataSourceAspect
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WriteDataSource {
}
