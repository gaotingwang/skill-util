package com.gtw.util.transactional.service;

import com.gtw.util.transactional.annotation.Transactional;
import com.gtw.util.transactional.exception.MyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("userService2")
public class UserServiceImpl2 implements IUserService {

    @Override
    public int addUser() {
        test();
        return 1;
    }

    @Override
    @Transactional
    public int updateUser() throws Exception {
        System.out.println("另一个修改用户");
        throw new MyException("检测型异常");
    }

    @Override
    @Transactional
    public void deleteUser() {
        System.out.println("删除用户");
    }

    @Transactional
    private void test(){
        System.out.println("test");
    }
}
