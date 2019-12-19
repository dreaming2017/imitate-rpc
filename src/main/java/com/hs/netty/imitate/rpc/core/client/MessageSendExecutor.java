package com.hs.netty.imitate.rpc.core.client;

import java.lang.reflect.Proxy;

/**
 * 客户端执行模块的入口
 * 
 * @author 王海生
 *
 */
public class MessageSendExecutor {
	public MessageSendExecutor(){
		System.out.println("MessageSendExecutor construct()");
	}
	private RpcServerLoader loader = RpcServerLoader.getInstance();

	public MessageSendExecutor(String serverAddress) {
		loader.load(serverAddress);
	}
	
	public void stop() {
        loader.unLoad();
    }
	
	@SuppressWarnings("unchecked")
	public static <T> T execute(Class<T> rpcInterface) {
        return (T) Proxy.newProxyInstance(
                rpcInterface.getClassLoader(),
                new Class<?>[]{rpcInterface},
                new MessageSendProxy<T>(rpcInterface)
        );
    }
}
