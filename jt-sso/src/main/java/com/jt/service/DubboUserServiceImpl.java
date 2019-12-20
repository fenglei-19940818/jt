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
    private JedisCluster jedisCluster;


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

        /**
         * 为了保证redis资源不浪费,则需要校验数据.
         * 如果检查发现当前用户已经登陆过,则删除之前的数据.
         */
        if (jedisCluster.exists("JT_USER_" + username)) {
            //之前已经登录过.删除之前的ticket
            String oldTicket = jedisCluster.get("JT_USER_" + username);
            jedisCluster.del(oldTicket);
        }


        //程序执行到这里说明用户输入正确.
        //3.1获取uuid
        String ticket = UUID.randomUUID().toString();
        //3.2准备userJSON数据  数据必须进行脱敏处理
        userDB.setPassword("123456");
        String userJSON = JsonUtil.getBeanToJson(userDB);
        jedisCluster.hset(ticket, "JT_USER", userJSON);
        jedisCluster.hset(ticket, "JT_USER_IP", ip);
        jedisCluster.expire(ticket, 7 * 24 * 3600);

        //将用户名和ticket信息绑定
        jedisCluster
                .setex("JT_USER_" + username, 7 * 24 * 3600, ticket);

        //用户名和ticket绑定即可!!!!!!
        return ticket;

    }
}
