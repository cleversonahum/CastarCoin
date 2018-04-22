package blockchain;

import java.util.Date;
import java.util.Base64;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.lang.StringBuilder;
import java.lang.Math;

public class Blockchain {
    
    public Blockchain() {
        this.blockchain.add(genesisBlock);
    }
    
    private ArrayList<Block> blockchain = new ArrayList<Block>();
    
    private final int BLOCK_GENERATION_INTERVAL = 10;
    private final int LEVEL_ADJUSTMENT_INTERVAL = 10;
    private final int TIMESTAMP_VALIDATION = 60000;
    
    final public Block genesisBlock = new Block(0, "816534932c2b7154836da6afc367695e6337db8a921823784c14378abed4f7d7", "", new Date(System.currentTimeMillis()), "Genesis Block", 0, 0); //First Block into Chain
    
    public ArrayList<Block> getBlockchain() { //Get all blocks
        return this.blockchain;
    }
    
    public Block getLastBlock() { //Get the last block added
        return this.blockchain.get(this.blockchain.size() - 1);
    }
    
    private String generateHash(int index, String previousHash, Date timestamp, String data, int level, int nonce) { //Generate a Hash in accord with parameters of the block
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        String  msgHash = Integer.toString(index) + previousHash + format.format(timestamp) + data + Integer.toString(level) + Integer.toString(nonce);
        String hash="";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash = digest.digest(msgHash.getBytes("UTF-8"));
            hash = Base64.getEncoder().encodeToString(byteHash);
        }
        catch(Exception e){e.printStackTrace();}

        return hash;
    }
    
    private String generateHashBlock(Block block) { //Generate Hash to a Block made
        return generateHash(block.index, block.previousHash, block.timestamp, block.data, block.level, block.nonce);
    }
    
    private Boolean isValidBlockStructure(Block newBlock) { //Verify if Block Variables are in accord with Block Structure
        return ((newBlock.index instanceof Integer) && (newBlock.hash instanceof String) && (newBlock.previousHash instanceof String) && (newBlock.timestamp instanceof Date) && (newBlock.data instanceof String) && (newBlock.level instanceof Integer) && (newBlock.nonce instanceof Integer));    
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
        else if (!previousBlock.hash.equals(newBlock.previousHash)) {
            System.out.println("Invalid Previous Hash");
            return false;
        }
        else if(!isValidTimestamp(newBlock, previousBlock)) {
            System.out.println("Invalid Timestamp");
            return false;
        }
        else if (!hasValidHash(newBlock)) {
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
        return ((this.genesisBlock.index == validateBlock.index) &&                         (this.genesisBlock.hash.equals(validateBlock.hash)) && (this.genesisBlock.previousHash.equals(validateBlock.previousHash)) && (this.genesisBlock.timestamp.equals(validateBlock.timestamp)) && (this.genesisBlock.data.equals(validateBlock.data)));
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
    
    private int getAccumulatedLevel(ArrayList<Block> receivedBlockchain) {
        int sum = 0;
        for(int i=0; i<receivedBlockchain.size();i++) 
            sum += Math.pow(2,receivedBlockchain.get(i).level);
            
        return sum;
    }
    
    private void replaceChain(ArrayList<Block> newBlocks) {
        if(isValidChain(newBlocks) && (getAccumulatedLevel(newBlocks) > getAccumulatedLevel(getBlockchain()))) {
            System.out.println("Blockchain received is valid, the current blockchain was replaced by the received blockchain");
            this.blockchain = newBlocks;
            //FUNCTION TO BROADCAST THIS, UNDONE
        }
        else
            System.out.println("Received Blockchain Invalid");
    }
    
    public Block generateNextBlock(String blockData) { //Making a a new value into a Block
        Block previousBlock = getLastBlock();
        int nextIndex = previousBlock.index + 1;
        int level = getLevel(getBlockchain());
        Date nextTimestamp = new Date(System.currentTimeMillis());
        Block newBlock = findBlock(nextIndex, previousBlock.hash, nextTimestamp, blockData, level);
        addBlock(newBlock);
        //BroadcastLatest UNDONE
        return newBlock;
    }
    
    public void printBlockchain(ArrayList<Block> blocks) {  //Print the blockchain (LOGS)
        for(int i = 0; i<blocks.size(); i++)
            System.out.println("Index: "+blocks.get(i).index+"\nHash: "+blocks.get(i).hash+"\nPrevious Hash: "+blocks.get(i).previousHash+"\nTimestamp: "+blocks.get(i).timestamp+"\nData: "+blocks.get(i).data+"\n\n");
    }
    
    private Boolean hashMatchLevel(String hash, int level) { //Verify the hash level
        String hashBinary = toBinary(hash); //Getting a String of the binary representation
        String requiredPrefix = '0'.repeat(level);
        
        return hashBinary.startsWith(requiredPrefix);
    }
    
    private Boolean hashMatchBlockContent(Block block) {
        return (block.hash.equals(generateHashBlock(block)));
    }
    
    private String toBinary(String text) { //Convert a String to a String of it binary
        StringBuilder sb = new StringBuilder();
        for (char character : text.toCharArray()) {
            sb.append(Integer.toBinaryString(character));
        }
        return sb.toString();
    }
    
    private Block findBlock(int index, String previousHash, Date timestamp, String data, int level) {
        int nonce = 0;
        while(true) {
            String hash = generateHash(index, previousHash, timestamp, data, level, nonce);
            if(hashMatchLevel(hash, level))
                return new Block(index, hash, previousHash, timestamp, data, level, nonce);
            nonce++;
        }
    }
    
    private int getLevel(ArrayList<Block> avaliateBlockchain) {
        Block lastBlock = getLastBlock(avaliateBlockchain);
        if(lastBlock.index % LEVEL_ADJUSTMENT_INTERVAL == 0 && lastBlock.index != 0)
            return getAdjustedLevel(lastBlock, avaliateBlockchain);
        else
            return lastBlock.level;
    }
    
    private int getAdjustedLevel (Block lastBlock, ArrayList<Block> avaliateBlockchain) {
        Block prevAdjustmentBlock = avaliateBlockchain.get(this.blockchain.size() - LEVEL_ADJUSTMENT_INTERVAL);
        int timeExpected = BLOCK_GENERATION_INTERVAL * LEVEL_ADJUSTMENT_INTERVAL;
        double timeTaken = lastBlock.timestamp.getTime() - prevAdjustmentBlock.timestamp.getTime();
        
        if(timeTaken < (timeExpected/2))
            return (prevAdjustmentBlock.level+1);
        else if(timeTaken > (timeExpected*2))
            return (prevAdjustmentBlock.difficult-1);
        else
            return prevAdjustmentBlock.level;
    }
    
    private Boolean isValidTimestamp(Block newBlock, Block previousBlock) {
        return ((previousBlock.timestamp.getTime() - TIMESTAMP_VALIDATION) < newBlock.timestamp.getTime()) && ((newBlock.timestamp.getTime() - TIMESTAMP_VALIDATION) < new Date().getTime());
    }
    
    private Date getCurrentTimestamp(){
        return new Date(System.currentTimeMillis());
    }
    
    private Boolean hasValidHash(Block block) {
        if(!hashMatchBlockContent(block)) {
            System.out.println("Invalid Hash: " + block.hash);
            return false;
        }
        else if(!hashMatchLevel(block.hash, block.level)) {
            System.out.println("Block Level not satisfied");
            return false;
        }
        else
            return true;
    }

}
