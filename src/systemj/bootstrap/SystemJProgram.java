package systemj.bootstrap;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import systemj.common.CDLCBuffer;
import systemj.common.CDObjectsBuffer;
import systemj.common.CyclicScheduler;
import systemj.common.IMBuffer;
import systemj.common.Interconnection;
import systemj.common.InterfaceManager;
import systemj.common.RegAllSSAddr;
import systemj.common.SJSSCDSignalChannelMap;
import systemj.common.SJServiceRegistry;
import systemj.common.SOAFacility.ClockDomainLifeCycleManager;
import systemj.common.SOAFacility.ClockDomainLifeCycleReconfigChanImpl2;
import systemj.common.SOAFacility.ClockDomainLifeCycleSignalImpl;
import systemj.common.SOAFacility.LinkCreationSenderHSImpl;
import systemj.common.SOAFacility.Support.SOABuffer;
import systemj.common.SOAFacility.TCPIPLinkRegistry;
import systemj.common.SchedulersBuffer;

import systemj.interfaces.GenericInterface;
import systemj.interfaces.Scheduler;

/**
 * SystemJ program
 * Must be compatible with CLDC 1.1
 * 
 * @author hpar081, udayanto dwi atmojo
 *
 */
public class SystemJProgram {
	private String name;
	private InterfaceManager im;
	public void setSubSystemName(String n){name = n;}
	public String getSubSystemName(){ return name ;}
	//public static boolean jpfenabled = false;
	
	public void setInterfaceManager(InterfaceManager iim){ 	im = iim; }
	public InterfaceManager getInterfaceManager(){ return im ;}
	public void resolveLocalInterface() { im.setLocalInterface(name); }
	
	private Vector scs = new Vector();
	public void addScheduler(Scheduler sc){	scs.addElement(sc);	}
        //Uday added method 27 Jan 2015 -- assuming only one scheduler used
        public Scheduler getScheduler (){ return (Scheduler)scs.get(0);}
        
        public void updateFirstScheduler(Scheduler sc){ 
            
            scs.remove(0);
            scs.add(0, sc);
            
            //System.out.println("updateFirstScheduler, amount of CD: " +sc.getClockDomainAmount());
            
        }
        
	public void init(){
            
		im.init();
		for(int i=0;i<scs.size();i++)
			((Scheduler)scs.elementAt(i)).setInterfaceManager(im);
	}
        
        public void initIM(){
            im.initIM();
            for(int i=0;i<scs.size();i++)
			((Scheduler)scs.elementAt(i)).setInterfaceManager(im);
        }
        
        public void suspend(){
            im.terminateInterface();
        }
       
	public void startProgram(){
            
                //store 'main' scheduler in the buffer
                
                //
            
		
                //SignalChannelClockDomainManager sigrec = new SignalChannelClockDomainManager();
                
                ClockDomainLifeCycleManager cdlcm = new ClockDomainLifeCycleManager();
                
                SchedulersBuffer.SaveSchedulers(scs);
                
                if(scs.size()==0){
                    CyclicScheduler cs = new CyclicScheduler();
                    addScheduler(cs);
                    setSubSystemName(SOABuffer.GetEmptySSName());
                    
                    JSONObject js = new JSONObject();
                    try {
                        js.put(getSubSystemName(), new JSONObject());
                        SJServiceRegistry.UpdateAllInternalRegistry(new JSONObject());
                        SJSSCDSignalChannelMap.UpdateAllCurrSignalChannelMapping(js);
                        SJSSCDSignalChannelMap.UpdateAllSignalChannelPrevMap(js);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    
                }
                
                System.out.println("Starting program");
                ClockDomainLifeCycleSignalImpl cdlcmsi = new ClockDomainLifeCycleSignalImpl();
                ClockDomainLifeCycleReconfigChanImpl2 cdlcrci = new ClockDomainLifeCycleReconfigChanImpl2();
                int shouldShutdown = 0;
                
		while(true){
                   
                  //if (SJSSSignalChannelMap.IsLocalSignalChangesLocEmpty() || SJSSSignalChannelMap.IsLocalChannelChangesLocEmpty()){
                   //if (!SJSSSignalChannelMap.getLocalCDChangedFlag()){   
                    
                    //will always assume one scheduler, so run one cyclic scheduler only
                    
                      for(int z=0;z<scs.size();z++){
                          
                          //if(((Scheduler)scs.elementAt(i)).getClockDomainAmount()>0){
                              
                                ((Scheduler)scs.elementAt(z)).run();
                                
                                //PerformReconfiguration();
                                
                                if(!SOABuffer.GetReceivedNotifyChangedCDStat()||!CDLCBuffer.IsMigratingCDNameBufferEmpty()||!CDLCBuffer.IsDevelCreateCDEmpty()||!CDLCBuffer.IsOldPartnerChanReconfigBufferEmpty()||!CDLCBuffer.IsChanReconfigBufferEmpty()||!CDLCBuffer.IsOldInvokeServPartnerChanReconfigBufferEmpty()||!CDLCBuffer.IsInvokeServChanReconfigBufferEmpty()||!CDLCBuffer.IsInvokeServ2ChanReconfigBufferEmpty()||!CDLCBuffer.IsCDSSLocTempBufferEmpty()||!CDLCBuffer.IsReconfigInChanConfigIMBufferEmpty()){
                                    
                                    if(!CDLCBuffer.IsReconfigInChanConfigIMBufferEmpty()){
                                            Vector recInvChan = CDLCBuffer.GetReconfigInChanConfigIMBuffer();

                                            for(int j=0;j<recInvChan.size();j++){

                                                Hashtable hash = (Hashtable)recInvChan.get(j);

                                                InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                                Scheduler scMod = getScheduler();

                                                String inchanName = hash.get("InchanName").toString();
                                                String inchanCDName = hash.get("InchanCDName").toString();
                                                String partnerCDName = hash.get("PartnerCDName").toString();
                                                String partnerChanCDName = hash.get("PartnerChanCDName").toString();
                                                String SSName = hash.get("SSName").toString();

                                                Vector vec = cdlcmsi.ReconfigInvServInChannel(inchanName, inchanCDName, partnerCDName, partnerChanCDName, SSName, scMod, imMod);

                                                InterfaceManager newIMMod = (InterfaceManager)vec.get(0);
                                                Scheduler newSCMod = (Scheduler)vec.get(1);

                                                updateFirstScheduler(newSCMod);
                                                IMBuffer.SaveInterfaceManagerConfig(newIMMod);


                                            }

                                        }

                                        if(!CDLCBuffer.IsCDSSLocTempBufferEmpty()){
                                            Hashtable CDSSLoc = CDLCBuffer.GetAllCDLocTempBuffer();

                                            InterfaceManager IMMod = IMBuffer.getInterfaceManagerConfig();

                                            Enumeration keysCDSSLoc = CDSSLoc.keys();

                                            while(keysCDSSLoc.hasMoreElements()){

                                                String CDNameToAdd = keysCDSSLoc.nextElement().toString();

                                                String SSNameLocOfCD = (String)CDSSLoc.get(CDNameToAdd);

                                                IMMod.addCDLocation(SSNameLocOfCD, CDNameToAdd);

                                            }
                                            CDLCBuffer.ClearCDLocTempBuffer();

                                            IMBuffer.SaveInterfaceManagerConfig(IMMod);

                                        }

                                        //service invocation via channel

                                        if(!CDLCBuffer.IsInvokeServ2ChanReconfigBufferEmpty()){

                                            Scheduler scMod = getScheduler();

                                            InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                            Hashtable vec = CDLCBuffer.GetReconfigInvokeServ2ChanBuffer();

                                            try {

                                                Enumeration keysChanInvRec = vec.keys();
                                                String locSSName = SJSSCDSignalChannelMap.getLocalSSName();

                                                JSONObject LocCDConfMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping().getJSONObject(locSSName);

                                                //for(int j=0;j<vec.size();j++){

                                                while (keysChanInvRec.hasMoreElements()){

                                                    String keyChanInv = keysChanInvRec.nextElement().toString();

                                                    Hashtable hashReconfigReq = (Hashtable) vec.get(keyChanInv);

                                                    //boolean IsLocal = Boolean.parseBoolean(hashReconfigReq.get("IsLocal").toString());

                                                    String ChanName = hashReconfigReq.get("ChanName").toString();
                                                    String ChanCDName = hashReconfigReq.get("CDName").toString();

                                                    String PartnerChanName = hashReconfigReq.get("PartnerChanName").toString();
                                                    String PartnerCDName = hashReconfigReq.get("PartnerCDName").toString();

                                                    boolean IsLocal = false;

                                                    if(LocCDConfMap.has(PartnerCDName)){
                                                        IsLocal = true;
                                                    }

                                                    if(IsLocal){

                                                        Vector vecIMSC = cdlcmsi.ReconfigInvServLocalChannel(ChanCDName, ChanName, PartnerCDName, PartnerChanName, scMod, imMod);

                                                        InterfaceManager newIMMod = (InterfaceManager) vecIMSC.get(0);

                                                        Scheduler newSCMod = (Scheduler)vecIMSC.get(1);

                                                        updateFirstScheduler(newSCMod);

                                                        IMBuffer.SaveInterfaceManagerConfig(newIMMod);

                                                    } else {

                                                        //String DestSS = hashReconfigReq.get("DestSS").toString();
                                                        //String DestAddr = hashReconfigReq.get("DestAddr").toString();

                                                        Vector vecIMSC = cdlcmsi.ReconfigInvServChannel(ChanCDName, ChanName, PartnerCDName, PartnerChanName, scMod, imMod);
                                                        InterfaceManager newIMMod = (InterfaceManager) vecIMSC.get(0);

                                                        Scheduler newSCMod = (Scheduler)vecIMSC.get(1);

                                                        updateFirstScheduler(newSCMod);

                                                        IMBuffer.SaveInterfaceManagerConfig(newIMMod);


                                            //request first



                                            //if local, then set to 127.0.0.1


                                            //LinkCreationSenderHSImpl lcshs = new LinkCreationSenderHSImpl();

                                            //InterfaceManager imA = lcshs.ExecuteLinkCreationHS(SSName, destAddr, IM);
                                            //setInterfaceManager(imA);
                                            // need to



                                                    }

                                                }

                                                CDLCBuffer.ClearInvokeServ2ChanReconfigBuffer();

                                            } catch (Exception ex) {
                                               ex.printStackTrace();
                                            }



                                        }

                                        if(!CDLCBuffer.IsInvokeServChanReconfigBufferEmpty()){

                                            Vector vec = CDLCBuffer.GetReconfigInvokeServChanBuffer();

                                            InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                            Scheduler scMod = getScheduler();

                                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();

                                            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();

                                            for(int r=0;r<vec.size();r++){

                                                Hashtable hash = (Hashtable)vec.get(r);

                                                String CDName = hash.get("CDName").toString();
                                                String ChanName = hash.get("ChanName").toString();
                                                String PartnerCDName = hash.get("PartnerCDName").toString();
                                                String PartnerChanName = hash.get("PartnerChanName").toString();
                                                String ChanDir = hash.get("ChanDir").toString();

                                                try {
                                                    JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                    Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                    imMod = (InterfaceManager) IMSCHED.get(0);
                                                    //scMod = (Scheduler) IMSCHED.get(1);

                                                    //updateFirstScheduler(scMod);
                                                    IMBuffer.SaveInterfaceManagerConfig(imMod);

                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }

                                            }

                                            CDLCBuffer.ClearInvokeServChanReconfigBuffer();                                    
                                        }

                                        if(!CDLCBuffer.IsOldInvokeServPartnerChanReconfigBufferEmpty()){

                                            Vector vec = CDLCBuffer.GetOldInvokeServPartnerReconfigChanBuffer();

                                            InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                            Scheduler scMod = getScheduler();

                                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();

                                            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();

                                            for(int r=0;r<vec.size();r++){

                                                Hashtable hash = (Hashtable)vec.get(r);

                                                String PartnerCDName = hash.get("PartnerCDName").toString();
                                                String PartnerChanName = hash.get("PartnerChanName").toString();
                                                String ChanDir = hash.get("PartnerChanDir").toString();

                                                try {
                                                    JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                    Vector IMSCHED = cdlcmsi.ReconfigPartnerChannel(jsLocalCDs, keyCurrSS, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                    imMod = (InterfaceManager) IMSCHED.get(0);
                                                    //scMod = (Scheduler) IMSCHED.get(1);

                                                    //updateFirstScheduler(scMod);
                                                    IMBuffer.SaveInterfaceManagerConfig(imMod);

                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }

                                            }

                                            CDLCBuffer.ClearOldInvokeServPartnerChanReconfigBuffer();

                                        }

                                        if(!CDLCBuffer.IsChanReconfigBufferEmpty()){

                                            Vector vec = CDLCBuffer.GetReconfigChanBuffer();

                                            InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                            Scheduler scMod = getScheduler();

                                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();

                                            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();

                                            for(int r=0;r<vec.size();r++){

                                                Hashtable hash = (Hashtable)vec.get(r);

                                                String CDName = hash.get("CDName").toString();
                                                String ChanName = hash.get("ChanName").toString();
                                                String PartnerCDName = hash.get("PartnerCDName").toString();
                                                String PartnerChanName = hash.get("PartnerChanName").toString();
                                                String ChanDir = hash.get("ChanDir").toString();

                                                if(hash.containsKey("destSS")){

                                                    String destSS = hash.get("destSS").toString();
                                                    String destSSAddr = hash.get("destSSAddr").toString();



                                                    //check if link is here, create if neeeded

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

                                                                                                    if(jsLocalPortPair.getString(PortNumStr).equals(destSS)){

                                                                                                        RemotePortNum = PortNumStr;
                                                                                                        remotePortExist = true;

                                                                                                    }

                                                                                                }

                                                                                            } catch (JSONException jex){
                                                                                                jex.printStackTrace();
                                                                                            }

                                                                                            if(!(LocalPortExist && remotePortExist)){
                                                                                                //need to negotiate both link establishment and which physical interface

                                                                                                String SSName = destSS;

                                                                                                            //InterfaceManager imA = getInterfaceManager();
                                                                                                //System.out.println("SJProgram, Trying to contact SS with name: " +SSName);

                                                                                                        Interconnection ic = imMod.getInterconnection();

                                                                                                        Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                                                                                        if(availRemoteLink.size()==0){

                                                                                                            //String destAddr = SJServiceRegistry.getAddrOfSS(SSName);

                                                                                                            LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();

                                                                                                            int q;

                                                                                                            for(q=0;q<5;q++){

                                                                                                                String linkCreationResp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destSSAddr);

                                                                                                                    if(linkCreationResp.equals("OK")){

                                                                                                                        if(LocalPortExist){

                                                                                                                            imMod = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destSSAddr, imMod, LocalPortNum);

                                                                                                                            break;

                                                                                                                        } else {
                                                                                                                             imMod = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destSSAddr, imMod);

                                                                                                                             break;
                                                                                                                        }

                                                                                                                    } 

                                                                                                            }




                                                                                                            // update SS to Contact

                                                                                                            //TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                                                                            imMod.setInterconnection(ic);
                                                                                                        }


                                                                                            }

                                                                                            imMod.addCDLocation(destSS, PartnerCDName);




                                                    try {
                                                       JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                       Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                       imMod = (InterfaceManager) IMSCHED.get(0);

                                                       scMod = (Scheduler) IMSCHED.get(1);

                                                       updateFirstScheduler(scMod);
                                                       IMBuffer.SaveInterfaceManagerConfig(imMod);


                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                } else {

                                                    try {
                                                        JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                        Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                        imMod = (InterfaceManager) IMSCHED.get(0);

                                                        scMod = (Scheduler) IMSCHED.get(1);

                                                        updateFirstScheduler(scMod);
                                                        IMBuffer.SaveInterfaceManagerConfig(imMod);

                                                    } catch (JSONException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                }

                                            }

                                            CDLCBuffer.ClearChanReconfigBuffer();

                                        }

                                        if(!CDLCBuffer.IsOldPartnerChanReconfigBufferEmpty()){

                                            Vector vec = CDLCBuffer.GetOldPartnerReconfigChanBuffer();

                                            InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();

                                            Scheduler scMod = getScheduler();

                                            JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();

                                            String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();

                                            for(int r=0;r<vec.size();r++){

                                                Hashtable hash = (Hashtable)vec.get(r);

                                                String PartnerCDName = hash.get("PartnerCDName").toString();
                                                String PartnerChanName = hash.get("PartnerChanName").toString();
                                                String ChanDir = hash.get("PartnerChanDir").toString();

                                                try {
                                                    JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                    Vector IMSCHED = cdlcmsi.ReconfigPartnerChannel(jsLocalCDs, keyCurrSS, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                    imMod = (InterfaceManager) IMSCHED.get(0);
                                                    //scMod = (Scheduler) IMSCHED.get(1);

                                                    //updateFirstScheduler(scMod);
                                                    IMBuffer.SaveInterfaceManagerConfig(imMod);

                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }

                                            }

                                            CDLCBuffer.ClearOldPartnerChanReconfigBuffer();


                                        }

                                        if(!CDLCBuffer.IsDevelCreateCDEmpty()){

                                            Vector vec = CDLCBuffer.getDevelCreateCD();

                                            for(int y=0;y<vec.size();y++){
                                                Hashtable hash = (Hashtable)vec.get(y);

                                                String CDName = hash.get("CDName").toString();
                                                JSONObject jsCDMap = (JSONObject)hash.get("CDMap");

                                                JSONObject jsCDServDesc = (JSONObject)hash.get("CDServDesc");

                                                  CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
                                                  CDLCBuffer.AddTempSigChanMapCD(jsCDMap);
                                                  CDLCBuffer.putUpdateServiceDescription(jsCDServDesc);

                                            }
                                            CDLCBuffer.ClearDevelCreateCD();

                                        }

                              if(!CDLCBuffer.IsMigratingCDNameBufferEmpty()){

                                  Scheduler sc = getScheduler();

                                  Vector cdnames = CDLCBuffer.GetMigratingCDNameFromBuffer();
                                  for(int u=0;u<cdnames.size();u++){
                                      String cdname = (String)cdnames.get(u);

                                      if(sc.SchedulerHasCD(cdname)){

                                          ClockDomain cdins = sc.getClockDomain(cdname);

                                          CDLCBuffer.AddMigratingCDInst(cdins);
                                          //sc.removeClockDomain(cdins);
                                          sc.removeClockDomain(cdname);
                                      } else if (CDObjectsBuffer.CDObjBufferHas(cdname)){
                                          CDLCBuffer.AddMigratingCDInst(CDObjectsBuffer.GetCDInstancesFromBuffer(cdname));
                                          CDObjectsBuffer.RemoveCDInstancesFromBuffer(cdname);
                                      }

                                  }

                              }
                              
                              if(!SOABuffer.GetReceivedNotifyChangedCDStat()){
                                  
                                  Scheduler scMod = getScheduler();
                                  InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                  
                                  Vector IMSCHED = cdlcmsi.ResetSubscribedInChan(scMod, imMod);

                                  imMod = (InterfaceManager) IMSCHED.get(0);

                                  scMod = (Scheduler)IMSCHED.get(1);

                                  updateFirstScheduler(scMod);
                                  
                                  IMBuffer.SaveInterfaceManagerConfig(imMod);
                                  
                                  SOABuffer.SetReceivedNotifyChangedCDStat(false);
                                  
                              }
                                    
                            }
                                
                                
                                
                      
                      if(SJSSCDSignalChannelMap.GetCheckLinkToRemove()|| SJSSCDSignalChannelMap.GetReqCreateLink() || !CDLCBuffer.IsRequestCreateCDEmpty() || !CDLCBuffer.IsRequestKillCDEmpty() || !CDLCBuffer.IsRequestHibernateCDEmpty() || !CDLCBuffer.IsRequestWakeUpCDEmpty() || !CDLCBuffer.IsRequestMigrateEmpty() || CDLCBuffer.GetStrongMigrationDoneFlag()|| CDLCBuffer.GetWeakMigrationDoneFlag() || CDLCBuffer.GetMigGoAheadModPartner()){
                          
                          //here 23
                          
                          
                          //here 23
                          
                          //System.gc();
                          
                          //System.out.println("Time taken for gc: " +getGarbageCollectionTime()+ " milliseconds" );
                          
                          Scheduler sc = (Scheduler)getScheduler();
                      //InterfaceManager IM = getInterfaceManager();
                          InterfaceManager IM = IMBuffer.getInterfaceManagerConfig();
                          
                          //System.out.println("SystemJProgram.java: Start reconfiguration");
                          
                          //long startTime = System.currentTimeMillis();
                          
                          Vector vec = cdlcm.run(sc, IM);
                          
                         
                          IM = (InterfaceManager)vec.get(0);
                          sc = (Scheduler)vec.get(1);
                          
                          
                          if(SJSSCDSignalChannelMap.GetReqCreateLink()){
                          
                          //if request to create link is present
                                Vector SSsToContact =  TCPIPLinkRegistry.GetAllSSToContact();
                                
                                boolean contactSSAgain = false;

                                // find local SS port
                                
                                JSONObject jsLocalPortPair = TCPIPLinkRegistry.GetSSAndPortPair();
                                
                                String LocalPortNum="";
                                boolean LocalPortExist = false;
                                
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
                                
                                //end find local SS port
                                
                                for(int k=0;k<SSsToContact.size();k++){
                                    
                                    String SSName = (String)SSsToContact.get(k);

                                                //InterfaceManager imA = getInterfaceManager();
                                    //System.out.println("SJProgram, Trying to contact SS with name: " +SSName);
                                        
                                            Interconnection ic = IM.getInterconnection();
                          
                                            Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                            if(availRemoteLink.size()==0){

                                                //String destAddr = SJServiceRegistry.getAddrOfSS(SSName);
                          
                                                String destAddr = RegAllSSAddr.getSSAddrOfSSName(SSName);
                                                LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();
                                                
                                                int r;
                                                
                                                for(r=0;r<5;r++){
                                                    
                                                    String resp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destAddr);
                                                    
                                                        if(resp.equals("OK")){
                                                        
                                                            if(LocalPortExist){
                                                                
                                                                IM = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM, LocalPortNum);
                                                                SSsToContact.remove(k);
                                                                break;
                                                                
                                                            } else {
                                                                 IM = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM);
                                                                 SSsToContact.remove(k);
                                                                 break;
                                                            }
                                                            
                                                        } 
                                                        
                                                }
                                                
                                                if(r>=5){
                                                    contactSSAgain=true;
                                                }
                                                
                                                
                                                // update SS to Contact
                                                
                                                TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                IM.setInterconnection(ic);
                                            }

                                    //request first

                                    
                                    
                                    //if local, then set to 127.0.0.1
                                    

                                    //LinkCreationSenderHSImpl lcshs = new LinkCreationSenderHSImpl();

                                    //InterfaceManager imA = lcshs.ExecuteLinkCreationHS(SSName, destAddr, IM);
                                    //setInterfaceManager(imA);
                                    // need to
                                     
                                }

                                
                                if(!contactSSAgain){ //next end of exec cycle
                                    SJSSCDSignalChannelMap.ResetReqCreateLink();
                                }
              
                             }
                          
                          //Unused Link Deletion
                          
                          //Link removal removed temporarily --> need fix!
                          
                          if(SJSSCDSignalChannelMap.GetCheckLinkToRemove()){
                              
                              Interconnection ic = IM.getInterconnection();
                          
                            JSONObject jsPortPair = TCPIPLinkRegistry.GetSSAndPortPair();

                            try{

                                    Enumeration keysPP = jsPortPair.keys();

                                    while(keysPP.hasMoreElements()){
                                        String portNum = keysPP.nextElement().toString();

                                        String SS = jsPortPair.getString(portNum);

                                        if(!SS.equals(SJSSCDSignalChannelMap.getLocalSSName())){

                                            if(!SJSSCDSignalChannelMap.IsAnyChanUseLinkToSS(SS)){

                                                Vector ListICSS = ic.getRemoteDestinationInterfaces(SS);

                                                for (int e=0;e<ListICSS.size();e++){
                                                    GenericInterface gct = (GenericInterface) ListICSS.get(e);
                                                    gct.TerminateInterface();

                                                }

                                                //System.out.println("SJProgram: Removing interface of SS: " +SS);
                                                
                                                ic.removeInterfaces(SS);

                                            }

                                        }

                                    }

                            } catch (JSONException jex){
                                jex.printStackTrace();
                            }

                            IM.setInterconnection(ic);
                            SJSSCDSignalChannelMap.ResetCheckLinkToRemove();
                          }
                          
                          
                          
                          // End
                          
                          
                          updateFirstScheduler(sc);
                          SchedulersBuffer.SaveSchedulers(scs);
                          //suspend();
                          
                          IMBuffer.SaveInterfaceManagerConfig(IM);
                          
                          setInterfaceManager(IM);
                          
                          // init();
                          
                          initIM();
                          
                          //readvertise updated Service description because a reconfiguration is present 
                          
                          SOABuffer.SetAdvTransmissionRequest(true);
                          
                          //long endTime = System.currentTimeMillis();                          j
                           
                           //System.out.println("SystemJProgram.java: Finished with reconfiguration");
                           //long timeRes = endTime-startTime;
                           
                           //System.out.println("Reconfig Time taken: " +timeRes+ " milliseconds" );
                           
                           // System.out.println("Time taken for gc: " +getGarbageCollectionTime()+ " milliseconds" );
                           
                           shouldShutdown++;
                           
                           if(shouldShutdown==3){
                               System.out.println("All done!" );
                           
                           }
                           
                      }
                      
                      
                                
                                im = IMBuffer.getInterfaceManagerConfig();
                                
                                //update im to execute after uncoupled channels have been coupled once the partner is present and in active state
                                
                                im.run();
                          //}
                          	
                      }
                      
                      /*
                      if(CDLCBuffer.GetRecoverIMSCBufferStatus()){
                
                        InterfaceManager imMig = IMBuffer.getInterfaceManagerConfig();
                        Scheduler scMig = (Scheduler) getScheduler();
                        
                        Vector cdObjs = CDLCBuffer.GetMigrationIMAllCDInsBuffer();
                        
                        for(int t=0;t<cdObjs.size();t++){
                            ClockDomain cdObj = (ClockDomain)cdObjs.get(t);
                            
                            imMig.addCDLocation(SJSSCDSignalChannelMap.getLocalSSName(), cdObj.getName());
                            
                            if(cdObj.getState().equalsIgnoreCase("Active")){
                                scMig.addClockDomain(cdObj);
                            } else if (cdObj.getState().equalsIgnoreCase("Sleep")){
                                CDObjectsBuffer.AddCDInstancesToBuffer(cdObj.getName(), cdObj);
                            }
                            
                        }
                        
                        IMBuffer.SaveInterfaceManagerConfig(imMig);
                        updateFirstScheduler(scMig);
                        CDLCBuffer.SetRecoverIMSCBufferStatus(false);
                        
                      }
                      */
                      
                      
                      
		}
	}
        
        private long getGarbageCollectionTime() {
            long collectionTime = 0;
            for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                collectionTime += garbageCollectorMXBean.getCollectionTime();
            }
            return collectionTime;
        }
        
        /*
        private void PerformReconfiguration(){
            
            ClockDomainLifeCycleManager cdlcm = new ClockDomainLifeCycleManager();
            ClockDomainLifeCycleSignalImpl cdlcmsi = new ClockDomainLifeCycleSignalImpl();
                ClockDomainLifeCycleReconfigChanImpl2 cdlcrci = new ClockDomainLifeCycleReconfigChanImpl2();
            
            if(!CDLCBuffer.IsReconfigInChanConfigIMBufferEmpty()){
                                    Vector recInvChan = CDLCBuffer.GetReconfigInChanConfigIMBuffer();
                                    
                                    for(int j=0;j<recInvChan.size();j++){
                                        
                                        Hashtable hash = (Hashtable)recInvChan.get(j);
                                        
                                        InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                        
                                        Scheduler scMod = getScheduler();
                                        
                                        String inchanName = hash.get("InchanName").toString();
                                        String inchanCDName = hash.get("InchanCDName").toString();
                                        String partnerCDName = hash.get("PartnerCDName").toString();
                                        String partnerChanCDName = hash.get("PartnerChanCDName").toString();
                                        String SSName = hash.get("SSName").toString();
                                        
                                        Vector vec = cdlcmsi.ReconfigInvServInChannel(inchanName, inchanCDName, partnerCDName, partnerChanCDName, SSName, scMod, imMod);
                                        
                                        InterfaceManager newIMMod = (InterfaceManager)vec.get(0);
                                        Scheduler newSCMod = (Scheduler)vec.get(1);
                                        
                                        updateFirstScheduler(newSCMod);
                                        IMBuffer.SaveInterfaceManagerConfig(newIMMod);
                                         
                                        
                                    }
                                    
                                }
                                
                                if(!CDLCBuffer.IsCDSSLocTempBufferEmpty()){
                                    Hashtable CDSSLoc = CDLCBuffer.GetAllCDLocTempBuffer();
                                    
                                    InterfaceManager IMMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Enumeration keysCDSSLoc = CDSSLoc.keys();
                                    
                                    while(keysCDSSLoc.hasMoreElements()){
                                        
                                        String CDNameToAdd = keysCDSSLoc.nextElement().toString();
                                        
                                        String SSNameLocOfCD = (String)CDSSLoc.get(CDNameToAdd);
                                        
                                        IMMod.addCDLocation(SSNameLocOfCD, CDNameToAdd);
                                        
                                    }
                                    CDLCBuffer.ClearCDLocTempBuffer();
                                    
                                    IMBuffer.SaveInterfaceManagerConfig(IMMod);
                                    
                                }
                                
                                //service invocation via channel
                                
                                if(!CDLCBuffer.IsInvokeServ2ChanReconfigBufferEmpty()){
                                    
                                    Scheduler scMod = getScheduler();
                                    
                                    InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Hashtable vec = CDLCBuffer.GetReconfigInvokeServ2ChanBuffer();
                                    
                                    try {
                                        
                                        Enumeration keysChanInvRec = vec.keys();
                                        String locSSName = SJSSCDSignalChannelMap.getLocalSSName();
                                        
                                        JSONObject LocCDConfMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping().getJSONObject(locSSName);
                                        
                                        //for(int j=0;j<vec.size();j++){
                                        
                                        while (keysChanInvRec.hasMoreElements()){
                                        
                                            String keyChanInv = keysChanInvRec.nextElement().toString();
                                            
                                            Hashtable hashReconfigReq = (Hashtable) vec.get(keyChanInv);

                                            //boolean IsLocal = Boolean.parseBoolean(hashReconfigReq.get("IsLocal").toString());

                                            String ChanName = hashReconfigReq.get("ChanName").toString();
                                            String ChanCDName = hashReconfigReq.get("CDName").toString();
                                            
                                            String PartnerChanName = hashReconfigReq.get("PartnerChanName").toString();
                                            String PartnerCDName = hashReconfigReq.get("PartnerCDName").toString();
                                            
                                            boolean IsLocal = false;
                                            
                                            if(LocCDConfMap.has(PartnerCDName)){
                                                IsLocal = true;
                                            }

                                            if(IsLocal){

                                                Vector vecIMSC = cdlcmsi.ReconfigInvServLocalChannel(ChanCDName, ChanName, PartnerCDName, PartnerChanName, scMod, imMod);

                                                InterfaceManager newIMMod = (InterfaceManager) vecIMSC.get(0);
                                                
                                                Scheduler newSCMod = (Scheduler)vecIMSC.get(1);
                                                
                                                updateFirstScheduler(newSCMod);
                                                
                                                IMBuffer.SaveInterfaceManagerConfig(newIMMod);
                                                
                                            } else {

                                                //String DestSS = hashReconfigReq.get("DestSS").toString();
                                                //String DestAddr = hashReconfigReq.get("DestAddr").toString();
                                                
                                                Vector vecIMSC = cdlcmsi.ReconfigInvServChannel(ChanCDName, ChanName, PartnerCDName, PartnerChanName, scMod, imMod);
                                                InterfaceManager newIMMod = (InterfaceManager) vecIMSC.get(0);
                                                
                                                Scheduler newSCMod = (Scheduler)vecIMSC.get(1);
                                                
                                                updateFirstScheduler(newSCMod);
                                                
                                                IMBuffer.SaveInterfaceManagerConfig(newIMMod);
                                                    

                                    //request first

                                    
                                    
                                    //if local, then set to 127.0.0.1
                                    

                                    //LinkCreationSenderHSImpl lcshs = new LinkCreationSenderHSImpl();

                                    //InterfaceManager imA = lcshs.ExecuteLinkCreationHS(SSName, destAddr, IM);
                                    //setInterfaceManager(imA);
                                    // need to
                                     
                                

                                            }

                                        }
                                        
                                        CDLCBuffer.ClearInvokeServ2ChanReconfigBuffer();
                                    
                                    } catch (Exception ex) {
                                       ex.printStackTrace();
                                    }
                                    
                                    
                                    
                                }
                                
                                if(!CDLCBuffer.IsInvokeServChanReconfigBufferEmpty()){
                                    
                                    Vector vec = CDLCBuffer.GetReconfigInvokeServChanBuffer();
                                    
                                    InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Scheduler scMod = getScheduler();
                                    
                                    JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                                    String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
                                    for(int r=0;r<vec.size();r++){
                                        
                                        Hashtable hash = (Hashtable)vec.get(r);
                                        
                                        String CDName = hash.get("CDName").toString();
                                        String ChanName = hash.get("ChanName").toString();
                                        String PartnerCDName = hash.get("PartnerCDName").toString();
                                        String PartnerChanName = hash.get("PartnerChanName").toString();
                                        String ChanDir = hash.get("ChanDir").toString();
                                        
                                        try {
                                            JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                            
                                            Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);
                                        
                                            imMod = (InterfaceManager) IMSCHED.get(0);
                                            //scMod = (Scheduler) IMSCHED.get(1);
                                            
                                            //updateFirstScheduler(scMod);
                                            IMBuffer.SaveInterfaceManagerConfig(imMod);
                                            
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        
                                    }
                                    
                                    CDLCBuffer.ClearInvokeServChanReconfigBuffer();                                    
                                }
                                
                                if(!CDLCBuffer.IsOldInvokeServPartnerChanReconfigBufferEmpty()){
                                    
                                    Vector vec = CDLCBuffer.GetOldInvokeServPartnerReconfigChanBuffer();
                                    
                                    InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Scheduler scMod = getScheduler();
                                    
                                    JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                                    String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
                                    for(int r=0;r<vec.size();r++){
                                        
                                        Hashtable hash = (Hashtable)vec.get(r);
                                        
                                        String PartnerCDName = hash.get("PartnerCDName").toString();
                                        String PartnerChanName = hash.get("PartnerChanName").toString();
                                        String ChanDir = hash.get("PartnerChanDir").toString();
                                        
                                        try {
                                            JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                            
                                            Vector IMSCHED = cdlcmsi.ReconfigPartnerChannel(jsLocalCDs, keyCurrSS, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);
                                        
                                            imMod = (InterfaceManager) IMSCHED.get(0);
                                            //scMod = (Scheduler) IMSCHED.get(1);
                                            
                                            //updateFirstScheduler(scMod);
                                            IMBuffer.SaveInterfaceManagerConfig(imMod);
                                            
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        
                                    }
                                    
                                    CDLCBuffer.ClearOldInvokeServPartnerChanReconfigBuffer();
                                    
                                }
                                
                                if(!CDLCBuffer.IsChanReconfigBufferEmpty()){
                                    
                                    Vector vec = CDLCBuffer.GetReconfigChanBuffer();
                                    
                                    InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Scheduler scMod = getScheduler();
                                    
                                    JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                                    String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
                                    for(int r=0;r<vec.size();r++){
                                        
                                        Hashtable hash = (Hashtable)vec.get(r);
                                        
                                        String CDName = hash.get("CDName").toString();
                                        String ChanName = hash.get("ChanName").toString();
                                        String PartnerCDName = hash.get("PartnerCDName").toString();
                                        String PartnerChanName = hash.get("PartnerChanName").toString();
                                        String ChanDir = hash.get("ChanDir").toString();
                                        
                                        if(hash.containsKey("destSS")){
                                            
                                            String destSS = hash.get("destSS").toString();
                                            String destSSAddr = hash.get("destSSAddr").toString();
                                            
                                            
                                            
                                            //check if link is here, create if neeeded
                                                                                    
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

                                                                                            if(jsLocalPortPair.getString(PortNumStr).equals(destSS)){

                                                                                                RemotePortNum = PortNumStr;
                                                                                                remotePortExist = true;

                                                                                            }

                                                                                        }

                                                                                    } catch (JSONException jex){
                                                                                        jex.printStackTrace();
                                                                                    }

                                                                                    if(!(LocalPortExist && remotePortExist)){
                                                                                        //need to negotiate both link establishment and which physical interface

                                                                                        String SSName = destSS;

                                                                                                    //InterfaceManager imA = getInterfaceManager();
                                                                                        //System.out.println("SJProgram, Trying to contact SS with name: " +SSName);

                                                                                                Interconnection ic = imMod.getInterconnection();

                                                                                                Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                                                                                if(availRemoteLink.size()==0){

                                                                                                    //String destAddr = SJServiceRegistry.getAddrOfSS(SSName);

                                                                                                    LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();

                                                                                                    int q;

                                                                                                    for(q=0;q<5;q++){

                                                                                                        String linkCreationResp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destSSAddr);

                                                                                                            if(linkCreationResp.equals("OK")){

                                                                                                                if(LocalPortExist){

                                                                                                                    imMod = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destSSAddr, imMod, LocalPortNum);

                                                                                                                    break;

                                                                                                                } else {
                                                                                                                     imMod = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destSSAddr, imMod);

                                                                                                                     break;
                                                                                                                }

                                                                                                            } 

                                                                                                    }




                                                                                                    // update SS to Contact

                                                                                                    //TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                                                                    imMod.setInterconnection(ic);
                                                                                                }


                                                                                    }
                                                                                    
                                                                                    imMod.addCDLocation(destSS, PartnerCDName);
                                                                                    
                                                                                    
                                                                                    
                                                                                    
                                            try {
                                               JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                               
                                               Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);
                                               
                                               imMod = (InterfaceManager) IMSCHED.get(0);
                                                
                                               scMod = (Scheduler) IMSCHED.get(1);
                                               
                                               updateFirstScheduler(scMod);
                                               IMBuffer.SaveInterfaceManagerConfig(imMod);

                                               
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                            
                                        } else {
                                            
                                            try {
                                                JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);

                                                Vector IMSCHED = cdlcrci.ReconfigChannel(jsLocalCDs, keyCurrSS, CDName, ChanName, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);

                                                imMod = (InterfaceManager) IMSCHED.get(0);
                                                
                                                scMod = (Scheduler) IMSCHED.get(1);

                                                updateFirstScheduler(scMod);
                                                IMBuffer.SaveInterfaceManagerConfig(imMod);

                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                            
                                        }
                                        
                                    }
                                    
                                    CDLCBuffer.ClearChanReconfigBuffer();
                                    
                                }
                                
                                if(!CDLCBuffer.IsOldPartnerChanReconfigBufferEmpty()){
                                    
                                    Vector vec = CDLCBuffer.GetOldPartnerReconfigChanBuffer();
                                    
                                    InterfaceManager imMod = IMBuffer.getInterfaceManagerConfig();
                                    
                                    Scheduler scMod = getScheduler();
                                    
                                    JSONObject jsCurrMap = SJSSCDSignalChannelMap.getCurrentSignalChannelMapping();
            
                                    String keyCurrSS = SJSSCDSignalChannelMap.getLocalSSName();
            
                                    for(int r=0;r<vec.size();r++){
                                        
                                        Hashtable hash = (Hashtable)vec.get(r);
                                        
                                        String PartnerCDName = hash.get("PartnerCDName").toString();
                                        String PartnerChanName = hash.get("PartnerChanName").toString();
                                        String ChanDir = hash.get("PartnerChanDir").toString();
                                        
                                        try {
                                            JSONObject jsLocalCDs = jsCurrMap.getJSONObject(keyCurrSS);
                                            
                                            Vector IMSCHED = cdlcmsi.ReconfigPartnerChannel(jsLocalCDs, keyCurrSS, ChanDir, PartnerChanName, PartnerCDName, imMod, scMod);
                                        
                                            imMod = (InterfaceManager) IMSCHED.get(0);
                                            //scMod = (Scheduler) IMSCHED.get(1);
                                            
                                            //updateFirstScheduler(scMod);
                                            IMBuffer.SaveInterfaceManagerConfig(imMod);
                                            
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        
                                    }
                                    
                                    CDLCBuffer.ClearOldPartnerChanReconfigBuffer();
                                    
                                    
                                }
                                
                                if(!CDLCBuffer.IsDevelCreateCDEmpty()){
                          
                          Vector vec = CDLCBuffer.getDevelCreateCD();
                          
                          for(int y=0;y<vec.size();y++){
                              Hashtable hash = (Hashtable)vec.get(y);
                              
                              String CDName = hash.get("CDName").toString();
                              JSONObject jsCDMap = (JSONObject)hash.get("CDMap");
                              
                              JSONObject jsCDServDesc = (JSONObject)hash.get("CDServDesc");
                              
                                CDLCBuffer.TransferRequestCreateCDToBuffer(CDName);
                                CDLCBuffer.AddTempSigChanMapCD(jsCDMap);
                                CDLCBuffer.putUpdateServiceDescription(jsCDServDesc);
                                
                          }
                          CDLCBuffer.ClearDevelCreateCD();
                          
                      }
                      
                      if(!CDLCBuffer.IsMigratingCDNameBufferEmpty()){
                          
                          Scheduler sc = getScheduler();
                          
                          Vector cdnames = CDLCBuffer.GetMigratingCDNameFromBuffer();
                          
                          for(int u=0;u<cdnames.size();u++){
                              String cdname = (String)cdnames.get(u);
                              
                              if(sc.SchedulerHasCD(cdname)){
                                  
                                  ClockDomain cdins = sc.getClockDomain(cdname);
                                  
                                  CDLCBuffer.AddMigratingCDInst(cdins);
                                  //sc.removeClockDomain(cdins);
                                  sc.removeClockDomain(cdname);
                              } else if (CDObjectsBuffer.CDObjBufferHas(cdname)){
                                  CDLCBuffer.AddMigratingCDInst(CDObjectsBuffer.GetCDInstancesFromBuffer(cdname));
                                  CDObjectsBuffer.RemoveCDInstancesFromBuffer(cdname);
                              }
                              
                          }
                          
                      }
                      
                      if(SJSSCDSignalChannelMap.GetCheckLinkToRemove()|| SJSSCDSignalChannelMap.GetReqCreateLink() || !CDLCBuffer.IsRequestCreateCDEmpty() || !CDLCBuffer.IsRequestKillCDEmpty() || !CDLCBuffer.IsRequestHibernateCDEmpty() || !CDLCBuffer.IsRequestWakeUpCDEmpty() || !CDLCBuffer.IsRequestMigrateEmpty() || CDLCBuffer.GetStrongMigrationDoneFlag()|| CDLCBuffer.GetWeakMigrationDoneFlag() || CDLCBuffer.GetMigGoAheadModPartner()){
                          
                          
                          //System.gc();
                          
                          System.out.println("Time taken for gc: " +getGarbageCollectionTime()+ " milliseconds" );
                          
                          Scheduler sc = (Scheduler)getScheduler();
                      //InterfaceManager IM = getInterfaceManager();
                          InterfaceManager IM = IMBuffer.getInterfaceManagerConfig();
                          
                          System.out.println("SystemJProgram.java: Start reconfiguration");
                          
                          //long startTime = System.currentTimeMillis();
                          
                          Vector vec = cdlcm.run(sc, IM);
                          
                          IM = (InterfaceManager)vec.get(0);
                          sc = (Scheduler)vec.get(1);
                          
                          if(SJSSCDSignalChannelMap.GetReqCreateLink()){
                          
                          //if request to create link is present
                                Vector SSsToContact =  TCPIPLinkRegistry.GetAllSSToContact();
                                
                                boolean contactSSAgain = false;

                                // find local SS port
                                
                                JSONObject jsLocalPortPair = TCPIPLinkRegistry.GetSSAndPortPair();
                                
                                String LocalPortNum="";
                                boolean LocalPortExist = false;
                                
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
                                
                                //end find local SS port
                                
                                for(int k=0;k<SSsToContact.size();k++){
                                    
                                    String SSName = (String)SSsToContact.get(k);

                                                //InterfaceManager imA = getInterfaceManager();
                                    //System.out.println("SJProgram, Trying to contact SS with name: " +SSName);
                                        
                                            Interconnection ic = IM.getInterconnection();
                          
                                            Vector availRemoteLink = ic.getRemoteDestinationInterfaces(SSName);

                                            if(availRemoteLink.size()==0){

                                                //String destAddr = SJServiceRegistry.getAddrOfSS(SSName);
                          
                                                String destAddr = RegAllSSAddr.getSSAddrOfSSName(SSName);
                                                LinkCreationSenderHSImpl lcsh = new LinkCreationSenderHSImpl();
                                                
                                                int r;
                                                
                                                for(r=0;r<5;r++){
                                                    
                                                    String resp = lcsh.SendLinkCreationReq(SJSSCDSignalChannelMap.getLocalSSName(), SSName, destAddr);
                                                    
                                                        if(resp.equals("OK")){
                                                        
                                                            if(LocalPortExist){
                                                                
                                                                IM = lcsh.ExecuteLinkCreationHSWithLocalICPortExist(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM, LocalPortNum);
                                                                SSsToContact.remove(k);
                                                                break;
                                                                
                                                            } else {
                                                                 IM = lcsh.ExecuteLinkCreationHS(SJSSCDSignalChannelMap.getLocalSSName(), destAddr, IM);
                                                                 SSsToContact.remove(k);
                                                                 break;
                                                            }
                                                            
                                                        } 
                                                        
                                                }
                                                
                                                if(r>=5){
                                                    contactSSAgain=true;
                                                }
                                                
                                                
                                                // update SS to Contact
                                                
                                                TCPIPLinkRegistry.UpdateAllSSToContact(SSsToContact);
                                                IM.setInterconnection(ic);
                                            }

                                    //request first

                                    
                                    
                                    //if local, then set to 127.0.0.1
                                    

                                    //LinkCreationSenderHSImpl lcshs = new LinkCreationSenderHSImpl();

                                    //InterfaceManager imA = lcshs.ExecuteLinkCreationHS(SSName, destAddr, IM);
                                    //setInterfaceManager(imA);
                                    // need to
                                     
                                }

                                
                                if(!contactSSAgain){ //next end of exec cycle
                                    SJSSCDSignalChannelMap.ResetReqCreateLink();
                                }
              
                             }
                          
                          //Unused Link Deletion
                          
                          //Link removal removed temporarily --> need fix!
                          
                          if(SJSSCDSignalChannelMap.GetCheckLinkToRemove()){
                              
                              Interconnection ic = IM.getInterconnection();
                          
                            JSONObject jsPortPair = TCPIPLinkRegistry.GetSSAndPortPair();

                            try{

                                    Enumeration keysPP = jsPortPair.keys();

                                    while(keysPP.hasMoreElements()){
                                        String portNum = keysPP.nextElement().toString();

                                        String SS = jsPortPair.getString(portNum);

                                        if(!SS.equals(SJSSCDSignalChannelMap.getLocalSSName())){

                                            if(!SJSSCDSignalChannelMap.IsAnyChanUseLinkToSS(SS)){

                                                Vector ListICSS = ic.getRemoteDestinationInterfaces(SS);

                                                for (int e=0;e<ListICSS.size();e++){
                                                    GenericInterface gct = (GenericInterface) ListICSS.get(e);
                                                    gct.TerminateInterface();

                                                }

                                                //System.out.println("SJProgram: Removing interface of SS: " +SS);
                                                
                                                ic.removeInterfaces(SS);

                                            }

                                        }

                                    }

                            } catch (JSONException jex){
                                jex.printStackTrace();
                            }

                            IM.setInterconnection(ic);
                            SJSSCDSignalChannelMap.ResetCheckLinkToRemove();
                          }
                          
                          
                          updateFirstScheduler(sc);
                          SchedulersBuffer.SaveSchedulers(scs);
                          //suspend();
                          
                          IMBuffer.SaveInterfaceManagerConfig(IM);
                          
                          setInterfaceManager(IM);
                          
                          // init();
                          
                          initIM();
                          SOABuffer.SetAdvTransmissionRequest(true);
            
        }
                      
                      
                          
                          //readvertise updated Service description because a reconfiguration is present 
                          
                          
                          
                          //Vector vec = new Vector();
                          
                          //vec.addElement(im1);
                          //vec.addElement(sc1);
                          
                          //return vec;
        
        }
        */
}
