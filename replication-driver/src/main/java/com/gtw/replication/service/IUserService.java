package com.gtw.replication.service;

import com.gtw.replication.domain.User;

import java.util.List;

public interface IUserService {
    User getUser(Long id);

    List<User> queryPage(int offset, int limit);

    Long save(User user);

    void writeAndRead(User user);

    void delete(Long id);
}
