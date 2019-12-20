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
import java.util.UUID;

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
    public String queryUserLogin(String ticket, String username, String ip) {
        //获取ip和user
        String redisIP = jedis.hget("JT_USER_" + username, "JT_USER_IP");
        String userStr = jedis.hget("JT_USER_" + username, "JT_USER");
        String redisTicket = jedis.hget("JT_USER_" + username, "JT_TICKET");
        User user = JsonUtil.getJsonToBean(userStr, User.class);
        //判断ip是否相同或者user是否存在
        if (!ip.equals(redisIP) || user == null || StringUtils.isEmpty(redisTicket) || !redisTicket.equals(ticket)) {
            return null;
        }
        //判断如果存在此KEY代表用户已经登录
        if (jedis.exists("JT_USER_" + username)) {

            //获取UUID
            String uuid = UUID.randomUUID().toString();
            //重写redis缓存中的数据
            jedis.hset("JT_USER_" + username, "JT_TICKET", uuid);
            jedis.hset("JT_USER_" + username, "JT_USER_IP", ip);
            return uuid;
        }

        return null;
    }
}
