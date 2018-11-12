package com.gtw.es.model;

import lombok.Data;

import java.util.List;

/**
 * 分页对象
 */
@Data
public class Page<T> {
    /**
     * 页码
     */
    private int pageNum;
    /**
     * 每页记录数
     */
    private int size;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 每页记录
     */
    private List<T> detail;
}
