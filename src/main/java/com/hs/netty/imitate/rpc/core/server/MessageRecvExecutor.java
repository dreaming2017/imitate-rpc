package com.hs.netty.imitate.rpc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hs.netty.imitate.rpc.common.threadpool.NamedThreadFactory;
import com.hs.netty.imitate.rpc.common.threadpool.RpcThreadPool;
import com.hs.netty.imitate.rpc.common.threadpool.ThreadNames;
import com.hs.netty.imitate.rpc.model.MessageKeyVal;
import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

/**
 * 服务端执行模块
 * 
 * @author 王海生
 *
 */
public class MessageRecvExecutor implements ApplicationContextAware,InitializingBean, DisposableBean {
	
	private final static Logger logger = LoggerFactory.getLogger(MessageRecvExecutor.class);
	
	//服务端的地址
	private final String serverAddress;
	
	private final static String DELIMITER = ":";

	private RpcSerializeProtocol serializeProtocol = null;

	private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();

	private static ListeningExecutorService threadPoolExecutor;

	private EventLoopGroup boss = null;
	private EventLoopGroup worker = null;

	public MessageRecvExecutor(String serverAddress) {
		// 默认kyro序列化协议
		this(serverAddress, RpcSerializeProtocol.KRYOSERIALIZE);
	}

	public MessageRecvExecutor(String serverAddress, String serializeProtocolName) {
		this(serverAddress,RpcSerializeProtocol.getRpcSerializeProtocolByName(serializeProtocolName));
	}

	public MessageRecvExecutor(String serverAddress,
			RpcSerializeProtocol serializeProtocol) {
		this.serverAddress = serverAddress;
		this.serializeProtocol = serializeProtocol;
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
		bootstrap.group(boss, worker)
				.channel(NioServerSocketChannel.class)
				.childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol)) //执行逻辑在这里呢!
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			logger.info(
					" Netty RPC Server start success ip:{}, port:{}", host,
					port);
			future.channel().closeFuture().sync();
		} else {
			logger.error("Netty RPC Server start fail! pelease config correct serverAddress! current serverAddress is {}",serverAddress);
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
		if(logger.isDebugEnabled()){
			logger.debug("MessageRecvExecutor setApplicationContext handlerMap = " + handlerMap.toString());
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info("准备关闭线程池");
		close(worker);
		close(boss);
		threadPoolExecutor.shutdown();;
	}

	private void close(EventLoopGroup eventLoopGroup) {
		
		if (eventLoopGroup != null) {
			eventLoopGroup.shutdownGracefully();
		}
	}
	
	/**
	 * 这部分代码考虑一下换一个位置
	 * @param task  处理的任务
	 * @param ctx   netty处理器上下文
	 * @param request   请求
	 * @param response  响应
	 */
	public static void submit(Callable<Boolean> task,
			ChannelHandlerContext ctx, MessageRequest request,
			MessageResponse response) {
		
		//业务线程池
		if (threadPoolExecutor == null) {
			synchronized (MessageRecvExecutor.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = MoreExecutors
							.listeningDecorator((ThreadPoolExecutor) RpcThreadPool
									.getExecutor(16, -1,
											ThreadNames.RPC_SERVER_THREAD));
				}
			}
		}
		ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);
		// Netty服务端把计算结果异步返回
		Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				ctx.writeAndFlush(response).addListener(
						new ChannelFutureListener() {
							public void operationComplete(
									ChannelFuture channelFuture)
									throws Exception {
								logger.info("RPC Server Send message-id respone:"
												+ request.getMessageId());
							}
						});

			}

			@Override
			public void onFailure(Throwable t) {
				 t.printStackTrace();

			}
		},threadPoolExecutor);

	}

}
