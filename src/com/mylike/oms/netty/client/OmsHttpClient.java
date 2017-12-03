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
 * 调用拨号请求-需要以异步的方式实现
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
		//如果没传递默认取配置的
		if(StringUtils.isEmpty(server)){
			server=PropertiesUtil.getOmsHost() + ":" + PropertiesUtil.getOmsPort();
		}
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL("http://" + server + "/xml");
			// 打开和URL之间的连接，设备地址和web远程端口
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(msg);
			// flush输出流的缓冲
			out.flush();
			logger.info("发送数据: " + msg);
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.info("应答数据："+result);
		} catch (Exception e) {
			logger.error("发送 POST 请求出现异常！", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				logger.error("IO 出现异常！", ex);
			}
		}
	}
}
