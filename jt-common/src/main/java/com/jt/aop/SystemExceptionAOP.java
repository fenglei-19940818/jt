package com.jt.aop;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jt.vo.SysResult;

@RestControllerAdvice
public class SystemExceptionAOP {
	
	/**
	 * 如果程序出错,应该在页面中返回什么???
	 * 应该返回SysResult.fail();将数据转化为JSON
	 * 在Controller中如果出现问题则执行业务操作
	 */
	
	@ExceptionHandler(RuntimeException.class)
	public SysResult fail(RuntimeException e) {
		e.printStackTrace();
		return SysResult.fail();
	}
	
}
