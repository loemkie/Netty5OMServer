package com.mylike.oms.netty.util;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * xml配置读取器
 * @author mingzou
 * 2009-08-31 huangjian 修改parse方法确保线程安全
 *
 */
public class XMLParser {
	
	private static Log log = LogFactory.getLog(XMLParser.class);

	private static SAXReader saxReader = new SAXReader();
	

	/**
	 * 根据配置文件数据流取得Document
	 * @param stream 配置文件数据流 
	 * @return
	 * @throws DocGenException
	 */
	public static Document parse(InputStream stream) throws Exception{
		try {
			//add by huangjian
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(stream);			
			return doc;
		} catch (Exception e) {
		    if(log.isDebugEnabled()){
		    	log.debug(e.getCause());
		    }
			throw new Exception(e);
		}		
	}
}
