import blockchain.*;
import transaction.*;
import wallet.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
	    System.out.println("Hello World!");
	    
	    Blockchain blockchain = new Blockchain();
	    TxPool txPool = new TxPool();
	    Wallet wallet = new Wallet();
	    
	    //Functions used into main.ts
	    //From Transaction
	    blockchain.generateNextBlock(wallet, txPool);
	    //blockchain.generateNextBlockTransaction(wallet.getPublicFromWallet(), 50, wallet, txPool); //It stopped in CreateTransaction() function because it is receiving a UnspentTxOut empty
	    //generateRawNextBlock tested into generateNextBlock()
	    System.out.println(blockchain.getAccountBalance(wallet));
	    System.out.println(blockchain.getBlockchain());
	    System.out.println(blockchain.getMyUnspentTransactionOutputs(wallet));
	    System.out.println(blockchain.getUnspentTxOuts());
	    //blockchain.sendTransaction(); //Does not work because of similar motives as generateNextBlockTransaction
	    
	    //From TxPool
	    System.out.println(txPool.getTransactionPool());
	    

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
