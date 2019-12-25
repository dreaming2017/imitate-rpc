package com.hs.netty.imitate.rpc.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import com.hs.netty.imitate.rpc.core.client.MessageSendExecutor;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;

public class RpcParallelTest2 {
	public static void parallelTask(MessageSendExecutor executor, int parallel, String serverAddress, RpcSerializeProtocol protocol) throws InterruptedException {
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();

        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);

        for (int index = 0; index < parallel; index++) {
            CalcParallelRequestThread client = new CalcParallelRequestThread(executor, signal, finish, index);
            new Thread(client).start();
        }

        //10000个并发线程瞬间发起请求操作
        signal.countDown();
        finish.await();
        sw.stop();

        String tip = String.format("[%s] RPC调用总共耗时: [%s] 毫秒", protocol, sw.getTime());
        System.out.println(tip);

    }

    //JDK本地序列化协议
    public static void jdkNativeParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18887";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.JDKSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest2.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    //Kryo序列化协议
    public static void kryoParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18888";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.KRYOSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest2.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    //Hessian序列化协议
    public static void hessianParallelTask(MessageSendExecutor executor, int parallel) throws InterruptedException {
        String serverAddress = "127.0.0.1:18889";
        RpcSerializeProtocol protocol = RpcSerializeProtocol.HESSIANSERIALIZE;
        executor.setRpcServerLoader(serverAddress, protocol);
        RpcParallelTest2.parallelTask(executor, parallel, serverAddress, protocol);
        TimeUnit.SECONDS.sleep(3);
    }

    public static void main(String[] args) throws Exception {
        //并行度10000
        int parallel = 2;
        MessageSendExecutor executor = new MessageSendExecutor();
       // hessianParallelTask(executor, parallel);
        jdkNativeParallelTask(executor, parallel);
        //kryoParallelTask(executor, parallel);

        executor.stop();
    }
}
