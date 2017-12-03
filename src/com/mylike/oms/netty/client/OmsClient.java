package com.mylike.oms.netty.client;

import org.apache.log4j.Logger;

import com.mylike.oms.netty.Constants;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class OmsClient {
	private static final Logger logger = Logger.getLogger(OmsClient.class);
	public static Bootstrap bootstrap = getBootstrap();
	public static Channel channel = getChannel(Constants.HOST,Constants.OMS_PORT);
	/**
	 * 初始化Bootstrap
	 * @return
	 */
	public static final Bootstrap getBootstrap(){
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast("handler", new TcpClientHandler());
			}
		});
		b.option(ChannelOption.SO_KEEPALIVE, true);
		return b;
	}

	public static final Channel getChannel(String host,int port){
		Channel channel = null;
		try {
			channel = bootstrap.connect(host, port).sync().channel();
		} catch (Exception e) {
			logger.error(String.format("连接Server(IP[%s],PORT[%s])失败", host,port),e);
			return null;
		}
		return channel;
	}

	public static void sendMsg(String msg) throws Exception {
		if(channel!=null){
			channel.writeAndFlush(msg).sync();
		}else{
			logger.warn("消息发送失败,连接尚未建立!");
		}
	}

    public static void main(String[] args) throws Exception {
		try {
			long t0 = System.nanoTime();
			for (int i = 0; i < 1; i++) {
				OmsClient.sendMsg("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
					"<Event attribute=\"TRANSIENT\">"+
					"   <outer id=\"6\" from=\"8003\" to=\"15980882896\" trunk=\"207\" callid=\"24582\"  />"+
					"   <ext id=\"8109\" />"+
					"</Event>");
			}
			long t1 = System.nanoTime();
			System.out.println((t1-t0)/1000000.0);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
