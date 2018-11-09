/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package systemj.common.SOAFacility.Support;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * contain all SOA msg buffer for data exchange between SOA thread
 * @author Udayanto
 */
public class SOABuffer {
   
    private static String RegBeaconPeriodTime;
    private static Vector DiscMsgBuffer = new Vector();
    private static Vector ReqAdvMsgBuffer = new Vector();
    
    private static JSONObject AdvVisibList = new JSONObject();
    private static boolean RequestForAdvTransmission = false;
    private static boolean RegNotify = false;
    private static volatile long notifID;
    private static boolean NotifyChangedCDStat = false;
    //private static boolean NotifyChangedTotalSS = false;
    private static boolean ReceivedNotifyChangedStat = false;
    private static long lastAdvertisementTime=0;
    
    private static final Object RequestForAdvTransmissionLock = new Object();
    private static final Object RegNotifyLock = new Object();
    private static final Object notifIDLock = new Object();
    private static final Object NotifyChangedCDStatLock = new Object();
    //private static final Object NotifyChangedTotalSSLock = new Object();
    private static final Object ReceivedNotifyChangedStatLock = new Object();
    private final static Object advTimeStampLock = new Object();
    private static final Object RegBeaconPeriodTimeLock = new Object();
    private static final Object ReqAdvMsgBufferLock = new Object();
    private static final Object DiscMsgBufferLock = new Object();
    
    private static Vector RegToProvReqAdvMsgBuffer = new Vector();
    private final static Object RegToProvReqAdvMsgBufferLock = new Object();
    
    private static String EmptySSName = null;
    private static final Object AdvVisibListLock = new Object();
    private static String SubnetMaskAddr;
    private static final Object SubnetMaskAddrLock = new Object();
    private static final Object SOSJRegistryIDLock = new Object();
    private static String SOSJRegistryID;
    private static String GtwyAddr;
    private static final Object GtwyAddrLock = new Object();
    private static final Object SOSJRegistryAddrLock = new Object();
    private static String SOSJRegistryAddr;
    private static final Object EmptySSNameLock = new Object();
  
    public static void AddEmptySSName(String SSName){
        synchronized(EmptySSNameLock){
            EmptySSName = SSName;
        }
    }
    
    public static String GetEmptySSName(){
        synchronized(EmptySSNameLock){
            return EmptySSName;
        }
    }
    
    
    
    public static void SetRegNotifySS(boolean stat){
        synchronized(RegNotifyLock){
            RegNotify = stat;
        }
    }
    
    public static boolean GetRegNotify(){
        synchronized(RegNotifyLock){
            return RegNotify;
        }
    }
    
    
    public static void SetNotifyChangedCDStat(boolean stat){
        synchronized(NotifyChangedCDStatLock){
           NotifyChangedCDStat = stat;
        }
    }
    
    public static boolean GetNotifyChangedCDStat(){
        synchronized(NotifyChangedCDStatLock){
            return NotifyChangedCDStat;
        }
    }
    
    /*
     public static boolean GetNotifyChangedTotalSS(){
        synchronized(NotifyChangedTotalSSLock){
            return NotifyChangedTotalSS;
        }
    }
    
    public static void SetNotifyChangedTotalSS(boolean stat){
        synchronized(NotifyChangedTotalSSLock){
           NotifyChangedTotalSS = stat;
        }
    }
    */
    
    public static void SetReceivedNotifyChangedCDStat(boolean stat){
        synchronized(ReceivedNotifyChangedStatLock){
           ReceivedNotifyChangedStat = stat;
        }
    }
    
    public static boolean GetReceivedNotifyChangedCDStat(){
        synchronized(ReceivedNotifyChangedStatLock){
            return ReceivedNotifyChangedStat;
        }
    }
    
    public static void SetNotifID(long notID){
        synchronized(notifIDLock){
            //prevNotifID = notifID;
            notifID = notID;
        }
    }
    
    public static long getNotifID(){
        synchronized(notifIDLock){
            return notifID;
        }
    }
    
    public static void AddRegToProvReqAdv (JSONObject jsMsg){
        synchronized(RegToProvReqAdvMsgBufferLock){
            RegToProvReqAdvMsgBuffer.addElement(jsMsg);
        }
    }
    
    public static Vector GetRegToProvReqAdv(){
        synchronized(RegToProvReqAdvMsgBufferLock){
            
            Vector vec = new Vector();
            
            for (int buffsize=0;buffsize<RegToProvReqAdvMsgBuffer.size();buffsize++){
                vec.addElement(RegToProvReqAdvMsgBuffer.get(buffsize));
            }
            RegToProvReqAdvMsgBuffer = new Vector();
            return vec;
        }
    }
    
    
    public static void setSOSJRegID(String regID){
        synchronized(SOSJRegistryIDLock){
            SOSJRegistryID = regID;
        }
    }
    
    public static String getSOSJRegID(){
        synchronized(SOSJRegistryIDLock){
            return SOSJRegistryID;
        }
    }
    
    public static void setSOSJRegAddr(String regaddr){
        synchronized(SOSJRegistryAddrLock){
            SOSJRegistryAddr=regaddr;
        }
    }
    
    public static String getSOSJRegAddr(){
        synchronized(SOSJRegistryAddrLock){
            return SOSJRegistryAddr;
        }
    }
    
    public static void setGatewayAddr(String addr){
        synchronized(GtwyAddrLock){
            GtwyAddr=addr;
        }
    }
    
    public static String getGatewayAddr(){
        synchronized(GtwyAddrLock){
            return GtwyAddr;
        }
    }
    
    public static void setSubnetMaskAddr(String addr1){
        synchronized(SubnetMaskAddrLock){
            SubnetMaskAddr=addr1;
        }
        
    }
    
    public static String getSubnetMaskAddr(){
        synchronized(SubnetMaskAddrLock){
            return SubnetMaskAddr;
        }
    }
    
    public static void setSOSJRegBeaconPeriodTime(String addr){
        synchronized(RegBeaconPeriodTimeLock){
            RegBeaconPeriodTime=addr;
        }
        
    }
    
    public static String getSOSJRegBeaconPeriodTime(){
         synchronized(RegBeaconPeriodTimeLock){
             return RegBeaconPeriodTime;
        }
    }
    
    public static void putDiscMsgToDiscBuffer(JSONObject jsMsg){

        synchronized (DiscMsgBufferLock){
            DiscMsgBuffer.addElement(jsMsg);
        }

    }
    
    public static Vector getAllDiscMsgFromBuffer(){
        synchronized (DiscMsgBufferLock){
            Vector vec = DiscMsgBuffer;
            DiscMsgBuffer = new Vector();
            return vec;
        }
    }
    
    /*
    public static JSONObject getDiscMsgBuffer(){

        synchronized (DiscMsgBufferLock){
            if (!DiscMsgBuffer.isEmpty()){
                return (JSONObject) DiscMsgBuffer.remove(0);
            } else {
                return new JSONObject();
            }
        }

    }
    */
    
    /*
    
    public static void putAdvModToBuffer(String servName, String advVisib){

        try{
        
            synchronized(AdvVisibListLock){
               
                AdvVisibList.put(servName, advVisib);
            }
          } catch (JSONException jex){
             System.out.println("SOABuffer putAdvModToBuffer:" +jex.getCause());
          }
    }
    */
    
    /*
    public static JSONObject getAdvModBuffer(){

        synchronized(AdvVisibListLock){
            
            return AdvVisibList;
        }

    }
    */
    
    public static void modifyAdvModBufferOfServiceName(String servName, String stat){
        synchronized(AdvVisibListLock){
            
            if(AdvVisibList.has(servName)){
                if(stat.equalsIgnoreCase("visible") || stat.equalsIgnoreCase("invisible")){
                    try {
                        AdvVisibList.put(servName, stat);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
        }
    }
    
    
    
    
    public static void removeAdvStatOfServName(String servName){
        synchronized(AdvVisibListLock){
            
           AdvVisibList.remove(servName);
        }
    }
    
     
    
    public static void putReqAdvToReqAdvBuffer(JSONObject jsMsg){

        synchronized (ReqAdvMsgBufferLock){
            ReqAdvMsgBuffer.addElement(jsMsg);
        }

    }
    
    public static JSONObject getReqAdvBuffer(){

        synchronized (ReqAdvMsgBufferLock){
            
            if (!ReqAdvMsgBuffer.isEmpty()){
                
                return (JSONObject) ReqAdvMsgBuffer.remove(0);
                
            } else {
                return new JSONObject();
		
            }
            
        }

    }
    
    public static Vector getAllContentReqAdvBuffer(){

        Vector allReqAdvs = new Vector();
        
        synchronized (ReqAdvMsgBufferLock){
            
                allReqAdvs = ReqAdvMsgBuffer;
                
                ReqAdvMsgBuffer = new Vector();
                
        }
        
        return allReqAdvs;

    }
    
    
    /*
    public static void putDiscReqToDiscReqBuffer(JSONObject jsMsg){

        synchronized (DiscReqLock){
            DiscReqBuffer.addElement(jsMsg);
        }

    }
    
    public static JSONObject getDiscReqBuffer(){

        synchronized(DiscReqLock){
            if (!DiscReqBuffer.isEmpty()){
                
                return (JSONObject) DiscReqBuffer.remove(0);
                
            } else {
                return new JSONObject();
		
            }
        }

    }
    */

    public static void setInitAdvVisibOneByOne(String servName, String VisibStat){
        
        synchronized (AdvVisibListLock){
            try {
                AdvVisibList.put(servName,VisibStat);
            } catch (JSONException ex) {
                System.out.println("SJServiceRegistry, setInitAdvVisbOneByOne JSONexception: " +ex.getCause());
            }
        }
        
    }
    
    public static JSONObject getAllAdvVisib(){
        
        synchronized (AdvVisibListLock){
            return AdvVisibList;
        }
        
    }

    
      
      public static void SetAdvTransmissionRequest(boolean stat){
          
          synchronized (RequestForAdvTransmissionLock){
              RequestForAdvTransmission = stat;
          }
          
      }
      
      public static boolean getAdvTransmissionRequest(){
          
          boolean stat = false;
          
          synchronized (RequestForAdvTransmissionLock){
              stat = RequestForAdvTransmission;
          }
          
          return stat;
          
      }
      
      
      public static void RecordAdvertisementTimeStamp(){
        
        synchronized (advTimeStampLock){
            lastAdvertisementTime = System.currentTimeMillis();
        }
        
        
    }
    
    public static long getRecordedAdvertisementTimeStamp(){
        
        long advTimeStamp=0;
        
        synchronized (advTimeStampLock){
            
                advTimeStamp=lastAdvertisementTime;
           
        }
        return advTimeStamp;
    }
   
}