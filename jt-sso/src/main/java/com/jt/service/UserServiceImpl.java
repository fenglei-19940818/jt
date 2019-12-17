package com.jt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.mapper.UserMapper;
import com.jt.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {

        List<User> userList = userMapper.selectList(null);
        return userList;
    }

    @Override
    public Boolean findUserExist(String param, Integer type) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        String column;
//        if (type == 1) {
//            column = "username";
//        } else if (type == 2) {
//            column = "phone";
//        } else {
//            column = "email";
//        }

//        String column = type == 1 ? "username" : type == 2 ? "phone" : "email";

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "username");
        map.put(2, "phone");
        map.put(3, "email");
        String column = map.get(type);

        queryWrapper.eq(column, param);
        Integer count = userMapper.selectCount(queryWrapper);

        //判断如果大于0则存在(true)不能注册  反之不存在(false)可以注册
        return count > 0;
    }

    @Override
    @Transactional
    public String register(User user) {
        userMapper.insert(user);
        return user.getUsername();
    }
}
