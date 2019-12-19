package com.hs.netty.imitaterpc.proxy;

public class RealSubject implements Subject{
	@Override
	public void request() {
        System.out.println("RealSubject execute request()");
    }

	@Override
	public void request(String name, int age) {
		 System.out.println("RealSubject execute request(name=" +name+",age="+age+")");
	}
}
