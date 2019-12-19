package com.hs.netty.imitate.rpc.common.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 业务线程池
 * @author 王海生
 *
 */
public class RpcThreadPool {
	/**
	 *独立出线程池主要是为了应对复杂耗I/O操作的业务，不阻塞netty的handler线程而引入;
	 *当然如果业务足够简单，把处理逻辑写入netty的handler（ChannelInboundHandlerAdapter）也未尝不可
	 * @param threads    线程池大小
	 * @param queues    队列大小 0将使用SynchronousQueue
	 * @return
	 */
	public static Executor getExecutor(int threads, int queues) {
		return getExecutor(threads, queues, "rpcThreadPool");
	}
	
	public static Executor getExecutor(int threads, int queues,String threadName) {
		
		return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<Runnable>()
                        : (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                                : new LinkedBlockingQueue<Runnable>(queues)),
                new NamedThreadFactory(threadName, true), new AbortPolicyWithReport(threadName));
	}
}
