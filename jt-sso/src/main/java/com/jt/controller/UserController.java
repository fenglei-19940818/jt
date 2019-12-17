package com.jt.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.pojo.User;
import com.jt.service.UserService;
import com.jt.util.JsonUtil;
import com.jt.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public List<User> findAll() {
        List<User> userList = userService.findAll();
        return userList;
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

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JSONPObject register(User user, String callback) {
        SysResult sysResult = new SysResult();
        String username = userService.register(user);
        return new JSONPObject(callback, sysResult.setData(username));
    }

//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public JSONPObject login(String u, String p) {
//        SysResult sysResult = new SysResult();
//        String username = userService.login(u, p);
//        return new JSONPObject(callback, sysResult.setData(username));
//    }
}
