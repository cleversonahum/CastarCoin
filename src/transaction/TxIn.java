package transaction;

import java.io.Serializable;

public class TxIn implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5762734478555076766L;
    public String txOutId;
    public Integer txOutIndex;
    public String signature;
}
