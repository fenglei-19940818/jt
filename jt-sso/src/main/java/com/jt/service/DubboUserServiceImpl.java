package com.jt.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.config.RedisConfig;
import com.jt.mapper.UserMapper;
import com.jt.pojo.User;
import com.jt.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.JedisCluster;

import java.util.UUID;

@Service
public class DubboUserServiceImpl implements DubboUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisCluster jedis;


    @Override
    public Integer insertUser(User user) {
        //获取md5加密的密码
        String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Pass).setEmail(user.getPhone());
        int insert = userMapper.insert(user);
        return insert;
    }

    /**
     * 用户登录往redis中存储登录信息
     *
     * @param username
     * @param password
     * @param ip
     * @return
     */
    @Override
    public String findUserByUP(String username, String password, String ip) {
        //获取密码的md5加密密码
        String md5Pass = DigestUtils.md5DigestAsHex(password.getBytes());
        //判断用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("username", username);
        User userDB = userMapper.selectOne(queryWrapper);
        if (userDB == null || !md5Pass.equals(userDB.getPassword())) {
            return null;
        }
        User user = new User().setId(userDB.getId()).setUsername(userDB.getUsername()).setPassword(UUID.randomUUID().toString());
        //获取UUID
        String uuid = UUID.randomUUID().toString();
        //将user对象存储在redis缓存中
        jedis.hset("JT_USER_" + username, "JT_TICKET", uuid);
        jedis.hset("JT_USER_" + username, "JT_USER", JsonUtil.getBeanToJson(user));
        jedis.hset("JT_USER_" + username, "JT_USER_IP", ip);
        //设置过期时间
        jedis.expire("JT_USER_" + username, 7 * 24 * 60 * 60);
//        //将user对象存储在redis缓存中
//        jedis.hset(uuid, "JT_USER", JsonUtil.getBeanToJson(user));
//        jedis.hset(uuid, "JT_USER_IP", ip);
//        //设置过期时间
//        jedis.expire(uuid, 7 * 24 * 60 * 60);
        return uuid;
    }
}
