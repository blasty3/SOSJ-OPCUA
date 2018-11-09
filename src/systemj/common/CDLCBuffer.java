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
import systemj.interfaces.Scheduler;


/**
 *
 * @author Udayanto
 */
public class CDLCBuffer {
    
    private static JSONObject AllCDMStateInLocSS = new JSONObject();
    private static final Object AllCDMStateInLocSSLock = new Object();
    
    private static final Object LocalCDAmountChangedFlagLock = new Object();
    private static boolean LocalCDAmountChangedFlag = false;
    
    private static final Object CDLCMigrationThreadBusyFlagLock = new Object();
    private static boolean CDLCMigrationThreadBusyFlag = false;
    
    //private static Vector vecAllMigCDName = new Vector();
    //private static final Object vecAllMigCDNameLock = new Object();
    private static boolean UpdateIMMigration = false;
    private static final Object UpdateIMMigrationLock = new Object();
    
    private static final Object AllCDInsVecLock = new Object();
    private static Vector AllCDInsVec = new Vector();
    
    private static boolean IMVecFlag = false;
    private final static Object IMVecFlagLock = new Object();
    
    private static JSONObject LocalCDSignalLocationChanges = new JSONObject();
    private static JSONObject CDChannelLocationChanges = new JSONObject();
    private static final Object LocalCDSignalLocationChangesLock = new Object();
    private static final Object CDChannelLocationChangesLock = new Object();
    
    private static boolean ICNeedToRemoveFlag = false;
    private final static Object ICNeedToRemoveFlagLock = new Object();
    
    private static boolean ICChangedFlag = false;
    private final static Object ICChangedFlagLock = new Object();
    
    private static boolean CDLifeCycleChanged = false;
    private final static Object CDLifeCycleChangedLock = new Object();
    
    private static Hashtable RequestCloneAndMigrateCDList = new Hashtable();
    private final static Object RequestCloneAndMigrateCDListLock = new Object();
    
    private static JSONObject ReqCloneAndMigrateTempSSSignalChannelMap = new JSONObject();
    private final static Object RequestCloneAndMigrateCDMapListLock = new Object();
    
    private static JSONObject ReqCloneAndMigrateTempServiceDescription = new JSONObject();
    private final static Object ReqCloneAndMigrateTempServiceDescriptionLock = new Object();
    
    private static Hashtable RequestCloneCDList = new Hashtable();
    private final static Object RequestCloneCDListLock = new Object();
    
    private static JSONObject ReqCloneTempSSSignalChannelMap = new JSONObject();
    private final static Object RequestCloneCDMapListLock = new Object();
    
    private static JSONObject ReqCloneTempServiceDescription = new JSONObject();
    private final static Object ReqCloneTempServiceDescriptionLock = new Object();
    
    private static Vector RequestUpdateCDList = new Vector();
    private final static Object RequestUpdateCDListLock = new Object();
    
    private static JSONObject UpdateTempSSSignalChannelMap = new JSONObject();
    private final static Object UpdateTempSSSSignalChannelMapLock = new JSONObject();
    
    private static JSONObject UpdateTempServiceDesc = new JSONObject();
    private final static Object UpdateTempServiceDescLock = new Object();
    
    private static JSONObject TempSSSignalChannelMap = new JSONObject();
    private final static Object TempSSSSignalChannelMapLock = new JSONObject();
    
    private static JSONObject TempMigrateSSSignalChannelMap = new JSONObject();
    private final static Object TempMigrateSSSignalChannelMapLock = new Object();
    
    private static JSONObject TempMigrateServDesc = new JSONObject();
    private final static Object TempMigrateServDescLock  = new Object();
    
    private static Vector StrongMigratedCDName = new Vector();
    private final static Object StrongMigratedCDNameLock = new Object();
    
    private static Vector WeakMigratedCDName = new Vector();
    private final static Object WeakMigratedCDNameLock = new Object();
    
    private static Hashtable CDNameTimeLogin = new Hashtable();
    private final static Object CDNameTimeLoginLock = new Object();
    
    private static Vector RequestCreateCDList = new Vector();
    private final static Object RequestCreateCDListLock = new Object();
    
    private static Vector RequestActivateCDList = new Vector();
    private final static Object RequestActivateCDListLock = new Object();
    
    private static Vector RequestHibernateCDList = new Vector();
    private final static Object RequestHibernateCDListLock = new Object();
    
    private static Hashtable RequestMigrateBuffer = new Hashtable();
    private final static Object RequestMigrateBufferLock = new Object();
    
    private static Hashtable MigrationReqReport = new Hashtable();
    private final static Object MigrationReqReportLock = new Object();
    
    //private static Vector RequestHibernateCDListWithTimer = new Vector();
    //private final static Object RequestHibernateCDListWithTimerLock = new Object();
    
    private static Vector RequestWakeUpCDList = new Vector();
    private final static Object RequestWakeUpCDListLock = new Object();
    
    private static Vector RequestKillCDList = new Vector();
    private final static Object RequestKillCDListLock = new Object();
    
    private static JSONObject MigTempSSSignalChannelMap = new JSONObject();
    private final static Object MigTempSSSignalChannelMapLock = new Object();
    
     private static boolean LocalCDChangedFlag = false;
    private final static Object LocalCDChangedFlagLock = new Object();
    
    private static final Object ReqServiceMigrationBufferLock = new Object();
    private static JSONObject ReqServiceMigrationBuffer = new JSONObject();
   
    private static final Object MigrationReqMsgBufferLock = new Object();
    private static Vector MigrationReqMsgBuffer = new Vector();
    
    private static final Object MigrationRespMsgBufferLock = new Object();
    private static JSONObject MigrationRespMsgBuffer = new JSONObject();
    
   // private static final Object MigrationDetailsLock = new Object();
    //private static Vector MigrationDetails = new Vector();
    //private static int MigrationIndex = 0;
    
    private static Vector MigrationStat = new Vector();
    private static final Object MigrationStatLock = new Object();
    
    private final static Object MigrationBusyFlagLock = new Object();
    private static boolean MigrationBusyFlag = false;
    
    private final static Object WeakMigrationDoneLock = new Object();
    private static boolean WeakMigrationDoneFlag = false;
    
    private final static Object StrongMigrationDoneLock = new Object();
    private static boolean StrongMigrationDoneFlag = false;
    
    private final static Object LinkCreationPortBusyFlagLock = new Object();
    private static boolean LinkCreationBusyFlag = false;
    
    private static boolean MigGoAheadModifyPartner = false;
    private static boolean MigModifyPartnerDone = false;
    private static Scheduler schedNow;
    private static Hashtable MigSSMigTypeCDObjs = new Hashtable();
    
    private static final Object MigGoAheadModifyPartnerLock  = new Object();
    private static final Object MigModifyPartnerDoneLock = new Object();
    private static final Object schedNowLock = new Object();
    private static final Object MigCDObjsLock = new Object();
  
    private static final Object signalIndLock = new Object();
    private static final Object channelIndLock = new Object();
    
    private static Vector MigratingCDNameBuffer = new Vector();
    private static final Object MigratingCDNameBufferLock = new Object();
    private static Vector MigratingCDInstances = new Vector();
    private static final Object MigratingCDInstancesLock = new Object();
    
    private static int signalIndex=1;
    private static int channelIndex=1;
    
    private static final Object CDSSLocBufferLock = new Object();
    private static Hashtable CDSSLocBuffer = new Hashtable();
    
     private static final Object SSLocBufferLock = new Object();
    private static Hashtable SSLocBuffer = new Hashtable();
    
    private static final Object TempDevelCreateCDBufferLock = new Object();
    private static final Object CodeRecThreadDevelBusyLock = new Object();
    private static boolean CodeRecThreadDevelBusy = false;
    private static Vector TempDevelCreateCDBuffer = new Vector();
    
    private static final Object ChanReconfigureBufferLock = new Object();
    private static Vector ChanReconfigureBuffer = new Vector();
    
    private static final Object PartnerChanReconfigureBufferLock = new Object();
    private static Vector PartnerChanReconfigureBuffer = new Vector();
    
    private static final Object InvokeServChanReconfigureBufferLock = new Object();
    private static Vector InvokeServChanReconfigureBuffer = new Vector();
    
    private static final Object InvokeServPartnerChanReconfigureBufferLock = new Object();
    private static Vector InvokeServPartnerChanReconfigureBuffer = new Vector();
    
    private static final Object InvokeServ2ChanReconfigureBufferLock = new Object();
    private static Hashtable InvokeServ2ChanReconfigureBuffer = new Hashtable();
    
    private static final Object InvokeServ2StatChanReconfigureBufferLock = new Object();
    private static JSONObject InvokeServ2StatChanReconfigureBuffer = new JSONObject();
    
    private static final Object StatChanReconfigureBufferLock = new Object();
    private static JSONObject StatChanReconfigureBuffer = new JSONObject();
    
    private static final Object ReconfigInChanConfigIMBufferLock = new Object();
    private static Vector ReconfigInChanConfigIMBuffer = new Vector();
    
    public static void AddReconfigInChanConfigIMBuffer(String InChanCDName, String InChanChanName, String NewPartnerCDName, String NewPartnerChanName, String PartnerSSName){
        synchronized(ReconfigInChanConfigIMBufferLock){
            Hashtable hash = new Hashtable();
            
            hash.put("InchanName", InChanChanName);
            hash.put("InchanCDName", InChanCDName);
            hash.put("PartnerCDName", NewPartnerCDName);
            hash.put("PartnerChanCDName", NewPartnerChanName);
            hash.put("SSName", PartnerSSName);
            
            ReconfigInChanConfigIMBuffer.addElement(hash);
        }
    }
    
    public static boolean IsReconfigInChanConfigIMBufferEmpty(){
        synchronized(ReconfigInChanConfigIMBufferLock){
            
            if(ReconfigInChanConfigIMBuffer.size()==0){
                return true;
            } else {
                return false;
            }
                    
            
        }
    }
    
    public static Vector GetReconfigInChanConfigIMBuffer(){
        synchronized(ReconfigInChanConfigIMBufferLock){
            return ReconfigInChanConfigIMBuffer;
        }
    }
    
    public static void ClearReconfigInChanConfigIMBuffer(){
        synchronized(ReconfigInChanConfigIMBufferLock){
            ReconfigInChanConfigIMBuffer = new Vector();
        }
    }
    
    public static void AddCDMacroState(String CDName, String CDState){
        synchronized(AllCDMStateInLocSSLock){
            try {
                AllCDMStateInLocSS.put(CDName, CDState);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static String GetCDMacroState(String CDName){
        synchronized(AllCDMStateInLocSSLock){
            try {
                return AllCDMStateInLocSS.getString(CDName);
            } catch (JSONException ex) {
                ex.printStackTrace();
                return "None";
            }
        }
    }
    
    public static JSONObject GetAllCDMacroState(){
        synchronized(AllCDMStateInLocSSLock){
            return AllCDMStateInLocSS;
        }
    }
    
    public static void RemoveCDMacroState(String CDName){
        synchronized(AllCDMStateInLocSSLock){
            if(AllCDMStateInLocSS.has(CDName)){
                AllCDMStateInLocSS.remove(CDName);
            }
        }
    }
    
    public static void AddCDLocTempToBuffer(String CDName, String SSLocName){
        synchronized(CDSSLocBufferLock){
            CDSSLocBuffer.put(CDName, SSLocName);
        }
    }
    
    public static Hashtable GetAllCDLocTempBuffer(){
         synchronized(CDSSLocBufferLock){
            return CDSSLocBuffer;
        }
    }
    
    public static String GetCDLocTempBuffer(String CDName){
         synchronized(CDSSLocBufferLock){
            return CDSSLocBuffer.get("CDName").toString();
        }
    }
    
    public static boolean IsCDSSLocTempBufferEmpty(){
         synchronized(CDSSLocBufferLock){
            if(CDSSLocBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearCDLocTempBuffer(){
        synchronized(CDSSLocBufferLock){
            CDSSLocBuffer = new Hashtable();
        }
    }
    
    public static void AddSSLocTempToBuffer(String SSName, String SSAddr){
        synchronized(SSLocBufferLock){
            SSLocBuffer.put(SSName, SSAddr);
        }
    }
    
    public static Hashtable GetAllSSLocTempBuffer(){
         synchronized(SSLocBufferLock){
            return SSLocBuffer;
        }
    }
    
    public static String GetSSAddrOfSSName(String SSName){
         synchronized(SSLocBufferLock){
            return SSLocBuffer.get(SSName).toString();
        }
    }
    
    public static boolean IsSSLocTempBufferEmpty(){
         synchronized(SSLocBufferLock){
            if(SSLocBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearSSLocTempBuffer(){
        synchronized(SSLocBufferLock){
            SSLocBuffer = new Hashtable();
        }
    }
   
    public static void AddDevelCreateCD(String CDName, JSONObject CDMap, JSONObject CDServDesc){
        synchronized(TempDevelCreateCDBufferLock){
            Hashtable req = new Hashtable();
            
            req.put("CDName",CDName);
            req.put("CDMap", CDMap);
            req.put("CDServDesc", CDServDesc);
            
            TempDevelCreateCDBuffer.addElement(req);
            
        }
    }
    
    public static boolean IsDevelCreateCDEmpty(){
        synchronized(TempDevelCreateCDBufferLock){
            if(TempDevelCreateCDBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearDevelCreateCD(){
        synchronized(TempDevelCreateCDBufferLock){
            
            TempDevelCreateCDBuffer = new Vector();
            
        }
    }
    
    public static Vector getDevelCreateCD(){
        synchronized(TempDevelCreateCDBufferLock){
            Vector vec = new Vector();
            
            for(int i=0;i<TempDevelCreateCDBuffer.size();i++){
                
                vec.addElement((Hashtable)TempDevelCreateCDBuffer.get(i));
                
            }
            return vec;
            
        }
    }
    
    public static void SetDevelThreadBusyFlag(boolean stat){
        synchronized(CodeRecThreadDevelBusyLock){
            CodeRecThreadDevelBusy = stat;
        }
    }
    
    public static boolean GetDevelThreadBusyFlag(){
        synchronized(CodeRecThreadDevelBusyLock){
            return CodeRecThreadDevelBusy ;
        }
    }
    
    
    
    public static boolean AddInvokeServ2ChanReconfig(String chcdname, String cname, String cdirection, String partnercdname, String partnercname){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                //hash2.put("IsLocal", Boolean.toString(IsLocal));
                
                if(InvokeServ2ChanReconfigureBuffer.containsKey(partnercdname)){
                    InvokeServ2ChanReconfigureBuffer.put(partnercdname,hash2);
                    return true;
                } else {
                    return false;
                }
                
        }
    }
    
    public static boolean AddInvokeServ2ChanReconfig(String ssdest, String destAddr, String chcdname, String cname, String cdirection, String partnercdname, String partnercname){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                //hash2.put("IsLocal", Boolean.toString(IsLocal));
                //hash2.put("SrcAddr",srcAddr);
                //hash2.put("SrcPort", srcPort);
                hash2.put("DestSS", ssdest);
                hash2.put("DestAddr",destAddr);
                
                if(InvokeServ2ChanReconfigureBuffer.containsKey(partnercdname)){
                    InvokeServ2ChanReconfigureBuffer.put(partnercdname,hash2);
                    return true;
                } else {
                    return false;
                }
                
        }
    }
    
    public static boolean AddInvokeServ2ChanReconfig(String ssdest, String destAddr, String destPort,String chcdname, String cname, String cdirection, String partnercdname, String partnercname){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                //hash2.put("IsLocal", Boolean.toString(IsLocal));
                //hash2.put("SrcAddr",srcAddr);
                //hash2.put("SrcPort", srcPort);
                hash2.put("DestSS", ssdest);
                hash2.put("DestAddr",destAddr);
                hash2.put("DestPort", destPort);
                if(InvokeServ2ChanReconfigureBuffer.containsKey(partnercdname)){
                    InvokeServ2ChanReconfigureBuffer.put(partnercdname,hash2);
                    return true;
                } else {
                    return false;
                }
                
        }
    }
    
    public static boolean IsInvokeServ2ChanReconfigBufferEmpty(){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
            if(InvokeServ2ChanReconfigureBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearInvokeServ2ChanReconfigBuffer(){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
            InvokeServ2ChanReconfigureBuffer = new Hashtable();
        }
    }
    
    public static Hashtable GetReconfigInvokeServ2ChanBuffer(){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
            return InvokeServ2ChanReconfigureBuffer;
        }
    }
    
    public static void AddAllInvokeServ2ChanReconfig(Hashtable vec){
        synchronized(InvokeServ2ChanReconfigureBufferLock){
            InvokeServ2ChanReconfigureBuffer = vec;
        }
    }
    
    public static void AddInvokeServ2StatChanReconfig(String chcdname, String cname, boolean stat){
        synchronized(InvokeServ2StatChanReconfigureBufferLock){
              
            try {
                
                if(InvokeServ2StatChanReconfigureBuffer.has(chcdname)){
                    
                    JSONObject jsChanName = InvokeServ2StatChanReconfigureBuffer.getJSONObject(chcdname);
                    
                    jsChanName.put(cname, Boolean.toString(stat));
                        
                    InvokeServ2StatChanReconfigureBuffer.put(chcdname, jsChanName);
                     
                } else {
                    
                    JSONObject jsChanName = new JSONObject();
                    
                    jsChanName.put(cname, stat);
                    
                    InvokeServ2StatChanReconfigureBuffer.put(chcdname, jsChanName);
                    
                }
                
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
              
                
        }
    }
    
    
    
    public static boolean IsInvokeServ2StatChanReconfigBufferEmpty(){
        synchronized(InvokeServ2StatChanReconfigureBufferLock){
            if(InvokeServ2StatChanReconfigureBuffer.length()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    
    
    public static boolean GetReconfigInvokeServ2StatChanBuffer(String chcdname, String cname){
       
        synchronized(InvokeServ2StatChanReconfigureBufferLock){
            
            boolean stat = false;
            
            try {
                JSONObject jsChanName = InvokeServ2StatChanReconfigureBuffer.getJSONObject(chcdname);
                
                stat = Boolean.parseBoolean(jsChanName.getString(cname));
                
                jsChanName.remove(cname);
                
                if(jsChanName.length()==0){
                    InvokeServ2StatChanReconfigureBuffer = new JSONObject();
                } else {
                    InvokeServ2StatChanReconfigureBuffer.put(chcdname, jsChanName);
                }
                       
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            
            return stat;
            
        }
    }
    
    
    public static void AddStatChanReconfig(String chcdname, String cname, boolean stat){
        synchronized(StatChanReconfigureBufferLock){
              
            try {
                
                if(StatChanReconfigureBuffer.has(chcdname)){
                    
                    JSONObject jsChanName = StatChanReconfigureBuffer.getJSONObject(chcdname);
                    
                    jsChanName.put(cname, Boolean.toString(stat));
                        
                    StatChanReconfigureBuffer.put(chcdname, jsChanName);
                     
                } else {
                    
                    JSONObject jsChanName = new JSONObject();
                    
                    jsChanName.put(cname, stat);
                    
                    StatChanReconfigureBuffer.put(chcdname, jsChanName);
                    
                }
                
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
              
                
        }
    }
    
    
    
    public static boolean IsStatChanReconfigBufferEmpty(){
        synchronized(StatChanReconfigureBufferLock){
            if(StatChanReconfigureBuffer.length()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    
    
    public static boolean GetReconfigStatChanBuffer(String chcdname, String cname){
       
        synchronized(StatChanReconfigureBufferLock){
            
            boolean stat = false;
            
            try {
                JSONObject jsChanName = StatChanReconfigureBuffer.getJSONObject(chcdname);
                
                stat = Boolean.parseBoolean(jsChanName.getString(cname));
                
                jsChanName.remove(cname);
                
                if(jsChanName.length()==0){
                    StatChanReconfigureBuffer = new JSONObject();
                } else {
                    StatChanReconfigureBuffer.put(chcdname, jsChanName);
                }
                       
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            
            return stat;
            
        }
    }
    
    public static void AddChanReconfig(String chcdname, String cname, String cdirection, String partnercdname, String partnercname){
        synchronized(ChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                
                ChanReconfigureBuffer.addElement(hash2);
                
        }
    }
    
    public static void AddChanReconfig(String chcdname, String cname, String cdirection, String partnercdname, String partnercname, String destSSName, String destSSAddr){
        synchronized(ChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                hash2.put("destSS", destSSName);
                hash2.put("destSSAddr", destSSAddr);
                
                ChanReconfigureBuffer.addElement(hash2);
                
        }
    }
    
    
    
    public static boolean IsChanReconfigBufferEmpty(){
        synchronized(ChanReconfigureBufferLock){
            if(ChanReconfigureBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearChanReconfigBuffer(){
        synchronized(ChanReconfigureBufferLock){
            ChanReconfigureBuffer = new Vector();
        }
    }
    
    public static Vector GetReconfigChanBuffer(){
        synchronized(ChanReconfigureBufferLock){
            return ChanReconfigureBuffer;
        }
    }
    
    
    
    public static void AddInvokeServChanReconfig(String chcdname, String cname, String cdirection, String partnercdname, String partnercname){
        synchronized(InvokeServChanReconfigureBufferLock){
           
                Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
                hash2.put("CDName",chcdname);
                hash2.put("ChanName",cname);
                hash2.put("ChanDir", cdirection);
                
                InvokeServChanReconfigureBuffer.addElement(hash2);
                
        }
    }
    
    
    
    public static boolean IsInvokeServChanReconfigBufferEmpty(){
        synchronized(InvokeServChanReconfigureBufferLock){
            if(InvokeServChanReconfigureBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearInvokeServChanReconfigBuffer(){
        synchronized(InvokeServChanReconfigureBufferLock){
            InvokeServChanReconfigureBuffer = new Vector();
        }
    }
    
    public static Vector GetReconfigInvokeServChanBuffer(){
        synchronized(InvokeServChanReconfigureBufferLock){
            return InvokeServChanReconfigureBuffer;
        }
    }
    
    public static void AddOldInvokeServPartnerChanReconfig(String partnercdirection, String partnercdname, String partnercname){
        synchronized(InvokeServPartnerChanReconfigureBufferLock){
        
            Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
               
                hash2.put("PartnerChanDir", partnercdirection);
                
                InvokeServPartnerChanReconfigureBuffer.addElement(hash2);
            
        }
    }
    
     
    
    public static boolean IsOldInvokeServPartnerChanReconfigBufferEmpty(){
        synchronized(InvokeServPartnerChanReconfigureBufferLock){
            if(InvokeServPartnerChanReconfigureBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearOldInvokeServPartnerChanReconfigBuffer(){
        synchronized(InvokeServPartnerChanReconfigureBufferLock){
            InvokeServPartnerChanReconfigureBuffer = new Vector();
        }
    }
    
    public static Vector GetOldInvokeServPartnerReconfigChanBuffer(){
        synchronized(InvokeServPartnerChanReconfigureBufferLock){
            return InvokeServPartnerChanReconfigureBuffer;
        }
    }
    
    public static void AddOldPartnerChanReconfig(String partnercdirection, String partnercdname, String partnercname){
        synchronized(PartnerChanReconfigureBufferLock){
        
            Hashtable hash2 = new Hashtable();
                hash2.put("PartnerCDName", partnercdname);
                hash2.put("PartnerChanName",partnercname);
               
                hash2.put("PartnerChanDir", partnercdirection);
                
                PartnerChanReconfigureBuffer.addElement(hash2);
            
        }
    }
    
     
    
    public static boolean IsOldPartnerChanReconfigBufferEmpty(){
        synchronized(PartnerChanReconfigureBufferLock){
            if(PartnerChanReconfigureBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void ClearOldPartnerChanReconfigBuffer(){
        synchronized(PartnerChanReconfigureBufferLock){
            PartnerChanReconfigureBuffer = new Vector();
        }
    }
    
    public static Vector GetOldPartnerReconfigChanBuffer(){
        synchronized(PartnerChanReconfigureBufferLock){
            return PartnerChanReconfigureBuffer;
        }
    }
    
    public static void AddMigratingCDInst(ClockDomain cdins){
        synchronized(MigratingCDInstancesLock){
            MigratingCDInstances.addElement(cdins);
        }
    }
    
    public static Vector GetMigratingCDInsts(){
        synchronized(MigratingCDInstancesLock){
            return MigratingCDInstances;
        }
    }
    
    public static void RemoveMigratingCDObjFromBufferCDInsts(String cdname){
        synchronized(MigratingCDInstancesLock){
            for(int t=0;t<MigratingCDInstances.size();t++){
                ClockDomain cdobj = (ClockDomain)MigratingCDInstances.get(t);
                
                if(cdobj.getName().equals(cdname)){
                    MigratingCDInstances.remove(t);
                }
                
            }
        }
    }
    
    public static void AddMigratingCDNameToBuffer(String cdname){
        synchronized(MigratingCDNameBufferLock){
            MigratingCDNameBuffer.addElement(cdname);
        }
    }
    
    public static Vector GetMigratingCDNameFromBuffer(){
        synchronized(MigratingCDNameBufferLock){
            return MigratingCDNameBuffer;
        }
    }
    
    public static boolean IsMigratingCDNameBufferEmpty(){
        synchronized(MigratingCDNameBufferLock){
            if(MigratingCDNameBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void SetCDLCMigrationFlagBusy(){
        synchronized(CDLCMigrationThreadBusyFlagLock){
            CDLCMigrationThreadBusyFlag = true;
        }
    }
    
    public static void SetCDLCMigrationFlagFree(){
        synchronized(CDLCMigrationThreadBusyFlagLock){
            CDLCMigrationThreadBusyFlag = false;
        }
    }
    
    public static boolean ISCDLCMigrationThreadBusy(){
        synchronized(CDLCMigrationThreadBusyFlagLock){
            return CDLCMigrationThreadBusyFlag;
        }
    }
    
    public static void SetMigModPartnerDone(boolean stat){
        synchronized(MigModifyPartnerDoneLock){
            MigModifyPartnerDone = stat;
        }
    }
    
    public static void SetMigGoAheadModPartner(boolean stat){
        synchronized(MigGoAheadModifyPartnerLock){
            MigGoAheadModifyPartner = stat;
        }
    }
    
    public static boolean GetMigGoAheadModPartner(){
        synchronized(MigGoAheadModifyPartnerLock){
            return MigGoAheadModifyPartner ;
        }
    }
    
    public static boolean GetMigModPartnerDone(){
        synchronized(MigModifyPartnerDoneLock){
            return MigModifyPartnerDone;
        }
    }
    
    public static void setScheduler(Scheduler sc){
        synchronized(schedNowLock){
            schedNow = sc;
        }
    }
    
    public static Scheduler getScheduler(){
        synchronized(schedNowLock){
            return schedNow;
        }
    }
    
    public static void SetAllMigCDObjsBuffer(String DestSS, String MigType, Vector vecAllCDIns, JSONObject migratingMap){
        synchronized(MigCDObjsLock){
            MigSSMigTypeCDObjs.put("CDObj", vecAllCDIns);
            MigSSMigTypeCDObjs.put("DestSS",DestSS);
            MigSSMigTypeCDObjs.put("MigType", MigType);
            MigSSMigTypeCDObjs.put("MigratingMap", migratingMap);
        }       
    }
    
    public static Hashtable GetAllMigCDObjsBuffer(){
        synchronized(MigCDObjsLock){
            return MigSSMigTypeCDObjs;
        }
    }
    
    public static void SetMigrationIMAllCDInsBuffer( Vector vecAllCDIns){
        
        synchronized(AllCDInsVecLock){
            AllCDInsVec = vecAllCDIns;
        }
        
    }
    
    public static Vector GetMigrationIMAllCDInsBuffer(){
        synchronized(AllCDInsVecLock){
            return AllCDInsVec;
        }
    }
    
    public static void SetRecoverIMSCBufferStatus(boolean stat){
        synchronized(UpdateIMMigrationLock){
            UpdateIMMigration = stat;
        }
    }
    
    public static boolean GetRecoverIMSCBufferStatus(){
        synchronized(UpdateIMMigrationLock){
            return UpdateIMMigration;
        }
    }
    
    public static void SetMigrationIMUpdated(){
        synchronized(IMVecFlagLock){
            IMVecFlag = true;
        }
    }
    
    public static boolean GetMigrationIMUpdated(){
        synchronized(IMVecFlagLock){
           return IMVecFlag;
        }
    }
    
     public static void ResetMigrationIMUpdated(){
        synchronized(IMVecFlagLock){
           IMVecFlag = false;
        }
     }
     
    public static void SetStrongMigrationDoneFlag(){
        synchronized(StrongMigrationDoneLock){
            StrongMigrationDoneFlag = true;
        }
    }
    
    public static void ResetStrongMigrationDoneFlag(){
        synchronized(StrongMigrationDoneLock){
            StrongMigrationDoneFlag = false;
        }
    }
    
    public static boolean GetStrongMigrationDoneFlag(){
        synchronized(StrongMigrationDoneLock){
            return StrongMigrationDoneFlag;
        }
    }
    
    public static void SetWeakMigrationDoneFlag(){
        synchronized(WeakMigrationDoneLock){
            WeakMigrationDoneFlag = true;
        }
    }
    
    public static void ResetWeakMigrationDoneFlag(){
        synchronized(WeakMigrationDoneLock){
            WeakMigrationDoneFlag = false;
        }
    }
    
    public static boolean GetWeakMigrationDoneFlag(){
        synchronized(WeakMigrationDoneLock){
            return WeakMigrationDoneFlag;
        }
    }
    
    public static void SetLinkCreationBusyFlag(boolean stat){
        synchronized(LinkCreationPortBusyFlagLock){
            LinkCreationBusyFlag = stat;
        }
    }
    
    public static boolean GetLinkCreationBusyFlag()  {
        synchronized (LinkCreationPortBusyFlagLock){
            return LinkCreationBusyFlag;
        }
    }
    
    public static void updateMigTempSignalChannelMap(JSONObject js){
        synchronized(MigTempSSSignalChannelMapLock){
            MigTempSSSignalChannelMap = js;
        }
    }
    
    public static JSONObject getMigTempSignalChannelMap(){
        synchronized(MigTempSSSignalChannelMapLock){
            return MigTempSSSignalChannelMap;
        }
    }
    
    public static void TransferMigrationRequestToBuffer(JSONObject jsMsg){
        synchronized(MigrationReqMsgBufferLock){
            MigrationReqMsgBuffer.addElement(jsMsg);
        }
    }
    
    
    
    
    public static void setMigrationBusyFlag(){
        synchronized(MigrationBusyFlagLock){
            MigrationBusyFlag = true;
        }
    }
    
    public static void releaseMigrationBusyFlag(){
        synchronized(MigrationBusyFlagLock){
            MigrationBusyFlag=false;
        }
    }
    
    public static boolean getMigrationBusyFlag(){
        synchronized(MigrationBusyFlagLock){
            return MigrationBusyFlag;
        }
    }
        
    public static Vector getMigrationRequestMsg(){
        
        Vector migReqMsg = new Vector();
        
        synchronized(MigrationReqMsgBufferLock){
            migReqMsg = MigrationReqMsgBuffer;
            MigrationReqMsgBuffer = new Vector();
        }
        
        return migReqMsg;
    }
    
    public static void setMigRespMsgBuffer(JSONObject js){
        synchronized(MigrationRespMsgBufferLock){
            MigrationRespMsgBuffer = js;
        }
    }

    
    public static JSONObject getRespMsgBuffer(JSONObject js){
        
        JSONObject jsResp = new JSONObject();
        
        synchronized(MigrationRespMsgBufferLock){
            jsResp = MigrationRespMsgBuffer;
        }
        
        return jsResp;
    }
    
    public static void updateMigrationStatus(String stat){
        
        synchronized(MigrationStatLock){
            MigrationStat.addElement(stat);
        }
        
    }
    
    // End CD migration
    
    //Update CD Signal Remap
    
    public static void updateUpdateSignalRemapCD(JSONObject js){
        synchronized(UpdateTempSSSSignalChannelMapLock){
            UpdateTempSSSignalChannelMap = js;
        }
    }
    
    public static JSONObject getUpdateSignalRemapCD(){
        synchronized(UpdateTempSSSSignalChannelMapLock){
            return UpdateTempSSSignalChannelMap;
        }
    }
    
   
    public static boolean IsRequestUpdateSignalRemapCDEmpty(){
        synchronized(UpdateTempSSSSignalChannelMapLock){
            return UpdateTempSSSignalChannelMap.isEmpty();
        }
    }
    
    public static void clearRequestUpdateSignalRemapCD(){
        synchronized(UpdateTempSSSSignalChannelMapLock){
            UpdateTempSSSignalChannelMap = new JSONObject();
        }
    }
    
    public static void removeRequestUpdateSigChanMap(String CDName){
        synchronized(UpdateTempSSSSignalChannelMapLock){
            UpdateTempSSSignalChannelMap.remove(CDName);
        }
    }
    
    // End Update CD Signal Remap
    
    // Update Service Description
    
    public static void putUpdateServiceDescription(JSONObject js){
        synchronized(UpdateTempServiceDescLock){
            
            Enumeration keysEn = js.keys();
            
            while(keysEn.hasMoreElements()){
                String servName = keysEn.nextElement().toString();
                try {
                    UpdateTempServiceDesc.put(servName, js.getJSONObject(servName));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            
        }
    }
    
    public static boolean IsRequestUpdateServDescIsEmpty(){
        
        synchronized(UpdateTempServiceDescLock){
            return UpdateTempServiceDesc.isEmpty();
        }
        
    }
    
    public static JSONObject GetTempUpdateServDesc(){
        synchronized(UpdateTempServiceDescLock){
            return UpdateTempServiceDesc;
        }
    }
    
    public static void ClearTempUpdateServDesc(){
        synchronized(UpdateTempServiceDescLock){
            UpdateTempServiceDesc = new JSONObject();
        }
    }
    
    public static JSONObject GetTempUpdateServDescOfServName(String servName){
        synchronized(UpdateTempServiceDescLock){
            JSONObject js = new JSONObject();
            try {
                js.put(servName, UpdateTempServiceDesc.getJSONObject(servName));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return js;
        }
    }
    
    
    
    // End Update Service Description
    
    //Create CD
    
    public static void updateTempSignalChannelMap(JSONObject js){
        synchronized(TempSSSSignalChannelMapLock){
            TempSSSignalChannelMap = js;
        }
    }
    
    public static JSONObject getAllTempSignalChannelMap(){
        synchronized(TempSSSSignalChannelMapLock){
            return TempSSSignalChannelMap;
        }
    }
    
   
    public static boolean IsTempSigChanMapEmpty(){
        synchronized(TempSSSSignalChannelMapLock){
            return TempSSSignalChannelMap.isEmpty();
        }
    }
    
    public static boolean HasTempSigChanMap(String Cdname){
        synchronized(TempSSSSignalChannelMapLock){
            if(TempSSSignalChannelMap.has(Cdname)){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static JSONObject GetTempSigChanMap(String Cdname){
        synchronized(TempSSSSignalChannelMapLock){
            JSONObject js = new JSONObject();
            try {
                js.put(Cdname,TempSSSignalChannelMap.getJSONObject(Cdname));
                
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            
            return js;
            
        }
    }
    
    public static void clearTempSigChanMapCD(){
        synchronized(TempSSSSignalChannelMapLock){
            TempSSSignalChannelMap = new JSONObject();
        }
    }
    
    public static void AddTempSigChanMapCD(JSONObject jsCDMap){
        synchronized(TempSSSSignalChannelMapLock){
            Enumeration keys = jsCDMap.keys();
            
            while(keys.hasMoreElements()){
                String key = keys.nextElement().toString();
                
                try {
                    JSONObject jsCDDet = jsCDMap.getJSONObject(key);
                    TempSSSignalChannelMap.put(key, jsCDDet);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            }
            
        }
    }
    
    public static boolean IsRequestCreateCDEmpty(){
        synchronized(RequestCreateCDListLock){
            if(RequestCreateCDList.size()==0){
                return true;
            } else {
                return false;
            }
            
        }
    }
    
    public static void clearRequestCreateCD(){
        synchronized(RequestCreateCDListLock){
            RequestCreateCDList = new Vector();
        }
    }
    
    public static Vector getAllRequestCreateCD(){
        synchronized(RequestCreateCDListLock){
            return RequestCreateCDList;
        }
    }
    
    public static String getRequestCreateCD(int i){
        synchronized(RequestCreateCDListLock){
            return RequestCreateCDList.get(i).toString();
        }
    }
    
    public static int getRequestCreateSize(){
        synchronized(RequestCreateCDListLock){
            return RequestCreateCDList.size();
        }
    }
    
    public static void TransferRequestCreateCDToBuffer(String CDName){
        synchronized(RequestCreateCDListLock){
            RequestCreateCDList.addElement(CDName);
        }
    }
    
    // End Create CD
    
    //Request Activate CD
    
    public static void TransferRequestActivateCDToBuffer(Vector vec){
      synchronized(RequestActivateCDListLock){
          for (int i=0;i<vec.size();i++){
              RequestActivateCDList.addElement(vec.get(i).toString());
          }
          
      }
    }
    
    public static String GetRequestActivateCD(int index){
        synchronized(RequestActivateCDListLock){
            return RequestActivateCDList.get(index).toString();
        }
    }
    
    public static int GetRequestActivateCDSize(){
        synchronized(RequestActivateCDListLock){
            return RequestActivateCDList.size();
        }
    }
    
    public static void ClearRequestActivateCD(){
        synchronized(RequestActivateCDListLock){
            RequestActivateCDList = new Vector();
        }
    }
    
    public static boolean IsRequestActivateCDEmpty(){
        synchronized(RequestActivateCDListLock){
            if(RequestActivateCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    //End Request Activate
    
     public static void TransferRequestWakeUpCDToBuffer(String cdname){
      synchronized(RequestWakeUpCDListLock){
          //for (int i=0;i<vec.size();i++){
              RequestWakeUpCDList.addElement(cdname);
          //}
          
      }
    }
    
    public static String GetRequestWakeUpCD(int index){
        synchronized(RequestWakeUpCDListLock){
            return RequestWakeUpCDList.get(index).toString();
        }
    }
    
    public static Vector GetAllRequestWakeUpCD(){
        synchronized(RequestWakeUpCDListLock){
            return RequestWakeUpCDList;
        }
    }
    
    public static int GetRequestWakeUpCDSize(){
        synchronized(RequestWakeUpCDListLock){
            return RequestWakeUpCDList.size();
        }
    }
    
    public static void ClearRequestWakeUpCD(){
        synchronized(RequestWakeUpCDListLock){
            RequestWakeUpCDList = new Vector();
        }
    }
    
    public static boolean IsRequestWakeUpCDEmpty(){
        synchronized(RequestWakeUpCDListLock){
            if(RequestWakeUpCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    // Migrated CD buffered to CDLC 
    
    public static void AddStrongMigratedCD(String CDName){
        synchronized(StrongMigratedCDNameLock){
            
        }
    }
    
    public static void AddWeakMigratedCD(){
        synchronized(WeakMigratedCDNameLock){
            
        }
    }
    
    public static void GetStrongMigratedCD(){
        synchronized(StrongMigratedCDNameLock){
            
        }
    }
    
    public static void GetWeakMigratedCD(){
        synchronized (WeakMigratedCDNameLock){
            
        }
    }
    
    //end
    
    
    // Request Clone and Migrate CD
    
        //clone and migrate serv desc
    public static void TransferRequestCloneAndMigrateServDescToBuffer(JSONObject js){
       
      synchronized(ReqCloneAndMigrateTempServiceDescriptionLock){
          
          Enumeration keys = js.keys();
          
          while (keys.hasMoreElements()){
              
              String keyName = keys.nextElement().toString();
              
              try {
                  ReqCloneAndMigrateTempServiceDescription.put(keyName, js.getJSONObject(keyName));
              } catch (JSONException ex) {
                  ex.printStackTrace();
              }
              
          }
          
          
      }
    }
    
    public static JSONObject GetRequestCloneAndMigrateServDescFromBuffer(){
      synchronized(ReqCloneAndMigrateTempServiceDescriptionLock){
          return ReqCloneAndMigrateTempServiceDescription;
      }
    }
    
    public static boolean IsRequestCloneAndMigrateServDescBufferEmpty(){
      synchronized(ReqCloneAndMigrateTempServiceDescriptionLock){
          return ReqCloneAndMigrateTempServiceDescription.isEmpty();
      }
    }
    
    public static void ClearRequestCloneAndMigrateServDescBuffer(JSONObject js){
      synchronized(ReqCloneAndMigrateTempServiceDescriptionLock){
          ReqCloneAndMigrateTempServiceDescription =  new JSONObject();
      }
    }
    
    
        //Clone and migrate CD Mapping
    public static void TransferRequestCloneAndMigrateCDMapToBuffer(JSONObject js){
      synchronized(RequestCloneAndMigrateCDMapListLock){
          
          Enumeration keys = js.keys();
          
          while (keys.hasMoreElements()){
              
              String keyName = keys.nextElement().toString();
              
              try {
                  ReqCloneAndMigrateTempSSSignalChannelMap.put(keyName, js.getJSONObject(keyName));
              } catch (JSONException ex) {
                  ex.printStackTrace();
              }
              
          }
          
      }
    }
    
    public static JSONObject GetRequestCloneAndMigrateCDMapFromBuffer(){
      synchronized(RequestCloneAndMigrateCDMapListLock){
          return ReqCloneAndMigrateTempSSSignalChannelMap;
      }
    }
    
    public static boolean IsRequestCloneAndMigrateCDMapEmpty(){
      synchronized(RequestCloneAndMigrateCDMapListLock){
          return ReqCloneAndMigrateTempSSSignalChannelMap.isEmpty();
      }
    }
    
    public static void ClearRequestCloneAndMigrateCDMapBuffer(JSONObject js){
      synchronized(RequestCloneAndMigrateCDMapListLock){
          ReqCloneAndMigrateTempSSSignalChannelMap =  new JSONObject();
      }
    }
    
        // Clone And Migrate CD name
     public static void TransferRequestCloneAndMigrateCDToBuffer(Hashtable hash){
      synchronized(RequestCloneAndMigrateCDListLock){
           //for (int i=0;i<vec.size();i++){
           //   RequestCloneAndMigrateCDList.addElement(vec.get(i).toString());
           //}
          RequestCloneAndMigrateCDList = hash;
          
      }
    }
    
    public static Hashtable GetAllRequestCloneAndMigrate(){
        synchronized(RequestCloneAndMigrateCDListLock){
            return RequestCloneAndMigrateCDList;
        }
    }
    
    public static int GetRequestCloneAndMigrateCDSize(){
        synchronized(RequestCloneAndMigrateCDListLock){
            return RequestCloneAndMigrateCDList.size();
        }
    }
    
    public static void ClearRequestAndMigrateCloneCD(){
        synchronized(RequestCloneAndMigrateCDListLock){
            RequestCloneAndMigrateCDList = new Hashtable();
        }
    }
    
    public static boolean IsRequestCloneAndMigrateCDEmpty(){
        synchronized(RequestCloneAndMigrateCDListLock){
            if(RequestCloneAndMigrateCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    
    // end
    
        // Request Clone only CD
    
    //serv desc
    public static void TransferRequestCloneServDescToBuffer(JSONObject js){
       
      synchronized(ReqCloneTempServiceDescriptionLock){
          
         Enumeration keys = js.keys();
          
          while (keys.hasMoreElements()){
              
              String keyName = keys.nextElement().toString();
              
              try {
                  ReqCloneTempServiceDescription.put(keyName, js.getJSONObject(keyName));
              } catch (JSONException ex) {
                  ex.printStackTrace();
              }
              
          }
          
          //ReqCloneTempServiceDescription =  js;
      }
    }
    
    public static JSONObject GetRequestCloneServDescFromBuffer(){
      synchronized(ReqCloneTempServiceDescriptionLock){
          return ReqCloneTempServiceDescription;
      }
    }
    
    public static boolean IsRequestCloneServDescBufferEmpty(){
      synchronized(ReqCloneTempServiceDescriptionLock){
          return ReqCloneTempServiceDescription.isEmpty();
      }
    }
    
    public static void ClearRequestCloneServDescBuffer(){
      synchronized(ReqCloneTempServiceDescriptionLock){
          ReqCloneTempServiceDescription =  new JSONObject();
      }
    }
    
    
        //Clone CD Mapping
    public static void TransferRequestCloneCDMapToBuffer(JSONObject js){
      synchronized(RequestCloneCDMapListLock){
          
          Enumeration keys = js.keys();
          
          while (keys.hasMoreElements()){
              
              String keyName = keys.nextElement().toString();
              
              try {
                  ReqCloneTempSSSignalChannelMap.put(keyName, js.getJSONObject(keyName));
              } catch (JSONException ex) {
                  ex.printStackTrace();
              }
              
          }
          
          //ReqCloneTempSSSignalChannelMap =  js;
      }
    }
    
    public static JSONObject GetRequestCloneCDMapFromBuffer(){
      synchronized(RequestCloneCDMapListLock){
          return ReqCloneTempSSSignalChannelMap;
      }
    }
    
    public static JSONObject GetReqCloneIndivCDMapBuffer(String CDName){
        synchronized(RequestCloneCDMapListLock){
            JSONObject js = new JSONObject();
            try {
               js = ReqCloneTempSSSignalChannelMap.getJSONObject(CDName);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            
            return js;
        }
    }
    
    
    public static boolean IsRequestCloneCDMapEmpty(){
      synchronized(RequestCloneCDMapListLock){
          return ReqCloneTempSSSignalChannelMap.isEmpty();
      }
    }
    
    public static void ClearRequestCloneCDMapBuffer(JSONObject js){
      synchronized(RequestCloneCDMapListLock){
          ReqCloneTempSSSignalChannelMap =  new JSONObject();
      }
    }
    
    public static boolean ReqCloneCDMapContains(String CDName){
        synchronized(RequestCloneCDMapListLock){
            if(ReqCloneTempSSSignalChannelMap.has(CDName)){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void RemoveReqCloneCDMapEntryFromBuffer(String CDName){
        synchronized(RequestCloneCDMapListLock){
            ReqCloneTempSSSignalChannelMap.remove(CDName);
        }
    }
    
        //requested Clone CD name
     public static void TransferRequestCloneCDToBuffer(Hashtable hash){
      synchronized(RequestCloneCDListLock){
          
           Enumeration keys = hash.keys();
          
          while (keys.hasMoreElements()){
              
              String keyName = keys.nextElement().toString();
              
              
                  RequestCloneCDList.put(keyName, hash.get(keyName).toString());
              
              
          }
          
          //RequestCloneCDList = hash;
      }
    }
    
   // public static String GetRequestCloneCD(String cdName){
   //     synchronized(RequestCloneCDListLock){
   //         return 
   //     }
   // }
     
      public static Hashtable GetRequestCloneCD(){
        synchronized(RequestCloneCDListLock){
            return RequestCloneCDList;
        }
    }
    
    public static int GetRequestCloneCDSize(){
        synchronized(RequestCloneCDListLock){
            return RequestCloneCDList.size();
        }
    }
    
    public static void ClearRequestCloneCD(){
        synchronized(RequestCloneCDListLock){
            RequestCloneCDList = new Hashtable();
        }
    }
    
    public static void RemoveRequestCloneCD(String keyCDName){
        synchronized(RequestCloneCDListLock){
            RequestCloneCDList.remove(keyCDName);
        }
    }
    
    public static boolean IsRequestCloneCDEmpty(){
        synchronized(RequestCloneCDListLock){
            if(RequestCloneCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    // End Clone CD
    
     public static void TransferRequestUpdateCDToBuffer(Vector vec){
      synchronized(RequestUpdateCDListLock){
          
          for (int i=0;i<vec.size();i++){
              RequestUpdateCDList.addElement(vec.get(i).toString());
          }
          
          //RequestUpdateCDList = vec;
      }
    }
    
    public static String GetRequestUpdateCD(int index){
        synchronized(RequestUpdateCDListLock){
            return RequestUpdateCDList.get(index).toString();
        }
    }
    
    public static int GetRequestUpdateCDSize(){
        synchronized(RequestUpdateCDListLock){
            return RequestUpdateCDList.size();
        }
    }
    
    public static void ClearRequestUpdateCD(){
        synchronized(RequestUpdateCDListLock){
            RequestUpdateCDList = new Vector();
        }
    }
    
   public static void RemoveRequestUpdateCD(int i){
        synchronized(RequestUpdateCDListLock){
            RequestUpdateCDList.remove(i);
        }
    } 
    
    public static boolean IsRequestUpdateCDEmpty(){
        synchronized(RequestUpdateCDListLock){
            if(RequestUpdateCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    //Trigger Hibernate
    
    public static void TransferRequestHibernateCDToBuffer(String cdname){
      synchronized(RequestHibernateCDListLock){
          
          //for (int i=0;i<vec.size();i++){
              RequestHibernateCDList.addElement(cdname);
          //}
          
          //RequestHibernateCDList = vec;
      }
   }
    
    public static int GetRequestHibernateCDSize(){
        synchronized(RequestHibernateCDListLock){
            return RequestHibernateCDList.size();
        }
    }
    
    public static String GetRequestHibernateCD(int index){
        synchronized(RequestHibernateCDListLock){
            return RequestHibernateCDList.get(index).toString();
        }
    }
    
    public static Vector GetAllRequestHibernateCD(){
        synchronized(RequestHibernateCDListLock){
            return RequestHibernateCDList;
        }
    }
    
    public static void ClearRequestHibernateCD(){
        synchronized(RequestHibernateCDListLock){
            RequestHibernateCDList = new Vector();
        }
    }
    
    public static void RemoveRequestHibernateCD(int i){
        synchronized(RequestHibernateCDListLock){
            RequestHibernateCDList.remove(i);
        }
    }
    
    public static boolean IsRequestHibernateCDEmpty(){
        synchronized(RequestHibernateCDListLock){
            if(RequestHibernateCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    
    
    public static void AddTimerCD(String CDName, int time, String unit){
        
        synchronized(CDNameTimeLoginLock){
            //try {
                Hashtable hash = new Hashtable();
                long time2 = (long) time;
                hash.put("waitTimeAmount", Long.toString(time2));
                hash.put("timeUnit", unit);
                //long loginTime = System.currentTimeMillis();
                //hash.put("loginTime",Long.toString(loginTime));
                CDNameTimeLogin.put(CDName, hash);
            //} catch (JSONException ex) {
               // ex.printStackTrace();
           // }
            
        }
    }
    
    public static long GetHibernateLoginTimeOfCDName(String CDName){
        synchronized(CDNameTimeLoginLock){
            
            Hashtable js = new Hashtable();
           
                js =  (Hashtable)CDNameTimeLogin.get(CDName);
            
            
                long loginTime = Long.parseLong(js.get("loginTime").toString());
                return loginTime;
                
        }
    }
    
    public static Hashtable GetAllHibernateCDWithTimer(){
        synchronized(CDNameTimeLoginLock){
            return CDNameTimeLogin;
        }
    }
    
    public static long GetHibernateWaitTimeAmountOfCDName(String CDName){
        synchronized(CDNameTimeLoginLock){
           // try {
            Hashtable hash =  (Hashtable)CDNameTimeLogin.get(CDName);
            
                long waitTime;
            
                waitTime = Long.parseLong(hash.get("waitTimeAmount").toString());
                return waitTime;
            //} catch (JSONException ex) {
                //ex.printStackTrace();
                //return 0;
            //}
            
        }
    }
    
    public static String GetHibernateWaitTimeUnitOfCDName(String CDName){
        synchronized(CDNameTimeLoginLock){
            
               Hashtable hash = (Hashtable)CDNameTimeLogin.get(CDName);
               String unit = hash.get("timeUnit").toString();
               return unit;
            
        }
    }
    
    public static void RemoveHibernateWithTimerEntryOfCDName(String CDName){
        synchronized(CDNameTimeLoginLock){
            CDNameTimeLogin.remove(CDName);
        }
    }
    
    public static boolean HibernateWithTimerHasCDName(String CDName){
        synchronized(CDNameTimeLoginLock){
            if(CDNameTimeLogin.containsKey(CDName)){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static boolean IsHibernateWithTimerEmpty(){
        synchronized(CDNameTimeLoginLock){
            if(CDNameTimeLogin.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void setCDLifeCycleChanged(boolean stat){
        synchronized (CDLifeCycleChangedLock){
            CDLifeCycleChanged = stat;
        }
        
    }
    
    public static boolean getCDLifeCycleChanged(){
        synchronized(CDLifeCycleChangedLock){
            return CDLifeCycleChanged;
        }
    }
    
    public static void TransferRequestKillCDToBuffer(String CDName){
        synchronized(RequestKillCDListLock){
            
            //for (int i=0;i<vec.size();i++){
              RequestKillCDList.addElement(CDName);
           //}
            
            //RequestKillCDList = vec;
        }
    }
    
    public static String GetRequestKillCD(int index){
        synchronized(RequestKillCDListLock){
            return RequestKillCDList.get(index).toString();
        }
    }
    
    public static void ClearRequestKillCD(){
        synchronized(RequestKillCDListLock){
            RequestKillCDList = new Vector();
        }
    }
    
    public static boolean IsRequestKillCDEmpty(){
        synchronized(RequestKillCDListLock){
            if(RequestKillCDList.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static int GetRequestKillCDSize(){
        synchronized(RequestKillCDListLock){
            return RequestKillCDList.size();
        }
    }
    
    // Migration buffer
    
    public static boolean IsRequestMigrateEmpty(){
        synchronized(RequestMigrateBufferLock){
            if(RequestMigrateBuffer.size()==0){
                return true;
            } else {
                return false;
            }
        }
    }
    
    public static void AddRequestMigrate(String CDName, String DestinationSS, JSONObject jsCDMap, JSONObject jsServDesc, String MigType){
        synchronized(RequestMigrateBufferLock){
            
            try{
             
            //hash.put("CDName",CDName);
            
            if(RequestMigrateBuffer.containsKey(DestinationSS)){
                
                Hashtable hashMigType = (Hashtable) RequestMigrateBuffer.get(DestinationSS);
                
                if(MigType.equalsIgnoreCase("strong")){
                    
                    if(hashMigType.containsKey("strong")){
                        Hashtable AllCD = (Hashtable)hashMigType.get("strong");
                    
                        Vector CDNameList = new Vector();
                    
                        CDNameList = (Vector)AllCD.get("CDNameList");
                    
                    //add new CDMapping
                    
                        JSONObject jsAllCDMap = (JSONObject)AllCD.get("CDMap");
                    
                    Enumeration keysjsCDMap = jsCDMap.keys();
                    
                    while(keysjsCDMap.hasMoreElements()){
                        String CDMapEntry = keysjsCDMap.nextElement().toString();
                        
                        JSONObject CDMapDet = jsCDMap.getJSONObject(CDMapEntry);
                        
                        jsAllCDMap.put(CDMapEntry, CDMapDet);
                        
                    }
                    
                    AllCD.put("CDMap", jsAllCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    JSONObject jsAllCDServDesc = (JSONObject)AllCD.get("CDServDesc");
                    
                    Enumeration keysjsCDServDesc = jsServDesc.keys();
                    
                    while(keysjsCDServDesc.hasMoreElements()){
                        String CDServDescEntry = keysjsCDServDesc.nextElement().toString();
                        
                        JSONObject CDServDescDet = jsServDesc.getJSONObject(CDServDescEntry);
                        
                        jsAllCDServDesc.put(CDServDescEntry, CDServDescDet);
                        
                    }
                    
                    AllCD.put("CDServDesc", jsAllCDServDesc);
                    
                    //end serv desc
                    
                    CDNameList.addElement(CDName);
                    
                    AllCD.put("CDNameList", CDNameList);
                    
                    hashMigType.put("strong", AllCD);
                    
                    RequestMigrateBuffer.put(DestinationSS, hashMigType);
                    
                } else {
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    CDNameList.put("CDServDesc", jsServDesc);
                    
                    //end serv desc
                    
                    Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    migtypeCD.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS, migtypeCD);
                }
                    
                } else if (MigType.equalsIgnoreCase("weak")){
                    
                    if(hashMigType.containsKey("weak")){
                    Hashtable AllCD = (Hashtable)hashMigType.get("weak");
                    
                    Vector CDNameList = new Vector();
                    
                    CDNameList = (Vector)AllCD.get("CDNameList");
                    
                    CDNameList.addElement(CDName);
                    
                    //add new CDMapping
                    
                    JSONObject jsAllCDMap = (JSONObject)AllCD.get("CDMap");
                    
                    Enumeration keysjsCDMap = jsCDMap.keys();
                    
                    while(keysjsCDMap.hasMoreElements()){
                        String CDMapEntry = keysjsCDMap.nextElement().toString();
                        
                        JSONObject CDMapDet = jsCDMap.getJSONObject(CDMapEntry);
                        
                        jsAllCDMap.put(CDMapEntry, CDMapDet);
                        
                    }
                    
                    AllCD.put("CDMap", jsAllCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    JSONObject jsAllCDServDesc = (JSONObject)AllCD.get("CDServDesc");
                    
                    Enumeration keysjsCDServDesc = jsServDesc.keys();
                    
                    while(keysjsCDServDesc.hasMoreElements()){
                        String CDServDescEntry = keysjsCDServDesc.nextElement().toString();
                        
                        JSONObject CDServDescDet = jsCDMap.getJSONObject(CDServDescEntry);
                        
                        jsAllCDServDesc.put(CDServDescEntry, CDServDescDet);
                        
                    }
                    
                    AllCD.put("CDServDesc", jsAllCDServDesc);
                    
                    //end serv desc
                    
                    AllCD.put("CDNameList", CDNameList);
                    
                    hashMigType.put("weak", AllCD);
                    
                    RequestMigrateBuffer.put(DestinationSS, hashMigType);
                } else {
                    
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    CDNameList.put("CDServDesc", jsServDesc);
                    
                    //end serv desc
                    
                    Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    migtypeCD.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS, migtypeCD);
                
                }
                    
                } else {
                    System.out.println("Migration type is not recognized");
                }
                
            } else {
                
                if(MigType.equalsIgnoreCase("strong") || MigType.equalsIgnoreCase("weak") ){
                    
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    CDNameList.put("CDServDesc", jsServDesc);
                    
                    //end serv desc
                    
                    Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    migtypeCD.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS, migtypeCD);
                    
                } else {
                    System.out.println("Migration type is not recognized");
                }
                
                
            }
                
            } catch (Exception ex){
                ex.printStackTrace();
            }
            
            
            
        }
    }
    
    public static void AddRequestMigrate(String CDName, String DestinationSS, JSONObject jsCDMap, String MigType){
        synchronized(RequestMigrateBufferLock){
            
            try{
             
            //hash.put("CDName",CDName);
            
            if(RequestMigrateBuffer.containsKey(DestinationSS)){
                
                Hashtable hashMigType = (Hashtable) RequestMigrateBuffer.get(DestinationSS);
                
                if(MigType.equalsIgnoreCase("strong")){
                    
                    if(hashMigType.containsKey("strong")){
                        Hashtable AllCD = (Hashtable)hashMigType.get("strong");
                    
                        Vector CDNameList = new Vector();
                    
                        CDNameList = (Vector)AllCD.get("CDNameList");
                    
                    //add new CDMapping
                    
                        JSONObject jsAllCDMap = (JSONObject)AllCD.get("CDMap");
                    
                    Enumeration keysjsCDMap = jsCDMap.keys();
                    
                    while(keysjsCDMap.hasMoreElements()){
                        String CDMapEntry = keysjsCDMap.nextElement().toString();
                        
                        JSONObject CDMapDet = jsCDMap.getJSONObject(CDMapEntry);
                        
                        jsAllCDMap.put(CDMapEntry, CDMapDet);
                        
                    }
                    
                    AllCD.put("CDMap", jsAllCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    
                    
                    
                    //end serv desc
                    
                    CDNameList.addElement(CDName);
                    
                    AllCD.put("CDNameList", CDNameList);
                    
                    hashMigType.put("strong", AllCD);
                    
                    RequestMigrateBuffer.put(DestinationSS, hashMigType);
                    
                } else {
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    //CDNameList.put("CDServDesc", jsServDesc);
                    
                    //end serv desc
                    
                   // Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    hashMigType.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS,hashMigType);
                }
                    
                } else if (MigType.equalsIgnoreCase("weak")){
                    
                    if(hashMigType.containsKey("weak")){
                    Hashtable AllCD = (Hashtable)hashMigType.get("weak");
                    
                    Vector CDNameList = new Vector();
                    
                    CDNameList = (Vector)AllCD.get("CDNameList");
                    
                    CDNameList.addElement(CDName);
                    
                    //add new CDMapping
                    
                    JSONObject jsAllCDMap = (JSONObject)AllCD.get("CDMap");
                    
                    Enumeration keysjsCDMap = jsCDMap.keys();
                    
                    while(keysjsCDMap.hasMoreElements()){
                        String CDMapEntry = keysjsCDMap.nextElement().toString();
                        
                        JSONObject CDMapDet = jsCDMap.getJSONObject(CDMapEntry);
                        
                        jsAllCDMap.put(CDMapEntry, CDMapDet);
                        
                    }
                    
                    AllCD.put("CDMap", jsAllCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    
                    //end serv desc
                    
                    AllCD.put("CDNameList", CDNameList);
                    
                    hashMigType.put("weak", AllCD);
                    
                    RequestMigrateBuffer.put(DestinationSS, hashMigType);
                } else {
                    
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    
                    //end serv desc
                    
                    //Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    hashMigType.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS, hashMigType);
                
                }
                    
                } else {
                    System.out.println("Migration type is not recognized");
                }
                
            } else {
                
                if(MigType.equalsIgnoreCase("strong") || MigType.equalsIgnoreCase("weak") ){
                    
                    Hashtable CDNameList = new Hashtable();
                
                    Vector vecAllCD = new Vector();
                
                    vecAllCD.addElement(CDName);
                
                    //add new CDMapping
                    
                    CDNameList.put("CDMap", jsCDMap);
                    
                    //end add CD Mapping
                    //add new Serv description
                    
                    
                    
                    //end serv desc
                    
                    Hashtable migtypeCD = new Hashtable();
                
                    CDNameList.put("CDNameList",vecAllCD);
                
                    migtypeCD.put(MigType, CDNameList);
                
                    RequestMigrateBuffer.put(DestinationSS, migtypeCD);
                    
                } else {
                    System.out.println("Migration type is not recognized");
                }
                
            }
                
            } catch (Exception ex){
                ex.printStackTrace();
            }
            
            
            
        }
    }
    
    public static Vector GetAllCDNameOfMigTypeAndDestSS(String destSS, String migType){
        synchronized(RequestMigrateBufferLock){
            Hashtable hashMig = (Hashtable)RequestMigrateBuffer.get(destSS);
            Hashtable AllCD = (Hashtable)hashMig.get(migType);
            Vector allCDVec = (Vector) AllCD.get("CDNameList");
            
            //RequestMigrateBuffer.remove(destSS); //commented out , deletion happens once transfer is done! 27 Aug 2015
            
            return allCDVec;
        }
    }
    
    
     //use this when req migrate of type and ss has been deleted
    public static void RecoverReqMigrateAllCD(String destSS, String migType, Vector vecAllCDNames){
        
        synchronized(RequestMigrateBufferLock){
            
            Hashtable recoveredCDs = new Hashtable();
            
            recoveredCDs.put(migType, vecAllCDNames);
            
            RequestMigrateBuffer.put(destSS, recoveredCDs);
            
        }
    }
    
   /*
    public static void RecoverReqMigrateOneCD(String destSS, String migType, String CDName){
        synchronized(RequestMigrateBufferLock){
            
            
            
        }
    }
    */
    
    
    public static void RemoveReqMigrate(String destSS,String migType){
        synchronized(RequestMigrateBufferLock){
            
            Hashtable hashMig = (Hashtable)RequestMigrateBuffer.get(destSS);
            
            if(hashMig.size()==1){
                RequestMigrateBuffer.remove(destSS);
            } else {
                hashMig.remove(migType);
                RequestMigrateBuffer.put(destSS, hashMig);
            }
            
        }
    }
    
    public static Hashtable GetRequestMigrate(){
        synchronized(RequestMigrateBufferLock){
            return RequestMigrateBuffer;
        }
    }
    
    public static void AddTempMigrateSignalChannelMap(JSONObject jsMap){
        synchronized(TempMigrateSSSignalChannelMapLock){
            Enumeration keysJsMap = jsMap.keys();
            
            while(keysJsMap.hasMoreElements()){
                
                try {
                    String ind = keysJsMap.nextElement().toString();
                    
                    JSONObject jsMapDet = jsMap.getJSONObject(ind);
                    
                    TempMigrateSSSignalChannelMap.put(ind, jsMapDet);
                } catch (JSONException ex) {
                   ex.printStackTrace();
                }
                
            }
        }
    }
    
    
    
    
    
    // Update Migration Service Description
    
    public static void AddMigrateServiceDescription(JSONObject js){
        synchronized(TempMigrateServDescLock){
            
            Enumeration keysEn = js.keys();
            
            while(keysEn.hasMoreElements()){
                String servName = keysEn.nextElement().toString();
                try {
                    TempMigrateServDesc.put(servName, js.getJSONObject(servName));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            
        }
    }
    
    public static boolean IsMigrateServDescIsEmpty(){
        
        synchronized(TempMigrateServDescLock){
            return TempMigrateServDesc.isEmpty();
        }
        
    }
    
    public static JSONObject GetTempMigrateServDesc(){
        synchronized(TempMigrateServDescLock){
            return TempMigrateServDesc;
        }
    }
    
    public static void ClearTempMigrateServDesc(){
        synchronized(TempMigrateServDescLock){
            TempMigrateServDesc = new JSONObject();
        }
    }
    
    public static JSONObject GetTempMigrateServDescOfServName(String servName){
        synchronized(TempMigrateServDescLock){
            JSONObject js = new JSONObject();
            try {
                js.put(servName, TempMigrateServDesc.getJSONObject(servName));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return js;
        }
    }
    
    
    
    // End Update Service Description
    
    // End migration buffer
    
    public static void setChangedFlag(){
        synchronized(LocalCDChangedFlagLock){
            LocalCDChangedFlag=true;
        }
    }
    
    public static void resetChangedFlag(){
        synchronized(LocalCDChangedFlagLock){
            LocalCDChangedFlag=false;
        }
    }
    
    //public static void setRequestSignal
    
    public static boolean getChangedFlag(){
        synchronized(LocalCDChangedFlagLock){
            return LocalCDChangedFlag;
        }
    }
    
    public static void setICNeedToRemoveFlag(boolean stat){
        synchronized(ICNeedToRemoveFlagLock){
            ICNeedToRemoveFlag = stat;
        }
    }
    
    public static boolean getICNeedToRemoveFlag(){
        synchronized(ICNeedToRemoveFlagLock){
            return ICNeedToRemoveFlag;
        }
    }
    
    public static void setICNeedToChangeFlag(boolean stat){
        synchronized(ICChangedFlagLock){
            ICChangedFlag=stat;
        }
    }
    
    public static boolean getICNeedToChangeFlag(){
        synchronized(ICChangedFlagLock){
            return ICChangedFlag;
        }
    }
    
    public static void setCDAmountChangedFlag(){
        synchronized(LocalCDAmountChangedFlagLock){
            LocalCDAmountChangedFlag=true;
        }
    }
    
    public static void resetCDAmountChangedFlag(){
        synchronized(LocalCDAmountChangedFlagLock){
            LocalCDAmountChangedFlag=false;
        }
    }
    
    public static boolean getCDAmountChangedFlag(){
        synchronized(LocalCDAmountChangedFlagLock){
            return LocalCDAmountChangedFlag;
        }
    }
    
    public static void setLocalSignalChangeLocation(String SSName, String CDName, String direction, String signalName, JSONObject configsChanges){
        
        int i = signalIndex;
        
        synchronized(LocalCDSignalLocationChangesLock){
            try{
            JSONObject jsSig = new JSONObject();
            
            jsSig.put("configs",configsChanges); //specific signal configuration
            jsSig.put("SSName",SSName);
            jsSig.put("CDName",CDName); 
            jsSig.put("SigDirection",direction); //input or output signal
            jsSig.put("SigName", signalName);
            
            LocalCDSignalLocationChanges.put(Integer.toString(i), jsSig);
            
            } catch (JSONException jex){
                jex.printStackTrace();
            }
        }
        synchronized(signalIndLock){
            signalIndex++;
        }
    }
    
    public static void setChannelLocationChange(String SSName, String CDName, String direction, String channelName, String channelType, JSONObject configs){
        
        int i = channelIndex;
        
        synchronized(CDChannelLocationChangesLock){
            try{
            JSONObject jsChan = new JSONObject();
            
            jsChan.put("configs",configs); //specific signal configuration
            jsChan.put("SSName",SSName);
            jsChan.put("CDName",CDName); 
            jsChan.put("SigDirection",direction); //input or output signal
            jsChan.put("SigName", channelName);
            jsChan.put("ChannelType", channelType);
            
            CDChannelLocationChanges.put(Integer.toString(i), jsChan);
            
            } catch (JSONException jex){
                jex.printStackTrace();
            }
        }
        synchronized(channelIndLock){
            channelIndex++;
        }
    }
    
    public static boolean IsLocalSignalChangesLocEmpty(){
        synchronized(LocalCDSignalLocationChangesLock){
            return LocalCDSignalLocationChanges.isEmpty();
        }
    }
    
    public static void ClearAmendedSignalChangeLoc(){
        synchronized(LocalCDSignalLocationChangesLock){
            LocalCDSignalLocationChanges = new JSONObject();
        }
    }
    
    public static boolean IsChannelChangesLocEmpty(){
        synchronized(CDChannelLocationChangesLock){
            return CDChannelLocationChanges.isEmpty();
        }
    }
    
    public static void ClearAmendedChannelChangeLoc(){
        synchronized(CDChannelLocationChangesLock){
           CDChannelLocationChanges = new JSONObject();
        }
    }
    
    public static JSONObject getLocalSignalChangesLocation(){
        synchronized(LocalCDSignalLocationChangesLock){
            return LocalCDSignalLocationChanges;
        }
    }
    
    public static JSONObject getLocalChannelChangesLocation(){
        synchronized(CDChannelLocationChangesLock){
            return CDChannelLocationChanges;
        }
    }
    
    public static void removeLocalSignalChangeLocation(String sigChangeIndex){
        
        synchronized(LocalCDSignalLocationChangesLock){
            
            if (LocalCDSignalLocationChanges.has(sigChangeIndex)){
                 LocalCDSignalLocationChanges.remove(sigChangeIndex);
            }
            
        }
        
    }
    
    public static void resetLocalSigChangeIndex(){
        synchronized(signalIndLock){
            signalIndex=1;
        }
    }
    
    public static void resetLocalChannelChangeIndex(){
        synchronized(channelIndLock){
            channelIndex=1;
        }
    }
    
    public static void setReqServiceMigrationAvailability(String CDName, String respResult){
          
          synchronized(ReqServiceMigrationBufferLock){
              
              try{
                  
                  if(ReqServiceMigrationBuffer.has(CDName)){
                 
                 
                     ReqServiceMigrationBuffer.remove(CDName);
                     ReqServiceMigrationBuffer.put(CDName,respResult);
                 
                 
                } else {
                      ReqServiceMigrationBuffer.put(CDName, respResult);
                }
                  
              } catch(JSONException jex){
                  jex.printStackTrace();
              }
             
          }
          
      }
      
      public static String getReqServiceMigrationAvailability(String CDName){
          
          String stat = "No response";
          
          synchronized(ReqServiceMigrationBuffer){
              if (ReqServiceMigrationBuffer.has(CDName)){
                  
                  try {
                      stat = ReqServiceMigrationBuffer.getString(CDName);
                  } catch (JSONException ex) {
                      ex.printStackTrace();
                  }
                  
              
              }
          
        }
      
         return stat;
      
      }
      
      public static boolean ReqServiceMigrationAvailContains(String CDName){
          
          boolean stat = false;
          
          synchronized(ReqServiceMigrationBufferLock){
             if(ReqServiceMigrationBuffer.has(CDName)){
                 stat=true;
             }
          }
          
          return stat;
      }
      
      public static void SetMigrationStatus(String DestSS, String Report){
          synchronized(MigrationReqReportLock){
              MigrationReqReport.put(DestSS,Report);
          }
      }
      
      public static String GetMigrationStatus(String DestSS){
          synchronized(MigrationReqReportLock){
              return (String)MigrationReqReport.get(DestSS);
          }
      }
      
      /*
      public static void setReqServiceMigration(String destAddress, String sourceAddress, String CDName, String ACKSigName){
          
          JSONObject js = new JSONObject();
          JSONObject js2 = new JSONObject();
          
          synchronized(ReqServiceMigrationBufferLock){
              try {
                  js.put("destAddress",destAddress);
                  js.put("srcAddress", sourceAddress);
                  js2.put(ACKSigName,js);
                  
                  if (ReqServiceMigrationBuffer.isEmpty()){
                      ReqServiceMigrationBuffer.put(CDName,js2);
                  } else {
                      JSONObject js3 = (JSONObject)ReqServiceMigrationBuffer.get(CDName);
                      
                      if(js3.has(ACKSigName)){
                          js3.remove(ACKSigName);
                          js3.put(ACKSigName,js2);
                      } else {
                          js3.put(ACKSigName,js2);
                      }
                      
                      ReqServiceMigrationBuffer.put(CDName,js3);
                  }
                  
              } catch (JSONException ex) {
                  ex.printStackTrace();
              }
          }
          
      }
      
      public static JSONObject getReqServiceMigration(String CDName, String ACKSigName){
          
          JSONObject jsSigList = new JSONObject();
          JSONObject jsAnsw = new JSONObject();
          
          synchronized(ReqServiceMigrationBuffer){
              if (ReqServiceMigrationBuffer.has(CDName)){
                  
                  try {
                      
                      if (ReqServiceMigrationBuffer.has(CDName)){
                          jsSigList = ReqServiceMigrationBuffer.getJSONObject(CDName);
                          
                          if (jsSigList.has(ACKSigName)){
                              jsAnsw = jsSigList.getJSONObject(ACKSigName);
                          }
                          
                      }
                      
                      
                  } catch (JSONException ex) {
                      ex.printStackTrace();
                  }
                  
              
            }
          
        }
      
         return jsAnsw;
      
      }
      */
    
}
