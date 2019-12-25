package com.hs.netty.imitate.rpc.core.client;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 这个 mapCallBack 相当于dubbo中rpc客户端的futures
 * @author 王海生
 *
 */
public class MessageSendHandler extends ChannelInboundHandlerAdapter {
	/**
	 * key为messageId
	 */
	private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();

	private volatile Channel channel;
	private SocketAddress remoteAddr;

	public Channel getChannel() {
		return channel;
	}

	public SocketAddress getRemoteAddr() {
		return remoteAddr;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.remoteAddr = this.channel.remoteAddress();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.channel = ctx.channel();
	}

	/**
	 * 服务端给客户端响应
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		MessageResponse response = (MessageResponse) msg;
		String messageId = response.getMessageId();
		MessageCallBack callBack = mapCallBack.get(messageId);
		if (callBack != null) {
			mapCallBack.remove(messageId);
			callBack.over(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

	/**
	 * 这个并不是重写的!
	 */
	public void close() {
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
				ChannelFutureListener.CLOSE);
	}

	/**
	 * 请求是从这里发出去的
	 * @param request
	 * @return
	 */
	public MessageCallBack sendRequest(MessageRequest request) {
		MessageCallBack callBack = new MessageCallBack(request);
		mapCallBack.put(request.getMessageId(), callBack);
		channel.writeAndFlush(request);
		return callBack;
	}
}
