package com.mylike.oms.netty;

public class Constants {
	/**
	 * ����
	 */
	public static final String HOST = "192.168.1.131";
	public static final int PORT = 9999;//�˿�
	public static final String OMS_HOST = "192.168.1.240";
	public static final int OMS_PORT = 80;//OM�˿�
	/**���ڷ��䴦��ҵ���̵߳��߳������ */
	public static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;	//Ĭ��
	/** ҵ������̴߳�С*/
	public static final int BIZTHREADSIZE = 4;
}
