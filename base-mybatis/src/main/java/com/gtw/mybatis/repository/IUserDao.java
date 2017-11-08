package com.gtw.mybatis.repository;

import com.gtw.mybatis.dao.BaseDao;
import com.gtw.mybatis.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserDao extends BaseDao<User, Long> {
}
