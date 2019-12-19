package com.hs.netty.imitate.rpc.common.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 * 
 * @author 王海生
 *
 */
public class NamedThreadFactory implements ThreadFactory {
	/**
	 * 线程数量
	 */
	private static final AtomicInteger threadNumber = new AtomicInteger(1);

	/**
	 * 线程编号
	 */
	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String prefix;

	private final boolean daemon;

	private final ThreadGroup threadGroup;

	public NamedThreadFactory() {
		this("rpcserver-threadpool-" + threadNumber.getAndIncrement());
	}

	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemo) {
		this.prefix = prefix;
		this.daemon = daemo;
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s
				.getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = prefix + mThreadNum.getAndIncrement();
		//stackSize :The requested stack size for this thread,0代表不指定大小
		Thread thread = new Thread(threadGroup, r, name, 0);
		thread.setDaemon(daemon);
		return thread;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

}
