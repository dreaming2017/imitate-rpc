package com.hs.netty.imitaterpc.proxy;

import java.lang.reflect.Proxy;

public class Client {
	public static void main(String[] args) throws Exception {
		// 通过设置参数，将生成的代理类的.class文件保存在本地
		//System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
		Subject subject = (Subject) Proxy.newProxyInstance(
				Subject.class.getClassLoader(), new Class[] { Subject.class },
				new JdkProxySubject(new RealSubject()));
		//subject.request();
		subject.request("hs", 132);
	}
}
