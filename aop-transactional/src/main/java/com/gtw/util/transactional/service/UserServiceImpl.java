package com.gtw.util.transactional.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Override
    public int addUser() {
        System.out.println("添加用户");
        return 0;
    }

    @Override
    public int updateUser() throws Exception {
        System.out.println("修改用户");
        throw new Exception("检查型异常");
    }

    @Override
    public int deleteUser() {
        System.out.println("删除用户");
        throw new RuntimeException("运行时异常");
    }
}
