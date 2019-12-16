package com.jt.aop;

import com.jt.anno.CacheFind;
import com.jt.util.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.List;

//@Aspect
//@Component
public class RedisCacheAop {

    @Autowired(required = false)
    private JedisCluster jedis;

//    @Autowired(required = false)
//    private Jedis jedis;

//    @Autowired(required = false)
//    private ShardedJedis jedis;

//    @Autowired(required = false)
//    private Jedis jedis;

    @Pointcut("@annotation(com.jt.anno.CacheFind)")
    public void except() {
    }

    @SuppressWarnings("")
    @Around("@annotation(cacheFind)")
//    @Around(value = "except()")
    public Object around(ProceedingJoinPoint joinPoint, CacheFind cacheFind) {
        Object result = new Object();

        String key = getKey(joinPoint, cacheFind);
//        String key = joinPoint.getArgs()[0].toString();
        //判断缓存中是否存在
        if (jedis.exists(key)) {
            String str = jedis.get(key);
            List<Object> jsonToList = JsonUtil.getJsonToList(str, Object.class);
            System.out.println("AOP实现数据库查询!!!!!");
            return jsonToList;
        }

        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException();
        }
        //将数据存入redis缓存中并设置过期时间(1天)
        String treeListStr = JsonUtil.getBeanToJson(result);
        if (cacheFind.seconds() > 0) {
            jedis.setex(key, 86400, treeListStr);
        } else {
            jedis.set(key, treeListStr);
        }

        return result;

    }

    private String getKey(ProceedingJoinPoint joinPoint, CacheFind cacheFind) {
        String key = cacheFind.key();
        if (StringUtils.isEmpty(key)) {
            //方法签名-方法名和参数列表
            Class<?> targetCls = joinPoint.getTarget().getClass();
            Signature signature = joinPoint.getSignature();
            //获取类名
            String className = targetCls.getName();
//            String className1 = joinPoint.getSignature().getDeclaringTypeName();
            //获取方法名
            String methodName = signature.getName();
            //获取第一个参数
            Object firstParam = joinPoint.getArgs()[0];
            return className + "." + methodName + "::" + firstParam;
        }
        return key;
    }

}
