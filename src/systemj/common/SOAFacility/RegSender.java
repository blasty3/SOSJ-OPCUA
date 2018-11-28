/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import java.io.ObjectOutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.MulticastSocket;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;

import systemj.common.InflaterDeflater;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.common.SOAFacility.Support.SOABuffer;

public class RegSender implements Runnable {

	private boolean IsOPCUA = false;
	
	public RegSender() {
		
	}
	
	public RegSender(boolean IsOPCUA) {
		this.IsOPCUA = IsOPCUA;
	}
	
	
    @Override
    public void run() {
        
        SJSOAMessage sjdisc = new SJSOAMessage();
        
        String GtwyAddr = SOABuffer.getGatewayAddr();
        String SubnetMask = SOABuffer.getSubnetMaskAddr();
        
        String broadcastAddr = getBroadcastAddress(GtwyAddr,SubnetMask);
        
        NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        long lastReqAdvTransmittedTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        
        while (true) {
                String connStat = netcheck.CheckNetworkConn(SOABuffer.getGatewayAddr(), 1400);
            
                    if (connStat.equalsIgnoreCase("Connected")){


                            Vector discMsgs = SOABuffer.getAllDiscMsgFromBuffer();
                            
                            for (int i=0;i<discMsgs.size();i++){
                                JSONObject discMsg = (JSONObject)discMsgs.get(i);
                                
                                try {
                                    String discReplyMsg = ProcessMessage(discMsg, SJServiceRegistry.obtainCurrentRegistry());
                                    int respPort = Integer.parseInt(discMsg.getString("respPort"));
                                    if(!discReplyMsg.equalsIgnoreCase("{}")){
                                       
                                        SendDiscReplyMsg(discReplyMsg, respPort);
                                    }
                                    
                                } catch (JSONException ex) {
                                   ex.printStackTrace();
                                }
                                
                            }
                            //adv transmission from req adv
                        
                            //send registry adv
                            Vector allContentReqAdvBuffer = SOABuffer.getAllContentReqAdvBuffer();
                            
                            if(allContentReqAdvBuffer.size()>0){
                                
                                for (int i=0;i<allContentReqAdvBuffer.size();i++){
                                    
                                     JSONObject jsReqAdvMsg = (JSONObject) allContentReqAdvBuffer.get(i);
                                     
                                            try {
                                                JSONObject generatedRespAdvMsgJSON = ProcessMessageInJSON(jsReqAdvMsg.toString(), SJServiceRegistry.obtainInternalRegistry().toString());     
                                                
                                                String destAddr = jsReqAdvMsg.getString("sourceAddress");
                                                
                                                if(destAddr.equalsIgnoreCase(SOABuffer.getSOSJRegAddr())){
                                                    SendAdvMsg("224.0.0.100", generatedRespAdvMsgJSON.toString());
                                                } else {
                                                    SendAdvMsg(destAddr, generatedRespAdvMsgJSON.toString());
                                                }
                                               
                                            } catch (JSONException ex) {
                                            System.out.println("cannot find destination Address in ProcessMessageInJSON");
                                        } 
                                        
                                }
                                
                            }
                          
                                //regular adv
                            
                            if(!IsOPCUA) {
                            	
                            	 boolean startRegAdv = CheckOwnBeaconAdv();
                                 
                                 if (startRegAdv){
                                   
                                    String AdvMsg = sjdisc.ConstructRegistryReAdvertisementMessage(SOABuffer.getSOSJRegBeaconPeriodTime(), SOABuffer.getSOSJRegID(), SOABuffer.getSOSJRegAddr());
                                     
                                     SendAdvMsg(broadcastAddr, AdvMsg);
                                     SOABuffer.RecordAdvertisementTimeStamp();
                                     
                                 }
                            	
                            }
                            
                            //end regular adv
                            
                            else {
                            	
                            	// wait for command to send notification to target
                            	
                            	String AdvMsg = sjdisc.ConstructRegistryReAdvertisementMessage(SOABuffer.getSOSJRegBeaconPeriodTime(), SOABuffer.getSOSJRegID(), SOABuffer.getSOSJRegAddr());
                                
                                SendAdvMsg(broadcastAddr, AdvMsg);
                                
                            	
                            }
                            
    
                            //notify all SS with broadcast
                            boolean notify = SOABuffer.GetRegNotify();
                            //boolean notifyCDStats = SOABuffer.GetNotifyChangedCDStat();
                            //boolean notifyChangedTotalSS = SOABuffer.GetNotifyChangedTotalSS();
                            
                            if(notify){
                             
                                String NotifyMsg = sjdisc.ConstructRegNotifyMessage(SOABuffer.getSOSJRegID(), Long.toString(System.currentTimeMillis()));
                                
                                SendReqAdvOrNotifMsg(broadcastAddr, NotifyMsg);
                                
                                if(SOABuffer.GetRegNotify()){
                                   SOABuffer.SetRegNotifySS(false);
                                }
                                
                                /*
                                if(SOABuffer.GetNotifyChangedCDStat()){
                                   SOABuffer.SetNotifyChangedCDStat(false);
                                }
                                
                                if(SOABuffer.GetNotifyChangedTotalSS()){
                                   SOABuffer.SetNotifyChangedTotalSS(false);
                                }
                                */
                            }
                          
                     // end adv transmission responding req adv

                    //req adv check and transmission
                           
                            Hashtable expiredServAddrs = CheckAlmostExpiredAdv();
                
                            if (expiredServAddrs.size()>0){
                                
                                    Enumeration keysExpServAddrs = expiredServAddrs.keys();
                                    
                                    currentTime = System.currentTimeMillis();
                                    
                                    if(currentTime-lastReqAdvTransmittedTime>300){
                                        
                                        while(keysExpServAddrs.hasMoreElements()){
                                            String index = keysExpServAddrs.nextElement().toString();
                                        
                                            String destSSName = (String) expiredServAddrs.get(index);
                                        
                                            String destAddr = RegAllSSAddr.getSSAddrOfSSName(destSSName);
                                            
                                            String ReqAdvMsg = sjdisc.ConstructNoP2PRegToProvReqAdvertisementMessage(SOABuffer.getSOSJRegID(), SOABuffer.getSOSJRegAddr(), destSSName);
                                            
                                            if(destAddr.equalsIgnoreCase(SOABuffer.getSOSJRegAddr())){
                                                
                                                 SendReqAdvOrNotifMsg("224.0.0.100",ReqAdvMsg);
                                                
                                            } else {
                                                
                                                 SendReqAdvOrNotifMsg(destAddr,ReqAdvMsg);
                                            
                                            }
                                         
                                        }
                               
                                        lastReqAdvTransmittedTime = System.currentTimeMillis();
                                    }
                                    
                            }
                            
                            
                            //adv transmission
                            
                           // if (!SOABuffer.getIsInitAdvDone()){
                              //  System.out.println("RegMessageSender transmitting beacon!");
                                
                              //      String AdvMsg= sjdisc.ConstructRegistryReAdvertisementMessage(SOABuffer.getSOSJRegExpiryTime(),SOABuffer.getSOSJRegID(), SOABuffer.getSOSJRegAddr());
                              //     SendAdvMsg(broadcastAddr,AdvMsg);
                                    
                              //     SOABuffer.RecordAdvertisementTimeStamp(); 
                                   
                               // SOABuffer.setIsInitAdvDone(true);
                                
                           // } 
                           
                      } else {
                       
                      }
                   
          }
    }
    
    private JSONObject ProcessMessageInJSON(String SJMessage,String OfferedServices)
    {
        JSONObject serviceList = new JSONObject();
        JSONObject processedSJMessage = new JSONObject();
        SJSOAMessage sjdisc = new SJSOAMessage();
        int i = 1;
        try {
            //JSONObject jsAllIntServ = SJServiceRegistry.obtainInternalRegistry();
            JSONObject jsAllIntServ = new JSONObject(new JSONTokener(OfferedServices));
            JSONObject jsMsg = new JSONObject(new JSONTokener(SJMessage));
            
            if (jsMsg.getString("MsgType").equalsIgnoreCase("Discovery")){ //if handling discovery request message
                
                Enumeration keysjsAllIntServ = jsAllIntServ.keys();
                
                while (keysjsAllIntServ.hasMoreElements()){
                    
                    Object keyAllIntServ = keysjsAllIntServ.nextElement();
                    
                    JSONObject jsIndivIntServ = jsAllIntServ.getJSONObject(keyAllIntServ.toString());
                    
                            serviceList.put(jsIndivIntServ.getString("serviceName"),jsIndivIntServ);
                     
                }
                
                if (!serviceList.toString().equalsIgnoreCase("{}")){
                    processedSJMessage = new JSONObject(new JSONTokener(SJMessage));
                  
                    processedSJMessage.put("serviceList",serviceList);
                    processedSJMessage.put("associatedSS",jsMsg.getString("associatedSS"));
                    
                    processedSJMessage.put("CDStats", RegAllCDStats.getAllCDStats());
                    processedSJMessage.put("SSAddrs", RegAllSSAddr.getAllSSAddr());
                    processedSJMessage.put("MsgType","DiscoveryReply");
                } else {
                    processedSJMessage = new JSONObject();
                    
                }
                
            }
           
            else if (jsMsg.getString("MsgType").equalsIgnoreCase("regRequestAdvertise")){
               
                processedSJMessage = sjdisc.ConstructRegResponseReAdvertisementMessageInJSON(SOABuffer.getSOSJRegBeaconPeriodTime(), SOABuffer.getSOSJRegID(), SOABuffer.getSOSJRegAddr());
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            //System.out.println("Error SJMessageHandler Message Process:" +ex.printStackTrace());
            System.exit(1);
        }
        
        return processedSJMessage;
 
    }
    
    private String ProcessMessage(JSONObject jsMsg,JSONObject jsAllServ)
    {
        JSONObject serviceList = new JSONObject();
        JSONObject processedSJMessage = new JSONObject();
        SJSOAMessage sjdisc = new SJSOAMessage();
        int i = 1;
        
        System.out.println("RegMessageSenderThread, responding to Msg: " +jsMsg);
        
        try {
           
            if (jsMsg.getString("MsgType").equalsIgnoreCase("Discovery")){ //if handling discovery request message
                
                Enumeration keysSSName = jsAllServ.keys();
                
                while (keysSSName.hasMoreElements()){
                    
                    String keySSName = keysSSName.nextElement().toString();
                    
                    JSONObject jsAllSSServ = jsAllServ.getJSONObject(keySSName);
                    
                            serviceList.put(keySSName,jsAllSSServ);
                    //    }
                    //}  
                }
             
                    if(jsMsg.getString("sourceAddress").equals(SOABuffer.getSOSJRegAddr())){
                        processedSJMessage.put("destinationAddress","224.0.0.100");
                        processedSJMessage.put("sourceAddress","224.0.0.100");
                    } else {
                        processedSJMessage.put("destinationAddress",jsMsg.getString("sourceAddress"));
                        processedSJMessage.put("sourceAddress",SOABuffer.getSOSJRegAddr());
                    }
                  
                    processedSJMessage.put("serviceList",serviceList);
                    //processedSJMessage.put("CDStats", CDLCBuffer.GetAllCDMacroState());
                    processedSJMessage.put("CDStats", RegAllCDStats.getAllCDStats());
                    processedSJMessage.put("SSAddrs", RegAllSSAddr.getAllSSAddr());
                    
                    processedSJMessage.put("MsgType","DiscoveryReply");
                    
                    if(jsMsg.has("associatedSS")){
                        processedSJMessage.put("destSS",jsMsg.getString("associatedSS"));
                    }
                    
                   
               // } 
                
            }
           
            else if (jsMsg.getString("MsgType").equalsIgnoreCase("regRequestAdvertise")){
                
                //processedSJMessage = new JSONObject(new JSONTokener(SJMessage));

                
                String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                
                String regAddr = SOABuffer.getSOSJRegAddr();
                
                if(regAddr.equals(jsMsg.getString("sourceAddress"))){
                    
                    String destIPAddr = "224.0.0.100";
                
                    processedSJMessage = sjdisc.ConstructResponseReAdvertisementMessageInJSON(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()), destIPAddr, LocalSSName);
                    
                } else {
                    
                    String destIPAddr = jsMsg.getString("sourceAddress");
                
                    processedSJMessage = sjdisc.ConstructResponseReAdvertisementMessageInJSON(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()), destIPAddr, LocalSSName);
                    
                }
                
            }
            
        } catch (JSONException ex) {
            
            ex.printStackTrace();
            System.exit(1);
        }
        
        return processedSJMessage.toString();
 
    }
    
    private void SendAdvMsg(String ipAddr, String message){
        
        int infoDebug=0;
        int Debug=1;
        
        try
                                   {
                                       
                                       InetAddress ipAddress = InetAddress.getByName(ipAddr);
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 1");
                                       byte[] msg = new byte[65508];
                                      //ipAddress=InetAddress.getByName("192.168.1.255"); //assumed broadcast address
                                      //ipAddress = InetAddress.getByName(super.buffer[1].toString());
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 2");
                                       
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 3");
                                               //SJServiceRegistry.ConstructBroadcastDiscoveryMessage("AllNodes").toString();
                                       
                                       //MulticastSocket s = new MulticastSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                       
                                       MulticastSocket s = new MulticastSocket(177);
                                       
                                       //s.setLoopbackMode(true);
                                       
                                       
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
                                       
                                       msg = InflaterDeflater.compress(msg);
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 177);
                                       if (infoDebug ==1 ) System.out.println("Sending AdvMessage");
                                       s.setLoopbackMode(true);
                                       s.send(hi);
                                       if (Debug ==1 )System.out.println("AdvMessage has been sent!");
                                       s.close();
                                        
                               }
                               catch (java.net.SocketTimeoutException e)
                               {
                                       System.out.println("Timeout when connecting to ip: " + ipAddr + " port :" + 177);
                               }
                               catch (Exception e)
                               {
                                       System.out.println("Problem when connecting to ip: " + ipAddr + " port :" + 177);
                                       e.printStackTrace();
                               }
        
    }
    
    private boolean SendReqAdvOrNotifMsg(String ipAddr, String message){
        
        int infoDebug=0;
        
        try
                                   {
                                       
                                       InetAddress ipAddress = InetAddress.getByName(ipAddr);
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 1");
                                       byte[] msg = new byte[65508];
                                      
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 2");
                                       
                                       
                                       if (infoDebug ==1 ) System.out.println("BroadcastMessage send stage 3");
                                               //SJServiceRegistry.ConstructBroadcastDiscoveryMessage("AllNodes").toString();
                                       
                                       //MulticastSocket s = new MulticastSocket(SJServiceRegistry.getMessageTransmissionPort(SJServiceRegistry.getMessage("BroadcastDiscoveryMessage")));
                                       
                                       MulticastSocket s = new MulticastSocket(177);
                                       
                                       //s.setLoopbackMode(true);
                                       
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
                                       
                                       msg = InflaterDeflater.compress(msg);
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 177);
                                       if (infoDebug ==1 ) System.out.println("Sending BroadcastDiscoveryMessage");
                                       s.setLoopbackMode(true);
                                       s.send(hi);
                                       
                                       if (infoDebug ==1 )System.out.println("data has been sent!");
                                       s.close();
                                       return true;
                                           
                               }
                               catch (java.net.SocketTimeoutException e)
                               {
                                       System.out.println("Timeout when connecting to ip: " + ipAddr + " port :" + 77);
                                       return false;
                               }
                               catch (java.net.UnknownHostException ex){
                                   System.out.println(" ip: " + ipAddr + " port :" + 77 + "Unavailable");
                                   return false;
                               }
        
                               catch (Exception e)
                               {
                                       System.out.println("Problem when connecting to ip: " + ipAddr + " port :" + 77);
                                       e.printStackTrace();
                                       return false;
                               }
        
    }
    
    private void SendDiscReplyMsg(String message, int respPort){
        
        int infoDebug=0;
        JSONObject js2 = new JSONObject();
        String Addr=null;
        
        try {
            js2 = new JSONObject(new JSONTokener(message));
            Addr = js2.getString("destinationAddress");
           
        } catch (JSONException ex) {
            
            ex.printStackTrace();
        }
        
        
        try
                                {
                                    
                                    byte[] msg = new byte[65508];
                                    byte[] compmsg = new byte[65508];
                                   
                                    InetAddress ipAddress = InetAddress.getByName(Addr);
                                   
                                    DatagramSocket s = new DatagramSocket(respPort);
                                    //s.setLoopbackMode(true);
                                    
                                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                    ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                                  //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
                                    out.writeObject(message); //put service description to be sent to remote devices
                                    out.flush();
                                    msg = byteStream.toByteArray();
                                    out.close();
                                    
                                    compmsg = InflaterDeflater.compress(msg);
                                    
                                    
                                    //DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, respPort);
                                    DatagramPacket hi = new DatagramPacket(compmsg, compmsg.length, ipAddress, respPort);
                                    if (infoDebug ==1 ) System.out.println("Sending data...");
                                    
                                    s.send(hi);
                                    //System.out.println("MsgSenderThread,ServDiscReply msg sent: " +message);
                                    if (infoDebug ==1 ) System.out.println("data has been sent!");
                                    s.close();
                            }
                            catch (java.net.SocketTimeoutException e)
                            {
                                    System.out.println("Timeout when connecting to ip: "  +Addr);
                            }
                            catch (Exception e)
                            {
                                    System.out.println("Problem when connecting to ip: " +Addr);
                                    e.printStackTrace();
                            }
    }
   
    private Hashtable CheckAlmostExpiredAdv(){
        
                        Hashtable answer = SJServiceRegistry.checkServiceExpiryForAdvertiseRequest();

                        if (answer.isEmpty()){
                            return new Hashtable();
                        } else {
                            
                            return answer;
                            
                        }
                     
    }
   
    
    private boolean CheckOwnBeaconAdv(){
        
        int debug=0;
        
        boolean stat;
        
        long time = System.currentTimeMillis()-SOABuffer.getRecordedAdvertisementTimeStamp();
        
        if (time>=(Long.parseLong(SOABuffer.getSOSJRegBeaconPeriodTime()))/3){
                    //list[0] = Boolean.TRUE;
                    stat=true;
                    if (debug==1) System.out.println("AdvertisementChecker: Advertise again");
                   // list[1] = "";
        } else {
                   // list[0]=Boolean.FALSE;
                    if (debug==1) System.out.println("AdvertisementChecker: Advertisement Not yet expired, time:" +time );
                   // list[1] = "";
                    stat=false;
         }
       
            
            return stat;
            
    }
    
    private String getBroadcastAddress(String GatewayAddr, String SubnetAddr){
        
       String[] gtwyaddrSplitted = GatewayAddr.split("\\.");
        String[] subnetmaskSplitted = SubnetAddr.split("\\.");
        
        String[] broadcastaddrString = new String[4];
        String [] flippedsubnetmaskString = new String[4];
        int[] flippedsubnetmasInt = new int[4];
        
        String broadcastAddr="";
        
        for(int i=0;i<gtwyaddrSplitted.length;i++){
            
            int[] subnetaddrint = new int[gtwyaddrSplitted.length];
            int[] broadcastaddrint = new int[subnetmaskSplitted.length];
            
            int[] gtwyint = new int[gtwyaddrSplitted.length];
            int[] sbnetmaskint = new int[subnetmaskSplitted.length];
            
            gtwyint[i] = Integer.parseInt(gtwyaddrSplitted[i]);
            sbnetmaskint[i]= Integer.parseInt(subnetmaskSplitted[i]);
            subnetmaskSplitted[i] = String.format("%8s", Integer.toString(sbnetmaskint[i], 2)).replace(' ', '0');
            
            subnetaddrint[i] = gtwyint[i] & sbnetmaskint[i];
            
            flippedsubnetmaskString[i] = subnetmaskSplitted[i].replaceAll("0", "x").replaceAll("1", "0").replaceAll("x", "1");
            flippedsubnetmasInt[i] = Integer.parseInt(flippedsubnetmaskString[i],2);
            //broadcastaddrint[i] = subnetaddrint[i] | ~sbnetmaskint[i];
            broadcastaddrint[i] = subnetaddrint[i] | flippedsubnetmasInt[i];
           
            broadcastaddrString[i] = Integer.toString(broadcastaddrint[i]);
            
        }
        
        broadcastAddr = broadcastaddrString[0]+"."+broadcastaddrString[1]+"."+broadcastaddrString[2]+"."+broadcastaddrString[3];
        
        return broadcastAddr;
        
    }
   
}  