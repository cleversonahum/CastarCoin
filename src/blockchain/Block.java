package blockchain;

import transaction.Transaction;
import java.util.Date;
import java.util.ArrayList;

public class Block {
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
}
