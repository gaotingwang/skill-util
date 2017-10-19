package com.gtw.util.transactional.service;

import com.gtw.util.transactional.annotation.Transactional;
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
    public int updateUser() throws Exception {
        return 0;
    }

    @Override
    @Transactional
    public int deleteUser() {
        return 0;
    }

    @Transactional
    private void test(){
        System.out.println("test");
    }
}
