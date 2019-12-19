package com.hs.netty.imitate.rpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class MessageSendInitializeTask implements Runnable {
	private final EventLoopGroup eventLoopGroup;
	private final InetSocketAddress serverAddress;
	private final RpcServerLoader rpcServerLoader;
	public MessageSendInitializeTask(EventLoopGroup eventLoopGroup,InetSocketAddress serverAddress,RpcServerLoader rpcServerLoader){
		this.eventLoopGroup = eventLoopGroup;
		this.serverAddress = serverAddress;
		this.rpcServerLoader = rpcServerLoader;
		
	}
	@Override
	public void run() {
		System.out.println("MessageSendInitializeTask run ...");
		 Bootstrap b = new Bootstrap();
	        b.group(eventLoopGroup)
	                .channel(NioSocketChannel.class).
	                option(ChannelOption.SO_KEEPALIVE, true);
	        b.handler(new MessageSendChannelInitializer());

	        ChannelFuture channelFuture = b.connect(serverAddress);
	        
	        channelFuture.addListener(new ChannelFutureListener() {
	            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
	               
	            	if (channelFuture.isSuccess()) {
	                    MessageSendHandler handler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
	                   rpcServerLoader.setMessageSendHandler(handler);
	                }
	            }
	        });
		
	}

}
