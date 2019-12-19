package com.hs.netty.imitate.rpc.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 响应实体
 * @author 王海生
 *
 */
public class MessageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8838970618741550164L;
	/**
	 * rpc的请求id
	 */
	private String messageId;
	/**
	 * 状态码
	 */
	 private String error;
	 /**
	  * 结果
	  */
    private Object resultDesc;
    
    
    
    public String getMessageId() {
		return messageId;
	}



	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}



	public String getError() {
		return error;
	}



	public void setError(String error) {
		this.error = error;
	}



	public Object getResultDesc() {
		return resultDesc;
	}



	public void setResultDesc(Object resultDesc) {
		this.resultDesc = resultDesc;
	}



	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("messageId", messageId).append("error", error).toString();
    }
}
