package com.gtw.util.transactional.service;

import com.gtw.util.transactional.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("userService1")
@Transactional
public class UserServiceImpl1 implements IUserService {

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
