package com.hs.netty.imitate.rpc.serialize.support;

import io.netty.channel.ChannelPipeline;

/**
 * RPC消息序序列化协议选择器接口
 * @author 王海生
 *
 */
public interface RpcSerializeFrame {
	public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline);
}
