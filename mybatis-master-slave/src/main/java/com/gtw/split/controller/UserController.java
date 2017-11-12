package com.gtw.split.controller;

import com.github.pagehelper.PageInfo;
import com.gtw.split.domain.User;
import com.gtw.split.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public PageInfo<User> getUsers() {
        return userService.queryPage(1, 3);
    }

    @GetMapping(value = "/users/{userId}")
    public User getUser(@PathVariable("userId") Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/users")
    public void save(@RequestBody User user) {
        userService.save(user);
    }

    @PostMapping(value="/writeUsers")
    public void writeAndRead(@RequestBody User user) {
        userService.writeAndRead(user);
    }

    @DeleteMapping(value="/users/{userId}")
    public void delete(@PathVariable("userId") Long id) {
        userService.delete(id);
    }

}
