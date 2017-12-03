package com.mylike.oms.netty.server;

//����һ���ı��ļ�������Ϊlistenserver.java���༭�������£�
//package com.pasier.quanzi.web.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class OmsServerSocket extends ServerSocket {
	OmsServerSocket(int serverPort) throws IOException {
		// ��ָ���Ķ˿ڹ���һ��ServerSocket
		super(serverPort);
		System.out.println("start listen 80\n");
		try {
			while (true) {
				// ����һ�˿ڣ��ȴ��ͻ�����
				Socket socket = accept();
				System.out.println("client " + socket + " connected\n");
				// ���Ự�����̴߳���
				new ServerThread(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(); // �رռ����˿�
		}
	}

	// inner-class ServerThread
	class ServerThread extends Thread {
		private Socket socket;
		private BufferedReader in;

		// Ready to conversation
		public ServerThread(Socket s) throws IOException {
			this.socket = s;
			// ����ûỰ�е����������
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			start();
		}

		// Execute conversation
		public void run() {
			try {
				int n = 1;
				while (true) {
					// ͨ�����������տͻ�����Ϣ
					String line = in.readLine();
					if (line == null || line.length() <= 0) {
						if (n == 1) {// API��Ϣ�й���httpͷ������ֱ�ӵĿհ�
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
				// �����¼���������������Ϣ
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
