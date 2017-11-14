package com.gtw.replication.service;

import com.github.pagehelper.PageInfo;
import com.gtw.replication.domain.User;

public interface IUserService {
    User getUser(Long id);

    PageInfo<User> queryPage(int pageNum, int pageSize);

    void save(User user);

    void writeAndRead(User user);

    void delete(Long id);
}
