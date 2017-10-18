package com.gtw.util.transactional.service;

import com.gtw.util.transactional.annotation.Transactional1;
import com.gtw.util.transactional.annotation.Transactional2;
import org.springframework.stereotype.Service;

@Transactional1(rollbackForClassName = "RuntimeException")
@Service
public class UserServiceImpl implements IUserService {

    @Override
    @Transactional1(rollbackForClassName = "SelfException")
    @Transactional2
    public int addUser() {
        System.out.println("添加用户");
        return 0;
    }

    @Override
    @Transactional2
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
