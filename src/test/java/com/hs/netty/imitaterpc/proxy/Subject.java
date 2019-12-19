package com.hs.netty.imitaterpc.proxy;

public interface Subject {
	void request() throws Exception;
	
	void request(String name,int age);
}
