package p2p;

import java.net.DatagramPacket;


//Message Structure:Type<CRLF><CRLF>Data

public class Message{
    private String type;
    private String data;
    private static byte[] body;
    
    public Message(DatagramPacket packet) {
    	
    	String msg = new String(packet.getData());
    	String[] cmd = msg.split("\\r\\n\\r\\n"); //<CRLF><CRLF> 
    	this.type= cmd[0];
    	int body_index=cmd[0].length()+4;
    	getBodyfromMessage(packet.getData(),body_index,packet.getLength());
    	
    	if(type.equals("RESPONSE_BLOCKCHAIN")) {
    		this.data=cmd[1];
    	}

	else if(type.equals("RESPONSE_TRANSACTION_POOL")) {
		this.data=cmd[1];
    	}
    	else
    	   this.data=null;
    	
    }
    
    public static void getBodyfromMessage(byte[] data, int body_index,int length) {

		int body_size=length - body_index;
		body =new byte[body_size];
		int i;

		//percorre cada elemento da data do packet, começando na zona do body e passa para body
		for(i=body_index; i < length; i++) {
            body[i - body_index] = data[i];
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public byte[] getBody(){
	    return Message.body;
}
}
