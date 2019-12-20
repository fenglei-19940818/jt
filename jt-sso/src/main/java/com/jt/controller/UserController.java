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
//    @RequestMapping(value = "/query/{ticket}/{username}/{ip}", method = RequestMethod.GET)
//    public JSONPObject queryUserLogin(@PathVariable String ticket, @PathVariable String username, @PathVariable String ip, String callback, HttpServletRequest request, HttpServletResponse response) {
    @RequestMapping(value = "/query/{ticket}/{username}", method = RequestMethod.GET)
    public JSONPObject queryUserLogin(@PathVariable String ticket, @PathVariable String username, String callback, HttpServletRequest request, HttpServletResponse response) {
        //获取调用者IP
        String ip = IPUtil.getIpAddr(request);
        ticket = userService.queryUserLogin(ticket, username, ip);
        //判断用户名是否为空
        if (StringUtils.isEmpty(ticket)) {
            //删除cookie
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            return new JSONPObject(callback, SysResult.fail());
        } else {
            //cookie中存放秘钥信息
            Cookie cookie = new Cookie("JT_TICKET", ticket);
            //设置Cookie的最大生命周期,否则浏览器关闭后Cookie即失效(7天有效)
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setPath("/");
            cookie.setDomain("jt.com");
            //将Cookie加到response中
            response.addCookie(cookie);
            //返回值
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            return new JSONPObject(callback, SysResult.success(map));
//            return new JSONPObject(callback, SysResult.success("username:"+userName));
        }
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
