/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package soart;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.IMBuffer;
import systemj.common.InterfaceManager;
import systemj.common.RegAllCDStats;
import systemj.common.SJChannelRequestMessage;
import systemj.common.SJChannelResponseMessage;
import systemj.common.SJMessageConstants;
import systemj.common.SJResponseMessage;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.SchedulersBuffer;
import systemj.interfaces.Scheduler;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;

/**
 *
 * @author Udayanto
 */
public class SOSJ {
    
    /*
    public static synchronized void SetServiceToInvokeOld(Hashtable result,int conf){
       
            String addr = result.get("nodeAddress").toString();
            String reqPort = result.get("requestPort").toString();
            
            String actName = result.get("actionName").toString();
            String servName = result.get("serviceName").toString();
            String target = result.get("signalName").toString();

            if (result.containsKey("responsePort")){
                 String respPort = result.get("responsePort").toString();
                 SOABuffer.setMatchingProvider(servName,addr, reqPort,respPort, Integer.toString(conf), actName,target);
            } else {
                String respPort = result.get("requestPort").toString();
                SOABuffer.setMatchingProvider(servName,addr, reqPort,respPort, Integer.toString(conf), actName,target);
            }
            //else {
           //     SOABuffer.setMatchingProvider(servName,addr, reqPort, conf, actName,target);
           // }
    }
    */
    
    public static synchronized String CreateChanInvReqMsg(Hashtable result){
       
        String res = "{}";
        
        
            SJChannelRequestMessage sjreqmsg = new SJChannelRequestMessage();
        
            //String destSSName = result.get("destSSName").toString();
           // String destCDName = result.get("destCDName").toString();
           // String destChanName = result.get("destChanName").toString();
            
            if(result.containsKey("respSSName")){
                 String respSSName = result.get("respSSName").toString();
                 sjreqmsg.setSrcSSName(respSSName);
            }
            
            if(result.containsKey("respCDName")){
                String respCDName = result.get("respCDName").toString();
                sjreqmsg.setSrcCDName(respCDName);
            }
            
            if(result.containsKey("respChanName")){
                String respChanName = result.get("respChanName").toString();
                sjreqmsg.setSrcChanName(respChanName);
            }
            
            if(result.containsKey("actionName")){
                
                //JSONObject jsPyld = new JSONObject();
                
                String act = result.get("actionName").toString();
                
                
                
                
                //jsPyld.put("action", act);
                
                  //  jsPyld.put("data", val);
                
                sjreqmsg.setAction(act);
                
                
                //jsPyld.put("actionName", act);
                
               // if(result.containsKey("data")){
               //     String actVal = result.get("data").toString();
                    
               //     jsPyld.put("data", actVal);
               // }
                
                //sjreqmsg.setPayloadInJSON(jsPyld);
                
            } 
            
           // sjreqmsg.setDestCDName(destCDName);
           // sjreqmsg.setDestChanName(destChanName);
           // sjreqmsg.setDestSSName(destSSName);
            
            res = sjreqmsg.createRequestMessage();
         
        
        

            return res;
            
    }
    
    public static synchronized String CreateChanInvReqMsg(Hashtable result, String val){
       
        String res = "{}";
        
        
            SJChannelRequestMessage sjreqmsg = new SJChannelRequestMessage();
        
            String destSSName = result.get("destSSName").toString();
            String destCDName = result.get("destCDName").toString();
            String destChanName = result.get("destChanName").toString();
            
            if(result.containsKey("respSSName")){
                 String respSSName = result.get("respSSName").toString();
                 sjreqmsg.setSrcSSName(respSSName);
            }
            
            if(result.containsKey("respCDName")){
                String respCDName = result.get("respCDName").toString();
                sjreqmsg.setSrcCDName(respCDName);
            }
            
            if(result.containsKey("respChanName")){
                String respChanName = result.get("respChanName").toString();
                sjreqmsg.setSrcChanName(respChanName);
            }
            
            if(result.containsKey("actionName")){
                
                //JSONObject jsPyld = new JSONObject();
                
                String act = result.get("actionName").toString();
                
                
                //jsPyld.put("action", act);
                
                  //  jsPyld.put("data", val);
                
                sjreqmsg.setAction(act);
                sjreqmsg.setActionData(val);
                //sjreqmsg.setPayloadInJSON(jsPyld);
                
            } 
            
            sjreqmsg.setDestCDName(destCDName);
            sjreqmsg.setDestChanName(destChanName);
            sjreqmsg.setDestSSName(destSSName);
           
            
            res = sjreqmsg.createRequestMessage();
         

            return res;
            
    }
    
    public static synchronized long GenerateMessageID(){
        return System.nanoTime();
    }
    
    public static synchronized String CreateSigInvocReqMsg(String actionName, long msgID){
        
        JSONObject jsReq = new JSONObject();
        try {
            jsReq.put("action", actionName);
            jsReq.put("msgID", Long.toString(msgID));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsReq.toString();
        
        
    }
    
    public static synchronized String CreateSigInvocReqMsg(String actionName, long msgID, String data){
        
        JSONObject jsReq = new JSONObject();
        try {
            jsReq.put("action", actionName);
            jsReq.put("msgID", Long.toString(msgID));
            jsReq.put("data", data);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsReq.toString();
        
    }
    
    public static synchronized String CreateSigInvocReqMsg(String actionName, long msgID, int data){
        
        JSONObject jsReq = new JSONObject();
        try {
            jsReq.put("action", actionName);
            jsReq.put("msgID", Long.toString(msgID));
            jsReq.put("data", Integer.toString(data));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsReq.toString();
        
    }
    
    public static synchronized String CreateSigInvocRespACKMsg(String ReqMsg){
        JSONObject jsReqMsg = new JSONObject();
        JSONObject jsRespMsg = new JSONObject();
        try {
            jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            String msgID = jsReqMsg.getString("msgID");
            
            jsRespMsg.put("msgID", msgID);
            jsRespMsg.put("status", "ACK");
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsRespMsg.toString();
    }
    
    public static synchronized String CreateSigInvocRespACKMsg(String ReqMsg, String data){
        JSONObject jsReqMsg = new JSONObject();
        JSONObject jsRespMsg = new JSONObject();
        try {
            jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            String msgID = jsReqMsg.getString("msgID");
            
            
            jsRespMsg.put("msgID", msgID);
            jsRespMsg.put("status", "ACK");
            jsRespMsg.put("data", data);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsRespMsg.toString();
    }
    
    public static synchronized String CreateSigInvocRespACKMsg(String ReqMsg, int data){
        JSONObject jsReqMsg = new JSONObject();
        JSONObject jsRespMsg = new JSONObject();
        try {
            jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            String msgID = jsReqMsg.getString("msgID");
            
            jsRespMsg.put("msgID", msgID);
            jsRespMsg.put("status", "ACK");
            jsRespMsg.put("data", Integer.toString(data));
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsRespMsg.toString();
    }
    
    public static synchronized long GetSignalInvocMsgID(String InvMsg){
            long msgID=0;
        try {
            JSONObject jsMsg = new JSONObject(new JSONTokener(InvMsg));
            msgID = Long.parseLong(jsMsg.getString("msgID"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return msgID;
        
    }
    
    /*
    public static synchronized String CreateSigInvRespNAKMsg(String ReqMsg){
        JSONObject jsReqMsg = new JSONObject();
        JSONObject jsRespMsg = new JSONObject();
        try {
            jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            String msgID = jsReqMsg.getString("msgID");
            
            
            jsRespMsg.put("msgID", msgID);
            jsRespMsg.put("status", "NAK");
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return jsRespMsg.toString();
    }
    */
    
    public static synchronized String CreateChanInvRespMsg(String ReqMsg, String val){
       
        String res = "{}";
        
        try {
            JSONObject jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            
           // String addr = jsReqMsg.get("nodeAddress").toString();
            //String port = jsReqMsg.get("port").toString();
            
            String respChanName = jsReqMsg.get("respChanName").toString();
            String respCDName = jsReqMsg.get("respCDName").toString();
            String respSSName = jsReqMsg.get("respSSName").toString();
            String srcCDName = jsReqMsg.get("destCDName").toString();
            String srcChanName = jsReqMsg.get("destChanName").toString();
            String srcSSName = jsReqMsg.get("destSSName").toString();
            
            SJChannelResponseMessage sjrespmsg = new SJChannelResponseMessage();
            sjrespmsg.setPayload(val);
            sjrespmsg.SetSrcChanName(srcChanName);
            sjrespmsg.SetSrcCDName(srcCDName);
            sjrespmsg.SetSrcSSName(srcSSName);
            sjrespmsg.SetDestSSName(respSSName);
            sjrespmsg.SetDestCDName(respCDName);
            sjrespmsg.SetDestChanName(respChanName);
            
            res = sjrespmsg.createResponseMessage();
            
        } catch (JSONException ex) {
           ex.printStackTrace();
        }
        
        return res;
        
    }
    
    public static synchronized String CreateChanInvRespMsg(String ReqMsg){
       
        String res = "{}";
        
        try {
            JSONObject jsReqMsg = new JSONObject(new JSONTokener(ReqMsg));
            
            //String addr = jsReqMsg.get("nodeAddress").toString();
            //String port = jsReqMsg.get("port").toString();
            
            String respSSName = jsReqMsg.get("respSSName").toString();
            String respChanName = jsReqMsg.get("respChanName").toString();
            String respCDName = jsReqMsg.get("respCDName").toString();
            String srcSSName = jsReqMsg.get("destSSName").toString();
            String srcCDName = jsReqMsg.get("destCDName").toString();
            String srcChanName = jsReqMsg.get("destChanName").toString();
            
            SJChannelResponseMessage sjrespmsg = new SJChannelResponseMessage();
            
            sjrespmsg.SetDestCDName(respCDName);
            sjrespmsg.SetDestSSName(respSSName);
            sjrespmsg.SetDestChanName(respChanName);
            sjrespmsg.SetSrcCDName(srcCDName);
            sjrespmsg.SetSrcSSName(srcSSName);
            sjrespmsg.SetSrcChanName(srcChanName);
        
            return sjrespmsg.createResponseMessage();
            
        } catch (JSONException ex) {
           ex.printStackTrace();
        }
        
        return res;
        
    }
    
    public static synchronized String GetValRspMsg(String RespMsg){
       
        String res = "";
        
        try {
            JSONObject jsRespMsg = new JSONObject(new JSONTokener(RespMsg));
            
            //String Addr = jsReqMsg.get("nodeAddress").toString();
           
            res = jsRespMsg.getString("payload");
            
        } catch (JSONException ex) {
           ex.printStackTrace();
        }
        
        return res;
    }
    
    public static synchronized long GetRegNotifID(){
        
        return SOABuffer.getNotifID();
        
    }
    //local  & distributed channel for service invocation
    public static synchronized boolean ConfigureInvocChannel(String CDSenderName, String ChanSenderName, String CDReceiverName, String ChanReceiverName){
        
        boolean stat = CDLCBuffer.AddInvokeServ2ChanReconfig(CDSenderName,ChanSenderName, "output", CDReceiverName,ChanReceiverName);
        return stat;
    }
    
    public static synchronized boolean ConfigureInvocChannel(String CDSenderName, String ChanSenderName, String RecReqMsg){
        
        boolean stat = false;
        
        try {
            JSONObject jsReqMsg = new JSONObject(new JSONTokener(RecReqMsg));
            
            String CDReceiverName = jsReqMsg.getString("respCDName");
            String ChanReceiverName = jsReqMsg.getString("respChanName");
            String respSSName = jsReqMsg.getString("respSSName");
            String respSSDestAddr = jsReqMsg.getString("respSSDestAddr");
            
        
            stat = CDLCBuffer.AddInvokeServ2ChanReconfig(respSSName,respSSDestAddr,CDSenderName,ChanSenderName, "output", CDReceiverName,ChanReceiverName);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        
        return stat;
    }
    
    public static synchronized boolean GetInvocChannelReconfigStat(String PartChanCDName, String PartChanName){
        boolean stat = CDLCBuffer.GetReconfigInvokeServ2StatChanBuffer(PartChanCDName, PartChanName);
        return stat;
    }
    
   
    /*
     //distributed channel
    public static synchronized boolean ConfigureChannel(String destSS, String destAddr, String CDSenderName, String ChanSenderName, String CDReceiverName, String ChanReceiverName){
        
        boolean stat = CDLCBuffer.AddInvokeServ2ChanReconfig(CDSenderName,ChanSenderName, "output", CDReceiverName,ChanReceiverName);
        return stat;
    }
    /*
    public static synchronized boolean ConfigureChannel(String destSS, String destAddr, String destPort, String CDSenderName, String ChanSenderName, String CDReceiverName, String ChanReceiverName){
        
        boolean stat = CDLCBuffer.AddInvokeServ2ChanReconfig(CDSenderName,ChanSenderName, "output", CDReceiverName,ChanReceiverName);
        return stat;
    }
    */
    
    public static synchronized void SetCDLocation(String CDName, String SSName){
        CDLCBuffer.AddCDLocTempToBuffer(CDName, SSName);
        
    }
    
    /*
    public static synchronized void SetServiceToInvoke(String jsMatchedService, String signalName, String actName, int conf){
        
        try {
            JSONObject jsServ = new JSONObject(new JSONTokener(jsMatchedService));
            
            Enumeration keysJsServ = jsServ.keys();
            
            while(keysJsServ.hasMoreElements()){
                String servName = keysJsServ.nextElement().toString();
                
                JSONObject jsServDet = jsServ.getJSONObject(servName);
                
                String addr = jsServDet.get("nodeAddress").toString();
                String reqPort = jsServDet.get("requestPort").toString();
            
                if (jsServDet.has("responsePort")){
                    String respPort = jsServDet.get("responsePort").toString();
                    SOABuffer.setMatchingProvider(servName,addr, reqPort,respPort, Integer.toString(conf), actName,signalName);
                } else {
                    //String respPort = jsServDet.get("requestPort").toString();
                    SOABuffer.setMatchingProvider(servName,addr, reqPort,reqPort, Integer.toString(conf), actName,signalName);
                }
                break;
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    */
    
    public static synchronized String GetPayloadData(String jsString){
        
        String payloadData=null;
        
        System.out.println("ControlMessage,GetPayloadData: " +jsString);
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            JSONObject jsPyldData = js.getJSONObject("payload");
             
            if (jsPyldData.has("data")){
                payloadData = jsPyldData.getString("data");
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
            //System.out.println("MessageExtractor, ExtractControlMessagePayload JSONException: " +ex.getMessage());
        }
        
        if (payloadData==null){
            return "";
        } else {
            return payloadData;
        }
        
        
    }
    
    public static synchronized String GetAction(String jsString){
        
        String action=null;
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            if(js.has("payload")){
                
                JSONObject jsPyldData = js.getJSONObject("payload");
            
                    if (jsPyldData.has("action")){

                        action = jsPyldData.getString("action");
                    }
                
            } else {
                
                if (js.has("action")){

                        action = js.getString("action");
                }
                
            }
            
            
            
        } catch (JSONException ex) {
            System.out.println("MessageExtractor, SOSJGetAction JSONException: " +ex.getMessage());
        }
        
        if (action==null){
            return "";
        } else {
            return action;
        }
        
    }
    
    public static synchronized String GetActionData(String jsString){
        
        String data=null;
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            if(js.has("payload")){
                
                JSONObject jsPyldData = js.getJSONObject("payload");
            
                if (jsPyldData.has("data")){

                    data = jsPyldData.getString("data");
                }
                
            } else {
                
                if(js.has("data")){
                    data = js.getString("data");
                }
                
            }
            
        } catch (JSONException ex) {
            System.out.println("MessageExtractor, ExtractControlMessagePayload JSONException: " +ex.getMessage());
        }
        
        if (data==null){
            return "";
        } else {
            return data;
        }
        
    }
    
    /*
    public static synchronized String GetConfirmable(String jsString){
        
        String conf=null;
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            JSONObject jsPyldData = js.getJSONObject("payload");
            
            if (jsPyldData.has("conf")){
               
                conf= jsPyldData.getString("conf");
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        if (conf==null){
            return "";
        } else {
            return conf;
        }
        
    }
    */
    
    /*
    public static synchronized String GetSourceAddress(String jsString){
         
        String srcIPAddr=null;
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            srcIPAddr = js.getString("srcAddr");
                             
            
        } catch (JSONException ex) {
            System.out.println("MessageExtractor, ExtractControlMessageSourceIPAddress JSONException: " +ex.getMessage());
        }
        
        if (srcIPAddr==null){
            return "0.0.0.0";
        } else {
             return srcIPAddr;
        }
 
        
    }
    
    public static synchronized String GetSourcePort(String jsString){
         
        String srcPort=null;
        
        try
        {
            JSONObject js = new JSONObject(new JSONTokener(jsString));
            
            srcPort = js.getString("reqPort");
                             
            
        } catch (JSONException ex) {
            System.out.println("MessageExtractor, ExtractControlMessageSourceIPAddress JSONException: " +ex.getMessage());
        }
        
        if (srcPort==null){
            return "0";
        } else {
             return srcPort;
        }
 
    }
    
    */
    
    public static synchronized void StoreServiceToRegistry(String regJSON){
        try {
            JSONObject serv = new JSONObject(new JSONTokener(regJSON));
            
            SJServiceRegistry.SaveDiscoveredServicesNoP2P(serv);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public static synchronized String GetLocalRegistry(){
        
        String reg="{}";
        
        try {
             reg = SJServiceRegistry.obtainInternalRegistry().toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return reg;
        
    }
    
    public static synchronized String CreateResponseWithData(String msg, String data){
        
        try {
            JSONObject js = new JSONObject(new JSONTokener(msg));
            
            JSONObject jsPyld = new JSONObject();
            
            
            
                    SJResponseMessage sjResp = new SJResponseMessage(SJMessageConstants.MessageType.ACK);
                    //sjResp.setMessageToken(Integer.parseInt(js.getString("token")));
            //sjResp.setSourceAddress(InetAddress.getByName(ipAddr));
                    sjResp.setMessageID(js.getInt("msgID"));
            
                    sjResp.setSourceAddress(js.getString("srcAddr"));
                    
                    sjResp.setDestinationPort(Integer.parseInt(js.getString("respPort"))); //port
                    
                    jsPyld.put("data", data);
                    
                    sjResp.setJSONPayload(jsPyld);
            
                    msg = sjResp.createResponseMessage();
            //js.put("payload", jsPyld);
            
            //msg = 
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return msg;
        
    }
    
    public static synchronized String GetMatchingServiceInJSON(Hashtable result, String RegistryContent){
        
        JSONObject res = new JSONObject();
        
        
        String desServName = (String)result.get("serviceName");
        
        try {
            JSONObject jsRegCont = new JSONObject(new JSONTokener(RegistryContent));
            
            Enumeration keysAllSS = jsRegCont.keys();
            
            while(keysAllSS.hasMoreElements()){
                
                String SSName = keysAllSS.nextElement().toString();
                
                JSONObject AllServSS = jsRegCont.getJSONObject(SSName);
                
                JSONObject resServ = new JSONObject();
                
                Enumeration keysAllServSS = AllServSS.keys();
                
                while(keysAllServSS.hasMoreElements()){
                    
                    String servName = keysAllServSS.nextElement().toString();
                    
                    if(servName.equals(desServName)){
                        
                        resServ.put(servName, AllServSS.getJSONObject(servName));
                        
                    }
                }
                res.put(SSName, resServ);
                
            }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return res.toString();
        
    }
    
    public static void TriggerRefreshAdv(){
        SOABuffer.SetAdvTransmissionRequest(true);
    }
    
    public static String GetAllCDMacroStates(){
        
        String res = "";
        
        try {
            res = RegAllCDStats.getAllCDStats().toPrettyPrintedString(2, 0);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return res;
    }
    
    /*
    public static String GetServiceRegistryContent(){
        
        if (SJServiceRegistry.getParsingStatus())
            {
                //System.out.println("SJRegFetcher, Obtaining reg"); 
                try {
                    JSONObject jsAllCurrServ = SJServiceRegistry.obtainCurrentRegistry();
                    
                    //System.out.println("ServiceRegistryFetcher,obtainCurrentRegistry: " +jsAllCurrServ.toPrettyPrintedString(2, 0));
                    
                    //obj[0]=Boolean.TRUE;

                    return jsAllCurrServ.toString();
                    
                } catch (JSONException ex) {
                    
                    System.out.println("Error in grabbing info from service description: " +ex.getMessage());
                    return "{}";
                }
            } else {
                //System.out.println("SJRegFetcher, Parsing incomplete");
                 return "{}";
                
            }
        
    }
    */
    
}
