package com.hs.netty.imitate.rpc.core.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hs.netty.imitate.rpc.common.threadpool.RpcThreadPool;
import com.hs.netty.imitate.rpc.common.threadpool.ThreadNames;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

/**
 * 配置加载
 * 
 * @author 王海生
 *
 */
public class RpcServerLoader {
	final static Logger logger = LoggerFactory.getLogger(RpcServerLoader.class);

	private final static String DELIMITER = ":";

	private MessageSendHandler messageSendHandler = null;

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

	private static ListeningExecutorService threadPoolExecutor = MoreExecutors
			.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(
					16, -1, ThreadNames.RPC_CLIENT_THREAD));

	public void load(String serverAddress){
		load(serverAddress,RpcSerializeProtocol.KRYOSERIALIZE);
	}
	public void load(String serverAddress,
			RpcSerializeProtocol serializeProtocol) {
		System.out.println("RpcServerLoader load() enter ...");
		String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
		if (ipAddr.length == 2) {
			String host = ipAddr[0];
			int port = Integer.parseInt(ipAddr[1]);
			final InetSocketAddress remoteAddr = new InetSocketAddress(host,
					port);
			ListenableFuture<Boolean> listenableFuture = threadPoolExecutor
					.submit(new MessageSendInitializeTask(eventLoopGroup,
							remoteAddr, serializeProtocol));

			// 监听线程池异步的执行结果成功与否再决定是否唤醒全部的客户端RPC线程
			Futures.addCallback(listenableFuture,
					new FutureCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							lock.lock();
							try {
								if (messageSendHandler == null) {
									handlerStatus.await();
								}

								// Futures异步回调，唤醒所有rpc等待线程
								if (result == Boolean.TRUE
										&& messageSendHandler != null) {
									connectStatus.signalAll();
								}
							} catch (InterruptedException ex) {
								logger.error(ex.getMessage(), ex);
							} finally {
								lock.unlock();
							}

						}

						@Override
						public void onFailure(Throwable t) {
							t.printStackTrace();
							logger.error(t.getMessage(), t);

						}
					});
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
	private Condition connectStatus = lock.newCondition();
	private Condition handlerStatus = lock.newCondition();

	public void setMessageSendHandler(MessageSendHandler messageInHandler) {
		lock.lock();
		try {
			this.messageSendHandler = messageInHandler;
			// 唤醒所有等待客户端RPC线程
			// signal.signalAll();
			handlerStatus.signal();
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
				connectStatus.await();
			}
			return messageSendHandler;
		} finally {
			lock.unlock();
		}
	}


}
