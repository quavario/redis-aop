package com.service;

import com.pojo.User;

import java.util.List;

public interface UserService {
    public User findOne(Integer id);

    void save(User user);

    List<User> findAll();
}
