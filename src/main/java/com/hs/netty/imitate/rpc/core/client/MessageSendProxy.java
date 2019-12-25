package com.hs.netty.imitate.rpc.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.hs.netty.imitate.rpc.model.MessageRequest;

/**
 * Rpc客户端消息处理
 * 
 * @author 王海生
 *
 */
public class MessageSendProxy<T> implements InvocationHandler {
	//构建请求是在这里的
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		MessageRequest request = new MessageRequest();
		request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);
        
        MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
        
	}

}
