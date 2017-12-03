package com.mylike.oms.netty;

public class Constants {
	/**
	 * 主机
	 */
	public static final String HOST = "192.168.1.131";
	public static final int PORT = 9999;//端口
	public static final String OMS_HOST = "192.168.1.240";
	public static final int OMS_PORT = 80;//OM端口
	/**用于分配处理业务线程的线程组个数 */
	public static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;	//默认
	/** 业务出现线程大小*/
	public static final int BIZTHREADSIZE = 4;
}
