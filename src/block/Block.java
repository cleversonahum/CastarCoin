package block;

import java.util.Date;
import java.util.Base64;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;

public class Block {
    public Integer index;
    public String hash;
    public String previousHash;
    public Date timestamp;
    public String data;
    private ArrayList<Block> blockchain = new ArrayList<Block>();
    
    Block (int index, String hash, String previousHash, Date timestamp, String data) {
        this.index = index;
        this.hash = hash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.blockchain.add(genesisBlock);
    }
    
    final public Block genesisBlock = new Block(0, "816534932c2b7154836da6afc367695e6337db8a921823784c14378abed4f7d7", "", new Date(System.currentTimeMillis()), "Genesis Block"); //First Block into Chain
    
    public ArrayList<Block> getBlockchain() { //Get all blocks
        return this.blockchain;
    }
    
    public Block getLastBlock() { //Get the last block added
        return this.blockchain.get(this.blockchain.size() - 1);
    }
    
    private String generateHash(int index, String previousHash, Date timestamp, String data) { //Generate a Hash in accord with parameters of the block
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        String  msgHash = Integer.toString(index) + previousHash + format.format(timestamp) + data;
        String hash="";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash = digest.digest(msgHash.getBytes("StandardCharsets.UTF_8"));
            hash = Base64.getEncoder().encodeToString(byteHash);
        }
        catch(Exception e){e.printStackTrace();}

        return hash;
    }
    
    private String generateHashBlock(Block block) { //Generate Hash to a Block made
        return generateHash(block.index, block.previousHash, block.timestamp, block.data);
    }
    
    private Boolean isValidBlockStructure(Block newBlock) { //Verify if Block Variables are in accord with Block Structure
        return ((newBlock.index instanceof Integer) && (newBlock.hash instanceof String) && (newBlock.previousHash instanceof String) && (newBlock.timestamp instanceof Date) && (newBlock.data instanceof String));    
    }
    
    private Boolean isValidNewBlock (Block newBlock, Block previousBlock) { //Verify if the new block to be added is Valid
        if(!isValidBlockStructure(newBlock)) {
            System.out.println("Invalid Block Structure");
            return false;
        }
        else if ((previousBlock.index + 1) != (newBlock.index)) {
            System.out.println("Invalid Index Number");
            return false;
        }
        else if (previousBlock.hash != newBlock.previousHash) {
            System.out.println("Invalid Previous Hash");
            return false;
        }
        else if (generateHashBlock(newBlock) != newBlock.hash) {
            System.out.println ("Invalid Hash Generated");
            return false;
        }
        else
            return true;
    }
    
    private void addBlock(Block newBlock) {
        if(isValidNewBlock(newBlock, getLastBlock()))
            this.blockchain.add(newBlock);
    }
    
    private Boolean isValidGenesisBlock(Block validateBlock) {
        return ((this.genesisBlock.index == validateBlock.index) && (this.genesisBlock.hash.equals(validateBlock.hash)) && (this.genesisBlock.previousHash.equals(validateBlock.previousHash)) && (this.genesisBlock.timestamp.equals(validateBlock.timestamp)) && (this.genesisBlock.data.equals(validateBlock.data)));
    }
    
    private Boolean isValidChain(ArrayList<Block> validateBlockchain) {
        if(!isValidGenesisBlock(validateBlockchain.get(0))) { //Verify if Genesis Block is correct
            System.out.println("Invalid Genesis Block");
            return false;
        }
        for(int i=1; i < validateBlockchain.size(); i++) { //Verify other blocks
            if(!isValidNewBlock(validateBlockchain.get(i), validateBlockchain.get(i-1)))
                return false;
        }
        
        return true;    
    }
    
    private Boolean addBlockToChain(Block newBlock) {
        if(isValidNewBlock(newBlock, getLastBlock())) {
            this.blockchain.add(newBlock);
            return true;
        }
        return false;
    }
    
    private void replaceChain(ArrayList<Block> newBlocks) {
        if(isValidChain(newBlocks) && (newBlocks.size() > getBlockchain().size())) {
            System.out.println("Blockchain received is valid, the current blockchain was replaced by the received blockchain");
            this.blockchain = newBlocks;
            //FUNCTION TO BROADCAST THIS, UNDONE
        }
        else
            System.out.println("Received Blockchain Invalid");
    }
    
    private Block generateNextBlock(String blockData) {
        Block previousBlock = getLastBlock();
        int nextIndex = previousBlock.index + 1;
        Date nextTimestamp = new Date(System.currentTimeMillis());
        String nextHash = generateHash(nextIndex, previousBlock.hash, nextTimestamp, blockData);
        Block newBlock = new Block(nextIndex, nextHash, previousBlock.hash, nextTimestamp, blockData);
        addBlock(newBlock);
        //BroadcastLatest UNDONE
        return newBlock;
    }
    
}
