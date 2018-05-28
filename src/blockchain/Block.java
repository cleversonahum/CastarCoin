package blockchain;

import transaction.Transaction;
import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3466731937657416478L;
    public Integer index;
    public String hash;
    public String previousHash;
    public Date timestamp;
    public ArrayList<Transaction> data = new ArrayList<>();
    public Integer level; //difficulty
    public Integer nonce;
    
    Block (int index, String hash, String previousHash, Date timestamp, ArrayList<Transaction> data, int level, int nonce) {
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.level = level;
        this.nonce = nonce;
    }
	
	public String printString() {
		// TODO Auto-generated method stub
		return "Index: " + this.index + "\n" + "Hash: " + this.hash + "\n" + "Previous Hash: " +this.previousHash+ "\n" 
				+ "Timestamp: " +this.timestamp+ "\n" + "Difficulty: " + this.level+ "\n" +"Nonce: " +this.nonce+ "\n"; 
		
	}
}
