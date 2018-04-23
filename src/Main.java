import server.HTTP;
import blockchain.Blockchain;
import blockchain.Block;

//ERASE AFTER
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Hello World!");
	    
	    Blockchain blockchain = new Blockchain();
	    Block teste = blockchain.getLastBlock();
	    //System.out.println("Index: "+teste.index+"\nHASH: "+teste.hash);
	    Block teste2 = blockchain.generateNextBlock("teste");
	    //System.out.println("Index: "+teste2.index+"\nHASH: "+teste2.hash+"\nPHASH: "+teste2.previousHash);
	    try{
        TimeUnit.SECONDS.sleep(30);
} catch(Exception e){}
	    Block teste3 = blockchain.generateNextBlock("teste2");
	    blockchain.printBlockchain(blockchain.getBlockchain());



//	    HTTP server = new HTTP();
//	    try{
//	    server.start();
//        }catch(Exception e){e.printStackTrace();}

    }
    
}
