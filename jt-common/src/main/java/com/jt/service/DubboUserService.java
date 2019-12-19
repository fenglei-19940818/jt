package com.jt.service;

import com.jt.pojo.User;

public interface DubboUserService {
    Integer insertUser(User user);

    String findUserByUP(String username, String password, String ip);
}
