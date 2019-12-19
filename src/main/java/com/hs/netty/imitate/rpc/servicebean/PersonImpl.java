package com.hs.netty.imitate.rpc.servicebean;

public class PersonImpl implements Person{

	@Override
	public String getMessage(String message) {
		return "hello hs " + message;
	}

	@Override
	public void savePerson(String name, int age) {
		//这里写入数据库!
		System.out.println("PersonImpl.savePerson(name="+name+" ,age= "+age+")");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
