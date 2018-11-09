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
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJRegistryEntry;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.interfaces.GenericSignalReceiver;

/**
 *
 * @author Atmojo
 */
public class ReceiveDisc extends GenericSignalReceiver{

    String DiscOSignalName = null;
    DatagramSocket s1 = null;
    
    @Override
    public void configure(Hashtable data) throws RuntimeException {
        
        if(data.containsKey("DiscReqName")){
            DiscOSignalName = (String)data.get("DiscReqName");
        } else {
            throw new RuntimeException("the 'DiscReqName' attribute is required");
        }
        
    }

    @Override
    public void run(){
        
        while(!terminated){
            
            Object[] obj = new Object[2];
        
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
                        
                        
                        if(DiscPortAssignment.IsDiscPortAssignmentExist(DiscOSignalName)){
                            
                            int portNum = DiscPortAssignment.GetDiscPortAssignment(DiscOSignalName);
                            
                            JSONObject jsMsg = new JSONObject();
                        

                            //synchronized(ss){
                                //jsMsg = SOSJDiscoveryAndReply.TransceiveDiscMsg(locSSName, regID, regAddr);
                                //jsMsg = SOSJDiscovery.TransceiveDiscMsg(SJSSCDSignalChannelMap.getLocalSSName(), regID, regAddr);
                            jsMsg = ReceiveDiscMsg(portNum);
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
                                            

                                            //Enumeration keysjsAllCDNamesStats = jsAllCDStats.keys();

                                        //}

                                    jsAllServs.put(indivSSName, jsServList.getJSONObject(indivSSName));

                                }



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
            
            super.setBuffer(obj);
            
        }
        
    }
    
    public ReceiveDisc(){
		super(); // Initializes the buffer
    }
    
   
        
        private JSONObject ReceiveDiscMsg(int recPort){
        
         //DatagramSocket socket = new DatagramSocket(188);
         //s = new DatagramSocket(199);
        
        JSONObject js = new JSONObject();
        
        
        try{
             
        
                    int infoDebug=0;

                    //DatagramSocket s = new DatagramSocket(199);
                    //MulticastSocket s2 = new MulticastSocket(199);
                    
                    s1 = new DatagramSocket(recPort);
                    
                                       
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
                                                                              
                                                                              //String ssToReceive = js.getString("destSS");
                                                                              
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
                        
                        //s.close();
                        s1.close();
                    } catch (SocketTimeoutException stex){
                        System.out.println("Disc Reply message Timeout");
                        
                        //stex.printStackTrace();
                       
                        //s.close();
                        s1.close();
                    }
                        catch (IOException iex){
                        System.out.println("Discovery and DiscReply communication problem, check for possible disconnection");
                         iex.printStackTrace();
                        
                         //s.close();
                         s1.close();
                    }
                        
                                //} // end of main while loop
                        
                   return js;
        
    }
    
}
