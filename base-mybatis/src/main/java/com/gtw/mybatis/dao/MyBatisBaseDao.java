package com.gtw.mybatis.dao;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.List;

/**
 * 用MyBatis默认方式，MyBatis会自动为接口创建实现类，此类无用
 * eg:IUserDao.findById() 代理会根据IUserDao.xml中 findById 的sql进行实现调用，根本用不到此类
 * 除非手动写UserDaoImp实现来进行调用 亦或 override源码
 *
 */
public abstract class MyBatisBaseDao<T, PK extends Serializable> implements BaseDao<T, PK> {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 根据主键查询数据
     * @param primaryKey 主键
     * @return 实体对象
     */
    @Override
    public T findById(PK primaryKey) {
        return this.sqlSessionTemplate.selectOne("findById", primaryKey);
    }

    /**
     * 删除数据
     * @param id 主键ID
     * @return 执行结果0/1
     */
    @Override
    public int delete(PK id) {
        return this.sqlSessionTemplate.delete("delete", id);
    }

    /**
     * 保存对象
     * @param entity 实体对象
     * @return 执行结果0/1
     */
    @Override
    public int save(T entity) {
        return this.sqlSessionTemplate.insert("insert", entity);
    }

    /**
     * 更新对象
     * @param entity 实体对象
     * @return 执行结果0/1
     */
    @Override
    public int update(T entity) {
        return this.sqlSessionTemplate.update("update", entity);
    }

    /**
     * 定制查询
     * @param statementId 查询语句
     * @param parameter 查询参数
     * @return 查询结果
     */
    @Override
    public T queryForObject(String statementId, Object parameter) {
        return this.sqlSessionTemplate.selectOne(statementId, parameter);
    }

    /**
     * 查询对象集合，集合数量最大为1000
     * @param statementId 查询语句
     * @return 对象集合
     */
    @Override
    public List<T> queryForList(String statementId) {
        return this.queryForList(statementId, null);
    }

    /**
     * 查询对象集合，集合数量最大为1000
     * @param statementId 查询语句
     * @param parameter 查询参数
     * @return 对象集合
     */
    @Override
    public List<T> queryForList(String statementId, Object parameter) {
        // 此处限制实际不一定是查询全部，如果查询结果数量为10w,数量太过庞大，限制最大查询数为 BaseDao.NO_ROW_LIMIT = 1000
        return this.queryForList(this.sqlSessionTemplate, statementId, parameter);
    }

    /**
     * 查询指定行数数据
     * @return 对象集合
     */
    @Override
    public List<T> queryForLimitList(String statementId, PageRequest parameter) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, parameter.getPageSize());
        return this.sqlSessionTemplate.selectList(statementId, parameter, rowBounds);
    }

    @Override
    public Page<T> pageQuery(String statementName, PageRequest pageRequest) {
        return pageQuery(this.sqlSessionTemplate, statementName, statementName + "_count", pageRequest);
    }

    /**
     * 查询有限数据，最大可以查询1000条
     * @param sqlSession sqlSession
     * @param statementId 查询语句
     * @param parameter 查询参数
     * @return 数据集合
     */
    private List<T> queryForList(SqlSession sqlSession, String statementId, Object parameter) {
        // 通过RowBounds采用的是逻辑分页：将数据库中所有数据全部取出，然后通过Java代码控制分页逻辑。
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, BaseDao.NO_ROW_LIMIT);
        return sqlSession.selectList(statementId, parameter, rowBounds);
    }

    private Page<T> pageQuery(SqlSession sqlSessionTemplate, String statementName, String countStatementName, PageRequest pageRequest) {
        return null;
    }

}
