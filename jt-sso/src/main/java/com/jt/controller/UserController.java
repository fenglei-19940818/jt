package com.jt.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.pojo.User;
import com.jt.service.UserService;
import com.jt.util.CookieUtil;
import com.jt.util.IPUtil;
import com.jt.util.JsonUtil;
import com.jt.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JedisCluster jedisCluster;

    @RequestMapping("/findAll")
    public List<User> findAll() {
        List<User> userList = userService.findAll();
        return userList;
    }

    /**
     * 查询用户是否登录(单点登录)
     *
     * @param ticket
     * @param callback
     * @return
     */
    @RequestMapping(value = "/query/{ticket}/{username}", method = RequestMethod.GET)
    public JSONPObject queryUserLogin(@PathVariable String ticket, @PathVariable String username, String callback, HttpServletRequest request, HttpServletResponse response) {
        JSONPObject object = null;

        //校验ticket是否有效 从redis中获取最终的ticket完成校验
        String redisTicket = jedisCluster.get("JT_USER_" + username);
        if (StringUtils.isEmpty(redisTicket)) {
            //IP地址不正确.
            object = new JSONPObject(callback, SysResult.fail());
            //删除cookie信息
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            return object;
        }

        //如果数据不相等,说明数据有误,不能展现.
        if (!redisTicket.equals(ticket)) {

            //IP地址不正确.
            object = new JSONPObject(callback, SysResult.fail());
            //删除cookie信息
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            return object;
        }


        //校验IP地址
        String IP = IPUtil.getIpAddr(request);
        Map<String, String> map = jedisCluster.hgetAll(ticket);

        //1.校验IP是否有效.
        if (!IP.equals(map.get("JT_USER_IP"))) {

            //IP地址不正确.
            object = new JSONPObject(callback, SysResult.fail());
            //删除cookie信息
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            //删除cookie信息
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            return object;
        }

        //2.校验ticket数据信息.
        String userJSON = map.get("JT_USER");
        if (StringUtils.isEmpty(userJSON)) {

            //IP地址不正确.
            object = new JSONPObject(callback, SysResult.fail());
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            return object;
        }

//        Map<String, String> usermap = new HashMap<>();
//        map.put("username", username);
//        return new JSONPObject(callback, SysResult.success(usermap));
        //3.表示校验成功
        object = new JSONPObject(callback, SysResult.success(userJSON));
        return object;
    }

    /**
     * 验证数据是否存在
     *
     * @param param
     * @param type
     * @param callback
     * @return
     */
    @RequestMapping(value = "/check/{param}/{type}", method = RequestMethod.GET)
    public JSONPObject findUserExist(@PathVariable String param, @PathVariable Integer type, String callback) {
        SysResult sysResult = new SysResult();
        if (type != 1 && type != 2 && type != 3) {
            sysResult.setStatus(400).setMsg("校验类型输入有误");
            return new JSONPObject(callback, sysResult);
        }
        Boolean userExist = userService.findUserExist(param, type);
        return new JSONPObject(callback, sysResult.setData(userExist));
    }

}
