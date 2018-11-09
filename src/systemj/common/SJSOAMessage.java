/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author Udayanto
 */
public class SJSOAMessage{
    
    private JSONObject responseAdvertisementMessage,advertisementMessage,broadcastDiscoveryMessage,DiscoveryReplyMessage,requestAdvertiseMessage,ReqWeakMigrationMessage,ReqStrongMigrationMessage,RespMigrationMessage;
    
    public String ConstructNoP2PServToRegDiscoveryMessage(String SSOrigin, String regID, int recPort){
       
        JSONObject jsDiscMsg = new JSONObject();
        
        try {
            
            jsDiscMsg = new JSONObject();
            jsDiscMsg.put("sourceAddress",SJSSCDSignalChannelMap.GetLocalSSAddr());
            jsDiscMsg.put("MsgType","Discovery");
            jsDiscMsg.put("associatedSS",SSOrigin);
            jsDiscMsg.put("regID", regID);
            jsDiscMsg.put("respPort", Integer.toString(recPort));
            //JSONObject jsExp = SJServiceRegistry.getConsumerExpectedServiceType();
            
            //broadcastDiscoveryMessage.put("expServiceType",jsExp.getJSONObject("expectedServiceType"));
            
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructBroadcastDiscoveryMessage: " +ex.getMessage());
        }
        return jsDiscMsg.toString();
    }
    
     public JSONObject ConstructNoP2PProvToRegResponseReAdvertisementMessageInJSON(String expiryTime, String regID, String SSOrigAddr, boolean notifySSStat, String SSOrigin){
        
         JSONObject NoP2PProvToRegRespAdvMsg = new JSONObject();
         
         try {
            //NoP2PProvToRegRespAdvMsg = new JSONObject();
            NoP2PProvToRegRespAdvMsg.put("MsgType","responseProvToRegReqAdvertise");
            NoP2PProvToRegRespAdvMsg.put("sourceAddress", SJSSCDSignalChannelMap.GetLocalSSAddr());
            //NoP2PProvToRegRespAdvMsg.put("destAddr",destAddr);
           // SJServiceRegistry.AppendSourceIPAddressToMessage(NoP2PProvToRegRespAdvMsg);
            NoP2PProvToRegRespAdvMsg.put("associatedSS",SSOrigin);
            NoP2PProvToRegRespAdvMsg.put("regID", regID);
            NoP2PProvToRegRespAdvMsg.put("SSAddr", SSOrigAddr);
            NoP2PProvToRegRespAdvMsg.put("expiryTime", expiryTime);
            NoP2PProvToRegRespAdvMsg.put("CDStats", CDLCBuffer.GetAllCDMacroState());
            NoP2PProvToRegRespAdvMsg.put("SSAddrs", RegAllSSAddr.getAllSSAddr());
            
              
            JSONObject jsInclServ = new JSONObject();
            JSONObject jsIntReg = SJServiceRegistry.obtainInternalRegistry();
            
            Enumeration keysJSIntReg = jsIntReg.keys();
            
            while(keysJSIntReg.hasMoreElements()){
                
                String servName = keysJSIntReg.nextElement().toString();
                
                JSONObject jsIndivServ = jsIntReg.getJSONObject(servName);
                
                String assocCDName = jsIndivServ.getString("associatedCDName");
                
                String CDStat = CDLCBuffer.GetCDMacroState(assocCDName);
                
                if(CDStat.equalsIgnoreCase("Active")){
                    jsInclServ.put(servName, jsIndivServ);
                }
                
            }
            
            //advertisementMessage.put("portForResponse","7777");
           
            NoP2PProvToRegRespAdvMsg.put("Notify", Boolean.toString(notifySSStat));
            //responseAdvertisementMessage.put("advertisementExpiry", expiryTime);   
            //responseAdvertisementMessage.put("portForResponse","7777");
            NoP2PProvToRegRespAdvMsg.put("serviceList", jsInclServ);
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return NoP2PProvToRegRespAdvMsg;
    }
    
    public String ConstructNoP2PRegToProvReqAdvertisementMessage(String regID, String regAddr, String destSS){  //uses port 8888 now
        
        JSONObject regRequestAdvertiseMessage = new JSONObject();
        
        try {
            
            regRequestAdvertiseMessage.put("MsgType","RequestForAdvertise");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(requestAdvertiseMessage);
            regRequestAdvertiseMessage.put("regID", regID);
            regRequestAdvertiseMessage.put("destSS", destSS);
            regRequestAdvertiseMessage.put("regAddr",regAddr);
            //regRequestAdvertiseMessage.put("sourceAddress", regAddr);
            //regRequestAdvertiseMessage.put("destAddr",destAddress);
            //regRequestAdvertiseMessage.put("expServiceType",SJServiceRegistry.getConsumerExpectedServiceType());
            
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructRegReqReadvertisementMessage: " +ex.getMessage());
        }
        return regRequestAdvertiseMessage.toString();
    }
    
    public String ConstructRegRequestAdvertisementMessage(String targetRegID, String srcAddr){  //uses port 8888 now
        
        JSONObject regRequestAdvertiseMessage = new JSONObject();
        
        try {
            
            regRequestAdvertiseMessage.put("MsgType","regRequestAdvertise");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(requestAdvertiseMessage);
            regRequestAdvertiseMessage.put("regID", targetRegID);
            //regRequestAdvertiseMessage.put("regAddr",regAddr);
            regRequestAdvertiseMessage.put("sourceAddress", srcAddr);
            //regRequestAdvertiseMessage.put("destAddr",destAddress);
            //regRequestAdvertiseMessage.put("expServiceType",SJServiceRegistry.getConsumerExpectedServiceType());
            
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructRegReqReadvertisementMessage: " +ex.getMessage());
        }
        return regRequestAdvertiseMessage.toString();
    }
    
    public String ConstructRegNotifyMessage(String regID, String notifyID /*boolean TotalSSChanged, boolean CDStatsChange*/){  //uses port 8888 now
        
        JSONObject regNotifyMessage = new JSONObject();
        
        try {
            
            regNotifyMessage.put("MsgType","Notify");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(requestAdvertiseMessage);
            //regRequestAdvertiseMessage.put("destSS", destSS);
            regNotifyMessage.put("regID", regID);
            regNotifyMessage.put("notifyID",notifyID);
            /*
            if(CDStatsChange){
                JSONObject regALLCD = RegAllCDStats.getAllCDStats();
                regNotifyMessage.put("CDStats", regALLCD);
            }
            
            if(TotalSSChanged){
                JSONObject allSSAddr = RegAllSSAddr.getAllSSAddr();
                regNotifyMessage.put("SSAddrs", allSSAddr);
            }
            */
            
            //regRequestAdvertiseMessage.put("regAddr",regAddr);
            //regRequestAdvertiseMessage.put("sourceAddress", srcAddr);
            //regRequestAdvertiseMessage.put("destAddr",destAddress);
            //regRequestAdvertiseMessage.put("expServiceType",SJServiceRegistry.getConsumerExpectedServiceType());
            
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructRegReqReadvertisementMessage: " +ex.getMessage());
        }
        return regNotifyMessage.toString();
    }
    
    public JSONObject ConstructRegResponseReAdvertisementMessageInJSON(String expiryTime, String regID, String regAddr){
        
        JSONObject regRespAdvMsg = new JSONObject();
        
        try {
           
            regRespAdvMsg.put("MsgType","regRespReqAdvertise");
            regRespAdvMsg.put("regID",regID);
            regRespAdvMsg.put("regAddr", regAddr);
            regRespAdvMsg.put("expiryTime", expiryTime);
            //regRespAdvMsg.put("sourceAddress", jsRegReqAdvMsg.getString("sourceAddress"));
            //SJServiceRegistry.AppendSourceIPAddressToMessage(regRespAdvMsg);
            //regRespAdvMsg.put("associatedSS",SSOrigin);
            //responseAdvertisementMessage.put("advertisementExpiry", expiryTime);   
            //responseAdvertisementMessage.put("portForResponse","7777");
            //responseAdvertisementMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistryProviderOnly());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
           ex.printStackTrace();
        }
        return regRespAdvMsg;
    }
    
    public String ConstructRegistryDiscoveryReplyMessage(String RegistryID, String RegAddr){
        
        JSONObject RegDiscReplyMessage = new JSONObject();
        
        try {
            
             RegDiscReplyMessage.put("MsgType","DiscoveryReply");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(RegDiscReplyMessage);
             RegDiscReplyMessage.put("regID",RegistryID);
             RegDiscReplyMessage.put("regAddr", RegAddr);
            // RegDiscReplyMessage.put("targetSS", targetSS);
             //DiscoveryReplyMessage.put("discoveryTarget",target);
           // unicastDiscoveryMessage.put("delayResponse","1200"); //should be between 1-5 seconds max
            //discoveryMessage.put("portForTransmission","1900");
             //RegDiscReplyMessage.put("portForResponse","77"); //as this uses broadcast, so listening response on the same port will cause the node to receive its own sent message
             RegDiscReplyMessage.put("serviceList", SJServiceRegistry.obtainCurrentRegistry());
             //discoveryMessage.put("expiry", expiryTime);
            //discoveryMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0"));
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructUnicastDiscoveryMessage: " +ex.getMessage());
        }
        return RegDiscReplyMessage.toString();
    }
    
    public String ConstructRegistryReAdvertisementMessage(String refreshTime, String RegistryID, String RegAddr){
       
        JSONObject RegAdvMessage = new JSONObject();
        
        try {
            
            RegAdvMessage.put("MsgType","Beacon");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(advertisementMessage);
            RegAdvMessage.put("regID",RegistryID);
            RegAdvMessage.put("regAddr", RegAddr);
            RegAdvMessage.put("retransmissionTime", refreshTime);
            //advertisementMessage.put("advertisementExpiry", expiryTime);   
            //advertisementMessage.put("portForResponse","7777");
            //RegAdvMessage.put("serviceList", SJServiceRegistry.obtainCurrentRegistry());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return RegAdvMessage.toString();
    }
    
    /*
    public JSONObject ConstructProvToRegistryResponseReAdvertisementMessageInJSON(String expiryTime, String destAddr, String sourceSS){
       
        JSONObject RegRespReqAdvMessage = new JSONObject();
        
        try {
            //responseAdvertisementMessage = new JSONObject();
            RegRespReqAdvMessage.put("MsgType","responseReqAdvertise");
            RegRespReqAdvMessage.put("destAddr",destAddr);
            //SJServiceRegistry.AppendSourceIPAddressToMessage(RegRespReqAdvMessage);
            RegRespReqAdvMessage.put("associatedSS",sourceSS);
            //RegRespReqAdvMessage.put("regAddr", RegAddr);
            RegRespReqAdvMessage.put("advertisementExpiry", expiryTime);   
            //responseAdvertisementMessage.put("portForResponse","7777");
            RegRespReqAdvMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistryProviderOnly());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructRespReqReadvertisementMessage: " +ex.getMessage());
        }
        return RegRespReqAdvMessage;
    }
    */
    
    /*
     public String ConstructReAdvertisementMessage(String expiryTime, String SSOrigin){
        
         try {
            advertisementMessage = new JSONObject();
            advertisementMessage.put("MsgType","reAdvertise");
            SJServiceRegistry.AppendSourceIPAddressToMessage(advertisementMessage);
            advertisementMessage.put("associatedSS",SSOrigin);
            //advertisementMessage.put("advertisementExpiry", expiryTime);   
            //advertisementMessage.put("portForResponse","7777");
            advertisementMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistryProviderOnly());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return advertisementMessage.toString();
    }
     
     public JSONObject ConstructReAdvertisementMessageInJSON(String expiryTime, String SSOrigin){
        try {
            advertisementMessage = new JSONObject();
            advertisementMessage.put("MsgType","reAdvertise");
            SJServiceRegistry.AppendSourceIPAddressToMessage(advertisementMessage);
            advertisementMessage.put("associatedSS",SSOrigin);
            //advertisementMessage.put("advertisementExpiry", expiryTime);   
            //advertisementMessage.put("portForResponse","7777");
            //advertisementMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistryProviderOnly());
            advertisementMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistry());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return advertisementMessage;
    }
    */
     
    
      public JSONObject ConstructResponseReAdvertisementMessageInJSON(String expiryTime, String destAddr, String SSOrigin){
        try {
            responseAdvertisementMessage = new JSONObject();
            responseAdvertisementMessage.put("MsgType","responseReqAdvertise");
            responseAdvertisementMessage.put("destAddr",destAddr);
            responseAdvertisementMessage.put("sourceAddress", SJSSCDSignalChannelMap.GetLocalSSAddr());
            responseAdvertisementMessage.put("associatedSS",SSOrigin);
            responseAdvertisementMessage.put("expiryTime", expiryTime);   
            //responseAdvertisementMessage.put("portForResponse","7777");
            responseAdvertisementMessage.put("serviceList", SJServiceRegistry.obtainInternalRegistry());
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return responseAdvertisementMessage;
    }
      
     /*
     public String ConstructReAdvertisementMessage(String expiryTime, String serviceList, String SSOrigin){
        try {
            advertisementMessage = new JSONObject();
            advertisementMessage.put("MsgType","reAdvertise");
            SJServiceRegistry.AppendSourceIPAddressToMessage(advertisementMessage);
            advertisementMessage.put("associatedSS",SSOrigin);
            //advertisementMessage.put("advertisementExpiry", expiryTime);   
            //advertisementMessage.put("portForResponse","7777");
            advertisementMessage.put("serviceList", serviceList);
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return advertisementMessage.toString();
    }
     */
     
     public String ConstructServToRegReAdvertisementMessageOfInclServ(String expiryTime, boolean notifySSStat, boolean ChangedCDStat, String SSOrigin, String SSOrigAddr, String regID){
        try {
            
            JSONObject inclAdv = new JSONObject();
            
            //JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistryProviderOnly();
            JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
            
            Enumeration keysjsIntServ = jsIntServ.keys();
            
            int i=0;
            
            while (keysjsIntServ.hasMoreElements()){
                
                Object keyjsIndivServ = keysjsIntServ.nextElement();
                
                JSONObject jsIndivServ = jsIntServ.getJSONObject(keyjsIndivServ.toString());
                
                String assocCDName = jsIndivServ.getString("associatedCDName");
                
                String CDStat = CDLCBuffer.GetCDMacroState(assocCDName);
                
                if(CDStat.equalsIgnoreCase("Active")){
                    inclAdv.put(jsIndivServ.getString("serviceName"), jsIndivServ);
                }
                
                
                
            }
            
            advertisementMessage = new JSONObject();
            advertisementMessage.put("MsgType","Advertisement");
            //SJServiceRegistry.AppendSourceIPAddressToMessage(advertisementMessage);
            advertisementMessage.put("sourceAddress", SJSSCDSignalChannelMap.GetLocalSSAddr());
            advertisementMessage.put("associatedSS",SSOrigin);
            advertisementMessage.put("SSAddr", SSOrigAddr);
            advertisementMessage.put("regID", regID);
            advertisementMessage.put("CDStats", CDLCBuffer.GetAllCDMacroState());
            
            advertisementMessage.put("expiryTime", expiryTime);   
            //advertisementMessage.put("portForResponse","7777");
            advertisementMessage.put("serviceList", inclAdv);
            advertisementMessage.put("changedCDStat", ChangedCDStat);
            advertisementMessage.put("Notify", Boolean.toString(notifySSStat));
            //helloMessage.put("serviceList",currentDetailedServiceRegistry.getString("Node0")); //perhaps this isn't needed at all
        } catch (JSONException ex) {
            System.out.println("What happens in ConstructReadvertisementMessage: " +ex.getMessage());
        }
        return advertisementMessage.toString();
    }
     
    public String ConstructReqWeakMigrationMessage(String destinationSubsystem, String SSOrigin){
        try {
            String sourceAddress = SJSSCDSignalChannelMap.GetLocalSSAddr();
            //to get address, must access Serv registry
            JSONObject jsInt = SJServiceRegistry.obtainInternalRegistry();
            
           // Enumeration keysJsInt = jsInt.keys();
            
           /*
            while(keysJsInt.hasMoreElements()){
                String servName = keysJsInt.nextElement().toString();
                
                JSONObject jsDet = jsInt.getJSONObject(servName);
                
                sourceAddress = jsDet.getString(servName)
                //sourceAddress = jsDet.getString("nodeAddress");
                break;
            }
            */
            
            ReqWeakMigrationMessage = new JSONObject();
            
            ReqWeakMigrationMessage.put("MsgType","requestWeakServiceMigration");
            ReqWeakMigrationMessage.put("sourceAddress",sourceAddress);
            ReqWeakMigrationMessage.put("associatedSS",SSOrigin);
            ReqWeakMigrationMessage.put("destinationSubsystem",destinationSubsystem);
            //ReqMigrationMessage.put("destAddress",destAddress);
            
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return ReqWeakMigrationMessage.toString();
    }
    
    public String ConstructReqStrongMigrationMessage(String destinationSubsystem, String SSOrigin){
        try {
            
            String sourceAddress = SJSSCDSignalChannelMap.GetLocalSSAddr();
            //to get address, must access Serv registry
            JSONObject jsInt = SJServiceRegistry.obtainInternalRegistry();
            
            Enumeration keysJsInt = jsInt.keys();
            
            /*
            while(keysJsInt.hasMoreElements()){
                String servName = keysJsInt.nextElement().toString();
                
                JSONObject jsDet = jsInt.getJSONObject(servName);
                
                sourceAddress = jsDet.getString("nodeAddress");
                break;
            }
            */
            
            ReqStrongMigrationMessage = new JSONObject();
            
            ReqStrongMigrationMessage.put("MsgType","requestStrongServiceMigration");
            ReqStrongMigrationMessage.put("sourceAddress",sourceAddress);
            ReqStrongMigrationMessage.put("associatedSS",SSOrigin);
            ReqStrongMigrationMessage.put("destinationSubsystem",destinationSubsystem);
            //ReqMigrationMessage.put("destAddress",destAddress);
            
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return ReqStrongMigrationMessage.toString();
    }
    
    public String ConstructResponseMigrationMessage(String ACK, String SSOrigin, String SSDest){
        try {
            
            String sourceAddress = SJSSCDSignalChannelMap.GetLocalSSAddr();
            //to get address, must access Serv registry
            JSONObject jsInt = SJServiceRegistry.obtainInternalRegistry();
            
            Enumeration keysJsInt = jsInt.keys();
            
            /*
            while(keysJsInt.hasMoreElements()){
                String servName = keysJsInt.nextElement().toString();
                
                JSONObject jsDet = jsInt.getJSONObject(servName);
                
                sourceAddress = jsDet.getString("nodeAddress");
                break;
            }
            */
            
            RespMigrationMessage = new JSONObject();
            RespMigrationMessage.put("associatedSS",SSOrigin);
            RespMigrationMessage.put("MsgType", "responseReqServiceMigration");
            RespMigrationMessage.put("destinationSubsystem",SSDest );
            RespMigrationMessage.put("sourceAddress",sourceAddress);
            RespMigrationMessage.put("data", ACK);
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return RespMigrationMessage.toString();
    }
    
    public String CreateLinkCreationReqMsg(String SSOrigin, String SSDest, String destAddr, String sourceAddress){
        
        JSONObject jsMSg = new JSONObject();
        
        try{
           
            jsMSg.put("associatedSS",SSOrigin);
            jsMSg.put("MsgType","reqLinkCreation");
            jsMSg.put("destinationSubsystem",SSDest );
            jsMSg.put("destAddr",destAddr);
            jsMSg.put("sourceAddress", sourceAddress);
            
            
            
        } catch (JSONException jex){
            jex.printStackTrace();
        }
        
        return jsMSg.toString();
        
    }
    
    public String CreateLinkCreationRespMsg(String SSOrigin, String SSDest, String sourceAddress, String ACK){
        
        JSONObject jsMSg = new JSONObject();
        
        try{
           
            jsMSg.put("associatedSS",SSOrigin);
            jsMSg.put("MsgType", "respLinkCreation");
            jsMSg.put("destinationSubsystem",SSDest );
            jsMSg.put("sourceAddress",sourceAddress);
            jsMSg.put("data",ACK);
            
        } catch (JSONException jex){
            jex.printStackTrace();
        }
        
        return jsMSg.toString();
        
    }
    
    public String CreateChanQueryMsg(String InChanName,String SSOrigin, String SSDest, String sourceAddress){
        
        JSONObject jsMSg = new JSONObject();
        
        try{
           
            jsMSg.put("associatedSS",SSOrigin);
            jsMSg.put("MsgType","chanQuery");
            jsMSg.put("destinationSubsystem",SSDest );
            jsMSg.put("sourceAddress",sourceAddress);
           
            jsMSg.put("inchanName", InChanName);
            
            
        } catch (JSONException jex){
            jex.printStackTrace();
        }
        
        return jsMSg.toString();
        
    }
    
    public String CreateChanQueryRespMsg(String InChanName,String SSOrigin, String SSDest, String sourceAddress, String inchanStat){
        
        JSONObject jsMSg = new JSONObject();
        
        try{
           
            jsMSg.put("associatedSS",SSOrigin);
            jsMSg.put("MsgType","chanQueryResp");
            jsMSg.put("destinationSubsystem",SSDest );
            jsMSg.put("sourceAddress",sourceAddress);
            
            jsMSg.put("inchanName", InChanName);
            jsMSg.put("inchanStat",inchanStat);
            
        } catch (JSONException jex){
            jex.printStackTrace();
        }
        
        return jsMSg.toString();
        
    }
    
}
