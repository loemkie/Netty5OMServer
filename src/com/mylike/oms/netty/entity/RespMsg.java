package com.mylike.oms.netty.entity;
/**
 * Ӧ��ͻ�����Ϣ
 * @author loemkie
 *
 */
public class RespMsg {
	String from;//��˭��绰
	String eventType;//�¼�����
	String to;//���÷ֻ���
	String extId;//�ֻ���
	String callId;//����ID
	String transType;//��������  visitor:�ͷ�����ͻ�, outer:�ͻ�����ͷ� 
	Crd crd;//ͨ����¼
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
