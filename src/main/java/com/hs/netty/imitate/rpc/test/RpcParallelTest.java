package com.hs.netty.imitate.rpc.test;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

import com.hs.netty.imitate.rpc.core.client.MessageSendExecutor;

/**
 * 测试入口
 * 
 * @author 王海生
 *
 */
public class RpcParallelTest {
	static final MessageSendExecutor executor = new MessageSendExecutor(
			"127.0.0.1:18888");
	static final MessageSendExecutor executor2 = new MessageSendExecutor(
			"127.0.0.1:18888");
	static final MessageSendExecutor executor3 = new MessageSendExecutor(
			"127.0.0.1:18888");
	static MessageSendExecutor[] executors = new MessageSendExecutor[] {
			executor, executor2, executor3 };

	public static void main(String[] args) throws Exception {

		// 并行度10000
		int parallel = 200;

		// 开始计时
		StopWatch sw = new StopWatch();
		sw.start();

		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch end = new CountDownLatch(parallel);

		for (int index = 0; index < parallel; index++) {
			MessageSendExecutor executorParam = getMessageSendExecutor(index);
			CalcParallelRequestThread client = new CalcParallelRequestThread(
					executorParam, start, end, index);
			new Thread(client).start();
		}

		// 10000个并发线程瞬间发起请求操作
		start.countDown();
		end.await();

		sw.stop();

		String tip = String.format("RPC调用总共耗时: [%s] 毫秒", sw.getTime());
		System.out.println(tip);
		stop(executors);
		// executor.stop();
	}

	private static void stop(MessageSendExecutor[] executorArray) {
		for (MessageSendExecutor messageSendExecutor : executorArray) {
			messageSendExecutor.stop();
		}
	}

	private static MessageSendExecutor getMessageSendExecutor(int index) {
		return executors[index % executors.length];
	}
}
