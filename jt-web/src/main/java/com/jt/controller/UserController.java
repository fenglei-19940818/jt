package com.jt.controller;

import com.jt.pojo.User;
import com.jt.service.UserService;
import com.jt.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 实现通用的跳转
     *
     * @param moduleName
     * @return
     */
    @RequestMapping("/{moduleName}")
    public String toModule(@PathVariable String moduleName) {
        return moduleName;
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @RequestMapping("/doRegister")
    public SysResult doRegister(User user) {
        String userName = userService.doRegister(user);
        return SysResult.success(userName);
    }

}
