package com.hs.netty.imitate.rpc.core.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;

/**
 * 服务端的任务
 * @author 王海生
 *
 */
public class MessageRecvInitializeTask  implements Callable<Boolean> {
	private static Logger logger = LoggerFactory.getLogger(MessageRecvInitializeTask.class);
	private MessageRequest request = null;
	private MessageResponse response = null;
	private Map<String, Object> handlerMap = null;

	public MessageResponse getResponse() {
		return response;
	}

	public MessageRequest getRequest() {
		return request;
	}

	public void setRequest(MessageRequest request) {
		this.request = request;
	}

	
	 MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
	        this.request = request;
	        this.response = response;
	        this.handlerMap = handlerMap;
	    }
	
	MessageRecvInitializeTask(MessageRequest request, MessageResponse response,
			Map<String, Object> handlerMap, ChannelHandlerContext ctx) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
	}

	/**
	 * 反射调用
	 * 
	 * @param request2
	 * @return
	 * @throws Throwable
	 */
	private Object reflect(MessageRequest request2) throws Throwable {
		String className = request.getClassName();
		Object serviceBean = handlerMap.get(className);
		String methodName = request.getMethodName();
		Object[] parameters = request.getParametersVal();
		return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
	}

	@Override
	public Boolean call() throws Exception {
		response.setMessageId(request.getMessageId());
		try {
			Object result = reflect(request);
			response.setResultDesc(result);
			return Boolean.TRUE;
		} catch (Throwable t) {
			response.setError(t.toString());
			t.printStackTrace();
			logger.error("RPC Server invoke error!\n");
			return Boolean.FALSE;
		}
	}

}
