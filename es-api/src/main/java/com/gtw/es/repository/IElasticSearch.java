package com.gtw.es.repository;

import com.gtw.es.model.Page;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * es常用方法接口
 */
public interface IElasticSearch {

    /**
     * 创建es索引type结构
     */
    void createIndexType();

    /**
     * 保存对象到es中，自动生成ID
     */
    String save(Object obj) throws IOException;

    /**
     * 将一个类型化的 JSON 文档索引到一个特定的索引中, 并且使它可以被搜索到
     */
    String save(String source) throws IOException;

    /**
     * 创建时指定ID，若ID存在，保存失败，抛出异常
     */
    String save(String id, String source) throws IOException;

    /**
     * 完全替换es中的document
     */
    void replace(String id, String source) throws IOException;

    /**
     * 修改document中的部分值
     */
    void update(String id, Map<String, Object> newValues) throws Exception;

    /**
     * 删除es中指定ID的记录
     */
    void deleteById(String id) throws IOException;

    /**
     * 根据ID查询document
     */
    String searchById(String id) throws IOException;

    /**
     * 根据ID集合批量查document
     */
    List searchByIds(String... ids) throws IOException;

    Page termSearchForPage(Page page, String termName, String termValue) throws IOException;
    List termSearchForList(Map<String ,Object> mapQuery) throws IOException;
}
