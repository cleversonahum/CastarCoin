import blockchain.*;
import transaction.*;
import wallet.*;


public class Main {

    public static void main(String[] args) {
	    System.out.println("Hello World!");
	    
	    Blockchain blockchain = new Blockchain();
	    TxPool txPool = new TxPool();
	    Wallet wallet = new Wallet();
	    
	    blockchain.generateNextBlockTransaction(wallet.getPublicFromWallet(), 50, wallet, txPool);
	    //blockchain.generateRawNextBlock();

    }
    
}
