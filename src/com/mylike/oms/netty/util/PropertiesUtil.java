package com.mylike.oms.netty.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *配置文件读取工具类
 * @author loemkie
 *
 */
public class PropertiesUtil {
	private static Properties docProps = null;
    private static Log logger = LogFactory.getLog( PropertiesUtil.class );

    static
    {
        try
        {
            if( docProps == null )
            {
                docProps = new Properties();
                docProps.load(PropertiesUtil.getResourceAsStream("om.properties"));
            }
        }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
        catch (IOException e)
        {
            logger.error( "init PropertiesUtil", e );
            e.printStackTrace();
        }
    }

	public static String getProperty(String key) {
		String returnVal = docProps.getProperty(key);
		if (returnVal == null)
			logger.warn("PdfConfigFileName with number(" + key + ") is null");

		return returnVal;
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static String getHost(){
		return getProperty("host");
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static String getOmsHost(String omServer){
		return getProperty("oms.host."+omServer);
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static String getOmsHost(){
		return getProperty("oms.host");
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static String getAppUrl(){
		return getProperty("app.url");
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static int getPort(){
		return new Integer(getProperty("port"));
	}
	/**
	 * 监听主机
	 * @return
	 */
	public static int getOmsPort(){
		return new Integer(getProperty("oms.port"));
	}
    /**
    *
    * @param resouce
    * @return an input stream for reading the resource, or null if the resource could not be found
    */
   public static InputStream getResourceAsStream( String resouce )
   {
       ClassLoader cl = PropertiesUtil.class.getClassLoader();
       if( cl!=null )
           return cl.getResourceAsStream( resouce );
       else
           return ClassLoader.getSystemResourceAsStream( resouce );
   }
}
