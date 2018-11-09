/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package systemj.common.SOAFacility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.bootstrap.ClockDomain;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.CyclicScheduler;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.SOSJConstructsMessageGenerator;
//import systemj.common.SignalObjBuffer;
import systemj.interfaces.GenericInterface;
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
public class ClockDomainLifeCycleManager {
    
    /*
    public void runForCreate(){
       
        try {
            
            ClockDomainLifeCycleSignalImpl cdlcimpl = new ClockDomainLifeCycleSignalImpl();
            
            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
            JSONObject jsLocalCDs  = jsCurrMap.getJSONObject(keyCurrSS);
            
            
        } catch (JSONException ex) {
            Logger.getLogger(ClockDomainLifeCycleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex){
            ex.printStackTrace();
            System.out.println("CD Class not found!");
        }catch (Exception ex){
            ex.printStackTrace();
            
        }
    }
    */
    
    public Vector run(Scheduler sc, InterfaceManager im){
        
        Vector vecSCIM = new Vector();
        SOSJConstructsMessageGenerator sosjcmg = new SOSJConstructsMessageGenerator();
        //System.out.println("CDLCM is run!");
        
         ClockDomainLifeCycleSignalImpl cdlcimpl = new ClockDomainLifeCycleSignalImpl();
        
        try {
            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
            JSONObject jsLocalCDs  = jsCurrMap.getJSONObject(keyCurrSS);
            
            
            
            if(CDLCBuffer.GetMigGoAheadModPartner()){
                
                //ClockDomainLifeCycleSignalImpl cdlcsigimpl = new ClockDomainLifeCycleSignalImpl();
                    Hashtable hashMigrating = CDLCBuffer.GetAllMigCDObjsBuffer();
                
                    Vector vecAllCDIns = (Vector)hashMigrating.get("CDObj");
                    String DestSS = (String)hashMigrating.get("DestSS");
                    String migType = (String) hashMigrating.get("MigType");
                    JSONObject migratingMap = (JSONObject) hashMigrating.get("MigratingMap");
                    
                    for(int r=0;r<vecAllCDIns.size();r++){
                            
                            ClockDomain MigratingCDIns = (ClockDomain)vecAllCDIns.get(r);
                            
                            
                            
                                                     if(MigratingCDIns.getState().equalsIgnoreCase("Active")){
                                
                                                        vecSCIM = cdlcimpl.ModifyMigratingCDLocalPartnerToDistributed(jsLocalCDs, migratingMap,SJSSCDSignalChannelMap.getLocalSSName(),MigratingCDIns.getName(), MigratingCDIns, migType, DestSS,im, sc);

                                                     } else if (MigratingCDIns.getState().equalsIgnoreCase("Sleep")){

                                                        vecSCIM = cdlcimpl.ModifyMigratingCDLocalPartnerToDistributed(jsLocalCDs, migratingMap, SJSSCDSignalChannelMap.getLocalSSName(),MigratingCDIns.getName(), MigratingCDIns, migType, DestSS,im, sc);

                                                     }
                                              String MigCDName = MigratingCDIns.getName();
                                              CDLCBuffer.RemoveCDMacroState(MigCDName);
                            //ClockDomain MigratingCDIns = (ClockDomain)vecAllCDIns.get(r);
                        }
                    CDLCBuffer.SetMigGoAheadModPartner(false);
                    CDLCBuffer.SetMigModPartnerDone(true);
                    
                
            }
            
            if(!CDLCBuffer.IsRequestCreateCDEmpty()){
                
                int createCDSize = CDLCBuffer.getRequestCreateSize();
                
                //first collect all CD to be created, and couple all new channels of the new CDs
                    
                Vector allCreateCDs =  CDLCBuffer.getAllRequestCreateCD();
                
                Hashtable CreatedClockDomains = new Hashtable();
                
                for (int i=0;i<allCreateCDs.size();i++){
                    
                    String CDName = CDLCBuffer.getRequestCreateCD(i);
                    JSONObject CDDet = CDLCBuffer.GetTempSigChanMap(CDName);
                    
                    JSONObject jsCDDet = CDDet.getJSONObject(CDName);
                    
                    String sclazz = jsCDDet.getString("CDClassName");
                    
                    ClockDomain newCD = (ClockDomain)Class.forName(sclazz).newInstance();
                    newCD.setName(CDName);
                    CreatedClockDomains.put(CDName, newCD);
                          
                }
                
                    //
                
                for(int i=0;i<createCDSize;i++){
                    
                    String CDName = CDLCBuffer.getRequestCreateCD(i);
                    
                    //check if the cd with the same name exist, if it does, than it means overwriting or update, else, it's creating new
                    
                    
                    if(sc.SchedulerHasCD(CDName)){
                        
                        //stop and terminate CD Descriptors
                        
                        Vector vec = cdlcimpl.removeSigChanObjModifySCIM(jsLocalCDs, keyCurrSS, CDName, "Active", im, sc);
                        
                        sc = (Scheduler)vec.get(1);
                        im = (InterfaceManager) vec.get(0);
                        
                                JSONObject CDDet = CDLCBuffer.GetTempSigChanMap(CDName);
                                //System.out.println("Parsed upd CD: " +CDDet.toPrettyPrintedString(2, 0));
                                JSONObject jsCDDet = CDDet.getJSONObject(CDName);
                                String sclazz = jsCDDet.getString("CDClassName");

                                ClockDomain newCD = (ClockDomain)Class.forName(sclazz).newInstance();
                                newCD.setName(CDName);
                                newCD.setState("Active");
                        
                        vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(CDDet, keyCurrSS, CDName, newCD,CreatedClockDomains, im, sc);
                        
                        sc = (Scheduler)vec.get(1);
                        im = (InterfaceManager) vec.get(0);
                        
                    } else if (CDObjectsBuffer.CDObjBufferHas(CDName)){
                        
                        //ClockDomain cd = CDObjectsBuffer.GetCDInstancesFromBuffer(CDName);
                        
                        Vector vec = cdlcimpl.removeSigChanObjModifySCIM(jsLocalCDs, keyCurrSS, CDName, "Sleep", im, sc);
                        
                        sc = (Scheduler)vec.get(1);
                        im = (InterfaceManager) vec.get(0);
                        /*
                        if(cd.IsTimedSleep()){
                            
                           long sleeptime =  cd.getSleepTimeAmount();
                           long starttime =  cd.getStartSleepTime();
                            
                            JSONObject CDDet = CDLCBuffer.GetTempSigChanMap(CDName);
                
                            String sclazz = CDDet.getString("CDClassName");

                            ClockDomain newCD = (ClockDomain)Class.forName(sclazz).newInstance();
                            newCD.setName(CDName);
                            newCD.setState("Sleep");
                            newCD.setSleepTimeDet(starttime, sleeptime);
                        
                            vec = cdlcimpl.AddSigChanObjNotInitModifySCIM(jsLocalCDs, keyCurrSS, CDName, newCD, im, sc);
                            
                            sc = (Scheduler)vec.get(1);
                            im = (InterfaceManager) vec.get(0);
                            
                        } else 
                        */
                        {
                            
                            JSONObject CDDet = CDLCBuffer.GetTempSigChanMap(CDName);
                             JSONObject jsCDDet = CDDet.getJSONObject(CDName);
                            String sclazz = jsCDDet.getString("CDClassName");

                            ClockDomain newCD = (ClockDomain)Class.forName(sclazz).newInstance();
                            newCD.setName(CDName);
                            newCD.setState("Sleep");
                            
                        
                            vec = cdlcimpl.AddSigChanObjNotInitModifySCIM(jsLocalCDs, keyCurrSS, CDName, newCD, CreatedClockDomains,im, sc);
                            
                            sc = (Scheduler)vec.get(1);
                            im = (InterfaceManager) vec.get(0);
                            
                            //vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(CDDet, keyCurrSS, CDName, newCD, im, sc);
                        
                            //sc = (Scheduler)vec.get(1);
                            //im = (InterfaceManager) vec.get(0);
                            
                        }
                        
                        
                        
                    } else {
                        
                        //creating new CD
                        
                        // get mapping details
                        JSONObject CDDet = CDLCBuffer.GetTempSigChanMap(CDName);
                        
                         JSONObject CdIndivDet = CDDet.getJSONObject(CDName);
                                 String sclazz = CdIndivDet.getString("CDClassName");

                            ClockDomain newCD = (ClockDomain)Class.forName(sclazz).newInstance();
                            newCD.setName(CDName);
                            newCD.setState("Active");
                            
                            Vector vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(CDDet, keyCurrSS, CDName, newCD, CreatedClockDomains, im, sc);
                            
                            sc = (Scheduler)vec.get(1);
                            im = (InterfaceManager) vec.get(0);
                        
                    }
                    
                    JSONObject jsCDSD = CDLCBuffer.GetTempUpdateServDesc();
                    
                    Enumeration keysJSCDSD = jsCDSD.keys();
                    
                    while(keysJSCDSD.hasMoreElements()){
                        
                        String keyServName = keysJSCDSD.nextElement().toString();
                        
                        
                        JSONObject jsIndivServ = jsCDSD.getJSONObject(keyServName);
                        
                        String assocCDName = jsIndivServ.getString("associatedCDName");
                        
                        if(assocCDName.equals(CDName)){
                            //String servRole = jsIndivServ.getString("serviceRole");
                        
                            //if(servRole.equalsIgnoreCase("provider")){
                                SOABuffer.setInitAdvVisibOneByOne(keyServName, "visible");
                            //}
                        }
                        
                    }
                    
                    SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsCDSD);
                    
                    CDLCBuffer.ClearTempUpdateServDesc();
                    
                }
                
                //if(!CDLCBuffer.IsRequestUpdateServDescIsEmpty()){
                    
                    //JSONObject jsCDSD = CDLCBuffer.GetTempUpdateServDesc();
                    
                    
                    
                //}
                
                //if(!CDLCBuffer.IsRequestUpdateServDescIsEmpty()){
                                                                 
                //       JSONObject jsServDesc = CDLCBuffer.GetRequestCloneServDescFromBuffer();
                                                                 
                //       SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsServDesc, true);
                                                                 
                //       CDLCBuffer.ClearTempUpdateServDesc();
               // }
                CDLCBuffer.clearRequestCreateCD();
            } 
            
            
            if(!CDLCBuffer.IsRequestWakeUpCDEmpty()){
                                    
                Vector CDToWakeUp = CDLCBuffer.GetAllRequestWakeUpCD();
                
                Hashtable CDinSSGroup = new Hashtable();
                
                 JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
                
                                            for(int i=0;i<CDLCBuffer.GetRequestWakeUpCDSize();i++){
                                                          
                                                //long startTime= System.currentTimeMillis();
                                                 
                                                //String keyCDName = CDLCBuffer.GetRequestWakeUpCD(i);
                                                String keyCDName = CDToWakeUp.get(i).toString();
                                                
                                               // if(ClockDomainLifeCycleStatusRepository.IsCDStatusRepositoryHas(keyCDName)){
                                                    
                                                   // if(ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equalsIgnoreCase("Sleep")){
                                                    if(im.getCDLocation(keyCDName).equals(keyCurrSS)){
                                                        
                                                        if(CDObjectsBuffer.CDObjBufferHas(keyCDName)){
                                                            
                                                            Vector vec = cdlcimpl.ReactivateSigChanObjAndInitModifySCIM(jsLocalCDs, keyCurrSS, keyCDName, im, sc);
                                                    
                                                            sc = (Scheduler) vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);

                                                            Enumeration keysServNames = jsIntServ.keys();

                                                            while(keysServNames.hasMoreElements()){

                                                                String servName = keysServNames.nextElement().toString();


                                                                JSONObject jsIndivServ = jsIntServ.getJSONObject(servName);

                                                                String assocCDName = jsIndivServ.getString("associatedCDName");

                                                                if(assocCDName.equals(keyCDName)){
                                                                    //String servRole = jsIndivServ.getString("serviceRole");

                                                                    //if(servRole.equalsIgnoreCase("provider")){
                                                                        SOABuffer.modifyAdvModBufferOfServiceName(servName, "visible");
                                                                    //}
                                                                }

                                                            }
                                                            
                                                        }
                                                        
                                                        
                                                        
                                                    } else {
                                                        
                                                        if(im.hasCDLocation(keyCDName)){
                                                        
                                                            String ssLoc = im.getCDLocation(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        } else {
                                                            
                                                            String ssLoc = CDLCBuffer.GetCDLocTempBuffer(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        }
                                                        
                                                    }
                                                
                                                        
                                                                
                                                        // long endTime= System.currentTimeMillis();
                                                            
                                                         //   long totTime = endTime-startTime;
                                                            
                                                           // System.out.println("Total time for waking up CD: " +keyCDName+" is : " +totTime+ " ms");
                                                
                                                        
                                                        //ClockDomainLifeCycleStatusRepository.ModifyCDStatus(keyCDName, "Active");
                                                    
                                                    //} else {
                                                    //    System.out.println("Try to wake up 'non hibernated' CD of name: " +keyCDName);
                                                   // }
                                                    
                                               /// } else {
                                               //     System.out.println("Try to wake up 'non existent' CD of name: " +keyCDName);
                                               // }
                                                        
                                                        
                                            }
                                            
                                            if(CDinSSGroup.size()>0){
                                                
                                                Enumeration keysSSname = CDinSSGroup.keys();
                                                
                                                while(keysSSname.hasMoreElements()){
                                                 
                                                    String destSS = keysSSname.nextElement().toString();
                                                    
                                                    JSONObject jsCDNames = (JSONObject)CDinSSGroup.get(destSS);
                                                    
                                                    String message = sosjcmg.GenerateWakeUpCDsMessageOfJSON(destSS, jsCDNames);
                                                    
                                                    String SSAddr = RegAllSSAddr.getSSAddrOfSSName(destSS);
                                                    
                                                    sendReconfigMessage(SSAddr, message);
                                                    
                                                }
                                                
                                            }
                                            
                                            CDLCBuffer.ClearRequestWakeUpCD();
                                            
                                        }
            
                                     
            
                                        if(!CDLCBuffer.IsRequestHibernateCDEmpty()){
                                            
                                            Vector CDToSuspend = CDLCBuffer.GetAllRequestHibernateCD();
                                            
                                            Hashtable CDinSSGroup = new Hashtable();
                                            
                                            JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
                                            
                                            int j = CDLCBuffer.GetRequestHibernateCDSize();
                                            
                                            //int k=0;
                                            
                                            for(int i=0;i<j;i++){
                                                
                                                //long startTime = System.currentTimeMillis();
                                                
                                                //String keyCDName = CDLCBuffer.GetRequestHibernateCD(i);
                                                String keyCDName = CDToSuspend.get(i).toString();
                                            //    if(ClockDomainLifeCycleStatusRepository.IsCDStatusRepositoryHas(keyCDName)){
                                                    
                                                    if(im.getCDLocation(keyCDName).equals(keyCurrSS)){

                                                        if (sc.SchedulerHasCD(keyCDName)){  //if CD is active, it will be in scheduler data structures

                                                                 Vector vec = cdlcimpl.disableSigChanObjModifySCIM(jsLocalCDs, keyCurrSS, keyCDName, im, sc);

                                                                 im = (InterfaceManager)vec.get(0);
                                                                 sc = (Scheduler) vec.get(1);

                                                                 //ClockDomain cdObj = CDObjectsBuffer.GetCDInstancesFromBuffer(keyCDName);

                                                                 //cdObj.setState("Sleep");



                                                                Enumeration keysServNames = jsIntServ.keys();

                                                                while(keysServNames.hasMoreElements()){

                                                                    String servName = keysServNames.nextElement().toString();


                                                                    JSONObject jsIndivServ = jsIntServ.getJSONObject(servName);

                                                                    String assocCDName = jsIndivServ.getString("associatedCDName");


                                                                    if(assocCDName.equals(keyCDName)){

                                                                        //String servRole = jsIndivServ.getString("serviceRole");

                                                                        //if(servRole.equalsIgnoreCase("provider")){
                                                                            SOABuffer.modifyAdvModBufferOfServiceName(servName, "invisible");
                                                                        //}

                                                                    }

                                                                }


                                                    }
                                                    
                                                } else {
                                                    
                                                        if(im.hasCDLocation(keyCDName)){
                                                        
                                                            String ssLoc = im.getCDLocation(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        } else {
                                                            
                                                            String ssLoc = CDLCBuffer.GetCDLocTempBuffer(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        }
                                                        
                                                }
                                                
                                            }
                                            
                                            
                                            if(CDinSSGroup.size()>0){
                                                
                                                Enumeration keysSSname = CDinSSGroup.keys();
                                                
                                                while(keysSSname.hasMoreElements()){
                                                 
                                                    String destSS = keysSSname.nextElement().toString();
                                                    
                                                    JSONObject jsCDNames = (JSONObject)CDinSSGroup.get(destSS);
                                                    
                                                    String message = sosjcmg.GenerateSuspendCDsMessageOfJSON(destSS, jsCDNames);
                                                    
                                                    String SSAddr = RegAllSSAddr.getSSAddrOfSSName(destSS);
                                                    
                                                    sendReconfigMessage(SSAddr, message);
                                                    
                                                }
                                                
                                            }
                                            
                                            CDLCBuffer.ClearRequestHibernateCD();
                                            
                                        }
                                        
                                        //clone only , better to separate condition of providing signal channel mapping and service description
                                        
                                        
                                        
                                        /*
                                        if (!CDLCBuffer.IsRequestCloneCDEmpty()){
                                            
                                            //get mapping and service descrption..has to be provided beforehand by users
                                            
                                            //JSONObject jsServDesc = CDLCBuffer.GetRequestCloneServDescFromBuffer();
                                            
                                            //SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsServDesc, true);
                                            
                                            //for(int i=0;i<CDLCBuffer.GetRequestCloneCDSize();i++){
                                                
                                                Enumeration keysReqClone = CDLCBuffer.GetRequestCloneCD().keys();
                                                
                                                while (keysReqClone.hasMoreElements()){
                                                    
                                                    String keyCDName = keysReqClone.nextElement().toString();
                                                    
                                                    String copyCDName = CDLCBuffer.GetRequestCloneCD().get(keyCDName).toString();
                                                    
                                                    if(ClockDomainLifeCycleStatusRepository.IsCDStatusRepositoryHas(keyCDName)){
                                                     
                                                        if(ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equals("Active")){
                                                         
                                                            //boolean blocked = CheckChannels(jsLocalCDs, keyCDName);
                                                            
                                                            if(!blocked){
                                                                
                                                                ClockDomain copiedCDObj = sc.getClockDomain(keyCDName);
                                                                copiedCDObj.setName(copyCDName);
                                                            
                                                                if(CDLCBuffer.ReqCloneCDMapContains(copyCDName)){
                                                                    JSONObject jsCDMapCopy = new JSONObject();
                                                                    //JSONObject jsSSAddedCDMap = new JSONObject();
                                                                    jsCDMapCopy.put(copyCDName, CDLCBuffer.GetReqCloneIndivCDMapBuffer(copyCDName));
                                                                    //jsSSAddedCDMap.put(localSSName, jsCDMapCopy);
                                                                    
                                                                    
                                                                    Vector vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(jsCDMapCopy, keyCurrSS, copyCDName, copiedCDObj, im, sc);
                                                            
                                                                    sc = (Scheduler)vec.get(0);
                                                                    im = (InterfaceManager) vec.get(1);
                                                                    
                                                                    CDLCBuffer.RemoveReqCloneCDMapEntryFromBuffer(copyCDName);
                                                                    
                                                                } else {
                                                                    
                                                                    //No new mapping from user, so copy mapping from the target CD mapping
                                                                    
                                                                    JSONObject jsAllCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                                                    
                                                                    JSONObject jsCDMapCopy = jsAllCDs.getJSONObject(keyCDName);
                                                                    
                                                                    jsAllCDs.put(copyCDName, jsCDMapCopy);
                                                                    //make a copy of CD Map from original CD map, then change the CD Name with the copied name
                                                                           
                                                                    Vector vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(jsAllCDs, keyCurrSS, copyCDName, copiedCDObj, im, sc);
                                                            
                                                                    sc = (Scheduler)vec.get(0);
                                                                    im = (InterfaceManager) vec.get(1);
                                                                    
                                                                }
                                            
                                                                if(!CDLCBuffer.IsRequestCloneServDescBufferEmpty()){
                                                
                                                                    JSONObject jsReqCloneServDesc = CDLCBuffer.GetRequestCloneServDescFromBuffer();
                                                                    
                                                                    SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsReqCloneServDesc, true);
                                                                    
                                                                    CDLCBuffer.ClearRequestCloneServDescBuffer();
                                                                    
                                                                }
                                                                
                                                                ClockDomainLifeCycleStatusRepository.AddCDNameAndStatus(keyCDName, keyCurrSS);
                                                                
                                                                CDLCBuffer.RemoveRequestCloneCD(keyCDName);
                                                                
                                                            } else if (ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equals("Sleep")){
                                                                
                                                                ClockDomain copiedCDObj = CDObjectsBuffer.GetCDInstancesFromMap(keyCDName);
                                                                
                                                                copiedCDObj.setName(copyCDName);
                                                                
                                                                
                                                                
                                                                if(CDLCBuffer.ReqCloneCDMapContains(copyCDName)){
                                                                    JSONObject jsCDMapCopy = new JSONObject();
                                                                    //JSONObject jsSSAddedCDMap = new JSONObject();
                                                                    jsCDMapCopy.put(copyCDName, CDLCBuffer.GetReqCloneIndivCDMapBuffer(copyCDName));
                                                                    //jsSSAddedCDMap.put(localSSName, jsCDMapCopy);
                                                                    
                                                                    
                                                                    Vector vec = cdlcimpl.AddSigChanObjNotInitModifySCIM(jsCDMapCopy, keyCurrSS, copyCDName, copiedCDObj, im, sc);
                                                            
                                                                    sc = (Scheduler)vec.get(0);
                                                                    im = (InterfaceManager) vec.get(1);
                                                                    
                                                                    CDLCBuffer.RemoveReqCloneCDMapEntryFromBuffer(copyCDName);
                                                                    
                                                                } else {
                                                                    
                                                                    //No new mapping from user, so copy mapping from the target CD mapping
                                                                    
                                                                    JSONObject jsAllCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                                                    
                                                                    JSONObject jsCDMapCopy = jsAllCDs.getJSONObject(keyCDName);
                                                                    
                                                                    jsAllCDs.put(copyCDName, jsCDMapCopy);
                                                                    //make a copy of CD Map from original CD map, then change the CD Name with the copied name
                                                                           
                                                                    Vector vec = cdlcimpl.AddSigChanObjAndInitModifySCIM(jsAllCDs, keyCurrSS, copyCDName, copiedCDObj, im, sc);
                                                            
                                                                    sc = (Scheduler)vec.get(0);
                                                                    im = (InterfaceManager) vec.get(1);
                                                                    
                                                                }
                                                                
                                                                if(!CDLCBuffer.IsRequestCloneServDescBufferEmpty()){
                                                
                                                                    JSONObject jsReqCloneServDesc = CDLCBuffer.GetRequestCloneServDescFromBuffer();
                                                                    
                                                                    SJServiceRegistry.AppendNodeServicesToCurrentRegistry(jsReqCloneServDesc, true);
                                                                    
                                                                    CDLCBuffer.ClearRequestCloneServDescBuffer();
                                                                    
                                                                }
                                                                
                                                            }
                                                            
                                                        }
                                                     
                                                 } else {
                                                     System.out.println("Try to hibernate 'non-existent CD' of name: " +keyCDName);
                                                 }
                                                    
                                                }
                                                
                                                //String keyCDName = CDLCBuffer.GetRequestCloneCD(i);
                                                
                                            //}
                                            
                                        }
                                        */
                                        
                                        if(!CDLCBuffer.IsRequestKillCDEmpty()){
                                            
                                            Hashtable CDinSSGroup = new Hashtable();
                                            
                                            Vector remvdCDName = new Vector();
                                            
                                            for(int i=0;i<CDLCBuffer.GetRequestKillCDSize();i++){
                                                
                                                String keyCDName = CDLCBuffer.GetRequestKillCD(i);
                                                
                                               // if(ClockDomainLifeCycleStatusRepository.IsCDStatusRepositoryHas(keyCDName)){
                                                   
                                                  //  if(ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equalsIgnoreCase("Active") || ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equalsIgnoreCase("Sleep") || ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName).equalsIgnoreCase("Inactive") ){
                                                        
                                                       // String stat = ClockDomainLifeCycleStatusRepository.GetCDStatus(keyCDName);
                                                        
                                                if(im.getCDLocation(keyCDName).equalsIgnoreCase(keyCurrSS)){
                                                    
                                                    remvdCDName.addElement(keyCDName);
                                                
                                                        JSONObject CDDet = jsLocalCDs.getJSONObject(keyCDName);
                                                        String sclazz = CDDet.getString("CDClassName");
                                                        
                                                        //String OSType = System.getProperty("os.name");
                                                        
                                                       // if(OSType.indexOf("win")>=0){
                                                            
                                                       // } else if (OSType.indexOf("lin")>=0){
                                                            
                                                        //}
                                                        
                                                        String fileRootDir = System.getProperty("user.dir");
                                                        fileRootDir = fileRootDir.replace("\\", "/");
                                                        //String fileDir = fileRootDir+"/"+sclazz+".java";
                                                        Path path = FileSystems.getDefault().getPath(fileRootDir,sclazz+".class");
                                                        
                                                        System.out.println("CDLCM, deleting file if file exists:" +path);
                                                        
                                                        Files.deleteIfExists(path);
                                                        
                                                        if(sc.SchedulerHasCD(keyCDName)){
                                                            
                                                            Vector vec = cdlcimpl.removeSigChanObjModifySCIM(jsLocalCDs, keyCurrSS, keyCDName,"Active",im, sc);

                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);
                                                        
                                                        } else if (CDObjectsBuffer.CDObjBufferHas(keyCDName)){
                                                            
                                                            Vector vec = cdlcimpl.removeSigChanObjModifySCIM(jsLocalCDs, keyCurrSS, keyCDName,"Sleep",im, sc);
                                                            
                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);
                                                            
                                                        }
                                                        
                                                        JSONObject jsIntServ = SJServiceRegistry.obtainInternalRegistry();
                                                        
                                                        Enumeration keysServNames = jsIntServ.keys();
                                                        
                                                        while(keysServNames.hasMoreElements()){
                                                            
                                                            String servName = keysServNames.nextElement().toString();
                                                            
                                                            JSONObject jsIndivIntServ = jsIntServ.getJSONObject(servName);
                                                            
                                                            String assocCDName = jsIndivIntServ.getString("associatedCDName");
                                                            
                                                            if(assocCDName.equals(keyCDName)){
                                                                
                                                                //String servRole = jsIndivIntServ.getString("serviceRole");
                                                                
                                                                //if(servRole.equalsIgnoreCase("provider")){
                                                                    SOABuffer.removeAdvStatOfServName(servName);
                                                                //}
                                                                
                                                            }
                                                            
                                                        }
                                                        
                                                        //ClockDomainLifeCycleStatusRepository.ModifyCDStatus(keyCDName, "Killed");

                                                        CDObjectsBuffer.RemoveCDInstancesFromBuffer(keyCDName);
                                                    
                                                } 
                                                
                                                //if CD is in another SS, probably wont be followed through
                                                else {
                                                       
                                                         if(im.hasCDLocation(keyCDName)){
                                                        
                                                            String ssLoc = im.getCDLocation(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        } else {
                                                            
                                                            String ssLoc = CDLCBuffer.GetCDLocTempBuffer(keyCDName);
                                                        
                                                            if(CDinSSGroup.containsKey(ssLoc)){

                                                                JSONObject cdnamevec = (JSONObject)CDinSSGroup.get(ssLoc);

                                                                int hashsize = cdnamevec.length();

                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);
                                                                CDinSSGroup.put(ssLoc, cdnamevec);

                                                            } else {

                                                                JSONObject cdnamevec = new JSONObject();

                                                                int hashsize = cdnamevec.length();
                                                                cdnamevec.put(Integer.toString(hashsize),keyCDName);

                                                                CDinSSGroup.put(ssLoc, cdnamevec);
                                                            }
                                                            
                                                        }
                                                    
                                                }
                                                
                                                       if(CDinSSGroup.size()>0){
                                                
                                                            Enumeration keysSSname = CDinSSGroup.keys();

                                                            while(keysSSname.hasMoreElements()){

                                                                String destSS = keysSSname.nextElement().toString();

                                                                JSONObject jsCDNames = (JSONObject)CDinSSGroup.get(destSS);

                                                                String message = sosjcmg.GenerateKillCDsMessageOfJSON(destSS, jsCDNames);

                                                                String SSAddr = RegAllSSAddr.getSSAddrOfSSName(destSS);

                                                                sendReconfigMessage(SSAddr, message);

                                                            }
                                                
                                            } 
                                                        
                                                        //need to delete Service Description
                                                        
                                                        
                                                        //
                                                        
                                                        //JSONObject CDDet = jsLocalCDs.getJSONObject(keyCDName);
                                                        
                                                        
                                                        
                                                        //Files.d
                                                    //} else {
                                                        //System.out.println("Try to kill 'non-alive CD' of name: " +keyCDName);
                                                   // }//
                                                    
                                                //} else {
                                                  //  System.out.println("Try to kill 'non-existent CD' of name: " +keyCDName);
                                                //}
                                                
                                            }
                                            
                                            //remove cds mapping
                                            
                                            if(remvdCDName.size()>0){
                                                
                                                JSONObject jsCopyMap = new JSONObject();
                                                JSONObject jsCopiedMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
                                                
                                                Enumeration keysJSCopiedMap = jsCopiedMap.keys();
                                                
                                                while(keysJSCopiedMap.hasMoreElements()){
                                                    
                                                    String indivAllSSName = keysJSCopiedMap.nextElement().toString();
                                                    
                                                    if(indivAllSSName.equals(SJSSCDSignalChannelMap.getLocalSSName())){
                                                        
                                                        JSONObject jsAllCDsMap = jsCopiedMap.getJSONObject(indivAllSSName);
                                                        
                                                        JSONObject jsCopyAllCDs = new JSONObject();
                                                        
                                                        Enumeration keysJSAllCDsMap = jsAllCDsMap.keys();
                                                        
                                                        while (keysJSAllCDsMap.hasMoreElements()){
                                                            
                                                            String indivCDName = keysJSAllCDsMap.nextElement().toString();
                                                            
                                                            if(!remvdCDName.contains(indivCDName)){
                                                                jsCopyAllCDs.put(indivCDName, jsAllCDsMap.getJSONObject(indivCDName));
                                                            }
                                                            
                                                        }
                                                        
                                                        jsCopyMap.put(indivAllSSName, jsCopyAllCDs);
                                                        
                                                    } else {
                                                        jsCopyMap.put(indivAllSSName, jsCopiedMap.getJSONObject(indivAllSSName));
                                                    }
                                                    
                                                }
                                                
                                                //update both mapping reposit, avoid conflict with the SigChanCDManager
                                                
                                                SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(jsCopyMap);
                                                SJSSCDSignalChannelMap.UpdateAllSignalChannelPrevMap(jsCopyMap);
                                                
                                            }
                                            
                                                
                                            
                                                CDLCBuffer.ClearRequestKillCD();
                                            
                                        }
                                        
                                        //schedule migrated active cd and store hibernated cd to the appropriate data structure
                                        
                                        if((CDLCBuffer.GetStrongMigrationDoneFlag() || CDLCBuffer.GetWeakMigrationDoneFlag()) && !CDLCBuffer.getMigrationBusyFlag()){
                                            
                                            if(CDLCBuffer.GetStrongMigrationDoneFlag() && CDLCBuffer.GetWeakMigrationDoneFlag()){
                                                
                                                Hashtable AllMigratedCDStrong = CDObjectsBuffer.GetGroupSSCDObjStrongMig();
                                                Hashtable AllMigratedCDWeak = CDObjectsBuffer.GetGroupSSCDObjWeakMig();
                                                
                                                AllMigratedCDStrong.putAll(AllMigratedCDWeak);
                                                
                                                Hashtable AllMigratedCDSS = AllMigratedCDStrong;
                                                
                                                //for strong migrated CD
                                                
                                                Enumeration keysSSAllMigCD = AllMigratedCDSS.keys();

                                                while(keysSSAllMigCD.hasMoreElements()){
                                                    String ssname = keysSSAllMigCD.nextElement().toString();

                                                    Hashtable AllMigratedCD = (Hashtable)AllMigratedCDSS.get(ssname);

                                                    Enumeration keysAllMigCD = AllMigratedCD.keys();

                                                    while(keysAllMigCD.hasMoreElements()){
                                                        String cdName = keysAllMigCD.nextElement().toString();

                                                        ClockDomain migCD = (ClockDomain)AllMigratedCD.get(cdName);

                                                        System.out.println("migrated CDname: " +migCD.getName());

                                                    //after CD is obtaines, check the initial state from the original location

                                                        if(migCD.getState().equalsIgnoreCase("Active")){

                                                            System.out.println("CDname: " +migCD.getName()+ " activated");

                                                       //Vector vec =  cdlcimpl.AddSigChanObjAndInitModifySCIM(jsLocalCDs, keyCurrSS, migCD.getName(), migCD, AllMigratedCD,im, sc);

                                                            Vector vec = cdlcimpl.AddMigrateSigChanObjAndInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, migCD.getName(), migCD, AllMigratedCDStrong,"strong",im, sc);

                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);

                                                        } else if (migCD.getState().equalsIgnoreCase("Sleep")) {

                                                            System.out.println("migrated CDname: " +cdName+ " put to sleep");

                                                            Vector vec = cdlcimpl.AddMigrateSigChanObjNotInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, cdName, migCD, AllMigratedCD,"strong",im, sc);

                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);

                                                        }

                                                    }

                                                }
                                                
                                                //weak migration CD
                                                
                                                 keysSSAllMigCD = AllMigratedCDSS.keys();
                                                
                                                 while(keysSSAllMigCD.hasMoreElements()){
                                                    String ssname = keysSSAllMigCD.nextElement().toString();

                                                    Hashtable AllMigratedCD = (Hashtable)AllMigratedCDSS.get(ssname);

                                                    Enumeration keysAllMigCD = AllMigratedCD.keys();

                                                while(keysAllMigCD.hasMoreElements()){
                                                    String cdName = keysAllMigCD.nextElement().toString();

                                                    ClockDomain migCD = (ClockDomain)AllMigratedCD.get(cdName);

                                                    System.out.println("migrated CDname: " +migCD.getName());

                                                    //after CD is obtaines, check the initial state from the original location

                                                    if(migCD.getState().equalsIgnoreCase("Active")){

                                                        System.out.println("CDname: " +migCD.getName()+ " activated");

                                                       Vector vec =  cdlcimpl.AddMigrateSigChanObjAndInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, migCD.getName(), migCD, AllMigratedCD,"weak",im, sc);

                                                       sc = (Scheduler)vec.get(1);
                                                       im = (InterfaceManager) vec.get(0);

                                                    } else if (migCD.getState().equalsIgnoreCase("Sleep")) {

                                                        System.out.println("migrated CDname: " +cdName+ " put to sleep");

                                                        Vector vec = cdlcimpl.AddMigrateSigChanObjNotInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, cdName, migCD, AllMigratedCD,"weak" ,im, sc);

                                                       sc = (Scheduler)vec.get(1);
                                                       im = (InterfaceManager) vec.get(0);

                                                    }

                                                }


                                                }
                                                
                                            } else
                                            
                                              if(CDLCBuffer.GetStrongMigrationDoneFlag()){
                                            
                                                System.out.println("New migrated CD exists");

                                                Hashtable AllMigratedCDSS = CDObjectsBuffer.GetGroupSSCDObjStrongMig();

                                                Enumeration keysSSAllMigCD = AllMigratedCDSS.keys();

                                                while(keysSSAllMigCD.hasMoreElements()){
                                                    String ssname = keysSSAllMigCD.nextElement().toString();

                                                    Hashtable AllMigratedCD = (Hashtable)AllMigratedCDSS.get(ssname);

                                                    Enumeration keysAllMigCD = AllMigratedCD.keys();

                                                    while(keysAllMigCD.hasMoreElements()){
                                                        String cdName = keysAllMigCD.nextElement().toString();

                                                        ClockDomain migCD = (ClockDomain)AllMigratedCD.get(cdName);

                                                        System.out.println("migrated CDname: " +migCD.getName());

                                                    //after CD is obtaines, check the initial state from the original location

                                                        if(migCD.getState().equalsIgnoreCase("Active")){

                                                            System.out.println("CDname: " +migCD.getName()+ " activated");

                                                       //Vector vec =  cdlcimpl.AddSigChanObjAndInitModifySCIM(jsLocalCDs, keyCurrSS, migCD.getName(), migCD, AllMigratedCD,im, sc);

                                                            Vector vec = cdlcimpl.AddMigrateSigChanObjAndInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, migCD.getName(), migCD, AllMigratedCD,"strong",im, sc);

                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);

                                                        } else if (migCD.getState().equalsIgnoreCase("Sleep")) {

                                                            System.out.println("migrated CDname: " +cdName+ " put to sleep");

                                                            Vector vec = cdlcimpl.AddMigrateSigChanObjNotInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, cdName, migCD, AllMigratedCD,"strong",im, sc);

                                                            sc = (Scheduler)vec.get(1);
                                                            im = (InterfaceManager) vec.get(0);

                                                        }

                                                   }

                                                }

                                                //Hashtable AllMigratedCD = CDObjectsBuffer.TakeAllCDObjFromTempStrongMigBuffer();

                                                CDLCBuffer.ResetStrongMigrationDoneFlag();
                                            } else

                                            // weak mig

                                            if(CDLCBuffer.GetWeakMigrationDoneFlag()){

                                                System.out.println("New migrated CD exists");

                                                Hashtable AllMigratedCDSS = CDObjectsBuffer.GetGroupSSCDObjWeakMig();

                                                Enumeration keysAllMigCDSS = AllMigratedCDSS.keys();

                                                while(keysAllMigCDSS.hasMoreElements()){
                                                    String ssname = keysAllMigCDSS.nextElement().toString();

                                                    Hashtable AllMigratedCD = (Hashtable)AllMigratedCDSS.get(ssname);

                                                    Enumeration keysAllMigCD = AllMigratedCD.keys();

                                                while(keysAllMigCD.hasMoreElements()){
                                                    String cdName = keysAllMigCD.nextElement().toString();

                                                    ClockDomain migCD = (ClockDomain)AllMigratedCD.get(cdName);

                                                    System.out.println("migrated CDname: " +migCD.getName());

                                                    //after CD is obtaines, check the initial state from the original location

                                                    if(migCD.getState().equalsIgnoreCase("Active")){

                                                        System.out.println("CDname: " +migCD.getName()+ " activated");

                                                       Vector vec =  cdlcimpl.AddMigrateSigChanObjAndInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, migCD.getName(), migCD, AllMigratedCD,"weak",im, sc);

                                                       sc = (Scheduler)vec.get(1);
                                                       im = (InterfaceManager) vec.get(0);

                                                    } else if (migCD.getState().equalsIgnoreCase("Sleep")) {

                                                        System.out.println("migrated CDname: " +cdName+ " put to sleep");

                                                        Vector vec = cdlcimpl.AddMigrateSigChanObjNotInitModifySCIM(jsLocalCDs, ssname,keyCurrSS, cdName, migCD, AllMigratedCD,"weak" ,im, sc);

                                                        sc = (Scheduler)vec.get(1);
                                                        im = (InterfaceManager) vec.get(0);
                                                        
                                                    }

                                                }


                                                }

                                                //Hashtable AllMigratedCD = CDObjectsBuffer.TakeAllCDObjFromTempWeakMigBuffer();


                                                CDLCBuffer.ResetWeakMigrationDoneFlag();
                                            }
                                            
                                        }
                                        
                                        // strong mig
                                        
                                        
                                        //
                                        
                                        if(!CDLCBuffer.ISCDLCMigrationThreadBusy()){
                                            
                                            if(!CDLCBuffer.IsRequestMigrateEmpty()){
                                            
                                                Vector vec = new Vector();
                                             
                                                Hashtable ReqMigrate = CDLCBuffer.GetRequestMigrate();
                                             
                                                //System.out.println("CDLCM, Req Migrate: " +ReqMigrate);
                                                
                                                Enumeration keysReqMig = ReqMigrate.keys();
                                             
                                                ClockDomainLifeCycleMigrationImpl cdlcmigimpl = new ClockDomainLifeCycleMigrationImpl();
                                             
                                                while(keysReqMig.hasMoreElements()){
                                                  String DestSS = keysReqMig.nextElement().toString();
                                                 
                                                  Hashtable hash = (Hashtable) ReqMigrate.get(DestSS);
                                                 
                                                   Enumeration keysHash = hash.keys();
                                                 
                                                   while(keysHash.hasMoreElements()){
                                                     
                                                     String migType = keysHash.nextElement().toString();
                                                     
                                                     //alternative physical address source --> Link??
                                                     if(RegAllSSAddr.getSSAddrOfSSName(DestSS).equalsIgnoreCase("0.0.0.0")){
                                                         
                                                         String Addr = SJSSCDSignalChannelMap.getRemoteGenericInterfaceAddr(DestSS);
                                                         
                                                             CDLCBuffer.SetCDLCMigrationFlagBusy();
                                                             
                                                             if(Addr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr()) || Addr.equalsIgnoreCase("127.0.0.1") || Addr.equalsIgnoreCase("localhost")){
                                                                
                                                                 
                                                                 vec = cdlcmigimpl.InitHandshakeAndTransfer(jsLocalCDs,DestSS, "224.0.0.100" , migType,im);
                                                            } else {
                                                               vec = cdlcmigimpl.InitHandshakeAndTransfer(jsLocalCDs, DestSS, Addr , migType,im);
                                                            }
                                                             
                                                         
                                                         
                                                     } else {
                                                         
                                                         //get from serv registry
                                                            //String Addr = SJServiceRegistry.getAddrOfSS(DestSS);
                                                         String Addr = RegAllSSAddr.getSSAddrOfSSName(DestSS);
                                                         
                                                     
                                                            CDLCBuffer.SetCDLCMigrationFlagBusy();
                                                            
                                                            if(Addr.equalsIgnoreCase(SJSSCDSignalChannelMap.GetLocalSSAddr()) || Addr.equalsIgnoreCase("127.0.0.1") || Addr.equalsIgnoreCase("localhost")){
                                                                vec = cdlcmigimpl.InitHandshakeAndTransfer(jsLocalCDs, DestSS, "224.0.0.100" , migType,im);
                                                            } else {
                                                                vec = cdlcmigimpl.InitHandshakeAndTransfer(jsLocalCDs, DestSS, Addr , migType,im);
                                                            }
                                                            
                                                     }
                                                     
                                                 }
                                                
                                             }
                                             
                                             //sc = (Scheduler)vec.get(1);
                                             //im = (InterfaceManager) vec.get(0);
                                             
                                         }
                                        }
                                        
                                        
                                         
                } catch (JSONException ex) {
                    ex.printStackTrace();
                } catch (Exception ex){
                    ex.printStackTrace();


                }
    
                SOABuffer.SetNotifyChangedCDStat(true);
        
                vecSCIM.addElement(im);
                vecSCIM.addElement(sc);
                IMBuffer.SaveInterfaceManagerConfig(im);
                return vecSCIM;
        }
    
    
    
    
        /*
        private boolean CheckChannels(JSONObject jsLocalCDs,String CDName){
            
            boolean stat = false;
            
            JSONObject jsSigsChans;
        try {
                        jsSigsChans = jsLocalCDs.getJSONObject(CDName);

                        Enumeration keysSigsChans = jsSigsChans.keys();
                                     
                                while (keysSigsChans.hasMoreElements()){
                                    
                                    String tagSigsChans = keysSigsChans.nextElement().toString();
            
                                    if (tagSigsChans.equalsIgnoreCase("SChannels")){

                                        JSONObject jsSigsChansInd = jsSigsChans.getJSONObject(tagSigsChans);
                                        
                                            Enumeration keysSChansInOuts = jsSigsChansInd.keys();
                                        
                                            while (keysSChansInOuts.hasMoreElements()){
                                            
                                                String keyInOut = keysSChansInOuts.nextElement().toString();
                                            
                                                if (keyInOut.equalsIgnoreCase("inputs")){
                                                
                                                    JSONObject jsSChansInputs = jsSigsChansInd.getJSONObject("inputs");
                                                    
                                                    if(!jsSChansInputs.isEmpty()){
                                                        
                                                        Enumeration keysSigsInputsName = jsSChansInputs.keys();
                                        
                                                        while (keysSigsInputsName.hasMoreElements()){
                                            
                                                            String SChansInputsName = keysSigsInputsName.nextElement().toString();
                                                            
                                                            String cname = SChansInputsName+"_in";
                                                            
                                                            ClockDomain cdInChan = CDObjectsBuffer.GetSCCDInstancesFromMap(CDName);
                                                            
                                                            input_Channel inChanObj = new input_Channel();
                                                            
                                                            try {
                                                                Field f = cdInChan.getClass().getField(cname);
                                                                
                                                                inChanObj = (input_Channel)f.get(cdInChan);
                                                                
                                                                //input_Channel inChanObj = (input_Channel) SJSSCDSignalChannelMap.getInOutChannelObject(LocalSSName, CDName, "SChannel", "input", SChansInputsName);
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        
                                                            
                                                                   if(inChanObj.get_r_r()!=0 || inChanObj.get_r_s()!=0){
                                                                       
                                                                       stat=true;
                                                                       
                                                                   }
                                                            
                                                        }
                                                        
                                                    }
                                                    
                                                }
                                                
                                                if(keyInOut.equalsIgnoreCase("outputs")){
                                                    JSONObject jsSChansOutputs = jsSigsChansInd.getJSONObject("outputs");
                                                    
                                                    if(!jsSChansOutputs.isEmpty()){
                                                        
                                                        Enumeration keysChansOutputsName = jsSChansOutputs.keys();
                                        
                                                        while (keysChansOutputsName.hasMoreElements()){
                                            
                                                            String SChansOutputsName = keysChansOutputsName.nextElement().toString();
                                                            
                                                            String pname = SChansOutputsName+"_o";
                                                            
                                                            ClockDomain cdOutChan = CDObjectsBuffer.GetSCCDInstancesFromMap(CDName);
                                                            
                                                            output_Channel outChanObj = new output_Channel();
                                                            
                                                            
                                                            try {
                                                                Field f = cdOutChan.getClass().getField(pname);
                                                                
                                                                outChanObj = (output_Channel)f.get(cdOutChan);
                                                                
                                                                
                                                                
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                            } 
                                                            
                                                            if(outChanObj.get_w_r()!=0 || outChanObj.get_w_s()!=0){
                                                                stat=true;
                                                            }
                                                            
//output_Channel outChanObj = (output_Channel)SJSSCDSignalChannelMap.getInOutChannelObject(LocalSSName, CDName, "SChannel", "output", SChansOutputsName);
                                                            
                                                        }
                                                        
                                                        
                                                        
                                                    }
                                                    
                                                }
                                            }
                                        
                                    }
                                    
                                }
            
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
           
            return stat;
            
        }
        */
    
    private void sendReconfigMessage(String ipAddr, String message){
        try {
            byte[] msg = new byte[65508];
            InetAddress ipAddress = InetAddress.getByName(ipAddr);
            int infoDebug=1;
            //JSONObject js = new JSONObject(message);
              
               ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
               ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteStream));
              //out.writeObject(SJServiceRegistry.obtainInternalRegistry().toString());
               out.writeObject(message);
               //out.writeObject(SJServiceRegistry.AdvertiseNodeServices("HelloMessage").toString()); //put service description to be sent to remote devices
               out.flush();
                msg = byteStream.toByteArray();
                out.close(); 
                MulticastSocket s = new MulticastSocket(212);
                DatagramPacket hi = new DatagramPacket(msg, msg.length, ipAddress, 212);
                if (infoDebug ==1 )System.out.println("Sending control message:" +message+ "to: " +ipAddress );
                s.send(hi);
                if (infoDebug ==1 ) System.out.println("data has been sent!");
                                          
                //SJServiceRegistry.AcknowledgeHelloMessageSent(true);
                 // if (infoDebug ==1 ) System.out.println("Status acknowledge in sender:" +SJServiceRegistry.getAcknowledgeHelloMessageSent());
                                         // SJServiceRegistry.RecordAdvertisementTimeStamp();
                s.close();
        } catch (UnknownHostException hex) {
            
            System.err.println("ControlMessage, problem IOException: " +hex.getMessage());
        } catch (Exception e){
            System.err.println("ControlMessage, problem Exception: " +e.getMessage());
        }
    }
    
}
