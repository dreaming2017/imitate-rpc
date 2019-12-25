package com.hs.netty.imitate.rpc.core.server;

import java.util.Map;

import com.hs.netty.imitate.rpc.model.MessageRequest;
import com.hs.netty.imitate.rpc.model.MessageResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端处理器
 * @author 王海生
 *
 */
public class MessageRecvHandler extends ChannelInboundHandlerAdapter{
	private final Map<String, Object> handlerMap;

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }
    
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        MessageResponse response = new MessageResponse();
       // MessageRecvInitializeTask recvTask = new MessageRecvInitializeTask(request, response, handlerMap, ctx);
        MessageRecvInitializeTask recvTask = new MessageRecvInitializeTask(request, response, handlerMap);
        //不要阻塞nio线程，复杂的业务逻辑丢给专门的线程池
        //MessageRecvExecutor.submit(recvTask);
        MessageRecvExecutor.submit(recvTask, ctx, request, response); //相互引用了，可以把业务线程池这块的代码都拿出去
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //网络有异常要关闭通道
        ctx.close();
    }
}
