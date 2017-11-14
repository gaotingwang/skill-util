package com.gtw.replication.controller;

import com.gtw.replication.domain.User;
import com.gtw.replication.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.queryPage(0, 3);
    }

    @GetMapping(value = "/users/{userId}")
    public User getUser(@PathVariable("userId") Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/users")
    public Long save(@RequestBody User user) {
        return userService.save(user);
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
