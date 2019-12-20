package com.jt.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void deleteCookie(String cookieName, String path, String domain, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath(path);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }
}
