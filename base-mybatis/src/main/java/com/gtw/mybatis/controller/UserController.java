package com.gtw.mybatis.controller;

import com.gtw.mybatis.domain.User;
import com.gtw.mybatis.repository.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final IUserDao userDao;

    @Autowired
    public UserController(IUserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping(value = "/users/{userId}")
    public User getUser(@PathVariable("userId") Long id) {
        return userDao.findById(id);
    }

    @PostMapping("/users")
    public void save(User user) {
        userDao.save(user);
    }

    @PutMapping(value="/users/{userId}")
    public void update(User user) {
        userDao.update(user);
    }

    @DeleteMapping(value="/users/{userId}")
    public void delete(@PathVariable("userId") Long id) {
        userDao.delete(id);
    }

}
