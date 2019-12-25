package com.hs.netty.imitate.rpc.serialize.support.hessian;

import com.hs.netty.imitate.rpc.serialize.support.MessageCodecUtil;
import com.hs.netty.imitate.rpc.serialize.support.MessageEncoder;

public class HessianEncoder extends MessageEncoder {

    public HessianEncoder(MessageCodecUtil util) {
        super(util);
    }
}
