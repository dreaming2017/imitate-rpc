package com.hs.netty.imitate.rpc.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

public class MessageRecvChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private RpcSerializeProtocol protocol = null;
	private RpcRecvSerializeFrame frame = null;

	MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
		this.frame = new RpcRecvSerializeFrame(handlerMap);
	}

	MessageRecvChannelInitializer buildRpcSerializeProtocol(
			RpcSerializeProtocol protocol) {
		this.protocol = protocol;
		return this;
	}

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		frame.select(protocol, pipeline);
	}

}
