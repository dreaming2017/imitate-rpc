package com.hs.netty.imitate.rpc.core.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;

/**
 * 消息回调
 * @author 王海生
 *
 */
public class MessageCallBack {
	private static final Logger logger = LoggerFactory.getLogger(MessageCallBack.class);
	private MessageRequest request;
	private MessageResponse response;
	private Lock lock = new ReentrantLock();
	private Condition finish = lock.newCondition();

	public MessageCallBack(MessageRequest request) {
		this.request = request;
	}

	public Object start() throws InterruptedException {
		lock.lock();
		try {
			// 设定一下超时时间，rpc服务器太久没有相应的话，就默认返回空吧。
			finish.await(10 * 1000, TimeUnit.MILLISECONDS);
			if (response != null) {
				response.setMessageId(request.getMessageId()); //这里只是设置了，后面要进行优化处理
				return response.getResultDesc(); //这里只返回结果，后面要优化起来!
			} else {
				//这部分代码后面要考虑优化
				response = new MessageResponse();
				response.setMessageId(request.getMessageId());
				response.setError("timeOut");
				logger.info(">>>>[rpc client ] MessageCallBack start() ...response={}",response);
				//System.out.println(response.toString());
				return null;
			}
		} finally {
			lock.unlock();
		}
	}

	public void over(MessageResponse reponse) {
		lock.lock();
		try {
			finish.signal();
			this.response = reponse;
		} finally {
			lock.unlock();
		}
	}
}
