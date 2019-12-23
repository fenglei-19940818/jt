package com.jt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.pojo.User;
import com.jt.service.DubboUserService;
import com.jt.service.UserService;
import com.jt.util.CookieUtil;
import com.jt.util.IPUtil;
import com.jt.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Reference(check = false)
    private DubboUserService dubboUserService;

    @Autowired
    private JedisCluster jedisCluster;

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
    @RequestMapping(value = "/doRegister", method = RequestMethod.POST)
    @ResponseBody
    public SysResult insertUser(User user) {
        //添加用户
        Integer insetCount = dubboUserService.insertUser(user);
        SysResult sysResult = new SysResult();
        //判断是否添加成功
        if (insetCount > 0) {
            sysResult = SysResult.success(user.getUsername());
        } else {
            sysResult = SysResult.fail();
        }
        return sysResult;
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public SysResult login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        SysResult sysResult = new SysResult();
        //获取发出请求的IP地址
        String ip = IPUtil.getIpAddr(request);
        String ticket = dubboUserService.findUserByUP(username, password, ip);
        //判断数据时否为空
        if (StringUtils.isEmpty(ticket)) {
            return SysResult.fail();
        }
        //cookie中存放秘钥信息
        Cookie cookie = new Cookie("JT_TICKET", ticket);
        //设置Cookie的最大生命周期,否则浏览器关闭后Cookie即失效(7天有效)
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setDomain("jt.com");
        //将Cookie加到response中
        response.addCookie(cookie);
        //cookie中存放User信息
        Cookie cookieUser = new Cookie("JT_USER", username);
        //设置Cookie的最大生命周期,否则浏览器关闭后Cookie即失效(7天有效)
        cookieUser.setMaxAge(7 * 24 * 60 * 60);
        cookieUser.setPath("/");
        cookieUser.setDomain("jt.com");
        //将Cookie加到response中
        response.addCookie(cookieUser);
        return sysResult.setData(ticket);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        String ticket = null;
        String username = null;
        Boolean flagTicket = false;
        //获取cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (("JT_USER").equals(cookie.getName())) {
                    username = cookie.getValue();
                }
                if ("JT_TICKET".equals(cookie.getName())) {
                    ticket = cookie.getValue();
                }
            }
        }
        //删除redis中ticket的数据
        if (!StringUtils.isEmpty(ticket)) {
            jedisCluster.del(ticket);
            //删除cookie中的数据
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
        }
        //删除redis中user的数据
        if (!StringUtils.isEmpty(username)) {
            jedisCluster.del("JT_USER_" + username);
            //删除cookie中的数据
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);

        }
        return "redirect:/";
    }

}
