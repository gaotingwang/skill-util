package com.gtw.util.transactional.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IUserServiceTest {
    @Autowired
    @Qualifier("userService1")
    private IUserService userService;

    @Autowired
    @Qualifier("userService2")
    private IUserService userService2;

    @Test
    public void addUser1() throws Exception {
        userService.addUser();
    }

    @Test
    public void updateUser1() throws Exception {
        userService.updateUser();
    }

    @Test
    public void deleteUser1() throws Exception {
        userService.deleteUser();
    }

    @Test
    public void addUser2() throws Exception {
        userService2.addUser();
    }

    @Test
    public void updateUser2() throws Exception {
        userService2.updateUser();
    }

    @Test
    public void deleteUser2() throws Exception {
        userService2.deleteUser();
    }

}