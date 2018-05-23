package wallet;

import java.io.InputStream;

import java.util.ArrayList;

import java.security.KeyStore;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import transaction.*;
public class Wallet {

	
	private static final String PRIVATE_KEY_LOCATION = "/keystore.jks";
	
	public Transaction createTransaction(PublicKey receiverAddress, int amount, PrivateKey privateKey, ArrayList<UnspentTxOut> unspentTxOuts,
			ArrayList<Transaction> txPool) {
				
		PublicKey myAddress = Transaction.getPublicKey(privateKey);
		
		ArrayList<UnspentTxOut> myUnspentTxOutsA = new ArrayList<UnspentTxOut>();
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(value.address.equals(myAddress)) myUnspentTxOutsA.add(value);
			
		}
		
		ArrayList<UnspentTxOut> myUnspentTxOuts = filterTxPoolTxs(myUnspentTxOutsA, txPool);
		
		ArrayList<UnspentTxOut> includedUnspentTxOuts = findTxPoolForAmountA(amount, myUnspentTxOuts);
		int leftOverAmount = findTxPoolForAmountB(amount, myUnspentTxOuts);
		
		ArrayList<TxIn> unsignedTxIns = new ArrayList<TxIn>();
		
		for(UnspentTxOut value : includedUnspentTxOuts) {
			
			unsignedTxIns.add(toUnsignedTxIn(value));
			
		}
		
		Transaction tx = new Transaction();
		tx.txIns = unsignedTxIns;
		tx.txOuts = createTxOuts(receiverAddress, myAddress, amount, leftOverAmount);
		tx.id = Transaction.getTransactionId(tx);
		
		for(int i = 0; i < tx.txIns.size(); i++) {
			
			tx.txIns.get(i).signature = Transaction.signTxIn(tx,i,privateKey, unspentTxOuts);
			
		}
		
		
		return tx;
		
	}
	
	private ArrayList<TxOut> createTxOuts(PublicKey receiverAddress, PublicKey myAddress, int amount, int leftOverAmount) {
		
		TxOut txOut1 = new TxOut(receiverAddress, amount);
		ArrayList<TxOut> txOut = new ArrayList<TxOut>();
		if(leftOverAmount == 0) {
			txOut.add(txOut1);
			return txOut;
		} else {
			TxOut leftOverTx = new TxOut(myAddress, leftOverAmount);
			txOut.add(txOut1);
			txOut.add(leftOverTx);
			return txOut;
		}
	}

	private ArrayList<UnspentTxOut> filterTxPoolTxs(ArrayList<UnspentTxOut> myUnspentTxOutsA, ArrayList<Transaction> txPool) {
		ArrayList<TxIn> txIns = new ArrayList<TxIn>();
		
		for(Transaction value : txPool) {
			
			for(TxIn tx : value.txIns) {
				
				if(tx != null) txIns.add(tx);
				
			}
			
		}
		
	
		ArrayList<UnspentTxOut> filtered = new ArrayList<UnspentTxOut>();
		
		for(UnspentTxOut value : myUnspentTxOutsA) {
			
			TxIn txin = null;
			
			for(TxIn aTxIn : txIns) {
				
				if(aTxIn.txOutIndex == value.txOutIndex && aTxIn.txOutId == value.txOutId) {
					txin = aTxIn;
					break;
				}
		
			} 
			
			if(txin != null) filtered.add(value); 
			
					
		}
				
		return filtered;
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
		return 0;
	}


	
	private TxIn toUnsignedTxIn(UnspentTxOut unspentTxOut){
		
		TxIn txIn = new TxIn();
		txIn.txOutId = unspentTxOut.txOutId;
		txIn.txOutIndex = unspentTxOut.txOutIndex;
		
		return txIn;
	}
	
	public PrivateKey getPrivateFromWallet() {
	   try {
		return getKeyPairFromKeyStore().getPrivate();
	   }catch(Exception e) {e.printStackTrace();}
       return null; 
        
	}
	
	public PublicKey getPublicFromWallet() {
		try{
		return getKeyPairFromKeyStore().getPublic();
		}catch(Exception e) {e.printStackTrace();}
       return null; 
	}
	
	public int getBalance(PublicKey address, ArrayList<UnspentTxOut> unspentTxOuts) {
		
		int balance = 0;
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(address == value.address) balance = value.amount;
			
		}	
		
		return balance;
		
	}
	
	public UnspentTxOut findUnspentTxOuts(PublicKey ownerAddress, ArrayList<UnspentTxOut> unspentTxOuts) {
		
		UnspentTxOut txout = null;
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(ownerAddress == value.address) txout = value;
			
		}
		
		return txout;
	
	}
	
	public static KeyPair getKeyPairFromKeyStore() throws Exception { //Reading KeyPar from KeyStore
        InputStream ins = Wallet.class.getResourceAsStream(PRIVATE_KEY_LOCATION);

        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(ins, "s3cr3t".toCharArray());   //Keystore password
        KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("s3cr3t".toCharArray()); //Key password
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("mykey", keyPassword);
        java.security.cert.Certificate cert = keyStore.getCertificate("mykey");
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();
    
        return new KeyPair(publicKey, privateKey);
    }
	
	
}
