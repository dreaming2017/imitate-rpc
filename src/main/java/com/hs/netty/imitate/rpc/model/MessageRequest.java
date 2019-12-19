package com.hs.netty.imitate.rpc.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 请求体
 * @author 王海生
 *
 */
public class MessageRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1763434963522415736L;
	
	/**
	 * rpc的请求id
	 */
	private String messageId;
	/**
	 * 请求的类名称
	 */
	private String className;
	/**
	 * 请求的方法名称
	 */
	private String methodName;
	/**
	 * 请求的参数类型
	 */
	private Class<?>[] typeParameters;
	/**
	 * 请求参数值
	 */
	private Object[] parametersVal;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getTypeParameters() {
		return typeParameters;
	}
	public void setTypeParameters(Class<?>[] typeParameters) {
		this.typeParameters = typeParameters;
	}
	public Object[] getParametersVal() {
		return parametersVal;
	}
	public void setParametersVal(Object[] parametersVal) {
		this.parametersVal = parametersVal;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("messageId", messageId).append("className", className)
                .append("methodName", methodName).toString();
    }
	
	
}
