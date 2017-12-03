package com.mylike.oms.netty.server;

import org.apache.log4j.Logger;

import com.mylike.oms.netty.Constants;
import com.mylike.oms.netty.entity.Message.Msg;
import com.mylike.oms.netty.util.PropertiesUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class TcpServer {

	private static final Logger logger = Logger.getLogger(TcpServer.class);
	
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
		b.option(ChannelOption.SO_BACKLOG, 1024); //������  
        b.option(ChannelOption.TCP_NODELAY, true);  //���ӳ٣���Ϣ��������  
        b.childOption(ChannelOption.SO_KEEPALIVE, true); //����
        
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				//1.tcpClient for python
				pipeline.addLast(new ProtobufDecoder(Msg.getDefaultInstance()));
				//decoded
				ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
				//encoded
				ch.pipeline().addLast(new LengthFieldPrepender(4));
				pipeline.addLast(new ProtobufEncoder());
				
				/*// ����ͨ��4�ֽ�Headerָ����Body���Ƚ���Ϣ�и�  tcpClinent for java
                pipeline.addLast("frameDecoder",   
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  
                // ����frameDecoder������������һ����Ϣ��protobuf�ֽ���ת��Student����  
                pipeline.addLast("protobufDecoder",  
                        new ProtobufDecoder(Msg.getDefaultInstance().getDefaultInstance()));  

                // ����д����ֽ������4�ֽ�Headerǰ׺��ָ��Body����  
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));  
                  
                // ����Student����ת��protobuf�ֽ���  
                pipeline.addLast("protobufEncoder", new ProtobufEncoder());*/
				
				pipeline.addLast(new TcpServerHandler());
			}
		});

		ChannelFuture f = b.bind(PropertiesUtil.getHost(), PropertiesUtil.getPort()).sync();
		logger.info("Tcp Python ������������");
//		f.channel().closeFuture().sync();
	}
	
	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}

