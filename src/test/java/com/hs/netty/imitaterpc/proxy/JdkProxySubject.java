package com.hs.netty.imitaterpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JdkProxySubject implements InvocationHandler{

	private RealSubject realSubject;
	public JdkProxySubject (RealSubject realSubject){
		this.realSubject = realSubject;
	}
	
	/**
	 * proxy是真实的代理对象  com.hs.netty.imitaterpc.proxy.RealSubject@5ccd43c2
	 * method 是 public abstract void com.hs.netty.imitaterpc.proxy.Subject.request(java.lang.String,int)
	 * args  [hs, 132]
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("method.getDeclaringClass().getName()="+method.getDeclaringClass().getName());
		System.out.println("method.getName()=" +method.getName());
		System.out.println("args=" +Arrays.toString(args));
		System.out.println("method.getParameterTypes()=" +Arrays.toString(method.getParameterTypes()));
		System.out.println("before");
		Object result = null;
		try {
            result = method.invoke(realSubject, args);
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("after");
        }
		return result;
	}

}
