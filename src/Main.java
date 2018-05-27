import blockchain.*;
import transaction.*;
import wallet.*;
import p2p.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
	    System.out.println("Hello World!");
	    Blockchain blockchain = new Blockchain();
	    TxPool txPool = new TxPool();
	    Wallet wallet = new Wallet();
	    MC p2p = new MC('m',"224.0.0.0",3000, 80000, "P2P",blockchain, txPool);
	    p2p.start();
	    
	    //Blockchain blockchain = new Blockchain();
	    //TxPool txPool = new TxPool();
	    //Wallet wallet = new Wallet();
	    
	    //Functions used into main.ts
	    //From Transaction
	    //System.out.println("1txPool: "+txPool.getTransactionPool());
	    blockchain.generateNextBlock(wallet, txPool);
	    //blockchain.generateNextBlock(wallet, txPool);
	    //blockchain.generateNextBlock(wallet, txPool);
	    //System.out.println("2txPool: "+txPool.getTransactionPool());
	    //blockchain.sendTransaction(wallet.getPublicFromWallet(), 50, wallet, txPool);
	    //System.out.println("3txPool: "+txPool.getTransactionPool());
	    //blockchain.generateNextBlockTransaction(wallet.getPublicFromWallet(), 50, wallet, txPool); //It stopped in CreateTransaction() function because it is receiving a UnspentTxOut empty
	    //generateRawNextBlock tested into generateNextBlock()
	    //System.out.println(blockchain.getAccountBalance(wallet));
	    //System.out.println(blockchain.getBlockchain());
//	    System.out.println(blockchain.getMyUnspentTransactionOutputs(wallet));
//	    System.out.println(blockchain.getUnspentTxOuts());
	    //blockchain.sendTransaction(); //Does not work because of similar motives as generateNextBlockTransaction
	    
	    //From TxPool
	    //System.out.println(txPool.getTransactionPool());
	    
        
        //Functions used into p2p.ts
        //System.out.println(blockchain.getLastBlock());
        //addBlockToChain needs to be tested
        //handleReceivedTransaction needs to be tested
        
		// Needs real keys
		// To be removed
		/*wallet.createTransaction(new PublicKey() {
									@Override
									public String getAlgorithm() {
										return null;
									}

									@Override
									public String getFormat() {
										return null;
									}

									@Override
									public byte[] getEncoded() {
										return new byte[0];
									}
								},
								10,
								new PrivateKey() {
									@Override
									public String getAlgorithm() {
										return null;
									}

									@Override
									public String getFormat() {
										return null;
									}

									@Override
									public byte[] getEncoded() {
										return new byte[0];
									}
								},
								new ArrayList<UnspentTxOut>(),
								new ArrayList<Transaction>()
		);*/
	}
}
