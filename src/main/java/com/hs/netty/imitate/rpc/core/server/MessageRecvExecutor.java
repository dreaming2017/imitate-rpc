package com.hs.netty.imitate.rpc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hs.netty.imitate.rpc.common.threadpool.NamedThreadFactory;
import com.hs.netty.imitate.rpc.common.threadpool.RpcThreadPool;
import com.hs.netty.imitate.rpc.common.threadpool.ThreadNames;
import com.hs.netty.imitate.rpc.model.MessageKeyVal;

/**
 * 服务端执行模块
 * 
 * @author 王海生
 *
 */
public class MessageRecvExecutor implements ApplicationContextAware,
		InitializingBean, DisposableBean {
	private String serverAddress;
	private final static String DELIMITER = ":";
	

	private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();

	private static ThreadPoolExecutor threadPoolExecutor;
	private EventLoopGroup boss = null;
	private EventLoopGroup worker = null;

	public MessageRecvExecutor(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public static void submit(Runnable task) {
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool
							.getExecutor(16, -1,ThreadNames.RPC_SERVER_THREAD);
				}
			}
		}
		threadPoolExecutor.submit(task);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// netty的线程池模型设置成主从线程池模式，这样可以应对高并发请求
		// 当然netty还支持单线程、多线程网络IO模型，可以根据业务需求灵活配置
		ThreadFactory threadRpcFactory = new NamedThreadFactory(
				"NettyRPC ThreadFactory");

		// 方法返回到Java虚拟机的可用的处理器数量
		int parallel = Runtime.getRuntime().availableProcessors() * 2;

		boss = new NioEventLoopGroup();
		worker = new NioEventLoopGroup(parallel, threadRpcFactory,
				SelectorProvider.provider());

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
				.childHandler(new MessageRecvChannelInitializer(handlerMap))
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			System.out
					.printf(" Netty RPC Server start success ip:%s port:%d\n",
							host, port);
			future.channel().closeFuture().sync();
		} else {
			System.out
					.printf("Netty RPC Server start fail!\n");
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		MessageKeyVal keyVal = (MessageKeyVal) applicationContext
				.getBean(MessageKeyVal.class);
		Map<String, Object> rpcServiceMap = keyVal.getMessageKeyVal();
		for (String key : rpcServiceMap.keySet()) {
			handlerMap.put(key, rpcServiceMap.get(key));
		}
	}

	@Override
	public void destroy() throws Exception {
		close(worker);
		close(boss);

	}
	
	private void close(EventLoopGroup eventLoopGroup){
		if(eventLoopGroup != null){
			eventLoopGroup.shutdownGracefully();
		}
	}

}
