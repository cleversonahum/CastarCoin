import server.HTTP;
import blockchain.Blockchain;
import blockchain.Block;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Hello World!");
	    
	    Blockchain blockchain = new Blockchain();
	    Block teste = blockchain.getLastBlock();
	    //System.out.println("Index: "+teste.index+"\nHASH: "+teste.hash);
	    Block teste2 = blockchain.generateNextBlock("teste");
	    //System.out.println("Index: "+teste2.index+"\nHASH: "+teste2.hash+"\nPHASH: "+teste2.previousHash);
	    blockchain.printBlockchain(blockchain.getBlockchain());

	    
//	    HTTP server = new HTTP();
//	    try{
//	    server.start();
//        }catch(Exception e){e.printStackTrace();}

    }
}
