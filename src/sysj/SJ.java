package sysj;

import java.lang.reflect.Field;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.SJSOAMessage;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SOAFacility.*;
import systemj.common.SchedulersBuffer;
import systemj.interfaces.Scheduler;
import systemj.lib.input_Channel;
import systemj.lib.output_Channel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Udayanto
 */
public class SJ {
    
    private final static Object CreateCDLock = new Object();
    private final static Object KillCDLock = new Object();
    private final static Object MigrateCDLock = new Object();
    
    private final static Object HibernateOneLock = new Object();
    private final static Object WakeUpLock = new Object();
    private final static Object ChannelQueryLock = new Object();
    
    //Create CD, usble for clone and update
    public static synchronized void CreateCD(String CDName, String filenameCDMapXML, String filenameCDServDescXML){
        
        //synchronized(CreateCDLock){
            //JSONObject CDMap = ParseCDMap(filenameCDMapXML);
            JSONObject CDMap = ParseCDMap(CDName, filenameCDMapXML);
            JSONObject CDServDesc = ParseServDesc(filenameCDServDescXML);
            //System.out.println("parsed CDMap: " +CDMap.toString());
            //Vector vec = new Vector();
            //vec.addElement(CDName);
        
            CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
            CDLCBuffer.AddTempSigChanMapCD(CDMap);
            CDLCBuffer.putUpdateServiceDescription(CDServDesc);
        
            //System.out.println("Request create transferred!");
        //}
        
    }
    
    public static synchronized void CreateCD(String CDName, String ServName,String filenameCDMapXML, String filenameCDServDescXML){
        
        //synchronized(CreateCDLock){
            //JSONObject CDMap = ParseCDMap(filenameCDMapXML);
            JSONObject CDMap = ParseCDMap(CDName, filenameCDMapXML);
            JSONObject CDServDesc = ParseServDesc(CDName, ServName,filenameCDServDescXML);
            //System.out.println("parsed CDMap: " +CDMap.toString());
            //Vector vec = new Vector();
            //vec.addElement(CDName);
        
            CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
            CDLCBuffer.AddTempSigChanMapCD(CDMap);
            CDLCBuffer.putUpdateServiceDescription(CDServDesc);
        
            //System.out.println("Request create transferred!");
        //}
        
    }
    
    public static synchronized void CreateCD(String CDName, String CDClassName, String ServName,String filenameCDMapXML, String filenameCDServDescXML){
        
        //synchronized(CreateCDLock){
            //JSONObject CDMap = ParseCDMap(filenameCDMapXML);
            JSONObject CDMap = ParseCDMap(CDName, CDClassName,filenameCDMapXML);
            JSONObject CDServDesc = ParseServDesc(CDName, ServName,filenameCDServDescXML);
            //System.out.println("parsed CDMap: " +CDMap.toString());
            //Vector vec = new Vector();
            //vec.addElement(CDName);
        
            CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
            CDLCBuffer.AddTempSigChanMapCD(CDMap);
            CDLCBuffer.putUpdateServiceDescription(CDServDesc);
        
            //System.out.println("Request create transferred!");
        //}
        
    }
    
    
    
    public static synchronized void CreateCD(String CDName, String filenameCDMapXML){
        
        //synchronized(CreateCDLock){
            JSONObject CDMap = ParseCDMap(filenameCDMapXML);
            //JSONObject CDServDesc = ParseServDesc(filenameCDServDescXML);
            //System.out.println("parsed CDMap: " +CDMap.toString());
            //Vector vec = new Vector();
            //vec.addElement(CDName);
        
            CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
            CDLCBuffer.AddTempSigChanMapCD(CDMap);
           // CDLCBuffer.putUpdateServiceDescription(CDServDesc);
        
            //System.out.println("Request create transferred!");
        //}
        
    }
    
    //Update Mapping only
    
    //end mapping only
    
    public static synchronized void KillCD(String CDName){
        //synchronized (KillCDLock){
           // Vector vec = new Vector();
            //vec.addElement(CDName);
        
            CDLCBuffer.TransferRequestKillCDToBuffer(CDName);
        //}
    }
    
    
    public static synchronized void MigrateCD(String CDName,String fileNameCDMap, String fileNameCDServDesc, String DestinationSS, String MigType){
        
        //synchronized (MigrateCDLock){
            if(MigType.equals("strong") || MigType.equals("weak")){
           
                JSONObject CDMap = ParseCDMap(fileNameCDMap);
                JSONObject CDServDesc = ParseServDesc(fileNameCDServDesc);
            
                //CDLCBuffer.AddTempMigrateSignalChannelMap(CDMap);
                //CDLCBuffer.AddMigrateServiceDescription(CDServDesc);
            
                CDLCBuffer.AddMigratingCDNameToBuffer(CDName);
                
                CDLCBuffer.AddRequestMigrate(CDName, DestinationSS, CDMap, CDServDesc,MigType);
            
            //sjsoamsg.ConstructReqStrongMigrationMessage(DestinationSS, SJSSCDSignalChannelMap.getLocalSSName());
            
            } else {
            
                throw new RuntimeException("Unknown migration type, choose either 'weak' or 'strong'");
            }
        //}
        
    }
    
    public static synchronized void SuspendCD(String CDName){
        //synchronized(HibernateOneLock){
            //Vector vec = new Vector();
            //vec.addElement(CDName);
            CDLCBuffer.TransferRequestHibernateCDToBuffer(CDName);
        //}
         
    }
    
    public static synchronized void WakeUpCD(String CDName){
        //synchronized(WakeUpLock){
            //Vector vec = new Vector();
            //vec.addElement(CDName);
            CDLCBuffer.TransferRequestWakeUpCDToBuffer(CDName);
        //}
        
    }
    
    public static JSONObject ParseCDMap(String filename){
        
        CDLCMapParser cdpars = new CDLCMapParser();
        
        JSONObject js = new JSONObject();
        try {
            js = cdpars.parse(filename);
            
            //System.out.println("Parsed New CDMap: " +js.toPrettyPrintedString(2, 0));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return js;
    }
    
    public static synchronized void ReconfigChan(String CDName, String ChanName, String ChanDir, String PartnerCDName, String PartnerChanName, String PartnerSSName){
       
        CDLCBuffer.AddChanReconfig(CDName, CDName, ChanDir, PartnerCDName, PartnerChanName);
    }
    
    public static synchronized void ReconfigChan(String CDName, String ChanName, String ChanDir, String PartnerCDName, String PartnerChanName){
        
    }
    
    public static JSONObject ParseCDMap(String CDName, String filename){
        
        CDLCMapParser cdpars = new CDLCMapParser();
        
        JSONObject js = new JSONObject();
        try {
            js = cdpars.parse(CDName, filename);
            
            //System.out.println("Parsed New CDMap: " +js.toPrettyPrintedString(2, 0));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return js;
    }
    
    public static JSONObject ParseCDMap(String CDName, String CDClassName, String filename){
        
        CDLCMapParser cdpars = new CDLCMapParser();
        
        JSONObject js = new JSONObject();
        try {
            js = cdpars.parse(CDName, CDClassName,filename);
            
            //System.out.println("Parsed New CDMap: " +js.toPrettyPrintedString(2, 0));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return js;
    }
    
    public static JSONObject ParseServDesc(String filename){
        
        ServDescParser cdsdparse = new ServDescParser();
        
        JSONObject js = new JSONObject();
        try {
            js = cdsdparse.parse(filename);
            
            //System.out.println("Parsed New SD: " +js.toPrettyPrintedString(2, 0));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return js;
        
    }
    
    public static JSONObject ParseServDesc(String CDName, String ServName,String filename){
        
        ServDescParser cdsdparse = new ServDescParser();
        
        JSONObject js = new JSONObject();
        try {
            js = cdsdparse.parse(CDName,ServName,filename);
            
            //System.out.println("Parsed New SD: " +js.toPrettyPrintedString(2, 0));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return js;
        
    }
    
    /*
    public static boolean IsPartnerTerminated(String CDName, String ChannelName, String ChannelDirection){ //in or out
        synchronized(ChannelQueryLock){
            
            boolean res = false;
            
            JSONObject jsAllCDMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
            try {
                JSONObject jsCDTrgtMap = jsAllCDMap.getJSONObject(CDName);
                
                JSONObject jsChansCD = jsCDTrgtMap.getJSONObject("SChannels");
                
                if(ChannelDirection.equalsIgnoreCase("input")){
                     
                    JSONObject jsInChansCD = jsChansCD.getJSONObject("inputs");
                    
                    if(jsInChansCD.has(ChannelName)){
                        
                        Scheduler sc = (Scheduler)SchedulersBuffer.ObtainSchedulers().get(0);
                        
                        if(sc.SchedulerHasCD(CDName)){
                            
                            String cname = ChannelName+"_in";
                            
                            ClockDomain cdins = sc.getClockDomain(CDName);
                            
                            try {
                                Field f = cdins.getClass().getField(cname);
                                
                                input_Channel inchan = (input_Channel)f.get(cdins);
                                
                                res = inchan.IsInputChannelTerminated();
                                
                            } catch (Exception ex) {
                               ex.printStackTrace();
                               System.exit(1);
                            } 
                            
                        } else if(CDObjectsBuffer.CDObjBufferHas(CDName)){
                            
                            String cname = ChannelName+"_in";
                            
                            ClockDomain cdins = CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                            
                            try {
                                Field f = cdins.getClass().getField(cname);
                                
                                input_Channel inchan = (input_Channel)f.get(cdins);
                                
                                res = inchan.IsInputChannelTerminated();
                                
                            } catch (Exception ex) {
                               ex.printStackTrace();
                               System.exit(1);
                            } 
                            
                            
                        }
                        
                    } else {
                        System.err.println(ChannelDirection+ " Channel of name "+ChannelName+" in the CD name: " +CDName+ " is non existent!");
                        System.exit(1);
                    }
                    
                } else if(ChannelDirection.equalsIgnoreCase("output")){
                    
                    JSONObject jsOutChansCD = jsChansCD.getJSONObject("outputs");
                    
                    if(jsOutChansCD.has(ChannelName)){
                        
                        Scheduler sc = (Scheduler)SchedulersBuffer.ObtainSchedulers().get(0);
                        
                        if(sc.SchedulerHasCD(CDName)){
                            
                            String cname = ChannelName+"_out";
                            
                            ClockDomain cdins = sc.getClockDomain(CDName);
                            
                            try {
                                Field f = cdins.getClass().getField(cname);
                                
                                output_Channel ochan = (output_Channel)f.get(cdins);
                                
                                res = ochan.IsOutputChannelTerminated();
                                
                            } catch (Exception ex) {
                               ex.printStackTrace();
                               System.exit(1);
                            } 
                            
                        } else if(CDObjectsBuffer.CDObjBufferHas(CDName)){
                            
                            String cname = ChannelName+"_out";
                            
                            ClockDomain cdins = CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                            
                            try {
                                Field f = cdins.getClass().getField(cname);
                                
                                output_Channel ochan = (output_Channel)f.get(cdins);
                                
                                res = ochan.IsOutputChannelTerminated();
                                
                            } catch (Exception ex) {
                               ex.printStackTrace();
                               System.exit(1);
                            } 
                            
                            
                        }
                        
                    } else {
                        System.err.println(ChannelDirection+ " Channel of name"+ChannelName+" in the CD name: " +CDName+ " is non existent!");
                        System.exit(1);
                    }
                    
                } else {
                    
                    System.err.println("Wrong direction for IsChannelTerminatedFunction executed in CD: " +CDName+" , choose input or output!");
                    
                    System.exit(1);
                }
                
            } catch (JSONException ex) {
                
                System.err.println("Channel "+ChannelName+" in the CD name: " +CDName+ " is non existent!");
                
                ex.printStackTrace();
               
                
            }
            
            return res;
            
        }
    }
    */
}
