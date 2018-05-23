package wallet;

import java.io.Console;
import java.util.ArrayList;

import java.security.KeyStore;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import transaction.*;
public class Wallet {

	
	private final String PRIVATE_KEY_LOCATION = "/keystore.jks";
	
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
	
	private ArrayList<TxOut> createTxOuts(String receiverAddress, String myAddress, int amount, int leftOverAmount) {
		
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
		
		//ArrayList<TxIn> txIns = COMPLETAR
		
		ArrayList<UnspentTxOut> removable = new ArrayList<UnspentTxOut>();
	
		for(UnspentTxOut value : myUnspentTxOutsA) {
			
			//TcIn txIn = COMPLETAR
			
			if(txIn == null) {
				
			}else {
				
				removable.add(value);
			}
			
		}
				
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
		return 0;
	}


	
	private TxIn toUnsignedTxIn(UnspentTxOut unspentTxOut){
		
		TxIn txIn = new TxIn();
		txIn.txOutId = unspentTxOut.txOutId;
		txIn.txOutIndex = unspentTxOut.txOutIndex;
		
		return txIn;
	}
	
	public PrivateKey getPrivateFromWallet() {
	    
		return getKeyPairFromKeyStore().getPrivate();
	}
	
	public PublicKey getPublicFromWallet() {
		
		return getKeyPairFromKeyStore().getPublic();
	}
	
	/*public String generatePrivateKey() {
		
		
		return null;
	}
	public void initWallet() {
		
		if (existsSync(privateKeyLocation)) {
			
			return;
		}
		
		
	}
	
	
	public void deleteWallet() {
		
	}*/ //Not used, "wallets" are the keys, and the keys are being generated using keytool outside program
	
	public int getBalance(String address, ArrayList<UnspentTxOut> unspentTxOuts) {
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(address == value.address.toString()) return value.amount;
			
		}	
		
	}
	
	public UnspentTxOut findUnspentTxOuts(String ownerAddress, ArrayList<UnspentTxOut> unspentTxOuts) {
		
		for(UnspentTxOut value : unspentTxOuts) {
			
			if(ownerAddress == value.address.toString()) return value;
			
		}
		
	}
	
	public static KeyPair getKeyPairFromKeyStore() throws Exception { //Reading KeyPar from KeyStore
        InputStream ins = RsaExample.class.getResourceAsStream(PRIVATE_KEY_LOCATION);

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
