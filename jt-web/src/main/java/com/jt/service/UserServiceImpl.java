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

}
