package com.dao;

import com.pojo.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    public User findOne(Integer id);

    public List<User> findAll();

    public void save(User user);
}
