package p2p;

import java.net.DatagramPacket;


//Message Structure:Type<CRLF><CRLF>Data

public class Message{
    private String type;
    private String data;

    public Message(DatagramPacket packet) {
    	
    	String msg = new String(packet.getData());
    	String[] cmd = msg.split("\\r\\n\\r\\n"); //<CRLF><CRLF> 
    	this.type= cmd[0];
    	
    	if(type.equals("RESPONSE_BLOCKCHAIN")) {
    		this.data=cmd[1];
    	}

	else if(type.equals("RESPONSE_TRANSACTION_POOL")) {
		this.data=cmd[1];
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
}
