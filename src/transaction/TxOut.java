package transaction;

import java.security.PublicKey;

public class TxOut {
    public PublicKey address;
    public Integer amount;
    
    TxOut(PublicKey address, int amount){
        this.address = address;
        this.amount = amount;
    }
}
