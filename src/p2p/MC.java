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
	public MC(char type, String address, int port, int packetS, String name, Blockchain blockchain, TxPool txPool) {
		super(type, address, port, packetS, name);
		this.blockchain = blockchain;
		this.txPool = txPool;
	}
	
	private Blockchain blockchain;
	private TxPool txPool;

	public void select (DatagramPacket dPacket) {
		Message M=new Message(dPacket);
		
		if(M.getType().equals("QUERY_LATEST")) {
            queryLatest(M,this.blockchain);
		}
		else if(M.getType().equals("QUERY_ALL")) {
			queryAll(M, this.blockchain);
		}
		else if(M.getType().equals("RESPONSE_BLOCKCHAIN")) {
			responseBlockchain(M, this.blockchain, this.txPool);
		}
		else if(M.getType().equals("QUERY_TRANSACTION_POOL")) {
			queryTransactionPool(M,this.txPool);
		}
		else if(M.getType().equals("RESPONSE_TRANSACTION_POOL")) {
			responseTransactionPool(M, this.blockchain, this.txPool);
		}
	}
	
	private void queryLatest(Message M, Blockchain blockchain) {
	     String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
		    try{
                byte[] headerBytes = header.getBytes();
                byte[] data = serializeObject(blockchain.getLastBlock());
		    
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      		    outputStream.write(headerBytes);
      		    outputStream.write(data);
		    
                byte msg[] = outputStream.toByteArray();
                
                sendMessage(msg, "224.0.0.0", 3000);
	       }
	       catch(Exception e){e.printStackTrace();}
	}
	
	private void queryAll(Message M, Blockchain blockchain) {
        String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(blockchain.getBlockchain());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
		    
            byte msg[] = outputStream.toByteArray();
                
            sendMessage(msg, "224.0.0.0", 3000);
        }
        catch(Exception e){e.printStackTrace();}
	}
	
	private void responseBlockchain(Message M, Blockchain blockchain, TxPool txPool) {
	    try {
            ArrayList<Block> receivedBlocks = (ArrayList<Block>)deserializeBytes(M.getData().getBytes());
            if(receivedBlocks.size()==0) {
                System.out.println("Invalid blocks received");
            }
            else
                handleBlockchainResponse(receivedBlocks, blockchain, txPool);
        }
        catch(Exception e){e.printStackTrace();};

	}
	
    private void queryTransactionPool(Message M, TxPool txPool) {
	        String header = "RESPONSE_TRANSACTION_POOL\r\n\r\n";
            try{
                byte[] headerBytes = header.getBytes();
                byte[] data = serializeObject(txPool.getTransactionPool());
                
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(headerBytes);
                outputStream.write(data);
    		    
                byte msg[] = outputStream.toByteArray();
                    
                sendMessage(msg, "224.0.0.0", 3000);
            }
            catch(Exception e){e.printStackTrace();}
    }
    
    private Boolean responseTransactionPool(Message M, Blockchain blockchain, TxPool txPool) {
        try {
            ArrayList<Transaction> receivedTransactions = (ArrayList<Transaction>)deserializeBytes(M.getData().getBytes());
            if(receivedTransactions.size()==0 || receivedTransactions==null) {
	           System.out.println("Invalid transaction received");
	           return false;
            }
            for(Transaction tx:receivedTransactions) {
                blockchain.handleReceivedTransaction(tx, txPool);
                broadcastTransactionPool(txPool);
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
                    
                    broadcastLastMsg(blockchain);
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
    
    public static void broadcastTransactionPool(TxPool txPool) {
        String header = "RESPONSE_TRANSACTION_POOL\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(txPool.getTransactionPool());
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
    		    
            byte msg[] = outputStream.toByteArray();
                    
            sendMessage(msg, "224.0.0.0", 3000);
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public static void broadcastLastMsg(Blockchain blockchain) {
        String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(blockchain.getLastBlock());
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
    		    
            byte msg[] = outputStream.toByteArray();
                    
            sendMessage(msg, "224.0.0.0", 3000);
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public static void broadcastLastMsg(Block lastBlock) {
        String header = "RESPONSE_BLOCKCHAIN\r\n\r\n";
        try{
            byte[] headerBytes = header.getBytes();
            byte[] data = serializeObject(lastBlock);
                
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(data);
    		    
            byte msg[] = outputStream.toByteArray();
                    
            sendMessage(msg, "224.0.0.0", 3000);
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
                    
            sendMessage(msg, "224.0.0.0", 3000);
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

}
