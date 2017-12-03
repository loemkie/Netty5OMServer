package com.mylike.oms.netty.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.mylike.oms.netty.entity.Message.Msg;

public class PythonClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        
        try {

            socket = new Socket("localhost", 9999);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
            // ����һ��Student����������
           Msg.Builder builder = Msg.newBuilder();
            builder.setId("1");
            builder.setCmd("�ͻ���");
            Msg msg = builder.build();
            byte[] outputBytes = msg.toByteArray(); // Studentת���ֽ���
            out.writeInt(outputBytes.length); // write header
            out.write(outputBytes); // write body
            out.flush();
            
            // ��ȡ��������������Student
            int bodyLength = in.readInt();  // read header
            byte[] bodyBytes = new byte[bodyLength];
            in.readFully(bodyBytes);  // read body
            Msg msg1 = Msg.parseFrom(bodyBytes); // body�ֽ��������Student
            System.out.println("Header:" + bodyLength);
            System.out.println("Body:");
            System.out.println("ID:" + msg1.getId());
            System.out.println("cmd:" + msg1.getCmd());

        } finally {
            // �ر�����
            in.close();
            out.close();
            socket.close();
        }
    }
}