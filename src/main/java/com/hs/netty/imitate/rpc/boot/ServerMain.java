package com.hs.netty.imitate.rpc.boot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMain {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		//new ClassPathXmlApplicationContext("rpc-invoke-config.xml");
		
		new ClassPathXmlApplicationContext("serialize/rpc-invoke-config-jdknative.xml");

		//new ClassPathXmlApplicationContext("serialize/rpc-invoke-config-kryo.xml");

		//new ClassPathXmlApplicationContext("serialize/rpc-invoke-config-hessian.xml");
	}
}
