package com.hs.netty.imitate.rpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

public class MessageSendInitializeTask implements Callable<Boolean> {
	private final EventLoopGroup eventLoopGroup;
	private final InetSocketAddress serverAddress;

	private final RpcSerializeProtocol protocol;

	MessageSendInitializeTask(EventLoopGroup eventLoopGroup,
			InetSocketAddress serverAddress, RpcSerializeProtocol protocol) {
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.protocol = protocol;
	}

	@Override
	public Boolean call() throws Exception {
		System.out.println("MessageSendInitializeTask run ...");
		Bootstrap b = new Bootstrap();
		b.group(eventLoopGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new MessageSendChannelInitializer()
				.buildRpcSerializeProtocol(protocol));
		ChannelFuture channelFuture = b.connect(serverAddress);
		channelFuture.addListener(new ChannelFutureListener() {
			public void operationComplete(final ChannelFuture channelFuture)
					throws Exception {
				if (channelFuture.isSuccess()) {
					MessageSendHandler handler = channelFuture.channel()
							.pipeline().get(MessageSendHandler.class);
					RpcServerLoader.getInstance()
							.setMessageSendHandler(handler);
				}
			}
		});
		return Boolean.TRUE;
	}

}
