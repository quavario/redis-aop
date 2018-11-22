package com.controller;

import com.pojo.User;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 找到用户
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public User findOne(Integer id) {
        return userService.findOne(id);
    }


    /**
     * 保存用户
     *
     * @param user
     */
    @RequestMapping("/saveUser")
    public void save(User user) {
        userService.save(user);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<User> findAll() {
        return userService.findAll();
    }
}
