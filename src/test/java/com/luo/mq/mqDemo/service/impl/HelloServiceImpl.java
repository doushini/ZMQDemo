package com.luo.mq.mqDemo.service.impl;

import org.springframework.stereotype.Service;

import com.luo.mq.mqDemo.service.IHelloService;

@Service
public class HelloServiceImpl implements IHelloService {

	public int sayHello(int a, int b) {
		System.out.println("sayHello called");
		if(a==0){
			throw new RuntimeException("参数不合法");
		}
		return a+b;
	}

}
