package com.hs.netty.imitate.rpc.common.threadpool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.netty.imitaterpc.log.LogTest;

/**
 * 异常处理类
 * @author 王海生
 *
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
	private static Logger LOG = LoggerFactory.getLogger(AbortPolicyWithReport.class);
	
	private final String threadName;

	public AbortPolicyWithReport(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		String msg = String
				.format("RpcServer["
						+ " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
						+ " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
						threadName, e.getPoolSize(), e.getActiveCount(),
						e.getCorePoolSize(), e.getMaximumPoolSize(),
						e.getLargestPoolSize(), e.getTaskCount(),
						e.getCompletedTaskCount(), e.isShutdown(),
						e.isTerminated(), e.isTerminating());
		
		LOG.error(msg);
		throw new RejectedExecutionException(msg);
	}

}
