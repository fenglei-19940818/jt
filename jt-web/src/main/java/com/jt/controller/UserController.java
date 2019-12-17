package com.jt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 实现通用的跳转
     * @param moduleName
     * @return
     */
    @RequestMapping("/{moduleName}")
    public String toModule(@PathVariable String moduleName) {
        return moduleName;
    }
}
