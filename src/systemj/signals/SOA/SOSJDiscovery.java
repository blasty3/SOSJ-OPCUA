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
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.SJSOAMessage;

/**
 *
 * @author Udayanto
 */
public class SOSJDiscovery {
    
    static DatagramSocket s1 = null;
    
    public static synchronized JSONObject TransceiveDiscMsg(String SSOrigin,String regID, String regAddr){
        
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
                    s1 = new DatagramSocket(recPort);
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
                                   
                                       byte data[];
                                    
                                    byte packet[] = new byte[65508];
                                    DatagramPacket pack = new DatagramPacket(packet, packet.length);
                                        
                                        //System.out.println("Own IP: "+SJServiceRegistry.getOwnIPAddressFromRegistry());
                                        //while (true){
                                           // pack = new DatagramPacket(packet, packet.length);
                                    
                                        //if (socket==null ) {
                                        //socket.setLoopbackMode(true);
                                       // }
                                        if (infoDebug==1) System.out.println("wait for message");
                                        
                                           
                                            s1.setSoTimeout(10000);
                                            s1.receive(pack);
                                            //socket.setSoTimeout(2500);
                                            //socket.receive(pack);
                                            
                                           // if (!pack.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            //    break;
                                            //}
                                            
                                        
                                        
                                        
                                        // packet[] is received here
                                        
                                        
                                        
                                        //need to ignore message coming from its own
                                        
                                   //    if (!pack.getAddress().toString().contains(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            
                                                if(infoDebug == 1) System.out.println("SOA MessageReceiverThread received pack length = " + pack.getLength() + ", from " + pack.getSocketAddress()+ "port" +pack.getPort());
                                                data = new byte[pack.getLength()];
                                                
                                                //String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                                
                                                System.arraycopy(packet, 0, data, 0, pack.getLength());
     
                                                if(data.length > 0)
                                                {
                                                        if(((int)data[0] == -84) && ((int)data[1] == -19))
                                                        {
                                                                try
                                                                {
                                                                        if(infoDebug == 1) System.out.println("Java built-in deserializer is used");
                                                                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                                                        Object mybuffer = ois.readObject();              
                                                                        if(infoDebug == 1) System.out.println(mybuffer);
                                                                        if(infoDebug == 1) System.out.println((mybuffer.getClass()).getName());
                                                                        if((mybuffer.getClass()).getName().compareTo("[Ljava.lang.Object;") == 0)
                                                                        {
                                                                                Object mybufferArray[] = (Object[])mybuffer;
                                                                                // System.out.println(mybufferArray.length);	
                                                                        }
                                                                        else
                                                                        {
                                                                           if(infoDebug == 1) System.out.println("Direct assign the received byffer to the value 3");


                                                                            //expected format in JSON String
                                                                             if (infoDebug==1) System.out.println("received info in SOA receiver: " +mybuffer.toString().trim()+"\n");
                                                                              
                                                                              js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                                                              
                                                                              //String targetSS = js.getString("targetSS");
                                                                              
                                                                              //if (debug==1) System.out.println("UDPSOAReceiver, from:" +sourceSS+" Received service:" +js.toPrettyPrintedString(2, 0));
                                                                              
                                                                              //System.out.println("MessageReceiverThread, sourceSS:" +sourceSS+ "LocalSSName: " +localSSName+ " Receive msg: " +js.toPrettyPrintedString(2, 0));
                                                                              
                                                                              //System.out.println("SOSJDiscoveryReply, Receive msg: " +js.toPrettyPrintedString(2, 0));
                                                                              
                                                                              String ssToReceive = js.getString("destSS");
                                                                              
                                                                              //if(!ssToReceive.equalsIgnoreCase(destSS)){
                                                                                  
                                                                             // }
                                                                                       //list[1]="{}";

                                                                                     //automatically service registry update --> registry of external service
                                                                                     // SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsData, false);

                                                                        }

                                                                }
                                                                catch(Exception e)
                                                                {
                                                                    //System.out.println(e.getCause());
                                                                        e.printStackTrace();
                                                                }
                                                                
                                                        }
                                                        
                                                }
                                         //}
                                                
                                            
                                        
                                       
                                        // own-IP check ends here
                                        
                                             
                                                //socket.close();
                                        //socket.close();
                                       
                                       //s.close();
                                       s1.close();

                    } catch (BindException bex){
                        System.out.println("Discovery and DiscReply ports have been bound and currently used by another CD");
                        //bex.printStackTrace();
                        
                        s.close();
                        s1.close();
                    } catch (SocketTimeoutException stex){
                        System.out.println("Disc Reply message Timeout");
                        
                        //stex.printStackTrace();
                       
                        s.close();
                        s1.close();
                    }
                        catch (IOException iex){
                        System.out.println("Discovery and DiscReply communication problem, check for possible disconnection");
                         iex.printStackTrace();
                        
                         s.close();
                         s1.close();
                    }
                        
                                //} // end of main while loop
                        
                   return js;
        
    }
    
    private static int getAvailablePort() {
   
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
        
        /*
        finally {
            if( s1 != null){
                try {
                    s1.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error." , e);
                }
            }
        }
                */
    }
    
}
