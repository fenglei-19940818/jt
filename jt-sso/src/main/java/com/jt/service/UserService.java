package com.jt.service;

import com.jt.pojo.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    Boolean findUserExist(String param, Integer type);

    String queryUserLogin(String ticket,String username, String ip);
}
