package com.hs.netty.imitate.rpc.core.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

public class MessageSendChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private RpcSerializeProtocol protocol;
	private RpcSendSerializeFrame frame = new RpcSendSerializeFrame();

	MessageSendChannelInitializer buildRpcSerializeProtocol(
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
