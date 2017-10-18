package com.gtw.util.transactional.service;

public interface IUserService {
    int addUser();

    int updateUser() throws Exception;

    int deleteUser();
}
