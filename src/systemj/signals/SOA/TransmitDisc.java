/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.signals.SOA;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJSOAMessage;
import systemj.interfaces.GenericSignalSender;

/**
 *
 * @author Atmojo
 */
public class TransmitDisc extends GenericSignalSender{

    DatagramSocket s1 = null;
    String SigName = null;
    
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("Name")){
            SigName = (String)data.get("Name");
        }
        
    }

    @Override
    public void run() {
        int recPort = getAvailablePort();
        
        DiscPortAssignment.SetDiscPortAssignment(SigName,recPort);
        
    }
    
    private int getAvailablePort() {
   
         int port = 3333;
         
         while(true){
             
                try {
                    s1 = new DatagramSocket(port);

                    // If the code makes it this far without an exception it means
                    // something is using the port and has responded.
                    //System.out.println("--------------Port " + port + " is available");
                   
                    break;
                   
                } catch (IOException e) {
                    //System.out.println("--------------Port " + port + " is not available");
                    port++;
                    //e.printStackTrace();
                    
                } 
         }
         return port;
        
    }
    
    private JSONObject TransmitDiscMsg(String SSOrigin,String regID, String regAddr){
        
         //DatagramSocket socket = new DatagramSocket(188);
         //s = new DatagramSocket(199);
        
        JSONObject js = new JSONObject();
        
        SJSOAMessage sjdisc = new SJSOAMessage();
        
        
        MulticastSocket s = null;
        
        try{
             
        
                    int infoDebug=0;

                    //DatagramSocket s = new DatagramSocket(199);
                    //MulticastSocket s2 = new MulticastSocket(199);
                    int recPort = getAvailablePort();
                    //s1 = new DatagramSocket(recPort);
                    s = new MulticastSocket(199);
                    
                                String message = sjdisc.ConstructNoP2PServToRegDiscoveryMessage(SSOrigin, regID, recPort);
                                //while(true){
                                
                                InetAddress ipAddress = InetAddress.getByName(regAddr);
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 1");
                                       byte[] msg = new byte[65508];
                                      //ipAddress=InetAddress.getByName("192.168.1.255"); //assumed broadcast address
                                      //ipAddress = InetAddress.getByName(super.buffer[1].toString());
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 2");
                                       
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 3");
                                               //SJServiceRegistry.ConstructBroadcastDiscoveryMessage("AllNodes").toString();
                                       
                                       //MulticastSocket s = new MulticastSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                       
                                       
                                       ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                       
                                       //DatagramSocket s = new DatagramSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                      
                                       //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                                       
                                       ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                                     //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                                       out.writeObject(message); //put service description to be sent to remote devices
                                       out.flush();
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 4");
                                       msg = byteStream.toByteArray();
                                       out.close();
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 5");
                                       
                                       System.out.println("SOSJDiscovery, transmitting discovery message: " +message);
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 199);
                                       if (infoDebug ==1 ) System.out.println("Sending BroadcastDiscoveryMessage");
                                       s.send(hi);
                                       if (infoDebug ==1 )System.out.println("data has been sent!");
                                       s.close();
                                       
                                       //if(regAddr.equals("224.0.0.100")){
                                        //   s.joinGroup(InetAddress.getByName("224.0.0.100"));
                                       //}
                                   
                                       

                    } catch (BindException bex){
                        System.out.println("Discovery and DiscReply ports have been bound and currently used by another CD");
                        //bex.printStackTrace();
                        
                        s.close();
                        //s1.close();
                    } catch (SocketTimeoutException stex){
                        System.out.println("Disc Reply message Timeout");
                        
                        //stex.printStackTrace();
                       
                        s.close();
                       // s1.close();
                    }
                        catch (IOException iex){
                        System.out.println("Discovery and DiscReply communication problem, check for possible disconnection");
                         iex.printStackTrace();
                        
                         s.close();
                        // s1.close();
                    }
                        
                                //} // end of main while loop
                        
                   return js;
        
    }
    
}
