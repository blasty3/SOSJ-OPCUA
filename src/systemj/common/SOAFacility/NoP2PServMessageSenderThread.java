/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.common.CDLCBuffer;
import systemj.common.InflaterDeflater;
import systemj.common.SJRegistryEntry;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.NetworkConnCheckSimple;
import systemj.common.SOAFacility.Support.SOABuffer;

 
    
    
/**
 *
 * @author Udayanto
 */
public class NoP2PServMessageSenderThread implements Runnable {

    //NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();

    
    @Override
    public void run() {
        
        SJSOAMessage sjdisc = new SJSOAMessage();
        
        //String GtwyAddr = SOABuffer.getGatewayAddr();
        //String SubnetMask = SOABuffer.getSubnetMaskAddr();
        
        //String broadcastAddr = getBroadcastAddress(GtwyAddr,SubnetMask);
        
        //String broadcastAddr = "192.168.1.255";
        
        System.out.println("NoP2PServMessageSender thread started");
        
        NetworkConnCheckSimple netcheck = new NetworkConnCheckSimple();
        
        long lastReqAdvTransmittedTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        
        while (true) {
            
           //System.out.println("MessageSender thread executed");
            
      //     if (SJServiceRegistry.getParsingStatus()) {

                String connStat = netcheck.CheckNetworkConn(SOABuffer.getGatewayAddr(), 2500);
                
                //System.out.println("MessageSender, ConnectionStat: " +connStat);
                
                    //need to send disc, disc reply, req adv, and adv message
                    
                    if (connStat.equalsIgnoreCase("Connected")){

                        // disc and adv
                        
                        //System.out.println("Adv  status: " +SOABuffer.getIsInitAdvDone());
                        
                     //   if (!SOABuffer.getIsInitDiscDone() || !SOABuffer.getIsInitAdvDone()){
                        
                            //System.out.println("Adv and Disc status: " +SOABuffer.getIsInitDiscDone()+ " and " +SOABuffer.getIsInitDiscDone());
                        
                            

                            //init adv transmission
                            
                        /*
                            if (!SOABuffer.getIsInitAdvDone()){
                                
                                 //System.out.println("performing init Adv");
                                
                               //if(SJServiceRegistry.obtainInternalRegistryVisibleProviderOnly().length()>0){
                                   String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                    
                                    //String AdvMsg = sjdisc.ConstructReAdvertisementMessageOfInclServ(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()),SOABuffer.getAdvModBuffer(),LocalSSName);
                                    
                                    //String AdvMsg = sjdisc.ConstructServToRegReAdvertisementMessageOfInclServ(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()), SOABuffer.getAdvModBuffer(), LocalSSName);
                                    
                                    
                                    
                                    JSONObject registryHash = SJRegistryEntry.GetRegistryFromEntry();
                                    
                                    //System.out.println("SJRegEntry :" +registryHash);
                                    
                                    
                                    
                                    if(registryHash.length()>0){
                                        
                                         //boolean toNotifySS = SOABuffer.GetRegNotify();
                                        
                                        Enumeration keyAvailReg = registryHash.keys();
                                    
                                    
                                            while(keyAvailReg.hasMoreElements()){
                                                String regID = keyAvailReg.nextElement().toString();
                                                String LocalSSAddr = SJSSCDSignalChannelMap.GetLocalSSAddr();

                                                String AdvMsg = sjdisc.ConstructServToRegReAdvertisementMessageOfInclServ(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()), true,LocalSSName, LocalSSAddr,regID);
                                                
                                                try {
                                                    String regAddr = registryHash.getString(regID);
                                                    
                                                    if(regAddr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr()) || regAddr.equalsIgnoreCase("127.0.0.1") || regAddr.equalsIgnoreCase("localhost")){
                                                        SendAdvMsg("224.0.0.100", AdvMsg);
                                                    } else {
                                                        SendAdvMsg(regAddr, AdvMsg);
                                                    }
                                                    
                                                    
                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }

                                              }
                                        SJServiceRegistry.RecordAdvertisementTimeStamp(); 
                                        
                                        if(SOABuffer.getAdvTransmissionRequest()){
                                            SOABuffer.SetAdvTransmissionRequest(false);
                                        }
                                        
                                        //if(SOABuffer.GetRegNotify()){
                                        //    SOABuffer.SetRegNotifySS(false);
                                        //}
                                         
                                    // }
                                
                                        
                                        SOABuffer.setIsInitAdvDone(true);
                                        
                                    }
                               //}
              
                                
                            } 
*/
                            
                            //sending req adv to almost expired reg
                            
                            Hashtable allRegExpiryDet = SJRegistryEntry.GetAllRegistryExpiryDet();
                            
                            Enumeration keysAllRegDet = allRegExpiryDet.keys();
                            currentTime = System.currentTimeMillis();
                            
                            if(currentTime-lastReqAdvTransmittedTime>400){
                                
                                while(keysAllRegDet.hasMoreElements()){
                                
                                    String regID = keysAllRegDet.nextElement().toString();

                                    Hashtable regExpiryDet = (Hashtable)allRegExpiryDet.get(regID);

                                    long logTime = Long.parseLong((String)regExpiryDet.get("loginTime"));
                                    long expiry = Long.parseLong((String)regExpiryDet.get("expiry"));
                                    String regAddr = (String)regExpiryDet.get("regAddr");

                                    long timeRem = System.currentTimeMillis()-logTime;

                                    if(timeRem>=0.8*expiry){

                                        String regReqAdv = sjdisc.ConstructRegRequestAdvertisementMessage(regAddr,SJSSCDSignalChannelMap.GetLocalSSAddr());

                                        if(regAddr.equals(SJSSCDSignalChannelMap.GetLocalSSAddr()) || regAddr.equalsIgnoreCase("127.0.0.1") || regAddr.equalsIgnoreCase("localhost")){
                                            SendReqAdvMsg("224.0.0.100", regReqAdv);
                                        } else {
                                            SendReqAdvMsg(regAddr, regReqAdv);
                                        }

                                    }
                                
                                }
                                
                                lastReqAdvTransmittedTime = System.currentTimeMillis();
                                
                            }
                            
                            
                            
                            // end
                            
                            //adv transmission from reg req adv
                        
                            Vector allContentReqAdvBuffer = SOABuffer.GetRegToProvReqAdv();
                            
                            if(allContentReqAdvBuffer.size()>0){
                                
                                for (int i=0;i<allContentReqAdvBuffer.size();i++){
                                    
                                     JSONObject jsReqAdvMsg = (JSONObject) allContentReqAdvBuffer.get(i);
                                     
                                     //int j = 0 ;
                                
                                     /*
                                     JSONObject jsAllServAvail = SOABuffer.getAdvModBuffer();
                                
                                     Enumeration keysAvail = jsAllServAvail.keys();
                                
                                     while (keysAvail.hasMoreElements()){
                                        String keyServ = keysAvail.nextElement().toString();
                                    
                                        try {
                                            String avail = jsAllServAvail.getString(keyServ);
                                        
                                            if (avail.equalsIgnoreCase("available") || avail.equalsIgnoreCase("visible")){
                                                j++;
                                            }
                                        
                                        
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    
                                    }
                                     */
                                    
                                     //if(j>0){
                                         
                                     //if(SJServiceRegistry.obtainInternalRegistryVisibleProviderOnly().length()>0){
                                         
                                         JSONObject generatedRespAdvMsgJSON = ProcessMessageInJSON(jsReqAdvMsg.toString());     
                            
                                            String destAddr=null;
                                            try {
                                            
                                                
                                                
                                                destAddr = jsReqAdvMsg.getString("regAddr");
                                                SendAdvMsg(destAddr, generatedRespAdvMsgJSON.toString());
                    //String respAdvMsg = sjdisc.ConstructReAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()));
                                            } catch (JSONException ex) {
                                            System.out.println("cannot find destination Address in ProcessMessageInJSON");
                                        }
                                         
                                     //}
                                     
                                           
                                         
                                       //}
                                     
                                    
                
                                       // if (destAddr!=null){
                                            
                                        //}
                                     
                                }
                                
                            }
                            
                        /* 
                        JSONObject jsReqAdvMsg = SOABuffer.getReqAdvBuffer();   
                        
                        if (!jsReqAdvMsg.isEmpty()){
                            
                            JSONObject generatedRespAdvMsgJSON = ProcessMessageInJSON(jsReqAdvMsg.toString(), SJServiceRegistry.obtainInternalRegistryVisibleProviderOnly().toString());     
                            
                            String destAddr=null;
                            try {
                            
                                destAddr = generatedRespAdvMsgJSON.getString("destIPAddr");
                    
                    //String respAdvMsg = sjdisc.ConstructReAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()));
                            } catch (JSONException ex) {
                                System.out.println("cannot find destination Address in ProcessMessageInJSON");
                            } 
                
                            if (destAddr!=null){
                                SendAdvMsg(destAddr, generatedRespAdvMsgJSON.toString());
                            }
                            
                        }
                        */
          
                            
                 //   }
                    //    else
                        
                       // {
 
                            
                        
                                
                                
                               // String expiredServAddr = CheckAlmostExpiredAdv();
                            
                            //req adv transmission

                        /*        
                                
                        currentTime = System.currentTimeMillis();
                        
                        if (currentTime-lastReqAdvTransmittedTime>500){
                            
                            String expiredServAddr = CheckAlmostExpiredAdv();
                
                            if (expiredServAddr.equalsIgnoreCase("nothing")){
                    
                            } else {
                    
                                String ReqAdvMsg = sjdisc.ConstructRequestAdvertisementMessage(expiredServAddr);
                    
                                SendReqAdvMsg(expiredServAddr,ReqAdvMsg);
                                lastReqAdvTransmittedTime = System.currentTimeMillis();
                            }
                            
                        }
                                
                                
                                
                            // end req adv tranmission
                                
                                //Disc transmission

                        //if (needDisc || !jsDiscReq.isEmpty()){
                        
                                //28 Dec 2014 - Discovery should be application controlled
                                
                                /*
                                
                                boolean needDisc = SJServiceRegistry.CheckUnavailableService();
                                
                                if (needDisc){
                                    JSONObject jsServList = SJServiceRegistry.getUnavailableServiceReturnTypeList();
                                    String DiscMsg = sjdisc.ConstructDiscoveryMessageOfExpServType(jsServList);
                                 
                                    SendDiscMsg(broadcastAddr, DiscMsg);
                                 
                                } 
                                */
                            
                            /*
                                JSONObject jsDiscReq = SOABuffer.getDiscReqBuffer();   
                                    
                                if (!jsDiscReq.isEmpty()){

                                    try {
                                        String servType = jsDiscReq.getString("servName");
                                    
                                        JSONObject js1 = new JSONObject();
                                    
                                        String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                        
                                        js1.put("1", servType);
                                    
                                        String DiscMsg = sjdisc.ConstructDiscoveryMessageOfExpServType(js1,LocalSSName);
                                        SendDiscMsg(broadcastAddr, DiscMsg);
                                    } catch (JSONException ex) {
                                        System.out.println("MessageSenderThread JSONException: " +ex.getMessage());
                                    }

                                
                            } 
                                */
                                /*
                                else if (needDisc && !jsDiscReq.isEmpty() ){
                                
                                    JSONObject jsServList = SJServiceRegistry.getUnavailableServiceReturnTypeList();
                                    String DiscMsg = sjdisc.ConstructDiscoveryMessageOfExpServType(jsServList);
                                 
                                    SendDiscMsg(broadcastAddr, DiscMsg);
                                    
                                    
                                    try {
                                        String servType = jsDiscReq.getString("servName");
                                    
                                        JSONObject js1 = new JSONObject();
                                    
                                        js1.put("1", servType);
                                    
                                        String DiscMsg2 = sjdisc.ConstructDiscoveryMessageOfExpServType(js1);
                                        SendDiscMsg(broadcastAddr, DiscMsg2);
                                    } catch (JSONException ex) {
                                        System.out.println("MessageSenderThread JSONException: " +ex.getMessage());
                                    }
                                
                            }
                             */   
                                //regular adv
                            
                            boolean startRegAdv = CheckOwnAdv();
                            boolean startAdv = SOABuffer.getAdvTransmissionRequest();

                                int j = 0 ;
                                
                                //JSONObject jsAllServAvail = SOABuffer.getAdvModBuffer();
                                
                                //Enumeration keysAvail = jsAllServAvail.keys();
                                
                                /*
                                while (keysAvail.hasMoreElements()){
                                    String keyServ = keysAvail.nextElement().toString();
                                    
                                    try {
                                        String avail = jsAllServAvail.getString(keyServ);
                                        
                                        if (avail.equalsIgnoreCase("available") || avail.equalsIgnoreCase("visible")){
                                            j++;
                                        }
                                        
                                        
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    
                                }
                                */
                            
                            
                            if ((startRegAdv || startAdv)){
                                
                                String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                String LocalSSAddr = SJSSCDSignalChannelMap.GetLocalSSAddr();
                                
                                
                                //String AdvMsg = sjdisc.ConstructServToRegReAdvertisementMessageOfInclServ(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()), SOABuffer.getAdvModBuffer(), LocalSSName);
                                //String AdvMsg = sjdisc.ConstructReAdvertisementMessageOfInclServ(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()),SOABuffer.getAllAdvVisib(),LocalSSName);
                                
                                //if(SJServiceRegistry.obtainInternalRegistryVisibleProviderOnly().length()>0){
                                    
                                    JSONObject jsAllAvailReg = SJRegistryEntry.GetRegistryFromEntry();
                                
                                    if(jsAllAvailReg.length()>0){

                                        Enumeration keysAvailReg = jsAllAvailReg.keys();

                                        boolean toNotifySS = SOABuffer.GetRegNotify();
                                            
                                        boolean changedCDStat = SOABuffer.GetNotifyChangedCDStat();
                                        
                                        while(keysAvailReg.hasMoreElements()){
                                            String regID = keysAvailReg.nextElement().toString();

                                            String AdvMsg = sjdisc.ConstructServToRegReAdvertisementMessageOfInclServ(Long.toString(SJSSCDSignalChannelMap.GetSSExpiryTime()), toNotifySS, changedCDStat,LocalSSName, LocalSSAddr,regID);
                                            
                                            //System.out.println("AdvMsg: " +AdvMsg);
                                            try {
                                                String regAddr = jsAllAvailReg.getString(regID);
                                                
                                                //System.out.println("NoP2PServMsgSender, Adv Msg: " +AdvMsg+ " regAddr: " +regAddr);
                                                
                                                if(regAddr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr()) || regAddr.equalsIgnoreCase("127.0.0.1") || regAddr.equalsIgnoreCase("localhost")){
                                                        SendAdvMsg("224.0.0.100", AdvMsg);
                                                    } else {
                                                        SendAdvMsg(regAddr, AdvMsg);
                                                    }
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }

                                    }

                                    SOABuffer.RecordAdvertisementTimeStamp();
                                    SOABuffer.SetAdvTransmissionRequest(false);
                                    SOABuffer.SetRegNotifySS(false);
                                    //SOABuffer.SetNotifyChangedCDStat(false);
                                    }
                                    
                                //}
                                
                                //System.out.println("MessageSenderThread, AdvMsg: " +AdvMsg);
                                //System.out.println("MessageSenderThread, VisibAdv: " +SOABuffer.getAllAdvVisib().toString());
                            }
    
                            //end regular adv
    
                    //}       
                        // adv transmission responding req adv
                        
              //      jsReqAdvMsg = SOABuffer.getReqAdvBuffer();     
                    
              //      if (!jsReqAdvMsg.isEmpty()){
              //          JSONObject generatedRespAdvMsgJSON = ProcessMessageInJSON(jsReqAdvMsg.toString(), SJServiceRegistry.obtainInternalRegistryVisibleProviderOnly().toString());     
                            
              //          String destAddr=null;
              //          try {
              //              destAddr = generatedRespAdvMsgJSON.getString("destIPAddr");
                    
                    //String respAdvMsg = sjdisc.ConstructReAdvertisementMessage(Long.toString(SJServiceRegistry.getOwnAdvertisementTimeLimit()));
             //           } catch (JSONException ex) {
             //               System.out.println("cannot find destination Address in ProcessMessageInJSON" +ex.getMessage());
             //           } 
                
              //          if (destAddr!=null){
             //               SendAdvMsg(destAddr, generatedRespAdvMsgJSON.toString());
             //           }

             //         }
                    
                     // end adv transmission responding req adv

                    //req adv check and transmission
                            
                            /*
                            
                            Hashtable expiredServAddrs = CheckAlmostExpiredAdv();
                
                            if (!expiredServAddrs.isEmpty()){
                                
                                    Enumeration keysExpServAddrs = expiredServAddrs.keys();
                                    
                                    currentTime = System.currentTimeMillis();
                                    
                                    if(currentTime-lastReqAdvTransmittedTime>400){
                                        
                                        while(keysExpServAddrs.hasMoreElements()){
                                            String index = keysExpServAddrs.nextElement().toString();
                                        
                                            String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                            
                                            String addr = (String) expiredServAddrs.get(index);
                                        
                                            String ReqAdvMsg = sjdisc.ConstructRequestAdvertisementMessage(addr, LocalSSName);
                    
                                            boolean stat = SendReqAdvMsg(addr,ReqAdvMsg);
                                            
                                            if(!stat){
                                                
                                            }
                                            
                                            //if not available, then remove from registry
                                        
                                        }
                               
                                    lastReqAdvTransmittedTime = System.currentTimeMillis();
                                    }
                                    
                            }
                            */
                            
                        /*
                        String answer = SJServiceRegistry.checkServiceExpiryForAdvertiseRequest();

                        if (!answer.equalsIgnoreCase("nothing")){

                            String message2 = sjdisc.ConstructRequestAdvertisementMessage(answer);
                            SendReqAdvMsg(answer, message2);
                        } 
                            */
                         // end req adv check and transmission

                      }
                   
                   
                
                // CD migration... for all conditions whether consumer only, prov only, or both
                
                /*
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
                */
     
       //  }     
            
           
       }
    }
    
    private JSONObject ProcessMessageInJSON(String SJMessage)
    {
        //JSONObject serviceList = new JSONObject();
        JSONObject processedSJMessage = new JSONObject();
        SJSOAMessage sjdisc = new SJSOAMessage();
        int i = 1;
        try {
            //JSONObject jsAllIntServ = SJServiceRegistry.obtainInternalRegistry();
            //JSONObject jsAllIntServ = new JSONObject(new JSONTokener(OfferedServices));
            JSONObject jsMsg = new JSONObject(new JSONTokener(SJMessage));
            
            /*
            else if (jsMsg.getString("discMsgType").equalsIgnoreCase("discReply")){
                //serviceList = SJServiceRegistry.obtainInternalRegistry();
                if (!jsAllIntServ.toString().equalsIgnoreCase("{}"))
                {
                    processedSJMessage = new JSONObject(new JSONTokener(SJMessage));
                    processedSJMessage.put("serviceList",jsAllIntServ);
                    processedSJMessage.remove("expecServiceList");
                }
            } 
            */
            if (jsMsg.getString("MsgType").equalsIgnoreCase("RequestForAdvertisement")){
                
                //processedSJMessage = new JSONObject(new JSONTokener(SJMessage));

               // Enumeration keysjsAllIntServ = jsAllIntServ.keys();
                
                //while (keysjsAllIntServ.hasMoreElements()){
                    
                    //Object keyAllIntServ = keysjsAllIntServ.nextElement();
                    
                    //JSONObject jsIndivIntServ = jsAllIntServ.getJSONObject(keyAllIntServ.toString());
                    
                    //JSONObject jsExpectedServiceType = jsMsg.getJSONObject("expServiceType");
                    
                   // Enumeration keysjsExpectedServiceType = jsExpectedServiceType.keys();
                    
                   // while (keysjsExpectedServiceType.hasMoreElements()){
                        //Object keyExpectedServiceType = keysjsExpectedServiceType.nextElement();
                        
                       // if (jsIndivIntServ.getString("serviceType").equalsIgnoreCase(jsExpectedServiceType.getString(keyExpectedServiceType.toString()))){
                            //serviceList.put(jsIndivIntServ.getString("serviceName"),jsIndivIntServ);
                            //i++;
                       // }
                    //} 
                //}
                
                 String LocalSSName = SJSSCDSignalChannelMap.getLocalSSName();
                
                //String destIPAddr = jsMsg.getString("sourceAddress");
                String regID = jsMsg.getString("regID");
                
                
                processedSJMessage = sjdisc.ConstructNoP2PProvToRegResponseReAdvertisementMessageInJSON(Long.toString(SJSSCDSignalChannelMap.GetSSExpiryTime()), regID, SJSSCDSignalChannelMap.GetLocalSSAddr(),false, LocalSSName);
   
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            //System.out.println("Error SJMessageHandler Message Process:" +ex.printStackTrace());
            System.exit(1);
        }
        
        return processedSJMessage;
 
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
                                       
                                       //compress message, best compression
                                       
                                       msg = InflaterDeflater.compress(msg);
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 177);
                                       if (infoDebug ==1 ) System.out.println("Sending AdvMessage");
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
    
    
    private boolean SendReqAdvMsg(String ipAddr, String message){
        
        int infoDebug=0;
        
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
                                       
                                       //compress message
                                       
                                       msg = InflaterDeflater.compress(msg);
                                       
                                       DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 177);
                                       if (infoDebug ==1 ) System.out.println("Sending BroadcastDiscoveryMessage");
                                       s.send(hi);
                                       //System.out.println("NoP2PServMsgSender, sending reqRegReAdv: " +message);
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
    
   
    
    
    
    /*
    private Vector getAlmostExpiredAdvServNames(){
        
        Vector allNames = new Vector();
        
        try {
            String answer = SJServiceRegistry.checkServiceExpiryForAdvertiseRequest();
            JSONObject jsCurrReg = SJServiceRegistry.obtainCurrentRegistry();
            
            JSONObject almostExpNode = jsCurrReg.getJSONObject(answer);
            
            Enumeration allServInd = almostExpNode.keys();
            
            while (allServInd.hasMoreElements()){
                
                String oneServInd = allServInd.nextElement().toString();
                
                JSONObject oneServ = almostExpNode.getJSONObject(oneServInd);
                
                String servName = oneServ.getString("serviceName");
                
                allNames.addElement(servName);
                
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(MessageSenderThread.class.getName()).log(Level.SEVERE, null, ex);
        }
                        
        return allNames;             
                     
    }
    */
    
    
    private boolean CheckOwnAdv(){
        
        int debug=0;
        
        boolean stat;
        
            if (SJServiceRegistry.getParsingStatus()){
                
                //if (SJServiceRegistry.HasServiceProvider()){
                    
                    long time = System.currentTimeMillis()-SOABuffer.getRecordedAdvertisementTimeStamp();
                
                if (time>=0.3*SJServiceRegistry.getOwnAdvertisementTimeLimit()){
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
                    
                    
               // } else {
               //     stat=false;
                   
               // }
                
                
                
            } else {
                //list[0]=Boolean.FALSE;
               // list[1] ="";
                stat=false;
            }
            return stat;
            
    }
    
    
    
    /*
    private InetAddress getBroadcastAddress(String Addr){
            
        InetAddress broadcastAddr = null;
             try {
            // TODO code application logic here
                    Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback())
                        continue;    // Don't want to broadcast to the loopback interface
                        for (InterfaceAddress interfaceAddress :
                            networkInterface.getInterfaceAddresses()) {
                            String addr = interfaceAddress.getAddress().getHostAddress();
                            
                            if(addr.equals(Addr)){
                                 InetAddress broadcast = interfaceAddress.getBroadcast();
                                 
                                  if (broadcast == null) {
                                    continue;
                                  } else {
                                      broadcastAddr = broadcast;
                                  }
                                 
                            }
                            
                           
                            
                            
                          //  if (broadcast == null) {
                         //           continue;
                          //      }
                          //  if (broadcast.toString().contains("192.168.1")) {
                          //          broadcastAddr = broadcast;
                         //       }
                           
                        // Use the address
                         }
                     }
               } catch (SocketException ex) {
                System.out.println("Cannot find address: " +ex.getMessage());
                
            }
             return broadcastAddr;
        }
    */
    
    
    
    
    
    private InetAddress getLocalHostLANAddress() throws UnknownHostException {
    try {
        InetAddress candidateAddress = null;
        // Iterate all NICs (network interface cards)...
        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            // Iterate all IP addresses assigned to each card...
            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                if (!inetAddr.isLoopbackAddress()) {

                    if (inetAddr.isSiteLocalAddress()) {
                        // Found non-loopback site-local address. Return it immediately...
                        if (!inetAddr.getHostAddress().equalsIgnoreCase("192.168.7.2")){
                            return inetAddr;
                        } 
                       
                    }
                    else if (candidateAddress == null) {
                        // Found non-loopback address, but not necessarily site-local.
                        // Store it as a candidate to be returned if site-local address is not subsequently found...
                        candidateAddress = inetAddr;
                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                        // only the first. For subsequent iterations, candidate will be non-null.
                    }
                }
            }
        }
        if (candidateAddress != null) {
            // We did not find a site-local address, but we found some other non-loopback address.
            // Server might have a non-site-local address assigned to its NIC (or it might be running
            // IPv6 which deprecates the "site-local" concept).
            // Return this non-loopback candidate address...
            return candidateAddress;
        }
        // At this point, we did not find a non-loopback address.
        // Fall back to returning whatever InetAddress.getLocalHost() returns...
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }
        return jdkSuppliedAddress;
    }
    catch (Exception e) {
        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
        unknownHostException.initCause(e);
        throw unknownHostException;
    }
}
    
 
}  
 


