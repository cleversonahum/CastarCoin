package transaction;

import java.util.ArrayList;

public class TxPool {

    private ArrayList<Transaction> txPool = new ArrayList<>();

    public ArrayList<Transaction> getTransactionPool() {
        return this.txPool;
    }

    public void addToTransactionPool(Transaction tx, ArrayList<UnspentTxOut> unspentTxOuts) {

        if (!tx.validateTransaction(tx, unspentTxOuts)) {
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
        // TODO: return the correct array
        //return _(aTransactionPool).map((tx) => tx.txIns).flatten().value();
        return new ArrayList<>();
    }

    private boolean containsTxIn(ArrayList<TxIn> txIns, TxIn txIn) {
        // TODO: a transaction cannot by added if any of the transaction inputs are already found in the existing transaction pool
        //return _.find(txPoolIns, ((txPoolIn) => { txIn.txOutIndex === txPoolIn.txOutIndex && txIn.txOutId === txPoolIn.txOutId;}));
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
        boolean foundTxIn = false;
        // TODO: search txIn
        //foundTxIn = unspentTxOuts.find((uTxO: UnspentTxOut) => { return uTxO.txOutId === txIn.txOutId && uTxO.txOutIndex === txIn.txOutIndex;});
        return foundTxIn;
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
            System.out.println("removing the following transactions from txPool: " + invalidTxs.toString());
            // TODO: remove invalidTxs from transactionPool
            //transactionPool = _.without(transactionPool, ...invalidTxs);
        }
    }
}
