package com.hs.netty.imitate.rpc.core.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hs.netty.imitate.rpc.common.threadpool.RpcThreadPool;
import com.hs.netty.imitate.rpc.common.threadpool.ThreadNames;

/**
 * 配置加载
 * 
 * @author 王海生
 *
 */
public class RpcServerLoader {

	private RpcServerLoader() {
	}

	private static class RpcServerLoaderHolder {
		public static RpcServerLoader rpcServerLoader = new RpcServerLoader();
	}

	public static RpcServerLoader getInstance() {
		return RpcServerLoaderHolder.rpcServerLoader; // 这里将导致InstanceHolder类被初始化
	}

	// 方法返回到Java虚拟机的可用的处理器数量
	private final static int parallel = Runtime.getRuntime()
			.availableProcessors() * 2;
	// netty nio线程池
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);

	private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool
			.getExecutor(16, -1,ThreadNames.RPC_CLIENT_THREAD);
	private final static String DELIMITER = ":";

	private MessageSendHandler messageSendHandler = null;


	public void load(String serverAddress) {
		System.out.println("RpcServerLoader load() enter ...");
		String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			final InetSocketAddress remoteAddr = new InetSocketAddress(host,
					port);
			threadPoolExecutor.submit(new MessageSendInitializeTask(
					eventLoopGroup, remoteAddr, this));
		}
	}

	/**
	 * 关闭处理器、线程池、io线程
	 */
	public void unLoad() {
		messageSendHandler.close();
		threadPoolExecutor.shutdown();
		eventLoopGroup.shutdownGracefully();
	}

	// 等待Netty服务端链路建立通知信号
	private Lock lock = new ReentrantLock();
	private Condition signal = lock.newCondition();

	public void setMessageSendHandler(MessageSendHandler messageInHandler) {
		lock.lock();
		try {
			this.messageSendHandler = messageInHandler;
			// 唤醒所有等待客户端RPC线程
			signal.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public MessageSendHandler getMessageSendHandler()
			throws InterruptedException {
		lock.lock();
		try {
			// Netty服务端链路没有建立完毕之前，先挂起等待
			if (messageSendHandler == null) {
				signal.await();
			}
			return messageSendHandler;
		} finally {
			lock.unlock();
		}
	}

}
