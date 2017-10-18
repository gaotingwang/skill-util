package com.gtw.util.transactional.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    public void addUser() throws Exception {
        userService.addUser();
    }

    @Test
    public void updateUser() throws Exception {
        userService.updateUser();
    }

    @Test
    public void deleteUser() throws Exception {
        userService.deleteUser();
    }

}