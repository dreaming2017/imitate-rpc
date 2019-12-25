package com.hs.netty.imitate.rpc.serialize.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * RPC消息序列化/反序列化接口定义
 * 
 * @author 王海生
 *
 */
public interface RpcSerialize {
	/**
	 * 序列化
	 * @param output
	 * @param object
	 * @throws IOException
	 */
	void serialize(OutputStream output, Object object) throws IOException;

	/**
	 * 反序列化
	 * @param input
	 * @return
	 * @throws IOException
	 */
	Object deserialize(InputStream input) throws IOException;
}
