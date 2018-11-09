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
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.channels.Channels;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.CyclicScheduler;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.SchedulersBuffer;
//import systemj.common.SignalObjBuffer;
import systemj.interfaces.GenericSignalReceiver;
import systemj.interfaces.GenericSignalSender;
import systemj.interfaces.Scheduler;
import systemj.lib.AChannel;
import systemj.lib.Signal;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;

/**
 *
 * @author Udayanto
 */
public class ClockDomainLifeCycleSignalImpl {
    
     public Vector ReconfigInvServInChannel(String inchanName, String inchanCDName, String partnerCDName, String partnerChanName, String partnerCDSSLoc, Scheduler sc, InterfaceManager im){
         
         Vector vec = new Vector();      

         
         try{
             
             String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
         
             JSONObject jsAllMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
             JSONObject jsLocalCDMap = jsAllMap.getJSONObject(localSSName);

             
             Hashtable channels = im.getAllChannelInstances();

             if(sc.SchedulerHasCD(inchanCDName)){

                 ClockDomain cdInChan = sc.getClockDomain(inchanCDName);
                 
                 Field f = cdInChan.getClass().getField(inchanName+"_in");
                   input_Channel inchan = (input_Channel)f.get(cdInChan);
                   
                   im.addCDLocation(partnerCDSSLoc, partnerCDName);
                   
                   inchan.setDistributed();
                   inchan.setInterfaceManager(im);
                   channels.put(inchan.Name, inchan);
                   
                   sc.updateClockDomain(cdInChan, inchanName);

             } else if (CDObjectsBuffer.CDObjBufferHas(inchanCDName)){

                 ClockDomain cdInChan = CDObjectsBuffer.GetCDInstancesFromBuffer(inchanCDName);
                 
                 Field f = cdInChan.getClass().getField(inchanName+"_in");
                   input_Channel inchan = (input_Channel)f.get(cdInChan);
                   
                   im.addCDLocation(partnerCDSSLoc, partnerCDName);
                   
                   inchan.setDistributed();
                   inchan.setInterfaceManager(im);
                   channels.put(inchan.Name, inchan);
                   
                   CDObjectsBuffer.AddCDInstancesToBuffer(inchanCDName, cdInChan);

             }
             
             JSONObject jsCD = jsLocalCDMap.getJSONObject(inchanCDName);
             JSONObject jsAllChan = jsCD.getJSONObject("SChannels");
             JSONObject jsInChans = jsAllChan.getJSONObject("inputs");
             JSONObject jsInChan = jsInChans.getJSONObject(inchanName);
             String newOrig = partnerCDName+"."+partnerChanName;
             jsInChan.put("From", newOrig);
             jsInChans.put(inchanName, jsInChan);
             jsAllChan.put("inputs", jsInChans);
             jsCD.put("SChannels", jsAllChan);
             jsLocalCDMap.put(inchanCDName, jsCD);
             jsAllMap.put(localSSName, jsLocalCDMap);
             
             SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsAllMap);
             
             im.setChannelInstances(channels);
             
             
             
         } catch (JSONException jex){
             jex.printStackTrace();
         } catch (Exception ex){
             ex.printStackTrace();
         }
         
         vec.addElement(im);
         vec.addElement(sc);
         
         return vec;
         
     }
    
    public Vector ReconfigInvServLocalChannel(String CDSenderName, String ChanSenderName, String CDReceiverName, String ChanReceiverName, Scheduler sc, InterfaceManager im){
        
      
      Vector vec = new Vector();      
      
      Hashtable channels = im.getAllChannelInstances();
      
      ClockDomain cdSender = sc.getClockDomain(CDSenderName);
     
      output_Channel ochan;
      
        try {
            
            
            Field f = cdSender.getClass().getField(CDSenderName+"_o");
            ochan = (output_Channel)f.get(cdSender);
            
            String partnerInChanDot = CDReceiverName+"."+ChanReceiverName+"_in";
            
            if(!partnerInChanDot.equals(ochan.PartnerName)){
                
                if(sc.SchedulerHasCD(CDReceiverName)){
           
                        ClockDomain cdReceiver = sc.getClockDomain(CDReceiverName);
           
                        input_Channel inchan;
           
                        Field f2 = cdReceiver.getClass().getField(CDReceiverName+"_in");
               
                        inchan = (input_Channel)f2.get(cdReceiver);
                        
                        //if(inchan.get_r_r()==0 && inchan.get_r_s()==0){
                        if(inchan.get_r_s()==0 && !inchan.getSubscribedStat()){
                            String SSName = SJSSCDSignalChannelMap.getLocalSSName();
                            
                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                            
                            JSONObject jsLocalCDs = jsCurrMap.getJSONObject(SSName);
                            
                            JSONObject jsCDSenderConf = jsLocalCDs.getJSONObject(CDSenderName);
                            
                            JSONObject jsCDReceiverConf = jsLocalCDs.getJSONObject(CDReceiverName);
                            
                            JSONObject jsChanCDSenderConf = jsCDSenderConf.getJSONObject("SChannels");
                            
                            JSONObject jsChanCDReceiverConf = jsCDReceiverConf.getJSONObject("SChannels");
                            
                            JSONObject jsOChanCDSenderConf = jsChanCDSenderConf.getJSONObject("outputs");
                            
                            JSONObject jsInChanCDReceiverConf = jsChanCDReceiverConf.getJSONObject("inputs");
                           
                            inchan.PartnerName = CDSenderName+"."+ChanSenderName+"_o";
                            
                            ochan.PartnerName = CDReceiverName+"."+ChanReceiverName+"_in";
                            
                            jsOChanCDSenderConf.put("To", CDReceiverName+"."+ChanReceiverName);
                            jsInChanCDReceiverConf.put("From",CDSenderName+"."+ChanSenderName);
                            
                            jsChanCDSenderConf.put("outputs", jsOChanCDSenderConf);
                            jsChanCDReceiverConf.put("inputs", jsInChanCDReceiverConf);
                            
                            jsCDSenderConf.put("SChannels", jsChanCDSenderConf);
                            jsCDReceiverConf.put("SChannels", jsChanCDReceiverConf);
                            
                            jsLocalCDs.put(CDSenderName, jsCDSenderConf);
                            jsLocalCDs.put(CDReceiverName, jsCDReceiverConf);
                            
                            jsCurrMap.put(SSName, jsLocalCDs);
                            
                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCurrMap);
                            
                            if(!ochan.IsChannelLocal()){
                                ochan.setLocal();
                            } 
                               
                            if(!inchan.IsChannelLocal()){
                                inchan.setLocal();
                            }
                            
                            inchan.setSubscribedStat();
                          
                            
                            inchan.set_partner_smp(ochan);
                            ochan.set_partner_smp(inchan);
                            
                            channels.put(inchan.Name,inchan);
                            channels.put(ochan.Name, ochan);
                            
                            CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, true);
                            
                            sc.updateClockDomain(cdSender, CDSenderName);
                            sc.updateClockDomain(cdReceiver, CDReceiverName);
                            
                            im.setChannelInstances(channels);
                            
                        } else {
                            CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, false);
                        }
           
           
                } else if (CDObjectsBuffer.CDObjBufferHas(CDReceiverName)){

                    ClockDomain cdReceiver = CDObjectsBuffer.GetCDInstancesFromBuffer(CDReceiverName);

                    input_Channel inchan;

                        Field f2 = cdReceiver.getClass().getField(CDReceiverName+"_in");

                        inchan = (input_Channel)f2.get(cdReceiver);

                        
                        //if(inchan.get_r_r()==0 && inchan.get_r_s()==0){
                        if( inchan.get_r_s()==0){   
                            String SSName = SJSSCDSignalChannelMap.getLocalSSName();
                            
                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                            
                            JSONObject jsLocalCDs = jsCurrMap.getJSONObject(SSName);
                            
                            JSONObject jsCDSenderConf = jsLocalCDs.getJSONObject(CDSenderName);
                            
                            JSONObject jsCDReceiverConf = jsLocalCDs.getJSONObject(CDReceiverName);
                            
                            JSONObject jsChanCDSenderConf = jsCDSenderConf.getJSONObject("SChannels");
                            
                            JSONObject jsChanCDReceiverConf = jsCDReceiverConf.getJSONObject("SChannels");
                            
                            JSONObject jsOChanCDSenderConf = jsChanCDSenderConf.getJSONObject("outputs");
                            
                            JSONObject jsInChanCDReceiverConf = jsChanCDReceiverConf.getJSONObject("inputs");
                            
                            inchan.PartnerName = CDSenderName+"."+ChanSenderName+"_o";
                            
                            ochan.PartnerName = CDReceiverName+"."+ChanReceiverName+"_in";
                            
                            inchan.setSubscribedStat();
                            
                            inchan.set_partner_smp(ochan);
                            ochan.set_partner_smp(inchan);
                            
                            channels.put(inchan.Name,inchan);
                            channels.put(ochan.Name, ochan);
                            
                            jsOChanCDSenderConf.put("To", CDReceiverName+"."+ChanReceiverName);
                            jsInChanCDReceiverConf.put("From",CDSenderName+"."+ChanSenderName);
                            
                            jsChanCDSenderConf.put("outputs", jsOChanCDSenderConf);
                            jsChanCDReceiverConf.put("inputs", jsInChanCDReceiverConf);
                            
                            jsCDSenderConf.put("SChannels", jsChanCDSenderConf);
                            jsCDReceiverConf.put("SChannels", jsChanCDReceiverConf);
                            
                            jsLocalCDs.put(CDSenderName, jsCDSenderConf);
                            jsLocalCDs.put(CDReceiverName, jsCDReceiverConf);
                            
                            jsCurrMap.put(SSName, jsLocalCDs);
                            
                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCurrMap);
                            
                            CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, true);
                            
                            sc.updateClockDomain(cdSender, CDSenderName);
                            CDObjectsBuffer.AddCDInstancesToBuffer(CDReceiverName, cdReceiver);
                            
                            im.setChannelInstances(channels);
                            
                        } else {
                            CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, false);
                        }

                }
                
            } else {
                CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, true);
            } 
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        vec.addElement(im);
        vec.addElement(sc);
        
        return vec;
        
    }
    
    public Vector ReconfigInvServChannel(String CDSenderName, String ChanSenderName, String CDReceiverName, String ChanReceiverName, Scheduler sc, InterfaceManager im){
        
      
      InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
        
      //SJSOAMessage sjsoa = new SJSOAMessage();
        
      Vector vec = new Vector();      
      
      Hashtable channels = im.getAllChannelInstances();
      
      ClockDomain cdSender = sc.getClockDomain(CDSenderName);
     
      output_Channel ochan;
      
        try {
            
            
            Field f = cdSender.getClass().getField(CDSenderName+"_o");
            ochan = (output_Channel)f.get(cdSender);
            
            String partnerInChanDot = CDReceiverName+"."+ChanReceiverName+"_in";
            
            if(!partnerInChanDot.equals(ochan.PartnerName)){
                
                String ssDest = im.getCDLocation(CDReceiverName);
           
                       String destAddr = RegAllSSAddr.getSSAddrOfSSName(ssDest);
                       
                       String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
                       
                       //String localSSAddr = RegAllSSAddr.getSSAddrOfSSName(localSSName);
                       
                       //create link if needed
                                                
                                                String SrcAddr = SJSSCDSignalChannelMap.GetLocalSSAddr();
                                           
                                                // find local SS port

                                                JSONObject jsLocalPortPair = TCPIPLinkRegistry.GetSSAndPortPair();

                                                String LocalPortNum="";
                                                boolean LocalPortExist = false;
                                                boolean remotePortExist = false;

                                                try{

                                                    Enumeration keysLoc = jsLocalPortPair.keys();

                                                    while(keysLoc.hasMoreElements()){
                                                        String PortNumStr = keysLoc.nextElement().toString();

                                                        if(jsLocalPortPair.getString(PortNumStr).equals(SJSSCDSignalChannelMap.getLocalSSName())){

                                                            LocalPortExist=true;
                                                            LocalPortNum = PortNumStr;
                                               
                                                        }

                                                    }

                                                } catch (JSONException jex){
                                                    jex.printStackTrace();
                                                }
                                                
                                                //find DestLink, if any
                                                
                                                String RemotePortNum = "";
                                                
                                                try{

                                                    Enumeration keysLoc = jsLocalPortPair.keys();

                                                    while(keysLoc.hasMoreElements()){
                                                        String PortNumStr = keysLoc.nextElement().toString();

                                                        if(jsLocalPortPair.getString(PortNumStr).equals(ssDest)){

                                                            RemotePortNum = PortNumStr;
                                                            remotePortExist = true;

                                                        }

                                                    }

                                                } catch (JSONException jex){
                                                    jex.printStackTrace();
                                                }
                                                
                                                if(!(LocalPortExist && remotePortExist)){
                                                    //need to negotiate both link establishment and which physical interface
                                                    
                                                    String SSName = ssDest;

                                                                //InterfaceManager imA = getInterfaceManager();
                                                    //System.out.println("SJProgram, Trying to contact SS with name: " +SSName);

                                                            Interconnection ic = imMod.getInterconnection();

                                                            Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                                            if(availRemoteLink.size()==0){

                                                                //String destAddr = SJServiceRegistry.getAddrOfSS(SSName);

                                                                LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();

                                                                int r;

                                                                for(r=0;r<5;r++){

                                                                    String linkCreationResp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destAddr);

                                                                        if(linkCreationResp.equals("OK")){

                                                                            if(LocalPortExist){

                                                                                imMod = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, imMod, LocalPortNum);
                                                                                
                                                                                break;

                                                                            } else {
                                                                                 imMod = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, imMod);
                                                                                 
                                                                                 break;
                                                                            }

                                                                        } 

                                                                }

                                                                


                                                                // update SS to Contact

                                                                //TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                                imMod.setInterconnection(ic);
                                                            }
                                                    
                                                    
                                                }
                       
                       
                                         
                        ochan.TransmitSubscribeChan(CDReceiverName+"."+ChanReceiverName+"_in", localSSName);
                        
                        //boolean wait = true;
                        
                        while(true){
                            
                            boolean test = ochan.CheckRcvdQueryRspMsg();
                            
                            if(test){
                                break;
                            }
                            
                        }
                        
                        boolean r_s_stat = ochan.GetRespSubscribeQuery();
                        
                                                
                       {
                           
                           //Hashtable resphash = TransceiveQueryChan(destAddr,reqChanQueryMsg);
                           
                           //String resp = resphash.get("ack").toString();
                           
                           //if(resp.equalsIgnoreCase("NOT OK")){
                           //    CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, false);
                           //} else 
                           {
                               
                               
                               //String stat = resphash.get("inchanStat").toString();
                               
                               if(r_s_stat){
                                   
                                    String SSName = SJSSCDSignalChannelMap.getLocalSSName();

                                    JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();

                                    JSONObject jsLocalCDs = jsCurrMap.getJSONObject(SSName);

                                    JSONObject jsCDSenderConf = jsLocalCDs.getJSONObject(CDSenderName);

                                    JSONObject jsCDReceiverConf = jsLocalCDs.getJSONObject(CDReceiverName);

                                    JSONObject jsChanCDSenderConf = jsCDSenderConf.getJSONObject("SChannels");

                                    JSONObject jsChanCDReceiverConf = jsCDReceiverConf.getJSONObject("SChannels");

                                    JSONObject jsOChanCDSenderConf = jsChanCDSenderConf.getJSONObject("outputs");

                                    //JSONObject jsInChanCDReceiverConf = jsChanCDReceiverConf.getJSONObject("inputs");

                                    //inchan.PartnerName = CDSenderName+"."+ChanSenderName+"_o";

                                    ochan.PartnerName = CDReceiverName+"."+ChanReceiverName+"_in";

                                    
                                    //need to synchronize with the other side, when both are in the reconfig time, then change the channel
                                    
                                    
                                    //
                                    
                                    jsOChanCDSenderConf.put("To", CDReceiverName+"."+ChanReceiverName);
                                    //jsInChanCDReceiverConf.put("From",CDSenderName+"."+ChanSenderName);

                                    jsChanCDSenderConf.put("outputs", jsOChanCDSenderConf);
                                   // jsChanCDReceiverConf.put("inputs", jsInChanCDReceiverConf);

                                    jsCDSenderConf.put("SChannels", jsChanCDSenderConf);
                                    jsCDReceiverConf.put("SChannels", jsChanCDReceiverConf);

                                    jsLocalCDs.put(CDSenderName, jsCDSenderConf);
                                    //jsLocalCDs.put(CDReceiverName, jsCDReceiverConf);

                                    jsCurrMap.put(SSName, jsLocalCDs);

                                    SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCurrMap);

                                    if(ochan.IsChannelLocal()){
                                        ochan.setDistributed();
                                        ochan.setInterfaceManager(imMod);
                                    } 



                                    //inchan.set_partner_smp(ochan);
                                    //ochan.set_partner_smp(inchan);

                                    //channels.put(inchan.Name,inchan);
                                    channels.put(ochan.Name, ochan);

                                    CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, true);

                                    sc.updateClockDomain(cdSender, CDSenderName);
                                   
                                    im.setChannelInstances(channels);

                                } else {
                                    CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, false);
                                }
                               
                           }
                           
                       }
                
            } else {
                
                CDLCBuffer.AddInvokeServ2StatChanReconfig(CDSenderName, ChanSenderName, true);
                
            }
            
            //im.updateCDLocationInIMFromServRegSOSJNoP2P();
            
                    
           
                        //if(inchan.get_r_r()==0 && inchan.get_r_s()==0){
                       
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        vec.addElement(im);
        vec.addElement(sc);
        
        return vec;
        
    }
    
    public Vector ResetSubscribedInChan(Scheduler sc, InterfaceManager im){
         
         Vector vec = new Vector();      

         
         try{
             
             String localSSName = SJSSCDSignalChannelMap.getLocalSSName();
         
             JSONObject jsAllMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
             JSONObject jsLocalCDMap = jsAllMap.getJSONObject(localSSName);
            // Hashtable channels = im.getAllChannelInstances();
             
             
             Enumeration keysjsIndivCDMaps = jsLocalCDMap.keys();
             
             while(keysjsIndivCDMaps.hasMoreElements()){
                 
                 String CDName = keysjsIndivCDMaps.nextElement().toString();
                 
                 JSONObject jsAllCDSigChans = jsLocalCDMap.getJSONObject(CDName);
                 
                 JSONObject jsAllCDChans = jsAllCDSigChans.getJSONObject("SChannels");
                 
                 JSONObject jsAllCDInChans = jsAllCDChans.getJSONObject("inputs");
                 
                 if(!jsAllCDInChans.isEmpty()){
                     
                     Enumeration keysjsAllCDInChans = jsAllCDInChans.keys();
                     
                     while(keysjsAllCDInChans.hasMoreElements()){
                         
                         String ChanName = keysjsAllCDInChans.nextElement().toString();
                         
                         JSONObject jsInChan = jsAllCDInChans.getJSONObject(ChanName);
                         
                         String OutPartner = jsInChan.getString("From");
                         
                         String CDOut = OutPartner.split(".")[0];
                         
                         JSONObject jsAllCDStats = RegAllCDStats.getAllCDStats();
                         
                         Enumeration keysSSCDStats = jsAllCDStats.keys();
                         
                         while(keysSSCDStats.hasMoreElements()){
                             
                             String SSName = keysSSCDStats.nextElement().toString();
                             
                             JSONObject jsCDStats = jsAllCDStats.getJSONObject(SSName);
                             
                             if(jsCDStats.has(CDOut)){
                                 
                                 String CDstat = jsCDStats.getString(CDOut);
                                 
                                 if(CDstat.equalsIgnoreCase("Killed")){
                                     
                                     
                                     
                                     if(sc.SchedulerHasCD(CDName)){
                                        
                                         ClockDomain cdInChan = sc.getClockDomain(CDName);
                                         
                                         Field f = cdInChan.getClass().getField(ChanName+"_in");
                                         input_Channel inchan = (input_Channel)f.get(cdInChan);
                                         
                                         inchan.setSubscribedStat(false);
                                         inchan.forcePartnerPreempt();
                                         
                                     } else if (CDObjectsBuffer.CDObjBufferHas(CDName)){
                                         
                                         ClockDomain cdInChan = CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                                         
                                         Field f = cdInChan.getClass().getField(ChanName+"_in");
                                         input_Channel inchan = (input_Channel)f.get(cdInChan);
                                         
                                         inchan.setSubscribedStat(false);
                                         inchan.forcePartnerPreempt();
                                         
                                     }
                                     
                                     
                                 }
                                 
                             }
                             
                         }
                         
                     }
                     
                 }
                 
             }
             
             

            
             
         } catch (JSONException jex){
             jex.printStackTrace();
         } catch (Exception ex){
             ex.printStackTrace();
         }
         
         vec.addElement(im);
         vec.addElement(sc);
         
         return vec;
         
     }
    //if new partner is Local
    
    
    public Vector ReconfigPartnerChannel(JSONObject jsLocalCDs, String keyCurrSS, String PartnerChanDir, String PartnerChanName, String PartnerChanCDName,InterfaceManager im, Scheduler sc)        
    {
        
       
        
        Vector vec = new Vector();
        try{
            
                    Hashtable channels = im.getAllChannelInstances();
            
                                                    //ClockDomain cdins = SJSSCDSignalChannelMap.GetCDInstancesFromMap(keyCDName);

                                                    //JSONObject jsSigsChans = jsLocalCDs.getJSONObject(migCDName);
                                                    if(jsLocalCDs.has(PartnerChanCDName) || keyCurrSS.equals(im.getCDLocation(PartnerChanCDName))){
                                                        
                                                        JSONObject jsSigsChans = jsLocalCDs.getJSONObject(PartnerChanCDName);

                                                        //Enumeration keysSigsChans = jsSigsChans.keys();

                                                      //  while (keysSigsChans.hasMoreElements()){
                                                        
                                                            //String tagSigsChans = keysSigsChans.nextElement().toString();

                                                           // if (tagSigsChans.equalsIgnoreCase("SChannels")){
                                                             
                                                                JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("SChannels");
                      
                                                            //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                                                if(PartnerChanDir.equalsIgnoreCase("input")){
                                                                    
                                                                    JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                                                    
                                                                    JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(PartnerChanName);
                                                                    
                                                                    
                                                                    String cname = PartnerChanName.trim()+"_in";
                                                                    
                                                                    ClockDomain cdObj = null;
                                                                    
                                                                    if(sc.SchedulerHasCD(PartnerChanCDName)){
                                                                        cdObj = sc.getClockDomain(PartnerChanCDName);
                                                                    } else if (CDObjectsBuffer.CDObjBufferHas(PartnerChanCDName)){
                                                                        cdObj = CDObjectsBuffer.GetCDInstancesFromBuffer(PartnerChanCDName);
                                                                    }
                                                                    
                                                                                                input_Channel inchan;

                                                                                                Field fInchanOld = cdObj.getClass().getField(cname);

                                                                                                inchan = (input_Channel)fInchanOld.get(cdObj);
                                                                                                
                                                                                                inchan.set_preempted();
                                                                                                
                                                                                                String Origin = SChansInputConfigs.getString("From");
                                                                        
                                                                    
                                                                                        if(!Origin.equalsIgnoreCase(".")){

                                                                                            String OrigCDName = Origin.split("\\.")[0];

                                                                                            String OrigPartChanNameO = Origin.split("\\.")[1]+"_o";

                                                                                                if(jsLocalCDs.has(OrigCDName) || keyCurrSS.equals(im.getCDLocation(OrigCDName))){

                                                                                                   ClockDomain partnercdOld = null;

                                                                                                   if(sc.SchedulerHasCD(OrigCDName)){
                                                                                                       partnercdOld = sc.getClockDomain(OrigCDName);
                                                                                                   } else if(CDObjectsBuffer.CDObjBufferHas(OrigCDName)){
                                                                                                       partnercdOld = CDObjectsBuffer.GetCDInstancesFromBuffer(OrigCDName);
                                                                                                   }
                                                                                                   
                                                                                                   JSONObject jsOldPartSigsChans = jsLocalCDs.getJSONObject(OrigCDName);

                                                                                                    Enumeration keysOldPartSigsChans = jsOldPartSigsChans.keys();

                                                                                                    while (keysOldPartSigsChans.hasMoreElements()){

                                                                                                        String tagOldPartSigsChans = keysOldPartSigsChans.nextElement().toString();

                                                                                                        if (tagOldPartSigsChans.equalsIgnoreCase("SChannels")){

                                                                                                            JSONObject jsOldPartSigsChansInd = jsOldPartSigsChans.getJSONObject(tagOldPartSigsChans);

                                                                                                        //Enumeration keysSChansInOuts = jsSigsChansInd.keys();
                                                                                                                output_Channel ochanOld;
                                                                                                                    Field fOChanOld = partnercdOld.getClass().getField(OrigPartChanNameO);
                                                                                                                    ochanOld = (output_Channel)fOChanOld.get(partnercdOld);

                                                                                                                    ochanOld.set_preempted();

                                                                                                                    ochanOld.PartnerName = ".";


                                                                                                                    inchan.set_partner_smp(new output_Channel());
                                                                                                                    ochanOld.set_partner_smp(new input_Channel());
                                                                                                           

                                                                                                                JSONObject jsOldPartSChansOutputs = jsOldPartSigsChansInd.getJSONObject("outputs");

                                                                                                                JSONObject SChansOldPartOutputConfigs = jsOldPartSChansOutputs.getJSONObject(OrigCDName);
                                                                                                                
                                                                                                                SChansOldPartOutputConfigs.put("To", ".");
                                                                                                                jsOldPartSChansOutputs.put(OrigCDName, SChansOldPartOutputConfigs);
                                                                                                                jsOldPartSigsChansInd.put("outputs", jsOldPartSChansOutputs);
                                                                                                                jsOldPartSigsChans.put("SChannels",jsOldPartSigsChansInd);
                                                                                                                jsLocalCDs.put(OrigCDName,jsOldPartSigsChans);
                                                                                                                
                                                                                                        }
                                                                                                    }

                                                                                                                    

                                                                                                } else {

                                                                                                    inchan.TransmitPartnerReconfigChanChanges(".");

                                                                                                }
                                                                                
                                                                                

                                                                                                inchan.PartnerName=".";
                                                                                                channels.put(inchan.Name, inchan);
                                                                                                
                                                                                                SChansInputConfigs.put("From", ".");
                                                                                                jsSChansInputs.put(PartnerChanName, SChansInputConfigs);
                                                                                                jsSigsChansInd.put("inputs", jsSChansInputs);
                                                                                                jsSigsChans.put("SChannels",jsSigsChansInd);
                                                                                                jsLocalCDs.put(PartnerChanCDName,jsSigsChans);
                                                                                                
                                                                                    } 
                                                                                        
                                                                                        
                                                                                    
                                                                    
                                                                    
                                                                    
                                                                } else if (PartnerChanDir.equalsIgnoreCase("output")){
                                                                    
                                                                    
                                                                    JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                                                    
                                                                    JSONObject SChansOutputConfigs = jsSChansOutputs.getJSONObject(PartnerChanName);
                                                                    
                                                                    
                                                                    String cname = PartnerChanName.trim()+"_o";
                                                                    
                                                                    ClockDomain cdObj = null;
                                                                    
                                                                    if(sc.SchedulerHasCD(PartnerChanCDName)){
                                                                        cdObj = sc.getClockDomain(PartnerChanCDName);
                                                                    } else if (CDObjectsBuffer.CDObjBufferHas(PartnerChanCDName)){
                                                                        cdObj = CDObjectsBuffer.GetCDInstancesFromBuffer(PartnerChanCDName);
                                                                    }
                                                                    
                                                                                                output_Channel ochan;

                                                                                                Field fochanOld = cdObj.getClass().getField(cname);

                                                                                                ochan = (output_Channel)fochanOld.get(cdObj);
                                                                                                
                                                                                                ochan.set_preempted();
                                                                                                
                                                                                                String Destination = SChansOutputConfigs.getString("To");
                                                                        
                                                                    
                                                                                        if(!Destination.equalsIgnoreCase(".")){

                                                                                            String DestCDName = Destination.split("\\.")[0];

                                                                                            String DestPartChanNameIn = Destination.split("\\.")[1]+"_in";

                                                                                                if(jsLocalCDs.has(DestCDName) || keyCurrSS.equals(im.getCDLocation(DestCDName))){

                                                                                                   ClockDomain partnercdOld = null;

                                                                                                   if(sc.SchedulerHasCD(DestCDName)){
                                                                                                       partnercdOld = sc.getClockDomain(DestCDName);
                                                                                                   } else if(CDObjectsBuffer.CDObjBufferHas(DestCDName)){
                                                                                                       partnercdOld = CDObjectsBuffer.GetCDInstancesFromBuffer(DestCDName);
                                                                                                   }

                                                                                                                    input_Channel inchanOld;
                                                                                                                    Field fInChanOld = partnercdOld.getClass().getField(DestPartChanNameIn);
                                                                                                                    inchanOld = (input_Channel)fInChanOld.get(partnercdOld);

                                                                                                                    inchanOld.set_preempted();

                                                                                                                    inchanOld.PartnerName = ".";


                                                                                                                    inchanOld.set_partner_smp(new output_Channel());
                                                                                                                    ochan.set_partner_smp(new input_Channel());

                                                                                                } else {

                                                                                                    ochan.TransmitPartnerReconfigChanChanges(".");

                                                                                                }
                                                                                
                                                                                

                                                                                                ochan.PartnerName=".";
                                                                                                channels.put(ochan.Name, ochan);
                                                                        
                                                                                    } 
                                                                    
                                                                    
                                                                }
                                                                
                                                            //}
                                                            
                                                       // }
                                                        
                                                    }
                                                    
                                                    
                                                    
                                                    im.setChannelInstances(channels);
                                                    
                                                    JSONObject currMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                                                    currMap.put(keyCurrSS, jsLocalCDs);
                                                    SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(currMap);
                                                    
            
        } catch (JSONException jex){
            jex.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
            
           
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    
        
        
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                
                                return vec;
            
        }
    
    public Vector ModifyMigratingCDLocalPartnerToDistributed(JSONObject jsLocalCDs, JSONObject jsMigratingMap, String keyCurrSS, String migCDName,ClockDomain migratingcdins, String MigType, String DestSS,InterfaceManager im, Scheduler sc)        
    {
        
        Vector vec = new Vector();
        try{
            
                    Hashtable channels = im.getAllChannelInstances();
            
                                                    //ClockDomain cdins = SJSSCDSignalChannelMap.GetCDInstancesFromMap(keyCDName);

                                                    //JSONObject jsSigsChans = jsLocalCDs.getJSONObject(migCDName);
                    
                                                    JSONObject jsSigsChans = jsMigratingMap.getJSONObject(migCDName);

                                                   // Enumeration keysSigsChans = jsSigsChans.keys();

                                                   // while (keysSigsChans.hasMoreElements()){

                                                 //       String tagSigsChans = keysSigsChans.nextElement().toString();

                                                       // if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                                //while (keysSChansInOuts.hasMoreElements()){

                                                   // String keyInOut = keysSChansInOuts.nextElement().toString();

                                                    //if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");

                                                        Enumeration keysSigsInputsName = jsSChansInputs.keys();

                                                        while (keysSigsInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSigsInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] pnames = pname.split("\\.");
                                                                

                                            // If the channel is local
                                                                //System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS);

                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){

                                                                        //System.out.println("Creating Input channel!");
                                                                        
                                                                        
                                                                        ClockDomain partnercd=null;
                                                                        
                                                                         Field f = migratingcdins.getClass().getField(cname);
                                                                                
                                                                        input_Channel inchan = (input_Channel)f.get(migratingcdins);
                                                                       
                                                                       // if(MigType.equals("strong")){
                                                                       //     inchan.setDistributedStrongMigration();
                                                                       // } else {
                                                                        //    inchan.setDistributedWeakMigration();
                                                                        //}
                                                                        
                                                                        channels.remove(inchan.Name);
                                                                        
                                                                        //im.addCDLocation(DestSS, migratingcdins.getName());
                                                                        
                                                                        //inchan.setInterfaceManager(im);
                                                                        
                                                                        // if cd in Active state, fetch from scheduler
                                                                        
                                                                        if (sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                        
                                                                                        if(MigType.equals("strong")){
                                                                                            
                                                                                            ochan.setDistributedStrongMigration();
                                                                                        } else {
                                                                                            ochan.setDistributedWeakMigration();
                                                                                        }
                                                                                        
                                                                                        //Interconnection ic = im.getInterconnection();
                                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(DestSS, pnames[0], "output", SChansInputsName);
                                                                                        im.addCDLocation(DestSS, migCDName);
                                                                                        //im.setInterconnection(ic);
                                                                                        
                                                                                        ochan.setInterfaceManager(im);
                                                                                        
                                                                                            
                                                                                            //ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        //ochan.Name = pname;
                                                                                        //ochan.PartnerName = keyCDName+"."+cname;

                                                                                        //ochan.set_partner_smp(inchan);
                                                                                        //inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        //channels.put(inchan.Name, inchan);
                                                                                        //channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                            //sc.updateClockDomain(partnercd, partnercd.getName());
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(MigType.equals("strong")){
                                                                                            ochan.setDistributedStrongMigration();
                                                                                        } else {
                                                                                            ochan.setDistributedWeakMigration();
                                                                                        }
                                                                                        
                                                                                        //Interconnection ic = im.getInterconnection();
                                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(DestSS, pnames[0], "output", SChansInputsName);
                                                                                        im.addCDLocation(DestSS, migCDName);
                                                                                        //im.setInterconnection(ic);
                                                                                        
                                                                                        ochan.setInterfaceManager(im);
                                                                                        
                                                                                        //CDObjectsBuffer.AddCDInstancesToBuffer(partnercd.getName(), partnercd);
                                                                                        
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                      //      ochan.setInit();
                                                                                          //  ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                            
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        //ochan.Name = pname;
                                                                                       // ochan.PartnerName = keyCDName+"."+cname;

                                                                                        //ochan.set_partner_smp(inchan);
                                                                                        //inchan.set_partner_smp(ochan);

                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        //channels.put(inchan.Name, inchan);
                                                                                        //channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                
                                                                            } 
                                                                        
                                                                        //System.out.println("SigRec, InCHan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                        //if(partnercd == null)
                                                                           // throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                                                
                                                                                
                                                                                //if(partnercd!=null) {
                                                                                    
                                                                                  //  if(channels.containsKey(pname)){
                                                                                        
                                                                                     //   output_Channel ochan = (output_Channel)channels.get(pname);
                                                                                        
                                                                                     //   inchan.set_partner_smp(ochan);
                                                                                      //  ochan.set_partner_smp(inchan);
                                                                                        
                                                                                  //  } else {
                                                                                        
                                                                                        
                                                                                        //if(!channels.containsKey(ochan.Name) ){
                                                                                            //channels.put(ochan.Name, ochan);
                                                                                        //} else
                                                                                            //throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);

                                                                                       // }
                                                                                    
                                                                                    
                                                                                //}
                                                                               

                                                                                
                                                                                   // } else
                                                                                       // throw new RuntimeException("Tried to initialize the same channel twice : "+inchan.Name);
                                                                                   

                                                                    }
                                                                   
                                                         }

                                               // } 
                                                    //else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            //String pname2 = SigOutputConfigs.getString("To").trim();
                                                            String[] pnames = pname.split("\\.");

                                                             //System.out.println( "Output channel detected!");
                                                            
                                                            //System.out.println("SigRec, OChan, cd location of: "+pnames[0]+"is " +keyCurrSS);
                                    // If the channel is local
                                                              //System.out.println( "Location of " +pnames[0]+"is :" +im.getCDLocation(pnames[0]));
                                                           // if(!channels.containsKey(keyCDName+"."+cname)){
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    ClockDomain partnercd = null;
                                                                
                                                                    Field f = migratingcdins.getClass().getField(cname);
                                                                    
                                                                    output_Channel ochan = (output_Channel) f.get(migratingcdins);
                                                                    
                                                                   // if(MigType.equals("strong")){
                                                                   //     ochan.setDistributedStrongMigration();
                                                                   // } else {
                                                                   //     ochan.setDistributedWeakMigration();
                                                                   // }
                                                                    
                                                                    //ochan.setInterfaceManager(im);
                                                                    
                                                                    channels.remove(ochan.Name);
                                                                    
                                                                     //System.out.println("Creating Output channel!");
                                                                // if cd in Active state, fetch from scheduler
                                                                    if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                           
                                                                            input_Channel inchan;
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);

                                                                            im.addCDLocation(DestSS, migCDName);
                                                                            
                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                            if(MigType.equals("strong")){
                                                                                inchan.setDistributedStrongMigration();
                                                                            } else {
                                                                                inchan.setDistributedWeakMigration();
                                                                            }
                                                                            
                                                                           //Interconnection ic = im.getInterconnection();
                                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(DestSS, pnames[0], "input", SChansOutputsName);
                                                                                        im.addCDLocation(DestSS, migCDName);
                                                                                        //im.setInterconnection(ic);
                                                                            
                                                                            inchan.setInterfaceManager(im);
                                                                       // } else {
                                                                       //     inchan.setChannelCDState("Sleep");
                                                                       // }
                                                                        
                                                                        //sc.updateClockDomain(partnercd, partnercd.getName());
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                               
                                                                                
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                            
                                                                        input_Channel inchan;
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        inchan = (input_Channel)f2.get(partnercd);

                                                                       // if (sc.SchedulerHasCD(pnames[0])){
                                                                       //     inchan.setInit();
                                                                       //     inchan.setChannelCDState("Active");
                                                                       // } else {
                                                                           if(MigType.equals("strong")){
                                                                              inchan.setDistributedStrongMigration();
                                                                           } else {
                                                                               
                                                                               inchan.setDistributedWeakMigration();
                                                                           }
                                                                           
                                                                           //Interconnection ic = im.getInterconnection();
                                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(DestSS, pnames[0], "input", SChansOutputsName);
                                                                                        im.addCDLocation(DestSS, migCDName);
                                                                                        //im.setInterconnection(ic);
                                                                                 
                                                                            inchan.setInterfaceManager(im);
                                                                       // }
                                                                           
                                                                        //CDObjectsBuffer.AddCDInstancesToBuffer(partnercd.getName(), partnercd);
                                                                    }
                                       
                                                                //System.out.println("SigRec, OutChan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                
                                                                
                                                                
                                                                        
                                                                       // if(!channels.containsKey(inchan.Name)){
                                                                            //channels.put(inchan.Name, inchan);

                                                                       // }
                                                                       // else
                                                                       //     throw new RuntimeException("Tried to initialize the same channel twice : "+inchan.Name);
                                                                        
                                                                        

                                                               // }
                                                                //else
                                                                //    throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);
                                                                
                                                        }
                                                                
                                                        /*        
                                                        else{
                                                             output_Channel ochan;

                                                                    Field f = cdins.getClass().getField(cname);
                                                                    ochan = (output_Channel)f.get(cdins);
                                                                    ochan.setInit();

                                                                    ochan.Name = keyCDName+"."+cname;
                                                                    ochan.PartnerName = pname;
                                                                    ochan.setDistributed();
                                                                    ochan.setInterfaceManager(im);
                                                                    ochan.setChannelCDState("Active");

                                                                    SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);

                                                                   //  if(!channels.containsKey(ochan.Name)){
                                                                            channels.put(ochan.Name, ochan);
                                                                            
                                                                   // }
                                                                   // else
                                                                   //         throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);
                                                                    
                                                                    
                                                                    }
                                                                */
                                                            //}
                                                            
                                            

                                              }

                                           //}

                                        //}

                                       // } 
                                                        /*
                                                        else if (tagSigsChans.equalsIgnoreCase("AChannels")){

                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);

                                             Enumeration keysAChansInOuts = jsSigsChansInd.keys();

                                                while (keysAChansInOuts.hasMoreElements()){

                                                    String keyInOut = keysAChansInOuts.nextElement().toString();

                                                    if (keyInOut.equalsIgnoreCase("inputs") || keyInOut.equalsIgnoreCase("outputs")){

                                                        
                                                        JSONObject jsAChans = new JSONObject();
                                                        
                                                        if (keyInOut.equalsIgnoreCase("inputs")){
                                                            
                                                            jsAChans = jsSigsChansInd.getJSONObject("inputs");
                                                            
                                                        } else if(keyInOut.equalsIgnoreCase("outputs")){
                                                        
                                                            jsAChans= jsSigsChansInd.getJSONObject("outputs");
                                                        
                                                        }
            
                                                        Enumeration keysAChansName = jsAChans.keys();

                                                        while (keysAChansName.hasMoreElements()){

                                                            String AChansName = keysAChansName.nextElement().toString();

                                                            JSONObject AChansConfigs = jsAChans.getJSONObject(AChansName);

                                                            String cname = AChansName;

                                                            String pname="";
                                                            
                                                            if (keyInOut.equalsIgnoreCase("inputs")){
                                                            
                                                                pname = AChansConfigs.getString("From").trim();
                                                            
                                                            } else if(keyInOut.equalsIgnoreCase("outputs")){
                                                        
                                                                pname = AChansConfigs.getString("To").trim();
                                                        
                                                            }
                                                            
                                                                String[] pnames = pname.split("\\.");

                                                                im.addCDLocation(DestSS, pnames[0]);
                                                                
                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                        
                                                                        if(sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                            ClockDomain partnercd = sc.getClockDomain(pnames[0]);
                                                                            //if(partnercd == null)
                                                                            //throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                                            //AChannel chan;

                                                                                
                                                                                Field f2 = partnercd.getClass().getField(pnames[1]);
                                                            //f.set(cdins, chan);
                                                                                AChannel chan = (AChannel)f2.get(partnercd);
                                                                                //AChannel chan = (AChannel)f.get(cdins);
                                                                                //f2.set(partnercd, chan); // sharing achan obj
                                                                                
                                                                                //chan.setDistributedRetainState();
                                                                                //channels.put(keyCDName+"."+cname, chan);
                                                                                //channels.put(pname, chan);

                                                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "AChannel", "input", AChansName,(Object) chan);
                                                                            
                                                                            
                                                                        } else if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                            
                                                                            
                                                                             ClockDomain partnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                            //if(partnercd == null)
                                                                            //throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                                            AChannel chan;

                                                                                Field f = migratingcdins.getClass().getField(cname);
                                                                                Field f2 = partnercd.getClass().getField(pnames[1]);
                                                            //f.set(cdins, chan);
                                                                                //chan = (AChannel)f.get(cdins);
                                                                                chan = (AChannel)f2.get(partnercd);
                                                                                f.set(migratingcdins, chan);
                                                                                //f2.set(partnercd, chan); // sharing achan obj
                                                                                chan.setInit();
                                                                                //channels.put(keyCDName+"."+cname, chan);
                                                                               // channels.put(pname, chan);

                                                                               // SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "AChannel", "input", AChansName,(Object) chan);
                                                                            
                                                                        }
                                                                        
                                                                    }

                                                                }

                                                } 
                                                    
                                                    else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsAChansOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysAChansOutputsName = jsAChansOutputs.keys();

                                                        while (keysAChansOutputsName.hasMoreElements()){

                                                            String AChansOutputsName = keysAChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsAChansOutputs.getJSONObject(AChansOutputsName);

                                                            String pname = SigOutputConfigs.getString("To").trim();
                                                            String cname = AChansOutputsName;
                                                            String[] pnames = pname.split("\\.");

                                    // If the channel is local

                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    ClockDomain partnercd = (ClockDomain)CDObjectsBuffer.getAllCDInstancesFromBuffer().get(pnames[0]);

                                                                    AChannel chan;

                                                                    Field f = cdins.getClass().getField(cname);
                                                                    Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                    //f.set(cdins, chan);
                                                                    chan = (AChannel)f.get(cdins);
                                                                    f2.set(partnercd, chan); // sharing achan obj
                                                                    chan.setInit();

                                                                    }
                                                                    else{
                                                                        AChannel chan;

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        chan = (AChannel)f.get(cdins);
                                                                        chan.setInit();

                                                                        chan.Name = keyCDName+"."+cname;
                                                                        chan.PartnerName = pname;
                                                                        chan.setDistributed();
                                                                        chan.setInterfaceManager(im);

                                                                        }

                                                                      }

                                                               }
                                                               
                                                            }

                                                         }
                                                        */

                                                    //}
                                                    
                                                    im.setChannelInstances(channels);
                                                    
                                                    
            
        } catch (JSONException jex){
            jex.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
            
           
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    
        
        
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                
                                return vec;
            
        }
    
    public Vector AddSigChanObjAndInitModifySCIM(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, ClockDomain cdins, Hashtable AllCDs,InterfaceManager im, Scheduler sc) throws JSONException, Exception
        {
            
            Vector vec = new Vector();
            
            Hashtable channels = im.getAllChannelInstances();
            
                                    im.addCDLocation(keyCurrSS, keyCDName);
                                                
                                                    //ClockDomain cdins = SJSSCDSignalChannelMap.GetCDInstancesFromMap(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                    //Enumeration keysSigsChans = jsSigsChans.keys();

                                                   // while (keysSigsChans.hasMoreElements()){

                                                        //String tagSigsChans = keysSigsChans.nextElement().toString();

                                                       // if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigs = jsSigsChans.getJSONObject("signals");

                                                            GenericSignalReceiver server = null;
                                                            GenericSignalSender client = null;
                                                            Hashtable config = new Hashtable();

                                                            //Enumeration keysSigsInOuts = jsSigsChansSigs.keys();

                                                            //while (keysSigsInOuts.hasMoreElements()){

                                                                //String keyInOut = keysSigsInOuts.nextElement().toString();

                                                              //  if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    JSONObject SigInputConfigs = jsSigsInputs.getJSONObject(SigsInputsName);

                                                                    Enumeration keysSigInputConfigs = SigInputConfigs.keys();

                                                                    while (keysSigInputConfigs.hasMoreElements()){

                                                                        String keySigInputConfig = keysSigInputConfigs.nextElement().toString();

                                                                        config.put(keySigInputConfig, SigInputConfigs.getString(keySigInputConfig));

                                                                    }

                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                                    
                                                            if(SigInputConfigs.get("Name").toString().equals("SOSJDiscovery")){
                                                                //server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.TransceiveDisc").newInstance();
                                                                server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                                            } else {
                                                                server = (GenericSignalReceiver) Class.forName(SigInputConfigs.get("Class").toString()).newInstance();
                                                            }       
                                                                    
                                                            //server = (GenericSignalReceiver) Class.forName(SigInputConfigs.get("Class").toString()).newInstance();

                                                            server.cdname = keyCDName;
                                                            server.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setServer(server);
                                                            signal.setuphook();
                                                            signal.setInit();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                               // } 
                                                             //   else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSigsOutputs = jsSigs.getJSONObject("outputs");

                                                    Enumeration keysSigsOutputsName = jsSigsOutputs.keys();

                                                        while (keysSigsOutputsName.hasMoreElements()){

                                                            String SigsOutputsName = keysSigsOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSigsOutputs.getJSONObject(SigsOutputsName);

                                                            Enumeration keysSigOutputConfigs = SigOutputConfigs.keys();

                                                            while (keysSigOutputConfigs.hasMoreElements()){

                                                                String keySigOutputConfig = keysSigOutputConfigs.nextElement().toString();

                                                                config.put(keySigOutputConfig, SigOutputConfigs.getString(keySigOutputConfig));

                                                            }
                                                            
                                                            if (SigOutputConfigs.get("Name").toString().equalsIgnoreCase("SendACK")){
                                         
                                                                client = (GenericSignalSender) Class.forName("systemj.signals.SOA.ServiceACKSender").newInstance();


                                                            } else {
                                                                client = (GenericSignalSender) Class.forName(SigOutputConfigs.getString("Class")).newInstance();
                                                            }

                                                            client.cdname = keyCDName;
                                                            client.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setClient(client);
                                                            signal.setInit();

                                                            //SignalObjBuffer.putOutputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsOutputsName);
                                                            //SignalObjBuffer.putOutputSignalGSSInstanceToMap((Object) client, keyCurrSS, keyCDName, SigsOutputsName);

                                                        }

                                                //}

                                            //}

                                        //} 
                                                    //    else if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSChansInd = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                               // while (keysSChansInOuts.hasMoreElements()){

                                                    //String keyInOut = keysSChansInOuts.nextElement().toString();

                                                   // if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSChansInd.getJSONObject("inputs");

                                                        Enumeration keysSChansInputsName = jsSChansInputs.keys();

                                                        while (keysSChansInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSChansInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                
                                                                if(SChansInputConfigs.getString("From").equalsIgnoreCase(".")){
                                                                    
                                                                    input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                        inchan.setDistributed();
                                                                                        inchan.setInit();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        //inchan.PartnerName = pname;
                                                                                        inchan.PartnerName = ".";
                                                                                        //inchan.set_partner_smp(ochan);
                                                                                        
                                                                                
                                                                                        //output_Channel ochan;
                                                                                        //Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        //ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            //ochan.setInit();
                                                                                            //ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        //ochan.Name = pname;
                                                                                        //ochan.PartnerName = keyCDName+"."+cname;

                                                                                       // ochan.set_partner_smp(inchan);
                                                                                       // inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                    
                                                                    
                                                                } else {
                                                                    
                                                                
                                                                
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] pnames = pname.split("\\.");
                                                                
                                                                
                                            // If the channel is local
                                                                //System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS);

                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0])) || AllCDs.containsKey(pnames[0])){

                                                                        System.out.println("Creating Input channel!");
                                                                        
                                                                        ClockDomain partnercd=null;
                                                                        
                                                                        JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDOSChanMap = jsPartnerCDSChanMap.getJSONObject("outputs");
                                                                        String PartnerDest = jsPartnerCDOSChanMap.getString("To");
                                                                       
                                                                        
                                                                        // if cd in Active state, fetch from scheduler
                                                                        if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            input_Channel inchan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                inchan.setInterfaceManager(im);
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            ochan.setInit();
                                                                                            ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromBuffer().get(pnames[0]);
                                                                                
                                                                                input_Channel inchan;
                                                                                
                                                                                

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                inchan.setInterfaceManager(im);
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        
                                                                                        ochan.setInit();
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                      //      ochan.setInit();
                                                                                          //  ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                            ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);

                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                
                                                                            } else {
                                                                                
                                                                                   if(AllCDs.containsKey(pnames[0])){
                                                                                       
                                                                                       partnercd = (ClockDomain) AllCDs.get(pnames[0]);
                                                                            
                                                                                input_Channel inchan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                inchan.setInterfaceManager(im);
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                            ochan.setInit();
                                                                                            ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                       
                                                                                   } else {
                                                                                       
                                                                                       //partnercd = (ClockDomain) AllCDs.get(pnames[0]);
                                                                            
                                                                                        input_Channel inchan;
                                                                            

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                        inchan.setInit();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                inchan.setInterfaceManager(im);
                                                                                        //output_Channel ochan;
                                                                                        //Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        //ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            //ochan.setInit();
                                                                                            //ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        //ochan.Name = pname;
                                                                                        //ochan.PartnerName = keyCDName+"."+cname;

                                                                                       // ochan.set_partner_smp(inchan);
                                                                                       // inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        //channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                       
                                                                                       
                                                                                   }
                                                                                   
                                                                            }
                                                                        
                                                                        

                                                                        //System.out.println("SigRec, InCHan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                        //if(partnercd == null)
                                                                           // throw new RuntimeException("Clock-domain "+pnames[0]+" not found");
                                                                                
                                                                                
                                                                                //if(partnercd!=null) {
                                                                                    
                                                                                  //  if(channels.containsKey(pname)){
                                                                                        
                                                                                     //   output_Channel ochan = (output_Channel)channels.get(pname);
                                                                                        
                                                                                     //   inchan.set_partner_smp(ochan);
                                                                                      //  ochan.set_partner_smp(inchan);
                                                                                        
                                                                                  //  } else {
                                                                                        
                                                                                        
                                                                                        //if(!channels.containsKey(ochan.Name) ){
                                                                                            //channels.put(ochan.Name, ochan);
                                                                                        //} else
                                                                                            //throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);

                                                                                       // }
                                                                                    
                                                                                    
                                                                                //}
                                                                               

                                                                                
                                                                                   // } else
                                                                                       // throw new RuntimeException("Tried to initialize the same channel twice : "+inchan.Name);
                                                                                   

                                                                    } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0]) || SJServiceRegistry.HasNonLocalServiceCD(pnames[0])){
                                                                   
                                                                        //partner CD is in another SS!
                                                                        
                                                                         input_Channel inchan;

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        inchan = (input_Channel)f.get(cdins);
                                                                        inchan.setInit();
                                                                        inchan.setDistributed();
                                                                        inchan.setInterfaceManager(im);
                                                                        inchan.setChannelCDState("Active");
                                                                        
                                                                       String partnerSSLocation;
                                                                    
                                                                        if(im.hasCDLocation(pnames[0])){
                                                                            partnerSSLocation = im.getCDLocation(pnames[0]);
                                                                        } else {
                                                                            //partnerSSLocation = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                            partnerSSLocation = RegAllSSAddr.getSSAddrOfSSName(CDLCBuffer.GetCDLocTempBuffer(pnames[0]));
                                                                        }

                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);

                                                                        
                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(partnerSSLocation, keyCDName, "input", SChansInputsName);
                                                                    
                                                                    //im.setInterconnection(ic);
                                                                    SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                        
                                                                      //  if(!channels.containsKey(inchan.Name)){
                                                                            channels.put(inchan.Name, inchan);
                                                                        
                                                                        // end
                                                                        
                                                                        
                                                                    }
                                                                        else{

                                                                       // cd ain't present anywhere, consider to be distributed to allow notifications from another SS
                                                                        
                                                                        input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                        inchan.setDistributed();
                                                                                        inchan.setInit();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        //inchan.set_partner_smp(ochan);
                                                                                        inchan.setInterfaceManager(im);
                                                                                
                                                                                       
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                        //
                                                                            
                                                                     
                                                                        
                                                                        
                                                                    }
                                                                    
                                                                    
                                                                    
                                                                }
                                                                
                                                         }

                                               // } 
                                                    
                                                    //else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSChansInd.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            
                                                            if(SigOutputConfigs.getString("To").equalsIgnoreCase(".")){
                                                                
                                                                output_Channel ochan;
                                                            //input_Channel inchan = new input_Channel();


                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    //ochan.PartnerName = pname;
                                                                                    ochan.PartnerName = ".";

                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setDistributed();
                                                                                    ochan.setInit();
                                                                                    //ochan.set_partner_smp(inchan);

                                                               // if(!channels.containsKey(ochan.Name)){
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                                
                                                            } else {
                                                                
                                                            
                                                            
                                                            //String pname2 = SigOutputConfigs.getString("To").trim();
                                                            String[] pnames = pname.split("\\.");

                                                             //System.out.println( "Output channel detected!");
                                                            
                                                            //System.out.println("SigRec, OChan, cd location of: "+pnames[0]+"is " +keyCurrSS);
                                    // If the channel is local
                                                              //System.out.println( "Location of " +pnames[0]+"is :" +im.getCDLocation(pnames[0]));
                                                           // if(!channels.containsKey(keyCDName+"."+cname)){
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0]))|| AllCDs.containsKey(pnames[0])){
                                                                    ClockDomain partnercd = null;
                                                                
                                                                        JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDInSChanMap = jsPartnerCDSChanMap.getJSONObject("inputs");
                                                                        String PartnerDest = jsPartnerCDInSChanMap.getString("From");
                                                                    
                                                                     //System.out.println("Creating Output channel!");
                                                                // if cd in Active state, fetch from scheduler
                                                                    if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            output_Channel ochan;
                                                                

                                                                            Field f = cdins.getClass().getField(cname);

                                                                            ochan = (output_Channel)f.get(cdins);

                                                                                // Mine
                                                                            ochan.Name = keyCDName+"."+cname;
                                                                            ochan.PartnerName = pname;

                                                                            ochan.setChannelCDState("Active");
                                                                            ochan.setInit();
                                                                
                                                               ochan.setInterfaceManager(im);

                                                                   // } else {
                                                                        
                                                                            input_Channel inchan;
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);

                                                                            if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                            
                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                            inchan.setInit();
                                                                            inchan.setChannelCDState("Active");
                                                                       // } else {
                                                                       //     inchan.setChannelCDState("Sleep");
                                                                       // }
                                                                            inchan.Name = pname; 
                                                                            inchan.PartnerName = keyCDName+"."+cname;

                                                                            inchan.set_partner_smp(ochan);
                                                                            ochan.set_partner_smp(inchan);
                                                                        
                                                                        
                                                                        
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                
                                                                                
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromBuffer().get(pnames[0]);
                                                                            
                                                                                output_Channel ochan;
                                                                

                                                                                Field f = cdins.getClass().getField(cname);

                                                                                ochan = (output_Channel)f.get(cdins);

                                                                                    // Mine
                                                                                ochan.Name = keyCDName+"."+cname;
                                                                                ochan.PartnerName = pname;

                                                                                ochan.setChannelCDState("Active");
                                                                                ochan.setInit();
                                                                                ochan.setInterfaceManager(im);
                                                                               
                                                                              
                                                                        
                                                                        input_Channel inchan;
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        inchan = (input_Channel)f2.get(partnercd);

                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                        
                                                                      
                                                                            inchan.setChannelCDState("Sleep");
                                                                            inchan.setInit();
                                                                      
                                                                        
                                                                        inchan.Name = pname; 
                                                                        inchan.PartnerName = keyCDName+"."+cname;
                                                                        
                                                                        inchan.set_partner_smp(ochan);
                                                                        ochan.set_partner_smp(inchan);
                                                                        
                                                                        
                                                                    
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                                
                                                                            } else {
                                                                                
                                                                                if(AllCDs.containsKey(pnames[0])){
                                                                                    
                                                                                    partnercd = (ClockDomain)AllCDs.get(pnames[0]);
                                                                            
                                                                                        output_Channel ochan;


                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setInit();

                                                                        ochan.setInterfaceManager(im);

                                                                                    input_Channel inchan;
                                                                                    Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                    inchan = (input_Channel)f2.get(partnercd);

                                                                                    if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                    
                                                                                //if (sc.SchedulerHasCD(pnames[0])){
                                                                                    inchan.setInit();
                                                                                    inchan.setChannelCDState("Active");
                                                                               // } else {
                                                                               //     inchan.setChannelCDState("Sleep");
                                                                               // }
                                                                                    inchan.Name = pname; 
                                                                                    inchan.PartnerName = keyCDName+"."+cname;

                                                                                    inchan.set_partner_smp(ochan);
                                                                                    ochan.set_partner_smp(inchan);
                                                                        
                                                                        
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                                                    channels.put(inchan.Name,inchan);
                                                                                    
                                                                                }
                                                                                
                                                                            }
                                       
                                                        } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0]) || SJServiceRegistry.HasNonLocalServiceCD(pnames[0])){
                                                            
                                                            //distributed CD, partner CD is in other SS
                                                            
                                                                    output_Channel ochan;

                                                                    Field f = cdins.getClass().getField(cname);
                                                                    ochan = (output_Channel)f.get(cdins);
                                                                    ochan.setInit();

                                                                    ochan.Name = keyCDName+"."+cname;
                                                                    ochan.PartnerName = pname;
                                                                    ochan.setDistributed();
                                                                    ochan.setInterfaceManager(im);
                                                                    ochan.setChannelCDState("Active");
                                                                    
                                                                    //Interconnection ic = im.getInterconnection();
                                                                    
                                                                    String partnerSSLocation;
                                                                    
                                                                    if(im.hasCDLocation(pnames[0])){
                                                                        partnerSSLocation = im.getCDLocation(pnames[0]);
                                                                    } else {
                                                                        //partnerSSLocation = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                        partnerSSLocation = RegAllSSAddr.getSSAddrOfSSName(CDLCBuffer.GetCDLocTempBuffer(pnames[0]));
                                                                    }
                                                                    
                                                                    //String partnerssLocation =  SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                    
                                                                    SJSSCDSignalChannelMap.AddChanLinkUserToSS(partnerSSLocation, keyCDName, "output", SChansOutputsName);
                                                                    
                                                                    //im.setInterconnection(ic);
                                                                    SJSSCDSignalChannelMap.SetReqCreateLink();

                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                    channels.put(ochan.Name,ochan);
                                                                    //
                                                        } else {
                                                                
                                                            // partner CD is not present at any ss!
                                                            
                                                            output_Channel ochan;
                                                            //input_Channel inchan = new input_Channel();


                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setDistributed();
                                                                                    ochan.setInit();
                                                                                    ochan.setInterfaceManager(im);
                                                                                   
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                            
                                                            
                                                        }
                                                        
                                                            
                                                        }

                                              }

                                           //}

                                        //}

                                       // } 
                                                       

                                                    //}
                                                    cdins.setState("Active");
                                                    sc.addClockDomain(cdins);
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    SJSSCDSignalChannelMap.AddOneCDToLocalCurrSignalChannelMap(keyCDName,keyCurrSS , jsLocalCDs);
                                                    im.setChannelInstances(channels);
                                                    
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                                    
                                                    CDLCBuffer.AddCDMacroState(keyCDName, "Active");
                                
                                return vec;
            
        }
    
    
    //to start migrating CD in the new location, strong migration, channel communication status need to be retained
    public Vector AddMigrateSigChanObjAndInitModifySCIM(JSONObject jsLocalCDs, String OriginSS, String keyCurrSS,String keyCDName, ClockDomain cdins, Hashtable AllCDs, String MigType,InterfaceManager im, Scheduler sc) throws JSONException, Exception
        {
            
                    Vector vec = new Vector();
            
                    Hashtable channels = im.getAllChannelInstances();
            
                                    im.addCDLocation(keyCurrSS, keyCDName);
                                                
                                                    //ClockDomain cdins = SJSSCDSignalChannelMap.GetCDInstancesFromMap(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                    //Enumeration keysSigsChans = jsSigsChans.keys();

                                                    //while (keysSigsChans.hasMoreElements()){

                                                        //String tagSigsChans = keysSigsChans.nextElement().toString();

                                                       // if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigs = jsSigsChans.getJSONObject("signals");

                                                            GenericSignalReceiver server = null;
                                                            GenericSignalSender client = null;
                                                            Hashtable config = new Hashtable();

                                                           // Enumeration keysSigsInOuts = jsSigs.keys();

                                                          //  while (keysSigsInOuts.hasMoreElements()){

                                                               // String keyInOut = keysSigsInOuts.nextElement().toString();

                                                              //  if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    JSONObject SigInputConfigs = jsSigsInputs.getJSONObject(SigsInputsName);

                                                                    Enumeration keysSigInputConfigs = SigInputConfigs.keys();

                                                                    while (keysSigInputConfigs.hasMoreElements()){

                                                                        String keySigInputConfig = keysSigInputConfigs.nextElement().toString();

                                                                        config.put(keySigInputConfig, SigInputConfigs.getString(keySigInputConfig));

                                                                    }

                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                                    
                                                                    if(SigInputConfigs.get("Name").toString().equals("SOSJDiscovery")){
                                                                        server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                                                    } else {
                                                                        server = (GenericSignalReceiver) Class.forName(SigInputConfigs.get("Class").toString()).newInstance();
                                                                    }
                                                                            
                                                                    
                                                            

                                                            server.cdname = keyCDName;
                                                            server.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setServer(server);
                                                            signal.setuphook();
                                                            signal.setInit();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                                //} 
                                                       //         else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSigsOutputs = jsSigs.getJSONObject("outputs");

                                                    Enumeration keysSigsOutputsName = jsSigsOutputs.keys();

                                                        while (keysSigsOutputsName.hasMoreElements()){

                                                            String SigsOutputsName = keysSigsOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSigsOutputs.getJSONObject(SigsOutputsName);

                                                            Enumeration keysSigOutputConfigs = SigOutputConfigs.keys();

                                                            while (keysSigOutputConfigs.hasMoreElements()){

                                                                String keySigOutputConfig = keysSigOutputConfigs.nextElement().toString();

                                                                config.put(keySigOutputConfig, SigOutputConfigs.getString(keySigOutputConfig));

                                                            }
                                                            
                                                            
                                                            if (SigOutputConfigs.get("Name").toString().equalsIgnoreCase("SendACK")){
                                         
                                                                client = (GenericSignalSender) Class.forName("systemj.signals.SOA.ServiceACKSender").newInstance();


                                                            } else {
                                                                client = (GenericSignalSender) Class.forName(SigOutputConfigs.getString("Class")).newInstance();
                                                            }
                                                            client.cdname = keyCDName;
                                                            client.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setClient(client);
                                                            signal.setInit();

                                                            //SignalObjBuffer.putOutputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsOutputsName);
                                                           // SignalObjBuffer.putOutputSignalGSSInstanceToMap((Object) client, keyCurrSS, keyCDName, SigsOutputsName);

                                                        }

                                                //}

                                            //}

                                       // } 
                                                        //else if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                                //while (keysSChansInOuts.hasMoreElements()){

                                                    //String keyInOut = keysSChansInOuts.nextElement().toString();

                                                    //if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");

                                                        Enumeration keysSChansInputsName = jsSChansInputs.keys();

                                                        while (keysSChansInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSChansInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                
                                                                if(SChansInputConfigs.getString("From").equalsIgnoreCase(".")){
                                                                    
                                                                     input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                        inchan.setInit();
                                                                                        inchan.setDistributed();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        //inchan.set_partner_smp_migration(ochan);
                                                                                        inchan.setInterfaceManager(im);
                                                                                       
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                    
                                                                } else {
                                                                    
                                                                
                                                                
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] pnames = pname.split("\\.");
                                                                

                                            // If the channel is local
                                                                System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS+ "and real location in IM: " +im.getCDLocation(pnames[0]));

                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){

                                                                        System.out.println("Distributed input channel is localized! of CD:  " +cdins.getName());
                                                                        
                                                                        ClockDomain partnercd=null;
                                                                        
                                                                        JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDOSChanMap = jsPartnerCDSChanMap.getJSONObject("outputs");
                                                                        String PartnerDest = jsPartnerCDOSChanMap.getString("To");
                                                                        
                                                                        // if cd in Active state, fetch from scheduler
                                                                        if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            input_Channel inchan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                inchan.setLocal();
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                inchan.setInterfaceManager(im);
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            ochan.setInit();
                                                                                            ochan.setLocal();
                                                                                            ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp_migration(inchan);
                                                                                        inchan.set_partner_smp_migration(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                       
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                                
                                                                                input_Channel inchan;
                                                                                
                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                inchan.setLocal();
                                                                               inchan.setInterfaceManager(im);
                                                          
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        ochan.setInit();
                                                                                        ochan.setLocal();
                                                                                       
                                                                                            ochan.setChannelCDState("Sleep");
                                                                                       
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp_migration(inchan);
                                                                                        inchan.set_partner_smp_migration(ochan);

                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name,inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                
                                                                            }
                                                                        
                                                                        


                                                                    } else if(SJServiceRegistry.IsCDFromRemoteSS(pnames[0], OriginSS) || (im.getCDLocation(pnames[0]).equals(OriginSS) )){
                                                                        
                                                                        //partner CD is in another SS! SOA helps to maintain the updated infor of the cd location in IM!
                                                                        
                                                                        input_Channel inchan;

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        inchan = (input_Channel)f.get(cdins);
                                                                        inchan.setInit();
                                                                        
                                                                       // if(inchan.IsChannelLocal()){
                                                                            //here
                                                                            if(MigType.equals("strong")){
                                                                                inchan.setDistributedStrongMigration();
                                                                            } else {
                                                                                inchan.setDistributed();
                                                                            }
                                                                            
                                                                            inchan.resetLocalPartnerStateAfterMigration();
                                                                            
                                                                            inchan.setChannelCDState("Active");
                                                                            
                                                                            inchan.Name = keyCDName+"."+cname;
                                                                            inchan.PartnerName = pname;

                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);

                                                                      //  if(!channels.containsKey(inchan.Name)){
                                                                            
                                                                        
                                                                            //Interconnection ic = im.getInterconnection();
                                                                            SJSSCDSignalChannelMap.AddChanLinkUserToSS(OriginSS, keyCDName, "input", SChansInputsName);
                                                                            
                                                                            System.out.println("CDLC SignalImpl,Input channel status of channel:" +SChansInputsName+", r_s:" +inchan.get_r_s()+ "and r_r: " +inchan.get_r_r());
                                                                            
                                                                            //im.setInterconnection(ic);
                                                                            
                                                                            //im.addCDLocation(OriginSS,pnames[0]);
                                                                            
                                                                            inchan.setInterfaceManager(im);
                                                                            
                                                                            channels.put(inchan.Name, inchan);
                                                                            
                                                                            TCPIPLinkRegistry.AddSSToContact(OriginSS);
                                                                            
                                                                            SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                            
                                                                       // }
                                                                       
                                                                        // end
                                                                        
                                                                        
                                                                    } else if (!SJServiceRegistry.IsCDFromRemoteSS(pnames[0], OriginSS) || (!im.getCDLocation(pnames[0]).equals(OriginSS) && !im.getCDLocation(pnames[0]).equals(SJSSCDSignalChannelMap.getLocalSSName()) && !im.getCDLocation(pnames[0]).equalsIgnoreCase(""))){
                                                                        
                                                                        //partner is not in originSS
                                                                          
                                                                       
                                                                        
                                                                           // String originSS = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                            
                                                                            String originSS = im.getCDLocation(pnames[0]);
                                                                            
                                                                            if(!originSS.equals("")){
                                                                                
                                                                                input_Channel inchan;

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                
                                                                                if(MigType.equals("strong")){
                                                                                    inchan.setDistributedStrongMigration();
                                                                                    
                                                                                } else {
                                                                                    inchan.setDistributed();
                                                                                }
                                                                                
                                                                               System.out.println("CDLC SignalImpl,Input channel status of channel:" +SChansInputsName+", r_s:" +inchan.get_r_s()+ "and r_r: " +inchan.get_r_r());
                                                                                
                                                                                inchan.resetLocalPartnerStateAfterMigration();
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                //Interconnection ic = im.getInterconnection();
                                                                                SJSSCDSignalChannelMap.AddChanLinkUserToSS(originSS, keyCDName, "input", SChansInputsName);
                                                                                
                                                                                //im.setInterconnection(ic);
                                                                                //im.addCDLocation(originSS,pnames[0]);
                                                                                inchan.setInterfaceManager(im);  
                                                                                TCPIPLinkRegistry.AddSSToContact(originSS);
                                                                                
                                                                                inchan.TransmitCDLocChanges(SJSSCDSignalChannelMap.getLocalSSName());
                                                                                
                                                                                channels.put(inchan.Name, inchan);
                                                                                
                                                                                SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                                
                                                                            } else {
                                                                                
                                                                                String originSS2 = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                        
                                                                                if(!originSS2.equals("")){

                                                                                    im.addCDLocation(originSS2, pnames[0]);
                                                                                    
                                                                                    input_Channel inchan;

                                                                                    Field f = cdins.getClass().getField(cname);
                                                                                    inchan = (input_Channel)f.get(cdins);

                                                                                    if(MigType.equals("strong")){
                                                                                        inchan.setDistributedStrongMigration();

                                                                                    } else {
                                                                                        inchan.setDistributed();
                                                                                    }

                                                                                    System.out.println("CDLC SignalImpl,Input channel status of channel:" +SChansInputsName+", r_s:" +inchan.get_r_s()+ "and r_r: " +inchan.get_r_r());

                                                                                    inchan.resetLocalPartnerStateAfterMigration();
                                                                                    inchan.setInit();
                                                                                    inchan.Name = keyCDName+"."+cname;
                                                                                    inchan.PartnerName = pname;

                                                                                    //Interconnection ic = im.getInterconnection();
                                                                                    SJSSCDSignalChannelMap.AddChanLinkUserToSS(originSS, keyCDName, "input", SChansInputsName);

                                                                                    //im.setInterconnection(ic);
                                                                                    im.addCDLocation(originSS,pnames[0]);
                                                                                    inchan.setInterfaceManager(im);  
                                                                                    TCPIPLinkRegistry.AddSSToContact(originSS);

                                                                                    inchan.TransmitCDLocChanges(SJSSCDSignalChannelMap.getLocalSSName());
                                                                                    
                                                                                    channels.put(inchan.Name, inchan);

                                                                                    SJSSCDSignalChannelMap.SetReqCreateLink();

                                                                                }
                                                                                
                                                                            }
                                                                            
                                                                        
                                                                    }
                                                                        else{

                                                                       // cd ain't present anywhere , but program it distributed!
                                                                        
                                                                        input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                        inchan.setInit();
                                                                                        inchan.setDistributed();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        //inchan.set_partner_smp_migration(ochan);
                                                                                        inchan.setInterfaceManager(im);
                                                                                       
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                        
                                                                    }
                                                                }     
                                                         }

                                                //} 
                                                   // else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            
                                                            if(SigOutputConfigs.getString("To").equalsIgnoreCase(".")){
                                                                
                                                                Field f = cdins.getClass().getField(cname);

                                                                                  output_Channel ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setInit();
                                                                                    ochan.setDistributed();
                                                                                    //ochan.set_partner_smp_migration(inchan);
                                                                        ochan.setInterfaceManager(im);
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                                
                                                            } else {
                                                                
                                                            
                                                            
                                                            //String pname2 = SigOutputConfigs.getString("To").trim();
                                                            String[] pnames = pname.split("\\.");

                                                             
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    //ClockDomain partnercd = null;
                                                                JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDInSChanMap = jsPartnerCDSChanMap.getJSONObject("inputs");
                                                                        String PartnerDest = jsPartnerCDInSChanMap.getString("From");
                                                                     //System.out.println("Creating Output channel!");
                                                                // if cd in Active state, fetch from scheduler
                                                                    if (sc.SchedulerHasCD(pnames[0])){
                                                                        
                                                                        System.out.println("CDLC SignalImpl Migration, Localizing CD: " +cdins.getName());
                                                                        
                                                                            ClockDomain partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            output_Channel ochan;
                                                                

                                                                            Field f = cdins.getClass().getField(cname);

                                                                            ochan = (output_Channel)f.get(cdins);

                                                                                // Mine
                                                                            ochan.Name = keyCDName+"."+cname;
                                                                            ochan.PartnerName = pname;

                                                                            ochan.setChannelCDState("Active");
                                                                            ochan.setInit();
                                                                            ochan.setLocal();
                                                                            ochan.setInterfaceManager(im);
                                                                
                                                              
                                                                        
                                                                            input_Channel inchan;
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);

                                                                             if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                            
                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                            inchan.setInit();
                                                                            inchan.setLocal();
                                                                            inchan.setChannelCDState("Active");
                                                                       // } else {
                                                                       //     inchan.setChannelCDState("Sleep");
                                                                       // }
                                                                            inchan.Name = pname; 
                                                                            inchan.PartnerName = keyCDName+"."+cname;

                                                                            inchan.set_partner_smp_migration(ochan);
                                                                            ochan.set_partner_smp_migration(inchan);
                                                                        
                                                                        
                                                                        
                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);
                                                                        
                                                                   // }
                              
                                                                
                                                                    
                                                                    
                                                                //}

                                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                
                                                                
                                                               // if(!channels.containsKey(ochan.Name)){
                                                                    
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                ClockDomain partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                            
                                                                                output_Channel ochan;
                                                                

                                                                                Field f = cdins.getClass().getField(cname);

                                                                                ochan = (output_Channel)f.get(cdins);

                                                                                    // Mine
                                                                                ochan.Name = keyCDName+"."+cname;
                                                                                ochan.PartnerName = pname;

                                                                                ochan.setChannelCDState("Active");
                                                                                ochan.setInit();
                                                                                ochan.setLocal();
                                                                                ochan.setInterfaceManager(im);
                                                                               
                                                                               
                                                                        
                                                                        input_Channel inchan;
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        inchan = (input_Channel)f2.get(partnercd);

                                                                         if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                        
                                                                      
                                                                            inchan.setChannelCDState("Sleep");
                                                                            inchan.setInit();
                                                                            //inchan.setLocal();
                                                                       // }
                                                                        
                                                                        inchan.Name = pname; 
                                                                        inchan.PartnerName = keyCDName+"."+cname;
                                                                        
                                                                        inchan.set_partner_smp_migration(ochan);
                                                                        ochan.set_partner_smp_migration(inchan);
                                                                     
                                                                    
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                                
                                                                            }
                                       
                                                               
                                                        } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0]) || SJServiceRegistry.IsCDFromRemoteSS(pnames[0], OriginSS)){
                                                            
                                                            //distributed CD, partner CD is in other SS
                                                            
                                                                    output_Channel ochan;

                                                                    Field f = cdins.getClass().getField(cname);
                                                                    ochan = (output_Channel)f.get(cdins);
                                                                    
                                                                    
                                                                    ochan.Name = keyCDName+"."+cname;
                                                                    ochan.PartnerName = pname;
                                                                    
                                                                    if(ochan.IsChannelLocal()){
                                                                        
                                                                        if(MigType.equals("strong")){
                                                                           ochan.setDistributedStrongMigration();
                                                                        } else {
                                                                           ochan.setDistributed();
                                                                        }
                                                                        
                                                                        //ochan.setDistributed();
                                                                        
                                                                        ochan.setChannelCDState("Active");
                                                                        ochan.setInit();
                                                                        //Interconnection ic = im.getInterconnection();
                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(OriginSS, keyCDName, "output", SChansOutputsName);
                                                                        im.addCDLocation(OriginSS, pnames[0]);
                                                                        //im.setInterconnection(ic);
                                                                        
                                                                        ochan.setInterfaceManager(im);
                                                                        
                                                                        channels.put(ochan.Name, ochan);
                                                                        
                                                                        TCPIPLinkRegistry.AddSSToContact(OriginSS);

                                                                        SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                        
                                                                    }
                                                                    
                                                                    

                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                            
                                                                    //
                                                        } else {
                                                                
                                                            // partner CD is not present! assuming distributed!!
                                                            
                                                            
                                                                                //input_Channel inchan = new input_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                  output_Channel ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setInit();
                                                                                    ochan.setDistributed();
                                                                                    ochan.setInterfaceManager(im);
                                                                                   
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                            // end
                                                            
                                                        }
                                                                
                                                        
                                                            
                                                        }

                                              }

                                           //}

                                        //}

                                        //} 
                                                       

                                                    //}
                                                    cdins.setState("Active");
                                                    sc.addClockDomain(cdins);
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    SJSSCDSignalChannelMap.AddOneCDToLocalCurrSignalChannelMap(keyCDName,keyCurrSS , jsLocalCDs);
                                                    im.setChannelInstances(channels);
                                                    
                                                    JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
                                                        
                                                        Enumeration keysServNames = jsIntServ.keys();
                                                        
                                                        while(keysServNames.hasMoreElements()){
                                                            
                                                            String servName = keysServNames.nextElement().toString();
                                                            
                                                            JSONObject jsServDet = jsIntServ.getJSONObject(servName);
                                                            
                                                            String assocCDName = jsServDet.getString("associatedCDName");
                                                            
                                                            if(assocCDName.equals(cdins.getName())){
                                                                
                                                                //String servRole = jsServDet.getString("serviceRole");
                                                                
                                                                //if(servRole.equalsIgnoreCase("provider")){
                                                                    SOABuffer.setInitAdvVisibOneByOne(servName,"visible");
                                                               // }
                                                                
                                                            }
                                                            
                                                        }
                                                    CDLCBuffer.AddCDMacroState(keyCDName, "Active");
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                
                                return vec;
            
        }
    
    public Vector AddMigrateSigChanObjNotInitModifySCIM(JSONObject jsLocalCDs, String OriginSS, String keyCurrSS,String keyCDName, ClockDomain cdins, Hashtable AllCDs, String MigType,InterfaceManager im, Scheduler sc) throws JSONException, Exception{
            
            Vector vec = new Vector();
            
                                    im.addCDLocation(keyCurrSS, keyCDName);
                                                
                                    Hashtable channels = im.getAllChannelInstances();
                                                    //ClockDomain cdins = CDObjectsBuffer.GetCDInstancesFromMap(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                    //Enumeration keysSigsChans = jsSigsChans.keys();

                                                    //while (keysSigsChans.hasMoreElements()){

                                                       // String tagSigsChans = keysSigsChans.nextElement().toString();

                                                //        if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("signals");

                                                            GenericSignalReceiver server = null;
                                                            GenericSignalSender client = null;
                                                            Hashtable config = new Hashtable();

                                                            //Enumeration keysSigsInOuts = jsSigsChansInd.keys();

                                                 //           while (keysSigsInOuts.hasMoreElements()){

                                                                //String keyInOut = keysSigsInOuts.nextElement().toString();

                                                               // if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigsChansInd.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    JSONObject SigInputConfigs = jsSigsInputs.getJSONObject(SigsInputsName);

                                                                    Enumeration keysSigInputConfigs = SigInputConfigs.keys();

                                                                    while (keysSigInputConfigs.hasMoreElements()){

                                                                        String keySigInputConfig = keysSigInputConfigs.nextElement().toString();

                                                                        config.put(keySigInputConfig, SigInputConfigs.getString(keySigInputConfig));

                                                                    }

                                                                    
                                                                    if(config.get("Name").toString().equals("SOSJDiscovery")){
                                                                        server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                                                    } else {
                                                                        server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();
                                                                    }
                                                                    
                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                            //server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();

                                                            server.cdname = keyCDName;
                                                            server.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setServer(server);
                                                            //signal.setuphook();
                                                            signal.setInit();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                                //} 
                                                            //    else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSigsOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysSigsOutputsName = jsSigsOutputs.keys();

                                                        while (keysSigsOutputsName.hasMoreElements()){

                                                            String SigsOutputsName = keysSigsOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSigsOutputs.getJSONObject(SigsOutputsName);

                                                            Enumeration keysSigOutputConfigs = SigOutputConfigs.keys();

                                                            while (keysSigOutputConfigs.hasMoreElements()){

                                                                String keySigOutputConfig = keysSigOutputConfigs.nextElement().toString();

                                                                config.put(keySigOutputConfig, SigOutputConfigs.getString(keySigOutputConfig));

                                                            }

                                                            if (SigOutputConfigs.get("Name").toString().equalsIgnoreCase("SendACK")){
                                         
                                                                client = (GenericSignalSender) Class.forName("systemj.signals.SOA.ServiceACKSender").newInstance();


                                                            } else {
                                                                client = (GenericSignalSender) Class.forName(SigOutputConfigs.getString("Class")).newInstance();
                                                            }
                                                            
                                                            
                                                            client.cdname = keyCDName;
                                                            client.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setClient(client);
                                                            signal.setInit();

                                                            //SignalObjBuffer.putOutputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsOutputsName);
                                                            //SignalObjBuffer.putOutputSignalGSSInstanceToMap((Object) client, keyCurrSS, keyCDName, SigsOutputsName);

                                                        }

                                                //}

                                           // }

                                       // } 
                                                   //     else if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSChans = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSigsChansInd.keys();

                                               // while (keysSChansInOuts.hasMoreElements()){

                                                   // String keyInOut = keysSChansInOuts.nextElement().toString();

                                                   // if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSChans.getJSONObject("inputs");

                                                        Enumeration keysSChansInputsName = jsSChansInputs.keys();

                                                        while (keysSChansInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSChansInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                
                                                                if( SChansInputConfigs.getString("From").equalsIgnoreCase(".")){
                                                                    
                                                                    input_Channel inchan;
                                                                        output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Sleep");
                                                                                
                                                                                //Mine
                                                                                
                                                                                        inchan.setInit();
                                                                                        inchan.setDistributed();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        inchan.setInterfaceManager(im);
                                                                                        
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                    
                                                                } else {
                                                                    
                                                                
                                                                
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] pnames = pname.split("\\.");
                                                                

                                            // If the channel is local
                                                                //System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS);

                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){

                                                                        //System.out.println("Creating Input channel!");
                                                                        
                                                                        ClockDomain partnercd=null;
                                                                        
                                                                        JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDOSChanMap = jsPartnerCDSChanMap.getJSONObject("outputs");
                                                                        String PartnerDest = jsPartnerCDOSChanMap.getString("To");
                                                                        
                                                                        // if cd in Active state, fetch from scheduler
                                                                        if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            input_Channel inchan;
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                inchan.setLocal();
                                                                                inchan.setInit();
                                                                                inchan.setInterfaceManager(im);
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            ochan.setInit();
                                                                                            ochan.setLocal();
                                                                                            ochan.setChannelCDState("Sleep");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp_migration(inchan);
                                                                                        inchan.set_partner_smp_migration(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                                
                                                                                input_Channel inchan;
                                                                                
                                                                                

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Sleep");
                                                                                inchan.setLocal();
                                                                                inchan.setInterfaceManager(im);
                                                          
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        ochan.setInit();
                                                                                        ochan.setLocal();
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                      //      ochan.setInit();
                                                                                          //  ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                            ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp_migration(inchan);
                                                                                        inchan.set_partner_smp_migration(ochan);

                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                
                                                                            }
                                                                        
                                                                        

                                                                       

                                                                    } else if(SJServiceRegistry.IsCDFromRemoteSS(pnames[0], OriginSS) || (im.getCDLocation(pnames[0]).equals(OriginSS))){
                                                                        
                                                                        //partner CD is in another SS! SOA helps to maintain the updated infor of the cd location in IM!
                                                                        
                                                                         input_Channel inchan;

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        inchan = (input_Channel)f.get(cdins);
                                                                        
                                                                        
                                                                        if(inchan.IsChannelLocal()){
                                                                            
                                                                            if(MigType.equals("strong")){
                                                                                inchan.setDistributedStrongMigration();
                                                                            } else {
                                                                                inchan.setDistributed();
                                                                            }
                                                                            
                                                                            inchan.setInit();
                                                                             inchan.Name = keyCDName+"."+cname;
                                                                             inchan.PartnerName = pname;
                                                                             
                                                                            //inchan.setDistributedMigration();
                                                                            
                                                                            inchan.setChannelCDState("Sleep");
                                                                        
                                                                            inchan.resetLocalPartnerStateAfterMigration();
                                                                            
                                                                            //Interconnection ic = im.getInterconnection();
                                                                            SJSSCDSignalChannelMap.AddChanLinkUserToSS(OriginSS, keyCDName, "input", SChansInputsName);
                                                                            im.addCDLocation(OriginSS, pnames[0]);
                                                                            inchan.setInterfaceManager(im);
                                                                            channels.put(inchan.Name, inchan);
                                                                            TCPIPLinkRegistry.AddSSToContact(OriginSS);
                                                                            SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                            
                                                                            //DynamicLinkManager dyLM = new DynamicLinkManager();
                                                                            
                                                                            //im = dyLM.ExecuteDynamicLinkManager(im);
                                                                            
                                                                        } 
                                                                       
                                                                        
                                                                        
                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);

                                                                      //  if(!channels.containsKey(inchan.Name)){
                                                                            //channels.put(inchan.Name, inchan);
                                                                        
                                                                        // end
                                                                        
                                                                        
                                                                    } else if(!SJServiceRegistry.IsCDFromRemoteSS(pnames[0], OriginSS) || (!im.getCDLocation(pnames[0]).equals(OriginSS) && im.getCDLocation(pnames[0]).equals(SJSSCDSignalChannelMap.getLocalSSName()) && !im.getCDLocation(pnames[0]).equals(""))){
                                                                        
                                                                        //partner is not in originSS
                                                                          
                                                                           // String originSS = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                            
                                                                            String originSS = im.getCDLocation(pnames[0]);
                                                                        
                                                                            if(!originSS.equals("")){
                                                                                
                                                                                input_Channel inchan;

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                
                                                                                if(MigType.equals("strong")){
                                                                                    inchan.setDistributedStrongMigration();
                                                                                    
                                                                                } else {
                                                                                    inchan.setDistributed();
                                                                                }
                                                                                
                                                                                System.out.println("CDLC SignalImpl,Input channel status of channel:" +SChansInputsName+", r_s:" +inchan.get_r_s()+ "and r_r: " +inchan.get_r_r());
                                                                                
                                                                                inchan.resetLocalPartnerStateAfterMigration();
                                                                                
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                inchan.setInit();
                                                                                //Interconnection ic = im.getInterconnection();
                                                                                SJSSCDSignalChannelMap.AddChanLinkUserToSS(originSS, keyCDName, "input", SChansInputsName);
                                                                                
                                                                                //im.setInterconnection(ic);
                                                                                im.addCDLocation(originSS,pnames[0]);
                                                                                inchan.setInterfaceManager(im);  
                                                                                TCPIPLinkRegistry.AddSSToContact(originSS);
                                                                                
                                                                                SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                                
                                                                                inchan.TransmitCDLocChanges(SJSSCDSignalChannelMap.getLocalSSName());
                                                                                
                                                                                channels.put(inchan.Name, inchan);
                                                                                
                                                                                
                                                                                
                                                                            } else {
                                                                                
                                                                                String originSS2 = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                        
                                                                                if(!originSS2.equals("")){

                                                                                    input_Channel inchan;

                                                                                    Field f = cdins.getClass().getField(cname);
                                                                                    inchan = (input_Channel)f.get(cdins);

                                                                                    if(MigType.equals("strong")){
                                                                                        inchan.setDistributedStrongMigration();

                                                                                    } else {
                                                                                        inchan.setDistributed();
                                                                                    }

                                                                                    System.out.println("CDLC SignalImpl,Input channel status of channel:" +SChansInputsName+", r_s:" +inchan.get_r_s()+ "and r_r: " +inchan.get_r_r());
                                                                                    inchan.setInit();
                                                                                    inchan.resetLocalPartnerStateAfterMigration();

                                                                                    inchan.Name = keyCDName+"."+cname;
                                                                                    inchan.PartnerName = pname;

                                                                                    //Interconnection ic = im.getInterconnection();
                                                                                    SJSSCDSignalChannelMap.AddChanLinkUserToSS(originSS, keyCDName, "input", SChansInputsName);

                                                                                    //im.setInterconnection(ic);
                                                                                    im.addCDLocation(originSS,pnames[0]);
                                                                                    inchan.setInterfaceManager(im);  
                                                                                    TCPIPLinkRegistry.AddSSToContact(originSS);

                                                                                    inchan.TransmitCDLocChanges(SJSSCDSignalChannelMap.getLocalSSName());
                                                                                    
                                                                                    channels.put(inchan.Name, inchan);

                                                                                    SJSSCDSignalChannelMap.SetReqCreateLink();

                                                                                }
                                                                                
                                                                            }
                                                                            
                                                                        
                                                                    }
                                                                        else{

                                                                       // cd ain't present anywhere , but programmed like local???
                                                                        
                                                                        input_Channel inchan;
                                                                        output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Sleep");
                                                                                
                                                                                //Mine
                                                                                
                                                                                        inchan.setInit();
                                                                                        inchan.setDistributed();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        inchan.setInterfaceManager(im);
                                                                                        
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                        //
                                                                         
                                                                    }
                                                              }
                                                         }

                                                //} 
                                                     //           else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            
                                                            if(SigOutputConfigs.getString("To").equalsIgnoreCase(".")){
                                                             
                                                            input_Channel inchan = new input_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                  output_Channel ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Sleep");
                                                                                    ochan.setInit();
                                                                                    ochan.setDistributed();
                                                                                    ochan.setInterfaceManager(im);
                                                                                   
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                            // end
                                                                
                                                            } else {
                                                                
                                                            
                                                            
                                                            //String pname2 = SigOutputConfigs.getString("To").trim();
                                                            String[] pnames = pname.split("\\.");

                                                            
                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    ClockDomain partnercd = null;
                                                                
                                                                    JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDInSChanMap = jsPartnerCDSChanMap.getJSONObject("inputs");
                                                                        String PartnerDest = jsPartnerCDInSChanMap.getString("From");
                                                                     //System.out.println("Creating Output channel!");
                                                                // if cd in Active state, fetch from scheduler
                                                                    if (sc.SchedulerHasCD(pnames[0])){
                                                                            partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            output_Channel ochan;
                                                                

                                                                            Field f = cdins.getClass().getField(cname);

                                                                            ochan = (output_Channel)f.get(cdins);

                                                                                // Mine
                                                                            ochan.Name = keyCDName+"."+cname;
                                                                            ochan.PartnerName = pname;

                                                                            ochan.setChannelCDState("Sleep");
                                                                            ochan.setInit();
                                                                            ochan.setLocal();
                                                                
                                                                //if(partnercd!=null){
                                                                    
                                                                   // if(channels.containsKey(pname)){
                                                                    
                                                                      //  input_Channel inchan = (input_Channel)channels.get(pname);
                                                                    
                                                                     //   inchan.set_partner_smp(ochan);
                                                                     //   ochan.set_partner_smp(inchan);
                                                                        

                                                                   // } else {
                                                                        
                                                                            input_Channel inchan;
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);
ochan.setInterfaceManager(im);
                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                            inchan.setInit();
                                                                            //inchan.setLocal();
                                                                            inchan.setChannelCDState("Sleep");
                                                                       // } else {
                                                                       //     inchan.setChannelCDState("Sleep");
                                                                       // }
                                                                            inchan.Name = pname; 
                                                                            inchan.PartnerName = keyCDName+"."+cname;

                                                                            inchan.set_partner_smp_migration(ochan);
                                                                            ochan.set_partner_smp_migration(inchan);
                                                                        
                                                                        
                                                                        
                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);
                                                                        
                                                                   // }
                              
                                                                
                                                                    
                                                                    
                                                                //}

                                                                //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                
                                                                
                                                               // if(!channels.containsKey(ochan.Name)){
                                                                    
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                            
                                                                        } else 
                                                                            
                                                                            // if cd in Idle state, fetch from CDObjBuffer
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                partnercd = (ClockDomain) CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                            
                                                                                output_Channel ochan;
                                                                

                                                                                Field f = cdins.getClass().getField(cname);

                                                                                ochan = (output_Channel)f.get(cdins);

                                                                                    // Mine
                                                                                ochan.Name = keyCDName+"."+cname;
                                                                                ochan.PartnerName = pname;

                                                                                ochan.setChannelCDState("Sleep");
                                                                                ochan.setInit();
                                                                                ochan.setLocal();
                                                                                
                                                                               ochan.setInterfaceManager(im);
                                                                              
                                                                        
                                                                        input_Channel inchan;
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        inchan = (input_Channel)f2.get(partnercd);

                                                                       
                                                                            inchan.setChannelCDState("Sleep");
                                                                            inchan.setInit();
                                                                            //inchan.setLocal();
                                                                       // }
                                                                        
                                                                        inchan.Name = pname; 
                                                                        inchan.PartnerName = keyCDName+"."+cname;
                                                                        
                                                                        inchan.set_partner_smp_migration(ochan);
                                                                        ochan.set_partner_smp_migration(inchan);
                                                                        
                                                                      
                                                                    
                                                                    channels.put(ochan.Name, ochan);
                                                                    channels.put(inchan.Name,inchan);
                                                                        
                                                                                
                                                                            }
                                       
                                                                
                                                                //    throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);
                                                                
                                                        } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0])){
                                                            
                                                            //distributed CD, partner CD is in other SS
                                                            
                                                                    output_Channel ochan;

                                                                    Field f = cdins.getClass().getField(cname);
                                                                    ochan = (output_Channel)f.get(cdins);
                                                                    ochan.setInit();
                                                                    
                                                                    if(ochan.IsChannelLocal()){
                                                                        
                                                                        ochan.Name = keyCDName+"."+cname;
                                                                        ochan.PartnerName = pname;
                                                                        
                                                                        if(MigType.equals("strong")){
                                                                            ochan.setDistributedStrongMigration();
                                                                        } else {
                                                                            ochan.setDistributed();
                                                                        }
                                                                        
                                                                        
                                                                        //ochan.setDistributedMigration();
                                                                        
                                                                        ochan.setChannelCDState("Sleep");

                                                                        //Interconnection ic = im.getInterconnection();
                                                                        SJSSCDSignalChannelMap.AddChanLinkUserToSS(OriginSS, keyCDName, "output", SChansOutputsName);
                                                                        //im.setInterconnection(ic);
                                                                        
                                                                        ochan.setInterfaceManager(im);
                                                                        
                                                                        channels.put(ochan.Name, ochan);
                                                                        
                                                                        TCPIPLinkRegistry.AddSSToContact(OriginSS);
                                                                        
                                                                        SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                        
                                                                    }

                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                            
                                                                    //
                                                        } else {
                                                                
                                                            // partner CD is not present! assuming its distributed
                                                            
                                                            
                                                            input_Channel inchan = new input_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                  output_Channel ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;

                                                                                    ochan.setChannelCDState("Sleep");
                                                                                    ochan.setInit();
                                                                                    ochan.setDistributed();
                                                                                    ochan.setInterfaceManager(im);
                                                                                    
                                                                    
                                                                                    channels.put(ochan.Name, ochan);
                                                            
                                                            // end
                                                            
                                                        }
                                                                
                                                        

                                                        }

                                                     }
                                               // }
                                        //}

                                        //} 
                                       
                                                    //}
                                                    cdins.setState("Sleep");
                                                    CDObjectsBuffer.AddCDInstancesToBuffer(keyCDName, cdins);
                                                    SJSSCDSignalChannelMap.AddOneCDToLocalCurrSignalChannelMap(keyCDName,keyCurrSS , jsLocalCDs);
                                                    
                                                    JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
                                                        
                                                        Enumeration keysServNames = jsIntServ.keys();
                                                        
                                                        while(keysServNames.hasMoreElements()){
                                                            
                                                            String servName = keysServNames.nextElement().toString();
                                                            
                                                            JSONObject jsServDet = jsIntServ.getJSONObject(servName);
                                                            
                                                            String assocCDName = jsServDet.getString("associatedCDName");
                                                            
                                                            if(assocCDName.equals(cdins.getName())){
                                                                
                                                                //String servRole = jsIntServ.getString("serviceRole");
                                                                
                                                                //if(servRole.equalsIgnoreCase("provider")){
                                                                    SOABuffer.setInitAdvVisibOneByOne(servName,"invisible");
                                                                //}
                                                                
                                                            }
                                                            
                                                        }

                                                        
                                                        
                                                        
                                                    //sc.addClockDomain(cdins);
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    //CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                                    im.setChannelInstances(channels);
                                                    
                                vec.addElement(im);
                                vec.addElement(sc);
                                
                                CDLCBuffer.AddCDMacroState(keyCDName, "Sleep");
                                
                                return vec;
            
        }
    
        public Vector ReactivateSigChanObjAndInitModifySCIM(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, InterfaceManager im, Scheduler sc) throws JSONException, Exception
        {
            
            //long startTime = System.currentTimeMillis();
            
            Vector vec = new Vector();
            
                                    //im.addCDLocation(keyCurrSS, keyCDName);
                                    Hashtable channels = im.getAllChannelInstances();
                                                
                                                    ClockDomain cdins = CDObjectsBuffer.GetCDInstancesFromBuffer(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                    //Enumeration keysSigsChans = jsSigsChans.keys();

                                                    //while (keysSigsChans.hasMoreElements()){

                                                        //String tagSigsChans = keysSigsChans.nextElement().toString();

                                                       // if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigs = jsSigsChans.getJSONObject("signals");

                                                            GenericSignalReceiver server = null;
                                                            //GenericSignalSender client = null;
                                                            

                                                           // Enumeration keysSigsInOuts = jsSigs.keys();

                                                           // while (keysSigsInOuts.hasMoreElements()){

                                                                //String keyInOut = keysSigsInOuts.nextElement().toString();

                                                              //  if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    
                                                                    
                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    Hashtable config = new Hashtable();
                                                                    
                                                                    JSONObject SigInputConfigs = jsSigsInputs.getJSONObject(SigsInputsName);

                                                                    Enumeration keysSigInputConfigs = SigInputConfigs.keys();

                                                                    while (keysSigInputConfigs.hasMoreElements()){

                                                                        String keySigInputConfig = keysSigInputConfigs.nextElement().toString();

                                                                        config.put(keySigInputConfig, SigInputConfigs.getString(keySigInputConfig));

                                                                    }
                                                                    

                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                                    
                                                                    if(config.get("Name").toString().equals("SOSJDiscovery")){
                                                                       server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                                                    } else {
                                                                       server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();
                                                                    }
                                                                    
                                                            //server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();

                                                            //Object objGSR = SignalObjBuffer.getInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                            
                                                            //    server = (GenericSignalReceiver) objGSR;
        
                                                            server.cdname = keyCDName;
                                                            server.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            
                                                            //signal.wakeupInputSignalThread();
                                                            
                                                            
                                                            signal.setServer(server);
                                                            signal.setuphook();
                                                            //signal.setInit();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                                

                                                //}

                                            //}

                                        //} 
                                                    //    else if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSChans = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSChans.keys();

                                              //  while (keysSChansInOuts.hasMoreElements()){

                                                    //String keyInOut = keysSChansInOuts.nextElement().toString();

                                                   // if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSChans.getJSONObject("inputs");

                                                        Enumeration keysSChansInputsName = jsSChansInputs.keys();

                                                        while (keysSChansInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSChansInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                String pname2 = SChansInputConfigs.getString("From").trim();
                                                                String[] pnames = pname2.split("\\.");

                                            // If the channel is local
                                                                System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS);

                                                              //  if(!channels.containsKey(keyCDName+"."+cname)){
                                                                    
                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){

                                                                        //ClockDomain partnercd = null;
                                                                        
                                                                        if(sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                            //partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            //input_Channel inchan = (input_Channel)channels.get(keyCDName+"."+cname);
                                                                            Field f = cdins.getClass().getField(cname);
                                                                            input_Channel inchan = (input_Channel)f.get(cdins);
                                                                                //inchan.setInit();
                                                                                inchan.setChannelCDState("Active");
                                                                                //output_Channel ochan = inchan.get_partner_smp();
                                                                                //ochan.setInit();
                                                                                //ochan.set_partner_smp(inchan);
                                                                                //inchan.set_partner_smp(ochan);
                                                                                //channels.put(inchan.Name, inchan);
                                                                                //channels.put(ochan.Name, ochan);
                                                                            
                                                                        } else 
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                //partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);
                                                                                
                                                                                //input_Channel inchan = (input_Channel)channels.get(keyCDName+"."+cname);
                                                                                Field f = cdins.getClass().getField(cname);
                                                                                input_Channel inchan = (input_Channel)f.get(cdins);
                                                                                //inchan.setInit();
                                                                                inchan.setChannelCDState("Active");
                                                                               
                                                                                
                                                                            }
                                                                         
                                                                        
                                                                    }
                                                                        else{

                                                                        //if(!channels.containsKey(keyCDName+"."+cname)){
                                                                            
                                                                            //input_Channel inchan = (input_Channel) channels.get(keyCDName+"."+cname);
                                                                            
                                                                        //}
                                                                            Field f = cdins.getClass().getField(cname);
                                                                            input_Channel inchan = (input_Channel)f.get(cdins);
                                                                                //inchan.setInit();
                                                                                inchan.setChannelCDState("Active");
                                                                        

                                                                      
                                                                    }
                                                                    
                                                                    
                                                                    
                                                               // }
                                                                
                                                                

                                                         }

                                               // } 
                                                    //else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSChans.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            String pname2 = SigOutputConfigs.getString("To").trim();
                                                            String[] pnames = pname.split("\\.");

                                                            //System.out.println("SigRec, OChan, cd location of: "+pnames[0]+"is " +keyCurrSS);
                                    // If the channel is local

                                                            if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    //ClockDomain partnercd=null;
                                                                    
                                                                    if(sc.SchedulerHasCD(pnames[0])){
                                                                          
                                                                        Field f = cdins.getClass().getField(cname);

                                                                        output_Channel ochan = (output_Channel)f.get(cdins);
                                                                            //partnercd = sc.getClockDomain(pnames[0]);
                                                                        //output_Channel ochan = (output_Channel) channels.get(keyCDName+"."+cname);
                                                                        //ochan.setInit();
                                                                        ochan.setChannelCDState("Active");
                                                                    //channels.put(keyCDName+"."+cname, ochan);
                                                                            //= (ClockDomain)CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);

                                                                    //System.out.println("SigRec, OutChan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                    
                                                                   // if(partnercd!=null){
                                                                        
                                                                        //input_Channel inchan = ochan.get_partner_smp(); //(input_Channel)channels.get(pname);
                                                                        //inchan.setInit();
                                                                        //ochan.setInit();
                                                                        
                                                                        //inchan.set_partner_smp(ochan);
                                                                        //ochan.set_partner_smp(inchan);
                                                                        //channels.put(pname,inchan);
                                                                        
                                                                    //}
                                                                    
                                                                    
                                                                        //if(!channels.containsKey(ochan.Name)){

                                                                        //channels.put(ochan.Name, ochan);
                                                                        //channels.put(inchan.Name,inchan);
                                                                        
                                                                            
                                                                        } else 
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                //partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);
                                                                                
                                                                                //output_Channel ochan = (output_Channel) channels.get(keyCDName+"."+cname);
                                                                    
                                                                    //channels.put(keyCDName+"."+cname, ochan);
                                                                            //= (ClockDomain)CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);

                                                                    //System.out.println("SigRec, OutChan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                    
                                                                   // if(partnercd!=null){
                                                                            Field f = cdins.getClass().getField(cname);
                                                                            output_Channel ochan = (output_Channel)f.get(cdins);
                                                                            //input_Channel inchan = ochan.get_partner_smp(); //(input_Channel)channels.get(pname);
                                                                            //inchan.setInit();
                                                                            //ochan.setInit();
                                                                            ochan.setChannelCDState("Active");
                                                                            
                                                                            //inchan.set_partner_smp(ochan);
                                                                            //ochan.set_partner_smp(inchan);
                                                                            
                                                                            //channels.put(pname,inchan);

                                                                        //}


                                                                            //if(!channels.containsKey(ochan.Name)){

                                                                            //channels.put(ochan.Name, ochan);
                                                                            //channels.put(inchan.Name,inchan);
                                                                                
                                                                            }
                                                                    
                                                                    

                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);
                                                            }
                                                               else{
                                                                    //output_Channel ochan = (output_Channel) channels.get(keyCDName+"."+cname);

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        output_Channel ochan = (output_Channel)f.get(cdins);
                                                                           // Field f = cdins.getClass().getField(cname);
                                                                           // ochan = (output_Channel)f.get(cdins);
                                                                            //ochan.setInit();
                                                                            ochan.setChannelCDState("Active");

                                                                           // ochan.Name = keyCDName+"."+cname;
                                                                            //ochan.PartnerName = pname;
                                                                            //ochan.setDistributed();
                                                                            //ochan.setInterfaceManager(im);

                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);

                                                                          //  if(!channels.containsKey(ochan.Name)){
                                                                    
                                                                                //channels.put(ochan.Name, ochan);

                                                                           // }
                                                                           // else
                                                                            //    throw new RuntimeException("Tried to initialize the same channel twice : "+ochan.Name);
                                                                            
                                                                    }

                                              }

                                           //}

                                       // }

                                        //} 
                                                       
                                                    cdins.setState("Active");
                                                    
                                                    //if(cdins.IsTimedSleep()){
                                                    //    cdins.resetSleepTimeState();
                                                   // }
                                                    
                                                    sc.addClockDomain(cdins);
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    im.setChannelInstances(channels);
                                                    CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                                    
                                                    
                                                    
                                                    vec.addElement(im);
                                                    vec.addElement(sc);
                                CDLCBuffer.AddCDMacroState(keyCDName, "Active");
                                
                                
                                
                                return vec;
            
        }
        
        public Vector AddSigChanObjNotInitModifySCIM(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, ClockDomain cdins, Hashtable AllCDs, InterfaceManager im, Scheduler sc) throws JSONException, Exception{
            
            Vector vec = new Vector();
            
                                    im.addCDLocation(keyCurrSS, keyCDName);
                                                
                                    Hashtable channels = im.getAllChannelInstances();
                                                    //ClockDomain cdins = CDObjectsBuffer.GetCDInstancesFromMap(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                    //Enumeration keysSigsChans = jsSigsChans.keys();

                                                 //   while (keysSigsChans.hasMoreElements()){

                                                       // String tagSigsChans = keysSigsChans.nextElement().toString();

                                                 //       if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigs = jsSigsChans.getJSONObject("signals");

                                                            GenericSignalReceiver server = null;
                                                            GenericSignalSender client = null;
                                                            Hashtable config = new Hashtable();

                                                            //Enumeration keysSigsInOuts = jsSigs.keys();

                                                      //      while (keysSigsInOuts.hasMoreElements()){

                                                                //String keyInOut = keysSigsInOuts.nextElement().toString();

                                                    //            if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    JSONObject SigInputConfigs = jsSigsInputs.getJSONObject(SigsInputsName);

                                                                    Enumeration keysSigInputConfigs = SigInputConfigs.keys();

                                                                    while (keysSigInputConfigs.hasMoreElements()){

                                                                        String keySigInputConfig = keysSigInputConfigs.nextElement().toString();

                                                                        config.put(keySigInputConfig, SigInputConfigs.getString(keySigInputConfig));

                                                                    }

                                                                    if(config.get("Name").equals("SOSJDiscovery")){
                                                                        server = (GenericSignalReceiver) Class.forName("systemj.signals.SOA.ReceiveDisc").newInstance();
                                                                    } else {
                                                                        server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();
                                                                    }
                                                                    
                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                            //server = (GenericSignalReceiver) Class.forName(config.get("Class").toString()).newInstance();

                                                            server.cdname = keyCDName;
                                                            server.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setServer(server);
                                                            //signal.setuphook();
                                                            signal.setInit();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                             //   } 
                                                         //       else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSigsOutputs = jsSigs.getJSONObject("outputs");

                                                    Enumeration keysSigsOutputsName = jsSigsOutputs.keys();

                                                        while (keysSigsOutputsName.hasMoreElements()){

                                                            String SigsOutputsName = keysSigsOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSigsOutputs.getJSONObject(SigsOutputsName);

                                                            Enumeration keysSigOutputConfigs = SigOutputConfigs.keys();

                                                            while (keysSigOutputConfigs.hasMoreElements()){

                                                                String keySigOutputConfig = keysSigOutputConfigs.nextElement().toString();

                                                                config.put(keySigOutputConfig, SigOutputConfigs.getString(keySigOutputConfig));

                                                            }

                                                            if (SigOutputConfigs.get("Name").toString().equalsIgnoreCase("SendACK")){
                                         
                                                                client = (GenericSignalSender) Class.forName("systemj.signals.SOA.ServiceACKSender").newInstance();


                                                            } else {
                                                                client = (GenericSignalSender) Class.forName(SigOutputConfigs.getString("Class")).newInstance();
                                                            }
                                                            
                                                            client.cdname = keyCDName;
                                                            client.configure(config);

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.setClient(client);
                                                            signal.setInit();

                                                            //SignalObjBuffer.putOutputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsOutputsName);
                                                           // SignalObjBuffer.putOutputSignalGSSInstanceToMap((Object) client, keyCurrSS, keyCDName, SigsOutputsName);

                                                        }

                                                //}

                                            //}

                                     //   } 
                                                     //   else if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                            JSONObject jsSChans = jsSigsChans.getJSONObject("SChannels");

                                                //Enumeration keysSChansInOuts = jsSChans.keys();

                                               // while (keysSChansInOuts.hasMoreElements()){

                                                    //String keyInOut = keysSChansInOuts.nextElement().toString();

                                                   // if (keyInOut.equalsIgnoreCase("inputs")){

                                                        JSONObject jsSChansInputs = jsSChans.getJSONObject("inputs");

                                                        Enumeration keysSChansInputsName = jsSChansInputs.keys();

                                                        while (keysSChansInputsName.hasMoreElements()){

                                                            String SChansInputsName = keysSChansInputsName.nextElement().toString();

                                                            JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);

                                                                String cname = SChansInputsName.trim()+"_in";
                                                                String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                                //String pname2 = SChansInputConfigs.getString("From").trim();
                                                                
                                                                if(SChansInputConfigs.getString("From").equalsIgnoreCase(".")){
                                                                    
                                                                    input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Sleep");
                                                                                
                                                                                //Mine
                                                                                        inchan.setDistributed();
                                                                                        inchan.setInit();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        inchan.setInterfaceManager(im);
                                                                                        
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                    
                                                                } else {
                                                                    
                                                                
                                                                
                                                                String[] pnames = pname.split("\\.");

                                            // If the channel is local
                                                                //System.out.println("SigRec, InChan cd location of: "+pnames[0]+"is " +keyCurrSS);

                                                              //  if(!channels.containsKey(keyCDName+"."+cname)){
                                                                    
                                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0])) || AllCDs.containsKey(pnames[0])){

                                                                        ClockDomain partnercd = null;
                                                                        
                                                                        JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDOSChanMap = jsPartnerCDSChanMap.getJSONObject("outputs");
                                                                        String PartnerDest = jsPartnerCDOSChanMap.getString("To");
                                                                        
                                                                        if(sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                          // partnercd = sc.getClockDomain(pnames[0]);
                                                                            
                                                                            input_Channel inchan; //= (input_Channel)channels.get(cname);
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Sleep");
                                                                                
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                            inchan.setInterfaceManager(im);
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                            ochan.setInit();
                                                                                            ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                            //ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);
                                                    
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                            
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                             //   if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                        } else 
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                               // partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);
                                                                                
                                                                                input_Channel inchan; //= (input_Channel)channels.get(cname);
                                                                            

                                                                                Field f = cdins.getClass().getField(cname);
                                                                                inchan = (input_Channel)f.get(cdins);
                                                                                inchan.setChannelCDState("Sleep");
                                                                                
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                inchan.setInterfaceManager(im);
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            //ochan.setInit();
                                                                                            //ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        ochan.setInit();
                                                                                            ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);
                                                                                        

                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                             //   if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                            } else {
                                                                                
                                                                                   if(AllCDs.containsKey(pnames[0])){
                                                                                       
                                                                                       partnercd = (ClockDomain) AllCDs.get(pnames[0]);
                                                                            
                                                                                    input_Channel inchan;
                                                                            

                                                                                    Field f = cdins.getClass().getField(cname);
                                                                                    inchan = (input_Channel)f.get(cdins);
                                                                                    inchan.setChannelCDState("Active");
                                                                                
                                                                                //Mine
                                                                                
                                                                                inchan.setInit();
                                                                                inchan.Name = keyCDName+"."+cname;
                                                                                inchan.PartnerName = pname;
                                                                                inchan.setInterfaceManager(im);
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        
                                                                                        if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansInputsName;
                                                                                            jsPartnerCDOSChanMap.put("To", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("outputs", jsPartnerCDOSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                        
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            ochan.setInit();
                                                                                            ochan.setChannelCDState("Active");
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        ochan.Name = pname;
                                                                                        ochan.PartnerName = keyCDName+"."+cname;

                                                                                        ochan.set_partner_smp(inchan);
                                                                                        inchan.set_partner_smp(ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                                
                                                                                
                                                                              //  if(!channels.containsKey(inchan.Name) ){
                                                                                        channels.put(inchan.Name, inchan);
                                                                                        channels.put(ochan.Name,ochan);
                                                                                        
                                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "output", pnames[1],(Object) ochan);
                                                                                       
                                                                                   }
                                                                                   
                                                                            }
                                                                        
                                                                         
                                                                                

                                                                    } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0])|| SJServiceRegistry.HasNonLocalServiceCD(pnames[0])){
                                                                        
                                                                        //distributed CD
                                                                        
                                                                        input_Channel inchan;

                                                                        Field f = cdins.getClass().getField(cname);
                                                                        inchan = (input_Channel)f.get(cdins);
                                                                        //inchan.setInit();
                                                                        inchan.setDistributed();
                                                                        
                                                                        inchan.setChannelCDState("Sleep");
                                                                        
                                                                        String partnerSSLoc;
                                                                        
                                                                        if(im.hasCDLocation(pnames[0])){
                                                                            partnerSSLoc = im.getCDLocation(pnames[0]);
                                                                        } else {
                                                                            partnerSSLoc = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                        }
                                                                        
                                                                        //String partnerSSLoc = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                        
                                                                        //Interconnection ic = im.getInterconnection();
                                                                        
                                                                        if(!partnerSSLoc.equals("")){
                                                                            SJSSCDSignalChannelMap.AddChanLinkUserToSS(partnerSSLoc, keyCDName, "input", SChansInputsName);
                                                                            TCPIPLinkRegistry.AddSSToContact(partnerSSLoc);
                                                                            
                                                                            SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                        }
                                                                        inchan.setInterfaceManager(im);
                                                                        channels.put(inchan.Name, inchan);
                                                                        //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName,(Object) inchan);
                                                                        
                                                                        
                                                                    }
                                                                        else{
                                                                        //cd not presenet anywhere
                                                                        input_Channel inchan;
                                                                        //output_Channel ochan = new output_Channel();

                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        inchan = (input_Channel)f.get(cdins);
                                                                                        inchan.setChannelCDState("Sleep");
                                                                                
                                                                                //Mine
                                                                                        inchan.setDistributed();
                                                                                        inchan.setInit();
                                                                                        inchan.Name = keyCDName+"."+cname;
                                                                                        inchan.PartnerName = pname;
                                                                                        //inchan.set_partner_smp(ochan);
                                                                                        inchan.setInterfaceManager(im);
                                                                                
                                                                                       
                                                                                        channels.put(inchan.Name, inchan);
                                                                        
                                                                    }
                                                                    
                                                               // }
                                                                
                                                              }

                                                         }

                                                //} 
                                                   // else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSChansOutputs = jsSChans.getJSONObject("outputs");

                                                    Enumeration keysChansOutputsName = jsSChansOutputs.keys();

                                                        while (keysChansOutputsName.hasMoreElements()){

                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();

                                                            JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);

                                                            String cname = SChansOutputsName.trim()+"_o";
                                                            String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                            String pname2 = SigOutputConfigs.getString("To").trim();
                                                            
                                                            if(SigOutputConfigs.getString("To").equalsIgnoreCase(".")){
                                                                
                                                                output_Channel ochan;
                                                                //input_Channel inchan = new input_Channel();


                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;
                                                                                    ochan.setInterfaceManager(im);
                                                                                    ochan.setDistributed();
                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setInit();
                                                                                    channels.put(ochan.Name,ochan);
                                                                
                                                            } else {
                                                                
                                                            
                                                            
                                                            String[] pnames = pname.split("\\.");

                                                            //System.out.println("SigRec, OChan, cd location of: "+pnames[0]+"is " +keyCurrSS);
                                    // If the channel is local

                                                           // if(!channels.containsKey(keyCDName+"."+cname)){
                                                                
                                                                if(keyCurrSS.equals(im.getCDLocation(pnames[0])) || AllCDs.containsKey(pnames[0])){
                                                                    ClockDomain partnercd = null;
                                                                    
                                                                    JSONObject jsPartnerCDMap = jsLocalCDs.getJSONObject(pnames[0]);
                                                                        JSONObject jsPartnerCDSChanMap = jsPartnerCDMap.getJSONObject("SChannels");
                                                                        JSONObject jsPartnerCDInSChanMap = jsPartnerCDSChanMap.getJSONObject("inputs");
                                                                        String PartnerDest = jsPartnerCDInSChanMap.getString("From");
                                                                    
                                                                    if(sc.SchedulerHasCD(pnames[0])){
                                                                            
                                                                        output_Channel ochan;
                                                                        Field f = cdins.getClass().getField(cname);
                                                                        ochan = (output_Channel)f.get(cdins);

                                                                        ochan.Name = keyCDName+"."+cname;
                                                                        ochan.PartnerName = pname;
                                                                        ochan.setChannelCDState("Sleep");
                                                                    // Partner
                                                                    
ochan.setInterfaceManager(im);
                                                                  
                                                                           //partnercd = sc.getClockDomain(pnames[0]);
                                                                            input_Channel inchan ; //= (input_Channel)channels.get(pname)
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);
                                                                            
                                                                            if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                            
                                                                            inchan.Name = pname; 
                                                                            inchan.PartnerName = keyCDName+"."+cname;
                                                                            inchan.setInit();
                                                                            //inchan.setInterfaceManager(im);
                                                                            inchan.setChannelCDState("Active");
                                                                            inchan.set_partner_smp(ochan);
                                                                            ochan.set_partner_smp(inchan);
                                                                            
                                                                            channels.put(ochan.Name, ochan);
                                                                            channels.put(inchan.Name,inchan);
                                                                            
                                                                            
                                                                            
                                                                        } else 
                                                                            if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                                
                                                                                //partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);
                                                                              
                                                                                output_Channel ochan;
                                                                            Field f = cdins.getClass().getField(cname);
                                                                            ochan = (output_Channel)f.get(cdins);

                                                                            ochan.Name = keyCDName+"."+cname;
                                                                            ochan.PartnerName = pname;
                                                                            ochan.setChannelCDState("Sleep");
                                                                                ochan.setInterfaceManager(im);
                                                                            input_Channel inchan ; //= (input_Channel)channels.get(pname)
                                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            inchan = (input_Channel)f2.get(partnercd);
                                                                            
                                                                            if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                            
                                                                            inchan.setInit();
                                                                            inchan.setChannelCDState("Sleep");
                                                                            
                                                                            inchan.Name = pname; 
                                                                            inchan.PartnerName = keyCDName+"."+cname;
                                                                            
                                                                            inchan.set_partner_smp(ochan);
                                                                            ochan.set_partner_smp(inchan);
                                                                            
                                                                            channels.put(ochan.Name, ochan);
                                                                            channels.put(inchan.Name,inchan);
                                                                            
                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                            
                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);
                                                                            
                                                                            
                                                                                
                                                                            } else {
                                                                                
                                                                                if(AllCDs.containsKey(pnames[0])){
                                                                                    
                                                                                    partnercd = (ClockDomain)AllCDs.get(pnames[0]);
                                                                            
                                                                                    output_Channel ochan;
                                                                

                                                                                    Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;
ochan.setInterfaceManager(im);
                                                                                    ochan.setChannelCDState("Sleep");
                                                                                    ochan.setInit();
                                                                
                                                                //if(partnercd!=null){
                                                                    
                                                                   // if(channels.containsKey(pname)){
                                                                    
                                                                      //  input_Channel inchan = (input_Channel)channels.get(pname);
                                                                    
                                                                     //   inchan.set_partner_smp(ochan);
                                                                     //   ochan.set_partner_smp(inchan);
                                                                        

                                                                   // } else {
                                                                        
                                                                                    input_Channel inchan;
                                                                                    Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                    inchan = (input_Channel)f2.get(partnercd);

                                                                                    if(PartnerDest.equalsIgnoreCase(".")){
                                                                                            PartnerDest = keyCDName+"."+SChansOutputsName;
                                                                                            jsPartnerCDInSChanMap.put("From", PartnerDest);
                                                                                            jsPartnerCDSChanMap.put("inputs", jsPartnerCDInSChanMap);
                                                                                            jsPartnerCDMap.put("SChannels",jsPartnerCDSChanMap);
                                                                                            jsLocalCDs.put(pnames[0],jsPartnerCDMap);
                                                                                            JSONObject NewCurrMap = new JSONObject();
                                                                                            NewCurrMap.put(keyCurrSS, jsLocalCDs);
                                                                                            SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(NewCurrMap);
                                                                                        }
                                                                                    
                                                                                //if (sc.SchedulerHasCD(pnames[0])){
                                                                                    inchan.setInit();
                                                                                    inchan.setChannelCDState("Active");
                                                                               // } else {
                                                                               //     inchan.setChannelCDState("Sleep");
                                                                               // }
                                                                                    inchan.Name = pname; 
                                                                                    inchan.PartnerName = keyCDName+"."+cname;

                                                                                    inchan.set_partner_smp(ochan);
                                                                                    ochan.set_partner_smp(inchan);



                                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);

                                                                           // }




                                                                        //}

                                                                                    //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);

                                                                
                                                               // if(!channels.containsKey(ochan.Name)){
                                                                    
                                                                                channels.put(ochan.Name, ochan);
                                                                                channels.put(inchan.Name,inchan);
                                                                        
                                                                                    
                                                                                }
                                                                                
                                                                                 
                                                                                
                                                                                
                                                                            }
                                                                    
                                                                    
                                                                            

                                                                    //System.out.println("SigRec, OutChan found partnerCD:" +SJSSCDSignalChannelMap.getCDInstancesFromMap().get(pnames[0]));

                                                                    //PARTNER CHANNEL
                                                                    
                                                                    //if(partnercd!=null){
                                                                        
                                                                        //if(channels.containsKey(pname)){
                                                                           
                                                                           // input_Channel inchan = (input_Channel)channels.get(pname);
                                                                            
                                                                            //inchan.set_partner_smp(ochan);
                                                                           // ochan.set_partner_smp(inchan);
                                                                            
                                                                           // SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, pnames[0], "SChannel", "input", pnames[1],(Object) inchan);
                                                                            
                                                                        //} else {
                                                                            
                                                                            
                                                                            
                                                                        //}
                                                                        
                                                                        
                                                                    //}
                                                                            
                                                                    
                                                                    //input_Channel inchan;

                                                                            
                                                                            //Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                            
                                                                            //inchan = (input_Channel)f2.get(partnercd);
                                                                            //ochan.setInit();
                                                                            //inchan.setInit();

                                                                            // Mine
                                                                   
                                                                    
                                                            } else if(im.IsCDNameRegisteredInAnotherSS(pnames[0]) || SJServiceRegistry.HasNonLocalServiceCD(pnames[0])){
                                                                // Distributed CD
                                                                
                                                                output_Channel ochan;

                                                                            Field f = cdins.getClass().getField(cname);
                                                                            ochan = (output_Channel)f.get(cdins);
                                                                            //ochan.setInit();
                                                                            ochan.setChannelCDState("Sleep");
                                                                            ochan.setDistributed();
                                                                            ochan.Name = keyCDName+"."+cname;
                                                                            ochan.PartnerName = pname;
                                                                            //ochan.setDistributed();
                                                                            //ochan.setInterfaceManager(im);
                                                                            channels.put(ochan.Name, ochan);
                                                                            
                                                                            String partnerSSLoc;// = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                            
                                                                            if(im.hasCDLocation(pnames[0])){
                                                                                partnerSSLoc = im.getCDLocation(pnames[0]);
                                                                            } else {
                                                                                partnerSSLoc = SJServiceRegistry.GetCDRemoteSSLocation(pnames[0]);
                                                                            }
                                                                            
                                                                            //Interconnection ic = im.getInterconnection();
                                                                            
                                                                            SJSSCDSignalChannelMap.AddChanLinkUserToSS(partnerSSLoc, keyCDName, "output", SChansOutputsName);
                                                                            
                                                                            //im.setInterconnection(ic);
                                                                            
                                                                            SJSSCDSignalChannelMap.SetReqCreateLink();
                                                                            //SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName,(Object) ochan);
                                                                            ochan.setInterfaceManager(im);
                                                                            //
                                                                
                                                                
                                                            }
                                                               else{
                                                                    
                                                                //partner cd is not present anywhere else
                                                                
                                                                output_Channel ochan;
                                                                //input_Channel inchan = new input_Channel();


                                                                                        Field f = cdins.getClass().getField(cname);

                                                                                    ochan = (output_Channel)f.get(cdins);

                                                                                        // Mine
                                                                                    ochan.Name = keyCDName+"."+cname;
                                                                                    ochan.PartnerName = pname;
                                                                                    
                                                                                    ochan.setDistributed();
                                                                                    ochan.setChannelCDState("Active");
                                                                                    ochan.setInit();
                                                                                    channels.put(ochan.Name,ochan);
                                                                                    ochan.setInterfaceManager(im);
                                                                
                                                                    }
                                                                
                                                            //}
                                                            
                                                        }   

                                              }

                                         
                                                    cdins.setState("Sleep");
                                                    CDObjectsBuffer.AddCDInstancesToBuffer(keyCDName, cdins);
                                                    SJSSCDSignalChannelMap.AddOneCDToLocalCurrSignalChannelMap(keyCDName,keyCurrSS , jsLocalCDs);
                                                    //sc.addClockDomain(cdins);
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    //CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                                    im.setChannelInstances(channels);
                                                    
                                vec.addElement(im);
                                vec.addElement(sc);
                                CDLCBuffer.AddCDMacroState(keyCDName, "Sleep");
                                return vec;
            
        }
        
        public Vector disableSigChanObjModifySCIM(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, InterfaceManager im, Scheduler sc) throws JSONException, Exception
        {
            
            Vector vec = new Vector();
            
                                                JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);
                                           
                                               // Vector AllActiveCD = sc.getAllClockDomain();
                                                Hashtable channels = im.getAllChannelInstances();
                                                ClockDomain cdins = sc.getClockDomain(keyCDName);
                                                
                                                    JSONObject jsSigs = jsSigsChans.getJSONObject("signals");
                                                   
                                                    //Enumeration keysSigsInOuts = jsSigs.keys();
                                        
                                               //     while (keysSigsInOuts.hasMoreElements()){
                                            
                                                        //String keyInOut = keysSigsInOuts.nextElement().toString();
                                            
                                                    //    if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                            JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");
                                        
                                                            Enumeration keysSigsInputsName = jsSigsInputs.keys();
                                        
                                                            while (keysSigsInputsName.hasMoreElements()){
                                            
                                                                String SigsInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                                //Object objSig = SignalObjBuffer.getInputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                               // Object objGSR = SignalObjBuffer.getInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                Field f = cdins.getClass().getField(SigsInputsName);
                                                                Signal signal = (Signal)f.get(cdins); 
                                                                //GenericSignalReceiver gsr = (GenericSignalReceiver) objGSR;
                                                       
                                                                
                                                                //gsr.suspendInputSignalThread();
                                                                //signal.setServer(gsr);
                                                                //signal.suspendInputSignalThread();
                                                                signal.killInputSignalThread();
                                                                //signal.disableInit();
                                                       
                                                                //SignalObjBuffer.removeInputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                //SignalObjBuffer.removeInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                       
                                                                //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                                //SignalObjBuffer.putInputSignalGSRInstanceToMap(objGSR, keyCurrSS, keyCDName, SigsInputsName);

                                                                
                                                            }
                                            
                                          
                                       // }
                                        
                                    //} 
                                                 //   else if (tagSigsChans.equalsIgnoreCase("SChannels")){
                                        
                                        JSONObject jsSChans = jsSigsChans.getJSONObject("SChannels");
                                        
                                            //Enumeration keysSChansInOuts = jsSChans.keys();
                                        
                                          //  while (keysSChansInOuts.hasMoreElements()){
                                            
                                               // String keyInOut = keysSChansInOuts.nextElement().toString();
                                            
                                              //  if (keyInOut.equalsIgnoreCase("inputs")){
                                                /*
                                                    JSONObject jsSChansInputs = jsSChans.getJSONObject("inputs");
                                        
                                                    Enumeration keysSChansInputsName = jsSChansInputs.keys();
                                        
                                                    while (keysSChansInputsName.hasMoreElements()){
                                            
                                                        String SChansInputsName = keysSChansInputsName.nextElement().toString();
                                            
                                                        JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);
                                            
                                                       
                                                            String cname = SChansInputsName.trim()+"_in";
                                                            String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                            //String pname = SChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");

                                        // If the channel is local
                                                                //if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                                
                                                            if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                            
                                                                    ClockDomain partnercd = null;
                                                                    
                                                                    if(sc.SchedulerHasCD(pnames[0])){
                                                                        
                                                                        partnercd = sc.getClockDomain(pnames[0]);
                        
                                                                                output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            
                                                                                            //ochan.set_partner_smp(new input_Channel());
                                                                                       // } else {
                                                                                       //     ochan.setChannelCDState("Sleep");
                                                                                       // }
                                                                                        
                                                                                         // Partner
                                                                                        

                                                                        
                                                                        
                                                                    } else 
                                                                        if(CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                            
                                                                            partnercd = (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromBuffer().get(pnames[0]);
                                                                            
                                                                            output_Channel ochan;
                                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                                        ochan = (output_Channel)f2.get(partnercd);
                                                                                        //if (sc.SchedulerHasCD(pnames[0])){
                                                                                            
                                                                                            //ochan.set_partner_smp(new input_Channel());
                                                                            
                                                                        }
                                                                    
                                                                    }
                                                                    
                               
                                                            }
                                                    */
                                            
                                           // } 
                                                //else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsSChansOutputs = jsSChans.getJSONObject("outputs");
                                        
                                                Enumeration keysChansOutputsName = jsSChansOutputs.keys();
                                        
                                                    while (keysChansOutputsName.hasMoreElements()){
                                            
                                                        String SChansOutputsName = keysChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);
                                            
                                                        String cname = SChansOutputsName.trim()+"_o";
                                                        String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                        //String[] pnames = SigOutputConfigs.getString("To").trim().split("\\.");
                                                        String[] pnames = pname.split("\\.");

                                // If the channel is local
                                
                                                 //   if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                
                          
                                                        //output_Channel ochan = (output_Channel)SJSSCDSignalChannelMap.getInOutChannelObject(keyCurrSS, keyCDName, "SChannel", "output", SChansOutputsName);
                                                                        
                                                                        
                                                        //String cname = SChansOutputsName+"_o";
                                                         output_Channel ochan;
                                                                                        Field f2 = cdins.getClass().getField(cname);
                                                                                        ochan = (output_Channel)f2.get(cdins);
                                                        
                                                                        //output_Channel ochan = (output_Channel)channels.get(keyCDName+"."+cname);
                                                                        //String cdPartnerName = ochan.getName().split("\\.")[0];
                                                                        //Field f = cdins.getClass().getField(cname);
                                                                        //output_Channel ochan = (output_Channel)f.get(cdins);
                                                                        //inchan.disableInit();
                                                                        //ochan.disableInit();
                                                                        ochan.setChannelCDState("Sleep");
                                                                        
                                                                        
                                                                        channels.put(keyCDName+"."+cname, ochan);
                                                                        
                                                                       
                                
                                                }

                                             //}

                                          //}
                                      
                                        //} 
                                                           

                                              //
                                                //im.removeCDLocation(keyCDName);
                                                cdins.setState("Sleep");
                                                //sc.removeClockDomain(cdins);
                                                sc.removeClockDomain(keyCDName);
                                                im.setChannelInstances(channels);
                                                
                                                CDObjectsBuffer.AddCDInstancesToBuffer(keyCDName, cdins);
                                                
                                                
                                                
                                                vec.addElement(im);
                                                vec.addElement(sc);
                                                CDLCBuffer.AddCDMacroState(keyCDName, "Sleep");
                                                return vec;
                                                
        }
        
        public Vector removeSigChanObjModifySCIM(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, String CDState,InterfaceManager im, Scheduler sc) throws JSONException, Exception
        {
            
            Vector vec = new Vector();
           
            if(jsLocalCDs.has(keyCDName)){
                
                 JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);
                                           
                                                Hashtable channels = im.getAllChannelInstances();
                                                
                                                ClockDomain cdins = null;
                                                if(CDState.equalsIgnoreCase("Active")){
                                                   cdins = sc.getClockDomain(keyCDName);
                                                  
                                                } else {
                                                    //Hashtable allCDInstances = CDObjectsBuffer.getAllCDInstancesFromBuffer();
                                                    
                                                    cdins = CDObjectsBuffer.GetCDInstancesFromBuffer(keyCDName);
                                                    
                                                }
                                               
                                           
                                                
                                        
                                                    JSONObject jsSigs = jsSigsChans.getJSONObject("signals");
                                                   
                                                    
                                                            JSONObject jsSigsInputs = jsSigs.getJSONObject("inputs");
                                        
                                                            Enumeration keysSigsInputsName = jsSigsInputs.keys();
                                        
                                                            while (keysSigsInputsName.hasMoreElements()){
                                            
                                                                String SigsInputsName = keysSigsInputsName.nextElement().toString();
                                            
                                                                if (CDState.equals("Active")){
                                                                    
                                                                    //Object objSig = SignalObjBuffer.getInputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                    //Object objGSR = SignalObjBuffer.getInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                            
                                                                    //GenericSignalReceiver gsr = (GenericSignalReceiver) objGSR;
                                                       
                                                                    Field f = cdins.getClass().getField(SigsInputsName);
                                                                    Signal signal = (Signal)f.get(cdins);
                                                       
                                                                    //signal.setServer(gsr);
                                                                    signal.killInputSignalThread();
                                                                    signal.disableInit();
                                                                    
                                                                     //SignalObjBuffer.removeInputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                    //SignalObjBuffer.removeInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                    
                                                                } 
                                                                //else if (CDState.equals("Sleep") || CDState.equals("Inactive")){
                                                                    
                                                                    //SignalObjBuffer.removeInputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                    //SignalObjBuffer.removeInputSignalGSRInstanceToMap(keyCurrSS, keyCDName, SigsInputsName);
                                                                    
                                                                //}
                                                                
                                                       
                                                            }
                                            
                                                        //} 
                                                        
                                                      //  else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                        JSONObject jsSigsOutputs = jsSigs.getJSONObject("outputs");
                                        
                                                        Enumeration keysSigsOutputsName = jsSigsOutputs.keys();
                                        
                                                        while (keysSigsOutputsName.hasMoreElements()){
                                            
                                                        String SigsOutputsName = keysSigsOutputsName.nextElement().toString();
                                                        
                                                        if(CDState.equals("Active")){
                                                            
                                                            //Object objSig = SignalObjBuffer.getOutputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                            //Object objGSS = SignalObjBuffer.getOutputSignalGSSInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                        
                                                        // Reflection !!
                                           
                                                            //GenericSignalSender gss = (GenericSignalSender) objGSS;
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            //Signal signal = (Signal) objSig;
                                                       
                                                            //signal.setClient(gss);
                                                            signal.disableInit();
                                                            
                                                            
                                                            //SignalObjBuffer.removeOutputSignalGSSInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                            //SignalObjBuffer.removeOutputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                       
                                                        }
                                                        //else if (CDState.equals("Sleep") || (CDState.equals("Inactive"))){
                                                            
                                                            //SignalObjBuffer.removeOutputSignalGSSInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                            //SignalObjBuffer.removeOutputSignalClassInstanceToMap(keyCurrSS, keyCDName, SigsOutputsName);
                                                            
                                                        //}
                                              
                                                    }
            
                                            //}
                                          
                                        //}
                                        
                                   // } 
                                             //       else if (tagSigsChans.equalsIgnoreCase("SChannels")){
                                        
                                        JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("SChannels");
                                        
                                           // Enumeration keysSChansInOuts = jsSigsChansInd.keys();
                                        
                                          //  while (keysSChansInOuts.hasMoreElements()){
                                            
                                               // String keyInOut = keysSChansInOuts.nextElement().toString();
                                            
                                              //  if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                        
                                                    Enumeration keysSChansInputsName = jsSChansInputs.keys();
                                        
                                                    while (keysSChansInputsName.hasMoreElements()){
                                            
                                                        String SChansInputsName = keysSChansInputsName.nextElement().toString();
                                            
                                                        JSONObject SChansInputConfigs = jsSChansInputs.getJSONObject(SChansInputsName);
                                            
                                                       
                                                            String cname = SChansInputsName.trim()+"_in";
                                                            String pname = SChansInputConfigs.getString("From").trim()+"_o";
                                                            //String pname2 = SChansInputConfigs.getString("From").trim();
                                                            String[] pnames = pname.split("\\.");

                                        // If the channel is local
                                                                //if(SSName.equals(im.getCDLocation(pnames[0]))){
                                                                
                                                            if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                                    ClockDomain partnercd; //= (ClockDomain) CDObjectsBuffer.getAllCDInstancesFromMap().get(pnames[0]);
                                                                    if(sc.SchedulerHasCD(pnames[0])){
                                                                        partnercd = sc.getClockDomain(pnames[0]);
                                                                        
                                                                        Field f = cdins.getClass().getField(cname);
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        input_Channel inchan = (input_Channel) f.get(cdins);
                                                                        output_Channel ochan = (output_Channel) f2.get(partnercd);
                                                                        
                                                                        inchan.setChannelCDState("Killed");
                                                                        //ochan.terminate_blocking_ochan();
                                                                        
                                                                        
                                                                        
                                                                        
                                                                        //ochan.terminate_blocking();
                                                                       // if(ochan.get_w_s()== ochan.get_w_r()){
                                                                        //    ochan.refresh();
                                                                        //} 
                                                                        //else {
                                                                       //     ochan.setChannelCDState("Killed");
                                                                      //      ochan.PartnerName = null;
                                                                      //  }
                                                                         
                                                                        
                                                                        //if(ochan.get_w_s()== ochan.get_w_r()){
                                                                            //inchan.setChannelCDState("Killed");
                                                                           
                                                                            
                                                                        //} else {
                                                                        //    inchan.setChannelCDState("Killed");
                                                                        //}
                                                                        
                                                                        
                                                                        
                                                                        //inchan.refresh();
                                                                        //inchan.setChannelCDState("Killed");
                                                                        //ochan.set_partner(inchan);
                                                                        //inchan.set_partner_smp(ochan);
                                                                        
                                                                        channels.remove(inchan.Name);
                                                                        
                                                                        
                                                                    } else if (CDObjectsBuffer.CDObjBufferHas(pnames[0])){
                                                                        partnercd = CDObjectsBuffer.GetCDInstancesFromBuffer(pnames[0]);
                                                                        
                                                                        Field f = cdins.getClass().getField(cname);
                                                                        Field f2 = partnercd.getClass().getField(pnames[1]);
                                                                        input_Channel inchan = (input_Channel) f.get(cdins);
                                                                        output_Channel ochan = (output_Channel) f2.get(partnercd);
                                                                        inchan.setChannelCDState("Killed");
                                                                        //ochan.terminate_blocking_ochan();
                                                                        
                                                                        //inchan.set_partner_smp(ochan);
                                                                       // ochan.CopyPartnerCDState(inchan);
                                                                        //ochan.terminate_blocking();
                                                                        //if(ochan.get_w_s()== ochan.get_w_r()){
                                                                         //  if(ochan.get_w_s()== ochan.get_w_r()){
                                                                          //      ochan.refresh();
                                                                          //      //ochan.setChannelCDState("Killed");
                                                                           // } 
                                                                           //     else {
                                                                                //ochan.setChannelCDState("Killed");
                                                                           //     ochan.PartnerName = null;
                                                                          //  }
                                                                        //} else {
                                                                       //     ochan.set_preempted();
                                                                       // }
                                                                        //input_Channel inchan2 = new input_Channel();
                                                                        //inchan2.refresh();
                                                                        //ochan.set_partner_smp(inchan);
                                                                        channels.remove(inchan.Name);
                                                                        
                                                                    }
                                                                        //input_Channel inchan;
                                                                        //output_Channel ochan;
                                                                
                                                                //input_Channel inchan = (input_Channel)channels.get(keyCDName+"."+cname);
                                                                //inchan.setChannelCDState("Terminated");
                                                                
                                                                //channels.remove(keyCDName+"."+cname);
                                                                //channels.remove(pname);
                                                                        
                                                                    
                                                                        //input_Channel inchan = (input_Channel)SJSSCDSignalChannelMap.getInOutChannelObject(keyCurrSS, keyCDName, "SChannel", "input", SChansInputsName);
                                                                        //output_Channel ochan;
                                                                        //System.out.println("SigChanMig, vec: " +vec);
                                                                        
                                                                        //inchan = (input_Channel)vec.get(0);
                                                                        //ochan = (output_Channel) vec.get(1);
                                                                        
                                                                        //get partner cd name
                                                                        
                                                                           //String cdPartnerName = inchan.getName().split("\\.")[0];
                                                                           
                                                                           
                                                                           //ochan.disableInit();
                                                                           
                                                                           //check if the partner cd is also migrated, if not, then assume the receiving CD needs interconnection
                                                                           /*
                                                                           if (jsLocalCDs.has(cdPartnerName)){
                                                                               
                                                                               //if true, then need to reinitialize Output channel of the CD pair, then create interconnection
                                                                                String ChanPairName = SChansInputConfigs.getString("From").split("\\.")[1];
                                                                               //Vector vec1 = (Vector)SJSSCDSignalChannelMap.getInOutChannelObject(keyCurrSS, cdPartnerName, "SChannel", "output", SChansInputsName);
                                                                               
                                                                                        output_Channel ochan  = (output_Channel)SJSSCDSignalChannelMap.getInOutChannelObject(keyCurrSS, keyCDName, "SChannel", "output", ChanPairName);
                                                                                        
                                                                                        Field f = cdins.getClass().getField(cname);
                                                                                        ochan = (output_Channel)f.get(cdins);
                                                                                        ochan.setInit();
                                                
                                                                                        ochan.Name = keyCDName+"."+cname;
                                                                                        ochan.PartnerName = pname;
                                                                                        ochan.setDistributed();
                                                                                        ochan.setInterfaceManager(im);
         
                                                                                        //Vector vec1 = new Vector();
                                                                                        //vec1.addElement(ochan);
                                                                                        SJSSCDSignalChannelMap.addInOutChannelObjectToMap(keyCurrSS, cdPartnerName, "SChannel", "output", ChanPairName,(Object)ochan);
                                                                               
                                                                    } else
                                                                               */
                                                                      //     {
                                                                            //   output_Channel ochan = (output_Channel)SJSSCDSignalChannelMap.getInOutChannelObject(keyCurrSS, pnames[0], "SChannel", "output", SChansInputsName);
                                                                            //   ochan.disableInit();
                                                                   // }
                                                                           
                                                                }
                                                                    else{
                                                                    
                                                                       
                                                                
                                                                        Field f = cdins.getClass().getField(cname);
                                                                        input_Channel inchan = (input_Channel) f.get(cdins);
                                                                        
                                                                        if(!inchan.IsChannelLocal()){
                                                                            inchan.setDistStateChanged(true);
                                                                        }
                                                                        
                                                                        inchan.setChannelCDState("Killed");
                                                                        
                                                                        
                                                                        //Object[] terminationSignal = new Object[2];
                                                                          // Creating an Object!!
                                                                        //terminationSignal[0] = inchan.PartnerName;
                                                                        
                                                                        //terminationSignal[1] = inchan.Name;
                                                                        //terminationSignal[2] = inchan.getChannelCDState();
                                                                        
                                                                        //im.AddStateChangeSignalToQueue(terminationSignal);
                                                                        
                                                                        //inchan.PartnerName=null;
                                                                        
                                                                        channels.remove(inchan.Name);
                                                                
                                                                    }
                               
                                                            }
                                            
                                            //} 
                                              //  else if (keyInOut.equalsIgnoreCase("outputs")){
                                                
                                                JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                        
                                                Enumeration keysChansOutputsName = jsSChansOutputs.keys();
                                        
                                                    while (keysChansOutputsName.hasMoreElements()){
                                            
                                                        String SChansOutputsName = keysChansOutputsName.nextElement().toString();
                                            
                                                        JSONObject SigOutputConfigs = jsSChansOutputs.getJSONObject(SChansOutputsName);
                                            
                                                        String cname = SChansOutputsName.trim()+"_o";
                                                        String pname = SigOutputConfigs.getString("To").trim()+"_in";
                                                        //String[] pnames = SigOutputConfigs.getString("To").trim().split("\\.");
                                                        String[] pnames = pname.split("\\.");
                                                        
                                                        //String[] pnames = SigOutputConfigs.getString("To").trim().split("\\.");
                                          
                                                    if(keyCurrSS.equals(im.getCDLocation(pnames[0]))){
                                                
                                                        if(sc.SchedulerHasCD(pnames[0])){
                                                            ClockDomain partnercd = sc.getClockDomain(pnames[0]);
                                                            Field f = cdins.getClass().getField(cname);
                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                            output_Channel ochan = (output_Channel) f.get(cdins);
                                                            input_Channel inchan = (input_Channel) f2.get(partnercd);
                                                            ochan.setChannelCDState("Killed"); // -- > needed for the following ticks after the next tick
                                                            inchan.setSubscribedStat(false);
                                                            inchan.forcePartnerPreempt();
                                                            
                                                            //inchan.terminate_blocking_local_inchan();
                                                            //inchan.terminate_blocking_local_inchan();
                                                            
                                                            //ochan.set_partner_smp(inchan);
                                                            //inchan.CopyPartnerCDState(ochan);
                                                            
                                                            //inchan.mod_r_s(); // --> needed for the next tick
                                                            
                                                            //ochan.refresh();
                                                            
                                                            //inchan.refresh();
                                                            //ochan.setChannelCDState("Killed");
                                                            //inchan.set_partner(ochan);
                                                            //inchan.refresh();
                                                            //inchan.setChannelCDState("Killed");
                                                            //ochan.set_partner(inchan);
                                                            channels.remove(ochan.Name);
                                                            
                                                        } else if (CDObjectsBuffer.CDObjBufferHas(cname)){
                                                            
                                                            ClockDomain partnercd = sc.getClockDomain(pnames[0]);
                                                            Field f = cdins.getClass().getField(cname);
                                                            Field f2 = partnercd.getClass().getField(pnames[1]);
                                                            output_Channel ochan = (output_Channel) f.get(cdins);
                                                            input_Channel inchan = (input_Channel) f2.get(partnercd);
                                                            inchan.setSubscribedStat(false);
                                                            inchan.forcePartnerPreempt();
                                                            //inchan.set_preempted();
                                                            ochan.setChannelCDState("Killed"); // -- > needed for the following ticks after the next tick
                                                            //inchan.terminate_blocking_local_inchan();
                                                            //inchan.terminate_blocking_local_inchan();
                                                             //ochan.set_partner_smp(inchan);
                                                            //inchan.CopyPartnerCDState(ochan);
                                                            //inchan.mod_r_s(); // --> needed for the next tick, force the channel to terminate blocking, read the buffer and proceed
                                                            //inchan.setChannelCDState("Killed");
                                                            //ochan.refresh();
                                                            //ochan.setChannelCDState("Killed");
                                                            //inchan.set_partner(ochan);
                                                            channels.remove(ochan.Name);
                                                            
                                                        }
                                                        
                                                              
                                                    }
                                                    else {
                                                                
                                                                        Field f = cdins.getClass().getField(cname);
                                                                        output_Channel ochan = (output_Channel) f.get(cdins);
                                                                        ochan.setChannelCDState("Killed");
                                                                        ochan.setDistStateChanged(true);
                                                                        ochan.TransmitResetSubscribeChan(keyCurrSS);
                                                                        channels.remove(ochan.Name);
                                                                
                                                                    }
                                
                                                }

                                             //}

                                          //}
                                      
                                        //} 
                                  

                                             // }
                                                //sc.removeClockDomain(cdins);
                                                sc.removeClockDomain(keyCDName);
                                                //im.removeCDLocation(keyCDName);
                                                //if(CDState.equals("Active")){
                                               //     sc.removeClockDomain(cdins);
                                               // } else if (CDState.equals("Sleep")){
                                              //      CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                              //  }
                                                CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                               
                                                SJSSCDSignalChannelMap.RemoveOneCDCurrSigChannelMapping(keyCDName, keyCurrSS);
                                                
                                               
                                                
                                                SJServiceRegistry.RemoveServiceOfCD(cdins.getName());
                                                
                                                SOABuffer.SetAdvTransmissionRequest(true);
                
            }
            
                                                vec.addElement(im);
                                                vec.addElement(sc);
                                                CDLCBuffer.RemoveCDMacroState(keyCDName);
                                                
                                                
                                                
                                                return vec;
                                                
        }
        
        public ClockDomain NullifySigObjForMigration(JSONObject jsLocalCDs, String keyCurrSS,String keyCDName, ClockDomain cdins, InterfaceManager im) throws JSONException, Exception
        {
            
            Hashtable channels = im.getAllChannelInstances();
            
                                    //im.addCDLocation(keyCurrSS, keyCDName);
                                                
                                                    //ClockDomain cdins = SJSSCDSignalChannelMap.GetCDInstancesFromMap(keyCDName);

                                                    JSONObject jsSigsChans = jsLocalCDs.getJSONObject(keyCDName);

                                                  //  Enumeration keysSigsChans = jsSigsChans.keys();

                                                   // while (keysSigsChans.hasMoreElements()){

                                                        //String tagSigsChans = keysSigsChans.nextElement().toString();

                                                     //   if (tagSigsChans.equalsIgnoreCase("signals")){

                                                            JSONObject jsSigsChansInd = jsSigsChans.getJSONObject("signals");

                                                            //GenericSignalReceiver server = null;
                                                            //GenericSignalSender client = null;
                                                            //Hashtable config = new Hashtable();

                                                            //Enumeration keysSigsInOuts = jsSigsChansInd.keys();

                                                        //    while (keysSigsInOuts.hasMoreElements()){

                                                                //String keyInOut = keysSigsInOuts.nextElement().toString();

                                                              //  if (keyInOut.equalsIgnoreCase("inputs")){

                                                                JSONObject jsSigsInputs = jsSigsChansInd.getJSONObject("inputs");

                                                                Enumeration keysSigsInputsName = jsSigsInputs.keys();

                                                                while (keysSigsInputsName.hasMoreElements()){

                                                                    String SigsInputsName = keysSigsInputsName.nextElement().toString();

                                                                    //Object classNewInst = (Object) Class.forName(config.get("Class").toString()).newInstance();

                                                            //server = (GenericSignalReceiver) classNewInst;
                                                            

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsInputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            //signal.setServer(server);
                                                            //signal.setuphook();
                                                            signal.disableInit();
                                                            signal.nullifyServer();

                                                            //SignalObjBuffer.putInputSignalClassInstanceToMap((Object) signal, keyCurrSS, keyCDName, SigsInputsName);
                                                            //SignalObjBuffer.putInputSignalGSRInstanceToMap((Object) server, keyCurrSS, keyCDName, SigsInputsName);

                                                        }

                                               // } 
                                                    //            else if (keyInOut.equalsIgnoreCase("outputs")){

                                                    JSONObject jsSigsOutputs = jsSigsChansInd.getJSONObject("outputs");

                                                    Enumeration keysSigsOutputsName = jsSigsOutputs.keys();

                                                        while (keysSigsOutputsName.hasMoreElements()){

                                                            String SigsOutputsName = keysSigsOutputsName.nextElement().toString();

                                                            // Reflection !!
                                                            Field f = cdins.getClass().getField(SigsOutputsName);
                                                            Signal signal = (Signal)f.get(cdins);
                                                            signal.disableInit();
                                                            signal.nullifyClient();

                                                        }

                                                //}

                                           // }

                                       // } 
                                       

                                    //}
                                                    
                                                    //ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, "Active");
                                                    
                                                    im.setChannelInstances(channels);
                                                    
                                                    return cdins;
                                                    
        }
        
        private Hashtable TransceiveQueryChanLocal (String reqMigMsg){
        
        Hashtable answer = new Hashtable();
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        
                        InetAddress ipAddress = InetAddress.getByName("224.0.0.100");
                        byte[] msg = new byte[65508];
                        byte[] packet = new byte[65508];
                        //byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        MulticastSocket s1 = new MulticastSocket(78);
                        MulticastSocket s2 = new MulticastSocket(66);
                        
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        
                        out.writeObject(reqMigMsg);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,78);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +ipAddress+"port:" +77);
                        s1.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        s2.setSoTimeout(700);
                        
                        //do {
                        
                        s2.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        
                                        
                                        answer.put("stat",js.getString("inchanStat"));
                                        answer.put("ack","OK");
                                        
                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                             }
                                   
                                   
                                   s1.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
                       answer.put("ack","NOT OK");
			e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound ");
                        bex.printStackTrace();
                        answer.put("ack","NOT OK");
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        answer.put("ack","NOT OK");
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                 return answer;     
                   
                    
      //  }
    }
    
    private Hashtable TransceiveQueryChan (String destAddress, String reqMigMsg){
        
        Hashtable answer = new Hashtable();
        //synchronized (lock)
        //{
            int debug=0;
            int infoDebug=0;
            
                    try
                    {
                        InetAddress ipAddress = InetAddress.getByName(destAddress);
                        byte[] msg = new byte[65508];
                        byte[] packet = new byte[65508];
                        //byte packet[] = new byte[8096];
                        //MulticastSocket s = new MulticastSocket(controlPort);
                        MulticastSocket s1 = new MulticastSocket(78);
                        MulticastSocket s2 = new MulticastSocket(66);
                        
                        //InetAddress ipAddress = SJServiceRegistry.getServicesIPLocationOfType(serviceType);
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                        
                        out.writeObject(reqMigMsg);
                        out.flush();
                        msg = byteStream.toByteArray();
                        out.close();
                        
                        //DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, Integer.parseInt(str[1]));
                        //DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,port);
                        DatagramPacket req = new DatagramPacket(msg, msg.length, ipAddress,78);
                        DatagramPacket resp = new DatagramPacket(packet, packet.length);
                        
                        if (infoDebug==1) System.out.println("TrncvReqMessage sending data to IP " +destAddress+"port:" +78);
                        if (infoDebug==1) System.out.println("Sending migration msg " +reqMigMsg);
                        s1.send(req);
                        if (infoDebug==1)System.out.println("data has been sent! Now wait for response");
                        
                        s2.setSoTimeout(1000);
                        
                        //do {
                        
                        s2.receive(resp);
                        
                       // } while (resp.getAddress().getHostAddress().equalsIgnoreCase(SJServiceRegistry.getOwnIPAddressFromRegistry()));
                        
                        byte[] data;
                        if(infoDebug == 1) System.out.println("TrncvReqMessage rcvd msg length = " + resp.getLength() + ", from " + resp.getSocketAddress()+ "port" +resp.getPort());
                        data = new byte[resp.getLength()];
                                                
                        System.arraycopy(packet, 0, data, 0, resp.getLength());

                                                // time to decode make use of data[]
                        //Object[] list = new Object[2];
                         //list[0] = Boolean.TRUE;        
                          if(data.length > 0)
                          {
                             if(((int)data[0] == -84) && ((int)data[1] == -19))
                             {
                                try
                                {
                                     if(debug == 1) System.out.println("Java built-in deserializer is used");
                                     ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                                     Object mybuffer = ois.readObject();              
                                     if(debug == 1) System.out.println(mybuffer);
                                     if(debug == 1) System.out.println((mybuffer.getClass()).getName());
                                    
                                        if(debug == 1) System.out.println("Direct assign the received byffer to the value 3");
                                     
                                        if (debug==1)  System.out.println("ControlMessageTransceive Message: " +mybuffer.toString().trim()+"\n");
                                        JSONObject js = new JSONObject(new JSONTokener(mybuffer.toString().trim()));
                                        
                                        
                                        answer.put("stat",js.getString("inchanStat"));
                                        answer.put("ack","OK");
                                        
                                        
                                 }
                                 catch(Exception e)
                                 {
                                      e.printStackTrace();
                                 }
                              }
                             }
                                   
                                   
                                   s1.close();
                                   s2.close();

                    }
                    catch (java.net.SocketTimeoutException e)
                    {
                        //*this cant be used to declare whether service is unreachable
                        answer.put("ack","NOT OK");
			e.printStackTrace();
                       
                       // s1.close();
                        //s2.close();
                        
                    }catch (java.net.BindException bex){
                        System.out.println("ControlMessage: Address cannot bound " );
                        bex.printStackTrace();
                        answer.put("ack","NOT OK");
                       // s1.close();
                      //  s2.close();
                    }
                    catch (Exception e)
                    {
			System.out.println("ControlMessage: Problem when connecting : "+ e.getMessage());
                        answer.put("ack","NOT OK");
			e.printStackTrace();
                      //  s1.close();
                      //  s2.close();
                    }
                    
                 return answer;     
                   
                    
      //  }
    }
    
}
