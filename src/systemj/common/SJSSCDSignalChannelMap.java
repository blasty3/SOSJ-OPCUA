/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.bootstrap.ClockDomain;

/**
 * Data structure
 * @author Udayanto
 */
public class SJSSCDSignalChannelMap {
    
   
    
    //private static Vector RequestCreateCDList = new Vector();
    //private final static Object RequestCreateCDListLock = new Object();
    
    /*
    private final static Object SInChanCoupleLock = new Object();
    private final static Object SOutChanCoupleLock = new Object();
    private final static Object AInChanCoupleLock = new Object();
    private final static Object AOutChanCoupleLock = new Object();
    
    private static Hashtable SInChanCouple = new Hashtable();
    private static Hashtable SOutChanCouple = new Hashtable();
    private static Hashtable AInChanCouple = new Hashtable();
    private static Hashtable AOutChanCouple = new Hashtable();
    */
    
    private static Vector destSSs = new Vector();
    private final static Object destSSsLock = new Vector();
    
    private static Hashtable CDPairSSLocation = new Hashtable();
    private final static Object CDPairSSLocationLock = new Object();
    
    private static JSONObject SSSignalChannelMap = new JSONObject();
    private static JSONObject SSSignalChannelPrevMap = new JSONObject();
    
    private static boolean requestCreateLink = false;
    private final static Object requestCreateLinkLock = new Object();
    
    private static boolean requestRemoveLink = false;
    private final static Object requestRemoveLinkLock = new Object();
   
    
    private final static Object SSSignalChannelMapLock = new Object();
    private final static Object SSSignalChannelPrevMapLock = new Object();
    
   
    private static JSONObject SSNonLocalMap = new JSONObject();
    private final static Object SSNonLocalMapLock = new Object();
    
    private static Vector LocalSSName = new Vector();
    private final static Object LocalSSNameLock = new Object();
    
    
    
    //private static InterfaceManager imObj = new InterfaceManager();
    //private final static Object imObjLock = new Object();
    
    private static Interconnection InterconnectionObj = new Interconnection();
    private final static Object InterconnectionObjLock = new Object();
    
    private static JSONObject InterConnectionDetails = new JSONObject();
    private static final Object InterConnectionDetailsLock = new Object();
    
    private static Hashtable InterConnLink = new Hashtable();
    private static final Object InterConnLinkLock = new Object();
    
    private static int interfaceIndex=1;
    private static int interconnLinkIndex=1;
    

    private static Hashtable InOutChannelObject = new Hashtable();
    private static final Object InOutChannelObjectLock = new Object();
    
    //private static Hashtable IcLinkLocal = new Vector();
    //private static Hashtable IcLinkDestination = new Vector();
    //private static final Object IcLinkLocalLock = new Object();
    //private static final Object IcLinkDestinationLock = new Object();
    
    private static final Object InterconnectSSAddrLock = new Object();
    private static Hashtable InterconnectSSAddr = new Hashtable();
    
    private static int ICPortNum=30000;
    private static final Object ICPortNumLock = new Object();
    
    private static Hashtable CDPartnerLocSS = new Hashtable();
    private volatile static String LocalSSAddr;
    
    private final static Object CDPartnerLocSSLock = new Object();
    
    private static volatile long SSExpiryTime;
    
    
    public static void SetLocalSSAddr(String Addr){
        LocalSSAddr = Addr;
    }
    
    public static void SetSSExpiryTime(long time){
        SSExpiryTime = time;
    }
    
    public static synchronized long GetSSExpiryTime(){
        return SSExpiryTime;
    }
    
    public static synchronized String GetLocalSSAddr(){
        return LocalSSAddr;
    }
    
    public static void SetReqCreateLink(){
        synchronized(requestCreateLinkLock){
            requestCreateLink = true;
        }
    }
    
    public static void ResetReqCreateLink(){
        synchronized(requestCreateLinkLock){
            requestCreateLink = false;
        }
    }
    
    public static boolean GetReqCreateLink(){
        synchronized(requestCreateLinkLock){
            return requestCreateLink;
        }
    }
    
    public static void SetCheckLinkToRemove(){
        synchronized(requestRemoveLinkLock){
            requestRemoveLink = true;
        }
    }
    
    public static void ResetCheckLinkToRemove(){
        synchronized(requestRemoveLinkLock){
            requestRemoveLink = false;
        }
    }
    
    public static boolean GetCheckLinkToRemove(){
        synchronized(requestRemoveLinkLock){
            return requestRemoveLink;
        }
    }
   
    public static void addUnusedDestSS(String SSName){
        synchronized(destSSsLock){
            destSSs.addElement(SSName);
        }
    }
    
    public static String getUnusedDestSS(int i){
        synchronized(destSSsLock){
            String ss = destSSs.get(i).toString();
            
            return ss;
        }
    }
    
    public static int getUnusedDestSSSize(){
        synchronized(destSSsLock){
            return destSSs.size();
        }
    }
    
    public static boolean IsUnusedICSSEmpty(){
        synchronized(destSSsLock){
            if (destSSs.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void clearUnusedICSS(){
        synchronized(destSSsLock){
            destSSs=new Vector();
        }
    }
    
    
   
    public static void addCDLocation(String CDName, String SSName){
        synchronized(CDPairSSLocationLock){
           CDPairSSLocation.put(CDName, SSName);
       }
    }
    
    public static String getCDLocation(String CDName){
        synchronized(CDPairSSLocationLock){
            return (String)CDPairSSLocation.get(CDName);
        }
    }
    
    public static boolean CDLocHasCDName(String CDName){
        synchronized(CDPairSSLocationLock){
            return CDPairSSLocation.containsKey(CDName);
        }
    }
    
    public static boolean CheckCDPairHasSSLocation(String SSName){
        synchronized(CDPairSSLocationLock){
            return CDPairSSLocation.containsValue(SSName);
        }
    }
    
    public static void addPortNum(){
        synchronized(ICPortNumLock){
            ICPortNum++;
        }
    }
    
    public static int getCurrentPortNum(){
        synchronized(ICPortNumLock){
            return ICPortNum;
        }
    }
    
    
    public static void addInterconnectionSSAddr(String SSName, String Addr){
        synchronized(InterconnectSSAddrLock){
            InterconnectSSAddr.put(SSName, Addr);
        }
    }
    
    public static String getInterconnectionSSAddr(String SSName){
         synchronized(InterconnectSSAddrLock){
             return (String)InterconnectSSAddr.get(SSName);
         }
    }
    
    
    
    
    
    
    
    //Method invoked during initialization\parsing process
    
    /*
    public static void InitAllSignalChannelToMap(JSONObject js){
        
        //Enumeration jsKeys = js.keys();
        
       // while (jsKeys.hasMoreElements()){
            //String key = jsKeys.nextElement().toString();
            
            synchronized (SSSignalChannelMapLock){
              //  try {
                    //SSSignalChannelMap.put(key,js.getJSONObject(key));
                    
                //} catch (JSONException ex) {
               //     ex.printStackTrace();
               // }
                SSSignalChannelMap = new JSONObject();
                SSSignalChannelMap=js;
                
            }
            
            synchronized(SSSignalChannelPrevMapLock){
                SSSignalChannelPrevMap = new JSONObject();
                SSSignalChannelPrevMap=js;
            }
            
            System.out.println("Initialized previous and current sigchan mapping");
          
        //}

    }
    */
    
    public static void AddOneCDToLocalCurrSignalChannelMap(String CDName, String LocSSName, JSONObject MapDet){
        
        synchronized(SSSignalChannelMapLock){
            try {
                JSONObject jsLocCDsFromMap = SSSignalChannelMap.getJSONObject(LocSSName);
                
                jsLocCDsFromMap.put(CDName, MapDet.getJSONObject(CDName));
                
                SSSignalChannelMap.put(LocSSName, jsLocCDsFromMap);
                
               // System.out.println("New map: " +SSSignalChannelMap.toPrettyPrintedString(2, 0));
                /*
                JSONObject newSigChanMap = new JSONObject();
                
                Enumeration keysJSAll = SSSignalChannelMap.keys();
                
                while(keysJSAll.hasMoreElements()){
                    String IndivSSName = keysJSAll.nextElement().toString();
                    
                    if(IndivSSName.equals(LocSSName)){
                         newSigChanMap.put(IndivSSName, SSSignalChannelMap.getJSONObject(IndivSSName));
                    } else {
                         newSigChanMap.put(CDName, jsLocCDsFromMap);
                    }
                    
                }
                
                SSSignalChannelMap = newSigChanMap;
                */
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    public static void AddOneCDToPrevCurrSignalChannelMap(String CDName, String LocSSName, JSONObject MapDet){
        
        synchronized(SSSignalChannelPrevMapLock){
            try {
                JSONObject jsLocCDsFromMap = SSSignalChannelPrevMap.getJSONObject(LocSSName);
                
                jsLocCDsFromMap.put(CDName, MapDet.getJSONObject(CDName));
                
                JSONObject newSigChanMap = new JSONObject();
                
                Enumeration keysJSAll = SSSignalChannelPrevMap.keys();
                
                while(keysJSAll.hasMoreElements()){
                    String IndivSSName = keysJSAll.nextElement().toString();
                    
                    if(IndivSSName.equals(LocSSName)){
                         newSigChanMap.put(IndivSSName, SSSignalChannelPrevMap.getJSONObject(IndivSSName));
                    } else {
                         newSigChanMap.put(CDName, jsLocCDsFromMap);
                    }
                    
                }
                
                SSSignalChannelPrevMap = newSigChanMap;
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    
    
    public static void UpdateAllCurrSignalChannelMapping(JSONObject js){
        
        synchronized(SSSignalChannelMapLock){
            SSSignalChannelMap = new JSONObject();
            SSSignalChannelMap=js;
        }
        
    }
    
    public static void RemoveOneCDCurrSigChannelMapping(String CDName, String SSName){
       
        JSONObject js = new JSONObject();
        
        synchronized(SSSignalChannelMapLock){
            js = SSSignalChannelMap;
            try {
                JSONObject jsAllCD = js.getJSONObject(SSName);
                jsAllCD.remove(CDName);
                js.put(SSName, jsAllCD);
                SSSignalChannelMap = js;
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    
    public static void UpdateAllSignalChannelPrevMap(JSONObject js){
        synchronized(SSSignalChannelPrevMapLock){
            SSSignalChannelPrevMap = new JSONObject();
            SSSignalChannelPrevMap=js;
        }
    }
   
    
    
    public static void InitAllNonLocalSSToMap(JSONObject js){
        
        Enumeration jsKeys = js.keys();
        
        while (jsKeys.hasMoreElements()){
            String key = jsKeys.nextElement().toString();
            
            synchronized (SSNonLocalMapLock){
                try {
                    SSNonLocalMap.put(key,js.getString(key));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
          
        }

    }
    
    public static JSONObject getCurrentSignalChannelMapping(){
        
            synchronized (SSSignalChannelMapLock){
                return SSSignalChannelMap;
            }
            
       
            
    }
    
    public static JSONObject getPrevSignalChannelMapping(){
        
        JSONObject js = new JSONObject();
        
            synchronized (SSSignalChannelPrevMapLock){
                js =  SSSignalChannelPrevMap;
            }
            return js;
    }
    
    public static void UpdateSignalChannelPrevMapping(String SSName, String CDName, String SignalName, String signalType, Hashtable config){
        
        JSONObject js1 = new JSONObject();
        
        //synchronized (SSSignalChannelPrevMapLock){
            
        //}
    }
    
    public static void AddOneSSToCurrSignalChanMapping(String SSName, JSONObject allCDsMapping){
        synchronized(SSSignalChannelMapLock){
            
            try {
                SSSignalChannelMap.put(SSName, allCDsMapping);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            
        }
    }
    
    
    
    
    
    
   // public static void putInputSignalInstanceToMap(String SSName){
     //   synchronized(LocalSSNameLock){
     //       LocalSSName.addElement(SSName);
     //   }
   // }
    
    //End initialization
    
    public static void addLocalSSName(String SSName){
        synchronized(LocalSSNameLock){
            if(LocalSSName.size()==1){
                LocalSSName.remove(0);
            }
            LocalSSName.addElement(SSName);
        }
    }
    
    public static String getLocalSSName(){
        synchronized(LocalSSNameLock){
            return LocalSSName.get(0).toString();
        }
    }
    
    public static boolean hasLocalSSName(){
        synchronized(LocalSSNameLock){
            if(LocalSSName.size()==0){
                return false;
            } else {
                return true;
            }
        }
    }
    
    public static boolean IsLocalSSEmpty(){
        synchronized(LocalSSNameLock){
            if(LocalSSName.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static boolean IsSSNameLocal(String SSName){
        
        boolean stat=false;
        
        synchronized(LocalSSNameLock){
            for (int i=0;i<LocalSSName.size();i++){
                String name = LocalSSName.get(i).toString();
                
                if(SSName.equalsIgnoreCase(name)){
                    stat=true;
                }
                
            }
        }
        
        return stat;
    }
    
    //Input and output signals, and GSR GSS Data structure
    
    
    
    //Signal Channel Changes details
    
    
    
    
    //Interconnection
    
  //  public static void saveInterConnection(Interconnection Interconnection){
   //     synchronized(InterconnectionObjLock){
   //         InterconnectionObj = Interconnection;
   //     }
   // }
    
   // public static Interconnection getInterConnection(){
    //    synchronized(InterconnectionObjLock){
    //        return InterconnectionObj;
    //    }
    //}
    
    public static void AddGenericInterfaceDet(String SSName, String ClassName, String InterfaceName, String ArgsName, String Type){
        
        synchronized(InterConnectionDetailsLock){
            //JSONObject js = new JSONObject();
            
            try {
                
                if(InterConnectionDetails.has(SSName)){
                    
                    JSONObject js2 = InterConnectionDetails.getJSONObject(SSName);
                    
                        JSONObject js3 = new JSONObject();
                        js3.put("Class",ClassName);
                        js3.put("Interface", InterfaceName);
                        js3.put("Args", ArgsName);
                        js3.put("SSName", SSName);
                        js2.put(Type, js3);
                        InterConnectionDetails.put(SSName, js2);
                } else {
                        JSONObject js3 = new JSONObject();
                        JSONObject js2 = new JSONObject();
                        js3.put("Class",ClassName);
                        js3.put("Interface", InterfaceName);
                        js3.put("Args", ArgsName);
                        js3.put("SSName", SSName);
                        js2.put(Type, js3);
                        InterConnectionDetails.put(SSName, js2);
                }
                
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    public static String getRemoteGenericInterfaceAddr(String SSName){
        synchronized(InterConnectionDetailsLock){
            try {
                
                if(InterConnectionDetails.has(SSName)){
                    JSONObject js2 = InterConnectionDetails.getJSONObject(SSName);
                JSONObject js3 = js2.getJSONObject("Destination");
                //String args = js3.getString("Args");
                
                String[] args = ((String)js3.get("Args")).trim().split(":");
			
			String ip = args[0];
                        
                        return ip;
                } else {
                    System.out.println("Subsystem: " +SSName+" is not available");
                    return "0.0.0.0";
                }
                
            } catch (JSONException ex) {
                ex.printStackTrace();
                return "0.0.0.0";
            }
        }
    }
    
    public static void addInterconnectionLink(String SSName, Object link){
        synchronized(InterConnLinkLock){
           // try {
                InterConnLink.put(SSName, link);
            //} catch (JSONException ex) {
           //     ex.printStackTrace();
           // }
        }
        
        //synchronized(InterConnIndLock){
        //    interconnLinkIndex++;
        //}
    }
    
    public static Object getInterconnectionLink(String SSName){
        synchronized(InterConnLinkLock){
         //   try {
                return InterConnLink.get(SSName);
         //   } catch (JSONException ex) {
         //       ex.printStackTrace();
         //       return new Object();
         //   }
        }
    }
    
    public static boolean HasInterconnectionLink(String SSName){
        synchronized(InterConnLinkLock){
            
                return InterConnLink.containsKey(SSName);
            
        }
    }
    
    public static Hashtable getAllInterconnectionLink(){
        synchronized(InterConnLinkLock){
            return InterConnLink;
        }
    }
    
    public static JSONObject getInterconnectionGenericInterfaceDet(){
        synchronized(InterConnectionDetailsLock){
            return InterConnectionDetails;
        }
    }
    
    public static boolean HasInterconnectionGenericInterfaceDet(String SSName){
        synchronized(InterConnectionDetailsLock){
            return InterConnectionDetails.has(SSName);
        }
    }
    
    public static JSONObject getInterconnectionGenericInterfaceDet(String SSName){
        synchronized(InterConnectionDetailsLock){
            try {
                return (JSONObject)InterConnectionDetails.get(SSName);
            } catch (JSONException ex) {
                ex.printStackTrace();
                return new JSONObject();
            }
        }
    }
    
    
    /*
    public static void addInOutChannelObjectToMap(String SSName, String CDName, String ChannelType, String ChannelDirection, String ChannelName, Vector InOutChannelObj){
        
            //js2.put(ChannelDirection, js);
           // js3.put(ChannelType,js2);
           // js4.put(CDName, js3);
            Hashtable has1= new Hashtable();
            
            System.out.println("Executing addInOutChannelObjectToMap");
             
            synchronized(InOutChannelObjectLock){
                
                has1 = InOutChannelObject;
            }
               // try {
                
                if (has1.containsKey(SSName)){
                    
                    Hashtable jsCDs = (Hashtable)has1.get(SSName);
                    
                    System.out.println("InOutChanObj CDs: " +jsCDs);
                    
                    if (jsCDs.containsKey(CDName)){
                        Hashtable jsChannelType = (Hashtable)jsCDs.get(CDName);
                        
                        System.out.println("InOutChanObj CD: " +jsChannelType);
                        
                        if (jsChannelType.containsKey(ChannelType)){
                            
                            Hashtable jsChannelDirection = (Hashtable)jsChannelType.get(ChannelType);
                            
                            if (jsChannelDirection.containsKey(ChannelDirection)){
                                
                                Hashtable jsChannelNames = (Hashtable)jsChannelDirection.get(ChannelDirection);
                                
                                jsChannelNames.put(ChannelName,InOutChannelObj);
                                
                                jsChannelDirection.put(ChannelDirection, jsChannelNames);
                                
                                jsChannelType.put(ChannelType,jsChannelDirection);
                                jsCDs.put(CDName,jsChannelType);
                                has1.put(SSName, jsCDs);
                                
                            } else {
                                
                                //JSONObject jsChannelNames= new JSONObject();
                                
                                Hashtable jsChannelNames= new Hashtable();
                                
                                jsChannelNames.put(ChannelName, InOutChannelObj);
                                
                                jsChannelDirection.put(ChannelDirection, jsChannelNames);
                                
                                jsChannelType.put(ChannelType,jsChannelDirection);
                                
                                jsCDs.put(CDName, jsChannelType);
                                
                                has1.put(SSName, jsCDs);
                                
                            }
                            
                        } else {
                            
                            //JSONObject js1 = new JSONObject();
                            //JSONObject js2 = new JSONObject();
                            //JSONObject js3 = new JSONObject(); 
                            
                            Hashtable js1 = new Hashtable();
                            Hashtable js2 = new Hashtable();
                            
                            js1.put(ChannelName, InOutChannelObj);
                            js2.put(ChannelDirection,js1);
                            jsChannelType.put(ChannelType,js2);
                            jsCDs.put(CDName,jsChannelType);
                            
                            has1.put(SSName, jsCDs);
                            
                        }
                        
                    } else {
                        
                        //JSONObject js1 = new JSONObject();
                        //JSONObject js2 = new JSONObject();
                        //JSONObject js3 = new JSONObject();
                        //JSONObject js4 = new JSONObject();
                        
                       Hashtable js1 = new Hashtable();
                       Hashtable js2 = new Hashtable();
                       Hashtable js3 = new Hashtable();
                       
                        
                        
                        js1.put(ChannelName, InOutChannelObj);
                        js2.put(ChannelDirection,js1);
                        js3.put(ChannelType,js2);
                        jsCDs.put(CDName, js3);
                        has1.put(SSName, jsCDs);
                        
                        System.out.println("adding" +CDName+", producing: " +has1);
                    }
                    
                } else {
                    
                   // JSONObject js1 = new JSONObject();
                   //     JSONObject js2 = new JSONObject();
                    //    JSONObject js3 = new JSONObject();
                    //    JSONObject js4 = new JSONObject();
                     Hashtable js1 = new Hashtable();
                       Hashtable js2 = new Hashtable();
                       Hashtable js3 = new Hashtable();
                       Hashtable js4 = new Hashtable();
                        
                       
                       
                        js1.put(ChannelName, InOutChannelObj);
                        js2.put(ChannelDirection,js1);
                        js3.put(ChannelType,js2);
                        js4.put(CDName, js3);
                        has1.put(SSName, js4);
                        
                        System.out.println("adding" +SSName+", producing: " +has1);
                    
                }
               
             //   } catch (JSONException ex) {
            //        ex.printStackTrace();
                // }
                synchronized(InOutChannelObjectLock){
                     InOutChannelObject = has1;
                }
                
               
            
            
    }
    */
    
    public static void addInOutChannelObjectToMap(String SSName, String CDName, String ChannelType, String ChannelDirection, String ChannelName, Object InOutChannelObj){
        
            //js2.put(ChannelDirection, js);
           // js3.put(ChannelType,js2);
           // js4.put(CDName, js3);
            Hashtable has1= new Hashtable();
            
            //System.out.println("Executing addInOutChannelObjectToMap");
             
            synchronized(InOutChannelObjectLock){
                
                has1 = InOutChannelObject;
            }
               // try {
                
                if (has1.containsKey(SSName)){
                    
                    Hashtable jsCDs = (Hashtable)has1.get(SSName);
                    
                    //System.out.println("InOutChanObj CDs: " +jsCDs);
                    
                    if (jsCDs.containsKey(CDName)){
                        Hashtable jsChannelType = (Hashtable)jsCDs.get(CDName);
                        
                        //System.out.println("InOutChanObj CD: " +jsChannelType);
                        
                        if (jsChannelType.containsKey(ChannelType)){
                            
                            Hashtable jsChannelDirection = (Hashtable)jsChannelType.get(ChannelType);
                            
                            if (jsChannelDirection.containsKey(ChannelDirection)){
                                
                                Hashtable jsChannelNames = (Hashtable)jsChannelDirection.get(ChannelDirection);
                                
                                jsChannelNames.put(ChannelName,InOutChannelObj);
                                
                                jsChannelDirection.put(ChannelDirection, jsChannelNames);
                                
                                jsChannelType.put(ChannelType,jsChannelDirection);
                                jsCDs.put(CDName,jsChannelType);
                                has1.put(SSName, jsCDs);
                                
                            } else {
                                
                                //JSONObject jsChannelNames= new JSONObject();
                                
                                Hashtable jsChannelNames= new Hashtable();
                                
                                jsChannelNames.put(ChannelName, InOutChannelObj);
                                
                                jsChannelDirection.put(ChannelDirection, jsChannelNames);
                                
                                jsChannelType.put(ChannelType,jsChannelDirection);
                                
                                jsCDs.put(CDName, jsChannelType);
                                
                                has1.put(SSName, jsCDs);
                                
                            }
                            
                        } else {
                            
                            //JSONObject js1 = new JSONObject();
                            //JSONObject js2 = new JSONObject();
                            //JSONObject js3 = new JSONObject(); 
                            
                            Hashtable js1 = new Hashtable();
                            Hashtable js2 = new Hashtable();
                            
                            js1.put(ChannelName, InOutChannelObj);
                            js2.put(ChannelDirection,js1);
                            jsChannelType.put(ChannelType,js2);
                            jsCDs.put(CDName,jsChannelType);
                            
                            has1.put(SSName, jsCDs);
                            
                        }
                        
                    } else {
                        
                        //JSONObject js1 = new JSONObject();
                        //JSONObject js2 = new JSONObject();
                        //JSONObject js3 = new JSONObject();
                        //JSONObject js4 = new JSONObject();
                        
                       Hashtable js1 = new Hashtable();
                       Hashtable js2 = new Hashtable();
                       Hashtable js3 = new Hashtable();
                       
                        
                        
                        js1.put(ChannelName, InOutChannelObj);
                        js2.put(ChannelDirection,js1);
                        js3.put(ChannelType,js2);
                        jsCDs.put(CDName, js3);
                        has1.put(SSName, jsCDs);
                        
                        //System.out.println("adding" +CDName+", producing: " +has1);
                    }
                    
                } else {
                    
                   // JSONObject js1 = new JSONObject();
                   //     JSONObject js2 = new JSONObject();
                    //    JSONObject js3 = new JSONObject();
                    //    JSONObject js4 = new JSONObject();
                     Hashtable js1 = new Hashtable();
                       Hashtable js2 = new Hashtable();
                       Hashtable js3 = new Hashtable();
                       Hashtable js4 = new Hashtable();
                        
                       
                       
                        js1.put(ChannelName, InOutChannelObj);
                        js2.put(ChannelDirection,js1);
                        js3.put(ChannelType,js2);
                        js4.put(CDName, js3);
                        has1.put(SSName, js4);
                        
                        //System.out.println("adding" +SSName+", producing: " +has1);
                    
                }
               
             //   } catch (JSONException ex) {
            //        ex.printStackTrace();
                // }
                synchronized(InOutChannelObjectLock){
                     InOutChannelObject = has1;
                }
                
               
            
            
    }
    
    public static Hashtable getAllInOutChannelObjects(){
        synchronized(InOutChannelObjectLock){
            return InOutChannelObject;
        }
    }
    
    public static Object getInOutChannelObject(String SSName, String CDName, String ChannelType, String ChannelDirection, String ChannelName){
        
            //js2.put(ChannelDirection, js);
           // js3.put(ChannelType,js2);
           // js4.put(CDName, js3);
             
            synchronized(InOutChannelObjectLock){
                
               // try {
                
                    if (InOutChannelObject.containsKey(SSName)){
                    Hashtable jsCDs = (Hashtable)InOutChannelObject.get(SSName);
                    
                    if (jsCDs.containsKey(CDName)){
                        Hashtable jsChannelType = (Hashtable)jsCDs.get(CDName);
                        
                        if (jsChannelType.containsKey(ChannelType)){
                            
                            Hashtable jsChannelDirection = (Hashtable)jsChannelType.get(ChannelType);
                            
                            if (jsChannelDirection.containsKey(ChannelDirection)){
                                
                                Hashtable jsChannelNames = (Hashtable)jsChannelDirection.get(ChannelDirection);
                                
                                if (jsChannelNames.containsKey(ChannelName)){
                                    Object InOutChanObj = jsChannelNames.get(ChannelName);
                                    return InOutChanObj;
                                } else {
                                    
                                    
                                    return new Object();
                                }
                               
                            } else {
                                
                                return new Object();
                                
                            }
                            
                        } else {
                            
                            return new Object();
                            
                        }
                        
                    } else {
                        
                        return new Object();
                    }
                    
                } else {
                    
                    return new Object();
                    
                }
               
               // } catch (JSONException ex) {
               //     ex.printStackTrace();
              //      return new Vector();
              //  }
                
       }
       
    }
    
   // public static void addCDNameToSSLocation(String CDName, String SSName){
   //     synchronized(CDLocationInSSLock){
           
   //             CDLocationInSS.put(CDName, SSName);
            
   //     }
 //   }
    
  //  public static String getSSLocationOfCD(String CDName){
  //      synchronized(CDLocationInSSLock){
            
   //             return (String)CDLocationInSS.get(CDName);
             
  //          
  //      }
   // }
    /*
    public static void addLocalLink(Interconnection ic){
        synchronized(IcLinkLocalLock){
             
               IcLinkLocal.addElement(ic);
            
        }
    }
    
    public static void addDestinationLink(Interconnection ic){
        synchronized(IcLinkDestinationLock){
             
               IcLinkDestination.addElement(ic);
            
        }
    }
    
    public static String getLinkType(String SSName){
        synchronized(IcLinkTypeLock){
            try {
                return IcLinkType.getString(SSName);
            } catch (JSONException ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }
    */
    
    public static void AddChanLinkUserToSS(String DestSSname, String cdName, String ChanDir, String ChanName){
            
        synchronized(CDPartnerLocSSLock){
            
            if(CDPartnerLocSS.containsKey(DestSSname)){
                
                Vector vec = (Vector) CDPartnerLocSS.get(DestSSname);
                
                for(int i=0;i<vec.size();i++){
                    
                   Hashtable hash = (Hashtable)vec.get(i);
                   
                   if(hash.containsKey(cdName)){
                       
                       Hashtable hashInOutChans = (Hashtable)hash.get(cdName);
                       
                       if(hashInOutChans.containsKey(ChanDir)){
                           
                           Vector InChans = (Vector) hashInOutChans.get(ChanDir);
                           
                           InChans.addElement(ChanName);
                           
                           hashInOutChans.put(ChanDir,InChans);
                           
                           hash.put(cdName, hashInOutChans);
                           
                           vec.remove(i);
                           
                           vec.addElement(hash);
                           //vec.insertElementAt(hash, i);
                           
                       } else {
                           Vector InChans = new Vector();
                           InChans.addElement(ChanName);
                           hashInOutChans.put(ChanDir, InChans);
                           hash.put(cdName, hashInOutChans);
                           vec.remove(i);
                           vec.addElement(hash);
                           
                           //vec.insertElementAt(hash, i);
                           
                       }
                       
                   } else {
                       
                           Vector InChans = new Vector();
                       
                           InChans.addElement(ChanName);
                           
                           Hashtable hashInOutChans = new Hashtable();
                           
                           hashInOutChans.put(ChanDir, InChans);
                           
                           hash.put(cdName, hashInOutChans);
                           
                           vec.remove(i);
                           vec.addElement(hash);
                          // vec.insertElementAt(hash, i);
                           
                       
                   }
                   
                }
                
                CDPartnerLocSS.put(DestSSname, vec);
                
            } else {
                
                Vector vec = new Vector();
                
                Hashtable hash = new Hashtable();
                
                Hashtable hashInOutChans = new Hashtable();
                
                Vector chans = new Vector();
                
                chans.addElement(ChanName);
                
                hashInOutChans.put(ChanDir, chans);
                
                hash.put(cdName, hashInOutChans);
                
                vec.addElement(hash);
                
                //System.out.println("One InterConnection: " +vec);
                
                CDPartnerLocSS.put(DestSSname, vec);
               
            }
            
        }
        
            
            
            //System.out.println("All InterConnection: " +CDPartnerLocSS);
            
        }
        
        public static void removeChanPartner(String ssname,String cdName, String ChanDir,String ChanName){
            
            synchronized(CDPartnerLocSSLock){
                if(CDPartnerLocSS.containsKey(ssname)){
                
                    Vector vec = (Vector) CDPartnerLocSS.get(ssname);

                    for(int i=0;i<vec.size();i++){
                       Hashtable hash = (Hashtable)vec.get(i);

                       if(hash.containsKey(cdName)){

                           Hashtable hashInOutChans = (Hashtable)hash.get(cdName);

                           if(hashInOutChans.containsKey(ChanDir)){

                               Vector InChans = (Vector) hashInOutChans.get(ChanDir);

                               for(int j=0;j<InChans.size();j++){
                                   
                                   String chanNam = (String)InChans.get(j);
                                   
                                   if(chanNam.equals(ChanName)){
                                       
                                        //InChans.removeElementAt(j);
                                        
                                        if(InChans.size()==1){
                                            hashInOutChans.remove(ChanDir);
                                        } else {
                                            InChans.removeElementAt(j);
                                            hashInOutChans.put(ChanDir,InChans);
                                        }
                                        
                                        if(hashInOutChans.isEmpty()){
                                            hash.remove(cdName);
                                        } else {
                                            hash.put(cdName, hashInOutChans);
                                        }
                                        //hashInOutChans.put(ChanDir,InChans);

                                        //hash.put(cdName, hashInOutChans);
                                        if(hash.isEmpty()){
                                            vec.remove(i);
                                        } else {
                                            //vec.insertElementAt(hash, i);
                                            vec.remove(i);
                                            vec.addElement(hash);
                                            //vec.add(i, hash);
                                        }
                                        //here
                                        if(vec.size()==0){
                                            CDPartnerLocSS.remove(ssname);
                                        } else{
                                            CDPartnerLocSS.put(ssname, vec);
                                        }
                                        //vec.insertElementAt(hash, i);
                                        
                                   }
                                   
                               }
                               
                           } 

                       } 

                    }

                }
            }
               
        }
        
        public static boolean IsAnyChanUseLinkToSS(String DestSSname){
            synchronized(CDPartnerLocSSLock){
                if(CDPartnerLocSS.containsKey(DestSSname)){
                 return true;
                } else {
                    return false;
                }
            }
            
        }
        
        public static Hashtable GetAllSSPartnerLinkToUse(){
            synchronized(CDPartnerLocSSLock){
                return CDPartnerLocSS;
            }
            
        }
           
    
    
}
