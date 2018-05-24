package transaction;

import java.util.ArrayList;
import java.util.Base64;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.math.BigInteger;

import java.nio.charset.StandardCharsets;

public class Transaction {
    public String id = "";
    public ArrayList<TxIn> txIns = new ArrayList<>();
    public ArrayList<TxOut> txOuts = new ArrayList<>();
    public static final Integer COINBASE_AMOUNT = 50;
    
    public static String getTransactionId(Transaction transaction) { //Generating Transaction ID
        String txInContent = "", txOutContent = "", hash="";
        for(int i=0; i<transaction.txIns.size();i++) { //Getting Values from txIns and making a String
            txInContent += transaction.txIns.get(i).txOutId + transaction.txIns.get(i).txOutIndex;
        }
        for(int i=0; i<transaction.txOuts.size();i++) { //Getting Values from txOuts and making a String
            txOutContent += getStringFromPublicKey(transaction.txOuts.get(i).address) + transaction.txOuts.get(i).amount;
        }
        
        try {
            String msgHash = txInContent + txOutContent;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash = digest.digest(msgHash.getBytes("UTF-8"));
            hash = Base64.getEncoder().encodeToString(byteHash);
        }
        catch(Exception e){e.printStackTrace();}
        
        return hash;
    }
    
    public static Boolean validateTransaction(Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        if(getTransactionId(transaction).equals(transaction.id)) {
            System.out.println("Invalid Transaction ID: "+transaction.id);
            return false;
        }
        else if(!hasValidTxIns(transaction, avaliateUnspentTxOuts)) {
            System.out.println("Some txIns are Invalid in Transaction: " + transaction.id);
            return false;
        }
        else if(totalTxOutValues(transaction) != totalTxInValues(transaction, avaliateUnspentTxOuts)) {
            System.out.println("Total value of InTx different of total value of OutTx in transaction: "+transaction.id);
            return false;
        }
        else
            return true;
        
    }
    
    private static Boolean hasValidTxIns(Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        Boolean result = true;
        for(int i=0; i<transaction.txIns.size(); i++) {
            result = result && validateTxIn(transaction.txIns.get(i), transaction, avaliateUnspentTxOuts);
        }
        return result;
    }
    
    private static int totalTxInValues (Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        int result = 0;
        for(int i=0; i<transaction.txIns.size();i++)
            result += getTxInAmount(transaction.txIns.get(i), avaliateUnspentTxOuts);
            
        return result;
    }
    
    private static int getTxInAmount(TxIn txIn, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        return findUnspentTxOut(txIn.txOutId, txIn.txOutIndex, avaliateUnspentTxOuts).amount;
    }

    private static UnspentTxOut findUnspentTxOut(String transactionId, int index, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        for(int i=0; i<avaliateUnspentTxOuts.size();i++)
            if(avaliateUnspentTxOuts.get(i).txOutId.equals(transactionId) && avaliateUnspentTxOuts.get(i).txOutIndex == index)
                return avaliateUnspentTxOuts.get(i);
        System.out.println("findUnspentTxOut() find nothing");
        return null;
    }

    private static int totalTxOutValues(Transaction transaction) {
        int result = 0;
        for(int i=0; i<transaction.txOuts.size();i++)
            result += transaction.txOuts.get(i).amount;
            
        return result;
    }

    private static Boolean validateTxIn(TxIn txIn, Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        UnspentTxOut referencedTxOut = null;
        Boolean result = false;
        
        for(int i=0; i<avaliateUnspentTxOuts.size();i++)
            if(avaliateUnspentTxOuts.get(i).txOutId.equals(txIn.txOutId) && avaliateUnspentTxOuts.get(i).txOutId == txIn.txOutId) 
                referencedTxOut = avaliateUnspentTxOuts.get(i);
                
        if(referencedTxOut == null) {        
            System.out.println("Referenced txOut not found");
            return false;  
        }

        PublicKey address = referencedTxOut.address;
        
        try {
            //Verifying signature using public key
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(address);
            publicSignature.update((transaction.id).getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(txIn.signature);
            result = publicSignature.verify(signatureBytes);
        }
        catch(Exception e){e.printStackTrace();}
        
        return result;

    }

    private static Boolean validateBlockTransactions(ArrayList<Transaction> avaliateTransaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts, int blockIndex) {
        ArrayList<TxIn> txIns = new ArrayList<TxIn>();
        Transaction coinbaseTx = avaliateTransaction.get(0);
        
        if(!validateCoinbaseTx(coinbaseTx, blockIndex)) {
            System.out.println("Invalid Coinbase Transaction");
            return false;
        }
        
        //Adding TxIn to txIns
        for(int i = 0;i < avaliateTransaction.size();i++) {
            for(int j = 0; j< avaliateTransaction.get(i).txIns.size(); j++) {
                txIns.add(avaliateTransaction.get(i).txIns.get(j));
            }
        }
        
        if(hasTxInDuplicates(txIns))
            return false;
        
        avaliateTransaction.remove(0); //Removing first transaction
        ArrayList<Transaction> normalTransactions = avaliateTransaction;//All transactions except the coinbaseTX
        
        for(int i=0; i<normalTransactions.size();i++) //Verify if the Transactions are valid
            if(!validateTransaction(normalTransactions.get(i), avaliateUnspentTxOuts))
                return false;
                
        return true; 

    }

    private static Boolean hasTxInDuplicates(ArrayList<TxIn> txIns) { //Verify if there is repetead values into txIns
        for(int i=0; i<txIns.size(); i++)
            for(int j=0; j<txIns.size();j++)
                if(i!=j)
                    if(txIns.get(i).txOutId.equals(txIns.get(j).txOutId) && txIns.get(i).txOutIndex == txIns.get(j).txOutIndex) {
                        System.out.println("There is a repeated value into txIns to: "+txIns.get(i).txOutIndex);
                        return true;   
                    }
        return false;
        
    }

    private static Boolean validateCoinbaseTx(Transaction transaction, int blockIndex) {
        if(transaction == null) {
            System.out.println("The first transaction needs to be a coinbase");
            return false;
        }
        else if (!getTransactionId(transaction).equals(transaction.id)) {
            System.out.println("Invalid coinbase TX id: "+transaction.id+"\nExpected: "+getTransactionId(transaction));
            return false;
        }
        else if(transaction.txIns.size() != 1) {
            System.out.println("one txIn needs to be specified in the coinbase");
            return false;
        }
        else if(transaction.txIns.get(0).txOutIndex != blockIndex) {
            System.out.println("The txIn signature in coinbase tx needs to be the block height");
            return false;
        }
        else if(transaction.txOuts.size() != 1) {
            System.out.println("Invalid number of txOuts in coinbase transaction");
            return false;
        }
        else if(transaction.txOuts.get(0).amount != COINBASE_AMOUNT) {
            System.out.println("Invalid coinbase amount in coinbase transaction");
            return false;
        }
        else
            return true;
    
    }

    private Boolean isValidTxInStructure(TxIn txIn) {
        if(txIn==null) {
            System.out.println("TxIn NULL");
            return false;
        }
        else if (!(txIn.signature instanceof String)) {
            System.out.println("Invalid signature type in TxIn");
            return false;
        }
        else if (!(txIn.txOutId instanceof String)) {
            System.out.println("Invalid TxOutId type in TxIn");
            return false;
        }
        else if (!(txIn.txOutIndex instanceof Integer)) {
            System.out.println("Invalid TxOutIndex type in TxIn");
            return false;
        }
        else
            return true;
    }

    private Boolean isValidTxOutStructure(TxOut txOut) {
        if(txOut == null) {
            System.out.println("TxOut NULL");
            return false;
        }
        else if (!(txOut.address instanceof PublicKey)) {
            System.out.println("Invalid Address type in TxOut");
            return false;
        }
        else if (!isValidAddress(txOut.address)) {
            System.out.println("Invalid Txout Address");
            return false;
        }
        else if(!(txOut.amount instanceof Integer)) {
            System.out.println("Invalid Amount Type in txOut");
            return false;
        }
        else
            return true;
    }

    public static Boolean isValidAddress(PublicKey address) {
        if(!(address instanceof PublicKey))
            return false;
        return true;
    }

    private Boolean isValidTransactionStructure(Transaction transaction) {
        if(!(transaction.id instanceof String)) {
            System.out.println("Invalid transaction ID type");
            return false;
        }
        else if(!(transaction.txIns instanceof ArrayList)) {
            System.out.println("Invalid TxIns type");
            return false;
        }
        else if(!(transaction.txOuts instanceof ArrayList)) {
            System.out.println("Invalid txOuts type");
            return false;
        }
        
        for(int i=0; i<transaction.txIns.size();i++)
            if(!isValidTxInStructure(transaction.txIns.get(i))) {
                System.out.println("Invalid TxIn Structure to: " + transaction.txIns.get(i).txOutIndex);
                return false;
            }
            
        for(int i=0; i<transaction.txOuts.size();i++)
            if(!isValidTxOutStructure(transaction.txOuts.get(i))) {
                System.out.println("Invalid TxOut Structure to: " + transaction.txOuts.get(i).address);
                return false;
            }
            
        return true;
        
    }
      
    public static Transaction getCoinbaseTransaction(PublicKey address, int blockIndex) {
        Transaction transaction = new Transaction();
        TxIn txIn = new TxIn();
        txIn.signature = "";
        txIn.txOutId = "";
        txIn.txOutIndex = blockIndex;
        
        transaction.txIns.add(txIn);
        transaction.txOuts = new ArrayList<TxOut>();
        transaction.txOuts.add(new TxOut(address, COINBASE_AMOUNT));
        transaction.id = getTransactionId(transaction);
        
        return transaction;
    }

    public static String signTxIn(Transaction transaction, int txInIndex, PrivateKey privateKey, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        String result = "";
        TxIn txIn = transaction.txIns.get(txInIndex);
        String dataToSign = transaction.id;
        UnspentTxOut referencedUnspentTxOut = findUnspentTxOut(txIn.txOutId, txIn.txOutIndex, avaliateUnspentTxOuts);
        if(referencedUnspentTxOut == null) {
            System.out.println("TxOut reference not found");
            throw new java.lang.RuntimeException("TxOut reference not found");
        }
        PublicKey referencedAddress = referencedUnspentTxOut.address;
        
        if(!getPublicKey(privateKey).equals(referencedAddress)) {
            System.out.println("Key does not match the address referenced in TxIn");
            throw new java.lang.RuntimeException("Key does not match the address referenced in TxIn");
        }
        
        try {
            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(privateKey);
            privateSignature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
            byte[] signature = privateSignature.sign();
            result = Base64.getEncoder().encodeToString(signature);
        }
        catch(Exception e){e.printStackTrace();}

        return result;
    }

    private static ArrayList<UnspentTxOut> updateUnspentTxOuts(ArrayList<Transaction> avaliateTransactions, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        //UNDONE
        ArrayList<UnspentTxOut> newUnspentTxOuts = new ArrayList<UnspentTxOut>();
        for(int i=0; i<avaliateTransactions.size();i++)
            for(int j=0; j<avaliateTransactions.get(i).txOuts.size(); j++)
                newUnspentTxOuts.add(new UnspentTxOut(avaliateTransactions.get(i).id, i, avaliateTransactions.get(i).txOuts.get(j).address, avaliateTransactions.get(i).txOuts.get(j).amount));
                
        ArrayList<UnspentTxOut> consumedTxOuts = new ArrayList<UnspentTxOut>();
        for(int i=0; i<avaliateTransactions.size();i++)
            for(int j=0; j<avaliateTransactions.get(i).txIns.size();j++)
                consumedTxOuts.add(new UnspentTxOut(avaliateTransactions.get(i).txIns.get(j).txOutId, avaliateTransactions.get(i).txIns.get(j).txOutIndex, null, 0));
                
        ArrayList<UnspentTxOut> resultingUnspentTxOuts = new ArrayList<UnspentTxOut>(newUnspentTxOuts);
        for(int i=0; i<avaliateUnspentTxOuts.size();i++)
            if(findUnspentTxOut(avaliateUnspentTxOuts.get(i).txOutId, avaliateUnspentTxOuts.get(i).txOutIndex, consumedTxOuts)!=null)
                resultingUnspentTxOuts.add(avaliateUnspentTxOuts.get(i));
                
        return resultingUnspentTxOuts;
    }
    
    public static ArrayList<UnspentTxOut> processTransactions(ArrayList<Transaction> avaliateTransactions, ArrayList<UnspentTxOut> avaliateUnspentTxOuts, int blockIndex) {
        if(!validateBlockTransactions(avaliateTransactions, avaliateUnspentTxOuts, blockIndex)) {
            System.out.println("Invalid Block Transaction");
            return null;
        }

        return updateUnspentTxOuts(avaliateTransactions, avaliateUnspentTxOuts);
    }
    
    public static PublicKey getPublicKey(PrivateKey avaliatePrivateKey) { //Making a public key from private
        PublicKey result = null;
        
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec priv = kf.getKeySpec(avaliatePrivateKey, RSAPrivateKeySpec.class);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
            result = kf.generatePublic(keySpec);
        }
        catch(Exception e){e.printStackTrace();}
        
        return result;
    }
    
    public static String getStringFromPublicKey(PublicKey publicKey) {
        byte[] encodedPublicKey = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(encodedPublicKey);
    }
}