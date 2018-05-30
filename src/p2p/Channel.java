package p2p;

import java.io.*;
import java.net.*;

public class Channel extends Thread {
    private Thread t;
    static DatagramSocket server;
    private String threadName;
    private int packetSize;
    public int portNumber;
    private char serverType; //"m" to multicast and "u" to unicast
    public String address;
   
    public Channel (char type, String addr, int port, int packetS, String name) {
        threadName = name;
        portNumber = port;
        packetSize = packetS;
        serverType = type;
        address = addr;
    }
   
    public void run() {
        
        if(serverType == 'u'){ //Unicast Server
        
            try{
                server = new DatagramSocket(portNumber);
                System.out.println("Running  "+serverType+"  "+threadName+"  "+address+"  "+portNumber);
                while(true) {
                    DatagramPacket rPacket = new DatagramPacket(new byte[packetSize], packetSize);
        			server.receive(rPacket);
                    select(rPacket);
                }
    
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else if (serverType == 'm') { //Multicast Server
            
            try (MulticastSocket clientSocket = new MulticastSocket(portNumber)){
                clientSocket.joinGroup(InetAddress.getByName(address));
                System.out.println("Running  "+serverType+"  "+threadName+"  "+address+"  "+portNumber);
                while (true) {
                    DatagramPacket rPacket = new DatagramPacket(new byte[packetSize], packetSize);
                    clientSocket.receive(rPacket);
                    select(rPacket);
                }
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            System.out.println("Server type not defined");
        }


    }
   
    public void start () {
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
    
    public void select (DatagramPacket dPacket) { //This function is Overrided by Extended Classes
        System.out.println("Default Funtion Channel Class");
    }
     
    public static void sendMessage(byte[] msg, String addr, int port){ 
        try (DatagramSocket server = new DatagramSocket()) {
                DatagramPacket msgPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(addr), port);
                server.send(msgPacket);
     
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
