package com.mylike.oms.netty;

import org.apache.log4j.Logger;

import com.mylike.oms.netty.server.OmsServer;
import com.mylike.oms.netty.server.TcpServer;
public class Platform {
	private static final Logger logger = Logger.getLogger(TcpServer.class);
	public static void main(String[] args) throws Exception {
		logger.info("��ʼ���� Tcp Python ������...");
		TcpServer.run();
		logger.info("��ʼ����  OMS ������...");
		OmsServer.run();
	}
}
