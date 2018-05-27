package transaction;

import java.security.PublicKey;

public class UnspentTxOut {
    public String txOutId;
    public Integer txOutIndex;
    public PublicKey address;
    public Integer amount;
    
    UnspentTxOut(String txOutId, int txOutIndex, PublicKey address, int amount) {
        this.txOutId = txOutId;
        this.txOutIndex = txOutIndex;
        this.address = address;
        this.amount = amount;
    }
    
    @Override
    public String toString(){
        return "TxOutId:\n" + this.txOutId +
                "\nTxOutIndex:\n" + this.txOutIndex +
                "\nAddress:\n" + this.address +
                "\nAmount:\n" + this.amount;
    }
}
