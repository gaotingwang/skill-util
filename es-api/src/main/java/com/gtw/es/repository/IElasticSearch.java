package com.gtw.es.repository;

import com.gtw.es.model.Page;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * es常用方法接口
 */
public interface IElasticSearch {
    String save(Object obj) throws IOException;
    String save(String source) throws IOException;
    String save(String id, String source) throws IOException;
    void update(String id, String source) throws IOException;
    String update(byte[] source, String id) throws IOException;
    List searchByIds(String... ids) throws IOException;
    Page termSearchForPage(Page page, String termName, String termValue) throws IOException;
    List termSearchForList( Map<String ,Object> mapQuery) throws IOException;
    void deleteEsById( String id) throws IOException;
    void createIndexType();
}
