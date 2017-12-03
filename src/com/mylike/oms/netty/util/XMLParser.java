package com.mylike.oms.netty.util;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

/**
 * xml���ö�ȡ��
 * @author mingzou
 * 2009-08-31 huangjian �޸�parse����ȷ���̰߳�ȫ
 *
 */
public class XMLParser {
	
	private static Log log = LogFactory.getLog(XMLParser.class);

	private static SAXReader saxReader = new SAXReader();
	

	/**
	 * ���������ļ�������ȡ��Document
	 * @param stream �����ļ������� 
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
