package com.jt.service;

//import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.mapper.UserMapper;
import com.jt.pojo.User;
import com.jt.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JedisCluster jedis;

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
    public String queryUserLogin(String ticket, String ip) {
        String redisIP = jedis.hget(ticket, "JT_USER_IP");
        String userStr = jedis.hget(ticket, "JT_USER");
        User user = JsonUtil.getJsonToBean(userStr, User.class);
        //对比当前访问IP和redis存储的IP是否一样
        if (!ip.equals(redisIP) || user == null) {
            return null;
        }

        return user.getUsername();
    }
}
