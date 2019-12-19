package com.hs.netty.imitate.rpc.core.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;

public class MessageRecvInitializeTask implements Runnable {

	private MessageRequest request = null;
	private MessageResponse response = null;
	private Map<String, Object> handlerMap = null;
	private ChannelHandlerContext ctx = null;

	public MessageResponse getResponse() {
		return response;
	}

	public MessageRequest getRequest() {
		return request;
	}

	public void setRequest(MessageRequest request) {
		this.request = request;
	}

	MessageRecvInitializeTask(MessageRequest request, MessageResponse response,
			Map<String, Object> handlerMap, ChannelHandlerContext ctx) {
		this.request = request;
		this.response = response;
		this.handlerMap = handlerMap;
		this.ctx = ctx;
	}

	@Override
	public void run() {
		response.setMessageId(request.getMessageId());
		try {
			Object result = reflect(request);
			response.setResultDesc(result);
		} catch (Throwable t) {
			response.setError(t.toString());
			t.printStackTrace();
			System.err.printf("RPC Server invoke error!\n");
		}

		ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture channelFuture)
					throws Exception {
				System.out.println("RPC Server Send message-id respone:"
						+ request.getMessageId());
			}
		});

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

}
