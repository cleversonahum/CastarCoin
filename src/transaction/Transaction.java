package transaction;

import java.util.ArrayList;
import java.util.Base64;
import java.security.MessageDigest;

public class Transaction {
    public String id;
    public ArrayList<TxIn> txIns;
    public ArrayList<TxOut> txOuts;
    
    
    private String getTransactionId(Transaction transaction) { //Generating Transaction ID
        String txInContent = "", txOutContent = "", hash="";;
        for(int i=0; i<transaction.txIns.size();i++) { //Getting Values from txIns and making a String
            txInContent += transaction.txIns.get(i).txOutId + transaction.txIns.get(i).txOutIndex;
        }
        
        for(int i=0; i<transaction.txOuts.size();i++) { //Getting Values from txOuts and making a String
            txOutContent += transaction.txOuts.get(i).address + transaction.txOuts.get(i).amount;
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
    
    private Boolean validateTransaction(Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
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
    
    private Boolean hasValidTxIns(Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        Boolean result = true;
        for(int i=0; i<transaction.txIns.size(); i++) {
            result = result && validateTxIn(transaction.txIns.get(i), transaction, avaliateUnspentTxOuts);
        }
        return result;
    }
    
    private int totalTxInValues (Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        int result = 0;
        for(int i=0; i<transaction.txIns.size();i++)
            result += getTxInAmount(transaction.txIns.get(i), avaliateUnspentTxOuts);
            
        return result;
    }
    
    private int getTxInAmount(TxIn txIn, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        return findUnspentTxOut(txIn.txOutId, txIn.txOutIndex, avaliateUnspentTxOuts).amount;
    }

    private UnspentTxOut findUnspentTxOut(String transactionId, int index, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        for(int i=0; i<avaliateUnspentTxOuts.size();i++)
            if(avaliateUnspentTxOuts.get(i).txOutId.equals(transactionId) && avaliateUnspentTxOuts.get(i).txOutIndex == index)
                return avaliateUnspentTxOuts.get(i);
        System.out.println("findUnspentTxOut() find nothing");
        return avaliateUnspentTxOuts.get(-1);
    }

    private int totalTxOutValues(Transaction transaction) {
        int result = 0;
        for(int i=0; i<transaction.txOuts.size();i++)
            result += transaction.txOuts.get(i).amount;
            
        return result;
    }

    private Boolean validateTxIn(TxIn txIn, Transaction transaction, ArrayList<UnspentTxOut> avaliateUnspentTxOuts) {
        UnspentTxOut referencedTxOut = null;
        for(int i=0; i<avaliateUnspentTxOuts.size();i++)
            if(avaliateUnspentTxOuts.get(i).txOutId.equals(txIn.txOutId) && avaliateUnspentTxOuts.get(i).txOutId == txIn.txOutId) 
                referencedTxOut = avaliateUnspentTxOuts.get(i);
                
        if(referencedTxOut == null) {        
            System.out.println("Referenced txOut not found");
            return false;  
        }

        String address = referencedTxOut.address;
        
            
        return false;
        
        //const validateTxIn = (txIn: TxIn, transaction: Transaction, aUnspentTxOuts: UnspentTxOut[]): boolean => {
//    const referencedUTxOut: UnspentTxOut =
//        aUnspentTxOuts.find((uTxO) => uTxO.txOutId === txIn.txOutId && uTxO.txOutId === txIn.txOutId);
//    if (referencedUTxOut == null) {
//        console.log('referenced txOut not found: ' + JSON.stringify(txIn));
//        return false;
//    }
//    const address = referencedUTxOut.address;
//
//    const key = ec.keyFromPublic(address, 'hex');
//    return key.verify(transaction.id, txIn.signature);
//};
    }

    private Boolean validateBlockTransactions(ArrayList<Transaction> avaliateTransaction, ArrayList<UnspentTxOut> avaliateUnspentTxOut, int blockIndex) {
        Transaction coinbaseTx = avaliateTransaction.get(0);
        if(!validateCoinbaseTx(coinbaseTx, blockIndex)) {
            System.out.println("Invalid Coinbase Transaction");
            return false;
        }
        
        //CHECK FOR DUPLICATE txIns
        for(int i = 0; i<avaliateTransaction.size();i++) {
            //UNDONE
        }
        //    //check for duplicate txIns. Each txIn can be included only once
//    const txIns: TxIn[] = _(aTransactions)
//        .map(tx => tx.txIns)
//        .flatten().value();

        
        if(hasDuplicates(txIns))
            return false;
    }
    

//    if (hasDuplicates(txIns)) {
//        return false;
//    }
//
//    // all but coinbase transactions
//    const normalTransactions: Transaction[] = aTransactions.slice(1);
//    return normalTransactions.map((tx) => validateTransaction(tx, aUnspentTxOuts))
//        .reduce((a, b) => (a && b), true);
//
//};

}


