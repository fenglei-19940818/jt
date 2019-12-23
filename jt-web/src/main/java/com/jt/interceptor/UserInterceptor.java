package com.jt.interceptor;

import com.jt.pojo.User;
import com.jt.util.CookieUtil;
import com.jt.util.IPUtil;
import com.jt.util.JsonUtil;
import com.jt.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie
        Cookie ticketCookie = CookieUtil.get(request, "JT_TICKET");
        Cookie userNameCookie = CookieUtil.get(request, "JT_USER");
        if (ticketCookie == null || userNameCookie == null) {
            response.sendRedirect("/user/login.html");
            return false;
        }
        //获取数据
        String ticket = ticketCookie.getValue();
        String username = userNameCookie.getValue();
        if (StringUtils.isEmpty(ticket) || StringUtils.isEmpty(username)) {
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            response.sendRedirect("/user/login.html");
            return false;
        }
        //开启校验 校验IP
        String requestIP = IPUtil.getIpAddr(request);
        String realIP = jedisCluster.hget(ticket, "JT_USER_IP");
        if (!requestIP.equals(realIP)) {
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            response.sendRedirect("/user/login.html");
            return false;
        }
        //校验用户名
        String redisTicket = jedisCluster.get("JT_USER_" + username);
        if (!ticket.equals(redisTicket)) {
            CookieUtil.deleteCookie("JT_TICKET", "/", "jt.com", response);
            CookieUtil.deleteCookie("JT_USER", "/", "jt.com", response);
            response.sendRedirect("/user/login.html");
            return false;
        }
        //动态获取用户信息
        User user = JsonUtil.getJsonToBean(jedisCluster.hget(ticket, "JT_USER"), User.class);
        request.setAttribute("JT_USER", user);
        UserThreadLocal.set(user);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
