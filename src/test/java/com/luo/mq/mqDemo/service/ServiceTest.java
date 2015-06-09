package com.luo.mq.mqDemo.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class ServiceTest {

	@Autowired
	private IHelloService helloService;
	
	@Test
	public void testAop(){
		int s = helloService.sayHello(0, 1);
		System.out.println(s);
	}
}
