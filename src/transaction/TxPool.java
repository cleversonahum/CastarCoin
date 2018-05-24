package transaction;

import java.util.ArrayList;

public class TxPool {

    private ArrayList<Transaction> txPool = new ArrayList<>();

    public ArrayList<Transaction> getTransactionPool() {
        return this.txPool;
    }

    public void addToTransactionPool(Transaction tx, ArrayList<UnspentTxOut> unspentTxOuts) {

        if (!Transaction.validateTransaction(tx, unspentTxOuts)) {
            try {
                throw new Exception("Trying to add invalid tx to pool");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!isValidTxForPool(tx, this.txPool)) {
            try {
                throw new Exception("Trying to add invalid tx to pool");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Adding to txPool: " + tx.toString());
        this.txPool.add(tx);
    }

    private ArrayList<TxIn> getTxPoolIns(ArrayList<Transaction> aTransactionPool) {
        // Add all unique TxIn in transaction pool
        ArrayList<TxIn> txIn = new ArrayList<>();

        for (Transaction tx : aTransactionPool) {
            for (TxIn ti : tx.txIns) {
                if(!containsTxIn(txIn, ti)){
                    txIn.add(ti);
                }
            }
        }

        return txIn;
    }

    private boolean containsTxIn(ArrayList<TxIn> txIns, TxIn txIn) {
        for (TxIn ti : txIns) {
            if((ti.txOutIndex == txIn.txOutIndex) && (ti.txOutId.equals(txIn.txOutId))){
                return true;
            }
        }
        return false;
    }

    private boolean isValidTxForPool(Transaction tx, ArrayList<Transaction> aTransactionPool) {

        ArrayList<TxIn> txPoolIns = getTxPoolIns(aTransactionPool);

        for (TxIn txIn : tx.txIns) {
            if (containsTxIn(txPoolIns, txIn)) {
                System.out.println("txIn already found in the txPool");
                return false;
            }
        }
        return true;
    }

    private boolean hasTxIn(TxIn txIn, ArrayList<UnspentTxOut> unspentTxOuts) {
        for (UnspentTxOut to : unspentTxOuts) {
            if((to.txOutIndex == txIn.txOutIndex) && (to.txOutId.equals(txIn.txOutId))){
                return true;
            }
        }
        return false;
    }

    public void updateTransactionPool(ArrayList<UnspentTxOut> unspentTxOuts) {

        ArrayList<Transaction> invalidTxs = new ArrayList<>();

        for (Transaction tx : this.txPool) {
            for (TxIn txIn : tx.txIns) {
                if (!hasTxIn(txIn, unspentTxOuts)) {
                    invalidTxs.add(tx);
                    break;
                }
            }
        }

        if (invalidTxs.size() > 0) {
            System.out.println("Removing invalid transactions from transaction pool.");
            for (Transaction txv : this.txPool) {
                for (Transaction txi : invalidTxs) {
                    if(txv == txi){
                        this.txPool.remove(txi);
                    }
                }
            }
        }
    }
}
