package blockchain;

import java.util.Date;

public class Block {
    public Integer index;
    public String hash;
    public String previousHash;
    public Date timestamp;
    public String data;
    
    Block (int index, String hash, String previousHash, Date timestamp, String data) {
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
    }
}
