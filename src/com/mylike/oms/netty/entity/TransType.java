package com.mylike.oms.netty.entity;
/**
 * �¼�����--�Ϻ�ѶʱOM 50
 * @author loemkie
 *
 */
public enum TransType {
	VISITOR  ("visitor"),//�ͻ�����ͷ�
	OUTER ("outer");//ת����
	// ����˽�б���
    private final String value ;

    // ���캯����ö������ֻ��Ϊ˽��
    private TransType( String value) {
        this. value = value;
    }
	public String getValue() {
		return value;
	}
}
