package com.mylike.oms.netty.entity;

/**
 * ͨ����¼
 * @author loemkie
 *
 */
public class Crd{
	String callId;
	String timeStart;
	String type;//����
	String route;//·��
	String cpn;//from
	String cdpn;//to
	String timeEnd;
	String duration;//ͨ��ʱ��
	String recording;//¼��id
	String trunkNumber;//�м̺���
	
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getTrunkNumber() {
		return trunkNumber;
	}
	public void setTrunkNumber(String trunkNumber) {
		this.trunkNumber = trunkNumber;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}
	public String getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}
	public String getCpn() {
		return cpn;
	}
	public void setCpn(String cpn) {
		this.cpn = cpn;
	}
	public String getCdpn() {
		return cdpn;
	}
	public void setCdpn(String cdpn) {
		this.cdpn = cdpn;
	}
	public String getRecording() {
		return recording;
	}
	public void setRecording(String recording) {
		this.recording = recording;
	}
	
	
}