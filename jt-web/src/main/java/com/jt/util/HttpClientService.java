package com.jt.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HttpClientService {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);

    //从池中获取连接
    @Autowired
    private CloseableHttpClient htClient;

    //控制请求超时时间
    @Autowired
    private RequestConfig requestConfig;

    /**
     * doGet HTTPClient的get请求
     * 目的:通过制定的URL地址,获取服务器数据
     * 参数:
     * 1.url地址
     * 2.封装用户参数 Map<String,String> params
     * 3.设定编码格式 String charset
     */
    public String doGet(String url, Map<String, String> params, String charset) {
        //1.判读字符集编码是否有值
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }

        //2.判读参数集合是否为空
        if (params != null) {
            url += "?";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                url += key + "=" + value + "&";
            }
            //去除多余的&符
            url = url.substring(0, url.length() - 1);
        }

        //3.定义请求类型
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        String result = "";
        try {
            //4.发起请求获取结果
            CloseableHttpResponse response = htClient.execute(httpGet);
            //获取状态码信息
            if (200 == response.getStatusLine().getStatusCode()) {
                result = EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (IOException e) {
            logger.info("请求失败!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * doGet HTTPClient的post请求
     * 目的:通过制定的URL地址,获取服务器数据
     * 参数:
     * 1.url地址
     * 2.封装用户参数 Map<String,String> params
     * 3.设定编码格式 String charset
     */
    public String doPost(String url, Map<String, String> params, String charset) {
        String result = "";
        //1.判读字符集编码是否有值
        if (StringUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        // 创建httpclient对象
        HttpPost httpPost = new HttpPost(url);
        try { // 参数键值对
            if (null != params && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                NameValuePair pair = null;
                for (String key : params.keySet()) {
                    pair = new BasicNameValuePair(key, params.get(key));
                    pairs.add(pair);
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
                httpPost.setEntity(entity);
            }
            HttpResponse response = htClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpPost) {
                // 释放连接
                httpPost.releaseConnection();
            }
        }
        return result;
    }

    public String doGet(String url) {

        return doGet(url, null, null);
    }

    public String doGet(String url, Map<String, String> params) {

        return doGet(url, params, null);
    }

    public <T> T doGet(String url, Map<String, String> params, Class<T> targetClass, String charset) {

        String result = doGet(url, params);
//        return JsonUtil.getJsonToBean(result, targetClass);
        return ObjectMapperUtil.toObject(result, targetClass);
    }

    public <T> T doGet(String url, Class<T> targetClass) {

        String result = doGet(url);
//        return ObjectMapperUtil.toObject(result, targetClass);
        return JsonUtil.getJsonToBean(result, targetClass);
    }


}
