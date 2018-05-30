package blockchain;

import transaction.*;
import wallet.*;
import p2p.MC;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Base64;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.lang.StringBuilder;
import java.lang.Math;

public class Blockchain {
    
    public Blockchain(ArrayList<String> peers) {
        this.peers = peers;
        //Init Genesis Transaction
        TxIn genesisTxIn = new TxIn();
        genesisTxIn.txOutId = "";
        genesisTxIn.txOutIndex = 0;
        genesisTxIn.signature = "";
        this.genesisTransaction.txIns.add(genesisTxIn);
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            PublicKey genesisPublicKey = pair.getPublic();
            this.genesisTransaction.txOuts.add(new TxOut(genesisPublicKey, 50));
        }
        catch(Exception e){e.printStackTrace();}
        this.genesisTransaction.id = Transaction.getTransactionId(this.genesisTransaction);
        
        //Init Genesis Block
        ArrayList<Transaction> genesisTransactionToBlock = new ArrayList<Transaction>();
        genesisTransactionToBlock.add(genesisTransaction);
        this.genesisBlock = new Block(0, "91a73664bc84c0baa1fc75ea6e4aa6d1d20c5df664c724e3159aefc2e1186627", "", new Date(System.currentTimeMillis()), genesisTransactionToBlock, 0, 0);
        
        //Init Blockchain
        this.blockchain.add(genesisBlock);
        
        //Init UnspentTxOut
        ArrayList<UnspentTxOut> genesisUnpentTxout = new ArrayList<UnspentTxOut>();
        this.unspentTxOuts= Transaction.processTransactions(this.blockchain.get(0).data, genesisUnpentTxout,0);
    }
    
    private ArrayList<Block> blockchain = new ArrayList<Block>();
    
    private final int BLOCK_GENERATION_INTERVAL = 10;
    private final int LEVEL_ADJUSTMENT_INTERVAL = 10;
    private final int TIMESTAMP_VALIDATION = 60000;
    
    private ArrayList<String> peers = new ArrayList<>();
    
    final public Transaction genesisTransaction = new Transaction(); //Initiated in constructor
    
    final public Block genesisBlock; //First Block into Chain, initiated in constructor
    
    public ArrayList<UnspentTxOut> unspentTxOuts = new ArrayList<UnspentTxOut>();
    
    public ArrayList<Block> getBlockchain() { //Get all blocks
        return this.blockchain;
    }
    
    public ArrayList<UnspentTxOut> getUnspentTxOuts() {
        return this.unspentTxOuts;
    }
    
    public void setUnspentTxOuts(ArrayList<UnspentTxOut> newUnspentTxOut) {
        System.out.println("Replacing UnspentTxOuts");
        this.unspentTxOuts = new ArrayList<>(newUnspentTxOut);
    }
    
    public Block getLastBlock() { //Get the last block added
        return this.blockchain.get(this.blockchain.size() - 1);
    }
    
    public Block getLastBlock(ArrayList<Block> blocks) { //Get the last block added
        return blocks.get(blocks.size() - 1);
    }
    
    private String generateHash(int index, String previousHash, Date timestamp, ArrayList<Transaction> data, int level, int nonce) { //Generate a Hash in accord with parameters of the block
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
    
        
//    const calculateHash = (index: number, previousHash: string, timestamp: number, data: Transaction[],
//                       difficulty: number, nonce: number): string =>
//    CryptoJS.SHA256(index + previousHash + timestamp + data + difficulty + nonce).toString();
    
    private String generateHashBlock(Block block) { //Generate Hash to a Block made
//        System.out.println("BLOCK INFO: \nIndex: "+block.index+"\nPreviousHash: "+block.previousHash+"\nTimestamp: "+block.timestamp+"\nblockData: ")
        return generateHash(block.index, block.previousHash, block.timestamp, block.data, block.level, block.nonce);
    }
    
    public Boolean isValidBlockStructure(Block newBlock) { //Verify if Block Variables are in accord with Block Structure
        return ((newBlock.index instanceof Integer) && (newBlock.hash instanceof String) && (newBlock.previousHash instanceof String) && (newBlock.timestamp instanceof Date) && (newBlock.data instanceof ArrayList) && (newBlock.level instanceof Integer) && (newBlock.nonce instanceof Integer));    
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
        return ((this.genesisBlock.index == validateBlock.index) && (this.genesisBlock.hash.equals(validateBlock.hash)) && (this.genesisBlock.previousHash.equals(validateBlock.previousHash)) && (this.genesisBlock.timestamp.equals(validateBlock.timestamp)) && (this.genesisBlock.data.equals(validateBlock.data)));
    }
    
    private ArrayList<UnspentTxOut> isValidChain(ArrayList<Block> validateBlockchain) {
        if(!isValidGenesisBlock(validateBlockchain.get(0))) { //Verify if Genesis Block is correct
            System.out.println("Invalid Genesis Block");
            return null;
        }
        
        ArrayList<UnspentTxOut> avaliateUnspentTxOuts = new ArrayList<UnspentTxOut>();
        
        for(int i=1; i < validateBlockchain.size(); i++) { //Verify other blocks
            Block currentBlock = validateBlockchain.get(i);
            
            if(!isValidNewBlock(validateBlockchain.get(i), validateBlockchain.get(i-1)))
                return null;
                
            avaliateUnspentTxOuts = Transaction.processTransactions(currentBlock.data, avaliateUnspentTxOuts, currentBlock.index);
            if(avaliateUnspentTxOuts==null) {
                System.out.println("Invalid Transaction in Blockchain");
                return null;
            }
        }
        
        return avaliateUnspentTxOuts;    
    }
    
    public Boolean addBlockToChain(Block newBlock, TxPool txPool) {
        if(isValidNewBlock(newBlock, getLastBlock())) {
            ArrayList<UnspentTxOut> retVal = Transaction.processTransactions(newBlock.data, getUnspentTxOuts(), newBlock.index);
            if(retVal == null) {
                System.out.println("Block is not valid in transaction");
                return false;
            }
            else {
                //System.out.println("PAST BLOCKCHAIN: "+this.blockchain);
                this.blockchain.add(newBlock);
                //System.out.println("NEW BLOCKCHAIN: "+this.blockchain);
                setUnspentTxOuts(retVal);
                txPool.updateTransactionPool(this.unspentTxOuts);
                return true;
            }
        }
        return false;
    }

    
    private int getAccumulatedLevel(ArrayList<Block> receivedBlockchain) {
        int sum = 0;
        for(int i=0; i<receivedBlockchain.size();i++) 
            sum += Math.pow(2,receivedBlockchain.get(i).level);
            
        return sum;
    }
    
    public void replaceChain(ArrayList<Block> newBlocks, TxPool txPool) {
        ArrayList<UnspentTxOut> avaliateUnspentTxOuts = isValidChain(newBlocks);
        Boolean validChain = (avaliateUnspentTxOuts != null);
        
        if(validChain && (getAccumulatedLevel(newBlocks) > getAccumulatedLevel(getBlockchain()))) {
            System.out.println("Blockchain received is valid, the current blockchain was replaced by the received blockchain");
            this.blockchain = newBlocks;
            setUnspentTxOuts(avaliateUnspentTxOuts);
            txPool.updateTransactionPool(unspentTxOuts);
            MC.broadcastLastMsg(getBlockchain(), this.peers);
        }
        else
            System.out.println("Received Blockchain Invalid");
    }

    public Block generateNextBlock(Wallet wallet, TxPool txPool) {
        Transaction coinbaseTx = Transaction.getCoinbaseTransaction(wallet.getPublicFromWallet(), getLastBlock().index+1);
        ArrayList<Transaction> transactionPool = txPool.getTransactionPool();
        transactionPool.add(0, coinbaseTx);
        ArrayList<Transaction> blockData = transactionPool;
        
        return generateRawNextBlock(blockData, txPool);
    }
    
    public Block generateNextBlockTransaction(PublicKey receiverAddress, Integer amount, Wallet wallet, TxPool txPool) {
        if(!Transaction.isValidAddress(receiverAddress)) {
            System.out.println("Invalid Address");
            throw new java.lang.RuntimeException("Invalid Address");
        }
        else if(!(amount instanceof Integer)) {
            System.out.println("Invalid Amount");
            throw new java.lang.RuntimeException("Invalid Amount");
        }
        
        Transaction coinbaseTx = Transaction.getCoinbaseTransaction(wallet.getPublicFromWallet(), getLastBlock().index+1);
        Transaction tx = wallet.createTransaction(receiverAddress, amount, wallet.getPrivateFromWallet(), getUnspentTxOuts(), txPool.getTransactionPool());
        ArrayList<Transaction> blockData = new ArrayList<Transaction>();
        blockData.add(coinbaseTx);
        blockData.add(tx);
        return generateRawNextBlock(blockData, txPool);
    }

    public void printBlockchain(ArrayList<Block> blocks) {  //Print the blockchain (LOGS)
        for(int i = 0; i<blocks.size(); i++)
            System.out.println("Index: "+blocks.get(i).index+"\nHash: "+blocks.get(i).hash+"\nPrevious Hash: "+blocks.get(i).previousHash+"\nTimestamp: "+blocks.get(i).timestamp+"\nData: "+blocks.get(i).data+"\nLevel: "+blocks.get(i).level+"\nNonce: "+blocks.get(i).nonce+"\n\n");
    }
    
    private Boolean hashMatchLevel(String hash, int level) { //Verify the hash level
        String hashBinary = toBinary(hash); //Getting a String of the binary representation
        String requiredPrefix = new String(new char[level]).replace("\0", "0");
        
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
    
    private Block findBlock(int index, String previousHash, Date timestamp, ArrayList<Transaction> data, int level) {
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
            return (prevAdjustmentBlock.level-1);
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
            System.out.println("Invalid Hash: " + block.hash+"\nExpected: "+generateHashBlock(block));
            return false;
        }
        else if(!hashMatchLevel(block.hash, block.level)) {
            System.out.println("Block Level not satisfied");
            return false;
        }
        else
            return true;
    }
    
    private Block generateRawNextBlock(ArrayList<Transaction> blockData, TxPool txPool) {
        Block previousBlock = getLastBlock();
        int level = getLevel(getBlockchain());
        int nextIndex = previousBlock.index + 1;
        Date nextTimestamp = getCurrentTimestamp();
        Block newBlock = findBlock(nextIndex, previousBlock.hash, nextTimestamp, blockData, level);
        if(addBlockToChain(newBlock, txPool)) {
            //System.out.println("Coinbase saindo é: "+newBlock.data.get(0).txOuts.get(0).amount);
            MC.broadcastLastMsg(getBlockchain(), this.peers);
            return newBlock;
        }
        else
            return null;
    }
    
    public UnspentTxOut getMyUnspentTransactionOutputs(Wallet wallet) {
        return wallet.findUnspentTxOuts(wallet.getPublicFromWallet(), getUnspentTxOuts());
    }
    
    public int getAccountBalance (Wallet wallet) {
        return wallet.getBalance(wallet.getPublicFromWallet(), getUnspentTxOuts());
    }
    
    public Transaction sendTransaction(PublicKey address, int amount, Wallet wallet, TxPool txPool) {
        Transaction tx = wallet.createTransaction(address, amount, wallet.getPrivateFromWallet(), getUnspentTxOuts(), txPool.getTransactionPool());
        txPool.addToTransactionPool(tx, getUnspentTxOuts());
        MC.broadcastTransactionPool(txPool, this.peers);
        
        return tx;
    }
    
    public void handleReceivedTransaction(Transaction transaction, TxPool txPool) {
        txPool.addToTransactionPool(transaction, getUnspentTxOuts());
    }

}
