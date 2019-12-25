package com.hs.netty.imitate.rpc.serialize.support;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 支持的序列化类型
 * @author 王海生
 *
 */
public enum RpcSerializeProtocol {
	JDKSERIALIZE("jdknative"),
	KRYOSERIALIZE("kryo"), //默认支持的协议
	HESSIANSERIALIZE("hessian");

	private String serializeProtocol;

	private RpcSerializeProtocol(String serializeProtocol) {
		this.serializeProtocol = serializeProtocol;
	}

	public String toString() {
		ReflectionToStringBuilder
				.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		return ReflectionToStringBuilder.toString(this);
	}

	public String getProtocol() {
		return serializeProtocol;
	}
	
	/**
	 * 根据名字获取协议
	 * @param name
	 * @return
	 */
	public static RpcSerializeProtocol getRpcSerializeProtocolByName (String name){
		return Enum.valueOf(RpcSerializeProtocol.class,
				name);
	}
}
