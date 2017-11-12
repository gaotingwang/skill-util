package com.gtw.split.mapper;

import com.gtw.split.domain.User;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface UserMapper {
    List<User> getAll();

    /**
     * 通过RowBounds采用的是逻辑分页：将数据库中所有数据全部取出，然后通过Java代码控制分页逻辑。
     */
    List<User> getAll(RowBounds rb);

    User getOne(Long id);

    void insert(User user);

    void update(User user);

    void delete(Long id);

}
