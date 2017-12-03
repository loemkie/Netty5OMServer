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
            
            // 创建一个Student传给服务器
           Msg.Builder builder = Msg.newBuilder();
            builder.setId("1");
            builder.setCmd("客户端");
            Msg msg = builder.build();
            byte[] outputBytes = msg.toByteArray(); // Student转成字节码
            out.writeInt(outputBytes.length); // write header
            out.write(outputBytes); // write body
            out.flush();
            
            // 获取服务器传过来的Student
            int bodyLength = in.readInt();  // read header
            byte[] bodyBytes = new byte[bodyLength];
            in.readFully(bodyBytes);  // read body
            Msg msg1 = Msg.parseFrom(bodyBytes); // body字节码解析成Student
            System.out.println("Header:" + bodyLength);
            System.out.println("Body:");
            System.out.println("ID:" + msg1.getId());
            System.out.println("cmd:" + msg1.getCmd());

        } finally {
            // 关闭连接
            in.close();
            out.close();
            socket.close();
        }
    }
}