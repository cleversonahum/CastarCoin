package p2p;

import java.net.*;

public class MC extends Channel {
	public MC(char type, String address, int port, int packetS, String name) {
		super(type, address, port, packetS, name);
	}

	public void select (DatagramPacket dPacket) {

		Message M=new Message(dPacket);
		
		if(M.getType().equals("QUERY_LATEST")) {
			
		}
		else if(M.getType().equals("QUERY_ALL")) {
			
		}
		else if(M.getType().equals("RESPONSE_BLOCKCHAIN")) {
			
		}
		else if(M.getType().equals("QUERY_TRANSACTION_POOL")) {
			
		}
		else if(M.getType().equals("RESPONSE_TRANSACTION_POOL")) {
			
		}
	}

}
