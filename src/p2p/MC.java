package p2p;

import blockchain.*;
import transaction.*;

import java.net.*;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;

public class MC extends Channel {
	public MC(char type, String address, int port, int packetS, String name, Blockchain blockchain, TxPool txPool, ArrayList<String> peers) {
		super(type, address, port, packetS, name);
		this.blockchain = blockchain;
		this.txPool = txPool;
		this.peers = peers;
	}
	
	private final static int PORT = 3000;
	private Blockchain blockchain;
	private TxPool txPool;
	private ArrayList<String> peers = new ArrayList<>();

	public void select (DatagramPacket dPacket) {
	    
	    //Selecting Message
		Message M=new Message(dPacket);
		
		if(M.getType().equals("QUERY_LATEST")) {
            queryLatest(dPacket.getAddress().getHostAddress(),dPacket.getPort(), M,this.blockchain);
		}
		else if(M.getType().equals("QUERY_ALL")) {
			queryAll(dPacket.getAddress().getHostAddress(),dPacket.getPort(), M, this.blockchain);
		}
		else if(M.getType().equals("RESPONSE_BLOCKCHAIN")) {
			responseBlockchain(M, this.blockchain, this.txPool);
		}
		else if(M.getType().equals("QUERY_TRANSACTION_POOL")) {
			queryTransactionPool(dPacket.getAddress().getHostAddress(),dPacket.getPort(), M,this.txPool);
		}
		else if(M.getType().equals("RESPONSE_TRANSACTION_POOL")) {
			responseTransactionPool(M, this.blockchain, this.txPool);
		}
		sendMessage(("QUERY_LATEST").getBytes(),dPacket.getAddress().getHostAddress(),dPacket.getPort());
		try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
		broadcastTransactionPool(txPool, this.peers);
	}
	
	private void queryLatest(String address, int port, Message M, Blockchain blockchain) {
	     String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
		    try{
                byte[] headerBytes = header.getBytes();
                byte[] data = serializeObject(blockchain.getLastBlock());
		    
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      		    outputStream.write(headerBytes);
      		    outputStream.write(data);
		    
                byte msg[] = outputStream.toByteArray();
                sendMessage(msg, address, port);
	       }
	       catch(Exception e){e.printStackTrace();}
	}
	
	private void queryAll(String address, int port, Message M, Blockchain blockchain) {
        String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(blockchain.getBlockchain());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
		    
            byte msg[] = outputStream.toByteArray();
                
            sendMessage(msg, address, port);
        }
        catch(Exception e){e.printStackTrace();}
	}
	
	private void responseBlockchain(Message M, Blockchain blockchain, TxPool txPool) {
	    try {
            Block receivedBlock = (Block)deserializeBytes(M.getBody());
            ArrayList<Block> receivedBlocks = new ArrayList<Block>();
            receivedBlocks.add(receivedBlock);
            if(receivedBlocks.size()==0) {
                System.out.println("Invalid blocks received");
            }
            else
                handleBlockchainResponse(receivedBlocks, blockchain, txPool);
        }
        catch(Exception e){e.printStackTrace();};

	}
	
    private void queryTransactionPool(String address, int port, Message M, TxPool txPool) {
	        String header = "RESPONSE_TRANSACTION_POOL\r\n\r\n";
            try{
                byte[] headerBytes = header.getBytes();
                byte[] data = serializeObject(txPool.getTransactionPool());
                
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(headerBytes);
                outputStream.write(data);
    		    
                byte msg[] = outputStream.toByteArray();
                    
                sendMessage(msg, address, port);
            }
            catch(Exception e){e.printStackTrace();}
    }
    
    private Boolean responseTransactionPool(Message M, Blockchain blockchain, TxPool txPool) {
        try {
            ArrayList<Transaction> receivedTransactions = (ArrayList<Transaction>)deserializeBytes(M.getBody());
            if(receivedTransactions.size()==0 || receivedTransactions==null) {
	           //System.out.println("Invalid transaction received");
	           return false;
            }
            for(Transaction tx:receivedTransactions) {
                blockchain.handleReceivedTransaction(tx, txPool);
                broadcastTransactionPool(txPool, this.peers);
            }
            return true;
        }
        catch(Exception e){e.printStackTrace();};
        return false;
    }

    private Boolean handleBlockchainResponse(ArrayList<Block> receivedBlocks, Blockchain blockchain, TxPool txPool) {
        if(receivedBlocks.size()==0 || receivedBlocks==null) {
            System.out.println("Received blockchain size 0");
            return false;
        }
        
        Block lastBlockReceived = receivedBlocks.get(receivedBlocks.size()-1);
        if(!blockchain.isValidBlockStructure(lastBlockReceived)) {
            System.out.println("Invalid Block Structure");
            return false;
        }
        
        Block lastBlockHeld = blockchain.getLastBlock();
        if(lastBlockReceived.index > lastBlockHeld.index) {
            System.out.println("Blockchain possibly behind.\nReceived Index: "+lastBlockReceived.index+"\nPeer has: "+lastBlockHeld.index);
            if(lastBlockHeld.hash.equals(lastBlockReceived.previousHash)) {
                if(blockchain.addBlockToChain(lastBlockReceived, txPool)) {
                    broadcastLastMsg(blockchain.getBlockchain(), this.peers);
                }
            }
            else if(receivedBlocks.size() == 1) {
                    System.out.println("Query the chain from our peer");
                    broadcastQueryAllMsg();
            }
            else {
                System.out.println("Received Blockchain is longer than current Blockchain");
                blockchain.replaceChain(receivedBlocks, txPool);
            }
            return true;
        }
        else {
            System.out.println("Received Blockchain isn't longer than actual blockchain");
            return true;
        }        
        
    }
    
    public static void broadcastTransactionPool(TxPool txPool, ArrayList<String> peers) {
        String header = "RESPONSE_TRANSACTION_POOL\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(txPool.getTransactionPool());
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
    		    
            byte msg[] = outputStream.toByteArray();
            
            for(String peer : peers) {
                sendMessage(msg, peer, PORT);
            }

        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public static void broadcastLastMsg(ArrayList<Block> blockchain, ArrayList<String> peers) {
        String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(blockchain.get(blockchain.size()-1));
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
    		    
            byte msg[] = outputStream.toByteArray();
            
            for(String peer : peers)
                sendMessage(msg, peer, PORT);

        }
        catch(Exception e){e.printStackTrace();}
    }
    
    private void broadcastQueryAllMsg() {
        String header = "QUERY_ALL\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
    		    
            byte msg[] = outputStream.toByteArray();
                    
            for(String peer : peers)
                sendMessage(msg, peer, PORT);
        }
        catch(Exception e){e.printStackTrace();}
    }
	
    private static Object deserializeBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bytesIn);
        Object obj = ois.readObject();
        ois.close();
        
        return obj;
    }


    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
    
        return bytes;
    }
    
    public void connectPeers(String address) {
        this.peers.add(address);
    }
    
    public ArrayList<String> getPeers() {
        return this.peers;
    }
}
