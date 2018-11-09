/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.CDLCBuffer;
import systemj.common.InflaterDeflater;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.common.SOAFacility.Support.SOABuffer;

/**
 *
 * @author Udayanto
 */
public class MigrationAndLinkLocalReqMsgRecThread implements Runnable{
    
    @Override
    public void run() {
        
        NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        System.out.println("MigrationAndLinkLocalReqMessageReceiver thread started");
        
        while(true){
            
          //  if (SJServiceRegistry.getParsingStatus()){

            //System.out.println("MessageReceiver thread executed");
            
                //String connStat = netcheck.CheckNetworkConn("192.168.1.1", 1300);
                String connStat = netcheck.CheckNetworkConn(SOABuffer.getGatewayAddr(), 2500);
                //System.out.println("MessageReceiver, ConnectionStat: " +connStat);
                
                if (connStat.equalsIgnoreCase("Connected")){
                    
                    JSONObject jsMsg = ReceiveMigAndLinkMsg();
                
                    if (!jsMsg.isEmpty()) {
                    
                            System.out.println("MigrationAndLinkLocalReqMsgRecThreadReceiver, requestServiceMigration message received");
                        
                            ResponseMigAndLinkReq(jsMsg);
                            
                    }
                    
                }
             
        }
        
    }
    
    private JSONObject ReceiveMigAndLinkMsg(){
        
        //MulticastSocket socket = null;
            
        JSONObject js = new JSONObject();
        
            int debug = 1;
		int infoDebug = 0;
                
                            try
                            {

                                //while(true){

                                    byte data[];
                                    
                                    byte packet[] = new byte[65508];
                                    
                                    MulticastSocket socket = new MulticastSocket(78);
                                    socket.joinGroup(InetAddress.getByName("224.0.0.100"));
                                        //if (socket==null ) {
                                        //socket.setLoopbackMode(true);
                                       // }
                                        if (infoDebug==1) System.out.println("wait for message");
                                        DatagramPacket pack = new DatagramPacket(packet, packet.length);
                                        
                                        //System.out.println("Own IP: "+SJServiceRegistry.getOwnIPAddressFromRegistry());
                                        while (true){
                                            pack = new DatagramPacket(packet, packet.length);
                                           
                                            socket.receive(pack);
                                            
                                           // if (!pack.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            //    break;
                                            //}
                                            
                                        
                                        
                                        
                                        // packet[] is received here
                                        
                                        
                                        
                                        //need to ignore message coming from its own
                                        
                                   //    if (!pack.getAddress().toString().contains(SJServiceRegistry.getOwnIPAddressFromRegistry())){
                                            
                                                if(infoDebug == 1) System.out.println("SOA Migration received pack length = " + pack.getLength() + ", from " + pack.getSocketAddress()+ "port" +pack.getPort());
                                                data = new byte[pack.getLength()];
                                                
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
                                                                              
                                                                              if(js.getString("destinationSubsystem").equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                                                                  break;
                                                                              } else {
                                                                                  
                                                                                  if(js.getString("destAddr").equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                                                                                      
                                                                                      if(pack.getAddress().getHostAddress().equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                                                                                          
                                                                                      } else {
                                                                                          BouncePacket(mybuffer.toString());
                                                                                      }
                                                                                      
                                                                                  }
                                                                                      
                                                                                      //if(js.getString("destinationSubsystem").equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                                                                      //    break;
                                                                                     // } else {
                                                                                          //Thread miglinkbouncpkt = new Thread(new MigLinkPacketBouncer(mybuffer.toString()));
                                                                                          //miglinkbouncpkt.start();
                                                                                     // }
                                                                                      
                                                                                  
                                                                                  
                                                                              }
                                                                              
                                                                              
                                                                              //if (debug==1) System.out.println("UDPSOAReceiver, from:" +sourceSS+" Received service:" +js.toPrettyPrintedString(2, 0));
                                                                              
                                                                              
                                                                              
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
                                         }
                                          
                                        // own-IP check ends here
                                          
                                                socket.close();
                                        
                                //} // end of main while loop
                        }
                        catch (SocketException se)
                        {
                            
                                se.printStackTrace();
                                
                        }
                        catch (Exception e)
                        {
                                e.printStackTrace();
                                
                        }
                            
                        return js;
        
    }
    
    private void BouncePacket(String message) {
        
        MulticastSocket socket = null;
        
        int infoDebug=0;
        
        try{
            socket = new MulticastSocket(177);
            byte[] msg = new byte[65508];
                                                                                                
                                                                                                InetAddress ipAddress = InetAddress.getByName("224.0.0.100");
                                                                                                
                                                                                                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                                                                                ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                                                                                              //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                                                                                                out.writeObject(message); //put service description to be sent to remote devices
                                                                                                out.flush();
                                                                                                msg = byteStream.toByteArray();
                                                                                                out.close();
                                                                                                //msg = InflaterDeflater.compress(msg);
                                                                                                //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                                                                                                DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 177);
                                                                                                if (infoDebug ==1 ) System.out.println("Sending data...");
                                                                                                socket.send(hi);
                                                                                                System.out.println("RegRemoteMessageReceiverThread, bouncing msg to dest (local) SS, msg sent: " +message);
                                                                                                if (infoDebug ==1 ) System.out.println("data has been sent!");
                                                                                                
            
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
         
        
    }
    
    
    private void ResponseMigAndLinkReq(JSONObject reqMsg){
         
                    //start migration msg receiver thread
                    
                   
                     SJSOAMessage sjdisc = new SJSOAMessage();
                    
        try {
            String msgType = reqMsg.getString("MsgType");
            
            if(msgType.equals("reqLinkCreation")){
                
                // response
                
                String destAddress="";
                
                if(reqMsg.getString("sourceAddress").equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                    destAddress = "224.0.0.100";
                } else {
                    destAddress = reqMsg.getString("sourceAddress");
                }
                
                //String destAddress = "224.0.0.100";
                                    String destSS = reqMsg.getString("associatedSS");
                                    
                                    if(CDLCBuffer.GetLinkCreationBusyFlag()){
                                        String message = sjdisc.CreateLinkCreationRespMsg(SJSSCDSignalChannelMap.getLocalSSName(), destSS, destAddress, "NOT OK");
                                        SendRespMigrationAndLink(destAddress, message);
                                    } else {
                                        String message = sjdisc.CreateLinkCreationRespMsg(SJSSCDSignalChannelMap.getLocalSSName(), destSS, destAddress, "OK");
                                        SendRespMigrationAndLink(destAddress, message);
                                        
                                        CDLCBuffer.SetLinkCreationBusyFlag(true);
                                        
                                        if(destAddress.equals(SJSSCDSignalChannelMap.GetLocalSSAddr())){
                                            
                                            Thread lclinkcrRecTh = new Thread(new LocalLinkCreationHSReceiverThread());
                                            lclinkcrRecTh.start();
                                            
                                        } else {
                                            Thread rmlinkcrRecTh = new Thread(new RemoteLinkCreationHSReceiverThread());
                                            rmlinkcrRecTh.start();
                                        }
                                        
                                    }
                                    
                                   
                
                                    
                                    
                
            } else if (msgType.equals("requestStrongServiceMigration") || msgType.equals("requestWeakServiceMigration")){
                
                if(CDLCBuffer.getMigrationBusyFlag()){
                               // try {
                                    //String destAddress = reqMsg.getString("sourceAddress");
                    String destAddress = "224.0.0.100";
                                    String destSS = reqMsg.getString("associatedSS");
                                    
                                    String message = sjdisc.ConstructResponseMigrationMessage("NOT OK", SJSSCDSignalChannelMap.getLocalSSName(), destSS);
                                
                                    SendRespMigrationAndLink(destAddress, message);
                                //}   catch (JSONException ex) {
                                //    ex.printStackTrace();
                               // }
                            } else {
                             //   try {
                                //String destAddress = reqMsg.getString("sourceAddress");
                                String destAddress = "224.0.0.100";
                                String destSS = reqMsg.getString("associatedSS");
                                String sourceDestSS = reqMsg.getString("destinationSubsystem");
                                String migType = reqMsg.getString("MsgType");
                                
                                if(SJSSCDSignalChannelMap.getLocalSSName().equals(sourceDestSS)){
                                    
                                    String message = sjdisc.ConstructResponseMigrationMessage("OK", SJSSCDSignalChannelMap.getLocalSSName(),destSS );
                                
                                    SendRespMigrationAndLink(destAddress, message);
                                    
                                    if(migType.equals("requestStrongServiceMigration")){
                                        
                                        //Thread CodeOnlyMigMsgRecThr = new Thread(new StrongMigrationMsgReceiverThread());
                                        
                                        Thread strongmigtrnsrec = new Thread(new StrongMigrationTransferRecThread());
                                        
                                        CDLCBuffer.setMigrationBusyFlag();
                                        
                                        strongmigtrnsrec.start();
                                        
                                        
                                        //CodeOnlyMigMsgRecThr.start();
                                    } else if(migType.equals("requestWeakServiceMigration")){
                                        //Thread WeakMigMsgRecThr = new Thread(new WeakMigrationMsgReceiverThread());
                    
                                        Thread weakmigtrnsrec = new Thread(new WeakMigrationTransferRecThread());
                                        
                                        CDLCBuffer.setMigrationBusyFlag();
                                        
                                        weakmigtrnsrec.start();
                                        
                                        //WeakMigMsgRecThr.start();
                                    }
                                    
                                    
                                
                                    
                                    
                                } else {
                                    String message = sjdisc.ConstructResponseMigrationMessage("NOT OK", SJSSCDSignalChannelMap.getLocalSSName(), destSS);
                                
                                    //Thread MigMsgRecThr = new Thread(new MigrationMsgReceiverThread());
                    
                                    //MigMsgRecThr.start();
                                
                                    SendRespMigrationAndLink(destAddress, message);
                                }
                                
                                
                              //  } catch (JSONException ex) {
                               //     ex.printStackTrace();
                               /// }
                            
                        } 
                
            } else if (msgType.equals("chanQuery")){
                
                String inchanName = reqMsg.getString("inchanName");
                
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
                        
                        //the first message is responded with ACK OK, and then needs to initiate code n sigchan mapping det, the rest of the message responded with ACK NOT OK. Opposite party needs to resend migration req if they wish
                        
                            
     }     
      
   
    
   private void SendRespMigrationAndLink(String destAddress, String message){
        
        int infoDebug=1;
        
        try
                                   {
                                       InetAddress ipAddress = InetAddress.getByName(destAddress);
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 1");
                                       byte[] msg = new byte[65508];
                                      //ipAddress=InetAddress.getByName("192.168.1.255"); //assumed broadcast address
                                      //ipAddress = InetAddress.getByName(super.buffer[1].toString());
                                       
                                       //if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 2");
                                       
                                       
                                       //if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 3");
                                               //SJServiceRegistry.ConstructBroadcastDiscoveryMessage("AllNodes").toString();
                                       
                                       //MulticastSocket s = new MulticastSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                       
                                       MulticastSocket s = new MulticastSocket(66);
                                       
                                       //s.setLoopbackMode(true);
                                       
                                       ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                       
                                       //DatagramSocket s = new DatagramSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                      
                                       //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                                       
                                       ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                                     //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                                       out.writeObject(message); //put service description to be sent to remote devices
                                       out.flush();
                                       //if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 4");
                                       msg = byteStream.toByteArray();
                                       out.close();
                                       //if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 5");
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 66);
                                       if (infoDebug ==1 ) System.out.println("Sending ResponseMigrationReqMessage: "+message+" to : " +ipAddress);
                                       s.send(hi);
                                       if (infoDebug ==1 )System.out.println("ResponseMigrationReqMessage has been sent!");
                                       s.close();
                                       
                                           
                               }
                               catch (java.net.SocketTimeoutException e)
                               {
                                       System.out.println("Timeout when sending to ip: " + destAddress + " port :" + 66);
                               }
                               catch (Exception e)
                               {
                                       System.out.println("Problem when sending to ip: " + destAddress + " port :" + 77);
                                       e.printStackTrace();
                               }
        
        
    }
     
     /*
     private void ResponseMigReq(){
         
          Vector allMigrationReqMsg = CDLCBuffer.getMigrationRequestMsg();
                
                if (allMigrationReqMsg.size()>0) {
                    
                    //start migration msg receiver thread
                    
                    int recMsgAmount = allMigrationReqMsg.size();
                    
                    for (int j=0;j<recMsgAmount;j++){
                        
                        //the first message is responded with ACK OK, and then needs to initiate code n sigchan mapping det, the rest of the message responded with ACK NOT OK. Opposite party needs to resend migration req if they wish
                        
                        JSONObject reqMsg = (JSONObject)allMigrationReqMsg.get(j);
                        
                        if(j==0){
                            
                            boolean migPortFree = CheckMigrationRecPortFree();
                            
                            if(CDLCBuffer.getMigrationBusyFlag() || !migPortFree){
                                try {
                                    String destAddress = reqMsg.getString("sourceAddress");
                                
                                    String message = sjdisc.ConstructResponseMigrationMessage("NOT OK", SJSSCDSignalChannelMap.getLocalSSName());
                                
                                    SendRespServMigration(destAddress, message);
                                }   catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                String destAddress = reqMsg.getString("sourceAddress");
                                String sourceDestSS = reqMsg.getString("destinationSubsystem");
                                String migType = reqMsg.getString("discMsgType");
                                
                                if(SJSSCDSignalChannelMap.getLocalSSName().equals(sourceDestSS)){
                                    
                                    String message = sjdisc.ConstructResponseMigrationMessage("OK", SJSSCDSignalChannelMap.getLocalSSName());
                                
                                    if(migType.equals("strong")){
                                        //Thread CodeOnlyMigMsgRecThr = new Thread(new StrongMigrationMsgReceiverThread());
                    
                                        //CodeOnlyMigMsgRecThr.start();
                                    } else if(migType.equals("weak")){
                                        //Thread WeakMigMsgRecThr = new Thread(new WeakMigrationMsgReceiverThread());
                    
                                        //WeakMigMsgRecThr.start();
                                    }
                                    
                                    
                                
                                    SendRespServMigration(destAddress, message);
                                    
                                } else {
                                    String message = sjdisc.ConstructResponseMigrationMessage("NOT OK", SJSSCDSignalChannelMap.getLocalSSName());
                                
                                    //Thread MigMsgRecThr = new Thread(new MigrationMsgReceiverThread());
                    
                                    //MigMsgRecThr.start();
                                
                                    SendRespServMigration(destAddress, message);
                                }
                                
                                
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            
                        } else {
                            
                            try {
                                
                                 String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                
                                String destAddress = reqMsg.getString("sourceAddress");
                                
                                String message = sjdisc.ConstructResponseMigrationMessage("NOT OK", LocalSSName);
                                
                                SendRespServMigration(destAddress, message);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                        
                        
                        
                    }
                    
                
         
     }
    
     }           
    */
}
