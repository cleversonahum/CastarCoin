package transaction;

import java.util.ArrayList;
import java.util.Base64;
import java.security.MessageDigest;

public class Transaction {
    public String id;
    public ArrayList<TxIn> txIns;
    public ArrayList<TxOut> txOuts;
    public final Integer COINBASE_AMOUNT = 50;
    
    
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
        ArrayList<TxIn> txIns = null;
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
        
        if(hasDuplicates(txIns))
            return false;
        
        ArraList<Transaction> normalTransactions = avaliateTransaction.remove(0);//All transactions except the coinbaseTX
        
        for(int i=0; i<normalTransactions.size();i++) //Verify if the Transactions are valid
            if(!validateTransaction(normalTransactions.get(i), avaliateUnspentTxOuts))
                return false;
                
        return true; 

}

private Boolean hasTxInDuplicates(ArrayList<TxIn> txIns) { //Verify if there is repetead values into txIns
    for(int i=0; i<txIns.size(); i++)
        for(int j=0; j<txIns.size();j++)
            if(i!=j)
                if(txIns.get(i).txOutId.equals(txIns.get(j).txOutId) && txIns.get(i).txOutIndex == txIns.get(j).txOutIndex) {
                    System.out.println("There is a repeated value into txIns to: "+txIns.get(i).txOutIndex);
                    return true;   
                }
    return false;
        
}

private Boolean validateCoinbaseTx(Transaction transaction, int blockIndex) {
    if(transaction == null) {
        System.out.println("The first transaction needs to be a coinbase");
        return false;
    }
    else if (getTransactionId(transaction).equals(transaction.id)) {
        System.out.println("Invalid coinbase TX id: "+transaction.id);
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
    else if (!(txOut.address instanceof String)) {
        System.out.println("Invalid Address type in TxOut");
        return false;
    }
    else if (!isValidAddress(txOut.address)) {
        System.out.println("Invalid Txout Address");
        return false;
    }
    else if(!(txOut.amount instanceof Integer)) {
        System.out.printl("Invalid Amount Type in txOut");
        return false;
    }
    else
        return true;
}

private Boolean isValidAddress(String address) {
    //TO BE DONE
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