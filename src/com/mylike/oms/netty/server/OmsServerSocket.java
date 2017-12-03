package com.mylike.oms.netty.server;

//创建一个文本文件，命名为listenserver.java，编辑代码如下：
//package com.pasier.quanzi.web.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class OmsServerSocket extends ServerSocket {
	OmsServerSocket(int serverPort) throws IOException {
		// 用指定的端口构造一个ServerSocket
		super(serverPort);
		System.out.println("start listen 80\n");
		try {
			while (true) {
				// 监听一端口，等待客户接入
				Socket socket = accept();
				System.out.println("client " + socket + " connected\n");
				// 将会话交给线程处理
				new ServerThread(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(); // 关闭监听端口
		}
	}

	// inner-class ServerThread
	class ServerThread extends Thread {
		private Socket socket;
		private BufferedReader in;

		// Ready to conversation
		public ServerThread(Socket s) throws IOException {
			this.socket = s;
			// 构造该会话中的输入输出流
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			start();
		}

		// Execute conversation
		public void run() {
			try {
				int n = 1;
				while (true) {
					// 通过输入流接收客户端信息
					String line = in.readLine();
					if (line == null || line.length() <= 0) {
						if (n == 1) {// API信息中过滤http头和内容直接的空白
							n = 0;
							System.out.println("Receivedmessage: " + line);
							continue;
						} else {
							break;
						}
					}
					System.out.println("Receivedmessage: " + line);
				}
				in.close();
				socket.close();
				// 处理事件、话单或其他信息
				System.out.println("client " + socket + " finished\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// main method
	public static void main(String[] args) throws IOException {
		new OmsServerSocket(80);
	}
}
