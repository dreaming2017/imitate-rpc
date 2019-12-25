package com.hs.netty.imitate.rpc.core.client;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.hs.netty.imitate.rpc.serialize.support.MessageCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeFrame;
import com.hs.netty.imitate.rpc.serialize.support.RpcSerializeProtocol;
import com.hs.netty.imitate.rpc.serialize.support.hessian.HessianCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.hessian.HessianDecoder;
import com.hs.netty.imitate.rpc.serialize.support.hessian.HessianEncoder;
import com.hs.netty.imitate.rpc.serialize.support.kryo.KryoCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.kryo.KryoDecoder;
import com.hs.netty.imitate.rpc.serialize.support.kryo.KryoEncoder;
import com.hs.netty.imitate.rpc.serialize.support.kryo.KryoPoolFactory;

public class RpcSendSerializeFrame implements RpcSerializeFrame {

	@Override
	public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {

		switch (protocol) {
		case JDKSERIALIZE: {
			pipeline.addLast(new LengthFieldBasedFrameDecoder(
					Integer.MAX_VALUE, 0, MessageCodecUtil.MESSAGE_LENGTH, 0,
					MessageCodecUtil.MESSAGE_LENGTH));
			pipeline.addLast(new LengthFieldPrepender(
					MessageCodecUtil.MESSAGE_LENGTH));
			pipeline.addLast(new ObjectEncoder());
			pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
					ClassResolvers.weakCachingConcurrentResolver(this
							.getClass().getClassLoader())));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		case KRYOSERIALIZE: {
			KryoCodecUtil util = new KryoCodecUtil(
					KryoPoolFactory.getKryoPoolInstance());
			pipeline.addLast(new KryoEncoder(util));
			pipeline.addLast(new KryoDecoder(util));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		case HESSIANSERIALIZE: {
			HessianCodecUtil util = new HessianCodecUtil();
			pipeline.addLast(new HessianEncoder(util));
			pipeline.addLast(new HessianDecoder(util));
			pipeline.addLast(new MessageSendHandler());
			break;
		}
		}
	}

}
