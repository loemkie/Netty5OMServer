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
	
	public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {  
		public static ChannelGroup channelGroup=new DefaultChannelGroup ("tcpServer", GlobalEventExecutor.INSTANCE);
		
	    private static final Logger logger = Logger.getLogger(TcpServerHandler.class);  
	@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			channelGroup.add(ctx.channel());
		}
	    @Override  
	    protected void channelRead0(ChannelHandlerContext ctx, Object msg)  
	            throws Exception {
	    	try{
		        logger.info("SERVER���յ���Ϣ:\nchannelId:"+ctx.channel().id().asShortText()+"\nmsg:"+msg.toString());
		        if(msg instanceof Msg){//��python�ͻ��˷�����Ϣ
		        	String cmd="201";
		        	if(((Msg) msg).getCmd().indexOf("EXTID")!=-1){
		        		cmd="200";//��ʼ�������豸������������
		        	}
			        Msg rep=Msg.newBuilder().setCmd(cmd).setId(ctx.channel().id().asShortText()).build();
			        /*if(!channelGroup.contains(ctx.channel())){
			        	channelGroup.add(ctx.channel());
			        }*/
			        //��Ӧ��ͻ���
			        ctx.channel().writeAndFlush(rep);
			        String server=((Msg) msg).getOmServer();
			        //������Ϣ��OM TODO ��Ҫ�ĳ��첽����
			        if(!StringUtils.equals(cmd, "200")){
			        	//����ڱ��ز��Ե������Ҫע�͵�����Ϊû�в����豸 TODO
			        	String omsHost=PropertiesUtil.getOmsHost(server);
			        	logger.info("omsHost: "+omsHost+" dial");
			        	OmsHttpClient.sendMsg(omsHost,((Msg) msg).getCmd());
			        }
		        }else{//��OM�豸������Ϣ
		        	String xml=msg.toString();
		        	//ȥ��ͷ��
		        	int index=xml.indexOf("<?");
		        	if(index>0){
		        		xml=xml.substring(index);
		        	}
		        	//���ͱ��ĵ�������
		        	RespMsg respMsg = this.parseRespMsg(xml);
		        	logger.info(JSON.toJSONString(respMsg));
		        	//����¼���ļ�
		        	if(respMsg.getCrd() != null){
		        		//д�����ݵ�app������
			        	try{
			        		//add by loemkie 0816
			        		KsUtil.httpPostWithJSON(PropertiesUtil.getAppUrl(), JSON.toJSONString(respMsg.getCrd()));
			        	}catch (Exception e) {
			        		logger.error("����ͨ����¼���������쳣��"+e.getMessage(),e);
						}
		        		for (Channel channel : channelGroup) {//��Ϊ���͸�ĳһ���ֻ��� TODO
		        			//OU|from|to|recording
			        		Msg rep=Msg.newBuilder().setCmd(respMsg.getCrd().getType()+"|"+respMsg.getCrd().getCpn()
			        				+"|"+respMsg.getCrd().getCdpn()+"|"
			        				+respMsg.getCrd().getRecording()+"|"+respMsg.getCrd().getCallId()).setId(ctx.channel().id().asShortText()).build();
//			        		Msg rep=Msg.newBuilder().setCmd(JSON.toJSONString(respMsg.getCrd())).setId(ctx.channel().id().asShortText()).build();
			        		//�·���Ҫ���ֶ� �ٸ���callid����ѯ
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
		        	//ֻ�·����������
		        	if(EventType.ALERT.getValue().equals(respMsg.getEventType())
		        			||EventType.RING.getValue().equals(respMsg.getEventType())){
			        	//TODO ������Ϣ����
		        		//JSON.toJSONString();
			        	for (Channel channel : channelGroup) {//��Ϊ���͸�ĳһ���ֻ��� TODO
			        		Msg rep=Msg.newBuilder().setCmd(respMsg.getFrom()+"|"+respMsg.getExtId()).setId(ctx.channel().id().asShortText()).build();
			        		try{
			        			channel.writeAndFlush(rep);
			        		}catch (Exception e) {
								logger.error(e.getMessage()+"���Ϳͻ�����Ϣʧ��!",e);
							}
						}
		        	}
		        }
	    	}catch (Exception e) {
				logger.error("������Ϣ�쳣��"+e.getMessage(),e);
			}
	    }
	    /**
	     * ��������
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
	    		if(EventType.TRANSIENT.getValue().equals(eventType)//ת��
	    			||EventType.ANSWERED.getValue().equals(eventType)//��ͨ
	    			||EventType.BYE.getValue().equals(eventType)
	    			||EventType.RING.getValue().equals(eventType)
	    			||EventType.ALERT.getValue().equals(eventType)){//�Ҷ�
	    			//from ��Ϊ�յĳ���
	    			Element child1 = (Element)root.elements().get(0);
	    			if(TransType.OUTER.getValue().equals(child1.getQName().getName())){//�ͷ�����ͻ�
		    			Element outer =  (Element) root.elementIterator("outer").next();
		    			buildRespMsg(respMsg,outer,eventType,TransType.OUTER.getValue());
	    			}else if(TransType.VISITOR.getValue().equals(child1.getQName().getName())){//�ͷ�����ͻ�
	    				Element visitor =  (Element) root.elementIterator("visitor").next();
	    				buildRespMsg(respMsg,visitor,eventType,TransType.OUTER.getValue());
	    			}
	    			//���÷ֻ���
	    			if( root.elementIterator("ext").hasNext()){
		    			Element ext =  (Element) root.elementIterator("ext").next();
		    			if(ext != null){
		    				respMsg.setExtId(ext.attribute("id").getValue());
		    			}
	    			}
	    		}
	    		//����ͨ����¼
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
	    		logger.error("������Ϣ�쳣��"+e.getMessage(),e);
			}
	    	return respMsg;
	    }
	    /**
	     * ������Ϣ
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
	        logger.info("channelActive:"+ctx.channel().id().asShortText()+"-"+incoming.remoteAddress()+"����");
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