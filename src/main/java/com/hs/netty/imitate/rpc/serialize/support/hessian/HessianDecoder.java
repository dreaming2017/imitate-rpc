package com.hs.netty.imitate.rpc.serialize.support.hessian;

import com.hs.netty.imitate.rpc.serialize.support.MessageCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.MessageDecoder;

/**
 * Hessian解码器
 * @author 王海生
 *
 */
public class HessianDecoder extends MessageDecoder {

    public HessianDecoder(MessageCodecUtil util) {
        super(util);
    }
}
