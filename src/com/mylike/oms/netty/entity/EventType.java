package com.mylike.oms.netty.entity;
/**
 * �¼�����--�Ϻ�ѶʱOM 50
 * @author loemkie
 *
 */
public enum EventType {
	ANSWERED ("ANSWERED"),//��ͨ
	TRANSIENT ("TRANSIENT"),//ת����
	ALERT ("ALERT"),//����
	RING ("RING"),//����
	BYE ("BYE");//�Ҷ�
	
	// ����˽�б���
    private final String value ;

    // ���캯����ö������ֻ��Ϊ˽��
    private EventType( String value) {
        this. value = value;
    }

	public String getValue() {
		return value;
	}
}
