import blockchain.*;
import transaction.*;
import wallet.*;
import p2p.*;

import java.util.Scanner;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
	    System.out.println("Hello World!");
//	    Blockchain blockchain = new Blockchain();
//	    TxPool txPool = new TxPool();
//	    Wallet wallet = new Wallet();
//	    MC p2p = new MC('m',"224.0.0.0",3000, 80000, "P2P",blockchain, txPool);
//	    p2p.start();
	    
	     System.out.println("Castar-Coin");
         Scanner input = new Scanner(System.in);      
        int selection = -1;
        do {
           
                System.out.println("1- View Current Balance");
                System.out.println("2- View Public Adress");
                System.out.println("3- Mine Block");
                System.out.println("4- View Blockchain");
                System.out.println("5- View Block");
                System.out.println("6- Do transaction");
                System.out.println("0- Leave");
                //mais cenas
               
            System.out.print("Enter an option  : ");
            
            selection = input.nextInt();
            
               
            switch(selection) {
           
            case 0:
                break;
            case 1:
            		System.out.print("Your current balance is: ");
            		System.out.println(blockchain.getAccountBalance(wallet));
                break;
            case 2:
            		System.out.println("Your public address is: ");
            		System.out.println(wallet.getPublicFromWallet());
            		break;
            case 3:
            		System.out.println("A new block was mined!");
            		blockchain.generateNextBlock(wallet, txPool);
                 break;
            case 4:
            		System.out.println(blockchain.getBlockchain());
                break;
            case 5:
                 System.out.print("Pick a block from the blockChain");
                 int a=input.nextInt();
                 Block b=blockchain.getBlockchain().get(0);
                 System.out.println(b.printString());
                 break;
            case 6:
            		System.out.println("Chose the receiver address:");
               // String address=input.nextLine();
                System.out.print("Chose the amount:");
                int amount=input.nextInt();
                blockchain.sendTransaction(wallet.getPublicFromWallet(), amount, wallet, txPool);
                System.out.println("Transaction completed");
                break;
            
            default:
                System.out.println("The selection is invalid!");
                    
               
            }
           //input.close();
        }while (selection!=0);
        input.close();
	    
	    //Blockchain blockchain = new Blockchain();
	    //TxPool txPool = new TxPool();
	    //Wallet wallet = new Wallet();
	    
	    //Functions used into main.ts
	    //From Transaction
	    //System.out.println("1txPool: "+txPool.getTransactionPool());
	    //blockchain.generateNextBlock(wallet, txPool);
//	    blockchain.generateNextBlock(wallet, txPool);
//	    blockchain.generateNextBlock(wallet, txPool);
	    //System.out.println("2txPool: "+txPool.getTransactionPool());
	    //blockchain.sendTransaction(wallet.getPublicFromWallet(), 50, wallet, txPool);
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
