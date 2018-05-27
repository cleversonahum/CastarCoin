package transaction;

import java.security.PublicKey;
import java.io.Serializable;

public class TxOut implements Serializable  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3399676997973612581L;
    public PublicKey address;
    public Integer amount;
    
    public TxOut(PublicKey address, int amount){
        this.address = address;
        this.amount = amount;
    }
}
