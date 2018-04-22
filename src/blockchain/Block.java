package blockchain;

import java.util.Date;

public class Block {
    public Integer index;
    public String hash;
    public String previousHash;
    public Date timestamp;
    public String data;
    public Integer level; //difficulty
    public Integer nonce;
    
    Block (int index, String hash, String previousHash, Date timestamp, String data, int level, int nonce) {
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.level = level;
        this.nonce = nonce;
    }
}
