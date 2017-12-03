package com.mylike.oms.netty.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.mylike.oms.netty.util.PropertiesUtil;

public class OmsSocketClient {
	private static final Logger logger = Logger.getLogger(OmsSocketClient.class);
    public static void main(String[] args) throws IOException {
        try {
            //���ַ���
            /*String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"+
					"<Event attribute=\"TRANSIENT\">\n"+
					"   <outer id=\"6\" from=\"15980882896\" to=\"8109\" trunk=\"207\" callid=\"24582\"  />\n"+
					"   <ext id=\"8109\" />\n"+
					"</Event>";*/
        	//�ͷ���绰���ͻ�
           String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"+
            		"   <Cdr id=\"320170223105317-0\">\n"+
            		"     <callid>45061</callid>\n"+
            		"     <outer id=\"7\" />\n"+
            		"    <TimeStart>20170223105242</TimeStart>\n"+
            		"     <Type>OU</Type>\n"+
            		"     <Route>XO</Route>\n"+
            		"     <CPN>0592-23678757</CPN>\n"+
            		"     <CDPN>18850547612</CDPN>\n"+
            		"     <TimeEnd>20170223105317</TimeEnd>\n"+
            		"    <Duration>26</Duration>\n"+
            "     <TrunkNumber>207</TrunkNumber>\n"+
            "     <Recording>20170223/8109_15980882896_20170223_105252_B005_cg.wav</Recording>\n"+
            "     <RecCodec>PCMU</RecCodec>\n"+
            "   </Cdr>";
        	/*String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
			"<Cdr id=\"820170222172627-0\">"+
			 " <callid>24593</callid>"+
			 " <visitor id=\"16\" />"+
			"  <TimeStart>20170222172543</TimeStart>"+
			 " <Type>IN</Type>"+
			 " <Route>XO</Route>"+
			  "<CPN>18850547612</CPN>"+
			 " <CDPN>0592-23678757</CDPN>"+
			  "<TimeEnd>20170222172627</TimeEnd>"+
			 " <Duration>0</Duration>"+
			 "<Recording>20170223/8109_15980882896_20170223_105252_B005_cg.wav</Recording>\n"+
			 " <TrunkNumber>207</TrunkNumber>"+
			"</Cdr>";*/
//        	String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
//			"<Transfer attribute=\"Connect\">"+
//			"	<outer to=\"15980882896\"/>"+
//			"	<ext id=\"8003\"/>"+
//			"</Transfer>";
        	
        	/*String msg="Host: 192.168.1.131"+
        			"	Cache-Control: no-cache"+
        			"	Content-Length: 83"+
			"	<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
			"	<Event attribute=\"CONFIG_CHANGE\">"+
			"	</Event>";*/
        	
			/*String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
			"<Transfer attribute=\"Connect\">"+
			"	<outer to=\"15980882896\"/>"+
			"	<ext id=\"8126\"/>"+
			"</Transfer>";*/
        	
        	/*String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
        	"<Event attribute=\"ALERT\">"+
        	"   <visitor id=\"16\" from=\"15980882895\" to=\"0592-2155142\" callid=\"24592\"  />"+
        	 " <ext id=\"0592-2155142\" />"+
        	"</Event>";*/

        	
        	/*String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
                	"<Event attribute=\"RING\">"+
                	"   <visitor id=\"16\" from=\"18850547612\" to=\"0592-23678757\" callid=\"24592\"  />"+
                	 " <ext id=\"0592-23678757\" />"+
                	"</Event>";*/
            OmsSocketClient socketClient = new OmsSocketClient();
            socketClient.sendMsg(msg);
        }catch (Exception e) {
        	logger.error(e.getMessage(),e);
		}
    }
    public void sendMsg(String msg) throws Exception{
    	Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        
        try {

            socket = new Socket(PropertiesUtil.getHost(), PropertiesUtil.getOmsPort());
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
            // ����һ��Student����������
           /* Msg.Builder builder = Msg.newBuilder();
            builder.setId("1");
            builder.setCmd("�ͻ���");
            Msg msg = builder.build();*/
            //���ַ���
          /*  String msg="<?xml version=\"1.0\" encoding=\"utf-8\" ?>"+
					"<Event attribute=\"TRANSIENT\">"+
					"   <outer id=\"6\" from=\"18850549613\" to=\"8109\" trunk=\"207\" callid=\"24582\"  />"+
					"   <ext id=\"8109\" />"+
					"</Event>";*/
            byte[] outputBytes = msg.getBytes(); // Studentת���ֽ���
            out.writeInt(outputBytes.length); // write header
            out.write(outputBytes); // write body
            out.flush();
            
            // ��ȡ��������������Student
          /*  int bodyLength = in.readInt();  // read header
            byte[] bodyBytes = new byte[bodyLength];
            in.readFully(bodyBytes);  // read body
*/          /*  Msg msg1 = Msg.parseFrom(bodyBytes); // body�ֽ��������Student
            System.out.println("Header:" + bodyLength);
            System.out.println("Body:");
            System.out.println("ID:" + msg1.getId());
            System.out.println("cmd:" + msg1.getCmd());*/
           /* String ret = in.readUTF();     
            System.out.println("�������˷��ع�������: " + ret);    */

        }catch (Exception e) {
			// TODO: handle exception
        	logger.error(e.getMessage(),e);
		} finally {
            // �ر�����
			if(in!=null){
				in.close();
			}
            out.close();
            socket.close();
        }
    }
}