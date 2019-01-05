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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.CDLCBuffer;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJRegistryEntry;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
//import systemj.common.SOAFacility.SOSJDiscoveryAndReply;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Udayanto
 */
public class TransceiveDisc extends GenericSignalReceiver{

    //private final static Object ss = new Object();
    
    //private String signalName;
    
    DatagramSocket s1 = null;
    ServerSocket ss1 = null;
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
    }

    @Override
    public void run() {
        
    }
    
    @Override
	public void getBuffer(Object[] obj){
            
            //String locSSName = SJSSCDSignalChannelMap.getLocalSSName();
            JSONObject allAvailReg = SJRegistryEntry.GetRegistryFromEntry();
            
            JSONObject jsAllServs = new JSONObject();
            
            //System.out.println("TransceiveDisc, Trying to discover services, registry: " +allAvailReg);
            
            if(allAvailReg.length()>0){
                
                Enumeration keysAllReg = allAvailReg.keys();
                
                //DatagramSocket s=null;
                //MulticastSocket s2=null;
                
                while (keysAllReg.hasMoreElements()){
                    
                    String regID = keysAllReg.nextElement().toString();
                    
                    try {
                        String regAddr = allAvailReg.getString(regID);
                        
                        String ownAddr = SJSSCDSignalChannelMap.GetLocalSSAddr();
                        
                        if(regAddr.equals(ownAddr)){
                            regAddr = "224.0.0.100";
                        }
                        
                        //s = new DatagramSocket(199);
                        //s2= new MulticastSocket();
                        
                        JSONObject jsMsg = new JSONObject();
                        
                        //synchronized(ss){
                            //jsMsg = SOSJDiscoveryAndReply.TransceiveDiscMsg(locSSName, regID, regAddr);
                            //jsMsg = SOSJDiscovery.TransceiveDiscMsg(SJSSCDSignalChannelMap.getLocalSSName(), regID, regAddr);
                        jsMsg = TransceiveDiscMsg(SJSSCDSignalChannelMap.getLocalSSName(), regID, regAddr);
                        //}
                        //}
                        
                        if(!jsMsg.isEmpty()){
                           
                            JSONObject jsServList = jsMsg.getJSONObject("serviceList");
                            
                            Enumeration keysServList = jsServList.keys();
                            
                                while(keysServList.hasMoreElements()){
                                    String indivSSName = keysServList.nextElement().toString();

                                   // JSONObject jsIndivSSServ = jsServList.getJSONObject(indivSSName);
                                    
                                    //String SSAddr = jsIndivSSServ.getString("nodeAddress");
                                    //String IndCDName = jsIndivSSServ.getString("associatedCDName");
                                    
                                    //while(keysjsAllCDNamesStats.hasMoreElements()){
                                        //String IndCDName = keysjsAllCDNamesStats.nextElement().toString();

                                        //CDLCBuffer.AddCDLocTempToBuffer(IndCDName, indivSSName);
                                        
                                        /*
                                        JSONObject jsAllCDStats = jsMsg.getJSONObject("CDStats");
                                        JSONObject jsAllSSAddrs = jsMsg.getJSONObject("SSAddrs");
                                        
                                        Enumeration keysAllCDStats = jsAllCDStats.keys();
                                        
                                        InterfaceManager im = IMBuffer.getInterfaceManagerConfig();
                                        
                                        while(keysAllCDStats.hasMoreElements()){
                                            String indivSSNameCDStats = keysAllCDStats.nextElement().toString();
                                            
                                            RegAllCDStats.UpdateCDStat(indivSSNameCDStats, jsAllCDStats.getJSONObject(indivSSNameCDStats));
                                            
                                            
                                            
                                            JSONObject jsCDStats = jsAllCDStats.getJSONObject(indivSSNameCDStats);
                                            
                                            Enumeration keysJSCDStats = jsCDStats.keys();
                                            
                                            while(keysJSCDStats.hasMoreElements()){
                                                
                                                String keyCDName = keysJSCDStats.nextElement().toString();
                                                
                                                im.addCDLocation(indivSSNameCDStats, keyCDName);
                                                
                                            }
                                            
                                           
                                        }
                                        
                                        IMBuffer.SaveInterfaceManagerConfig(im);
                                        
                                        Enumeration keysAllSSAddrs = jsAllSSAddrs.keys();
                                        
                                        while(keysAllSSAddrs.hasMoreElements()){
                                            String indivSSNameSSAddr = keysAllSSAddrs.nextElement().toString();
                                            
                                             RegAllSSAddr.AddSSAddr(indivSSNameSSAddr, jsAllSSAddrs.getString(indivSSNameSSAddr));
                                            
                                        }
                                        */
                                       
                                        //Enumeration keysjsAllCDNamesStats = jsAllCDStats.keys();

                                    //}
                                
                                jsAllServs.put(indivSSName, jsServList.getJSONObject(indivSSName));
                                
                            }
                            
                            
                           
                        } 
                        
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        //obj[0]=Boolean.FALSE;
                        //s.close();
                    } 
                    
                }
                
            }
            
            if(jsAllServs.length()>0){
                obj[0] = Boolean.TRUE;
                obj[1] = jsAllServs.toString();
            } else {
                obj[0] = Boolean.FALSE;
            }
            
        }
        
        
        
        public TransceiveDisc(){
		super(); // Initializes the buffer
	}
        
        private JSONObject TransceiveDiscMsg(String SSOrigin,String regID, String regAddr){
        
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
                                        
                                           
                                            s1.setSoTimeout(5000);
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
    
    private int getAvailablePortTCP() {
   
         int port = 3333;
         
         while(true){
             
                try {
                    
                    
                    
                   ss1 = new ServerSocket(port,50,InetAddress.getByName(SJSSCDSignalChannelMap.GetLocalSSAddr()));

                    // If the code makes it this far without an exception it means
                    // something is using the port and has responded.
                    //System.out.println("--------------Port " + port + " is available");
                   
                    break;
                   
                } catch (Exception e) {
                    //System.out.println("--------------Port " + port + " is not available");
                    port++;
                    //e.printStackTrace();
                    
                } 
         }
         return port;
        
    }
    
    public synchronized JSONObject TransceiveDiscMsgTCP(String SSOrigin,String regID, String regAddr){
        
         //DatagramSocket socket = new DatagramSocket(188);
         //s = new DatagramSocket(199);
        
        JSONObject js = new JSONObject();
          SJSOAMessage sjdisc = new SJSOAMessage();
        //SJSOAMessage sjdisc = new SJSOAMessage();
        
        
        MulticastSocket s = null;
        
        try{
             
        
                    int infoDebug=0;

                    //DatagramSocket s = new DatagramSocket(199);
                    //MulticastSocket s2 = new MulticastSocket(199);
                    int portNum = getAvailablePortTCP();
                    s = new MulticastSocket(199);
                    
                                String message = sjdisc.ConstructNoP2PServToRegDiscoveryMessage(SSOrigin, regID, portNum);
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
                                       //    s.joinGroup(InetAddress.getByName("224.0.0.100"));
                                       //}
                                   
                                     //  byte data[];
                                    
                                    //byte packet[] = new byte[65508];
                                    //DatagramPacket pack = new DatagramPacket(packet, packet.length);
                                       
                                        //System.out.println("Own IP: "+SJServiceRegistry.getOwnIPAddressFromRegistry());
                                        //while (true){
                                           // pack = new DatagramPacket(packet, packet.length);
                                    
                                        //if (socket==null ) {
                                        //socket.setLoopbackMode(true);
                                       // }
                                        if (infoDebug==1) System.out.println("wait for message");
                                        
                                        Socket socketReceive = ss1.accept();
                   
                                        ObjectInputStream sInt = new ObjectInputStream(socketReceive.getInputStream());
                                        
                                        String strdat = sInt.readObject().toString();
                                           
                                        
                                        js = new JSONObject(new JSONTokener(strdat));
                                            //s1.setSoTimeout(5000);
                                            //s1.receive(pack);
                                            //socket.setSoTimeout(2500);
                                            //socket.receive(pack);
                                            
                                           // if (!pack.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            //    break;
                                            //}
                                            
                                        
                                        
                                        
                                        // packet[] is received here
                                        
                                        
                                        
                                        //need to ignore message coming from its own
                                        
                                   //    if (!pack.getAddress().toString().contains(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            
                                                //if(infoDebug == 1) System.out.println("SOA MessageReceiverThread received pack length = " + pack.getLength() + ", from " + pack.getSocketAddress()+ "port" +pack.getPort());
                                                
                                         //}
                                                
                                            
                                        
                                       
                                        // own-IP check ends here
                                        
                                             
                                                //socket.close();
                                        //socket.close();
                                       
                                      socketReceive.close();
                                      ss1.close();

                    } catch (BindException bex){
                        System.out.println("Discovery and DiscReply ports have been bound and currently used by another CD");
                        //bex.printStackTrace();
                        
                        s.close();
                        
                    } catch (SocketTimeoutException stex){
                        System.out.println("Disc Reply message Timeout");
                        
                        //stex.printStackTrace();
                       
                        s.close();
                        
                    }
                        catch (IOException iex){
                        System.out.println("Discovery and DiscReply communication problem, check for possible disconnection");
                         iex.printStackTrace();
                        
                         s.close();
                         
                    } catch (Exception ex){
                        s.close();
                        ex.printStackTrace();
                    }
                        
                                //} // end of main while loop
                        
                   return js;
        
    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
    
    
}
