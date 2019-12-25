package com.hs.netty.imitate.rpc.serialize.support.kryo;

import com.hs.netty.imitate.rpc.serialize.support.MessageCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.MessageDecoder;

/**
 * Kryo解码器
 * @author 王海生
 *
 */
public class KryoDecoder extends MessageDecoder{
	public KryoDecoder(MessageCodecUtil util) {
        super(util);
    }
}
