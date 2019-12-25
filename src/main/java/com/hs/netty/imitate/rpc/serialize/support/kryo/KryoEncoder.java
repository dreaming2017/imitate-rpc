package com.hs.netty.imitate.rpc.serialize.support.kryo;

import com.hs.netty.imitate.rpc.serialize.support.MessageCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.MessageEncoder;

public class KryoEncoder extends MessageEncoder {

	public KryoEncoder(MessageCodecUtil util) {
		super(util);
	}
}
