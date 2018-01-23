package com.mylike.oms.netty.server;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.mylike.oms.netty.client.OmsHttpClient;
import com.mylike.oms.netty.entity.Crd;
import com.mylike.oms.netty.entity.EventType;
import com.mylike.oms.netty.entity.Message.Msg;
import com.mylike.oms.netty.entity.RespMsg;
import com.mylike.oms.netty.entity.TransType;
import com.mylike.oms.netty.util.KsUtil;
import com.mylike.oms.netty.util.PropertiesUtil;
import com.mylike.oms.netty.util.XMLParser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;  
	
	public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {  
		public static ChannelGroup channelGroup=new DefaultChannelGroup ("tcpServer", GlobalEventExecutor.INSTANCE);
		
	    private static final Logger logger = Logger.getLogger(TcpServerHandler.class);  
	    @Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			channelGroup.add(ctx.channel());
		}
		public static String buildCmd(String omsHost,String cmd){
			String result =  "";
			try{
				String extId = parseExtId(cmd);
				String req = buildExtReq(extId);
				result = OmsHttpClient.sendMsg(omsHost,req);
//				result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Status><ext id=\"208\"><lineid>Phone 1</lineid><group id=\"1\"/><group id=\"2\"/><staffid>1304081</staffid><Call_Pickup>yes</Call_Pickup><Fwd_Number>18603752801</Fwd_Number><Call_Restriction>3</Call_Restriction><Off_Line_Num>200</Off_Line_Num><mobile>18603752800</mobile><fork>18603752802</fork><email>admin@hotmail.com</email><record>on</record><api>7</api><voicefile>welcome</voicefile><state>active</state><outer id=\"8\" from=\"208\" to=\"13012345678\" trunk=\"02161208234\" callid=\"28680\"><state>talk</state></outer></ext></Status>";
				if(StringUtil.isNullOrEmpty(result)){
					result = cmd;
				}else{
					//在cmd中加入trunk
					String trunk = parseTrunk(result);
					result = addTrunk(cmd, trunk);
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
			return result;
		}
		public static String addTrunk(String cmd,String trunk){
			String cmdx = "";
			try{
				InputStream io = new ByteArrayInputStream(cmd.getBytes("UTF-8"));
	    		Document doc=XMLParser.parse(io);
	    		Element root = doc.getRootElement();
	    		
				if(!StringUtil.isNullOrEmpty(trunk)){
					Element meta = root.addElement("trunk");
					meta.addAttribute("id", trunk);
					cmdx = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"+root.asXML();
				}else{
					cmdx = cmd;
				}
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
			return cmdx;
		}
		public static String parseTrunk(String cmd){
			String trunk = "";
			try{
				InputStream io = new ByteArrayInputStream(cmd.getBytes("UTF-8"));
	    		Document doc=XMLParser.parse(io);
	    		Element root = doc.getRootElement();
	    		//设置分机号
				if( root.elementIterator("ext").hasNext()){
	    			Element ext =  (Element) root.elementIterator("ext").next();
	    			Element outer =(Element) ext.elementIterator("outer").next();
	    			if(outer != null){
	    				trunk = outer.attribute("trunk").getValue();
	    			}
				}
				/*if(!StringUtil.isNullOrEmpty(trunk)){
					Element meta = root.addElement("trunk");
					meta.addAttribute("id", trunk);
					cmd = root.asXML();
				}else{
					cmdx = cmd;
				}*/
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
			return trunk;
		}
		public static String buildExtReq(String extId){
			String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Control attribute=\"Query\"><ext id=\""+extId+"\"/></Control>";
			return xml;
		}
		public static String parseExtId(String cmd){
			String extId = "";
			try{
				InputStream io = new ByteArrayInputStream(cmd.getBytes("UTF-8"));
	    		Document doc=XMLParser.parse(io);
	    		Element root = doc.getRootElement();
	    		//设置分机号
				if( root.elementIterator("ext").hasNext()){
	    			Element ext =  (Element) root.elementIterator("ext").next();
	    			if(ext != null){
	    				extId = ext.attribute("id").getValue();
	    			}
				}
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
			return extId;
		}
	    @Override  
	    protected void channelRead0(ChannelHandlerContext ctx, Object msg)  
	            throws Exception {
	    	try{
		        logger.info("SERVER接收到消息:\nchannelId:"+ctx.channel().id().asShortText()+"\nmsg:"+msg.toString());
		        if(msg instanceof Msg){//从python客户端发的消息
		        	String cmd="201";
		        	if(((Msg) msg).getCmd().indexOf("EXTID")!=-1){
		        		cmd="200";//初始化拨号设备，并建立连接
		        	}
			        Msg rep=Msg.newBuilder().setCmd(cmd).setId(ctx.channel().id().asShortText()).build();
			        /*if(!channelGroup.contains(ctx.channel())){
			        	channelGroup.add(ctx.channel());
			        }*/
			        //先应答客户端
			        ctx.channel().writeAndFlush(rep);
			        
			        //发送消息至OM TODO 需要改成异步发送
			        if(!StringUtils.equals(cmd, "200")){
			        	//如果在本地测试的情况需要注释掉，因为没有拨号设备 TODO
			        	//根据分机号获取中继号，并修改cmd命令
			        	String serverStr=((Msg) msg).getOmServer();
				        String ar[] = serverStr.split("\\|");
				        String server=ar[0];
				        String trunk = "";
				        if(ar.length==2){
				        	trunk = ar[1];
				        }
			        	String omsHost=PropertiesUtil.getOmsHost(server);
//			        	String cmdx = buildCmd(omsHost,((Msg) msg).getCmd());
			        	String cmdx = "";
			        	if(StringUtil.isNullOrEmpty(trunk)){
			        		cmdx = ((Msg) msg).getCmd();
			        	}else{
			        		cmdx = addTrunk(((Msg) msg).getCmd(), trunk);
			        	}
			        	
			        	logger.info("omsHost: "+omsHost+" dial");
			        	OmsHttpClient.sendMsg(omsHost,cmdx);
			        }
		        }else{//从OM设备发的消息
		        	String xml=msg.toString();
		        	//去掉头部
		        	int index=xml.indexOf("<?");
		        	if(index>0){
		        		xml=xml.substring(index);
		        	}
		        	//发送报文到服务器
		        	RespMsg respMsg = this.parseRespMsg(xml);
		        	logger.info(JSON.toJSONString(respMsg));
		        	//发送录音文件
		        	if(respMsg.getCrd() != null){
		        		//写入数据到app服务器
			        	try{
			        		//add by loemkie 0816
			        		KsUtil.httpPostWithJSON(PropertiesUtil.getAppUrl(), JSON.toJSONString(respMsg.getCrd()));
			        	}catch (Exception e) {
			        		logger.error("发送通话记录到服务器异常："+e.getMessage(),e);
						}
		        		for (Channel channel : channelGroup) {//改为发送给某一个分机号 TODO
		        			//OU|from|to|recording
			        		Msg rep=Msg.newBuilder().setCmd(respMsg.getCrd().getType()+"|"+respMsg.getCrd().getCpn()
			        				+"|"+respMsg.getCrd().getCdpn()+"|"
			        				+respMsg.getCrd().getRecording()+"|"+respMsg.getCrd().getCallId()).setId(ctx.channel().id().asShortText()).build();
//			        		Msg rep=Msg.newBuilder().setCmd(JSON.toJSONString(respMsg.getCrd())).setId(ctx.channel().id().asShortText()).build();
			        		//下发需要的字段 再根据callid来查询
		        			/*Crd crd = new Crd();
		        			crd.setCallId(respMsg.getCrd().getCallId());
		        			crd.setType(respMsg.getCrd().getType());
		        			crd.setCpn(respMsg.getCrd().getCpn());
		        			crd.setCdpn(respMsg.getCrd().getCdpn());
		        			crd.setRecording(respMsg.getCrd().getRecording());
			        		Msg rep=Msg.newBuilder().setCmd(JSON.toJSONString(crd)).setId(ctx.channel().id().asShortText()).build();*/
		        			channel.writeAndFlush(rep);
						}
		        	}
		        	//只下发响铃的数据
		        	if(EventType.ALERT.getValue().equals(respMsg.getEventType())
		        			||EventType.RING.getValue().equals(respMsg.getEventType())){
			        	//TODO 保存消息内容
		        		//JSON.toJSONString();
			        	for (Channel channel : channelGroup) {//改为发送给某一个分机号 TODO
			        		Msg rep=Msg.newBuilder().setCmd(respMsg.getFrom()+"|"+respMsg.getExtId()).setId(ctx.channel().id().asShortText()).build();
			        		try{
			        			channel.writeAndFlush(rep);
			        		}catch (Exception e) {
								logger.error(e.getMessage()+"发送客户端消息失败!",e);
							}
						}
		        	}
		        }
	    	}catch (Exception e) {
				logger.error("接收消息异常："+e.getMessage(),e);
			}
	    }
	    /**
	     * 解析报文
	     * @param msg
	     * @return
	     */
	    private RespMsg parseRespMsg(String xml){
	    	RespMsg respMsg = new RespMsg();
	    	try{
		    	String eventType="";
		    	xml=xml.replaceAll("\\\\", "");
	        	InputStream io = new ByteArrayInputStream(xml.getBytes("UTF-8"));
	    		Document doc=XMLParser.parse(io);
	    		Element root = doc.getRootElement();
	    		eventType=root.attributeValue("attribute");
	    		if(EventType.TRANSIENT.getValue().equals(eventType)//转接
	    			||EventType.ANSWERED.getValue().equals(eventType)//接通
	    			||EventType.BYE.getValue().equals(eventType)
	    			||EventType.RING.getValue().equals(eventType)
	    			||EventType.ALERT.getValue().equals(eventType)){//挂断
	    			//from 不为空的场景
	    			Element child1 = (Element)root.elements().get(0);
	    			if(TransType.OUTER.getValue().equals(child1.getQName().getName())){//客服打给客户
		    			Element outer =  (Element) root.elementIterator("outer").next();
		    			buildRespMsg(respMsg,outer,eventType,TransType.OUTER.getValue());
	    			}else if(TransType.VISITOR.getValue().equals(child1.getQName().getName())){//客服打给客户
	    				Element visitor =  (Element) root.elementIterator("visitor").next();
	    				buildRespMsg(respMsg,visitor,eventType,TransType.OUTER.getValue());
	    			}
	    			//设置分机号
	    			if( root.elementIterator("ext").hasNext()){
		    			Element ext =  (Element) root.elementIterator("ext").next();
		    			if(ext != null){
		    				respMsg.setExtId(ext.attribute("id").getValue());
		    			}
	    			}
	    		}
	    		//代表通话记录
	    		if("Cdr".equals(root.getQName().getName())){
	    			Crd crd = new Crd();
	    			Element callid=(Element) root.elementIterator("callid").next();
	    			Element timeStart=(Element) root.elementIterator("TimeStart").next();
	    			Element timeEnd=(Element) root.elementIterator("TimeEnd").next();
	    			Element cpn=(Element) root.elementIterator("CPN").next();
	    			Element cdpn=(Element) root.elementIterator("CDPN").next();
	    			if(root.elementIterator("Recording").hasNext()){
	    				Element recording=(Element) root.elementIterator("Recording").next();
	    				if(recording!=null){
		    				crd.setRecording(recording.getText());
		    			}
	    			}else{
	    				crd.setRecording("");
	    			}
	    			Element type=(Element) root.elementIterator("Type").next();
	    			Element duration=(Element) root.elementIterator("Duration").next();
	    			if(root.elementIterator("TrunkNumber").hasNext()){
		    			Element trunkNumber=(Element) root.elementIterator("TrunkNumber").next();
		    			if(trunkNumber !=null){
		    				crd.setTrunkNumber(trunkNumber.getText());
		    			}
	    			}
	    			crd.setCallId(callid.getText());
	    			crd.setTimeStart(timeStart.getText());
	    			crd.setTimeEnd(timeEnd.getText());
	    			crd.setCpn(cpn.getText());
	    			crd.setCdpn(cdpn.getText());
	    			crd.setType(type.getText());
	    			crd.setDuration(duration.getText());
	    			respMsg.setCrd(crd);
	    		}
	    	}catch (Exception e) {
	    		logger.error("解析消息异常："+e.getMessage(),e);
			}
	    	return respMsg;
	    }
	    /**
	     * 构建消息
	     * @param respMsg
	     * @param element
	     * @param eventType
	     */
	    private void buildRespMsg(RespMsg respMsg,Element element,String eventType,String transType){
	    	respMsg.setFrom(element.attribute("from").getValue());
    		respMsg.setTo(element.attribute("to").getValue());
    		respMsg.setCallId(element.attribute("callid").getValue());
    		respMsg.setEventType(eventType);
    		respMsg.setTransType(transType);
	    }
	    @Override
	    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
	    	channelGroup.remove(ctx.channel());
	    	logger.info("handlerRemoved remove "+ctx.channel().id().asShortText()+"-"+ctx.channel().remoteAddress());
	    }
	    @Override
	    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	    	logger.info("channelInactive "+ctx.channel().id().asShortText()+"-"+ctx.channel().remoteAddress());
	    }
	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
	        Channel incoming = ctx.channel();
	        logger.info("channelActive:"+ctx.channel().id().asShortText()+"-"+incoming.remoteAddress()+"在线");
	    }
	    @Override  
	    public void exceptionCaught(ChannelHandlerContext ctx,  
	            Throwable cause) throws Exception {  
//	        logger.warn("Unexpected exception from downstream.", cause);  
	        ctx.close();
	    } 
	    @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelUnregistered();
        }
}
