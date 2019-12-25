package com.hs.netty.imitate.rpc.core.client;

import com.google.common.reflect.Reflection;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

/**
 * 客户端执行模块的入口
 * 
 * @author 王海生
 *
 */
public class MessageSendExecutor {
	
	private RpcServerLoader loader = RpcServerLoader.getInstance();
	
	public MessageSendExecutor() {
    }
	
	public MessageSendExecutor(String serverAddress) {
		//默认使用kryo序列化
		this(serverAddress,RpcSerializeProtocol.KRYOSERIALIZE);
    }

    public MessageSendExecutor(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }
	
	public void stop() {
        loader.unLoad();
    }
	
	public static <T> T execute(Class<T> rpcInterface) {
		 return (T) Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
      
    }
}
