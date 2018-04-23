package transaction;

public class TxOut {
    public String address;
    public int amount;
    
    TxOut(String address, int amount){
        this.address = address;
        this.amount = amount;
    }
}
