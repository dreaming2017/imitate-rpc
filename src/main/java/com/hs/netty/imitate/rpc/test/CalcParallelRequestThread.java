package com.hs.netty.imitate.rpc.test;

import java.util.concurrent.CountDownLatch;

import com.hs.netty.imitate.rpc.core.client.MessageSendExecutor;
import com.hs.netty.imitate.rpc.servicebean.Calculate;

public class CalcParallelRequestThread implements Runnable {

    private CountDownLatch start;
    private CountDownLatch end;
    private MessageSendExecutor executor;
    private int taskNumber = 0;

    public CalcParallelRequestThread(MessageSendExecutor executor, CountDownLatch start, CountDownLatch end, int taskNumber) {
        this.start = start;
        this.end = end;
        this.taskNumber = taskNumber;
        this.executor = executor;
    }

    public void run() {
        try {
        	start.await();

            Calculate calc = executor.execute(Calculate.class);
            Integer add = calc.add(taskNumber, taskNumber);
            
            System.out.println("calc add result:[" + add + "= ("+ taskNumber+ "加上" +taskNumber + ")]");

            end.countDown();
        } catch (InterruptedException ex) {
           ex.printStackTrace();
        }
    }
}
