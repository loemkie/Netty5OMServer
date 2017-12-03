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
	 * NioEventLoopGroup实际上就是个线程池,
	 * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
	 * 每一个NioEventLoop负责处理m个Channel,
	 * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
	 */
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(Constants.BIZGROUPSIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.BIZTHREADSIZE);
	
	public static void run() throws Exception {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.option(ChannelOption.SO_BACKLOG, 1024); //连接数  
        b.option(ChannelOption.TCP_NODELAY, true);  //不延迟，消息立即发送  
        b.childOption(ChannelOption.SO_KEEPALIVE, true); //长连
        
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
				
				/*// 负责通过4字节Header指定的Body长度将消息切割  tcpClinent for java
                pipeline.addLast("frameDecoder",   
                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  
                // 负责将frameDecoder处理后的完整的一条消息的protobuf字节码转成Student对象  
                pipeline.addLast("protobufDecoder",  
                        new ProtobufDecoder(Msg.getDefaultInstance().getDefaultInstance()));  

                // 负责将写入的字节码加上4字节Header前缀来指定Body长度  
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));  
                  
                // 负责将Student对象转成protobuf字节码  
                pipeline.addLast("protobufEncoder", new ProtobufEncoder());*/
				
				pipeline.addLast(new TcpServerHandler());
			}
		});

		ChannelFuture f = b.bind(PropertiesUtil.getHost(), PropertiesUtil.getPort()).sync();
		logger.info("Tcp Python 服务器已启动");
//		f.channel().closeFuture().sync();
	}
	
	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}

