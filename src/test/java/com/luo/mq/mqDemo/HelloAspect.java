package com.luo.mq.mqDemo;

import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.apache.commons.lang.exception.ExceptionUtils;


@Component
public class HelloAspect {
	
	public Object doAround(ProceedingJoinPoint joinPoint){
		System.out.println("do before");
		Object o = null;
		try {
			o = joinPoint.proceed(joinPoint.getArgs());
		} catch (Throwable e) {
			System.out.println("捕获到异常: "+ExceptionUtils.getFullStackTrace(e));
			System.out.println("----------------------");
		}
		System.out.println("do after");
		return o;
	}
}
