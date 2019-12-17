package com.jt.aop;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jt.vo.SysResult;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class SystemExceptionAOP {

    /**
     * 如果程序出错,应该在页面中返回什么???
     * 应该返回SysResult.fail();将数据转化为JSON
     * 在Controller中如果出现问题则执行业务操作
     */

    @ExceptionHandler(RuntimeException.class)
    public Object fail(RuntimeException e, HttpServletRequest request) {
        e.printStackTrace();
        String callback = request.getParameter("callback");
        if (StringUtils.isEmpty(callback)) {
            return SysResult.fail();
        } else {
            JSONPObject jsonpObject = new JSONPObject(callback, SysResult.fail());
            return jsonpObject;
        }
    }

}
