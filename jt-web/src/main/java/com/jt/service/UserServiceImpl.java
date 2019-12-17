package com.jt.service;

import com.jt.pojo.User;
import com.jt.util.BeanMapUtil;
import com.jt.util.HttpClientService;
import com.jt.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@PropertySource("classpath:/properties/url.properties")
public class UserServiceImpl implements UserService {

    @Autowired
    private HttpClientService httpClientService;

    @Value("${sso.url}")
    private String ssoUrl;

    @Override
    public String doRegister(User user) {
//        String beanToJson = JsonUtil.getBeanToJson(user);
//        Map<String, String> paramMap = BeanMapUtil.stringToMap(beanToJson, ":", ",");
//        String s = httpClientService.doPost(ssoUrl, paramMap, "utf-8");
        return null;
    }
}
