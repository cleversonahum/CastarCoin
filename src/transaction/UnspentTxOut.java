package transaction;

public class UnspentTxOut {
    public String txOutId;
    public Integer txOutIndex;
    public String address;
    public Integer amount;
    
    UnspentTxOut(String txOutId, int txOutIndex, String address, int amount) {
        this.txOutId = txOutId;
        this.txOutIndex = txOutIndex;
        this.address = address;
        this.amount = amount;
    }
}
