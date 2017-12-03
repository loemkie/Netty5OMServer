package com.mylike.oms.netty.entity;
/**
 * 应答客户端信息
 * @author loemkie
 *
 */
public class RespMsg {
	String from;//从谁打电话
	String eventType;//事件类型
	String to;//内置分机号
	String extId;//分机号
	String callId;//呼叫ID
	String transType;//传输类型  visitor:客服打给客户, outer:客户打给客服 
	Crd crd;//通话记录
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getExtId() {
		return extId;
	}

	public void setExtId(String extId) {
		this.extId = extId;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Crd getCrd() {
		return crd;
	}

	public void setCrd(Crd crd) {
		this.crd = crd;
	}
	
}
