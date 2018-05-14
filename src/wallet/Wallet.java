package wallet;

import java.io.Console;
import java.util.ArrayList;

import transaction.*;
public class Wallet {

	
	public Transaction createTransaction(String receiverAddress, int amount, String privateKey, ArrayList<UnspentTxOut> unspentTxOuts,
			ArrayList<Transaction> txPool) {
				
		String myAddress = getPublicKey(privateKey);
		
		ArrayList<UnspentTxOut> myUnspentTxOutsA = new ArrayList<UnspentTxOut>();
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(value.address == myAddress) myUnspentTxOutsA.add(value);
			
		}
		
		ArrayList<UnspentTxOut> myUnspentTxOuts = filterTxPoolTxs(myUnspentTxOutsA, txPool);
		
		ArrayList<UnspentTxOut> includedUnspentTxOuts = findTxPoolForAmountA(amount, myUnspentTxOuts);
		int leftOverAmount = findTxPoolForAmountB(amount, myUnspentTxOuts);
		
		ArrayList<TxIn> unsignedTxIns = includedUnspentTxOuts.map(toUnsignedTxIn);
		
		Transaction tx = new Transaction();
		tx.txIns = unsignedTxIns;
		tx.txOuts = createTxOuts(receiverAddress, myAddress, amount, leftOverAmount);
		tx.id = getTransactionId(tx);
		
		return tx;
		
	}
	
	private ArrayList<UnspentTxOut> filterTxPoolTxs(ArrayList<UnspentTxOut> myUnspentTxOutsA, ArrayList<Transaction> txPool) {
		
		ArrayList<TxIn> txIns = 
		
		
		return null;
	}

	private ArrayList<UnspentTxOut> findTxPoolForAmountA(int amount, ArrayList<UnspentTxOut> myUnspentTxOuts) {
		
		int currentAmount = 0;
		
		ArrayList<UnspentTxOut> includedUnspentTxOuts = new ArrayList<UnspentTxOut>();
		
		for(UnspentTxOut value : myUnspentTxOuts) {
			includedUnspentTxOuts.add(value);
			currentAmount = currentAmount + value.amount;
			if(currentAmount >= amount) {
				return includedUnspentTxOuts;
			}
			
		}
		
		return null;
	}
	
	private int findTxPoolForAmountB(int amount, ArrayList<UnspentTxOut> myUnspentTxOuts) {
		
		int currentAmount = 0;
		
		for(UnspentTxOut value : myUnspentTxOuts) {
			currentAmount = currentAmount + value.amount;
			if(currentAmount >= amount) {
				int leftOverAmount = currentAmount - amount;
				return leftOverAmount;
			}
			
		}
		
		return null;
	}


	
	private TxIn toUnsignedTxIn(UnspentTxOut unspentTxOut){
		
		TxIn txIn = new TxIn();
		txIn.txOutId = unspentTxOut.txOutId;
		txIn.txOutIndex = unspentTxOut.txOutIndex;
		
		return txIn;
	}
}
