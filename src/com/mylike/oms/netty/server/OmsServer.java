package com.mylike.oms.netty.server;

import org.apache.log4j.Logger;

import com.mylike.oms.netty.Constants;
import com.mylike.oms.netty.util.PropertiesUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * ������OMS�豸�����ı���
 * @author loemkie
 *
 */
public class OmsServer {

	private static final Logger logger = Logger.getLogger(OmsServer.class);
        /*
	 * NioEventLoopGroupʵ���Ͼ��Ǹ��̳߳�,
	 * NioEventLoopGroup�ں�̨������n��NioEventLoop������Channel�¼�,
	 * ÿһ��NioEventLoop������m��Channel,
	 * NioEventLoopGroup��NioEventLoop�����ﰤ��ȡ��NioEventLoop������Channel
	 */
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(Constants.BIZGROUPSIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.BIZTHREADSIZE);
	
	public static void run() throws Exception {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				/*pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast(new TcpServerHandler());*/
				pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast("handler", new TcpServerHandler());
			}
		});

		b.bind(PropertiesUtil.getHost(), PropertiesUtil.getOmsPort()).sync();
		logger.info("OMS ������������");
	}
	
	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		logger.info("��ʼ���� OMS ������...");
//		OmsServer.run();
//		OmsServer.shutdown();
	}
}
