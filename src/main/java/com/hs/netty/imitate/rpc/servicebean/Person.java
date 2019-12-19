package com.hs.netty.imitate.rpc.servicebean;

public interface Person {
	String getMessage(String message);
	
	void savePerson(String name,int age);
}
