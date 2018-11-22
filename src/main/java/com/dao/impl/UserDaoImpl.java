package com.dao.impl;

import com.dao.UserDao;
import com.pojo.User;
import org.junit.Test;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private static Connection connection = null;
    private static PreparedStatement statement = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://192.168.152.128/test", "root", "123");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findOne(Integer id) {

        String sql = "select * from user where id = ?";
        User user = null;
        try {

            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            user = new User();
            while (resultSet.next()) {

                String username = resultSet.getString("username");
                Integer age = resultSet.getInt("age");

                user.setId(id);
                user.setUsername(username);
                user.setAge(age);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<User> findAll() {
        String sql = "select * from user";
        List<User> list = new ArrayList<User>();
        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                Integer age = resultSet.getInt("age");

                User user = new User();
                user.setId(id);
                user.setUsername(username);
                user.setAge(age);
                list.add(user);

            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save(User user) {
        String sql = "insert into user values(null,?,?)";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setInt(2, user.getAge());

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
