package com.gtw.mybatis.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T, PK extends Serializable> {
    /**
     * 查询集合的最大数
     */
    int NO_ROW_LIMIT = 1000;

    /**
     * 根据主键查询数据
     * @param primaryKey 主键
     * @return 实体对象
     */
    T findById(PK primaryKey);
    /**
     * 删除数据
     * @param id 主键ID
     * @return 执行结果0/1
     */
    int delete(PK id);
    /**
     * 保存对象
     * @param entity 实体对象
     * @return 执行结果0/1
     */
    int save(T entity);
    /**
     * 更新对象
     * @param entity 实体对象
     * @return 执行结果0/1
     */
    int update(T entity);
    /**
     * 定制查询
     * @param statementId 查询语句
     * @param parameter 查询参数
     * @return 查询结果
     */
    T queryForObject(String statementId, Object parameter);
    /**
     * 查询对象集合，集合数量最大为1000
     * @param statementId 查询语句
     * @return 对象集合
     */
    List<T> queryForList(String statementId);
    /**
     * 查询对象集合，集合数量最大为1000
     * @param statementId 查询语句
     * @param parameter 查询参数
     * @return 对象集合
     */
    List<T> queryForList(String statementId, Object parameter);
    /**
     * 查询指定行数数据
     * @return 对象集合
     */
    List<T> queryForLimitList(String statementId, PageRequest parameter);
    /**
     * 分页查询
     * @param statementName 查询语句
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    Page<T> pageQuery(String statementName, PageRequest pageRequest);
}
