package com.mylike.oms.netty.util;

import java.net.URLEncoder;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/**
 * 快商通工具类
 * 
 * @author loemkie
 *
 */
public class KsUtil {
	private static final long serialVersionUID = 1L;
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	private static final String UTF_8 = "UTF-8";


	public static void httpPostWithJSON(String url, String json) throws Exception {
		// 将JSON进行UTF-8编码,以便传输中文
		String encoderJson = URLEncoder.encode(json, UTF_8);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

		StringEntity se = new StringEntity(encoderJson);
		se.setContentType(CONTENT_TYPE_TEXT_JSON);
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
		httpPost.setEntity(se);
		httpclient.execute(httpPost);
	}
	public static void main(String[] args) throws Exception {
		KsUtil.httpPostWithJSON(PropertiesUtil.getAppUrl(), "{id:1}");
	}
}