package com.mylike.oms.netty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.alibaba.druid.util.StringUtils;
import com.mylike.oms.netty.util.PropertiesUtil;

/**
 * ���ò�������-��Ҫ���첽�ķ�ʽʵ��
 */
public class OmsHttpClient {
	private static final Logger logger = Logger.getLogger(OmsHttpClient.class);

	public static void main(String[] args) {
		String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
				"<Transfer attribute=\"Connect\">"+
				"	<outer to=\"15980882896\"/>"+
				"	<ext id=\"8003\"/>"+
				"</Transfer>";
		OmsHttpClient.sendMsg(PropertiesUtil.getOmsHost("1601") + ":" + PropertiesUtil.getOmsPort(),msg);
	}

	public static void sendMsg(String server,String msg) {
		//���û����Ĭ��ȡ���õ�
		if(StringUtils.isEmpty(server)){
			server=PropertiesUtil.getOmsHost() + ":" + PropertiesUtil.getOmsPort();
		}
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL("http://" + server + "/xml");
			// �򿪺�URL֮������ӣ��豸��ַ��webԶ�̶˿�
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������
			out.print(msg);
			// flush������Ļ���
			out.flush();
			logger.info("��������: " + msg);
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.info("Ӧ�����ݣ�"+result);
		} catch (Exception e) {
			logger.error("���� POST ��������쳣��", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("IO �����쳣��", ex);
			}
		}
	}
}
